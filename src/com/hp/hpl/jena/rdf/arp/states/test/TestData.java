/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.rdf.arp.states.test;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.arp.impl.AbsXMLContext;
import com.hp.hpl.jena.rdf.arp.impl.AttributeLexer;
import com.hp.hpl.jena.rdf.arp.impl.Names;
import com.hp.hpl.jena.rdf.arp.impl.URIReference;
import com.hp.hpl.jena.rdf.arp.impl.XMLBaselessContext;
import com.hp.hpl.jena.rdf.arp.impl.XMLContext;
import com.hp.hpl.jena.rdf.arp.impl.XMLHandler;
import com.hp.hpl.jena.rdf.arp.states.AbsXMLLiteral;
import com.hp.hpl.jena.rdf.arp.states.DAMLCollection;
import com.hp.hpl.jena.rdf.arp.states.FrameI;
import com.hp.hpl.jena.rdf.arp.states.HasSubjectFrameI;
import com.hp.hpl.jena.rdf.arp.states.InnerXMLLiteral;
import com.hp.hpl.jena.rdf.arp.states.LookingForRDF;
import com.hp.hpl.jena.rdf.arp.states.OuterXMLLiteral;
import com.hp.hpl.jena.rdf.arp.states.RDFCollection;
import com.hp.hpl.jena.rdf.arp.states.WantEmpty;
import com.hp.hpl.jena.rdf.arp.states.WantLiteralValueOrDescription;
import com.hp.hpl.jena.rdf.arp.states.WantPropertyElement;
import com.hp.hpl.jena.rdf.arp.states.WantTopLevelDescription;
import com.hp.hpl.jena.rdf.arp.states.WantTypedLiteral;
import com.hp.hpl.jena.rdf.arp.states.WantsObjectFrameI;

/**
 * For each state s, for each element-attribute event e1, - test s, e1 - if s,
 * e1 is not an error + test s, e1; Description; test s,e1, eg:prop; test s, e1,
 * end; for each element-attribute event e2 + test s, e1, e2 if s, e1, e2 is not
 * an error
 * 
 * @author Jeremy J. Carroll
 * 
 */
public class TestData implements ARPErrorNumbers{

    // TODO: not for 2.3. get rid of short names all together, not good idea.
    
    
    private static final URIReference foo = URIReference.createNoChecks("http://foo/");
    private static final URIReference bar = URIReference.createNoChecks("http://bar/");

    static TestHandler xmlHandler = new TestHandler();
//    static {
//        try {
//            xmlHandler.initParse("http://example.org/", "");
//        } catch (SAXParseException e) {
//           e.printStackTrace();
//        }
//        
//    }
    static String dataFile = "testing/arp/state.txt";
    static AbsXMLContext xmlContext;
    
    static { 
        try {
            xmlContext= new XMLBaselessContext(xmlHandler,
                    ERR_RESOLVING_AGAINST_RELATIVE_BASE).withBase(xmlHandler,"http://example.org/base/");
        } catch (SAXParseException e) {
            throw new RuntimeException(e);
        }
    }
    static TestFrame testFrame = new TestFrame(xmlHandler, xmlContext);
    
    static char white[] = { 32, 32, 32, 32, 32 };

    static char black[] = { 97, 98, 99, 100, 101 };

    private static final AttrEvent xmlSpace = new AttrEvent(QName.xml("space"));
    static Event allEvents[] = { 
           new ElementEvent(QName.rdf("li")),
            new ElementEvent(QName.rdf("Description")),
            new ElementEvent("F",QName.rdf("RDF")),
            new ElementEvent(QName.eg("Goo")),
            new AttrEvent(QName.xml("base")),
            new AttrEvent("g", QName.xml("lang"), "en"),
            new AttrEvent(QName.eg("foo")), 
            xmlSpace,
            new AttrEvent("B", QName.rdf("bagID"), "en"),
            new AttrEvent(QName.rdf("about")),
            new AttrEvent("h", QName.rdf("aboutEach"), "en"),
            new AttrEvent("H", QName.rdf("aboutEachPrefix"), "en"),
            new AttrEvent(QName.rdf("ID")), new AttrEvent(QName.rdf("nodeID")),
            new AttrEvent(QName.rdf("resource")),
            new AttrEvent(QName.rdf("type")),
            new AttrEvent(QName.rdf("datatype")),
            new AttrEvent("C", QName.rdf("parseType"), "Collection"),
            new AttrEvent("L", QName.rdf("parseType"), "Literal"),
            new AttrEvent("R", QName.rdf("parseType"), "Resource"),
            new InternalEvent("e", "</end>") {
                FrameI apply(FrameI from, Attributes att) throws SAXParseException {
                    from.endElement();
                    return from.getParent();
                }
            }, new InternalEvent("O", "object") {
                FrameI apply(FrameI from, Attributes att) {
                    ((WantsObjectFrameI) from).theObject(foo);
                    return from;
                }
            }, new InternalEvent("W", "white") {
                FrameI apply(FrameI from, Attributes att) throws SAXParseException {
                    from.characters(white, 0, 5);
                    return from;
                }
            }, new InternalEvent("Q", "'abcde'") {
                FrameI apply(FrameI from, Attributes att) throws SAXParseException {
                    from.characters(black, 0, 5);
                    return from;
                }
            }, 
            new InternalEvent("P", "pred-object") {
                FrameI apply(FrameI from, Attributes att) {
                    ((HasSubjectFrameI) from).aPredAndObj(foo,bar);
                    return from;
                }
            }, };

