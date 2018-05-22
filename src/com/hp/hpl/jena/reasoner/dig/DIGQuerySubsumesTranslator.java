/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            July 19th 2003
 * Filename           $RCSfile: DIGQuerySubsumesTranslator.java,v $
 * Revision           $Revision: 1.12 $
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
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;



/**
 * <p>
 * Translator that generates DIG allconcepts queries in response to a find query:
 * <pre>
 * :x rdf:subClassOf :y
 * </pre>
 * or similar.
 * </p>
 *
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com">email</a>)
 * @version CVS $Id: DIGQuerySubsumesTranslator.java,v 1.12 2007/01/02 11:49:27 andy_seaborne Exp $
 */
public class DIGQuerySubsumesTranslator 
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
     * <p>Construct a translator for the DIG query 'subsumes'.</p>
     * @param predicate The predicate URI to trigger on
     */
    public DIGQuerySubsumesTranslator( String predicate ) {
        super( null, predicate, null );
    }
    

    // External signature methods
    //////////////////////////////////


    /**
     * <p>Answer a query that will test subsumption between two classes</p>
     */
    public Document translatePattern( TriplePattern pattern, DIGAdapter da ) {
        DIGConnection dc = da.getConnection();
        Document query = dc.createDigVerb( DIGProfile.ASKS, da.getProfile() );
        Element subsumes = da.createQueryElement( query, DIGProfile.SUBSUMES );
        
        // note reversal of subject-object: x rdfs:subClassOf y --> y dig:subsumes x
        da.addClassDescription( subsumes, pattern.getObject() );
        da.addClassDescription( subsumes, pattern.getSubject() );

        return query;
    }


    /**
     * <p>Answer a query that will test subsumption between two classes, given that either one
     * or both may be defined as an expression given the premises</p>
     */
    public Document translatePattern( TriplePattern pattern, DIGAdapter da, Model premises ) {
        DIGConnection dc = da.getConnection();
        Document query = dc.createDigVerb( DIGProfile.ASKS, da.getProfile() );
        Element subsumes = da.createQueryElement( query, DIGProfile.SUBSUMES );
        
        // note reversal of subject-object: x rdfs:subClassOf y --> y dig:subsumes x
        if (pattern.getObject().isBlank()) {
            da.addClassDescription( subsumes, pattern.getObject(), premises );
        }
        else {
            da.addClassDescription( subsumes, pattern.getObject() );
        }
        
        if (pattern.getSubject().isBlank()) {
            da.addClassDescription( subsumes, pattern.getSubject(), premises );
        }
        else {
            da.addClassDescription( subsumes, pattern.getSubject() );
        }
        
        return query;
    }


    /**
     * <p>Answer an iterator of triples that match the original find query.</p>
     */
    public ExtendedIterator translateResponseHook( Document response, TriplePattern query, DIGAdapter da ) {
        return isTrue( response ) ? (ExtendedIterator) new SingletonIterator( query.asTriple() ) : NullIterator.instance;
    }
    
    public boolean checkSubject( com.hp.hpl.jena.graph.Node subject, DIGAdapter da, Model premises ) {
        boolean isSubjectConcept = da.isConcept( subject, premises );
        return isSubjectConcept;
    }
    
    public boolean checkObject( com.hp.hpl.jena.graph.Node object, DIGAdapter da, Model premises ) {
        boolean isObjectConcept = da.isConcept( object, premises );
        return isObjectConcept;
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
