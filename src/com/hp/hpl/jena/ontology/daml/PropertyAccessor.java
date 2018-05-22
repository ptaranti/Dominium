/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena
 * Created            26 Jan 2001
 * Filename           $RCSfile: PropertyAccessor.java,v $
 * Revision           $Revision: 1.9 $
 * Release status     Preview-release $State: Exp $
 *
 * Last modified on   $Date: 2007/01/08 14:40:52 $
 *               by   $Author: ian_dickinson $
 *
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.ontology.daml;


// Imports
///////////////
import com.hp.hpl.jena.rdf.model.*;


/**
 * <p>Encapsulates the standard methods of modifying a property on a DAML value.</p>
 *
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com">email</a>)
 * @version CVS info: $Id: PropertyAccessor.java,v 1.9 2007/01/08 14:40:52 ian_dickinson Exp $
 * @deprecated The DAML API is scheduled to be removed from Jena 2.6 onwards. Please use the DAML profile in the main ontology API
 */
public interface PropertyAccessor
{
    // Constants
    //////////////////////////////////


    // External signature methods
    //////////////////////////////////

    /**
     * <p>Answer the property that this accessor works on</p>
     *
     * @return A property
     */
    public Property getProperty();


    /**
     * <p>Answer the number of values that the encapsulated property has in the
     * RDF model.</p>
     *
     * @return The number statements for this property in the model.
     */
    public int count();


    /**
     * <p>Answer an iteration over the DAML values that this property has in the
     * RDF model.</p>
     * <p><strong>Note:</strong> In Jena 1, this method took a paramter <code>closed</code>,
     * to control whether the transitive closure of the class and property hierarchies was
     * considered.  Computing these entailments is now handled by the reaoner attached to the
     * DAML or Ontology model, and is not controlled by a method parameter at the API level.
     * Accordingly, this parameter has been removed.  See the documentation for details on
     * controlling the operation of the reasoners.</p>
     *
     * @return An iteration over the values of the encapsulated property.
     */
    public NodeIterator getAll();


    /**
     * <p>Answer a general value of the encapsulated property. If it has no values, answer
     * null. If it has one value, answer that value. Otherwise, answer an undetermined
     * member of the set of values. See also {@link #getDAMLValue}.
     *
     * @return A value for the encapsulated property in the RDF model, or null
     *         if the property has no value.
     */
    public RDFNode get();


    /**
     * <p>Answer the value of the encapsulated property, presented as a DAML list. If it has no values, answer
     * null. If it has one value, answer that value (as a list). Otherwise, answer an undetermined
     * member of the set of values. See also {@link #getDAMLValue}.
     *
     * @return A value for the encapsulated property in the RDF model, or null
     *         if the property has no value.
     * @exception com.hp.hpl.jena.ontology.ConversionException if the value is not a list
     */
    public DAMLList getList();


    /**
     * <p>Answer a value of the encapsulated property, converted to a DAML common value</p>
     *
     * @return A DAML value for the encapsulated property in the RDF model, or null
     *         if the property has no value.
     */
    public DAMLCommon getDAMLValue();


    /**
     * <p>Add a value to the encapsulated property.</p>
     *
     * @param value The value to be added.
     */
    public void add( RDFNode value );


    /**
     * <p>Remove a value from the encapsulated property.</p>
     *
     * @param value The value to be removed.
     */
    public void remove( RDFNode value );


    /**
     * <p>Answer true if the encapsulated property has the given value as one of its
     * values.</p>
     *
     * @param value A value to test for
     * @return True if the RDF model contains a statement giving a value for
     *         the encapsulated property matching the given value.
     */
    public boolean hasValue( RDFNode value );

}



/*
    (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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

