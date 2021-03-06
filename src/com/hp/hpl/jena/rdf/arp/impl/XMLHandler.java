/*
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP All rights
 * reserved.
 * 
 * (c) Copyright 2003, Plugged In Software 
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * $Id: XMLHandler.java,v 1.29 2007/01/11 11:52:45 jeremy_carroll Exp $
 * 
 * AUTHOR: Jeremy J. Carroll
 */
/*
 * ARPFilter.java
 * 
 * Created on June 21, 2001, 10:01 PM
 */

package com.hp.hpl.jena.rdf.arp.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.arp.ARPHandlers;
import com.hp.hpl.jena.rdf.arp.ARPOptions;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.ExtendedHandler;
import com.hp.hpl.jena.rdf.arp.FatalParsingErrorException;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.rdf.arp.StatementHandler;
import com.hp.hpl.jena.rdf.arp.states.Frame;
import com.hp.hpl.jena.rdf.arp.states.FrameI;
import com.hp.hpl.jena.rdf.arp.states.LookingForRDF;
import com.hp.hpl.jena.rdf.arp.states.StartStateRDForDescription;

/**
 * This class converts SAX events into a stream of encapsulated events suitable
 * for the RDF parser. In effect, this is the RDF lexer. updates by kers to
 * handle exporting namespace prefix maps.
 * 
 * @author jjc
 */
