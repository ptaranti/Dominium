/*****************************************************************************
 * Source code information
 * -----------------------
 * Package            Jena 2
 * Web site           http://jena.sourceforge.net
 * Created            13 Aug 2004 15:35
 * Filename           $RCSfile: OntDocManagerVocab.java,v $
 * Revision           $Revision: 1.6 $
 * Release status     @releaseStatus@ $State: Exp $
 *
 * Last modified on   $Date: 2007/01/02 11:49:32 $
 *               by   $Author: andy_seaborne $
 *
 * (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/


// Package
///////////////////////////////////////
package com.hp.hpl.jena.vocabulary;


// Imports
///////////////////////////////////////
import com.hp.hpl.jena.rdf.model.*;




/**
 * Vocabulary definitions from file:vocabularies/ont-manager.rdf
 * @author Auto-generated by jena.schemagen on 13 Aug 2004 15:35
 */
public class OntDocManagerVocab {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string ({@value})</p> */
    public static final String NS = "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    

    // Vocabulary properties
    ///////////////////////////

    /** <p>The representation language used by the ontology document</p> */
    public static final Property language = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#language" );
    
    /** <p>The public URI that is used to refer to the ontology document</p> */
    public static final Property publicURI = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#publicURI" );
    
    /** <p>The prefix string that is used when writing qnames in the ontology's namespace</p> */
    public static final Property prefix = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#prefix" );
    
    /** <p>Boolean flag for whether new ontology models will include the pre-declared 
     *  namespace prefixes</p>
     */
    public static final Property useDeclaredNsPrefixes = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#useDeclaredNsPrefixes" );
    
    /** <p>Specifies URL that will never be loaded as the result of processing an imports 
     *  statement</p>
     */
    public static final Property ignoreImport = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#ignoreImport" );
    
    /** <p>If true, this property denotes that the document manager should process the 
     *  imports closure of documents</p>
     */
    public static final Property processImports = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#processImports" );
    
    /** <p>If true, this property denotes that loaded models should be cached for re-use</p> */
    public static final Property cacheModels = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#cacheModels" );
    
    /** <p>The resolvable URL that an alternative copy of the ontology document may be 
     *  fetched from</p>
     */
    public static final Property altURL = m_model.createProperty( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#altURL" );
    

    // Vocabulary classes
    ///////////////////////////

    /** <p>A class of node that specifies document metadata for the DocumentManager</p> */
    public static final Resource OntologySpec = m_model.createResource( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#OntologySpec" );
    
    /** <p>A node that specifies behavioural options for the document manager</p> */
    public static final Resource DocumentManagerPolicy = m_model.createResource( "http://jena.hpl.hp.com/schemas/2003/03/ont-manager#DocumentManagerPolicy" );
    

    // Vocabulary individuals
    ///////////////////////////

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

