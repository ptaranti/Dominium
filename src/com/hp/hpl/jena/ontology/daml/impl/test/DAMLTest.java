/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena
 * Created            10 Nov 2000
 * Filename           $RCSfile: DAMLTest.java,v $
 * Revision           $Revision: 1.30 $
 * Release status     Preview-release $State: Exp $
 *
 * Last modified on   $Date: 2007/01/02 11:52:00 $
 *               by   $Author: andy_seaborne $
 *
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.ontology.daml.impl.test;


// Imports
///////////////
import junit.framework.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.daml.*;
import com.hp.hpl.jena.vocabulary.*;

import java.util.*;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Legacy JUnit regression tests for the Jena DAML model.
 *
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com">email</a>)
 * @version CVS info: $Id: DAMLTest.java,v 1.30 2007/01/02 11:52:00 andy_seaborne Exp $,
 */
public class DAMLTest
    extends TestCase
{
    // Constants
    //////////////////////////////////


    // Static variables
    //////////////////////////////////

    private static Log log = LogFactory.getLog( DAMLTest.class );


    // Instance variables
    //////////////////////////////////

    // Constructors
    //////////////////////////////////

    /**
     * Constructor requires that all tests be named
     *
     * @param name The name of this test
     */
    public DAMLTest( String name ) {
        super( name );
    }



    // External signature methods
    //////////////////////////////////



    // Internal implementation methods
    //////////////////////////////////

    /**
     * Set up the test conditions
     */
    public void setUp() {
        // reset default condition
        OntDocumentManager.getInstance().reset( true );
    }


    /**
     * Release objects no longer needede when we're done
     */
    public void tearDown() {
    }


    // Test cases
    /////////////

    /**
     * Test the various pathways through loading the ontology from a source document.
     */
    public void testLoadOntology()

    {
        DAMLModel m = getCleanModel();

        // first do the model read with all options turned on
        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "Load success status should be true", m.getLoadSuccessful() );
        assertEquals( "Count of number of classes in daml store (2001/03, import)", 31, countClasses( m ) );
        // Replaced by der after reasoner update, uncertain whether the "new" results are correct yet!
//        assertEquals( "Property count ", 66, countProperties( m ) );
        assertEquals( "Property count ", 68, countProperties( m ) );
        //dumpModel( m );

        // now turn off importing - should only get the classes and properties in the source doc
        m = getCleanModel();
        m.getLoader().setLoadImportedOntologies( false );
        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "Load success status should be true", m.getLoadSuccessful() );
        assertEquals( "Count of number of classes in daml store (2001/03, no import)", 16, countClasses( m ) );

        // test case for bug reported by Charlie Abela: must be able to load instance files that import
        // their own class declarations
        m = getCleanModel();
        m.read( "file:testing/ontology/daml/test-instance-load.daml" );
        assertTrue( "Load status should be true", m.getLoadSuccessful() );
        Resource pugh = m.getResource( "http://dickinson-i-4/daml/tests/test-instance-load.daml#pugh" );
        assertNotNull( "Resource for officer Pugh should not be null", pugh );
        DAMLInstance pughInst = (DAMLInstance) pugh.as( DAMLInstance.class );
        assertNotNull( pughInst );

        // test case for bug report by Michael Sintek
        // try to ascertain the most specific class we can at load time -
        // case in point is shoesize in standard example ontology
        m = getCleanModel();
        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "Load success status should be true", m.getLoadSuccessful() );
        DAMLProperty shoesize = (DAMLProperty) m.getProperty( "http://www.daml.org/2001/03/daml+oil-ex#shoesize" ).as( DAMLProperty.class );
        assertNotNull( "Failed to find shoesize property in example ontology", shoesize );
        assertEquals( "shoesize should be a unique property", true, shoesize.isUnique() );
    }


    /**
     * Test case for testing rdf:type traversal
     */
    public void testRDFType()

    {
        String ns = "http://dickinson-i-4/daml/tests/test-cases.daml#";

        DAMLModel m = getCleanModel();
        m.read( "file:testing/ontology/daml/test-cases.daml" );

        //dumpModel( m );

        // first find fido
        DAMLInstance fido = m.getDAMLInstance( ns + "fido" );
        assertNotNull( "fido instance should not be null", fido );

        // lookup some classes for convenience
        DAMLClass cDog = m.getDAMLClass( ns + "Dog" );
        assertNotNull( "Dog class should not be null", cDog );

        DAMLClass cVertebrate = m.getDAMLClass( ns + "Vertebrate" );
        assertNotNull( "Vertebrate class should not be null", cVertebrate );

        DAMLClass cPet = m.getDAMLClass( ns + "Pet" );
        assertNotNull( "Pet class should not be null", cPet );

        // fido is a Dog, a vertebrate and a pet
        assertTrue( "fido should be member of class Dog", fido.hasRDFType( cDog ) );
        assertTrue( "fido should be member of class Vertebrate", fido.hasRDFType( cVertebrate ) );
        assertTrue( "fido should be member of class Vertebrate (by URL)", fido.hasRDFType( ns + "Vertebrate" ) );
        assertTrue( "fido should be member of class Pet", fido.hasRDFType( cPet ) );

        // fido is not a class
        assertTrue( "fido should not be a class", !fido.hasRDFType( DAML_OIL.Class ) );

        // fido is a companion, even though this class is not defined in the current ontology
        assertTrue( "fido should be a companion", fido.hasRDFType( ns + "Companion" ) );

        // fido is a Thing (all things DAML are Things)
        // disabled pending adding DAML semantic rules assertTrue( "fido should be a thing", fido.hasRDFType( DAML_OIL.Thing ) );

        // get some more classes
        DAMLClass cA = m.getDAMLClass( ns + "A" );
        assertNotNull( "Class A should not be null", cA );

        DAMLClass cB = m.getDAMLClass( ns + "B" );
        assertNotNull( "Class B should not be null", cB );

        DAMLInstance ab = m.getDAMLInstance( ns + "ab" );
        assertNotNull( "Instance ab should not be null", ab );

        // note that cA --subclass--> cB --subclass--> cA   is a loop
        assertTrue( "ab should be an A", ab.hasRDFType( cA ) );
        assertTrue( "ab should be a B", ab.hasRDFType( cB ) );

        // how many ways do I know thee? let me count the ways ...
        assertEquals( "Number of classes fido belongs to (closure) should be 8",
                      8, countIteration( fido.getRDFTypes( true ), true, "fido member of class " ) );
        assertEquals( "Number of classes fido belongs to (non-closure) should be 3",
                      3, countIteration( fido.getRDFTypes( false ), true, "fido member of non-closed class " ) );

        // some tests on the built-in classes
        DAMLProperty queenOf = m.getDAMLProperty( ns + "queen-of" );
        assertNotNull( "queen-of property should be defined", queenOf );
        assertTrue( "an UnabmbiguousProperty should be an ObjectProperty", queenOf.hasRDFType( DAML_OIL.UnambiguousProperty ) );
        assertTrue( "an UnabmbiguousProperty should be an ObjectProperty", queenOf.hasRDFType( DAML_OIL.ObjectProperty ) );
        assertTrue( "an UnabmbiguousProperty should be an rdf:Property",   queenOf.hasRDFType( RDF.Property ) );
    }


    /**
     * Test some of the properties of DAML classes
     */
    public void testClass() {
        DAMLModel m = getCleanModel();

        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );
        String ns = "http://www.daml.org/2001/03/daml+oil-ex#";

        // get a reference to the Person class
        DAMLClass person = m.getDAMLClass( ns + "Person" );
        assertNotNull( "Person class should not be null", person );
        assertTrue( "Person should be a named class", person.isNamedClass() );

        // count the super-classes of a Person
        int sCount0 = countIteration( person.prop_subClassOf().getAll(  ), true, "super-class of Person (prop_subClassOf) " );
        int sCount1 = countIteration( person.getSuperClasses(), true, "super-class of Person (getSuperClasses) " );
        assertEquals( "person should have 10 super-classes (by prop_subClassOf)", 10, sCount0 );
        assertEquals( "person should have 9 super-classes (by getSuperClasses)", 9, sCount1 );

        // count the number of sub-classes of a Person
        assertEquals( "person should have 3 sub-classes", 3,
                      countIteration( person.getSubClasses(), true, "Person super-class of: " ) );

        // person is a disjoint union of Man and Woman
        assertTrue( "Person should be a disjoint union", person.isDisjointUnion() );
        DAMLList mw = person.prop_disjointUnionOf().getList();
        assertNotNull( "Value of disjoint union should not be null", mw );
        assertEquals( "Person should be a disjoint union of size 2", 2, mw.getCount() );

        // Female is disjoint with Male
        DAMLClass female = m.getDAMLClass( ns + "Female" );
        assertNotNull( "Class Female should not be null", female );
        DAMLClass male = m.getDAMLClass( ns + "Male" );
        assertNotNull( "Class Male should not be null", male );
        assertTrue( "Female should be disjoint with male", female.prop_disjointWith().hasValue( male ) );

        // HumanBeing is the same class as Person
        DAMLClass humanBeing = m.getDAMLClass( ns + "HumanBeing" );
        assertNotNull( "Class humanBeing should not be null", humanBeing );
        assertTrue( "Person should be same class as HumanBeing", humanBeing.prop_sameClassAs().hasValue( person ) );

        // TallMan is an intersection of Man and TallThing
        DAMLClass tallMan = m.getDAMLClass( ns + "TallMan" );
        assertNotNull( "Class TallMan should not be null", tallMan );
        DAMLList tm = tallMan.prop_intersectionOf().getList();
        assertNotNull( "Value of intersection should not be null", tm );
        assertEquals( "Tall man should be an intersection of size 2", 2, tm.getCount() );

        // Car is a complement of Person
        DAMLClass car = m.getDAMLClass( ns + "Car" );
        assertNotNull( "Class Car should not be null", car );
        DAMLClass carSuper = (DAMLClass) car.getSuperClasses(false).next();
        assertNotNull( "Car should have a super-class", carSuper );
        assertTrue( "Car super-class should be a complement", carSuper.isComplement() );
        assertTrue( "Car super-class should be a complement of Person", carSuper.prop_complementOf().hasValue( person ) );

        // Height is an enumeration of three values
        DAMLClass height = m.getDAMLClass( ns + "Height" );
        assertNotNull( "Class Height should not be null", height );
        assertTrue( "Height should be an enumeration", height.isEnumeration() );
        assertEquals( "Height should be an enumeration of 3 elements", 3, height.prop_oneOf().getList().getCount() );

        // daml:subClassOf is processed as rdfs:subClassOf
        DAMLModel m0 = getCleanModel();
        m0.getLoader().setLoadImportedOntologies( true );
        m0.read( "file:testing/ontology/daml/test-cases.daml", "http://dickinson-i-4/daml/tests/test-cases.daml", null );
        String tcNs = "http://dickinson-i-4/daml/tests/test-cases.daml#";
        DAMLClass subClassBug0 = m0.getDAMLClass( tcNs + "SubClassBug0" );
        DAMLClass subClassBug1 = m0.getDAMLClass( tcNs + "SubClassBug1" );
        assertNotNull( "Class SubClassBug0 should not be null", subClassBug0 );
        assertNotNull( "Class SubClassBug1 should not be null", subClassBug1 );
        assertTrue( "SubClassBug1 should have SubClassBug0 as a super-class", subClassBug1.hasSuperClass( subClassBug0 ) );
        assertTrue( "SubClassBug0 should have SubClassBug1 as a sub-class", subClassBug0.hasSubClass( subClassBug1 ) );

        // defined properties are those that mention this class as domain
        DAMLClass defProp0 = m0.getDAMLClass( tcNs + "DefProp0" );
        DAMLClass defProp1 = m0.getDAMLClass( tcNs + "DefProp1" );
        DAMLClass defProp2 = m0.getDAMLClass( tcNs + "DefProp2" );
        assertNotNull( "Class DefProp0 should not be null", defProp0 );
        assertNotNull( "Class DefProp1 should not be null", defProp1 );
        assertNotNull( "Class DefProp2 should not be null", defProp2 );

        int nP0 = countIteration( defProp0.getDefinedProperties(), true, "Defined property of DefProp0, closed" );
        int nP0nc = countIteration( defProp0.getDefinedProperties( false ), true, "Defined property of DefProp0, not closed" );
        int nP1 = countIteration( defProp1.getDefinedProperties(), true, "Defined property of DefProp1, closed" );
        int nP1nc = countIteration( defProp1.getDefinedProperties( false ), true, "Defined property of DefProp1, not closed" );
        int nP2 = countIteration( defProp2.getDefinedProperties(), true, "Defined property of DefProp2, closed" );
        int nP2nc = countIteration( defProp2.getDefinedProperties( false ), true, "Defined property of DefProp2, not closed" );
        assertEquals( "Defined properties of DefProp0 should number 11", 11, nP0 );
        assertEquals( "Defined properties of DefProp0 (non-closed) should number 11", 11, nP0nc );
        assertEquals( "Defined properties of DefProp1 should number 11", 11, nP1 );
        assertEquals( "Defined properties of DefProp1 (non-closed) should number 0", 0, nP1nc );

        assertEquals( "Defined properties of DefProp2 should number 12", 12, nP2 );
        assertEquals( "Defined properties of DefProp2 (non-closed) should number 1", 1, nP2nc );
        assertEquals( "Defined properties of DefProp2 should number 12", 12, nP2 );
        assertEquals( "Defined properties of DefProp2 (non-closed) should number 1", 1, nP2nc );

        // Bug report by Thorsten Liebig
        DAMLClass tl_one = m0.getDAMLClass( tcNs + "tl_one" );
        assertNotNull( "Class tl_one should not be null", tl_one );
        int tl_one_supers0 = countIteration( tl_one.prop_subClassOf().getAll(  ), true, "prop_subClassOf " );
        int tl_one_supers1 = countIteration( tl_one.getSuperClasses( false ), true, "getSuperClasses ");
        assertEquals( "Should be four super-classes of tl_one by prop_subClassOf", 4, tl_one_supers0 );
        assertEquals( "Should be two super-classes of tl_one by getSuperClasses", 2, tl_one_supers1 );

        // Bug report by Andrei S. Lopatenko
        DAMLClass researcher = m0.getDAMLClass( tcNs + "Researcher" );
        assertNotNull( "Class Researcher should not be null", researcher );
        int researcherSupers = countIteration( researcher.getSuperClasses( false ), true, "Super-class of researcher" );
        assertEquals( "Should be 2 super-classes of researcher", 2, researcherSupers );
    }


    /**
     * Test equivalance classes
     */
    public void testEquivalence() {
        String ns = "http://dickinson-i-4/daml/tests/test-cases.daml#";

        DAMLModel m = getCleanModel();

        // don't allow any additional info to load
        m.read( "file:testing/ontology/daml/test-cases.daml", "http://dickinson-i-4/daml/tests/test-cases.daml", null );

        // get the root object
        DAMLInstance root = m.getDAMLInstance( ns + "x0" );
        assertNotNull( "Instance x0 should not be null", root );
        // should really be 4 under daml rules, but only 1 under rdfs with no support for daml entailments
        assertEquals( "Number of elements in equivalence class should be 1", 1,
                      countIteration( root.getEquivalentValues(), true, "Member of equivalence class to x0: " ) );

        // now it's the classes' turn ...
        DAMLClass cRoot = m.getDAMLClass( ns + "C0" );
        assertNotNull( "Class C0 should not be null", cRoot );
        // should be 4 under daml semantic rules, but only 1 under rdfs
        assertEquals( "Number of elements in equivalence class should be 1", 1,
                      countIteration( cRoot.getSameClasses(), true, "sameClass as C0: " ) );

        // and now the properties ...
        DAMLProperty pRoot = m.getDAMLProperty( ns + "p0" );
        assertNotNull( "Property p0 should not be null", pRoot );
        // should be 4 under daml semantics rules, but only 1 under rdfs
        assertEquals( "Number of elements in equivalence class should be 1", 1,
                      countIteration( pRoot.getSameProperties(), true, "sameProperty as p0: " ) );

        // check that daml:type is recognised as equivalent to rdf:type
        Resource dClass = m.getResource( ns + "CDaml" );
        assertNotNull( "Resource dClass should not be null", dClass );
        assertTrue( "Resource dClass should be a daml class", dClass.canAs( DAMLClass.class ) );

        // check that class equivalence is considered during type testing
        DAMLClass cD0 = m.getDAMLClass( ns + "D0" );
        DAMLClass cD1 = m.getDAMLClass( ns + "D1" );
        DAMLInstance d1 = m.getDAMLInstance( ns + "d1" );
        assertNotNull( "Class D0 should not be null", cD0 );
        assertNotNull( "Class D1 should not be null", cD1 );
        assertNotNull( "Instance d1 should not be null", d1 );

        assertTrue( "Instance d1 should have class D1", d1.hasRDFType( cD1 ) );
        // RDFS reasoner can't do class equivalence assertTrue( "Instance d1 should have class D0", d1.hasRDFType( cD0 ) );

        // check equivalence on properties, pd0 and pd1 are the same
        DAMLProperty pd0 = m.getDAMLProperty( ns + "pd0" );
        DAMLProperty pd1 = m.getDAMLProperty( ns + "pd1" );
        assertNotNull( "Property pd0 should not be null", pd0 );
        assertNotNull( "Property pd1 should not be null", pd1 );

        DAMLInstance d2 = m.getDAMLInstance( ns + "d2" );
        assertNotNull( "Instance d2 should not be null", d2 );

        // check that d2 has d1 as a property value under pd0, and pd1 (it's equivalent)
        assertTrue( "d2 should have d1 as a value for pd0", d2.accessProperty( pd0 ).hasValue( d1 ) );
        // RDFS reasoner can't do property equivalence assertTrue( "d2 should have d1 as a value for pd1", d2.accessProperty( pd1 ).hasValue( d1 ) );

        DAMLProperty pd2 = m.getDAMLProperty( ns + "pd2" );
        DAMLProperty pd3 = m.getDAMLProperty( ns + "pd3" );
        assertNotNull( "Property pd2 should not be null", pd2 );
        assertNotNull( "Property pd3 should not be null", pd3 );

        // we know that 'd1 pd3 d2', which implies 'd1 pd2 d2' since pd2 is a super-prop of pd3
        assertTrue( "d2 should have d1 as a value for pd3", d2.accessProperty( pd3 ).hasValue( d1 ) );
        assertTrue( "d2 should have d1 as a value for pd2", d2.accessProperty( pd2 ).hasValue( d1 ) );
    }


    /**
     * Unit tests on DAMLProperty and its subclasses
     */
    public void testProperty() {
        DAMLModel m = getCleanModel();

        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );
        String ns = "http://www.daml.org/2001/03/daml+oil-ex#";

        // Tests on property objects themselves
        DAMLProperty hasMother = m.getDAMLProperty( ns + "hasMother" );
        assertNotNull( "hasMother property should not be null", hasMother );
        assertTrue( "hasMother property should be a unique property", hasMother.isUnique() );

        DAMLProperty hasParent = m.getDAMLProperty( ns + "hasParent" );
        assertNotNull( "hasParent property should not be null", hasParent );

        DAMLClass female = m.getDAMLClass( ns + "Female" );
        assertNotNull( "Class Female should not be null", female );
        DAMLClass animal = m.getDAMLClass( ns + "Animal" );
        assertNotNull( "Class Animal should not be null", animal );
        DAMLClass person = m.getDAMLClass( ns + "Person" );
        assertNotNull( "Class Person should not be null", person );

        // range of hasMother includes female
        assertTrue( "Mother should have Female as range", hasMother.prop_range().hasValue( female ) );
        assertTrue( "Mother should not have Animal as local domain (prop_domain)", !hasMother.prop_domain().hasValue( animal ) );

        /* test removed - depends on reasoner configuration
        boolean found = false;
        for (Iterator i = hasMother.getDomainClasses();  !found && i.hasNext(); ) {
            Object cls = i.next();
            found = found || cls.equals( animal );
        }
        assertTrue( "Mother should have Animal as domain (getDomainClasses)", found );
        */

        // ancestor is transitive
        DAMLObjectProperty hasAncestor = (DAMLObjectProperty) m.getDAMLProperty( ns + "hasAncestor" ).as( DAMLObjectProperty.class );
        assertNotNull( "hasAncestor should not be null", hasAncestor );
        assertTrue( "hasAncestor should be transitive", hasAncestor.isTransitive() );

        // bug report by Michael Sintek: getNext() does not terminate on getAll(false)
        DAMLInstance peter = m.getDAMLInstance( ns + "Peter" );
        assertNotNull( "Instance Peter should not be null", peter );
        DAMLProperty shoesize = m.getDAMLProperty( ns + "shoesize" );
        assertNotNull( "Property shoesize should not be null", shoesize );
        PropertyAccessor paShoesize = peter.accessProperty( shoesize );
        Iterator iShoes = paShoesize.getAll(  );
        assertEquals( "iShoes iterator should have at least one value", true, iShoes.hasNext() );
        Object size = iShoes.next();
        assertNotNull( "size returned from property accessor iterator should not be null", size );
        assertTrue( "size object should be a literal", size instanceof Literal );
        boolean nse = false;
        try {
            iShoes.next();
        }
        catch (NoSuchElementException ignore) {
            nse = true;
        }
        assertEquals( "Accessing past end of property iterator should throw no such element exception", true, nse );

        // try the same bug test with a multi-valued property
        Iterator iSubClassOf = person.prop_subClassOf().getAll(  );
        assertNotNull( "Iterator over subClassOf values should not be null", iSubClassOf );
        assertTrue( "Iteration of subClassOf should have at least one value", iSubClassOf.hasNext() );
        int nSupers = countIteration( iSubClassOf, true, "direct super-class of Person = " );
        assertEquals( "Should be 10 super-classes of Person", 10, nSupers );

        // another bug report from Michael Sintek - get() on single-valued property does not terminate
        DAMLClass male = m.getDAMLClass( ns + "Male" );
        assertNotNull( "Class Male should not be null", male );
        DAMLCommon femaleDisjoint = (DAMLCommon) female.prop_disjointWith().get();
        assertNotNull( "Value for female.disjointWith should not be null", femaleDisjoint );
        assertTrue( "female.disjointWith should be male", male.equals( femaleDisjoint ) );

        // another property accessor check
        m.getLoader().setLoadImportedOntologies( false );
        m.read( "file:testing/ontology/daml/test-cases.daml", "http://dickinson-i-4/daml/tests/test-cases.daml", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );
        ns = "http://dickinson-i-4/daml/tests/test-cases.daml#";

        DAMLClass subClassCheck3 = (DAMLClass) m.getDAMLValue( ns + "subClassCheck3" ).as( DAMLClass.class );
        assertNotNull( "Class subClassCheck3 should not be null", subClassCheck3 );

        Iterator iSubClassOf3 = subClassCheck3.getSuperClasses( false );    // not closed
        assertNotNull( "Iterator over subClassOf values should not be null", iSubClassOf );
        assertTrue( "Iteration of subClassOf should have at least one value", iSubClassOf3.hasNext() );
        nSupers = countIteration( iSubClassOf3, true, "property access on subClassCheck3" );
        assertEquals( "Should be 1 non-closed super-classes of subClassCheck3", 1, nSupers );

        iSubClassOf3 = subClassCheck3.getSuperClasses( true );  // closed
        assertNotNull( "Iterator over subClassOf values should not be null", iSubClassOf );
        assertTrue( "Iteration of subClassOf should have at least one value", iSubClassOf3.hasNext() );
        nSupers = countIteration( iSubClassOf3, true, "property access on subClassCheck3 with closed = true " );
        assertEquals( "Should be 3 closed super-classes of subClassCheck3", 3, nSupers );

        // bug submitted by Michael Sintek: setUseEquivalence(false) does not work with property accessors
        DAMLProperty q = m.getDAMLProperty( ns + "q" );
        assertNotNull( "Property q should not be null", q );
        DAMLInstance qX = m.getDAMLInstance( ns + "qX" );
        assertNotNull( "Instance qX should not be null", qX );

        // with equivalence turned on, there should be four values for q of qX
        int nQ = qX.accessProperty( q ).count();
        // daml equivalance not currently implemented assertEquals( "There should be 4 values for q of qX (equivalence on)", 4, nQ );

        // turn off equivalence, should only be one value
        assertEquals( "There should be 1 values for q of qX (equivalence off)", 1, nQ );

        // Bug report by Thorsten Liebig
        DAMLProperty tlPropTest = (DAMLObjectProperty) m.getDAMLProperty( ns + "TL_PropertyTest" ).as( DAMLObjectProperty.class );
        assertNotNull( "Property should not be null", tlPropTest );

        Iterator tl_domains = tlPropTest.prop_domain().getAll();
        assertEquals( "Property TL_PropertyTest should have a domain of two classes", 2, countIteration( tl_domains, false, null ) );
        Iterator tl_ranges = tlPropTest.prop_range().getAll();
        assertEquals( "Property TL_PropertyTest should have a range of two classes", 2, countIteration( tl_ranges, false, null ) );

        // bug reported by Wesley Bille
        DAMLClass humanBody = (DAMLClass) m.getDAMLValue( ns + "HumanBody" ).as( DAMLClass.class );
        assertNotNull( "Class humanBody should not be null", humanBody );
        PropertyAccessor propUnion = humanBody.prop_unionOf();
        assertNotNull( "Property accessor should not be null", propUnion );
        assertEquals( "Should be two value in union", 1, propUnion.count() );
        DAMLCommon union = propUnion.getDAMLValue();
        assertNotNull( "Union should not be null", union );
        assertTrue( "Union value should be a list", union.canAs( DAMLList.class ) );
        assertEquals( "Should be two values in list", 2, ((DAMLList) union.as( DAMLList.class )).getCount() );
    }


    /**
     * Tests on lists
     */
    public void testList()    {
        DAMLModel m = getCleanModel();

        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );
        String ns = "http://www.daml.org/2001/03/daml+oil-ex#";

        // get the Person class
        DAMLClass person = m.getDAMLClass( ns + "Person" );
        assertNotNull( "Person class should not be null", person );
        DAMLClass man = m.getDAMLClass( ns + "Man" );
        assertNotNull( "Man class should not be null", man );
        DAMLClass woman = m.getDAMLClass( ns + "Woman" );
        assertNotNull( "Woman class should not be null", woman );

        // check some basic characteristics of the list
        DAMLList union = (DAMLList) person.prop_disjointUnionOf().get().as( DAMLList.class );
        assertNotNull( "union should not be null", union );
        assertEquals( "union should have two values", 2, union.getCount() );

        // man should be the first element in the list
        RDFNode val1 = union.getFirst();
        assertEquals( "Man should be the first element in the list", man, val1 );

        // woman should be the other element in the list
        DAMLList tail = union.getRest();
        assertNotNull( "Tail of list should not be null", tail );

        RDFNode val2 = tail.getFirst();
        assertNotNull( "head of tail should not be null", val2 );
        assertEquals( "Woman should be the first element in the tail of the list", woman, val2 );

        DAMLList tail2 = tail.getRest();
        assertNotNull( "Tail of tail should not be null", tail2 );
        assertTrue( "Remainder of list should be empty", tail2.isEmpty() );

        // ontologically nonsensical ... just want to test list manipulations
        DAMLClass car = m.getDAMLClass( ns + "Car" );
        assertNotNull( "Class Car should not be null", car );
        union.add( car );
        // DEBUG dumpModel( m.getModel() );
        assertEquals( "Union should contain three elements", 3, union.getCount() );
    }


    /**
     * Tests on instances
     */
    public void testInstance() {
        DAMLModel m = getCleanModel();

        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );
        String ns = "http://www.daml.org/2001/03/daml+oil-ex#";

        // count the number of instances loaded from the standard example
        assertEquals( "Number of instances should be 7", 7,
                      countIteration( m.listDAMLInstances(), false, " instance = " ) );

        // test listing the instances of a class
        DAMLClass person = m.getDAMLClass( ns + "Person" );
        assertNotNull( "Person DAML class should not be null", person );
        int nPerson = countIteration( person.getInstances(), true, "instance of person" );
        assertEquals( "There should be 4 instances of Person in the model", 4, nPerson );
    }


    /**
     * Tests on DAML datatypes
     */
    public void testDatatype() {
        DAMLModel m = getCleanModel();

        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );
        String ns = "http://www.daml.org/2001/03/daml+oil-ex#";

        // get the Person class
        DAMLInstance ian = m.getDAMLInstance( ns + "Ian" );
        assertNotNull( "Instance Ian should not be null", ian );
        DAMLProperty shirtsize = m.getDAMLProperty( ns + "shirtsize" );
        assertNotNull( "Property shirtsize should not be null", shirtsize );
        DAMLProperty shoesize = m.getDAMLProperty( ns + "shoesize" );
        assertNotNull( "Property shoesize should not be null", shoesize );

        DAMLDataInstance sSize = (DAMLDataInstance) ian.getRequiredProperty( shirtsize ).getObject().as( DAMLDataInstance.class );
        assertNotNull( "Object ian should have a shirtsize", sSize );
        Object x = sSize.getValue();
        assertNotNull( "Value of shirtsize should not be null", x );
        assertEquals( "Shirt size should be a string", String.class, x.getClass() );
        assertEquals( "Shirt size should be \"12\"", "12", x );
    }


    public void testDataInstance() {
        DAMLModel m = getCleanModel();
        DAMLDataInstance di = m.createDAMLDataInstance( new Integer( 9 ) );
        assertNotNull( "Failed to create data instance ", di );
        assertEquals( "data instance URI not correct ", TypeMapper.getInstance().getTypeByName( XSD.xint.getURI() ), di.getDatatype() );

        String NS="http://example.org/eg#";
        DAMLClass person = m.createDAMLClass( NS+"Person");
        DAMLInstance peter = m.createDAMLInstance( person, NS+"peter");
        DAMLDatatypeProperty p = m.createDAMLDatatypeProperty( NS+"shirtsize");
        peter.addProperty( p, di );

        // just to confirm we are getting reasonable output m.write( System.out, "RDF/XML-ABBREV" );
    }


    /**
     * Test the removal of DAML objects. We'll load a model, then
     * delete everything in it one step at a time.
     */
    public void testRemove() {
        DAMLModel m = ModelFactory.createDAMLModel();
        m.getDocumentManager().setProcessImports( false );
        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );

        List cache = new ArrayList();
        checkValidLists( m, "baseline" );

        // remove classes, properties and instances
        for (Iterator i = m.listDAMLClasses();  i.hasNext(); ) {
            cache.add( i.next() );
        }
        while (!cache.isEmpty()) {
            DAMLClass c = (DAMLClass) cache.remove( 0 );
            log.debug( "Removing class " + c );
            c.remove();
            checkValidLists( m, "remove class" );
        }

        for (Iterator i = m.listDAMLInstances();  i.hasNext(); ) {
            cache.add( i.next() );
        }
        while (!cache.isEmpty()) {
            DAMLInstance c = (DAMLInstance) cache.remove( 0 );
            log.debug( "Removing instance " + c );
            c.remove();
            checkValidLists( m, "remove instance" );
        }

        for (Iterator i = m.listDAMLProperties();  i.hasNext(); ) {
            cache.add( i.next() );
        }
        while (!cache.isEmpty()) {
            DAMLProperty c = (DAMLProperty) cache.remove( 0 );
            log.debug( "Removing property " + c );
            c.remove();
            checkValidLists( m, "remove property" );
        }

        boolean notBuiltin = false;
        for (Iterator i = m.listDAMLClasses(); i.hasNext();  ) {
            Resource x = (Resource) i.next();
            if (x.equals( DAML_OIL.Thing ) ||
                x.equals( DAML_OIL.Nothing ) ||
                x.hasProperty( DAML_OIL.complementOf, DAML_OIL.Nothing )) {
                // is a builtin
            }
            else {
                log.debug( "Unexpected class remains: " + x );
                for (StmtIterator j = ((Resource) x).listProperties(); j.hasNext(); ) {
                    log.debug( " ... has prop " + j.next() );
                }
                notBuiltin = true;
            }
        }
        assertFalse( "Should be no more classes", notBuiltin );
        assertFalse( "Should be no more properties", m.listDAMLProperties().hasNext() );
        assertFalse( "Should be no more instances", m.listDAMLInstances().hasNext() );
    }


    /**
     * Test the creation of new DAML values
     */
    public void testCreate()

    {
        DAMLModel m = getCleanModel();

        String cURI = "http://dickinson-i-4/daml/tests/gen#A";
        DAMLClass c = m.createDAMLClass( cURI );
        assertNotNull( "Failed to create new DAML Class " + cURI, c );

        // check that we can find this class again
        boolean found = false;
        for (Iterator i = m.listDAMLClasses();  i.hasNext();   ) {
            if (((DAMLClass) i.next()).equals( c )) {
                found = true;
            }
        }
        assertTrue( "Could not see class after it was created", found );

        // now create a new instance
        DAMLInstance x = (DAMLInstance) m.createDAMLValue( "http://dickinson-i-4/daml/tests/gen#x", c );
        assertNotNull( "Failed to create new DAML instance", x );

        found = false;
        for (Iterator i = m.listDAMLInstances();  i.hasNext();   ) {
            if (((DAMLInstance) i.next()).equals( x )) {
                found = true;
            }
        }
        assertTrue( "Could not see instance after it was created", found );
    }



    /**
     * Testing restrictions
     */
    public void testRestriction() {
        DAMLModel m = getCleanModel();

        m.read( "file:testing/ontology/daml/daml_oil_2001_03/daml+oil-ex.daml", "http://www.daml.org/2001/03/daml+oil-ex", null );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );
        String ns = "http://www.daml.org/2001/03/daml+oil-ex#";

        // bug report by Michael Sintek: class cast exception from getting the value of the property iterator
        // get a reference to the Person class
        DAMLClass person = m.getDAMLClass( ns + "Person" );

        // now get a restriction, which we can find as one of the super-classes of Person
        for (Iterator i = person.getSuperClasses();  i.hasNext(); ) {
            Resource r = (Resource) i.next();

            if (r instanceof DAMLRestriction) {
                DAMLRestriction restriction = (DAMLRestriction) r;
                PropertyAccessor onPropertyAccessor = restriction.prop_onProperty();

                // now get the value from the restriction
                int count = onPropertyAccessor.count();

                if (count >= 1) {
                    Object x = onPropertyAccessor.get();
                    Object y = onPropertyAccessor.getAll(  ).next();

                    assertNotNull( "Failed to access value of property accessor on restriction", x );
                    assertNotNull( "Failed to access value of property accessor on restriction", y );
                }
            }
        }
   }


    /**
     * Test adding a model to an existing model
     */
    public void testModelAdd() {
        DAMLModel m = getCleanModel();

        // create a daml model
        m.read( "file:testing/ontology/daml/test-add-0.daml" );
        assertTrue( "loadStatus should be true for successful load", m.getLoadSuccessful() );

        // create a normal rdf model
        Model m0 = ModelFactory.createDefaultModel();
        m0.read( "file:testing/ontology/daml/test-add-1.daml" );

        // should be 0 instances in the daml model so far
        assertEquals( "Instance count in DAML model should be 0", 0, countIteration( m.listDAMLInstances(), true, "instance in test add" ) );

        // now add the RDF data
        m.add( m0 );

        // now should be 1 instances in the daml model
        assertEquals( "Instance count in DAML model should be 1", 1, countIteration( m.listDAMLInstances(), true, "instance in test add" ) );
    }


    /**
     * Testing equality: case DatatypeProperty
     */
    public void testDatatypeProperty()

    {
        eqTest(new EqualityTest("DatatypeProperty") {
                String xml() {
                    return "<daml:DatatypeProperty/>";
                }
                void java(DAMLModel m) {
                    m.createDAMLDatatypeProperty(null);
                }
        });
    }
    /**
     * Testing equality: case ObjectProperty
     */
    public void testObjectProperty()

    {
        eqTest(new EqualityTest("ObjectProperty") {
                String xml() {
                    return "<daml:ObjectProperty/>";
                }
                void java(DAMLModel m) {
                    m.createDAMLObjectProperty(null);
                }
        });
    }
    /**
     * Testing equality: case Property
     */
    public void testPropertyEq()

    {
        eqTest(new EqualityTest("Property") {
                String xml() {
                    return "<daml:Property/>";
                }
                void java(DAMLModel m) {
                    m.createDAMLProperty(null);
                }
        });
    }


    /**
     * Testing equality: case Datatype
     */
    public void testDatatypeEq2()

    {
        eqTest(new EqualityTest("Datatype") {
                String xml() {
                    return "<daml:Datatype rdf:about='http://www.w3.org/2000/10/XMLSchema#string'/>";
                }
                void java(DAMLModel m) {
                    m.createDAMLDatatype("http://www.w3.org/2000/10/XMLSchema#string");
                }
        });
    }

    public void testDatatypeRange()

    {
        eqTest(new EqualityTest("Datatype Range") {
                String xml() {
                    // Example taken from the DAML+OIL walk-thru
                    return
"<daml:DatatypeProperty rdf:ID='shoesize'>"+
 " <rdf:type rdf:resource='http://www.daml.org/2001/03/daml+oil#UniqueProperty'/>" +
 " <daml:range rdf:resource='http://www.w3.org/2000/10/XMLSchema#decimal'/>" +
"</daml:DatatypeProperty>" +
"<daml:Datatype rdf:about='http://www.w3.org/2000/10/XMLSchema#decimal'/>";
                }
                void java(DAMLModel m) {
                    DAMLDatatypeProperty shoeSize=m.createDAMLDatatypeProperty("http://example.org/#shoesize");
                    shoeSize.setIsUnique(true);
                    shoeSize.prop_range().add(
                        m.createDAMLDatatype("http://www.w3.org/2000/10/XMLSchema#decimal") );
                }
        });
    }


    /**
     * Concept: the EqualityTest object embodies some java code that adds
     * stuff to a DAMLModel, and some xml that is the body of an RDF/XML doc.
     * This test checks that these two different ways of describing a DAML
     * model are the same.
     *
     */
    private void eqTest(EqualityTest test) {
        DAMLModel m1 = getCleanModel();
        test.java(m1);

        Model m2 = ModelFactory.createDefaultModel();
        Reader rdr = new StringReader(
          "<rdf:RDF " +
    "xmlns:daml='http://www.daml.org/2001/03/daml+oil#' " +
    "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>"
     + test.xml()
       + "</rdf:RDF>");
       m2.read(rdr,"http://example.org/");

       if (! m1.getBaseModel().isIsomorphicWith(m2) ) {
           System.out.println("Java:");
           m1.write(System.out,"RDF/XML-ABBREV");
           System.out.println("XML:");
           m2.write(System.out,"RDF/XML-ABBREV");
       }

       assertTrue("java code and xml should be equivalent",m1.getBaseModel().isIsomorphicWith(m2));

   }

   static abstract private class EqualityTest {
       String name;
       EqualityTest(String nm) {
           name = nm;
       }
       public String toString() {
           return name;
       }
       abstract void java(DAMLModel m);
       abstract String xml();
   }


    /**
     * Dump the model out to a file for debugging
     */
    public void dumpModel( Model m ) {
        dumpModel( m, "model-out.rdf" );
    }
    public void dumpModel( Model m, String fileName ) {
        try {
            OutputStream f = new FileOutputStream(fileName);
            m.write( f, "RDF/XML-ABBREV" );
            f.close();
        }
        catch (Exception e) {
           LogFactory.getLog( getClass() ).debug( "Exception while dumping model: " + e, e );
        }

    }


    /**
     * Count the number of things in an iterator, optionally logging them
     */
    private int countIteration( Iterator i, boolean doLog, String message ) {
        int count = 0;
        for (;  i.hasNext();  count++) {
            Object x = i.next();

            if (doLog) {
                LogFactory.getLog( getClass() ).debug( "counting iteration, " + message + x );
            }
        }

        return count;
    }

    private int countClasses( DAMLModel m ) {
        return countIteration( m.listDAMLClasses(), true, "class = " );
    }

    private int countProperties( DAMLModel m ) {
        return countIteration( m.listDAMLProperties(), true, "property = " );
    }

    private DAMLModel getCleanModel() {
        DAMLModel m = ModelFactory.createDAMLModel();
        m.getDocumentManager().setProcessImports(true);
        m.getDocumentManager().clearCache();
        m.getDocumentManager().setMetadataSearchPath( "file:etc/ont-policy-test.rdf", true );
        List ll = new ArrayList();
        for (Iterator i = m.getImportModelMaker().listModels(); i.hasNext(); ll.add( i.next() ) );
        for (Iterator i = ll.iterator(); i.hasNext(); ) {
            String mName = (String) i.next();
            m.getImportModelMaker().removeModel(mName);
            log.debug( "Removing " + mName );
        }
        return m;
    }

    private void checkValidLists( Model m, String label ) {
        log.debug( "Checking lists in DAMLTest - " + label );
        for (StmtIterator i = m.listStatements(); i.hasNext(); ) {
            Statement s = i.nextStatement();
            RDFNode n = s.getObject();
            if (n instanceof Resource && ((Resource) n).canAs( RDFList.class )) {
                if (!((RDFList) n.as(RDFList.class)).isValid()) {
                    log.debug( "!!Found invalid list in " + label + " - " + n );
                    assertTrue( "DAML list not valid ", false );
                }
            }
        }
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

