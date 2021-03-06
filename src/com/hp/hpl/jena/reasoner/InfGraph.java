/******************************************************************
 * File:        InfGraph.java
 * Created by:  Dave Reynolds
 * Created on:  10-Jan-2003
 * 
 * (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: InfGraph.java,v 1.13 2007/01/02 11:52:18 andy_seaborne Exp $
 *****************************************************************/
package com.hp.hpl.jena.reasoner;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.util.Iterator;

/**
 * Extends the Graph interface to give additional means to query an inferred
 * graph. Many entailments from the raw data are made to appear as if they
 * are extract triples in the inferred graph and so appear through the
 * normal Graph.find interface. 
 * 
 * However, here are two extensions required. Firstly, the ability to
 * ask about global properties of the whole graph (e.g. consistency). Secondly,
 * the ability to temporarily construct expressions (encoded in RDF) which 
 * form more complex queries.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.13 $ on $Date: 2007/01/02 11:52:18 $
 */
public interface InfGraph extends Graph {

    /**
     * Return the raw RDF data Graph being processed (i.e. the argument
     * to the Reasonder.bind call that created this InfGraph).
     */
    public Graph getRawGraph();
    
    /**
     * Return the Reasoner which is being used to answer queries to this graph.
     */
    public Reasoner getReasoner();

    /**
     * Replace the underlying data graph for this inference graph and start any
     * inferences over again. This is primarily using in setting up ontology imports
     * processing to allow an imports multiunion graph to be inserted between the
     * inference graph and the raw data, before processing.
     * @param data the new raw data graph
     */
    public void rebind(Graph data);
    
    /**
     * Cause the inference graph to reconsult the underlying graph to take
     * into account changes. Normally changes are made through the InfGraph's add and
     * remove calls are will be handled appropriately. However, in some cases changes
     * are made "behind the InfGraph's back" and this forces a full reconsult of
     * the changed data. 
     */
    public void rebind();
    
    /**
     * Perform any initial processing and caching. This call is optional. Most
     * engines either have negligable set up work or will perform an implicit
     * "prepare" if necessary. The call is provided for those occasions where
     * substantial preparation work is possible (e.g. running a forward chaining
     * rule system) and where an application might wish greater control over when
     * this prepration is done.
     */
    public void prepare();
    
    /**
     * Reset any internal caches. Some systems, such as the tabled backchainer, 
     * retain information after each query. A reset will wipe this information preventing
     * unbounded memory use at the expense of more expensive future queries. A reset
     * does not cause the raw data to be reconsulted and so is less expensive than a rebind.
     */
    public void reset();
    
    /**
     * Test a global boolean property of the graph. This might included
     * properties like consistency, OWLSyntacticValidity etc.
     * It remains to be seen what level of generality is needed here. We could
     * replace this by a small number of specific tests for common concepts.
     * @param property the URI of the property to be tested 
     * @return a Node giving the value of the global property, this may 
     * be a boolean literal, some other literal value (e.g. a size).
     */    
    public Node getGlobalProperty(Node property);
    
    /**
     * A convenience version of getGlobalProperty which can only return
     * a boolean result.
     */
    public boolean testGlobalProperty(Node property);
    
    /**
     * Test the consistency of the bound data. This normally tests
     * the validity of the bound instance data against the bound
     * schema data. 
     * @return a ValidityReport structure
     */
    public ValidityReport validate();
    
    /**
     * An extension of the Graph.find interface which allows the caller to 
     * encode complex expressions in RDF and then refer to those expressions
     * within the query triple. For example, one might encode a class expression
     * and then ask if there are any instances of this class expression in the
     * InfGraph.
     * @param subject the subject Node of the query triple, may be a Node in 
     * the graph or a node in the parameter micro-graph or null
     * @param property the property to be retrieved or null
     * @param object the object Node of the query triple, may be a Node in 
     * the graph or a node in the parameter micro-graph.    
     * @param param a small graph encoding an expression which the subject and/or
     * object nodes refer.
     */
    public ExtendedIterator find(Node subject, Node property, Node object, Graph param);
    
    /**
     * Switch on/off drivation logging
     */
    public void setDerivationLogging(boolean logOn);
   
    /**
     * Return the derivation of the given triple (which is the result of
     * some previous find operation).
     * Not all reasoneers will support derivations.
     * @return an iterator over Derivation records or null if there is no derivation information
     * available for this triple.
     */
    public Iterator getDerivation(Triple triple);
    
    /**
     * Returns a derivations graph. The rule reasoners typically create a 
     * graph containing those triples added to the base graph due to rule firings.
     * In some applications it can useful to be able to access those deductions
     * directly, without seeing the raw data which triggered them. In particular,
     * this allows the forward rules to be used as if they were rewrite transformation
     * rules.
     * @return the deductions graph, if relevant for this class of inference
     * engine or null if not.
     */
    public Graph getDeductionsGraph(); 
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

