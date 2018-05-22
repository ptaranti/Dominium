/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: IteratorFactory.java,v 1.13 2007/01/02 11:48:30 andy_seaborne Exp $
*/

package com.hp.hpl.jena.rdf.model.impl;


import java.util.*;



import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.*;

import com.hp.hpl.jena.graph.*;


/**
 * @author jjc
 *  Builds Jena Iterators and other things (RDFNode and Statement)
 *  needed in a Model.
 */
public final class IteratorFactory {

    private IteratorFactory(){}
	
    /**
	     @deprecated use Model::asRDFNode( Node ) instead. 
	*/
	static public RDFNode asRDFNode( Node n, Model m ) {
        return m.asRDFNode( n );
	}
		
	/**
	    @deprecated use Model::toStatement( Triple )
	*/
	static public Statement asStatement( Triple t, ModelCom m ) {
        return StatementImpl.toStatement( t, m );
	}

	/**
	 * 
	 */
	static public StmtIterator asStmtIterator(Iterator i, final ModelCom m) {
		return new StmtIteratorImpl(new Map1Iterator(new Map1(){
			public Object map1(Object o) {	return m.asStatement( (Triple) o ); }
		},i));
	}

	/**
	 * 
	 */
	static public ResIterator asResIterator(Iterator i, final ModelCom m) {
		return new ResIteratorImpl(new Map1Iterator(new Map1(){
			public Object map1(Object o) { return m.asRDFNode( (Node) o ); }
		},i),null);
	}

	/**
	 * 
	 */
	static public NodeIterator asRDFNodeIterator(Iterator i, final ModelCom m) {
		return new NodeIteratorImpl(new Map1Iterator(new Map1(){
			public Object map1(Object o) { return m.asRDFNode( (Node) o ); }
		},i),null);
	}
	    
    static  Resource asResource(Node n,ModelCom m) {
    	return asResource(n,Resource.class,m);
    	
    }	    
    
    static  Property asProperty(Node n,ModelCom m) {
    	return (Property)asResource(n,Property.class,m);
    }
    
    static  Literal asLiteral(Node n,ModelCom m) {
    	// return (Literal) m.getNodeAs( n, Literal.class );
        return (Literal) m.getNodeAs( n, Literal.class );
    }
    
    static  Resource asResource(Node n, Class cl,ModelCom m) {
    	return (Resource)m.getNodeAs(n,cl);
    }
}

/*
    (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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
