/*
 	(c) Copyright 2006 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: TestPropertyImpl.java,v 1.1 2007/01/11 10:13:46 chris-dollin Exp $
*/

package com.hp.hpl.jena.rdf.model.test;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.*;

public class TestPropertyImpl extends ModelTestBase
    {
    public TestPropertyImpl( String name )
        { super( name ); }
    
    public void testOrdinalValues() 
        {
        testRDFOrdinalValue( 1, "_1" );
        testRDFOrdinalValue( 2, "_2" );
        testRDFOrdinalValue( 3, "_3" );
        testRDFOrdinalValue( 4, "_4" );
        testRDFOrdinalValue( 5, "_5" );
        testRDFOrdinalValue( 6, "_6" );
        testRDFOrdinalValue( 7, "_7" );
        testRDFOrdinalValue( 8, "_8" );
        testRDFOrdinalValue( 9, "_9" );
        testRDFOrdinalValue( 10, "_10" );
        testRDFOrdinalValue( 100, "_100" );
        testRDFOrdinalValue( 1234, "_1234" );
        testRDFOrdinalValue( 67890, "_67890" );
        }

    public void testNonOrdinalRDFURIs()
        {
        testRDFOrdinalValue( 0, "x" );
        testRDFOrdinalValue( 0, "x1" );
        testRDFOrdinalValue( 0, "_x" );
        testRDFOrdinalValue( 0, "x123" );
        testRDFOrdinalValue( 0, "0xff" );
        testRDFOrdinalValue( 0, "_xff" );
        }
    
    private void testRDFOrdinalValue( int i, String local )
        { testOrdinalValue( i, RDF.getURI() + local ); }

    public void testNonRDFElementURIsHaveOrdinal0()
        {
        testOrdinalValue( 0, "foo:bar" );
        testOrdinalValue( 0, "foo:bar1" );
        testOrdinalValue( 0, "foo:bar2" );
        testOrdinalValue( 0, RDFS.getURI() + "_17" );
        }

    private void testOrdinalValue( int i, String URI )
        {
        String message = "property should have expected ordinal value for " + URI;
        assertEquals( message, i, createProperty( URI ).getOrdinal() );
        }

    protected Property createProperty( String uri )
        { return new PropertyImpl( uri ); }
    }

