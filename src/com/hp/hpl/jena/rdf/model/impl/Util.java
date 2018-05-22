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
 * Util.java
 *
 * Created on 01 August 2000, 16:31
 */

package com.hp.hpl.jena.rdf.model.impl;
import java.util.regex.*;

import org.apache.xerces.util.XMLChar;

import com.hp.hpl.jena.shared.*;

/** Some utility functions.
 *
 * @author  bwm
 * @version   Release='$Name:  $' Revision='$Revision: 1.13 $' Date='$Date: 2007/04/16 15:28:19 $'
 */
public class Util extends Object {

    public static final String CLASSPATH = "com.hp.hpl.jena";

    /** Given an absolute URI, determine the split point between the namespace part
     * and the localname part.
     * If there is no valid localname part then the length of the
     * string is returned.
     * The algorithm tries to find the longest NCName at the end
     * of the uri, not immediately preceeded by the first colon
     * in the string.
     * @param uri
     * @return the index of the first character of the localname
     */
    public static int splitNamespace(String uri) {
        char ch;
        int lg = uri.length();
        if (lg == 0)
            return 0;
        int j;
        int i;
        for (i = lg - 1; i >= 1; i--) {
            ch = uri.charAt(i);
            if (notNameChar(ch)) break;
        }
        for (j = i + 1; j < lg; j++) {
            ch = uri.charAt(j);
            if (XMLChar.isNCNameStart(ch)) {
                if (uri.charAt(j - 1) == ':'
                    && uri.lastIndexOf(':', j - 2) == -1)
                    continue; // split "mailto:me" as "mailto:m" and "e" !
                else
                    break;
            }
        }
        return j;
    }

    /**
	    answer true iff this is not a legal NCName character, ie, is
	    a possible split-point start.
    */
    public static boolean notNameChar( char ch )
        { return !XMLChar.isNCName( ch ); }

    protected static Pattern standardEntities = 
    	   Pattern.compile( "&|<|>|\t|\n|\r|\'|\"" );
    
    public static String substituteStandardEntities( String s )
        {
        if (standardEntities.matcher( s ).find())
            {
            return substituteEntitiesInElementContent( s )
                .replaceAll( "'", "&apos;" )
                .replaceAll( "\t","&#9;" )
                .replaceAll( "\n", "&#xA;" )
                .replaceAll( "\r", "&#xD;" )
                .replaceAll( "\"", "&quot;" )
                ;
            }
        else
            return s;
        }
    
    protected static Pattern entityValueEntities = 
 	   Pattern.compile( "&|%|\'|\"" );
 
   public static String substituteEntitiesInEntityValue( String s )
     {
     if (entityValueEntities.matcher( s ).find())
         {
         return s
             .replaceAll( "&","&amp;" )
             .replaceAll( "'", "&apos;" )
             .replaceAll( "%", "&#37;" )
             .replaceAll( "\"", "&quot;" )
             ;
         }
     else
         return s;
     }
    protected static Pattern elementContentEntities = Pattern.compile( "<|>|&|[\0-\37&&[^\n\r\t]]|\uFFFF|\uFFFE" );
    /**
        Answer <code>s</code> modified to replace &lt;, &gt;, and &amp; by
        their corresponding entity references. 
        
    <p>
        Implementation note: as a (possibly misguided) performance hack, 
        the obvious cascade of replaceAll calls is replaced by an explicit
        loop that looks for all three special characters at once.
    */
    public static String substituteEntitiesInElementContent( String s ) 
        {
        Matcher m = elementContentEntities.matcher( s );
        if (!m.find())
            return s;
        else
            {
            int start = 0;
            StringBuffer result = new StringBuffer();
            do
                {
                result.append( s.substring( start, m.start() ) );
                char ch = s.charAt( m.start() );
                switch ( ch )
                    {
                    case '<': result.append( "&lt;" ); break;
                    case '&': result.append( "&amp;" ); break;
                    case '>': result.append( "&gt;" ); break;
                    default: throw new CannotEncodeCharacterException( ch, "XML" );
                    }
                start = m.end();
                } while (m.find( start ));
            result.append( s.substring( start ) );
            return result.toString();
            }
        }

    public static String replace(
        String s,
        String oldString,
        String newString) {
        String result = "";
        int length = oldString.length();
        int pos = s.indexOf(oldString);
        int lastPos = 0;
        while (pos >= 0) {
            result = result + s.substring(lastPos, pos) + newString;
            lastPos = pos + length;
            pos = s.indexOf(oldString, lastPos);
        }
        return result + s.substring(lastPos, s.length());
    }

    /** Call System.getProperty and suppresses SecurityException, (simply returns null).
     *@return The property value, or null if none or there is a SecurityException.
     */
    public static String XgetProperty(String p) {
        return XgetProperty( p, null );
    }
    /** Call System.getProperty and suppresses SecurityException, (simply returns null).
     *@return The property value, or null if none or there is a SecurityException.
     */
    public static String XgetProperty(String p, String def) {
        try {
            return System.getProperty(p, def);
        } catch (SecurityException e) {
            return def;
        }
    }

}
