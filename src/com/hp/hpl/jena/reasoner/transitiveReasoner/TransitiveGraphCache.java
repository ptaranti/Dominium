/******************************************************************
 * File:        TransitiveGraphCacheNew.java
 * Created by:  Dave Reynolds
 * Created on:  16-Nov-2004
 * 
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: TransitiveGraphCache.java,v 1.23 2007/01/11 15:51:05 der Exp $
 *****************************************************************/

package com.hp.hpl.jena.reasoner.transitiveReasoner;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.reasoner.*;

import java.util.*;

/**
 * Datastructure used to represent a closed transitive reflexive relation.
 * It (mostly) incrementally maintains a transitive reduction and transitive
 * closure of the relationship and so queries should be faster than dynamically 
 * computing the closed or reduced relations.
 * <p>
 * The implementation stores the reduced and closed relations as real graph
 * (objects linked together by pointers). For each graph node we store its direct
 * predecessors and successors and its closed successors.  A cost penalty 
 * is the storage turnover involved in turning the graph representation back into 
 * triples to answer queries. We could avoid this by optionally also storing the
 * manifested triples for the links.
 * </p><p>
 * Cycles are currently handled by collapsing strongly connected components.
 * Incremental deletes would be possible but at the price of substanially 
 * more storage and code complexity. We compromise by doing the easy cases
 * incrementally but some deletes (those that break strongly connected components)
 * will trigger a fresh rebuild.
 * </p><p>
 * TODO Combine this with interval indexes (Agrawal, Borigda and Jagadish 1989) 
 * for storing the closure of the predecessor relationship. Typical graphs
 * will be nearly tree shaped so the successor closure is modest (L^2 where
 * L is the depth of the tree branch) but the predecessor closure would be 
 * expensive. The interval index would handle predecessor closure nicely.
 * </p>
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.23 $
 */

// Note to maintainers. The GraphNode object is treated as a record structure
// rather than an abstract datatype by the rest of the GraphCache code - which
// directly access its structure. I justify this on the flimsy grounds that it is a
// private inner class.

public class TransitiveGraphCache implements Finder {

	/** Flag controlling the whether the triples 
	 *  representing the closed relation should also be cached. */
	protected boolean cacheTriples = false;
	
    /** Map from RDF Node to the corresponding Graph node. */
    protected HashMap nodeMap = new HashMap();
    
    /** The RDF predicate representing the direct relation */
    protected Node directPredicate;
    
    /** The RDF predicate representing the closed relation */
    protected Node closedPredicate;
	
    /** A list of pending deletes which break the cycle-free normal form */
    protected Set deletesPending;
    
	/** The original triples, needed for processing delete operations
	 * because some information is lost in the SCC process */ 
	protected Set originalTriples = new HashSet();
	
    /**
     * Inner class used to represent vistors than can be applied to each
     * node in a graph walk. 
     */
    static interface Visitor {
        // The visitor must not delete and pred entries to avoid CME
        // If this is needed return a non-null result which is a list of pred nodes to kill
    	List visit(GraphNode node, GraphNode processing, Object arg1, Object arg2);
    }
    
	/**
	 * Inner class used to represent the graph node structure.
	 * Rather fat nodes (four sets)
	 */
	private static class GraphNode {
        /** The RDF Graph Node this corresponds to */
        protected Node rdfNode;
        
		/** The list of direct successor nodes to this node */
		protected Set succ = new HashSet();
		
		/** The list of direct predecessors nodes */
		protected Set pred = new HashSet();
		
		/** The set of all transitive successor nodes to this node */
		protected Set succClosed = new HashSet();
		
		/** An optional cache of the triples that represent succClosed */
		protected List succClosedTriples;
		
		/** Null for simple nodes. For the lead node in a SCC will be a list
		 * of all the nodes in the SCC. For non-lead nodes it will be a ref to the lead node. */
		protected Object aliases;

        /**
         * Constructor.
         */
        public GraphNode(Node node) {
            rdfNode = node;
        }
        
        /**
         * Return true if there is a path from this node to the argument node.
         */
        public boolean pathTo(GraphNode A) {
            if (this == A) return true;
            return succClosed.contains(A);
        }

