/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.extern.web.geoservice;


import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.metadon.control.Controller;
import org.metadon.extern.location.Waypoint;
import org.metadon.utils.ActivityAlert;
import org.xml.sax.SAXException;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class GeonamesWSC implements Runnable {

    private static String USER_AGENT = "geonames webservice client 0.5";
    private static String GEONAMES_SERVER = "http://ws.geonames.org";
    
    protected static Toponym toponym = new Toponym();
    private static final ActivityAlert activityScreen = new ActivityAlert(true);
    private boolean connected = false;
    HttpConnection httpConnection = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    int responseCode;
    Controller controller;
    String language = "en";

    public GeonamesWSC() {
        super();
        new Thread(this).start();
    }
    
    public void setController(Controller c) {
        this.controller = c;
    }

    public void run() {
        this.connected = true;
        while (this.connected) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
        }
    }
    
    public Toponym getToponym(Waypoint wp) throws IOException, Exception, SecurityException {
        activityScreen.setString(Locale.get("geoNamesWS.resolvingJourney"));
        this.controller.show(activityScreen);
        
        this.findCountryName(wp);
        this.findNearbyPlaceName(wp);
        this.findSRTMElevation(wp);
        return toponym;
    }
    
    public void stop() {
        this.connected = false;
    }

    public void cleanUp() {
        toponym.cleanUp();
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.outputStream != null) {
                this.outputStream.close();
            }
            if (this.httpConnection != null) {
                this.httpConnection.close();
                this.httpConnection = null;
            }
        } catch (IOException ioe) {
        }
    }
    
    // return nearest populated place for a specific waypoint
    private void findCountryName(Waypoint wp) throws IOException, Exception, SecurityException {

        // set request parameters
        double latitude = wp.getLocation().getLatitude();
        double longitude = wp.getLocation().getLongitude();

        // service url
        String url = GEONAMES_SERVER + "/countrySubdivision?";

        url += "type=XML";           // response type
        url += "&lat=" + latitude;
        url += "&lng=" + longitude;
        url += "&lang" + this.language;

        // open connection and send request
        this.httpConnection = this.getServiceConnection(url);
        
        // read the HTTP response headers
        this.verifyConnection(this.httpConnection);
        // get response stream
        this.inputStream = this.httpConnection.openInputStream();
        // parse XML response and return result
        this.parseResponseCountry(this.inputStream);
    }

    // return nearest populated place for a specific waypoint
    private void findNearbyPlaceName(Waypoint wp) throws IOException, Exception, SecurityException {

        // set request parameters
        double latitude = wp.getLocation().getLatitude();
        double longitude = wp.getLocation().getLongitude();

        // service url
        String url = GEONAMES_SERVER + "/findNearbyPlaceName?";

        url += "type=XML";             // response type
        url += "&style=SHORT";         // verbosity of returned xml document
        url += "&lat=" + latitude;
        url += "&lng=" + longitude;
        url += "&lang" + this.language;

        // open connection and send request
        this.httpConnection = this.getServiceConnection(url);
        
        // read the HTTP response headers
        this.verifyConnection(this.httpConnection);
        // get response stream
        this.inputStream = this.httpConnection.openInputStream();
        // parse XML response
        this.parseResponseNearbyPlace(this.inputStream);
    }

    // return Shuttle Radar Topography Mission (SRTM) elevation data for a waypoint
    private void findSRTMElevation(Waypoint wp) throws IOException, Exception, SecurityException {

        // set request parameters
        double latitude = wp.getLocation().getLatitude();
        double longitude = wp.getLocation().getLongitude();

        // service url
        String url = GEONAMES_SERVER + "/srtm3?";

        url += "type=XML";
        url += "&lat=" + latitude;
        url += "&lng=" + longitude;

        // open connection and send request
        this.httpConnection = this.getServiceConnection(url);

        // read the HTTP response headers
        this.verifyConnection(this.httpConnection);
        // get response stream
        this.inputStream = this.httpConnection.openInputStream();
        // parse XML response
        this.parseResponseElevation(this.inputStream);
    }

    // verify server connection
    private void verifyConnection(HttpConnection c) throws IOException {

        this.responseCode = c.getResponseCode();
        if (this.responseCode != HttpConnection.HTTP_OK) {
            //#debug
//#             System.out.println("HTTP Error: " + this.responseCode);
            if (this.responseCode == 504) {
                throw new IOException("No Service Reachable.\nGateway Timeout.");
            } else {
                throw new IOException("No Service Reachable.");
            }
        }
    }

    // returns a connection to the webservice
    private HttpConnection getServiceConnection(String url) throws IOException {

        try {
            // define connection to restful geonames web service
            HttpConnection c = (HttpConnection) Connector.open(url);

            // setup request method and headers
            c.setRequestProperty("User-Agent", USER_AGENT);
//            c.setRequestMethod(HttpConnection.GET);
//            c.setRequestProperty("lang", "en");

            return c;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Unvalid HTTP URL.");
        }
    }
    
    // parse XML response to update toponym
    private void parseResponseCountry(InputStream is) throws
            FactoryConfigurationError,
            ParserConfigurationException,
            SAXException,
            IOException {
        SAXParser xmlParser = SAXParserFactory.newInstance().newSAXParser();
        ResponseHandlerCountry handler = new ResponseHandlerCountry();
        xmlParser.parse(is, handler);
    }

    // parse XML response to update toponym
    private void parseResponseNearbyPlace(InputStream is) throws
            FactoryConfigurationError,
            ParserConfigurationException,
            SAXException,
            IOException {
        SAXParser xmlParser = SAXParserFactory.newInstance().newSAXParser();
        ResponseHandlerNearbyPlace handler = new ResponseHandlerNearbyPlace();
        xmlParser.parse(is, handler);
    }

    // parse XML response to update elevation
    private void parseResponseElevation(InputStream is) throws
            FactoryConfigurationError,
            ParserConfigurationException,
            SAXException,
            IOException {
        SAXParser xmlParser = SAXParserFactory.newInstance().newSAXParser();
        ResponseHandlerElevation handler = new ResponseHandlerElevation();
        xmlParser.parse(is, handler);
    }
}

