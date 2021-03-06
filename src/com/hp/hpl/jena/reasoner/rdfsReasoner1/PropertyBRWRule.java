/******************************************************************
 * File:        PropertyBRWRule.java
 * Created by:  Dave Reynolds
 * Created on:  28-Jan-03
 * 
 * (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: PropertyBRWRule.java,v 1.13 2007/01/02 11:48:52 andy_seaborne Exp $
 *****************************************************************/
package com.hp.hpl.jena.reasoner.rdfsReasoner1;

import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.util.iterator.*;

import java.util.*;

/**
 * A special case of a backchaing rule to handle the nasty case
 * of "anything mentioned in predicated position is a Property".
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.13 $ on $Date: 2007/01/02 11:48:52 $
 */
public class PropertyBRWRule extends BRWRule {

    /**
     * Constructor
     */
    public PropertyBRWRule() {
        super(new TriplePattern(Node.createVariable("p"), RDF.type.asNode(), RDF.Property.asNode()),   
               new TriplePattern(null, Node.createVariable("s"), null));
    }
    
    /**
     * Use the rule to implement the given query. This will
     * instantiate the rule against the query, run the new query
     * against the whole reasoner+rawdata again and then rewrite the
     * results from that query according the rule.
     * @param query the query being processed
     * @param infGraph the parent InfGraph that invoked us, will be called recursively
     * @param data the raw data graph which gets passed back to the reasoner as part of the recursive invocation
     * @param firedRules set of rules which have already been fired and should now be blocked
     * @return a ExtendedIterator which aggregates the matches and rewrites them
     * according to the rule
     */
    public ExtendedIterator execute(TriplePattern query, InfGraph infGraph, Finder data, HashSet firedRules) {
        RDFSInfGraph bRr = (RDFSInfGraph) infGraph;
        Node prop = query.getSubject();
        if (bRr.getScanProperties()) {
            // All properties are cached in the subProperty graph
            // That in turn will be accessed via the rules generated by the subPropertyOf domain/range
            // rules do don't need to do anything here
            return NullIterator.instance;
        } else {
            // Have to scan all the raw data for property usage
            TriplePattern pattern = instantiate(body, query);
            return new RewriteIterator(bRr.findRawWithContinuation(body, data), this);
        }
    }    

    /**
     * Inner class. Rewrites property node lists in to (p, type, Property)
     * assertions.
     */
    static class PropertyNodeIterator extends UniqueExtendedIterator {
        
        /** 
         * Constructor 
         * @param underlying the iterator whose results are to be rewritten
         * @param rule the BRWRule which defines the rewrite
         */
        public PropertyNodeIterator(Iterator underlying) {
            super(underlying);
        }
    
        /**
         * Fetch the next object to be returned, only if not already seen.
         * Filters on the property rather than the generated triple.
         * 
         * @return the object to be returned or null if the object has been filtered.
         */
        protected Object nextIfNew() {
            Node prop = (Node)super.next();
            if (seen.add(prop)) {
                return new Triple(prop, RDF.type.asNode(), RDF.Property.asNode());
            } else {
                return null;
            }
        }
        
    }    // End of inner class - ResourceRewriteIterator

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
