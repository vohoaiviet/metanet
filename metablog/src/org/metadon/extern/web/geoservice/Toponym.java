/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.extern.web.geoservice;

/**
 *
 * @author Hannes
 */
public class Toponym {
    
    // see http://www.geonames.org
    private String countryName = null;
    private String stateName = null;
    private String nearbyPlaceName = null;
    private String countryCode = null;
    private Double elevation = null;
    
    public boolean containsCountryName = false;
    public boolean containsStateName = false;
    public boolean containsNearbyPlaceName = false;
    public boolean containsCountryCode = false;
    public boolean containsElevation = false;
    
 
    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(String name) {
        this.countryName = name;
        this.containsCountryName = true;
    }
    
    public String getStateName() {
        return this.stateName;
    }

    public void setStateName(String name) {
        this.stateName = name;
        this.containsStateName = true;
    }

    public String getNearbyPlaceName() {
        return this.nearbyPlaceName;
    }

    public void setNearbyPlaceName(String name) {
        this.nearbyPlaceName = name;
        this.containsNearbyPlaceName = true;
    }
    
    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        this.containsCountryCode = true;
    }
    
    public Double getElevation() {
        return this.elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
        this.containsElevation = true;
    }
    
    public void cleanUp() {
        countryName = null;
        stateName = null;
        nearbyPlaceName = null;
        countryCode = null;
        elevation = null;
        
        containsCountryName = false;
        containsStateName = false;
        containsNearbyPlaceName = false;
        containsCountryCode = false;
        containsElevation = false;
    }
    
}
