/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            02-Apr-2003
 * Filename           $RCSfile: TestListSyntaxCategories.java,v $
 * Revision           $Revision: 1.27 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2007/01/02 11:51:55 $
 *               by   $Author: andy_seaborne $
 *
 * (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.ontology.impl.test;


// Imports
///////////////
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.vocabulary.*;

import junit.framework.*;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * <p>
 * Unit tests for listXXX methods on ontmodel
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: TestListSyntaxCategories.java,v 1.27 2007/01/02 11:51:55 andy_seaborne Exp $
 */
public class TestListSyntaxCategories
    extends TestCase
{
    // Constants
    //////////////////////////////////
    public static final String NS = "http://jena.hpl.hp.com/testing/ontology#";


    // Static variables
    //////////////////////////////////

    protected static DoListTest[] testCases = {
        // Ontology
        new DoListTest( "OWL list ontologies",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {"http://jena.hpl.hp.com/testing/ontology"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listOntologies();
            }
            public boolean test( Resource r ) {
                return r instanceof Ontology;
            }
        },
        new DoListTest( "DAML list ontologies",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {"http://jena.hpl.hp.com/testing/ontology"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listOntologies();
            }
            public boolean test( Resource r ) {
                return r instanceof Ontology;
            }
        },
        // Properties
        new DoListTest( "OWL list properties",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"p"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listOntProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL list object properties",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  2,
                        new String[] {NS+"op", NS+"op1"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listObjectProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL list datatype properties",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"dp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listDatatypeProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL list functional properties",  "file:testing/ontology/owl/list-syntax/test-proptypes.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"fp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listFunctionalProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof FunctionalProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL list transitive properties",  "file:testing/ontology/owl/list-syntax/test-proptypes.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"tp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listTransitiveProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof TransitiveProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL list symmetric properties",  "file:testing/ontology/owl/list-syntax/test-proptypes.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"sp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listSymmetricProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof SymmetricProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL list inverse functional properties",  "file:testing/ontology/owl/list-syntax/test-proptypes.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"ifp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listInverseFunctionalProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof InverseFunctionalProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "DAML list properties",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  5,
                        new String[] {NS+"p", NS+"rdf-p", NS+"dp", NS+"op", NS+"op1"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listOntProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "DAML list object properties",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  2,
                        new String[] {NS+"op", NS+"op1"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listObjectProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "DAML list datatype properties",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"dp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listDatatypeProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "DAML list functional properties",  "file:testing/ontology/daml/list-syntax/test-proptypes.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"fp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listFunctionalProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof FunctionalProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "DAML list transitive properties",  "file:testing/ontology/daml/list-syntax/test-proptypes.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"tp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listTransitiveProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof TransitiveProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "DAML list inverse functional properties",  "file:testing/ontology/daml/list-syntax/test-proptypes.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"ifp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listInverseFunctionalProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof InverseFunctionalProperty &&
                       r instanceof Property;
            }
        },

        // individuals
        new DoListTest( "OWL list individuals",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  8,
                        new String[] {NS+"A0", NS+"A1", NS+"C0", NS+"a0", NS+"a1", NS+"a2", NS+"z0", NS+"z1"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listIndividuals();
            }
            public boolean test( Resource r ) {
                return r instanceof Individual;
            }
        },
        new DoListTest( "OWL list typed individuals",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM,  2,
                new String[] {NS+"A0", NS+"A1"} )
        {
            public Iterator doList( OntModel m ) {
                Model mVocab = ModelFactory.createDefaultModel();
                Resource cA = mVocab.createResource( "http://jena.hpl.hp.com/testing/ontology#A");
                return m.listIndividuals( cA );
            }
            public boolean test( Resource r ) {
                return r instanceof Individual;
            }
        },
        new DoListTest( "empty OWL list individuals",  null,  OntModelSpec.OWL_MEM,  0, new String[] {} )
        {
            public Iterator doList( OntModel m ) {
                return m.listIndividuals();
            }
            public boolean test( Resource r ) {
                return r instanceof Individual;
            }
        },
        new DoListTest( "empty OWL+rule list individuals",  null,  OntModelSpec.OWL_MEM_RULE_INF,  0, new String[] {} )
        {
            public Iterator doList( OntModel m ) {
                return m.listIndividuals();
            }
            public boolean test( Resource r ) {
                return r instanceof Individual;
            }
        },
        new DoListTest( "DAML list individuals",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  6,
                        new String[] {NS+"A0", NS+"A1", NS+"C0", NS+"a1", NS+"a2", NS+"a0"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listIndividuals();
            }
            public boolean test( Resource r ) {
                return r instanceof Individual;
            }
        },
        new DoListTest( "empty DAML+rule list individuals",  null,  OntModelSpec.DAML_MEM_RULE_INF,  0, new String[] {} )
        {
            public Iterator doList( OntModel m ) {
                return m.listIndividuals();
            }
            public boolean test( Resource r ) {
                return r instanceof Individual;
            }
        },

        new DoListTest( "OWL list all different",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listAllDifferent();
            }
            public boolean test( Resource r ) {
                return r instanceof AllDifferent;
            }
        },
        new DoListTest( "DAML list all different",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  0,  null,
                        true /* exception expected */ )
        {
            public Iterator doList( OntModel m ) {
                return m.listAllDifferent();
            }
            public boolean test( Resource r ) {
                return r instanceof AllDifferent;
            }
        },

        // classes
        new DoListTest( "OWL list classes",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  11,
                        new String[] {NS+"A", NS+"B", NS+"C", NS+"D", NS+"E", NS+"X0", NS+"X1", NS+"Y0", NS+"Y1", NS+"Z", } )
        {
            public Iterator doList( OntModel m ) {
                return m.listClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL list named classes",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  10,
                        new String[] {NS+"A", NS+"B", NS+"C", NS+"D", NS+"E", NS+"X0", NS+"X1", NS+"Y0", NS+"Y1", NS+"Z", } )
        {
            public Iterator doList( OntModel m ) {
                return m.listNamedClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL list intersection classes",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"A" } )
        {
            public Iterator doList( OntModel m ) {
                return m.listIntersectionClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL list union classes",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"B"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listUnionClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL list complement classes",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"C"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listComplementClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL list enumerated classes",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"D"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listEnumeratedClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL list restrictions",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listRestrictions();
            }
            public boolean test( Resource r ) {
                return r instanceof Restriction;
            }
        },
        new DoListTest( "DAML list classes",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  12,
                        new String[] {NS+"A", NS+"B", NS+"C", NS+"D", NS+"E", NS+"X0", NS+"X1", NS+"Y0", NS+"Y1", NS+"Z", DAML_OIL.Thing.getURI()} )
        {
            public Iterator doList( OntModel m ) {
                return m.listClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "DAML list named classes",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  11,
                        new String[] {NS+"A", NS+"B", NS+"C", NS+"D", NS+"E", NS+"X0", NS+"X1", NS+"Y0", NS+"Y1", NS+"Z", DAML_OIL.Thing.getURI()} )
        {
            public Iterator doList( OntModel m ) {
                return m.listNamedClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "DAML list intersection classes",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"A" } )
        {
            public Iterator doList( OntModel m ) {
                return m.listIntersectionClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "DAML list union classes",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"B"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listUnionClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "DAML list complement classes",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"C"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listComplementClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "DAML list enumerated classes",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        new String[] {NS+"D"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listEnumeratedClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "DAML list restrictions",  "file:testing/ontology/daml/list-syntax/test.rdf",  OntModelSpec.DAML_MEM_RULE_INF,  1,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listRestrictions();
            }
            public boolean test( Resource r ) {
                return r instanceof Restriction;
            }
        },

        // Annotation property
        new DoListTest( "OWL list annotation properties",  "file:testing/ontology/owl/list-syntax/test.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  6,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listAnnotationProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof AnnotationProperty;
            }
        },

        // !!!!!!! Following tests use ontology that imports owl.owl !!!!!!!!!!!

        // ontologies
        new DoListTest( "OWL+import list ontologies",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  2,
                        new String[] {"http://jena.hpl.hp.com/testing/ontology", "http://www.w3.org/2002/07/owl"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listOntologies();
            }
            public boolean test( Resource r ) {
                return r instanceof Ontology;
            }
        },
        // Properties
        new DoListTest( "OWL+import list properties",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  40,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listOntProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL+import list object properties",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  2,
                        new String[] {NS+"op", NS+"op1"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listObjectProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },
        new DoListTest( "OWL+import list datatype properties",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"dp"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listDatatypeProperties();
            }
            public boolean test( Resource r ) {
                return r instanceof OntProperty &&
                       r instanceof Property;
            }
        },

        // individuals
        new DoListTest( "OWL+import list individuals",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  8,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listIndividuals();
            }
            public boolean test( Resource r ) {
                return r instanceof Individual;
            }
        },

        new DoListTest( "OWL+import list all different",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listAllDifferent();
            }
            public boolean test( Resource r ) {
                return r instanceof AllDifferent;
            }
        },

        // classes
        new DoListTest( "OWL+import list classes",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  14,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL+import list named classes",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  12,
                        new String[] {NS+"A", NS+"B", NS+"C", NS+"D", NS+"E", NS+"X0", NS+"X1", NS+"Y0", NS+"Y1", NS+"Z",
                                      OWL.Thing.getURI(), OWL.Nothing.getURI()} )
        {
            public Iterator doList( OntModel m ) {
                return m.listNamedClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL+import list intersection classes",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"A" } )
        {
            public Iterator doList( OntModel m ) {
                return m.listIntersectionClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL+import list union classes",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  2,
                        new String[] {NS+"B", OWL.Thing.getURI()} )
        {
            public Iterator doList( OntModel m ) {
                return m.listUnionClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL+import list complement classes",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  3,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listComplementClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL+import list enumerated classes",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        new String[] {NS+"D"} )
        {
            public Iterator doList( OntModel m ) {
                return m.listEnumeratedClasses();
            }
            public boolean test( Resource r ) {
                return r instanceof OntClass;
            }
        },
        new DoListTest( "OWL+import list restrictions",  "file:testing/ontology/owl/list-syntax/test-with-import.rdf",  OntModelSpec.OWL_MEM_TRANS_INF,  1,
                        null )
        {
            public Iterator doList( OntModel m ) {
                return m.listRestrictions();
            }
            public boolean test( Resource r ) {
                return r instanceof Restriction;
            }
        },
    };


    // Instance variables
    //////////////////////////////////

    // Constructors
    //////////////////////////////////

    public TestListSyntaxCategories( String name ) {
        super( name );
    }



    // External signature methods
    //////////////////////////////////

    public static TestSuite suite() {
        TestSuite s = new TestSuite( "TestListSyntaxCategories" );

        for (int i = 0;  i < testCases.length;  i++) {
            s.addTest( testCases[i] );
        }

        return s;
    }


    // Internal implementation methods
    //////////////////////////////////

    //==============================================================================
    // Inner class definitions
    //==============================================================================

    protected static class DoListTest
        extends TestCase
    {
        protected String m_fileName;
        protected OntModelSpec m_spec;
        protected int m_count;
        protected String[] m_expected;
        protected boolean m_exExpected;     // exception expected during list operation

        protected DoListTest( String name, String fileName, OntModelSpec spec, int count, String[] expected ) {
            this( name, fileName, spec, count, expected, false );
        }

        protected DoListTest( String name, String fileName, OntModelSpec spec, int count, String[] expected, boolean exExpected ) {
            super( name );
            m_fileName = fileName;
            m_spec = spec;
            m_count = count;
            m_expected = expected;
            m_exExpected = exExpected;
        }

        public void setUp() {
            // ensure the ont doc manager is in a consistent state
            OntDocumentManager.getInstance().reset( true );
        }


        public void runTest() {
            OntModel m = ModelFactory.createOntologyModel( m_spec, null );
            m.getDocumentManager().setMetadataSearchPath( "file:etc/ont-policy-test.rdf", true );

            if (m_fileName != null) {
                m.read( m_fileName );
            }

            boolean exOccurred = false;
            Iterator i = null;
            try {
                i = doList( m );
            }
            catch (OntologyException e) {
                exOccurred = true;
            }

            assertEquals( "Ontology exception" + (m_exExpected ? " was " : " was not ") + "expected", m_exExpected, exOccurred );

            if (!exOccurred) {
                List expected = expected( m );
                List actual = new ArrayList();
                int extraneous = 0;

                // now we walk the iterator
                while (i.hasNext()) {
                    Resource res = (Resource) i.next();
                    assertTrue( "Should not fail node test on " + res, test( res ));

                    actual.add( res );
                    if (expected != null) {
                        if (expected.contains( res )) {
                            expected.remove( res );
                        }
                        else {
                            if (!res.isAnon()) {
                                // since we can't list expected anon resources, we ignore them in this check
                                extraneous++;
                            }
                        }
                    }
                }

                // debugging
                if (m_count != actual.size()) {
                    Log logger = LogFactory.getLog( getClass() );
                    logger.debug( getName() + " - expected " + m_count + " results, actual = " + actual.size() );
                    for (Iterator j = actual.iterator(); j.hasNext(); ) {
                        logger.debug( getName() + " - saw actual: " + j.next() );
                    }
                }
                if (expected != null && !expected.isEmpty()) {
                    Log logger = LogFactory.getLog( getClass() );
                    for (Iterator j = expected.iterator(); j.hasNext(); ) {
                        logger.debug( getName() + " - expected but did not find: " + j.next() );
                    }
                }

                assertEquals( getName() + ": wrong number of results returned", m_count, actual.size() );
                if (expected != null) {
                    assertTrue( "Did not find all expected resources in iterator", expected.isEmpty() );
                    assertEquals( "Found extraneous results, not in expected list", 0, extraneous );
                }
            }
        }

        /* get the iterator */
        public Iterator doList( OntModel m ) {
            // should be overriden in sub-classes
            return null;
        }

        /* test the Java type of the result, and other tests */
        public boolean test( Resource r ) {
            return true;
        }

        protected List expected( OntModel m ) {
            if (m_expected != null) {
                List expected = new ArrayList();

                for (int i = 0;  i < m_expected.length; i++) {
                    expected.add( m.getResource( m_expected[i] ) );
                }

                return expected;
            }
            else {
                return null;
            }
        }
    }
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
