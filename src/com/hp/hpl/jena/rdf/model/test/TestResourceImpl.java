/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP, all rights reserved.
  [See end of file]
  $Id: TestResourceImpl.java,v 1.7 2007/01/02 11:48:26 andy_seaborne Exp $
*/

package com.hp.hpl.jena.rdf.model.test;

import com.hp.hpl.jena.rdf.model.*;

import junit.framework.*;

/**
	TestResourceImpl - fresh tests, make sure as-ing works a bit.

	@author kers
*/
public class TestResourceImpl extends ModelTestBase 
    {
	public TestResourceImpl( String name ) 
        { super(name); }

    public static TestSuite suite()
        { return new TestSuite( TestResourceImpl.class ); }

    /**
        Test that a non-literal node can be as'ed into a resource
    */
    public void testCannotAsNonLiteral()
        { Model m = ModelFactory.createDefaultModel();  
        resource( m, "plumPie" ).as( Resource.class ); }
    
    /**
        Test that a literal node cannot be as'ed into a resource.
    */    
    public void testAsLiteral()
        { Model m = ModelFactory.createDefaultModel();  
        try 
            { literal( m, "17" ).as( Resource.class );  
            fail( "literals cannot be resources"); }
        catch (ResourceRequiredException e)
            { pass(); }}
    
    public void testNameSpace()
        { 
        assertEquals( "eh:x", resource( "eh:xyz" ).getNameSpace() ); 
        assertEquals( "http://d/", resource( "http://d/stuff" ).getNameSpace() ); 
        assertEquals( "ftp://dd.com/12345", resource( "ftp://dd.com/12345" ).getNameSpace() ); 
        assertEquals( "http://domain/spoo#", resource( "http://domain/spoo#anchor" ).getNameSpace() ); 
        assertEquals( "ftp://abd/def#ghi#", resource( "ftp://abd/def#ghi#e11-2" ).getNameSpace() ); 
        }
    
    public void testLocalName()
        { 
        assertEquals( "yz", resource( "eh:xyz" ).getLocalName() );
        }
    
    public void testHasURI()
        { 
        assertTrue( resource( "eh:xyz" ).hasURI( "eh:xyz" ) );
        assertFalse( resource( "eh:xyz" ).hasURI( "eh:1yz" ) );
        assertFalse( ResourceFactory.createResource().hasURI( "42" ) );
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