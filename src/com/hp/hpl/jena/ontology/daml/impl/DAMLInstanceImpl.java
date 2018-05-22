/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena
 * Created            4 Jan 2001
 * Filename           $RCSfile: DAMLInstanceImpl.java,v $
 * Revision           $Revision: 1.12 $
 * Release status     Preview-release $State: Exp $
 *
 * Last modified on   $Date: 2007/01/08 14:40:30 $
 *               by   $Author: ian_dickinson $
 *
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.ontology.daml.impl;


// Imports
///////////////
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.enhanced.*;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.ontology.daml.*;
import com.hp.hpl.jena.vocabulary.*;



/**
 * Java representation of a DAML Instance.
 *
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com">email</a>)
 * @version CVS info: $Id: DAMLInstanceImpl.java,v 1.12 2007/01/08 14:40:30 ian_dickinson Exp $
 * @deprecated The DAML API is scheduled to be removed from Jena 2.6 onwards. Please use the DAML profile in the main ontology API
 */
public class DAMLInstanceImpl
    extends DAMLCommonImpl
    implements DAMLInstance
{
    // Constants
    //////////////////////////////////


    // Static variables
    //////////////////////////////////

    /**
     * A factory for generating DAMLDataInstance facets from nodes in enhanced graphs.
     * Note: should not be invoked directly by user code: use
     * {@link com.hp.hpl.jena.rdf.model.RDFNode#as as()} instead.
     */
    public static Implementation factory = new Implementation() {
        public EnhNode wrap( Node n, EnhGraph eg ) {
            if (canWrap( n, eg )) {
                return new DAMLInstanceImpl( n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n.toString() + " to DAMLDatatype" );
            }
        }

        public boolean canWrap( Node node, EnhGraph eg ) {
            Profile profile = (eg instanceof OntModel) ? ((OntModel) eg).getProfile() : null;
            return (profile != null)  &&  profile.isSupported( node, eg, DAMLInstance.class );
        }
    };

    // Instance variables
    //////////////////////////////////

    /** Property accessor for sameIndividualAs */
    protected PropertyAccessor m_propsameIndividualAs = null;



    // Constructors
    //////////////////////////////////

    /**
     * <p>
     * Construct a DAML  instance represented by the given node in the given graph.
     * </p>
     *
     * @param n The node that represents the resource
     * @param g The enh graph that contains n
     */
    public DAMLInstanceImpl( Node n, EnhGraph g ) {
        super( n, g );
    }




    // External signature methods
    //////////////////////////////////


    /**
     * Answer a key that can be used to index collections of this DAML instance for
     * easy access by iterators.  Package access only.
     *
     * @return a key object.
     */
    Object getKey() {
        return DAML_OIL.Thing.getURI();
    }


    /**
     * Property accessor for <code>daml:sameIndividualAs</code> property on a DAML instance.
     *
     * @return a property accessor
     */
    public PropertyAccessor prop_sameIndividualAs() {
        if (m_propsameIndividualAs == null) {
            m_propsameIndividualAs = new PropertyAccessorImpl( getVocabulary().sameIndividualAs(), this );
        }

        return m_propsameIndividualAs;
    }


    /**
     * Return an iterator over all of the instances that are the same as this one,
     * by generating the transitive closure over the <code>daml:samePropertyAs</code>
     * property.
     *
     * @return an iterator whose values will all be DAMLInstance objects
     */
    public ExtendedIterator getSameInstances() {
        return listAs( getProfile().SAME_INDIVIDUAL_AS(), "SAME_INDIVIDUAL_AS", DAMLInstance.class );
    }



    /**
     * Answer an iterator over all of the DAML objects that are equivalent to this
     * instance, which will be the union of <code>daml:equivalentTo</code> and
     * <code>daml:sameIndividualAs</code>.
     *
     * @return an iterator ranging over every equivalent DAML instance - each value of
     *         the iteration should be a DAMLInstance object.
     */
    public ExtendedIterator getEquivalentValues() {
        return UniqueExtendedIterator.create( listAs( getProfile().SAME_AS(), "SAME_AS", DAMLInstance.class ).andThen( getSameInstances() ) );
    }


    /**
     * Answer a property accessor for a user defined property.
     *
     * @param property An RDF or DAML property
     * @return a property accessor, that simplifies some of the basic operations
     *         of a given property on a given object
     */
    public PropertyAccessor accessProperty( Property property ) {
        return new PropertyAccessorImpl( property, this );
    }


    // Internal implementation methods
    //////////////////////////////////

    /**
     * Answer a value that will be a default type to include in an iteration of
     * the value's rdf types.  Typically there is no default (null), but for an
     * instance we want to ensure that the default type is daml:Thing.
     *
     * @return The default type: <code>daml:Thing</code>
     */
    protected Resource getDefaultType() {
        return getVocabulary().Thing();
    }



    //==============================================================================
    // Inner class definitions
    //==============================================================================


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

