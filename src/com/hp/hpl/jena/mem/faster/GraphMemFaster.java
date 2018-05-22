/*
 	(c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: GraphMemFaster.java,v 1.15 2007/01/02 11:52:40 andy_seaborne Exp $
*/

package com.hp.hpl.jena.mem.faster;

import java.util.Iterator;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.Reifier.Util;
import com.hp.hpl.jena.graph.impl.*;
import com.hp.hpl.jena.graph.query.*;
import com.hp.hpl.jena.mem.*;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class GraphMemFaster extends GraphMemBase
    {
    public GraphMemFaster()
        { this( ReificationStyle.Minimal ); }
    
    public GraphMemFaster( ReificationStyle style )
        { super( style ); }

    protected TripleStore createTripleStore()
        { return new FasterTripleStore( this ); }

    protected void destroy()
        { store.close(); }

    public void performAdd( Triple t )
        { if (!getReifier().handledAdd( t )) store.add( t ); }

    public void performDelete( Triple t )
        { if (!getReifier().handledRemove( t )) store.delete( t ); }

    public int graphBaseSize()  
        { return store.size(); }
    
    public QueryHandler queryHandler()
        { 
        if (queryHandler == null) queryHandler = new GraphMemFasterQueryHandler( this );
        return queryHandler;
        }
    
    /**
         Answer an ExtendedIterator over all the triples in this graph that match the
         triple-pattern <code>m</code>. Delegated to the store.
     */
    public ExtendedIterator graphBaseFind( TripleMatch m ) 
        { return store.find( m.asTriple() ); }

    public Applyer createApplyer( ProcessedTriple pt )
        { 
        Applyer plain = ((FasterTripleStore) store).createApplyer( pt ); 
        return matchesReification( pt ) && hasReifications() ? withReification( plain, pt ) : plain;
        }

    protected boolean hasReifications()
        { return reifier != null && reifier.size() > 0; }

    public static boolean matchesReification( QueryTriple pt )
        {
        return 
            pt.P.node.isVariable()
            || Util.isReificationPredicate( pt.P.node )
            || Util.isReificationType( pt.P.node, pt.O.node )
            ;
        }
    
    protected Applyer withReification( final Applyer plain, final QueryTriple pt )
        {
        return new Applyer() 
            {
            public void applyToTriples( Domain d, Matcher m, StageElement next )
                {
                plain.applyToTriples( d, m, next );
                Triple tm = new Triple
                    ( pt.S.finder( d ), pt.P.finder( d ), pt.O.finder( d ) );
                Iterator it = reifier.findExposed( tm );
                while (it.hasNext())
                    if (m.match( d, (Triple) it.next() )) next.run( d );
                }
            };
        }

    /**
         Answer true iff this graph contains <code>t</code>. If <code>t</code>
         happens to be concrete, then we hand responsibility over to the store.
         Otherwise we use the default implementation.
    */
    public boolean graphBaseContains( Triple t )
        { return t.isConcrete() ? store.contains( t ) : super.graphBaseContains( t ); }
    
    /**
        Clear this GraphMem, ie remove all its triples (delegated to the store).
    */
    public void clear()
        { 
        store.clear(); 
        ((SimpleReifier) getReifier()).clear();
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