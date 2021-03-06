/*
 	(c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: NewRegressionStatementMethods.java,v 1.3 2007/01/02 11:49:22 andy_seaborne Exp $
*/

package com.hp.hpl.jena.regression;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.regression.Regression.*;

import junit.framework.*;

public class NewRegressionStatementMethods extends NewRegressionBase
    {
    public NewRegressionStatementMethods( String name )
        { super( name ); }

    public static Test suite()
        { return new TestSuite( NewRegressionStatementMethods.class ); }

    protected Model getModel()
        { return ModelFactory.createDefaultModel(); }

    protected Model m;
    protected Resource r;

    public void setUp()
        { 
        m = getModel(); 
        r = m.createResource();
        }

    public void testGetResource()
        {
        assertEquals( r, m.createStatement( r, RDF.value, r ).getResource() );
        }
    
    public void testGetResourceFailure()
        {
        try { m.createStatement( r, RDF.value, false ).getResource(); fail( "should trap non-resource object" ); }
        catch (ResourceRequiredException e) { pass(); }
        }
    
    public void testGetTrueBoolean()
        {
        assertEquals( true, m.createStatement( r, RDF.value, true ).getLiteral().getBoolean() );
        }
    
    public void testGetLiteralFailure()
        {
        try { m.createStatement( r, RDF.value, r ).getLiteral(); fail( "should trap non-literal object" ); }
        catch (LiteralRequiredException e) { pass(); }
        }
    
    public void testBoolean()
        {
        assertEquals( true, m.createStatement( r, RDF.value, true ).getBoolean() );
        }
    
    public void testByte()
        {
        assertEquals( tvByte, m.createStatement( r, RDF.value, tvByte ).getByte() );
        }
    
    public void testShort()
        {
        assertEquals( tvShort, m.createStatement( r, RDF.value, tvShort ).getShort() );
        }
    
    public void testInt()
        {
        assertEquals( tvInt, m.createStatement( r, RDF.value, tvInt ).getInt() );
        }
    
    public void testLong()
        {
        assertEquals( tvLong, m.createStatement( r, RDF.value, tvLong ).getLong() );
        }
    
    public void testChar()
        {
        assertEquals( tvChar, m.createStatement( r, RDF.value, tvChar ).getChar() );
        }
    
    public void testFloat()
        {
        assertEquals( tvFloat, m.createStatement( r, RDF.value, tvFloat ).getFloat(), fDelta );
        }
    
    public void testDouble()
        {
        assertEquals( tvDouble, m.createStatement( r, RDF.value, tvDouble ).getDouble(), dDelta );
        }
    
    public void testString()
        {
        assertEquals( tvString, m.createStatement( r, RDF.value, tvString ).getString() );
        }
    
    public void testStringWithLanguage()
        {
        String lang = "fr";
        assertEquals( tvString, m.createStatement( r, RDF.value, tvString, lang ).getString() );
        assertEquals( lang, m.createStatement( r, RDF.value, tvString, lang ).getLanguage() );
        }
    
    public void testResObj()
        {
        Resource   tvResObj = m.createResource( new ResTestObjF() );
        assertEquals( tvResObj, m.createStatement( r, RDF.value, tvResObj ).getResource() );
        }
    
    public void testLitObj()
        {
        assertEquals( tvLitObj, m.createStatement( r, RDF.value, tvLitObj ).getObject( new LitTestObjF() ) );
        }
    
    public void testBag()
        {
        Bag tvBag = m.createBag();
        assertEquals( tvBag, m.createStatement( r, RDF.value, tvBag ).getBag() );
        }
    
    public void testSeq()
        {
        Seq tvSeq = m.createSeq();
        assertEquals( tvSeq, m.createStatement( r, RDF.value, tvSeq ).getSeq() );
        }
    
    public void testAlt()
        {
        Alt tvAlt = m.createAlt();
        assertEquals( tvAlt, m.createStatement( r, RDF.value, tvAlt ).getAlt() );
        }

    public void testChangeObjectBoolean()
        {
        Statement sTrue = loadInitialStatement();
        Statement sFalse = sTrue.changeObject( false );
        checkChangedStatementSP( sFalse );
        assertEquals( false, sFalse.getBoolean() );
        checkCorrectStatements( sTrue, sFalse );
        assertTrue( m.contains( r, RDF.value, false ) );
        }

    public void testChangeObjectByte()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvByte );
        checkChangedStatementSP( changed );
        assertEquals( tvByte, changed.getByte() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvByte ) );
        }

    public void testChangeObjectShort()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvShort );
        checkChangedStatementSP( changed );
        assertEquals( tvShort, changed.getShort() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvShort ) );
        }

    public void testChangeObjectInt()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvInt );
        checkChangedStatementSP( changed );
        assertEquals( tvInt, changed.getInt() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvInt ) );
        }

    public void testChangeObjectLong()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvLong );
        checkChangedStatementSP( changed );
        assertEquals( tvLong, changed.getLong() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvLong ) );
        }

    public void testChangeObjectChar()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvChar );
        checkChangedStatementSP( changed );
        assertEquals( tvChar, changed.getChar() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvChar ) );
        }
    
    public void testChangeObjectFloat()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvFloat );
        checkChangedStatementSP( changed );
        assertEquals( tvFloat, changed.getFloat(), fDelta );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvFloat ) );
        }

    public void testChangeObjectDouble()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvDouble );
        checkChangedStatementSP( changed );
        assertEquals( tvDouble, changed.getDouble(), dDelta );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvDouble ) );
        }

    public void testChangeObjectString()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvString );
        checkChangedStatementSP( changed );
        assertEquals( tvString, changed.getString() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvString ) );
        }

    public void testChangeObjectStringWithLanguage()
        {
        String lang = "en";
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvString, lang );
        checkChangedStatementSP( changed );
        assertEquals( tvString, changed.getString() );
        assertEquals( lang, changed.getLanguage() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvString, lang ) );
        }

    public void testChangeObjectResObject()
        {
        Resource   tvResObj = m.createResource( new ResTestObjF() );
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvResObj );
        checkChangedStatementSP( changed );
        assertEquals( tvResObj, changed.getResource() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvResObj ) );
        }

    public void testChangeObjectLiteral()
        {
        Statement sTrue = loadInitialStatement();
        m.remove( sTrue );
        assertFalse( m.contains( sTrue ) );
        assertFalse( m.contains( r, RDF.value, true ) );
        }

    public void testChangeObjectYByte()
        {
        Statement sTrue = loadInitialStatement();
        Statement changed = sTrue.changeObject( tvByte );
        checkChangedStatementSP( changed );
        assertEquals( tvByte, changed.getByte() );
        checkCorrectStatements( sTrue, changed );
        assertTrue( m.contains( r, RDF.value, tvByte ) );
        }
    
    protected void checkCorrectStatements( Statement sTrue, Statement changed )
        {
        assertFalse( m.contains( sTrue ) );
        assertFalse( m.contains( r, RDF.value, true ) );
        assertTrue( m.contains( changed ) );
        }

    protected void checkChangedStatementSP( Statement changed )
        {
        assertEquals( r, changed.getSubject() );
        assertEquals( RDF.value, changed.getPredicate() );
        }

    protected Statement loadInitialStatement()
        {
        Statement sTrue = m.createStatement( r, RDF.value, true );
        m.add( sTrue );
        return sTrue;
        }
    }


/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
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