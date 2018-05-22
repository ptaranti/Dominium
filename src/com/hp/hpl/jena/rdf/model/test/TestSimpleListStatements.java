/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: TestSimpleListStatements.java,v 1.15 2007/01/02 11:48:26 andy_seaborne Exp $
*/

package com.hp.hpl.jena.rdf.model.test;

/**
	@author bwm out of kers
*/

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;


import junit.framework.*;

public class TestSimpleListStatements extends ModelTestBase
    {    
        
    public TestSimpleListStatements( String name )
        { super( name ); }
    
    public static TestSuite suite()
        { return new TestSuite( TestSimpleListStatements.class ); }   
    
    Model model = null; 
    
    static boolean booleanValue = true;
    static char    charValue   = 'c';
    static long    longValue   = 456;
    static float   floatValue  = 5.67F;
    static double  doubleValue = 6.78;
    static String   stringValue ="stringValue";
    static String   langValue   = "en";   
        
    protected void setUp() throws java.lang.Exception {
        
    	model = ModelFactory.createDefaultModel();
    	model.createResource("http://example.org/boolean")
    	     .addProperty(RDF.value, booleanValue);             
        model.createResource("http://example.org/char")
             .addProperty(RDF.value, charValue);             
        model.createResource("http://example.org/long")             
             .addProperty(RDF.value, longValue);              
        model.createResource("http://example.org/float")
             .addProperty(RDF.value, floatValue);            
        model.createResource("http://example.org/double")
             .addProperty(RDF.value, doubleValue);             
        model.createResource("http://example.org/string")
             .addProperty(RDF.value, stringValue);             
        model.createResource("http://example.org/langString")
             .addProperty(RDF.value, stringValue, langValue);
    	
    }
    
    protected void tearDown() throws java.lang.Exception {
    	model.close();
        model = null;
    }
    
    public void testBoolean() {
        StmtIterator iter = model.listStatements(null, null, booleanValue);
        int i =0;
        while (iter.hasNext()) {
            i++;
            assertEquals(iter.nextStatement().getSubject().getURI(),
                         "http://example.org/boolean");
        }
        assertEquals(1, i);
    }
    
    public void testChar() {
        StmtIterator iter = model.listStatements(null, null, charValue);
        int i =0;
        while (iter.hasNext()) {
            i++;
            assertEquals(iter.nextStatement().getSubject().getURI(),
                         "http://example.org/char");
        }
        assertEquals(1, i);
    }
    
    public void testLong() {
        StmtIterator iter = model.listStatements(null, null, longValue);
        int i =0;
        while (iter.hasNext()) {
            i++;
            assertEquals(iter.nextStatement().getSubject().getURI(),
                         "http://example.org/long");
        }
        assertEquals(1, i);
    }
    
    public void testFloat() {
        StmtIterator iter = model.listStatements(null, null, floatValue);
        int i =0;
        while (iter.hasNext()) {
            i++;
            assertEquals(iter.nextStatement().getSubject().getURI(),
                         "http://example.org/float");
        }
        assertEquals(1, i);
    }
    
    public void testDouble() {
        StmtIterator iter = model.listStatements(null, null, doubleValue);
        int i =0;
        while (iter.hasNext()) {
            i++;
            assertEquals(iter.nextStatement().getSubject().getURI(),
                         "http://example.org/double");
        }
        assertEquals(1, i);
    }
    
    public void testString() {
        StmtIterator iter = model.listStatements(null, null, stringValue);
        int i =0;
        while (iter.hasNext()) {
            i++;
            assertEquals(iter.nextStatement().getSubject().getURI(),
                         "http://example.org/string");
        }
        assertEquals(1, i);
    }
    
    public void testLangString() {
        StmtIterator iter = model.listStatements(null, null,
                                                           stringValue, langValue);
        int i =0;
        while (iter.hasNext()) {
            i++;
            assertEquals(iter.nextStatement().getSubject().getURI(),
                         "http://example.org/langString");
        }
        assertEquals(1, i);
    }
        
    
    public void testAll() {
        StmtIterator iter = model.listStatements(null, null, (RDFNode) null);
        int i =0;
        while (iter.hasNext()) {
            i++;
            iter.next();
        }
        assertEquals(7, i);
    }
    
    public void testAllString() {
        StmtIterator iter = model.listStatements(null, null, (String) null);
        int i =0;
        while (iter.hasNext()) {
            i++;
            iter.next();
        }
        assertEquals(7, i);
    }

    public Model modelWithStatements( StmtIterator it )
        {
        Model m = ModelFactory.createDefaultModel();
        while (it.hasNext()) m.add( it.nextStatement() );
        return m;
        }
        
    public void checkReturns( String things, StmtIterator it )
        {
        Model wanted = modelWithStatements( things );
        Model got = modelWithStatements( it );
        if (wanted.isIsomorphicWith( got ) == false)
            fail( "wanted " + wanted + " got " + got );
        }
        
    public void testListStatementsSPO()
        {
        Model m = ModelFactory.createDefaultModel();
        Resource A = resource( m, "A" ), X = resource( m, "X" );
        Property P = property( m, "P" ), P1 = property( m, "P1" );
        RDFNode O = resource( m, "O" ), Y = resource( m, "Y" );
        String S1 = "S P O; S1 P O; S2 P O";
        String S2 = "A P1 B; A P1 B; A P1 C";
        String S3 = "X P1 Y; X P2 Y; X P3 Y";
        modelAdd( m, S1 );
        modelAdd( m,  S2 );
        modelAdd( m, S3 );
        checkReturns( S1, m.listStatements( null, P, O ) );
        checkReturns( S2, m.listStatements( A, P1, (RDFNode) null ) );
        checkReturns( S3, m.listStatements( X, null, Y ) );
        m.close();
        }
        
    public void testListStatementsClever()
        {
        Model m = ModelFactory.createDefaultModel();
        modelAdd( m, "S P O; S P O2; S P2 O; S2 P O" );
        Selector sel = new SimpleSelector( null, null, (RDFNode) null )
            {
            public boolean test( Statement st )
                { return 
                        st.getSubject().toString().length() 
                        + st.getPredicate().toString().length()
                        + st.getObject().toString().length()
                        == 15; /* eh:/S + eh:/P + eh:/O */
                }
                
            public boolean isSimple()
                { return false; }
            };
        checkReturns( "S P O", m.listStatements( sel ) );
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
