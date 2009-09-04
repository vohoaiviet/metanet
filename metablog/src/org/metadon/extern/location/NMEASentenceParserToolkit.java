/*
 * NMEASentenceParserToolkit.java
 *
 * Created on 09. Dezember 2007, 13:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.extern.location;

import java.util.Vector;

/**
 *
 * @author Hannes
 */
public class NMEASentenceParserToolkit {
    
    private static final String DELIMETER = ",";
    
    /**
     * Creates a new instance of NMEASentenceParserToolkit
     */
    private NMEASentenceParserToolkit() {
    }
    
     /**
     * Tokenize NMEA sentence
     */
    public static String[] tokenize(String nmeaSentence) {
        Vector values = new Vector();

        // put mnea-sentence values into vector
        int index = nmeaSentence.indexOf(DELIMETER);
        while (index >= 0) {
            values.addElement(nmeaSentence.substring(0, index));
            nmeaSentence = nmeaSentence.substring(index + DELIMETER.length());
            index = nmeaSentence.indexOf(DELIMETER);
        }
        // get last value
        values.addElement(nmeaSentence);

        // return array of tokenized nmea sentence
        String[] tokenizedSentence = new String[values.size()];
        if (values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                tokenizedSentence[i] = (String) values.elementAt(i);
            }
        }
        return tokenizedSentence;
    }
    
    /**
     * Convert latitude or longitude from NMEA format into decimal degree format
     * (used within google maps as well).
     */
    public static double[] NMEACoordToDegreeCoord(String[] nmeaCoord) {
        double[] degreeCoord = new double[2];
        for(int i = 0; i < 2; i++) {
            // (longitude, latitude)
            int degreeInteger = 0;
            double minutes = 0.0;
            if(i == 0) {
                // longitude
                // e.g. 12311.12,W (NMEACoord) => 123 deg. 11.12 min West (DegreeCoord)
                degreeInteger = Integer.parseInt(nmeaCoord[i].substring(0, 3));
                minutes = Double.parseDouble(nmeaCoord[i].substring(3));
            } else if(i == 1) {
                // latitude
                // e.g. 4916.45,N (NMEACoord) => 49 deg. 16.45 min North (DegreeCoord)
                degreeInteger = Integer.parseInt(nmeaCoord[i].substring(0, 2));
                minutes = Double.parseDouble(nmeaCoord[i].substring(2));
            }
            degreeCoord[i] = degreeInteger + minutes / 60.0;
        }
        return degreeCoord;
    }
    
}
