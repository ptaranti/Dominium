/*
 *  (c) Copyright 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 *  All rights reserved.
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
 *
 * $Id: TestCaseBugs.java,v 1.9 2007/01/02 11:51:11 andy_seaborne Exp $
 */

package com.hp.hpl.jena.mem.test;

import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.rdf.model.*;

/**
    @author  bwm
    @version $Name:  $ $Revision: 1.9 $ $Date: 2007/01/02 11:51:11 $
*/
public class TestCaseBugs 
            extends TestCaseBasic {
                
    Model model = null;

    public TestCaseBugs(String name) {
        super(name);
    }
    
    public void setUp() {
        model = ModelFactory.createDefaultModel();
    }
    
    public void bug36() {
            Resource r    = model.createResource();
            Object   oc   = RDFS.Class;
            Object   op   = RDF.Property;
            
            Statement s = model.createStatement(r, RDF.type, oc);
            assertInstanceOf(Resource.class, s.getObject() );
            
            s.changeObject(op);
            assertInstanceOf(Resource.class, s.getObject() );
            
            model.add(r, RDF.type, oc);
            RDFNode n = model.listStatements()
                             .nextStatement()
                             .getObject();
            assertInstanceOf(Resource.class, n );
            
            assertTrue(model.listSubjectsWithProperty(RDF.type, oc).hasNext());
            
            assertTrue(model.contains(r, RDF.type, oc));  
         }
}
