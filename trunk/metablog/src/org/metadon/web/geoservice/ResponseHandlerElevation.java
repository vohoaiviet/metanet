/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.web.geoservice;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Hannes
 */
public class ResponseHandlerElevation extends DefaultHandler {

    // XML tags
    private static final String SRTM3 = "srtm3";
    private double elevation = -1;
    private Stack qNameStack = new Stack();
    private String currentElementContent;

    public void startElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes) throws SAXException {
        this.qNameStack.addElement(qName);
        currentElementContent = "";
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        currentElementContent += new String(ch, start, length);
    }

    public void endElement(
            String uri,
            String localName,
            String qName) throws SAXException {
        // get current qualified name
        qName = (String) qNameStack.peek();

        if (SRTM3.equals(qName) && !currentElementContent.equals("")) {
            double elevation = Double.parseDouble(currentElementContent);
            // this value is system specific for ocean areas
            if (elevation == -32768)
                this.elevation = 0;
            GeonamesWSC.toponym.setElevation(new Double(elevation));
        }
    }

}
