/*
 * Message.java
 *
 * Created on 07. November 2007, 17:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.beans;

/**
 *
 * @author Hannes
 */
public class Message {
    
    private String text = null;
    
    /**
     * Creates a new instance of Message
     */
    public Message(String text) {
        super();
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
}