        /**
         * Return true if there is a direct path from this node to the argument node.
         */
        public boolean directPathTo(GraphNode A) {
            if (this == A) return true;
            return succ.contains(A);
        }
		
		/**
		 * Return the lead node in the strongly connected component containing this node.
		 * It will be the node itself if it is a singleton or the lead node. 
		 */
		public GraphNode leadNode() {
			if (aliases != null && aliases instanceof GraphNode) {
				return ((GraphNode)aliases).leadNode();
			} else {
				return this;
			}
		}
		
		/**
		 * Visit each predecessor of this node applying the given visitor.
		 */
		public void visitPredecessors(Visitor visitor, Object arg1, Object arg2) {
            List kill = visitor.visit(this, null, arg1, arg2);
            if (kill != null)  pred.removeAll(kill);
			doVisitPredecessors(visitor, arg1, arg2, new HashSet());
		}
		
		/**
		 * Visit each predecessor of this node applying the given visitor.
         * Breadth first.
		 */
		private void doVisitPredecessors(Visitor visitor, Object arg1, Object arg2, Set seen) {
			if (seen.add(this)) {
                Collection allKill = null;
                for (Iterator i = pred.iterator(); i.hasNext(); ) {
                    GraphNode pred = (GraphNode)i.next();
                    List kill = visitor.visit(pred, this, arg1, arg2);
                    if (kill != null) {
                        if (allKill == null) allKill = new ArrayList();
                        allKill.addAll(kill);
                    }
                }
                if (allKill != null) pred.removeAll(allKill);
                for (Iterator i = pred.iterator(); i.hasNext(); ) {
                    GraphNode pred = (GraphNode)i.next();
                    pred.doVisitPredecessors(visitor, arg1, arg2, seen);
                }
			}
		}
		
		/**
		 * Return an iterator over all the indirect successors of this node.
         * This does NOT include aliases of successors and is used for graph maintenance.
		 */
		public Iterator iteratorOverSuccessors() {
			return succClosed.iterator();
		}
		
		/**
		 * Assert a direct link between this node and this given target.
		 * Does not update the closed successor cache
		 */
		public void assertLinkTo(GraphNode target) {
            if (this == target) return;
			succ.add(target);
			target.pred.add(this);
			clearTripleCache();
		}
		
		/**
		 * Remove a direct link currently from this node to the given target.
		 * Does not update the closed successor cache.
		 */
		public void retractLinkTo(GraphNode target) {
            if (this == target) return;
			succ.remove(target);
			target.pred.remove(this);
			clearTripleCache();
		}
		
		/**
		 * Assert an inferred indirect link from this node to the given traget
		 */
		public void assertIndirectLinkTo(GraphNode target) {
//            if (this == target) return;
			succClosed.add(target);
			clearTripleCache();
		}
		
		/**
		 * Clear the option cache of the closure triples.
		 */
		public void clearTripleCache() {
			succClosedTriples = null;
		}
        
		/**
		 * Propagate the results of adding a link from this
		 * node to the target node.
		 */
		public void propagateAdd(GraphNode target) {
            Set sc = new HashSet(target.succClosed);
            sc.add(target); 
			visitPredecessors(new Visitor() {
				public List visit(GraphNode node, GraphNode processing, Object arg1, Object target) {
					Set sc = (Set)arg1;
					// Add closure
					node.succClosed.addAll(sc);
					// Scan for redundant links
                    List kill = null;
					for (Iterator i = node.succ.iterator(); i.hasNext();) {
						GraphNode s = (GraphNode)i.next();
						if (sc.contains(s)) {
							i.remove();
                            if (s == processing) {
                                // Can't remove immediately w/o beaking the visitor loop
                                if (kill == null) kill = new ArrayList();
                                kill.add(node);
                            } else {
                                s.pred.remove(node);
                            }
						}
					}
                    return kill;
				}
		    }, sc, target);
		}
        