    static Map short2Event = new HashMap();
    static {
        for (int i=0;i<allEvents.length;i++) {
            String key = allEvents[i].oneChar;
            if (short2Event.get(key)!=null)
                System.err.println("Duplicate event code: "+key);
            short2Event.put(key,allEvents[i]);
        }
    }
    static Map state2Name = new HashMap();

    static Map state2ShortName = new HashMap();

    static Map shortName2State = new HashMap();

    static Map state2Args = new HashMap();

    static void add(String sh, String nm, Class f, Object args[]) {
        state2Name.put(f, nm);
        sh = getSimpleName(f);
        if (shortName2State.get(sh) != null) {
            System.err.println("Duplicate: " + sh);
        }
        state2Args.put(f, args);
        shortName2State.put(sh, f);
        state2ShortName.put(f, sh);
    }

    private static String getSimpleName(Class f) {
        return XMLHandler.getSimpleName(f);
    }

    static AttributeLexer ap = new AttributeLexer(testFrame, 0, 0);
    static {
        add("ix", "inner-xml-literal", InnerXMLLiteral.class,
                new Object[] { testFrame, "foo", testFrame.namespaces });
        add("xl", "xml-literal", OuterXMLLiteral.class, new Object[] {
                testFrame, xmlContext });
        add("ip", "vanilla-prop-elt", WantLiteralValueOrDescription.class,
                new Object[] { testFrame, xmlContext });
        add("tl", "typed-literal", WantTypedLiteral.class, new Object[] {
                testFrame, "http://ex/dt", xmlContext });
        add("dl", "daml:collection", DAMLCollection.class, new Object[] {
                testFrame, xmlContext });
        add("cl", "collection", RDFCollection.class, new Object[] {
                testFrame, xmlContext });
        add("tp", "top-level", WantTopLevelDescription.class,
                new Object[] { testFrame, ap });
        add("em", "empty-prop-elt", WantEmpty.class, new Object[] {
                testFrame, xmlContext });
        add("de", "inside-Description", WantPropertyElement.class,
                new Object[] { testFrame, xmlContext });
        add("RD", "looking-for-RDF", LookingForRDF.class, new Object[] {
                testFrame, ap });
    }


    int localCount;

    int globalCount;

    private EventList eventList = new EventList();

    public TestData() {
        super();
    }
//    String characters[] = {
//            "G",
//            "G e",
//            "G e G",
//            "Q",
//            "Q G",
//            "Q e",
//            "e",
//            "P",
//            "O",
//        };
    String characters[] = {
        "<eg:Goo>",
        "<eg:Goo> </end>",
        "<eg:Goo> </end> <eg:Goo>",
        "'abcde'",
        "'abcde' <eg:Goo>",
        "'abcde' </end>",
        "</end>",
        "pred-object",
        "object",
    };

    boolean inCharacterize = false;
    void characterize(Class f){
        inCharacterize = true;
        int sz = eventList.size;
        StringBuffer rslt = new StringBuffer();
        String skip = null;
        eventList.test(f);
        rslt.append(eventListName(f,null));
        rslt.append(" $ " + testInfo(f) + " {");
        if ( eventList.testResult.getClass() != LookingForRDF.class)
            
        for (int i=0;i<characters.length;i++) {
            if (skip != null && characters[i].startsWith(skip))
                continue;
            skip = null;
            addEvents(characters[i]);
            rslt.append( " " + characters[i]+ " $ ");
            boolean testV = eventList.test(f);
            rslt.append( testInfo(f) + " ;");
            eventList.size = sz;
            if ( !testV ) {
                skip = characters[i];
                continue;
            }
        }
        rslt.append(" }");
        data.add(rslt.toString());
        inCharacterize = false;
    }

    private String eventListName(Class f, Class f2) {
        StringBuffer rslt = new StringBuffer();
        rslt.append(stateName(f, f2));
        for (int i=0;i<eventList.size;i++) {
           rslt.append(' ');
           rslt.append(eventList.events[i].oneChar);
        }
        return rslt.toString();
    }

    private String stateName(Class f, Class f2) {
        return f==f2?"*":(String)state2ShortName.get(f);
    }

