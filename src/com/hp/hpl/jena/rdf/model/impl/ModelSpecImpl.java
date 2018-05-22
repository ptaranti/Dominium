/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: ModelSpecImpl.java,v 1.64 2007/02/21 09:17:05 chris-dollin Exp $
*/
package com.hp.hpl.jena.rdf.model.impl;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.shared.*;

/**
    An abstract base class for implementations of ModelSpec. It provides the base 
    functionality of providing a ModelMaker (different sub-classes use this for different
    purposes) and utility methods for reading and creating RDF descriptions. It also
    provides a value table associating freshly-constructed bnodes with arbitrary Java
    values, so program-constructed specifications can pass on database connections,
    actual document managers, and so forth.
    
 	@author kers
    @deprecated ModelSpecs are dead
*/
public abstract class ModelSpecImpl implements ModelSpec
    {
    /**
        The ModelMaker that may be used by sub-classes.
    */
    protected ModelMaker maker;
    
    /**
        Initialise this ModelSpec with the supplied non-nullModeMaker.
        
        @param maker the ModelMaker to use
    */
    public ModelSpecImpl( ModelMaker maker )
        {
        if (maker == null) throw new RuntimeException( "null maker not allowed" );
        this.maker = maker; 
        }
    
    public static final Model emptyModel = ModelFactory.createDefaultModel();
    
    protected Model defaultModel = null;
    
    public static final Resource emptyResource = emptyModel.createResource();
    
    protected Model description = emptyModel;
    
    protected Resource root = ResourceFactory.createResource( "" );
        
    /**
        Answer a Model created according to this ModelSpec, with any required
        files loaded into it.
    */
    public final Model createFreshModel()
        { return loadFiles( doCreateModel() ); }
    
    /**
        Answer a Model created according to this ModelSpec; subclasses must 
        implement. The resulting model is returned by <code>createModel</code>
        after loading any files specified by jms:loadFile properties.
    */
    protected abstract Model doCreateModel();
    
    public Model createDefaultModel() 
        { if (defaultModel == null) defaultModel = makeDefaultModel();
        return defaultModel; }
    
    protected Model makeDefaultModel()
        {
        Statement s = root.getProperty( JenaModelSpec.modelName );
        return loadFiles( s == null ? maker.createDefaultModel() : maker.createModel( s.getString() ) );
        }
    
    /**
        Answer a Model created according to this ModelSpec and based on an underlying
        Model with the given name.
         
     	@see com.hp.hpl.jena.rdf.model.ModelSpec#createModelOver(java.lang.String)
     */
    public Model createModelOver( String name )
        { return loadFiles( implementCreateModelOver( name ) ); }
    
    public abstract Model implementCreateModelOver( String name );
   
    /**
     	Answer a Model, as per the specification of ModelSpec; appeal to 
        the sibling Maker.
    */
    public Model openModel( String name )
        { return loadFiles( maker.openModel( name ) ); }
    
    public Model openModel()
        {
        Statement s = root.getProperty( JenaModelSpec.modelName );
        return loadFiles( s == null ? maker.openModel() : maker.openModel( s.getString(), true ) );
        }
    
    /**
        Answer the model hidden in the sibling maker, if it has one, and
        null otherwise.
    */
    public Model openModelIfPresent( String name )
        { return maker.hasModel( name ) ? loadFiles( maker.openModel( name ) ) : null; }
        
    /**
        Answer the ModelMaker that this ModelSpec uses.
        @return the embedded ModelMaker
    */
    public ModelMaker getModelMaker()
        { return maker; }
        
    protected Model loadFiles( Model m )
        {
        StmtIterator it = description.listStatements( root, JenaModelSpec.loadWith, (RDFNode) null );
        while (it.hasNext()) loadFile( m, it.nextStatement().getResource() );
        return m;
        }

    protected Model loadFile( Model m, Resource file )
        { FileManager.get().readModel( m, file.getURI() ); 
        return m; }
    
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
    
    public Model getModel( String URL )
        { return null; }
    
    public Model getModel( String URL, ModelReader loadIfAbsent )
        { throw new CannotCreateException( URL ); }
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