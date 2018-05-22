/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: RecordingListener.java,v 1.15 2007/01/02 11:50:07 andy_seaborne Exp $
*/

package com.hp.hpl.jena.graph.test;

import com.hp.hpl.jena.graph.*;

import junit.framework.*;
import java.util.*;

/**
    This testing listener records the event names and data, and provides
    a method for comparing the actual with the expected history. 
*/    
public class RecordingListener implements GraphListener
    {
    public List history = new ArrayList();
    
    public void notifyAddTriple( Graph g, Triple t )
        { record( "add", g, t ); }
        
    public void notifyAddArray( Graph g, Triple [] triples )
        { record( "add[]", g, triples ); }
        
    public void notifyAddList( Graph g, List triples )
        { record( "addList", g, triples ); }
        
    public void notifyAddIterator( Graph g, Iterator it )
        { record( "addIterator", g, GraphTestBase.iteratorToList( it ) ); }
        
    public void notifyAddGraph( Graph g, Graph added )
        { record( "addGraph", g, added ); }
        
    public void notifyDeleteTriple( Graph g, Triple t )
        { record( "delete", g, t ); }
        
    public void notifyDeleteArray( Graph g, Triple [] triples )
        { record( "delete[]", g, triples ); }
        
    public void notifyDeleteList( Graph g, List triples )
        { record( "deleteList", g, triples ); }
        
    public void notifyDeleteIterator( Graph g, Iterator it )
        { record( "deleteIterator", g, GraphTestBase.iteratorToList( it ) ); }
        
    public void notifyDeleteGraph( Graph g, Graph removed )
        { record( "deleteGraph", g, removed ); }
    
    public void notifyEvent( Graph source, Object event )
        { record( "someEvent", source, event ); }
        
    protected void record( String tag, Object x, Object y )
        { history.add( tag ); history.add( x ); history.add( y ); }
    
    protected void record( String tag, Object info )
        { history.add( tag ); history.add( info ); }
        
    public void clear()
        { history.clear(); }

    public boolean has( List things )
        { return history.equals( things ); } 
    
    public boolean hasStart( List L )
        { return L.size() <= history.size() && L.equals( history.subList( 0, L.size() ) ); }
    
    public boolean hasEnd( List L )
        { return L.size() <= history.size() && L.equals( history.subList( history.size() - L.size(), history.size() ) ); }
    
    public boolean has( Object [] things )
        { return has( Arrays.asList( things ) ); } 
        
    public void assertHas( List things )
        { if (has( things ) == false) Assert.fail( "expected " + things + " but got " + history ); }  
    
    public void assertHas( Object [] things )
        { assertHas( Arrays.asList( things ) ); }
    
    public void assertHasStart( Object [] start )
        { 
        List L = Arrays.asList( start );
        if (hasStart( L ) == false) Assert.fail( "expected " + L + " at the beginning of " + history );
        }
    
    public void assertHasEnd( Object [] end )
        {
        List L = Arrays.asList( end );
        if (hasEnd( L ) == false) Assert.fail( "expected " + L + " at the end of " + history );        
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