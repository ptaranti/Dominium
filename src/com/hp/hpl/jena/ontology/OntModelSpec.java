/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            13-May-2003
 * Filename           $RCSfile: OntModelSpec.java,v $
 * Revision           $Revision: 1.51 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2007/03/08 15:24:16 $
 *               by   $Author: chris-dollin $
 *
 * (c) Copyright 2002, 2003, 204, Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.ontology;



// Imports
///////////////

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.*;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.reasoner.rulesys.impl.WrappedReasonerFactory;
import com.hp.hpl.jena.assembler.*;
import com.hp.hpl.jena.ontology.impl.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.reasoner.transitiveReasoner.TransitiveReasonerFactory;
import com.hp.hpl.jena.shared.*;


/**
 * <p>
 * Encapsulates a description of the components of an ontology model, including the
 * storage scheme, reasoner and language profile.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: OntModelSpec.java,v 1.51 2007/03/08 15:24:16 chris-dollin Exp $
 */
public class OntModelSpec extends OntModelSpecObsolete implements ModelSpec {
    // Constants
    //////////////////////////////////
    // Static variables
    //////////////////////////////////

    /** A specification for OWL models that are stored in memory and do no additional entailment reasoning */
    public static final OntModelSpec OWL_MEM = new OntModelSpec( ModelFactory.createMemModelMaker(), null, null, ProfileRegistry.OWL_LANG );

