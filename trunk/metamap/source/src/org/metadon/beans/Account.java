/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.beans;

/**
 *
 * @author Hannes
 */
public class Account extends org.apache.struts.action.ActionForm {
    
   private String username = "";
   private String password = "";
   private String deviceID;
   
   /**
    * @return
    */
   public String getUsername() {
       return this.username;
   }

   /**
    * @param string
    */
   public void setUsername(String string) {
       this.username = string;
   }
   
   /**
    * @return
    */
   public String getPassword() {
       return this.password;
   }

   /**
    * @param i
    */
   public void setPassword(String password) {
       this.password = password;
   }
   
   /**
    * @return
    */
   public String getDeviceID() {
       return this.deviceID;
   }

   /**
    * @param i
    */
   public void setDeviceID(String deviceID) {
       this.deviceID = deviceID;
   }

   /**
    *
    */
   public Account() {
       super();
       // TODO Auto-generated constructor stub
   }
   
}
