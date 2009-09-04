/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.extern.web.geoservice;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Hannes
 */
public class ResponseHandlerNearbyPlace extends DefaultHandler {
    
    // XML tags
    private static final String PLACE = "name";
    private static final String COUNTRY_CODE = "countryCode";
    
    private Stack qNameStack = new Stack();
    private String currentElementContent = null;
    
    private Toponym toponym = new Toponym();
    
    public void startElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes) throws SAXException
    {
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
            String qName) throws SAXException
    {
        // get current qualified name
        qName = (String) qNameStack.peek();

        if(!currentElementContent.equals("")) {
            if(PLACE.equals(qName)) {
                GeonamesWSC.toponym.setNearbyPlaceName(currentElementContent);
            }
            else if(COUNTRY_CODE.equals(qName)) {
                GeonamesWSC.toponym.setCountryCode(currentElementContent);
            }
        }
    }
    
}
