package org.metadon.extern.web.metaservice;
import javax.xml.namespace.QName;

import de.enough.polish.rmi.Remote;
import de.enough.polish.rmi.RemoteException;

public interface IGateway extends Remote {
    
    /**
     *
     */
    public Boolean post(Long timestamp, String journeyName, String message, Double longitude, Double latitude, Double elevation, String encodedPhoto) throws RemoteException;
    
    /**
     *
     */
    public Boolean logout(String deviceID) throws RemoteException;
    
    /**
     *
     */
    public Boolean signon(String deviceID) throws RemoteException;
    
}