    /** A specification for OWL models that are stored in memory and use the RDFS inferencer for additional entailments */
    public static final OntModelSpec OWL_MEM_RDFS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, RDFSRuleReasonerFactory.theInstance(), ProfileRegistry.OWL_LANG );

    /** A specification for OWL models that are stored in memory and use the transitive inferencer for additional entailments */
    public static final OntModelSpec OWL_MEM_TRANS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, TransitiveReasonerFactory.theInstance(), ProfileRegistry.OWL_LANG );

    /** A specification for OWL models that are stored in memory and use the OWL rules inference engine for additional entailments */
    public static final OntModelSpec OWL_MEM_RULE_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, OWLFBRuleReasonerFactory.theInstance(), ProfileRegistry.OWL_LANG );

    /** A specification for OWL models that are stored in memory and use the micro OWL rules inference engine for additional entailments */
    public static final OntModelSpec OWL_MEM_MICRO_RULE_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, OWLMicroReasonerFactory.theInstance(), ProfileRegistry.OWL_LANG );

    /** A specification for OWL models that are stored in memory and use the mini OWL rules inference engine for additional entailments */
    public static final OntModelSpec OWL_MEM_MINI_RULE_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, OWLMiniReasonerFactory.theInstance(), ProfileRegistry.OWL_LANG );

    /** A specification for OWL DL models that are stored in memory and do no additional entailment reasoning */
    public static final OntModelSpec OWL_DL_MEM = new OntModelSpec( ModelFactory.createMemModelMaker(), null, null, ProfileRegistry.OWL_DL_LANG );

    /** A specification for OWL DL models that are stored in memory and use the RDFS inferencer for additional entailments */
    public static final OntModelSpec OWL_DL_MEM_RDFS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, RDFSRuleReasonerFactory.theInstance(), ProfileRegistry.OWL_DL_LANG );

    /** A specification for OWL DL models that are stored in memory and use the transitive inferencer for additional entailments */
    public static final OntModelSpec OWL_DL_MEM_TRANS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, TransitiveReasonerFactory.theInstance(), ProfileRegistry.OWL_DL_LANG );

    /** A specification for OWL DL models that are stored in memory and use the OWL rules inference engine for additional entailments */
    public static final OntModelSpec OWL_DL_MEM_RULE_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, OWLFBRuleReasonerFactory.theInstance(), ProfileRegistry.OWL_DL_LANG );

    /** A specification for OWL Lite models that are stored in memory and do no entailment additional reasoning */
    public static final OntModelSpec OWL_LITE_MEM = new OntModelSpec( ModelFactory.createMemModelMaker(), null, null, ProfileRegistry.OWL_LITE_LANG );

    /** A specification for OWL Lite models that are stored in memory and use the transitive inferencer for additional entailments */
    public static final OntModelSpec OWL_LITE_MEM_TRANS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, TransitiveReasonerFactory.theInstance(), ProfileRegistry.OWL_LITE_LANG );

    /** A specification for OWL Lite models that are stored in memory and use the RDFS inferencer for additional entailments */
    public static final OntModelSpec OWL_LITE_MEM_RDFS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, RDFSRuleReasonerFactory.theInstance(), ProfileRegistry.OWL_LITE_LANG );

    /** A specification for OWL Lite models that are stored in memory and use the OWL rules inference engine for additional entailments */
    public static final OntModelSpec OWL_LITE_MEM_RULES_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, OWLFBRuleReasonerFactory.theInstance(), ProfileRegistry.OWL_LITE_LANG );

    /** A specification for DAML models that are stored in memory and do no additional entailment reasoning */
    public static final OntModelSpec DAML_MEM = new OntModelSpec( ModelFactory.createMemModelMaker(), null, null, ProfileRegistry.DAML_LANG );

    /** A specification for DAML models that are stored in memory and use the transitive reasoner for entailments */
    public static final OntModelSpec DAML_MEM_TRANS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, TransitiveReasonerFactory.theInstance(), ProfileRegistry.DAML_LANG );

    /** A specification for DAML models that are stored in memory and use the RDFS inferencer for additional entailments */
    public static final OntModelSpec DAML_MEM_RDFS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, RDFSRuleReasonerFactory.theInstance(), ProfileRegistry.DAML_LANG );

    /** A specification for DAML models that are stored in memory and use a subset of the DAML semantic model additional entailments */
    public static final OntModelSpec DAML_MEM_RULE_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, DAMLMicroReasonerFactory.theInstance(), ProfileRegistry.DAML_LANG );

    /** A specification for RDFS ontology models that are stored in memory and do no additional entailment reasoning */
    public static final OntModelSpec RDFS_MEM = new OntModelSpec( ModelFactory.createMemModelMaker(), null, null, ProfileRegistry.RDFS_LANG );

    /** A specification for RDFS ontology models that are stored in memory and use the transitive reasoner for entailments */
    public static final OntModelSpec RDFS_MEM_TRANS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, TransitiveReasonerFactory.theInstance(), ProfileRegistry.RDFS_LANG );

    /** A specification for RDFS ontology models that are stored in memory and use the RDFS inferencer for additional entailments */
    public static final OntModelSpec RDFS_MEM_RDFS_INF = new OntModelSpec( ModelFactory.createMemModelMaker(), null, RDFSRuleReasonerFactory.theInstance(), ProfileRegistry.RDFS_LANG );


    // Instance variables
    //////////////////////////////////

    /** The specification document manager */
    protected OntDocumentManager m_docManager = null;

    /** The specification reasoner */
    protected Reasoner m_reasoner = null;

    /** The language URI for the ontology */
    protected String m_languageURI;

    /** The ontology language profile */
    protected Profile m_profile = null;

    /** The reasoner factory for creating the reasoner on demand */
    protected ReasonerFactory m_rFactory = null;

    /** The ModelMaker used for creating imported models */
    protected ModelMaker m_importsMaker;

    /** the name of the base model in the baseModelMaker, if specified */
    protected String m_baseModelName;
    
    /** the ModelGetter which will be used - eventually - for imports */
    protected ModelGetter importModelGetter;

    // Constructors
    //////////////////////////////////

    /**
     * <p>Construct a new ontology model specification with the given specification parameters</p>
     * @param importsMaker The model maker, which will be used to construct stores for statements in the
     * imported ontologies and the base ontology. Use null to get a default (memory) model maker.
     * @param docMgr The document manager, or null for the default document manager.
     * @param rFactory The factory for the reasoner to use to infer additional triples in the model, or null for no reasoner
     * @param languageURI The URI of the ontology language. Required.
     */
    public OntModelSpec( ModelMaker importsMaker, OntDocumentManager docMgr, ReasonerFactory rFactory, String languageURI ) {
        this( ModelFactory.createMemModelMaker(), importsMaker, docMgr, rFactory, languageURI );
    }

    /**
     * Construct a new ontology model specification from the supplied components.
     * @param baseMaker the model-maker to use for the base model
     * @param importsMaker the model-maker to use for imported models
     * @param docMgr the document manager (null for the default manager)
     * @param rFactory the reasoner (null for no reasoner)
     * @param languageURI the ontology language URI (must not be null)
    */
    public OntModelSpec( ModelMaker baseMaker, ModelMaker importsMaker, OntDocumentManager docMgr,
                         ReasonerFactory rFactory, String languageURI ) {
        this( null, baseMaker, importsMaker, docMgr, rFactory, languageURI );
    }


    /**
     * Construct a new ontology model specification from the supplied components.
     * @param baseModelName the name of the model in the baseModelMaker
     * @param baseMaker the model-maker to use for the base model
     * @param importsMaker the model-maker to use for imported models
     * @param docMgr the document manager (null for the default manager)
     * @param rFactory the reasoner (null for no reasoner)
     * @param languageURI the ontology language URI (must not be null)
    */
    public OntModelSpec( String baseModelName, ModelMaker baseMaker,
                         ModelMaker importsMaker, OntDocumentManager docMgr,
                         ReasonerFactory rFactory, String languageURI ) {
        super( baseMaker );
        m_baseModelName = baseModelName;
        m_importsMaker = importsMaker == null ? ModelFactory.createMemModelMaker() : importsMaker;
        setDocumentManager( docMgr );
        setReasonerFactory( rFactory );

        if (languageURI == null) {
            throw new IllegalArgumentException( "Cannot create OntModelSpec with a null ontology language" );
        }
        setLanguage( languageURI );
    }


    /**
     * <p>Create one model spec as a copy of another. This is useful when what is required is similar to
     * an existing spec, but with some changes. <strong>Note:</strong> this is only a shallow copy, so the
     * structured objects (reasoners, document managers, etc) are not themselves copied. Thus, even after
     * calling this copy constructor, making a change to the document manager in the copy specification
     * will also affect the one that the copy was made from. The correct idiom is to replace the object
     * before side-effecting it, e.g:
     * <code><pre>
     *   OntModelSpec newSpec = new OntModelSpec( existingSpec );
     *   newSpec.setDocumentManager( new OntDocumentManager() );
     *   newSpec.getDocumentManager().setMetaDataSearchPath( "..." );
     * </pre></code>
     * @param spec
     */
    public OntModelSpec( OntModelSpec spec ) {
        this( spec.getBaseModelMaker(), spec.getImportModelMaker(), spec.getDocumentManager(),
              spec.getReasonerFactory(), spec.getLanguage() );
    }

    public boolean equals( Object other )
        { return other instanceof OntModelSpec && same( (OntModelSpec) other );}
    
    private boolean same( OntModelSpec other )
        {
        return 
            getLanguage().equals( other.getLanguage() )
            && sameReasonerFactory( other )
            && getDocumentManager().equals( other.getDocumentManager() )
            && getImportModelGetter().equals( other.getImportModelGetter() )
            ;
        }
        
    private boolean sameReasonerFactory( OntModelSpec other )
        {
        ReasonerFactory rf = getReasonerFactory();
        ReasonerFactory orf = other.getReasonerFactory();
        return rf == null ? orf == null : rf.equals( orf );
        }

    /**
     * Answer the model maker used for creating imported models.
     * @return The ModelMaker that is used to get or create a model used
     * to hold imports to an OntModel.
     */
    public ModelMaker getImportModelMaker() {
        return m_importsMaker;
    }

    /**
     * Answer the model maker used for creating base models.
     */
    public ModelMaker getBaseModelMaker() {
        return super.getModelMaker();
    }
    
    public ModelGetter getImportModelGetter() {
        if (importModelGetter == null) importModelGetter = m_importsMaker; //  fabricateModelGetter();
        return importModelGetter;
    }

