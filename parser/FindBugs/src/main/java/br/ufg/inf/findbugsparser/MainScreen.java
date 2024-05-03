/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.inf.findbugsparser;

import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author suporte
 */
public class MainScreen {

    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                // Parse the data, filtering out the start elements
                XMLInputFactory xmlif = XMLInputFactory.newInstance();
                FileReader fr = new FileReader(args[0]);
                XMLEventReader xmler = xmlif.createXMLEventReader(fr);
                EventFilter filter = new EventFilter() {
                    @Override
					public boolean accept(XMLEvent event) {
                        return event.isStartElement();
                    }
                };
                XMLEventReader xmlfer = xmlif.createFilteredReader(xmler, filter);

                // Parse into typed objects
                JAXBContext ctx = JAXBContext.newInstance("br.ufg.inf.findbugsparser");
                Unmarshaller um = ctx.createUnmarshaller();

                int bugs = 0;
                BugInstance bi = null;

                if (xmlfer.peek() != null) {
                    Object o = um.unmarshal(xmler);

                    if (o instanceof BugCollection) {
                        BugCollection bc = (BugCollection) o;

                        List bl = bc.getBugInstance();

                        for (Iterator<BugInstance> it = bl.iterator(); it.hasNext();) {
                            bi = it.next();

                            List<Object> itens = bi.getClazzOrTypeOrMethod();
                            Method met = null;
                            SourceLine sl = null;
                            for (Iterator<Object> ctm = itens.iterator(); ctm.hasNext();) {
                                Object item = ctm.next();  // No downcasting required.

                                if (item instanceof Method) {
                                    if (met == null) {
                                        met = (Method) item;
                                    }
                                }

                                if (item instanceof SourceLine) {
                                    if (sl == null) {
                                        sl = (SourceLine) item;
                                    }
                                }
                            }

                            System.out.println("BUG: " + bugs + "\tAbreviation: " + bi.getAbbrev());
                            System.out.println("\tType: " + bi.getType());
                            System.out.println("\tPriority: " + bi.getPriority());
                            System.out.println("\tCategory: " + bi.getCategory());
                            if (met == null) {
                                System.out.println("\tClass Name: <no name>");
                                System.out.println("\tMethod Name:  <no name>");
                            } else {
                                System.out.println("\tClass Name: " + met.getClassname());
                                System.out.println("\tMethod Name: " + met.getName());
                            }
                            System.out.println("\tSource Line Start: " + sl.getStart());
                            System.out.println("\tSource Line End: " + sl.getEnd());
                            System.out.println("\tShort Message: " + bi.getShortMessage());
                            System.out.println("\tLong Message: " + bi.getLongMessage());
                            bugs++;
                        }

                    }
                    System.out.println(o.toString());
                }
                fr.close();
            } else {
                System.out.println("Uso: br.ufg.inf.findbugsparser.MainScreen <xml_findbugs>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
