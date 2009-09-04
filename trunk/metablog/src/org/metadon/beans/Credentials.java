/*
 * Login.java
 *
 * Created on 03. November 2007, 14:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.beans;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.metadon.utils.ISerializable;


/**
 *
 * @author Hannes
 */
public class Credentials implements ISerializable {
    
    private String user;
    private String key;
    private String btAddress;
    
    
    /** Creates a new instance of Login */
    public Credentials() {
        super();
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public void setBtAddress(String btAddress) {
        this.btAddress = btAddress;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getBtAddress() {
        return this.btAddress;
    }
    
    // serialize login data for storage
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeUTF(this.user);
        dos.writeUTF(this.key);
        dos.writeUTF(this.btAddress);
        dos.close(); // closes baos as well
        return baos.toByteArray();
    }
    
    // reconstruct login data from byte stream
    public void unserialize(byte[] record) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(record);
        DataInputStream dis = new DataInputStream(bais);
        
        // read instance variables in same order as written out
        this.user = dis.readUTF();
        this.key = dis.readUTF();
        this.btAddress = dis.readUTF();
        dis.close(); // closes bais as well
    }
    
}