		/**
		 * Propagate the results of creating a new SCC with this
		 * node as lead.
		 */
		public void propagateSCC() {
			Set visited = new HashSet();
			visited.add(this);
			// Scan predecessors not including ourselves
			doVisitPredecessors(new Visitor() {
				public List visit(GraphNode node, GraphNode processing, Object arg1, Object arg2) {
					Set sc = (Set)arg1;
					// Add closure
					node.succClosed.addAll(sc);
					// Scan for redundant links
                    List kill = null;
					for (Iterator i = node.succ.iterator(); i.hasNext();) {
						GraphNode s = (GraphNode)i.next();
						if (sc.contains(s)) {
							i.remove();
//                            s.pred.remove(node);
                            if (s == processing) {
                                // Can't remove immediately w/o beaking the visitor loop
                                if (kill == null) kill = new ArrayList();
                                kill.add(node);
                            } else {
                                s.pred.remove(node);
                            }
						}
					}
                    return kill;
				}
		    }, succClosed, null, visited);
		}
		
        /**
         * Given a set of SCC nodes make this the lead member of the SCC and
         * reroute all incoming and outgoing links accordingly.
         * This eager rewrite is based on the assumption that there are few cycles
         * so it is better to rewrite once and keep the graph easy to traverse.
         */
        public void makeLeadNodeFor(Set members) {
            // Accumulate all successors
            Set newSucc = new HashSet();
            Set newSuccClosed = new HashSet();
            for (Iterator i = members.iterator(); i.hasNext(); ) {
                GraphNode n = (GraphNode)i.next();
                newSucc.addAll(n.succ);
                newSuccClosed.addAll(n.succClosed);
            }
            newSucc.removeAll(members);
            newSuccClosed.removeAll(members);
            succ = newSucc;
            succClosed = newSuccClosed;
            
            // Rewrite all direct successors to have us as predecessor
            for (Iterator i = succ.iterator(); i.hasNext();) {
                GraphNode n = (GraphNode)i.next();
                n.pred.removeAll(members);
                n.pred.add(this);
            }
            
            // Find all predecessor nodes and relink link them to point to us
            Set done = new HashSet();
            Set newAliases = new HashSet();
            for (Iterator i = members.iterator(); i.hasNext(); ) {
            	GraphNode m = (GraphNode)i.next();
            	if (m.aliases instanceof Set) {
            		newAliases.addAll((Set)m.aliases);
            	} else {
            		newAliases.add(m);
            	}
            }
            this.aliases = newAliases;
            for (Iterator i = members.iterator(); i.hasNext(); ) {
                GraphNode n = (GraphNode)i.next();
                if (n != this) {
                    pred.addAll(n.pred);
                    n.relocateAllRefTo(this, done);
                    n.aliases = this;
                }
            }
            pred.removeAll(members);
        }
		
        /**
         * This node is being absorbed into an SCC with the given node as the
         * new lead node. Trace out all predecessors to this node and relocate
         * them to point to the new lead node.
         */
        private void relocateAllRefTo(GraphNode lead, Set done) {
            visitPredecessors(new Visitor(){
                public List visit(GraphNode node, GraphNode processing, Object done, Object leadIn) {
                    if (((Set)done).add(node)) {
                        GraphNode lead = (GraphNode)leadIn;
                        Set members = (Set)lead.aliases;
                        int before = node.succ.size();
                        node.succ.removeAll(members);
                        node.succClosed.removeAll(members);
                        node.succClosed.add(lead);
                        if (node.succ.size() != before) {
                            node.succ.add(lead);
                        }
                    }
                    return null;
                }
            }, done, lead);
        }
        
        /**
         * Return an iterator over all of the triples representing outgoing links
         * from this node.  
         * @param closed if set to true it returns triples in the transitive closure,
         * if set to false it returns triples in the transitive reduction
         * @param cache the enclosing TransitiveGraphCache
         */
        public ExtendedIterator listTriples(boolean closed, TransitiveGraphCache tgc) {
            if (tgc.cacheTriples) {
                // TODO implement - for now default to non-cached
                return WrappedIterator.create(leadNode().triplesForSuccessors(rdfNode, closed, tgc).iterator());
            } else {
                return WrappedIterator.create(leadNode().triplesForSuccessors(rdfNode, closed, tgc).iterator());
            }
        }
        
