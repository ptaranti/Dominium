/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: TestAllImpl.java,v 1.8 2007/01/02 11:50:23 andy_seaborne Exp $
*/

package com.hp.hpl.jena.enhanced.test;
import com.hp.hpl.jena.enhanced.*;
import com.hp.hpl.jena.graph.*;

/**
 * @see TestObjectImpl
 * @author  jjc
 */
public class TestAllImpl extends TestCommonImpl implements TestSubject, TestProperty, TestObject {

    public static final Implementation factory = new Implementation() {
    public boolean canWrap( Node n, EnhGraph eg )
        { return true; }
    public EnhNode wrap(Node n,EnhGraph eg) {
        return new TestAllImpl(n,eg);
    }
};
    
    /** Creates a new instance of TestAllImpl */
    private TestAllImpl(Node n,EnhGraph eg) {
        super( n, eg );
    }
    
    public boolean supports( Class t )
        {
        // return convertTo( t ) != null;
        return
            t == TestProperty.class ? isProperty()
            : t == TestSubject.class ? isSubject()
            : t == TestObject.class ? isObject()
            : false
            ;
        }
        
    public boolean isObject() {
        return findObject() != null;
    }
    
    public boolean isProperty() {
        return findPredicate() != null;
    }
    
    public boolean isSubject() {
        return findSubject() != null;
    } 
    
    public TestObject anObject() {
        if (!isProperty())
            
            throw new IllegalStateException("Node is not the property of a triple.");
        return (TestObject)enhGraph.getNodeAs(findPredicate().getObject(),TestObject.class);
    }
    
    public TestProperty aProperty() {
        if (!isSubject())
            throw new IllegalStateException("Node is not the subject of a triple.");
        return (TestProperty)enhGraph.getNodeAs(findSubject().getPredicate(),TestProperty.class);
    }
    
    public TestSubject aSubject() {
        if (!isObject())
            throw new IllegalStateException("Node is not the object of a triple.");
        return (TestSubject)enhGraph.getNodeAs(findObject().getSubject(),TestSubject.class);
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
