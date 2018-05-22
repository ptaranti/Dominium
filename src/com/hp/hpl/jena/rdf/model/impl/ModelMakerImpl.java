/*
  (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: ModelMakerImpl.java,v 1.24 2007/01/02 11:48:30 andy_seaborne Exp $
*/

package com.hp.hpl.jena.rdf.model.impl;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.CannotCreateException;
import com.hp.hpl.jena.util.iterator.*;

/**
    A ModelMakerImpl implements a ModelMaker over a GraphMaker.
*/
public class ModelMakerImpl implements ModelMaker
    {
    protected GraphMaker maker;
    protected Model description;
    
    public ModelMakerImpl( GraphMaker maker )
        { this.maker = maker; }
        
    public GraphMaker getGraphMaker()
        { return maker; }
        
    public void close()
        { maker.close(); }
       
    public Model openModel()
        { return new ModelCom( maker.openGraph() ); }
    
    protected Model makeModel( Graph g )
        { return new ModelCom( g ); }
    
    public Model openModelIfPresent( String name )
        { return maker.hasGraph( name ) ? openModel( name ) : null; }
    
    public Model openModel( String name, boolean strict )
        { return makeModel( maker.openGraph( name, strict ) ); }
        
    public Model openModel( String name )
        { return openModel( name, false ); }
        
    public Model createModel( String name, boolean strict )
        { return makeModel( maker.createGraph( name, strict ) ); }
        
    public Model createModel( String name )
        { return createModel( name, false ); }
        
    public Model createModelOver( String name )
        { return createModel( name ); }
        
    public Model createFreshModel()
        { return makeModel( maker.createGraph() ); }
        
    public Model createDefaultModel()
        { return makeModel( maker.getGraph() ); }
        
    public Model getDescription()
        { 
        if (description == null) description = makeModel( maker.getDescription() );    
        return description; 
        }
        
    public Model getDescription( Resource root )
        { return makeModel( maker.getDescription( root.asNode() ) ); }
        
    public Model addDescription( Model m, Resource self )
        { return makeModel( maker.addDescription( m.getGraph(), self.asNode() ) ); }
        
    public void removeModel( String name )
        { maker.removeGraph( name ); }
        
    public boolean hasModel( String name )
        { return maker.hasGraph( name ); }
      
    public ExtendedIterator listModels()
        { return maker.listGraphs(); }
    
    /**
        @deprecated 
        @see com.hp.hpl.jena.rdf.model.ModelSource#getModel()
    */
    public Model getModel()
        { return createDefaultModel(); }
    
    /**
        @deprecated 
        @see com.hp.hpl.jena.rdf.model.ModelSource#createModel()
     */
    public Model createModel()
        { return createFreshModel(); }
    
    /**
        ModelGetter implementation component.     
    */
    public Model getModel( String URL )
        { return hasModel( URL ) ? openModel( URL ) : null; }         
    
    public Model getModel( String URL, ModelReader loadIfAbsent )
        { 
        Model already = getModel( URL );
        return already == null ? loadIfAbsent.readModel( createModel( URL ), URL ) : already;
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