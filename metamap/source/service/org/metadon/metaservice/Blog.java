package org.metadon.metaservice;

public class Blog {
	
	long timestamp;
	String username;
	String journeyName;
	String message;
	double longitude;
	double latitude;
	double elevation;
	int photoLength;
	byte[] photo;
	
	boolean containsTimestamp = false;
	
	// timestamp
	public void setTimestamp(long ts) {
		this.timestamp = ts;
		this.containsTimestamp = true;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	// username
	public void setUserName(String uname) {
		this.username = uname;
	}
	
	public String getUserName() {
		return this.username;
	}
	
	// journey name
	public void setJourneyName(String jname) {
		this.journeyName = jname;
	}
	
	public String getJourneyName() {
		return this.journeyName;
	}
	
	// message
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    // longitude
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public double getLongitude() {
        return this.longitude;
    }
    
    // latitude
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLatitude() {
        return this.latitude;
    }
    
    // elevation
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }
    
    public double getElevation() {
        return this.elevation;
    }
    
    // photo length
    public void setPhotoLength(int length) {
        this.photoLength = length;
    }
    
    public int getPhotoLength() {
        return this.photoLength;
    }
    
    // photo
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    
    public byte[] getPhoto() {
        return this.photo;
    }
    
    public boolean isValid() {
    	if(this.containsTimestamp && this.message != null || this.photo != null) {
    		return true;
    	}
    	return false;
    }
}
