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
public class ResponseHandlerCountry extends DefaultHandler {
    
    // XML tags
    private static final String COUNTRY = "countryName";
    private static final String STATE = "adminName1";
   
    
    private Stack qNameStack = new Stack();
    private String currentElementContent = null;
    
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
            if(COUNTRY.equals(qName)) {
                GeonamesWSC.toponym.setCountryName(currentElementContent);
            }
            else if(STATE.equals(qName)) {
                // trim response if necessary
                String state = currentElementContent;
                int index = state.indexOf("\n");
                if(index != -1)
                    state = state.substring(0, index);
                GeonamesWSC.toponym.setStateName(state);
            }
        }
    }
}