        /**
         * Create a list of triples for a given set of successors to this node.
         */
        private List triplesForSuccessors(Node base, boolean closed, TransitiveGraphCache tgc) {
            Set successors = closed ? succClosed : succ;
            ArrayList result = new ArrayList(successors.size() + 10);
            result.add(new Triple(base, tgc.closedPredicate, base));    // implicit reflexive case 
            for (Iterator i = successors.iterator(); i.hasNext(); ) {
                GraphNode s = (GraphNode)i.next();
                result.add(new Triple(base, tgc.closedPredicate, s.rdfNode));
                if (s.aliases instanceof Set) {
                    for (Iterator j = ((Set)s.aliases).iterator(); j.hasNext(); ) {
                        result.add(new Triple(base, tgc.closedPredicate, ((GraphNode)j.next()).rdfNode));
                    }
                }
            }
            if (aliases instanceof Set) {
                for (Iterator j = ((Set)aliases).iterator(); j.hasNext(); ) {
                    result.add(new Triple(base, tgc.closedPredicate, ((GraphNode)j.next()).rdfNode));
                }
            }
            return result;
        }
        
        /**
         * Return an iterator over all of the triples representing incoming links to this node.
         * Currently no caching enabled.
         */
        public ExtendedIterator listPredecessorTriples(boolean closed, TransitiveGraphCache tgc) {
            return new GraphWalker(leadNode(), rdfNode, closed, tgc.closedPredicate);
        }
        
        /**
         * Print node label to assist with debug.
         */
        public String toString() {
            return "[" + rdfNode.getLocalName() + "]";
        }
        
        /**
         * Full dump for debugging
         */
        public String dump() {
        	String result = rdfNode.getLocalName();
        	if (aliases != null) {
        		if (aliases instanceof GraphNode) {
        			result = result + " leader=" + aliases + ", ";
        		} else {
        			result = result + " SCC=" + dumpSet((Set)aliases) +", ";
        		}
        	}
        	return result + " succ=" + dumpSet(succ) + ", succClose=" + dumpSet(succClosed) + ", pred=" + dumpSet(pred);
        }
        
        /**
         * Dump a set to a string for debug.
         */
        private String dumpSet(Set s) {
        	StringBuffer sb = new StringBuffer();
        	sb.append("{");
        	boolean started = false;
        	for (Iterator i = s.iterator(); i.hasNext(); ) {
        		if (started) {
        			sb.append(", ");
        		} else {
        			started = true;
        		}
        		sb.append(i.next().toString());
        	}
        	sb.append("}");
        	return sb.toString();
        }
        
	} // End of GraphNode inner class
	
    /**
     * Inner class used to walk backward links of the graph.
     * <p> The triples are dynamically allocated which is costly. 
     */
    private static class GraphWalker extends NiceIterator implements ExtendedIterator {
        
        /** Indicate if this is a shallow or deep walk */
        boolean isDeep;
        
        /** The current node being visited */
        GraphNode node;
        
        /** The root node for reconstructing triples */
        Node root;
        
        /** The predicate for reconstructing triples */
        Node predicate; 
        
        /** Iterator over the predecessors to the current node bein walked */
        Iterator iterator = null;
        
        /** Iterator over the aliases of the current predecessor being output */
        Iterator aliasIterator = null;
        
        /** stack of graph nodes being walked */
        ArrayList nodeStack = new ArrayList();
        
        /** stack of iterators for the higher nodes in the walk */
        ArrayList iteratorStack = new ArrayList();
        
        /** The next value to be returned */
        Triple next;
        
        /** The set of junction nodes already visited */
        HashSet visited = new HashSet();
        
        /** 
         * Constructor. Creates an iterator which will walk
         * the graph, returning triples.
         * @param node the starting node for the walk
         * @param rdfNode the rdfNode we are try to find predecessors for
         * @param closed set to true of walking the whole transitive closure
         * @param predicate the predicate to be walked
         */
        GraphWalker(GraphNode node, Node rdfNode, boolean closed, Node predicate) {
            isDeep = closed;
            this.node = node;
            this.root = rdfNode;
            this.predicate = predicate;
            this.iterator = node.pred.iterator();
            if (node.aliases instanceof Set) {
                aliasIterator = ((Set)node.aliases).iterator();
            }
            next = new Triple(root, predicate, root);   // implicit reflexive case 
        }
        
