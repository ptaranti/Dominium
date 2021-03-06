/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.rdf.arp;


/**
 * The interface to set the various options on ARP.
 *  User defined
 * implementations of this interface are not supported. This is a class rather
 * than an interface to have better backward compatibilitiy with earlier
 * versions, however constructing instances of this class is deprecated.
 * In addition, accessing the fields of {@link ARPErrorNumbers} through this
 * class is not supported. The inheritance of this interface will be removed.
 * 
 * @author Jeremy J. Carroll
 *
 */
public class ARPOptions implements ARPErrorNumbers {
    
    /**
     * Do not use this constructor.
     * An example of not using this constructor is as follows.
     * <br/>
     * Deprecated usage:
     * <br/>
     * <pre>
        ARP arp = new ARP();
        ARPOptions options = new ARPOptions();
     </pre>
     <br/>
     * Preferred code:
     * <br/>
     * <pre>
        ARP arp = new ARP();
        ARPOptions options = arp.getOptions();
     </pre>
     *@deprecated Use {@link ARPConfig#getOptions()}
     */
    public ARPOptions() {
        
    }
    private static int defaultErrorMode[] = new int[400];
    static {
        for (int i = 0; i < defaultErrorMode.length; i++)
            defaultErrorMode[i] = i / 100;
        
    }
    private boolean embedding = false;
    private int errorMode[] = (int[]) defaultErrorMode.clone();

    /** Sets or gets the error handling mode for a specific error condition.
     * Changes that cannot be honoured are silently ignored.
     * Illegal error numbers may result in an ArrayIndexOutOfBoundsException but
     * are usually ignored.
     * Most conditions are associated with one or more specific resources or literals
     * formed during the parse. 
     * Triples involving resource or literal associated with an error condition 
     * are not produced.
     * The precise definition of 'associated with' is deliberately 
     * undefined, and may change in future releases.
     * This method can be used to downgrade an error condition to 
     * a warning, or to upgrade a warning to an error.
     * Such a change modifies which triples are produced.
     * <p>
     * 
     * When the condition is a violation of the RDF/XML Syntax (Revised) Recommendations, 
     * and the error mode is {@link ARPErrorNumbers#EM_IGNORE} or  {@link ARPErrorNumbers#EM_WARNING},
     * the precise rules which ARP uses to generate triples for such ill-formed input are 
     * not defined by any standard and are subject to change with future releases.
     * For input involving no errors, ARP creates triples in accordance with 
     * the RDF/XML Syntax Revised Recommendation. 
     * <p>
     * 
     * The mode can have one of the following four values.
     * 
     * <dl>
     * <dt>{@link ARPErrorNumbers#EM_IGNORE}</dt>
     * <dd>Ignore this condition. Produce triples.</dd>
     * <dt>{@link ARPErrorNumbers#EM_WARNING}</dt>
     * <dd>Invoke ErrorHandler.warning() for this condition. Produce triples.</dd>
     * <dt>{@link ARPErrorNumbers#EM_ERROR}</dt>
     * <dd>Invoke ErrorHandler.error() for this condition. Do not produce triples.</dd>
     * <dt>{@link ARPErrorNumbers#EM_FATAL}</dt>
     * <dd>Aborts parse and invokes ErrorHandler.fatalError() for this condition.
     * Do not produce triples.
     * In unusual situations, a few further warnings and errors may be reported.
     * </dd>
     * </dl>
     * 
     * 
     * @param errno The specific error condition to change.
     * @param mode The new mode for this condition.
     * @return The old error mode for this condition.
     */
    public int setErrorMode(int errno, int mode) {
        int old = errorMode[errno];
        errorMode[errno] = mode;
        return old;
    }

    /** Resets error mode to the default values:
     * many errors are reported as warnings, and resulting triples are produced.
     */
    public void setDefaultErrorMode() {
        errorMode = (int[]) defaultErrorMode.clone();
    }

    /** As many errors as possible are ignored.
     * As many triples as possible are produced.
     */
    public void setLaxErrorMode() {
        setDefaultErrorMode();
        for (int i = 100; i < 200; i++)
            setErrorMode(i, EM_IGNORE);
    }

    /** This sets strict conformance to the W3C Recommendations.
     */
    public void setStrictErrorMode() {
        setStrictErrorMode(EM_IGNORE);
    }

