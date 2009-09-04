package org.metadon.extern.web.metaservice;

import javax.xml.rpc.JAXRPCException;
import javax.xml.namespace.QName;
import javax.microedition.xml.rpc.Operation;
import javax.microedition.xml.rpc.Type;
import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;

import de.enough.polish.rmi.RemoteException;

public class Gateway_Stub implements IGateway, javax.xml.rpc.Stub {
    
    private String[] _propertyNames;
    private Object[] _propertyValues;
    
    public Gateway_Stub() {
        _propertyNames = new String[] { ENDPOINT_ADDRESS_PROPERTY };
        _propertyValues = new Object[] { "http://192.168.1.35:80/axis2/services/Gateway" };
    }
    
    public void _setProperty( String name, Object value ) {
        int size = _propertyNames.length;
        for (int i = 0; i < size; ++i) {
            if( _propertyNames[i].equals( name )) {
                _propertyValues[i] = value;
                return;
            }
        }
        String[] newPropNames = new String[size + 1];
        System.arraycopy(_propertyNames, 0, newPropNames, 0, size);
        _propertyNames = newPropNames;
        Object[] newPropValues = new Object[size + 1];
        System.arraycopy(_propertyValues, 0, newPropValues, 0, size);
        _propertyValues = newPropValues;
        
        _propertyNames[size] = name;
        _propertyValues[size] = value;
    }
    
    public Object _getProperty(String name) {
        for (int i = 0; i < _propertyNames.length; ++i) {
            if (_propertyNames[i].equals(name)) {
                return _propertyValues[i];
            }
        }
        if (ENDPOINT_ADDRESS_PROPERTY.equals(name) || USERNAME_PROPERTY.equals(name) || PASSWORD_PROPERTY.equals(name)) {
            return null;
        }
        if (SESSION_MAINTAIN_PROPERTY.equals(name)) {
            return new Boolean(false);
        }
        throw new JAXRPCException("Stub does not recognize property: " + name);
    }
    
    protected void _prepOperation(Operation op) {
        for (int i = 0; i < _propertyNames.length; ++i) {
            op.setProperty(_propertyNames[i], _propertyValues[i].toString());
        }
    }
    
    public Boolean post(Long timestamp, String journeyName, String message, Double longitude, Double latitude, Double elevation, String encodedPhoto) throws RemoteException {
        Object inputObject[] = new Object[] {
            timestamp,
            journeyName,
            message,
            longitude,
            latitude,
            elevation,
            encodedPhoto
        };
        
        Operation op = Operation.newInstance( _qname_operation_post, _type_post, _type_postResponse );
        _prepOperation( op );
        op.setProperty( Operation.SOAPACTION_URI_PROPERTY, "urn:post" );
        Object resultObj;
        try {
            resultObj = op.invoke( inputObject );
        } catch( JAXRPCException e ) {
            Throwable cause = e.getLinkedCause();
            if( cause instanceof RemoteException ) {
                throw (RemoteException) cause;
            }
            throw e;
        }
        
        return (Boolean )((Object[])resultObj)[0];
    }
    
    public Boolean logout(String deviceID) throws RemoteException {
        Object inputObject[] = new Object[] {
            deviceID
        };
        
        Operation op = Operation.newInstance( _qname_operation_logout, _type_logout, _type_logoutResponse );
        _prepOperation( op );
        op.setProperty( Operation.SOAPACTION_URI_PROPERTY, "urn:logout" );
        Object resultObj;
        try {
            resultObj = op.invoke( inputObject );
        } catch( JAXRPCException e ) {
            Throwable cause = e.getLinkedCause();
            if( cause instanceof RemoteException ) {
                throw (RemoteException) cause;
            }
            throw e;
        }
        
        return (Boolean )((Object[])resultObj)[0];
    }
    
    public Boolean signon(String deviceID) throws RemoteException {
        Object inputObject[] = new Object[] {
            deviceID
        };
        
        Operation op = Operation.newInstance( _qname_operation_signon, _type_signon, _type_signonResponse );
        _prepOperation( op );
        op.setProperty( Operation.SOAPACTION_URI_PROPERTY, "urn:signon" );
        Object resultObj;
        try {
            resultObj = op.invoke( inputObject );
        } catch( JAXRPCException e ) {
            Throwable cause = e.getLinkedCause();
            if( cause instanceof RemoteException ) {
                throw (RemoteException) cause;
            }
            throw e;
        }
        
        return (Boolean )((Object[])resultObj)[0];
    }
    
    protected static final QName _qname_operation_signon = new QName( "http://webservice.tmblog.tuwien.ac.at", "signon" );
    protected static final QName _qname_operation_logout = new QName( "http://webservice.tmblog.tuwien.ac.at", "logout" );
    protected static final QName _qname_operation_post = new QName( "http://webservice.tmblog.tuwien.ac.at", "post" );
    protected static final QName _qname_postResponse = new QName( "http://webservice.tmblog.tuwien.ac.at", "postResponse" );
    protected static final QName _qname_logoutResponse = new QName( "http://webservice.tmblog.tuwien.ac.at", "logoutResponse" );
    protected static final QName _qname_signonResponse = new QName( "http://webservice.tmblog.tuwien.ac.at", "signonResponse" );
    protected static final QName _qname_signon = new QName( "http://webservice.tmblog.tuwien.ac.at", "signon" );
    protected static final QName _qname_post = new QName( "http://webservice.tmblog.tuwien.ac.at", "post" );
    protected static final QName _qname_logout = new QName( "http://webservice.tmblog.tuwien.ac.at", "logout" );
    protected static final Element _type_postResponse;
    protected static final Element _type_logoutResponse;
    protected static final Element _type_post;
    protected static final Element _type_logout;
    protected static final Element _type_signon;
    protected static final Element _type_signonResponse;
    
    static {
        _type_logoutResponse = new Element( _qname_logoutResponse, _complexType( new Element[] {
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "return" ), Type.BOOLEAN, 0, 1, false )}), 1, 1, false );
        _type_postResponse = new Element( _qname_postResponse, _complexType( new Element[] {
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "return" ), Type.BOOLEAN, 0, 1, false )}), 1, 1, false );
        _type_signonResponse = new Element( _qname_signonResponse, _complexType( new Element[] {
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "return" ), Type.BOOLEAN, 0, 1, false )}), 1, 1, false );
        _type_signon = new Element( _qname_signon, _complexType( new Element[] {
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "deviceID" ), Type.STRING, 0, 1, true )}), 1, 1, false );
        _type_logout = new Element( _qname_logout, _complexType( new Element[] {
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "deviceID" ), Type.STRING, 0, 1, true )}), 1, 1, false );
        _type_post = new Element( _qname_post, _complexType( new Element[] {
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "timestamp" ), Type.LONG, 0, 1, false ),
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "journeyName" ), Type.STRING, 0, 1, true ),
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "message" ), Type.STRING, 0, 1, true ),
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "longitude" ), Type.DOUBLE, 0, 1, false ),
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "latitude" ), Type.DOUBLE, 0, 1, false ),
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "elevation" ), Type.DOUBLE, 0, 1, false ),
            new Element( new QName( "http://webservice.tmblog.tuwien.ac.at", "encodedPhoto" ), Type.STRING, 0, 1, true )}), 1, 1, false );
    }
    
    private static ComplexType _complexType( Element[] elements ) {
        ComplexType result = new ComplexType();
        result.elements = elements;
        return result;
    }
}