//    private ModelGetter fabricateModelGetter()
//        {
//        return new ModelGetter()
//            {
//            public Model getModel( String URL )
//                {
//                return m_importsMaker.hasModel( URL ) ? m_importsMaker.openModel( URL ) : null;
//                }
//
//            public Model getModel( String URL, ModelReader loadIfAbsent )
//                {
//                return m_importsMaker.hasModel( URL ) ? m_importsMaker.openModel( URL ) : createAndLoad( URL, loadIfAbsent );
//                }
//
//            private Model createAndLoad( String URL, ModelReader loadIfAbsent )
//                {
//                Model result = m_importsMaker.createModel( URL );
//                return loadIfAbsent.readModel( result, URL );
//                }
//            };
//        }

    public void setImportModelGetter( ModelGetter mg ) {
        importModelGetter = mg;
    }
    
    /**
     * <p>Initialise an OntModelSpec from an RDF description using the JenaModelSpec vocabulary. See
     * <a href="../../../../../doc/howto/modelspec.html">the modelspec howto</a>
     * for the description of the OntModel used. The root of the
     * description is the unique resource with type <code>jms:OntMakerClass</code>.</p>
     *
     * @param description an RDF model using the JenaModelSpec vocabulary
     * @deprecated ModelSpec has been removed; use the <code>assemble</code>
     * methods.
     */
    public OntModelSpec( Model description )  {
        this( findRootByType( description, JenaModelSpec.OntModelSpec ), description );
    }

    /**
     * <p>Initialise an OntModelSpec from an RDF description using the JenaModelSpec vocabulary. See
     * <a href="../../../../../doc/howto/modelspec.html">the modelspec howto</a>
     * for the description of the OntModel used.  The root of the
     *  description is supplied as a parameter (so the description may describe several
     *  different OntModels).</p>
     *
     *  @param description an RDF model using the JenaModelSpec vocabulary
     *  @param root the root of the sub-graph to use for the specification
     *  @deprecated ModelSpec has been removed; use the <code>assemble</code>
     * methods.
     */
    public OntModelSpec( Resource root, Model description )  {
        this( getBaseModelName( description, root ),
              getBaseMaker( description, root ),
              getImportMaker( description, root ),
              getDocumentManager( description, root ),
              getReasonerFactory( description, root ),
              getLanguage( description, root )  );
    }
    
    /**
        Answer the OntModelSpec described using the Jena Assembler vocabulary
        properties of <code>root</code>. If the assembled resource is not
        an OntModelSpec, throw an exception reporting the constructed class.
    */
    public static OntModelSpec assemble( Resource root )
        {
        Object assembled = Assembler.general.open( root );
        if (!(assembled instanceof OntModelSpec)) 
            throw new JenaException( "assemble: expected an OntModelSpec, but got a " + assembled.getClass().getName() );
        return (OntModelSpec) assembled;
        }
    
    /**
         Answer the OntModelSpec described using the Jena Assembler vocabulary
        properties of the single resource in <code>model</code> of type
        JA:OntModelSpec.
    */
    public static OntModelSpec assemble( Model model )
        { return assemble( AssemblerHelp.singleRoot( model, JA.OntModelSpec ) ); }

    /**
     * <p>Answer a default specification for the given language URI. This default
     * will typically use a memory model and have minimal inferencing capabilities.
     * Specifically, OWL and RDFS languages will have RDFS level inferencing
     * capability (chosen to give a reasonable balance between power and efficiency
     * of computation), and DAML language will have the minimal DAML rule reasoner.
     * To get other (more powerful or less powerful) reasoning capabilities, users
     * should create ontology models by passing an explicit <code>OntModelSpec</code>
     * parameter to the
     * {@link ModelFactory#createOntologyModel( OntModelSpec, Model ) model factory}.
     * </p>
     * @param languageURI The ontology language we want a default model spec for
     * @return The default model spec for that language
     * @exception OntologyException if the URI is not a recognised name of an ontology language
     */
    public static OntModelSpec getDefaultSpec( String languageURI ) {
        if (languageURI.equals( ProfileRegistry.OWL_LANG )) {
            return OWL_MEM_RDFS_INF;
        }
        else if (languageURI.equals( ProfileRegistry.OWL_DL_LANG )) {
            return OWL_DL_MEM_RDFS_INF;
        }
        else if (languageURI.equals( ProfileRegistry.OWL_LITE_LANG )) {
            return OWL_LITE_MEM_RDFS_INF;
        }
        else if (languageURI.equals( ProfileRegistry.DAML_LANG )) {
            return DAML_MEM_RULE_INF;
        }
        else if (languageURI.equals( ProfileRegistry.RDFS_LANG )) {
            return RDFS_MEM_RDFS_INF;
        }
        else {
            throw new OntologyException( "Did not recognise this language URI, so cannot determine default model spec: " + languageURI );
        }
    }


    /**
     * <p>Answer the document manager for this model specification. Defaults to
     * a standard instance of {@link OntDocumentManager}</p>
     * @return The document manager to be used by models matching this specification
     */
    public OntDocumentManager getDocumentManager() {
        if (m_docManager == null) {
            // need to set the default document manager
            m_docManager = OntDocumentManager.getInstance();
        }

        return m_docManager;
    }


    /**
     * <p>Set the document manager in this specification</p>
     * @param docMgr The new document manager
     */
    public void setDocumentManager( OntDocumentManager docMgr ) {
        m_docManager = docMgr;
    }

    /**
     * <p>Set the model maker that will be used when the ontology model needs to create
     * an additional container for an imported ontology</p>
     * @param maker The new model maker to use
     */
    public void setImportModelMaker( ModelMaker maker ) {
        m_importsMaker = maker;
    }

    /**
     * <p>Set the model maker used for base models.</p>
     * @param m The model maker that is used to create the base model
     * if one is not supplied when a model is created.
     */
    public void setBaseModelMaker( ModelMaker m ) {
        this.maker = m;
    }

    /**
     * <p>Answer the reasoner that will be used to infer additional entailed
     * triples in the ontology model.</p>
     * @return The reasoner for this specification
     */
    public Reasoner getReasoner() {
        if (m_reasoner == null && m_rFactory != null) {
            // we need to create the reasoner
            // create a new one on each call since reasoners aren't guaranteed to be reusable
            return m_rFactory.create( null );
        }

        return m_reasoner;
    }

    /**
     * <p>Set the reasoner that will be used by ontology models that conform
     * to this specification to compute entailments.
     * <strong>Note:</strong> The reasoner is generated on demand by the reasoner
     * factory. To prevent this spec from having a reasoner, set the reasoner factory
     * to null, see {@link #setReasonerFactory}.
     * </p>
     * @param reasoner The new reasoner
     */
    public void setReasoner( Reasoner reasoner ) {
        m_reasoner = reasoner;
    }

    /**
     * <p>Set the factory object that will be used to generate the reasoner object
     * for this model specification. <strong>Note</strong> that the reasoner itself is cached, so setting
     * the factory after a call to {@link #getReasoner()} will have no effect.</p>
     * @param rFactory The new reasoner factory, or null to prevent any reasoner being used
     */
    public void setReasonerFactory( ReasonerFactory rFactory ) {
        m_rFactory = rFactory;
    }

    /**
     * <p>Answer the current reasoner factory</p>
     * @return The reasoner factory, or null.
     */
    public ReasonerFactory getReasonerFactory() {
        return m_rFactory;
    }

    /**
     * <p>Answer the URI of the ontology lanuage to use when constructing
     * models from this specification.  Well known language URI's are
     * available from the {@link ProfileRegistry}</p>
     * @return The ontology language URI
     */
    public String getLanguage() {
        return m_languageURI;
    }

    /**
     * <p>Set the URI of the ontology to use for models that conform to
     * this specification.</p>
     * @param languageURI The new language URI
     * @exception OntologyException if the URI does not map to a known language profile
     */
    public void setLanguage( String languageURI ) {
        m_languageURI = languageURI;
        m_profile = ProfileRegistry.getInstance().getProfile( m_languageURI );

        if (m_profile == null) {
            throw new OntologyException( "Could not determine an ontology language profile for URI " + m_languageURI );
        }
    }

    /**
     * <p>Answer the language profile for this ontology specification</p>
     * @return An ontology langauge profile object
     */
    public Profile getProfile() {
        return m_profile;
    }

    /**
     *  <p>Create an OntModel according to this model specification.
     *  The base model comes from the attached base ModelMaker.</p>
     *  @return an OntModel satisfying this specification
     */
    public Model doCreateModel() {
        Model m = m_baseModelName == null ? maker.createFreshModel() : maker.createModel( m_baseModelName );
        return new OntModelImpl( this, m );
    }

    /**
     * <p>Create an OntModel according to this model specification.
     * The base model comes from the underlying ModelMaker and is named by the
     *  given name.</p>
     * @see com.hp.hpl.jena.rdf.model.ModelSpec#createModelOver(java.lang.String)
     */
    public Model implementCreateModelOver( String name ) {
        return new OntModelImpl( this, maker.createModel( name, false ) );
    }
    
    /**
        Add the description of this OntModelSpec to the given model under the given
        resource. This same description can be used to create an equivalent OntModelSpec.
        Serialising the description will lose the DocumentManager description.

        TODO allow the DocumentManager to be [de]serialised
        @deprecated ModelSpecs are obsolete
    */
    public Model addDescription( Model d, Resource self )  
        {
        return addDescription
            ( d, self, m_importsMaker, m_languageURI, 
            getDocumentManager(), m_rFactory );
        }

    /**
         Answer a base model constructed according to this specificiation. This is used for the
         "base" (ie non-imported) model for an OntModel.
    */
    public Model createBaseModel()  {
        return ModelFactory.createDefaultModel();
    }

}


/*
    (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