public class XMLHandler extends LexicalHandlerImpl implements ARPErrorNumbers,
        Names {

    boolean encodingProblems = false;

    protected Map idsUsed = new HashMap();
    protected int idsUsedCount = 0;

    public void triple(ANode s, ANode p, ANode o) {
        StatementHandler stmt;
        boolean bad=s.isTainted() || p.isTainted() || o.isTainted();
        if (bad) {
            stmt = badStatementHandler;
        } else {
            stmt = handlers.getStatementHandler();
        }
        AResourceInternal subj = (AResourceInternal) s;
        AResourceInternal pred = (AResourceInternal) p;
        if (!bad)
            subj.setHasBeenUsed();
        if (o instanceof AResource) {
            AResourceInternal obj = (AResourceInternal) o;
            if (!bad) obj.setHasBeenUsed();
            stmt.statement(subj, pred, obj);
        } else
            stmt.statement(subj, pred, (ALiteral) o);
    }

    // This is the current frame.
    FrameI frame;

    public void startPrefixMapping(String prefix, String uri)
            throws SAXParseException {
        checkNamespaceURI(uri);
        handlers.getNamespaceHandler().startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) {
        handlers.getNamespaceHandler().endPrefixMapping(prefix);
    }

    public Locator getLocator() {
        return locator;
    }

    Locator locator;

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    static final private boolean DEBUG = false;

    public void startElement(String uri, String localName, String rawName,
            Attributes atts) throws SAXException {
        if (Thread.interrupted())
            warning(null, ERR_INTERRUPTED, "Interrupt detected.");
        FrameI oldFrame = frame;
        frame = frame.startElement(uri, localName, rawName, atts);
        if (DEBUG)
            System.err.println("<" + rawName + "> :: "
                    + getSimpleName(oldFrame.getClass()) + " --> "
                    + getSimpleName(frame.getClass()));
    }

    public void endElement(String uri, String localName, String rawName)
            throws SAXException {
        frame.endElement();
        frame = frame.getParent();
        frame.afterChild();
        if (DEBUG)
            System.err.println("</" + rawName + "> :: <--"
                    + getSimpleName(frame.getClass()));
    }

    static public String getSimpleName(Class c) {
        String rslt[] = c.getName().split("\\.");
        return rslt[rslt.length - 1];
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        frame.characters(ch, start, length);
    }

    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException { // Never called.
        characters(ch, start, length);
    }

    void setUserData(String nodeId, Object v) {
        nodeIdUserData.put(nodeId, v);
    }

    Object getUserData(String nodeId) {
        return nodeIdUserData.get(nodeId);
    }

    public void comment(char[] ch, int start, int length)
            throws SAXParseException {
        frame.comment(ch, start, length);
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
        frame.processingInstruction(target, data);
    }

    public void warning(Taint taintMe,int id, String msg) throws SAXParseException {
        if (options.getErrorMode(id) != EM_IGNORE)
            warning(taintMe,id, location(), msg);
    }

    void warning(Taint taintMe, int id, Location loc, String msg) throws SAXParseException {
        if (options.getErrorMode(id) != EM_IGNORE)
            warning(taintMe, id, new ParseException(id, loc, msg) {
                private static final long serialVersionUID = 1990910846204964756L;
            });
    }

    void generalError( int id, Exception e) throws SAXParseException {
        Location where = new Location(locator);
        // System.err.println(e.getMessage());
        warning(null, id, new ParseException(id, where, e));

    }

    void warning(Taint taintMe, int id, SAXParseException e) throws SAXParseException {
        try {
            switch (options.getErrorMode(id)) {
            case EM_IGNORE:
                break;
            case EM_WARNING:
                handlers.getErrorHandler().warning(e);
                break;
            case EM_ERROR:
                if (taintMe != null)
                    taintMe.taint();
                handlers.getErrorHandler().error(e);
                break;
            case EM_FATAL:
                handlers.getErrorHandler().fatalError(e);
                break;
            }
        } catch (SAXParseException xx) {
            throw xx;
        } catch (SAXException ee) {
            throw new WrappedException(ee);
        }
        if (e instanceof ParseException && ((ParseException) e).isPromoted())
            throw e;
        if (options.getErrorMode(id) == EM_FATAL) {
            // If we get here, we shouldn't go on
            // throw an error into Jena.
            throw new FatalParsingErrorException();

        }
    }

    public void error(SAXParseException e) throws SAXParseException {
        warning(null,ERR_SAX_ERROR, e);
    }

    public void warning(SAXParseException e) throws SAXParseException {
        warning(null,WARN_SAX_WARNING, e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
        warning(null,ERR_SAX_FATAL_ERROR, e);
        // If we get here, we shouldn't go on
        // throw an error into Jena.
        throw new FatalParsingErrorException();

    }

    /**
     * @param v
     */
    public void endLocalScope(ANode v) {
        if (handlers.getExtendedHandler() != nullScopeHandler) {
            ARPResource bn = (ARPResource) v;
            if (!bn.getHasBeenUsed())
                return;
            if (bn.hasNodeID()) {
                // save for later end scope
                if (handlers.getExtendedHandler().discardNodesWithNodeID())
                    return;
                String bnodeID = bn.nodeID;
                if (!nodeIdUserData.containsKey(bnodeID))
                    nodeIdUserData.put(bnodeID, null);
            } else {
                handlers.getExtendedHandler().endBNodeScope(bn);
            }
        }
    }

    public void endRDF() {
        handlers.getExtendedHandler().endRDF();
    }

    public void startRDF() {
        handlers.getExtendedHandler().startRDF();
    }

    boolean ignoring(int eCode) {
        return options.getErrorMode(eCode) == EM_IGNORE;
    }
    
    public boolean isError(int eCode) {
        return options.getErrorMode(eCode) == EM_ERROR;
    }

    protected AbsXMLContext initialContext(String base, String lang)
            throws SAXParseException {
        return initialContextWithBase(base).withLang(this,lang);
    }

    private boolean allowRelativeReferences = false;
    
    private AbsXMLContext initialContextWithBase(String base) throws SAXParseException {
        allowRelativeReferences = false;
            if (base == null) {
                warning(null,IGN_NO_BASE_URI_SPECIFIED,
                        "Base URI not specified for input file; local URI references will be in error.");

                return new XMLBaselessContext(this,
                        ERR_RESOLVING_URI_AGAINST_NULL_BASE);

            } else if (base.equals("")) {
                allowRelativeReferences = true;
                warning(null,IGN_NO_BASE_URI_SPECIFIED,
                        "Base URI specified as \"\"; local URI references will not be resolved.");
                return new XMLBaselessContext(this,
                        WARN_RESOLVING_URI_AGAINST_EMPTY_BASE);
            } else {
//                if (base.toLowerCase().startsWith("file:")
//                    && base.length()>5
//                    && base.charAt(5) != '/'
//                ) {
//                    System.err.print(base);
//                    try {
//                        base = new File(base.substring(5)).toURL().toString();
//                        if (base.length()<=6
//                                || base.charAt(6)!= '/')
//                            base = "file://"+base.substring(5);
//                    } catch (MalformedURLException e) {
//                        // ignore, just leave it alone.
//                    }
//                    System.err.println(" ==> "+base);
//                    
//                }
                return new XMLBaselessContext(this,
                        ERR_RESOLVING_AGAINST_RELATIVE_BASE).withBase(this,base);
            }
    }
    /*
    private XMLContext initialContextWithBasex(String base)
            throws SAXParseException {
        XMLContext rslt = new XMLContext(this, base);
        RDFURIReference b = rslt.getURI();
        if (base == null) {
            warning(null,IGN_NO_BASE_URI_SPECIFIED,
                    "Base URI not specified for input file; local URI references will be in error.");

        } else if (base.equals("")) {
            warning(null,IGN_NO_BASE_URI_SPECIFIED,
                    "Base URI specified as \"\"; local URI references will not be resolved.");

        } else {
            checkBadURI(null,b);
            // Warnings on bad base.

            // if (b.isVeryBad()||b.isRelative()) {
            // return
        }

        return rslt;
    }
    */

    private ARPOptions options = new ARPOptions();

    private ARPHandlers handlers = new ARPHandlers();

    StatementHandler getStatementHandler() {
        return handlers.getStatementHandler();
    }

    public ARPHandlers getHandlers() {
        return handlers;
    }

    public ARPOptions getOptions() {
        return options;
    }

    public void setOptionsWith(ARPOptions newOpts) {
           options = newOpts.copy();
        
    }

    public void setHandlersWith(ARPHandlers newHh) {
        handlers = new ARPHandlers();
        handlers.setErrorHandler(newHh.getErrorHandler());
        handlers.setExtendedHandler(newHh.getExtendedHandler());
        handlers.setNamespaceHandler(newHh.getNamespaceHandler());
        handlers.setStatementHandler(newHh.getStatementHandler());
       
    }

    private Map nodeIdUserData;

    public void initParse(String base, String lang) throws SAXParseException {
        nodeIdUserData = new HashMap();
        idsUsed = 
        	ignoring(WARN_REDEFINITION_OF_ID)?
        			null:
        	        new HashMap();
        idsUsedCount = 0;
        if (options.getEmbedding())
            frame = new LookingForRDF(this, initialContext(base, lang));
        else
            frame = new StartStateRDForDescription(this, initialContext(base,
                    lang));

    }

    /**
     * This method must be always be called after parsing, e.g. in a finally
     * block.
     * 
     */
    void afterParse() {
        while (frame != null) {
            frame.abort();
            frame = frame.getParent();
        }
        // endRDF();
        endBnodeScope();
        idsUsed = null;
    }

    void endBnodeScope() {
        if (handlers.getExtendedHandler() != nullScopeHandler) {
            Iterator it = nodeIdUserData.keySet().iterator();
            while (it.hasNext()) {
                String nodeId = (String) it.next();
                ARPResource bn = new ARPResource(this, nodeId);
                handlers.getExtendedHandler().endBNodeScope(bn);
            }
        }
    }

    public Location location() {
        return new Location(locator);
    }

    private IRIFactory factory = IRIFactory.jenaImplementation();

    IRIFactory iriFactory() {
        if (factory == null) {
            
            // TODO locator stuff
//            factory = new IRIFactory();
//            factory.useSpecificationRDF(false);
            /*
            if (locator != null)
                factory = new IRIFactory(locator);
            else
                factory = new IRIFactory(new Locator() {

                    public int getColumnNumber() {
                        return locator == null ? -1 : locator.getColumnNumber();
                    }

                    public int getLineNumber() {
                        return locator == null ? -1 : locator.getLineNumber();
                    }

                    public String getPublicId() {
                        return locator == null ? null : locator.getPublicId();
                    }

                    public String getSystemId() {
                        return locator == null ? null : locator.getSystemId();
                    }

                });
              */
        }
        return factory;
    }

    private void checkNamespaceURI(String uri) throws SAXParseException {
        ((Frame) frame).checkEncoding(null,uri);
        if (uri.length() != 0)
             {
                IRI u = iriFactory().create(uri);
//                if (u.isVeryBad()) {
//                    warning(null,
//                            WARN_BAD_NAMESPACE_URI,
//                            "The namespace URI: <"
//                                    + uri
//                                    + "> is not well formed.");
//                    return;
//                 
//                }
                if (!u.isAbsolute()) {
                    warning(null,
                            WARN_RELATIVE_NAMESPACE_URI_DEPRECATED,
                            "The namespace URI: <"
                                    + uri
                                    + "> is relative. Such use has been deprecated by the W3C, and may result in RDF interoperability failures. Use an absolute namespace URI.");
                }
                try {
                    if (!u.toASCIIString().equals(u.toString()))
                        warning(null,
                                WARN_BAD_NAMESPACE_URI,
                                "Non-ascii characters in a namespace URI may not be completely portable: <"
                                        + u.toString()
                                        + ">. Resulting RDF URI references are legal.");
                } catch (MalformedURLException e) {
                    warning(null,
                            WARN_BAD_NAMESPACE_URI,
                            "toAscii failed for namespace URI: <"
                                    + u.toString()
                                    + ">. " + e.getMessage());
              } 

                if (uri.startsWith(rdfns) && !uri.equals(rdfns))
                    warning(null,WARN_BAD_RDF_NAMESPACE_URI, "Namespace URI ref <"
                            + uri + "> may not be used in RDF/XML.");
                if (uri.startsWith(xmlns) && !uri.equals(xmlns))
                    warning(null,WARN_BAD_XML_NAMESPACE_URI, "Namespace URI ref <"
                            + uri + "> may not be used in RDF/XML.");
             }   
    }

    public boolean allowRelativeURIs() {
        return allowRelativeReferences;
    }
    private IRI sameDocRef;
    public IRI sameDocRef() {
        if (sameDocRef==null){
            sameDocRef = iriFactory().create("");
        }
        return sameDocRef;
    }

    private StatementHandler badStatementHandler = nullStatementHandler;
    
    public void setBadStatementHandler(StatementHandler sh) {
        badStatementHandler = sh;
    }

    final public static StatementHandler nullStatementHandler =
    new StatementHandler() {
        public void statement(AResource s, AResource p, AResource o) {
        }
        public void statement(AResource s, AResource p, ALiteral o) {
        }
    };
    final public static ExtendedHandler nullScopeHandler = new ExtendedHandler() {
        
        public void endBNodeScope(AResource bnode) {
        }

        public void startRDF() {
        }

        public void endRDF() {
        }

        public boolean discardNodesWithNodeID() {
            return true;
        }
    };
}