        /** Iterator interface - test if more values available */
        public boolean hasNext() {
            return next != null;
        }
        
        /** Iterator interface - get next value */
        public Object next() {
            Object toReturn = next;
            walkOne();
            return toReturn;
        }
                
        /**
         * Walk one step
         */
        protected void walkOne() {
            if (aliasIterator != null) {
                if (aliasIterator.hasNext()) {
                    GraphNode nextNode = (GraphNode)aliasIterator.next();
                    next =  new Triple(nextNode.rdfNode, predicate, root);
                    return;
                } else {
                    aliasIterator = null;
                }
            }
            if (iterator.hasNext()) {
                GraphNode nextNode = (GraphNode)iterator.next();
                if (visited.add(nextNode)) {
                    // Set up for depth-first visit next
                    if (isDeep)
                        pushStack(nextNode);
                    next =  new Triple(nextNode.rdfNode, predicate, root);
                    if (nextNode.aliases instanceof Set) {
                        aliasIterator = ((Set)nextNode.aliases).iterator();
                    }
                } else { 
                    // Already visited this junction, skip it
                    walkOne();
                    return;
                }
            } else {
                // Finished this node
                if (nodeStack.isEmpty()) {
                    next = null;
                    return;
                }
                popStack();
                walkOne();
            }
        }
        
        /**
         * Push the current state onto the stack
         */
        protected void pushStack(GraphNode next) {
            nodeStack.add(node);
            iteratorStack.add(iterator);
            iterator = next.pred.iterator();
            node = next;
        }
        
        /**
         * Pop the prior state back onto the stack
         */
        protected void popStack() {
            int i = nodeStack.size()-1;
            iterator = (Iterator) iteratorStack.remove(i);
            node = (GraphNode) nodeStack.remove(i);
        }
        
    } // End of GraphWalker inner class    
    
    /**
     * Inner class used to do a complete walk over the graph
     */
    private static class FullGraphWalker extends NiceIterator implements ExtendedIterator {

        /** Flag whether we are walking over the closed or direct relations */
        boolean closed;
        
        /** Iterator over the start nodes in the node map */
        Iterator baseNodeIt;
        
        /** The current node being visited */
        GraphNode node;
        
        /** The root node for reconstructing triples */
        Node nodeN;
        
        /** The predicate for reconstructing triples */
        Node predicate; 
        
        /** Iterator over the successor nodes for the baseNode */
        Iterator succIt = null;
        
        /** The current successor being processed */
        GraphNode succ;
        
        /** Iterator over the aliases for the current successor */
        Iterator aliasesIt = null;
        
        /** The next value to be returned */
        Triple next;
        
        /** Construct a walker for the full closed or direct graph */
        FullGraphWalker(boolean closed, Node predicate, HashMap nodes) {
            this.predicate = predicate;
            this.closed = closed;
            baseNodeIt = nodes.values().iterator();
            walkOne();
        }
        
        /** Iterator interface - test if more values available */
        public boolean hasNext() {
            return next != null;
        }
        
        /** Iterator interface - get next value */
        public Object next() {
            Object toReturn = next;
            walkOne();
            return toReturn;
        }
                
