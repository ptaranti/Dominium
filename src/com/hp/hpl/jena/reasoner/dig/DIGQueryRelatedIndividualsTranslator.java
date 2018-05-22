/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            April 23rd 2006
 * Filename           $RCSfile: DIGQueryRelatedIndividualsTranslator.java,v $
 * Revision           $Revision: 1.2 $
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
import org.w3c.dom.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.vocabulary.RDF;



/**
 * <p>
 * Translator that generates DIG relatedIndividuals queries in response to a find queries:
 * <pre>
 * * :r *
 * </pre>
 * or similar.
 * </p>
 *
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com">email</a>)
 * @version CVS $Id: DIGQueryRelatedIndividualsTranslator.java,v 1.2 2007/01/02 11:49:27 andy_seaborne Exp $
 */
public class DIGQueryRelatedIndividualsTranslator
    extends DIGQueryTranslator
{

    // Constants
    //////////////////////////////////

    // Static variables
    //////////////////////////////////

    // Instance variables
    //////////////////////////////////


    // Constructors
    //////////////////////////////////

    /**
     * <p>Construct a translator for the DIG query 'relatedIndividuals'.</p>
     */
    public DIGQueryRelatedIndividualsTranslator() {
        super( ALL, null, ALL );
    }


    // External signature methods
    //////////////////////////////////


    /**
     * <p>Answer a query that will list the role fillers for an individual-role pair</p>
     */
    public Document translatePattern( TriplePattern pattern, DIGAdapter da ) {
        DIGConnection dc = da.getConnection();
        Document query = dc.createDigVerb( DIGProfile.ASKS, da.getProfile() );

        Element instances = da.createQueryElement( query, DIGProfile.RELATED_INDIVIDUALS );
        da.addNamedElement( instances, DIGProfile.RATOM, da.getNodeID( pattern.getPredicate() ) );

        return query;
    }


    /**
     * <p>Answer an iterator of triples that match the original find query.</p>
     */
    public ExtendedIterator translateResponseHook( Document response, TriplePattern query, DIGAdapter da ) {
        return translateIndividualPairSetResponse( response, query );
    }


    public Document translatePattern( TriplePattern pattern, DIGAdapter da, Model premises ) {
        // not used
        return null;
    }

    public boolean checkPredicate( com.hp.hpl.jena.graph.Node predicate, DIGAdapter da, Model premises ) {
        // check that the predicate is not a datatype property
        if (predicate.isConcrete()) {
            Resource p = (Resource) da.m_sourceData.getRDFNode( predicate );
            String pNS = p.getNameSpace();
            return !(da.m_sourceData.contains( p, RDF.type, da.m_sourceData.getProfile().DATATYPE_PROPERTY() ) ||
                     RDFS.getURI().equals( pNS ) ||
                     RDF.getURI().equals( pNS ) ||
                     OWL.getURI().equals( pNS ));
        }
        else {
            return false;
        }
    }


    // Internal implementation methods
    //////////////////////////////////

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
