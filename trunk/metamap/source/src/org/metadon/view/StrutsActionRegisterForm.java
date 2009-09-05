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
public class StrutsActionRegisterForm extends ValidatorForm {

    private String username;
    private String password;
    private String passwordConfirm;
    private String deviceID;

    /**
     * @return
     */
    public String getUsernameR() {
        return this.username;
    }

    /**
     * @param string
     */
    public void setUsernameR(String string) {
        this.username = string;
    }

    /**
     * @return
     */
    public String getPasswordR() {
        return this.password;
    }

    /**
     * @param i
     */
    public void setPasswordR(String password) {
        this.password = password;
    }

    /**
     * @return
     */
    public String getPasswordConfirm() {
        return this.passwordConfirm;
    }

    /**
     * @param i
     */
    public void setPasswordConfirm(String password) {
        this.passwordConfirm = password;
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
    public StrutsActionRegisterForm() {
        super();
    // TODO Auto-generated constructor stub
    }

}