    /**
     * This method detects and prohibits errors according to
     *the W3C Recommendations.
     * For other conditions, such as 
     {@link ARPErrorNumbers#WARN_PROCESSING_INSTRUCTION_IN_RDF}, nonErrorMode is used. 
     *@param nonErrorMode The way of treating non-error conditions.
     */
    public void setStrictErrorMode(int nonErrorMode) {
        setDefaultErrorMode();
        for (int i = 1; i < 100; i++)
            setErrorMode(i, nonErrorMode);
        int warning = EM_WARNING;
        int error = EM_ERROR;
        switch (nonErrorMode) {
            case EM_ERROR :
                warning = EM_ERROR;
                break;
            case EM_FATAL :
                warning = error = EM_FATAL;
                break;
        }
        for (int i = 100; i < 200; i++)
            setErrorMode(i, error);
        // setErrorMode(IGN_XMLBASE_USED,warning);
        // setErrorMode(IGN_XMLBASE_SIGNIFICANT,error);
        setErrorMode(WARN_DEPRECATED_XMLLANG, warning);
        setErrorMode(WARN_STRING_NOT_NORMAL_FORM_C, warning);
        //       setErrorMode(WARN_EMPTY_ABOUT_EACH,nonErrorMode);
        setErrorMode(WARN_UNKNOWN_PARSETYPE, warning);
        //     setErrorMode(WARN_BAD_XML, nonErrorMode);
        setErrorMode(WARN_PROCESSING_INSTRUCTION_IN_RDF, nonErrorMode);
//      setErrorMode(WARN_LEGAL_REUSE_OF_ID, nonErrorMode);
        setErrorMode(WARN_RDF_NN_AS_TYPE, nonErrorMode);
        setErrorMode(WARN_UNKNOWN_RDF_ELEMENT, warning);
        setErrorMode(WARN_UNKNOWN_RDF_ATTRIBUTE, warning);
        setErrorMode(WARN_UNQUALIFIED_RDF_ATTRIBUTE, warning);
        setErrorMode(WARN_UNKNOWN_XML_ATTRIBUTE, nonErrorMode);
        setErrorMode(WARN_NOT_RDF_NAMESPACE,nonErrorMode);
        // setErrorMode(WARN_QNAME_AS_ID, error);
        //      setErrorMode(WARN_BAD_XML, error);
        setErrorMode(WARN_SAX_WARNING, warning);
        setErrorMode(IGN_DAML_COLLECTION, error);
    }

    /**
     * Copies this object.
     * @return A copy.
     * @deprecated Not intended for public use, will be removed from API
     */
    public ARPOptions copy() {
    	ARPOptions rslt = new ARPOptions();
    	rslt.errorMode = (int[])errorMode.clone() ;
    	rslt.embedding = embedding;
    	return rslt;
    }

    /** Sets whether the XML document is only RDF, or contains RDF embedded in other XML.
     * The default is non-embedded mode.
     * Embedded mode also matches RDF documents that use the
     * rdf:RDF tag at the top-level.
     * Non-embeded mode matches RDF documents which omit that optional tag, and consist of a single rdf:Description or
     * typed node.
     * To find embedded RDF it is necessary to setEmbedding(true).
     * @param embed true: Look for embedded RDF; or false: match a typed node or rdf:Description against the whole document (the default).
     * @return Previous setting.
     */

    public boolean setEmbedding(boolean embed) {
        boolean old = embedding;
        embedding = embed;
        return old;
    }

    /**
     * Returns the error mode for the given error code.
     * @param eCode
     * @return One of {@link ARPErrorNumbers#EM_IGNORE},
     * {@link ARPErrorNumbers#EM_WARNING},
     * {@link ARPErrorNumbers#EM_ERROR},
     * {@link ARPErrorNumbers#EM_FATAL}
     */
    public int getErrorMode(int eCode) {
    		return errorMode[eCode];
    }

    /**
     * True if the embedding flag is set.
     * Indicates that the parser should look for rdf:RDF
     * element, rather than treat the whole file as an RDF/XML
     * document (possibly without rdf:RDF element).
     */
    public boolean getEmbedding() {
    	return embedding;
    }

}

/*
 *  (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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