        /**
         * Walk one step
         */
        protected void walkOne() {
            if (aliasesIt != null) {
                while (aliasesIt.hasNext()) {
                    GraphNode al = (GraphNode)aliasesIt.next();
                    if (al != succ && al != node) {
                        next = new Triple(nodeN, predicate, al.rdfNode);
                        return;
                    }
                }
                aliasesIt = null;      // End of aliases
            }
            
            if (succIt != null) {
                while (succIt.hasNext()) {
                    succ = (GraphNode)succIt.next();
                    if (succ == node) continue; // Skip accidental reflexive cases, already done
                    if (succ.aliases instanceof Set) {
                        aliasesIt = ((Set)succ.aliases).iterator();
                    }
                    next = new Triple(nodeN, predicate, succ.rdfNode);
                    return;
                }
                succIt = null;      // End of the successors
            }
            
            if (baseNodeIt.hasNext()) {
                node = (GraphNode)baseNodeIt.next();
                nodeN = node.rdfNode;
                GraphNode lead = node.leadNode();
                succIt = (closed ? lead.succClosed : lead.succ).iterator();
                if (lead.aliases instanceof Set) {
                    succIt = new ConcatenatedIterator(succIt, ((Set)lead.aliases).iterator());
                }
                next = new Triple(nodeN, predicate, nodeN); // Implicit reflexive case
            } else {
                next = null; // End of walk
            }
        }
        
    } // End of FullGraphWalker inner class
    
    /**
     * Constructor - create a new cache to hold the given relation information.
     * @param directPredicate The RDF predicate representing the direct relation
     * @param closedPredicate The RDF predicate representing the closed relation
     */
    public TransitiveGraphCache(Node directPredicate, Node closedPredicate) {
        this.directPredicate = directPredicate;
        this.closedPredicate = closedPredicate;
    }
    
    /**
     * Returns the closedPredicate.
     * @return Node
     */
    public Node getClosedPredicate() {
        return closedPredicate;
    }

    /**
     * Returns the directPredicate.
     * @return Node
     */
    public Node getDirectPredicate() {
        return directPredicate;
    }
     
    /**
     * Register a new relation instance in the cache
     */
    public synchronized void addRelation(Triple t) {
    	originalTriples.add(t);
    	addRelation(t.getSubject(), t.getObject());
    }
    
    /**
     * Register a new relation instance in the cache
     */
    private void addRelation(Node start, Node end) {
        if (start.equals(end)) return;      // Reflexive case is built in
        GraphNode startN = getLead(start);
        GraphNode endN = getLead(end);
    	
    	// Check if this link is already known about
    	if (startN.pathTo(endN)) {
    		// yes, so no work to do
    		return;
    	}

    	boolean needJoin = endN.pathTo(startN);
        Set members = null;
        if (needJoin) {
        	// Reduce graph to DAG by factoring out SCCs
//	        startN.assertLinkTo(endN);
            // First find all the members of the new component
            members = new HashSet();
            members.add(endN);
            startN.visitPredecessors(new Visitor() {
                public List visit(GraphNode node, GraphNode processing, Object members, Object endN) {
                    if (((GraphNode)endN).pathTo(node)) ((Set)members).add(node);
                    return null;
                } }, members, endN);
            // Then create the SCC
            startN.makeLeadNodeFor(members);
            // Now propagate the closure in the normalized graph
            startN.propagateSCC();
        } else {
	    	// Walk all predecessors of start retracting redundant direct links
	    	// and adding missing closed links
	        startN.propagateAdd(endN);
	        startN.assertLinkTo(endN);
        }
        
    	if (needJoin) {
    		// Create a new strongly connected component
    	}
    }
    
    /**
     * Remove an instance of a relation from the cache.
     */
    public void removeRelation(Triple t) {
    	Node start = t.getSubject();
    	Node end = t.getObject();
    	if (start == end) {
    		return;		// Reflexive case is built in
    	}
    	GraphNode startN = getLead(start);
    	GraphNode endN = getLead(end);
    	if (startN != endN && !(startN.directPathTo(endN))) {
    		// indirect link can't be removed by itself
    		return;
    	}
    	// This is a remove of a direct link possibly within an SCC
    	// Delay as long as possible and do deletes in a batch
    	if (deletesPending == null) {
    		deletesPending = new HashSet();
    	}
    	deletesPending.add(t);
    }

