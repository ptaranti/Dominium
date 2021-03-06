/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            July 19th 2003
 * Filename           $RCSfile: DIGReasoner.java,v $
 * Revision           $Revision: 1.11 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2007/01/02 11:49:27 $
 *               by   $Author: andy_seaborne $
 *
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 * ****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.reasoner.dig;



// Imports
///////////////
import java.io.*;

import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.reasoner.rulesys.Util;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;


/**
 * <p>
 * This reasoner is the generator of inf-graphs that can use an external DIG inference engine
 * to perform DL reasoning tasks.
 * </p>
 *
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com">email</a>)
 * @version Release @release@ ($Id: DIGReasoner.java,v 1.11 2007/01/02 11:49:27 andy_seaborne Exp $)
 */
public class DIGReasoner
    implements Reasoner
{

    // Constants
    //////////////////////////////////

    // Static variables
    //////////////////////////////////

    /** The graph capabilities of the infgraphs generated by this reasoner */
    protected Capabilities capabilities;

    // Instance variables
    //////////////////////////////////

    /** A graph that contains ontology definition data (tbox) */
    protected Graph m_tbox;

    /** Reference to the factory that created this reasoner */
    protected ReasonerFactory m_factory;

    /** The original configuration properties, if any */
    protected Resource m_configuration;

    /** The URL to use to connect to the external reasoner */
    protected String m_extReasonerURL = DIGConnection.DEFAULT_REASONER_URL;

    /** The profile of the ontology language we're expecting */
    protected OntModelSpec m_ontLang = getModelSpec( ProfileRegistry.OWL_LANG );

    /** The axioms that provide additional triples based on the language we're processing */
    protected Model m_axioms = null;


    // Constructors
    //////////////////////////////////

    /**
     * <p>Construct a DIG reasoner, that can generate inference graphs binding
     * an external DIG inference engine (e.g. Racer) to a given source graph.<p>
     * @param tbox Optional schema to bind to this reasoner instance.  Unlike other Jena
     * reasoners, pre-binding a tbox to a DIG reasoner does not allow any
     * efficiencies to be exploited.
     * @param factory The reasoner factory that created this reasoner
     * @param configuration Optional resource to which is attached configuration
     * parameters for this reasoner
     */
    public DIGReasoner( Graph tbox, ReasonerFactory factory, Resource configuration ) {
        m_tbox = tbox;
        m_factory = factory;
        m_configuration = configuration;

        configure( configuration );
    }


    // External signature methods
    //////////////////////////////////

    /**
     * <p>Bind a schema, or tbox, to this DIG reasoner.  This does not have any efficiency
     * value in DIG reasoners, since we must re-load the entire tbox into each new instance
     * of a DIG inference graph.<p>
     * @param tbox The graph containing the ontology (tbox) data
     * @return A new DIG reasoner containing the tbox data
     * @see com.hp.hpl.jena.reasoner.Reasoner#bindSchema(com.hp.hpl.jena.graph.Graph)
     */
    public Reasoner bindSchema( Graph tbox ) {
        return new DIGReasoner( tbox, m_factory, m_configuration );
    }


    /**
     * <p>Bind a schema, or tbox, to this DIG reasoner.  This does not have any efficiency
     * value in DIG reasoners, since we must re-load the entire tbox into each new instance
     * of a DIG inference graph.<p>
     * @param tbox A model wrapping the graph containing the ontology (tbox) data
     * @return A new DIG reasoner containing the tbox data
     * @see com.hp.hpl.jena.reasoner.Reasoner#bindSchema(com.hp.hpl.jena.graph.Graph)
     */
    public Reasoner bindSchema(Model tbox) {
        return bindSchema( tbox.getGraph() );
    }


    /**
     * <p>Bind the given data graph to any existing t-box schema that we have, and answer
     * the resulting inference graph.</p>
     * @param data A graph containing the source data
     * @return A new inference graph that will apply the DIG reasoner to the combination
     * of the tbox and data graphs.
     */
    public InfGraph bind( Graph data ) {
        return new DIGInfGraph( data, this );
    }


    /**
     * Not available.
     * @exception UnsupportedOperationException
     */
    public void setDerivationLogging( boolean logOn ) {
        throw new UnsupportedOperationException( "DIG reasoner does not support derivation logging" );
    }


    /**
     * <p>Answer the capabilities of this reasoner.</p>
     * @return An RDF model denoting the capabilties of the reasoner
     */
    public Model getReasonerCapabilities() {
        return (m_factory == null) ? null : m_factory.getCapabilities();
    }


    /**
     * <p>Add this reasoner's description to the given configuration model.</p>
     * @param configSpec A configuration model to add this reasoner's configuration to
     * @param base The base URI in the given model to which we will attach the configuration
     * of this reasoner.
     */
    public void addDescription( Model configSpec, Resource base ) {
        if (m_configuration != null) {
            StmtIterator i = m_configuration.listProperties();
            while (i.hasNext()) {
                Statement st = i.nextStatement();
                configSpec.add(base, st.getPredicate(), st.getObject());
            }
        }
    }

    /**
     * Determine whether the given property is recognized and treated specially
     * by this reasoner. This is a convenience packaging of a special case of getCapabilities.
     * @param property the property which we want to ask the reasoner about, given as a Node since
     * this is part of the SPI rather than API
     * @return true if the given property is handled specially by the reasoner.
     */
    public boolean supportsProperty(Property property) {
        if (m_factory == null) return false;
        Model caps = m_factory.getCapabilities();
        Resource root = caps.getResource(m_factory.getURI());
        return caps.contains(root, ReasonerVocabulary.supportsP, property);
    }


    /**
     * Set a configuration parameter for the reasoner. The supported parameters
     * are:
     * <ul>
     * <li>PROPderivationLogging - set to true to enable recording all rule derivations</li>
     * <li>PROPtraceOn - set to true to enable verbose trace information to be sent to the logger INFO channel</li>
     * </ul>
     *
     * @param parameter the property identifying the parameter to be changed
     * @param value the new value for the parameter, typically this is a wrapped
     * java object like Boolean or Integer.
     * @throws IllegalParameterException if the parameter is unknown
     */
    public void setParameter(Property parameter, Object value) {
        if (!doSetParameter(parameter, value)) {
            throw new IllegalParameterException( "DIGReasoner does not recognize configuration parameter " + parameter );
        }
        else {
            // Record the configuration change
            if (m_configuration == null) {
                Model configModel = ModelFactory.createDefaultModel();
                m_configuration = configModel.createResource();
            }

            Util.updateParameter( m_configuration, parameter, value );
        }
    }


    /**
     * <p>Configure the reasoner using the properties attached to the given config
     * resource.</p>
     * @param configuration A configuration resource.
     */
    public void configure( Resource configuration ) {
        if (configuration != null) {
            for (StmtIterator i = configuration.listProperties(); i.hasNext(); ) {
                Statement s = i.nextStatement();
                if (!doSetParameter( s.getPredicate(), s.getObject() )) {
                    // we used to throw an exception here, but such is no longer felicitous
                    // TODO consider namespace-based checking
                }
            }
        }
    }


    /**
     * <p>Answer the URL to use when connecting to the external reasoner.</p>
     * @return The connection URL for the external reasoner as a string
     */
    public String getReasonerURL() {
        return m_extReasonerURL;
    }


    /**
     * <p>Answer the model spec that corresponds to the ontology model type we'll use to
     * access the terms of the ontology according to language.</p>
     * @return The appropriate ont model spec
     */
    public OntModelSpec getOntLangModelSpec() {
        return m_ontLang;
    }


    /**
     * <p>Answer the schema (tbox) graph for this reasoner, or null if no schema is defined.</p>
     * @return The schema graph, or null
     */
    public Graph getSchema() {
        return m_tbox;
    }


    /**
     * <p>Answer the model that contains the given axioms for this reasoner, or null if
     * not defined.</p>
     * @return The axioms model
     */
    public Model getAxioms() {
        return m_axioms;
    }

    /**
     * Return the Jena Graph Capabilties that the inference graphs generated
     * by this reasoner are expected to conform to.
     */
    public Capabilities getGraphCapabilities() {
        if (capabilities == null) {
            capabilities = new BaseInfGraph.InfCapabilities();
        }
        return capabilities;
    }

    // Internal implementation methods
    //////////////////////////////////

    /**
     * <p>Set a configuration parameter for the reasoner. The supported parameters
     * are:</p>
     * <ul>
     * <li>{@link ReasonerVocabulary#EXT_REASONER_URL} the URL to use to connect to the external reasoners</li>
     * <li>{@link ReasonerVocabulary#EXT_REASONER_ONT_LANG} the URI of the ontology language (OWL, DAML, etc) to process</li>
     * <li>{@link ReasonerVocabulary#EXT_REASONER_AXIOMS} the URL of the ontology axioms model</li>
     * </ul>
     *
     * @param parameter the property identifying the parameter to be changed
     * @param value the new value for the parameter, typically this is a wrapped
     * java object like Boolean or Integer.
     * @return false if the parameter was not known
     */
    protected boolean doSetParameter(Property parameter, Object value) {
        if (parameter.equals(ReasonerVocabulary.EXT_REASONER_URL)) {
            m_extReasonerURL = (value instanceof Resource) ? ((Resource) value).getURI() : value.toString();
            return true;
        }
        else if (parameter.equals(ReasonerVocabulary.EXT_REASONER_ONT_LANG)) {
            String lang = (value instanceof Resource) ? ((Resource) value).getURI() : value.toString();
            m_ontLang = getModelSpec( lang );
            return true;
        }
        else if (parameter.equals(ReasonerVocabulary.EXT_REASONER_AXIOMS)) {
            String axURL = (value instanceof Resource) ? ((Resource) value).getURI() : value.toString();
            m_axioms = ModelFactory.createDefaultModel();

            // if a file URL, try to load it as a stream (which means we can extract from jars etc)
            if (axURL.startsWith( "file:")) {
                String fileName = axURL.substring( 5 );
                InputStream in = null;
                try {
                    in = FileUtils.openResourceFileAsStream( fileName );
                    m_axioms.read( in, axURL );
                }
                catch (FileNotFoundException e) {
                    LogFactory.getLog( getClass() ).error( "Could not open DIG axioms " + axURL );
                }
                finally {
                    if (in != null) {
                        try {in.close();} catch (IOException ignore) {}
                    }
                }
            }
            else {
                m_axioms.read( axURL );
            }

            return true;
        }
        else {
            return false;
        }
    }


    /**
     * <p>Answer the ont model spec for the given ontology language</p>
     * @param lang The ontology language as a URI
     * @return The correspondig ont model spec to use (should be no reasoner attached)
     */
    protected OntModelSpec getModelSpec( String lang ) {
        if (lang.equals( ProfileRegistry.OWL_LANG ) ||
            lang.equals( ProfileRegistry.OWL_DL_LANG ) ||
            lang.equals( ProfileRegistry.OWL_LITE_LANG )) {
           return OntModelSpec.OWL_MEM;
        }
        else if (lang.equals( ProfileRegistry.DAML_LANG )) {
            return OntModelSpec.DAML_MEM;
        }
        else if (lang.equals( ProfileRegistry.RDFS_LANG )) {
            return OntModelSpec.RDFS_MEM;
        }
        else {
            throw new IllegalParameterException( "DIG reasoner did not recognise ontology language " + lang );
        }
    }



    //==============================================================================
    // Inner class definitions
    //==============================================================================

}


/*
 *  (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
