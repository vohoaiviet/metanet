/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.view;

import org.apache.struts.validator.ValidatorForm;

/**
 *
 * @author Hannes
 */
public class StrutsActionLoginForm extends ValidatorForm {
    
    private String username;
    private String password;
    
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
    *
    */
   public StrutsActionLoginForm() {
       super();
       // TODO Auto-generated constructor stub
   }

}