    /**
     * Process outstanding delete actions
     */
    private void processDeletes() {
    	// The kernel is the set of start nodes of deleted links
    	Set kernel = new HashSet();
    	for (Iterator i = deletesPending.iterator(); i.hasNext(); ) {
    		Triple t = (Triple)i.next();
    		GraphNode start = (GraphNode)nodeMap.get(t.getSubject());
    		kernel.add(start);
    	}
    	
    	// The predecessor set of kernel
    	Set pKernel = new HashSet();
    	pKernel.addAll(kernel);
    	for (Iterator i = nodeMap.values().iterator(); i.hasNext(); ) {
    		GraphNode n = (GraphNode)i.next();
    		for (Iterator j = kernel.iterator(); j.hasNext();) {
    			GraphNode target = (GraphNode)j.next();
    			if (n.pathTo(target)) {
    				pKernel.add(n); break;
    			}
    		}
    	}
    	
    	// Cut the pKernel away from the finge of nodes that it connects to
    	for (Iterator i = pKernel.iterator(); i.hasNext(); ) {
    		GraphNode n = (GraphNode)i.next();
    		for (Iterator j = n.succ.iterator(); j.hasNext(); ) {
    			GraphNode fringe = (GraphNode)j.next();
    			if (! pKernel.contains(fringe)) {
    				fringe.pred.remove(n);
    			}
    		}
    		n.succ.clear();
    		n.succClosed.clear();
    		n.pred.clear();
    	}
    	
    	// Delete the triples
    	originalTriples.removeAll(deletesPending);
    	deletesPending.clear();
    	
    	// Reinsert the remaining links
    	for (Iterator i = originalTriples.iterator(); i.hasNext(); ) {
    		Triple t = (Triple)i.next();
    		GraphNode n = (GraphNode)nodeMap.get(t.getSubject());
    		if (pKernel.contains(n)) {
    			addRelation(t);
    		}
    	}
    }
    
    /**
     * Extended find interface used in situations where the implementator
     * may or may not be able to answer the complete query. 
     * <p>
     * In this case any query on the direct or closed predicates will
     * be assumed complete, any other query will pass on to the continuation.</p>
     * @param pattern a TriplePattern to be matched against the data
     * @param continuation either a Finder or a normal Graph which
     * will be asked for additional match results if the implementor
     * may not have completely satisfied the query.
     */
    public ExtendedIterator findWithContinuation(TriplePattern pattern, Finder continuation) {
        Node p = pattern.getPredicate();
        
        if (p.isVariable()) {
            // wildcard predicate so return merge of cache and continuation
            return find(pattern).andThen(continuation.find(pattern));
        } else if (p.equals(directPredicate) || p.equals(closedPredicate)) {
            // Satisfy entire query from the cache
            return find(pattern);
        } else {
            // No matching triples in this cache so just search the continuation
            return continuation.find(pattern);
        }
        
    }
    
    /**
     * Return true if the given pattern occurs somewhere in the find sequence.
     */
    public boolean contains(TriplePattern pattern) {
        ClosableIterator it = find(pattern);
        boolean result = it.hasNext();
        it.close();
        return result;
    }
    /**
     * Return an iterator over all registered subject nodes
     */
    public ExtendedIterator listAllSubjects() {
        return WrappedIterator.create(nodeMap.keySet().iterator());
    }
   
    /**
     * Return true if the given Node is registered as a subject node
     */
    public boolean isSubject(Node node) {
        return nodeMap.keySet().contains(node);
    }
    
    /**
     * Cache all instances of the given predicate which are
     * present in the given Graph.
     * @param graph the searchable set of triples to cache
     * @param predicate the predicate to cache, need not be the registered
     * predicate due to subProperty declarations
     * @return returns true if new information has been cached
     */
    public boolean cacheAll(Finder graph, Node predicate) {
        ExtendedIterator it = graph.find(new TriplePattern(null, predicate, null));
        boolean foundsome = it.hasNext();
        while (it.hasNext()) {
            Triple triple = (Triple) it.next();
            addRelation(triple);
        }
        it.close();
        return foundsome;
    }
    
