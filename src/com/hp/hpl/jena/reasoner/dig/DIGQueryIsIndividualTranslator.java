/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            July 19th 2003
 * Filename           $RCSfile: DIGQueryIsIndividualTranslator.java,v $
 * Revision           $Revision: 1.8 $
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
import java.util.*;

import org.w3c.dom.Document;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Concrete;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * <p>
 * Translator that generates a DIG query to test whether a ground name is an individual atom
 * <pre>
 * x rdf:type owl:Thing,  or
 * x rdf:type y  /\  isConcept( y )
 * </pre>
 * or similar.
 * </p>
 *
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com">email</a>)
 * @version Release @release@ ($Id: DIGQueryIsIndividualTranslator.java,v 1.8 2007/01/02 11:49:27 andy_seaborne Exp $)
 */
public class DIGQueryIsIndividualTranslator 
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
     * <p>Construct a translator for the DIG for an individual name.</p>
     */
    public DIGQueryIsIndividualTranslator() {
        super( null, RDF.type.getURI(), null );
    }
    

    // External signature methods
    //////////////////////////////////


    /**
     * <p>Since known concept names are cached by the adapter, we can just look up the
     * current set and map directly to triples</p>
     * @param pattern The pattern to translate to a DIG query
     * @param da The DIG adapter through which we communicate with a DIG reasoner
     */
    public ExtendedIterator find( TriplePattern pattern, DIGAdapter da ) {
        List result = new ArrayList();
        if (da.isIndividual( pattern.getSubject() )) {
            result.add( pattern.asTriple() );
        }
        
        return WrappedIterator.create( result.iterator() );
    }
    
    
    /** For this translation, we ignore premises */
    public ExtendedIterator find( TriplePattern pattern, DIGAdapter da, Model premises ) {
        return find( pattern, da );
    }
    
    
    public Document translatePattern( TriplePattern pattern, DIGAdapter da ) {
        // not used
        return null;
    }


    public Document translatePattern( TriplePattern pattern, DIGAdapter da, Model premises ) {
        // not used
        return null;
    }

    public ExtendedIterator translateResponseHook( Document response, TriplePattern query, DIGAdapter da ) {
        // not used
        return null;
    }

    /**
     * <p>Additional test on the object of the incoming find pattern.</p>
     * @param object The object resource from the incoming pattern
     * @param da The current dig adapter
     * @param premises A model that conveys additional information about the premises
     * of the query, which might assist the check to suceed or fail. By default it
     * is ignored.
     * @return True if this object matches the trigger condition expressed by this translator instance
     */
    public boolean checkObject( Node object, DIGAdapter da, Model premises ) {
        return da.getOntLanguage().THING().asNode().equals( object ) ||
               da.isConcept( object, premises );
    }
    

    public boolean checkSubject( Node subject, DIGAdapter da, Model premises ) {
        return subject instanceof Node_Concrete;
    }
    
    

    // Internal implementation methods
    //////////////////////////////////

    //==============================================================================
    // Inner class definitions
    //==============================================================================

}


/*
 *  (c) Copyright 2001-2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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