    private void addEvents(String string) {
        String all[] = string.split(" ");
        for (int i=0;i<all.length;i++){
           eventList.add((Event)short2Event.get(all[i]));   
        }
    }

    private String testInfo(Class f) {
        return 
        eventList.testFailure ? (eventList.testException ? "!" : "?") :
           (stateName(eventList.testResult.getClass(),f) + " " + 
                xmlHandler.info() + " " + testFrame.info());
    }

    static Class tryClasses[] = { FrameI.class, AbsXMLLiteral.class,
            HasSubjectFrameI.class, WantsObjectFrameI.class };

  
   static FrameI create(Class cl) throws InstantiationException, IllegalAccessException, InvocationTargetException {
       FrameI frame = null; 
       Object args[] = (Object[]) state2Args.get(cl);
        Class types[] = new Class[args.length];
        for (int i = 1; i < args.length; i++) {
            types[i] = args[i].getClass();
            if (types[i]==XMLContext.class)
                types[i] = AbsXMLContext.class;
        }
        if (cl == InnerXMLLiteral.class)
            types[2] = Map.class;
        for (int j = 0; j < tryClasses.length; j++) {
            types[0] = tryClasses[j];

            try {
                frame = (FrameI) cl.getConstructor(types).newInstance(args);
                break;
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        return frame;
    }
    
    void expand(Class f) {
        if (AbsXMLLiteral.class.isAssignableFrom(f))
            return;
        if (randomPurgeXMLAttrs())
            return;
        localCount++;
        globalCount++;
        if (localCount % 20000 == 0)
            stats(f);
        if (!eventList.test(f)) {
            if (!shorterTestFails(f)) 
                data.add(eventListName(f,null)+" $ " + testInfo(f));
            return;
        }
        characterize(f);
        if (eventList.size >= (AbsXMLLiteral.class.isAssignableFrom(f) ? 3 :
            eventList.testResult instanceof LookingForRDF ? 2
                : 8))
            return;
        for (int i = 0; i < allEvents.length; i++) {
            if (allEvents[i].isAttribute()) {
                Event e = eventList.last();
                if (!(e.isElement() || (e.isAttribute() && e.hashCode() < allEvents[i]
                        .hashCode())))
                    continue;
            } else if (true) {
                continue;
            }
            eventList.add(allEvents[i]);
            expand(f);
            eventList.pop();
        }
    }
    
    private Random dice = new Random(23);
    
    private boolean randomPurgeXMLAttrs() {
        int weight = 0;
        eventList.rewind();
        while (eventList.hasNext()) {
            Event e = eventList.next();
            if ( e==xmlSpace)
                weight += 2;
            else if ( e.isAttribute() && ((AttrEvent)e).q.uri.equals(Names.xmlns) )
                weight ++;
        }
        while (weight-- >0)
            if (dice.nextBoolean())
                return true;
        return false;
    }

    private boolean shorterTestFails(Class f) {
        if (eventList.size <= 2)
            return false;
          for (int i=1;i<eventList.size-1;i++){
              EventList copy = eventList.copy();
              copy.delete(i);
              if (!copy.test(f))
                  return true;
          }
          return false;
    }

    Set data = new TreeSet(new Comparator(){
        public int compare(Object arg1, Object arg2) {
            StringBuffer b1 = new StringBuffer((String)arg1).reverse();
            StringBuffer b2 = new StringBuffer((String)arg2).reverse();
            return b1.toString().compareTo(b2.toString());
        }}
            );

    void stats(Class f) {
        if (false)
        System.out.println(state2ShortName.get(f) + ":" + state2Name.get(f)
                + ":" + getSimpleName(f) + "  " + localCount + "/"
                + globalCount);

    }

    void test1() throws IOException {
        Iterator it = state2Name.keySet().iterator();
        while (it.hasNext()) {
            Class f = (Class) it.next();
//            System.out.println(state2ShortName.get(f) + ":" + state2Name.get(f)
//                    + ":" + f.getSimpleName());
            localCount = 0;
            for (int i = 0; i < allEvents.length; i++) {
                if (allEvents[i].isElement()) {
                    eventList.clear();
                    eventList.add(allEvents[i]);
                    expand(f);
                }
            }
            stats(f);
        }
        FileWriter fw = new FileWriter(dataFile);
        it = data.iterator();
        while (it.hasNext()) {
            fw.write((String)it.next());
            fw.write('\n');
        }
        fw.close();
    }

    static public void main(String args[]) throws IOException {
        long start = System.currentTimeMillis();
        new TestData().test1();
        System.out.println((System.currentTimeMillis()-start) + " ms");
    }

    public static String stateLongName(String sh) {
        return (String)state2Name.get(shortName2State.get(sh));
    }

    public static Class toState(String sh) {
        return (Class)shortName2State.get(sh);
    }

}

/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
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
 */