    /**
     * Basic pattern lookup interface.
     * @param pattern a TriplePattern to be matched against the data
     * @return a ExtendedIterator over all Triples in the data set
     *  that match the pattern
     */
    public ExtendedIterator find(TriplePattern pattern) {
    	if (deletesPending != null && deletesPending.size() > 0) {
    		processDeletes();
    	}

    	Node s = pattern.getSubject();
        Node p = pattern.getPredicate();
        Node o = pattern.getObject();
        
        if (p.isVariable() || p.equals(directPredicate) || p.equals(closedPredicate)) {
            boolean closed = !p.equals(directPredicate);
            Node pred = closedPredicate; // p.isVariable() ? closedPredicate : p;
            if (s.isVariable()) {
                if (o.isVariable()) {
                    // list all the graph contents
//                    ExtendedIterator result = null;
//                    for (Iterator i = nodeMap.values().iterator(); i.hasNext(); ) {
//                        ExtendedIterator nexti = ((GraphNode)i.next()).listTriples(closed, this);
//                        if (result == null) {
//                            result = nexti;
//                        } else {
//                            result = result.andThen(nexti);
//                        }
//                    }
//                    if (result == null) {
//                        return NullIterator.instance;
//                    }
                    return new FullGraphWalker(closed, closedPredicate, nodeMap);
                } else {
                    // list all backwards from o
                    GraphNode gn_o = (GraphNode)nodeMap.get(o);
                    if (gn_o == null) return NullIterator.instance;
                    return gn_o.listPredecessorTriples(closed, this);
                }
            } else {
                GraphNode gn_s = (GraphNode)nodeMap.get(s);
                if (gn_s == null) return NullIterator.instance;
                if (o.isVariable()) {
                    // list forward from s
                    return gn_s.listTriples(closed, this);
                } else {
                    // Singleton test
                    GraphNode gn_o = (GraphNode)nodeMap.get(o);
                    gn_s = gn_s.leadNode();
                    if (gn_o == null) return NullIterator.instance;
                    gn_o = gn_o.leadNode();
                    if ( closed ? gn_s.pathTo(gn_o) : gn_s.directPathTo(gn_o) ) {
                        return new SingletonIterator(new Triple(s, pred, o));
                    } else {
                        return NullIterator.instance;
                    }
                }
            }
        } else {
            // No matching triples in this cache
            return NullIterator.instance;
        }
    }
    
    /**
     * Create a deep copy of the cache contents.
     * Works by creating a completely new cache and just adding in the
     * direct links.
     */
    public TransitiveGraphCache deepCopy() {
        TransitiveGraphCache copy = new TransitiveGraphCache(directPredicate, closedPredicate);
        Iterator i = find(new TriplePattern(null, directPredicate, null));
        while (i.hasNext()) {
            Triple t = (Triple)i.next();
            copy.addRelation(t.getSubject(), t.getObject());
        }
        return copy;
    }
    
    /**
     * Clear the entire cache contents. 
     */
    public void clear() {
        nodeMap.clear();
    }
	
    /**
     * Enable/disabling caching of the Triples representing the relationships. If this is
     * enabled then a number of triples quadratic in the graph depth will be stored. If it
     * is disabled then all queries will turn over storage dynamically creating the result triples.
     */
    public void setCaching(boolean enable) {
    	if (! enable && cacheTriples) {
    		// Switching off so clear the existing cache
    		for (Iterator i = nodeMap.values().iterator(); i.hasNext(); ) {
    			((GraphNode)i.next()).clearTripleCache();
    		}
    	}
    	cacheTriples = enable;
    }
    
    /**
     * Dump a description of the cache to a string for debug.
     */
    public String dump() {
    	StringBuffer sb = new StringBuffer();
    	for (Iterator i = nodeMap.values().iterator(); i.hasNext(); ) {
    		GraphNode n = (GraphNode)i.next();
    		sb.append(n.dump());
    		sb.append("\n");
    	}
    	return sb.toString();
    }
    
//  ----------------------------------------------------------------------
//  Internal utility methods    
//  ----------------------------------------------------------------------
    
    /**
     * Return the lead node of the strongly connected component corresponding
     * to the given RDF node. 
     */
    private GraphNode getLead(Node n) {
    	GraphNode gn = (GraphNode)nodeMap.get(n);
        if (gn == null) {
            gn = new GraphNode(n);
            nodeMap.put(n, gn);
            return gn;
        } else {
            return gn.leadNode();
        }
    }
    
}


/*
    (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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
