/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.metadon.beans.Account;
import org.metadon.utils.UserRepositoryManager;
import org.metadon.view.StrutsActionRegisterForm;


/**
 *
 * @author Hannes
 */
public class StrutsActionRegister extends org.apache.struts.action.Action {

    /* forward name="success" path="" */
    private final static String SUCCESS = "registerFwd";
    private final static String FAILED = "registerFwd";
    private javax.sql.DataSource dataSource;
    private java.sql.Connection connection = null;

    /**
     * This is the action called from the Struts framework.
     * @param mapping The ActionMapping used to select this instance.
     * @param form The optional ActionForm bean for this request.
     * @param request The HTTP Request we are processing.
     * @param response The HTTP Response we are processing.
     * @throws java.lang.Exception
     * @return
     */
    // doPost, doGet equivalent
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        StrutsActionRegisterForm registerForm = (StrutsActionRegisterForm) form;

        ActionMessages errors = new ActionErrors();
        ActionMessages messages = new ActionMessages();

        Account account = new Account();
        account.setUsername(registerForm.getUsernameR());
        account.setPassword(registerForm.getPasswordR());
        account.setDeviceID(registerForm.getDeviceID().toUpperCase());

        try {
            this.dataSource = getDataSource(request);
            this.connection = dataSource.getConnection();

            // db interaction
            if (this.existsUser(account)) {
                System.out.println("user: " + account.getUsername() + " already exists.");
                // inform user
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.register.userExists", account.getUsername()));
            } else if (this.existsDevice(account)) {
                System.out.println("device: " + account.getDeviceID() + " already in use.");
                // inform user
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.register.deviceExists", account.getDeviceID()));
            } else {
                // create new user repository
                System.out.println("creating user repository:"+account.getUsername());
                if (UserRepositoryManager.createUserRepository(account.getUsername())) {
                    // add new account to database
                    this.addAccount(account);
                    // inform user
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("success.register.ok", account.getUsername()));
                    saveMessages(request, messages);
                    System.out.println("user: " + account.getUsername() + " registered.");
                    return mapping.findForward(SUCCESS);
                } else {
                    System.out.println("account: can not be created. server error");
                    // inform user
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.register.serverError", account.getUsername()));
                }
            }
        } catch (SQLException sqle) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.db.failed"));
            getServlet().log("Connection.process", sqle);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                getServlet().log("Connection.close", e);
            }
        }
        account = null;
        saveErrors(request, errors);
        return mapping.findForward(FAILED);
    }

    private boolean existsUser(Account account) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rsUser = stmt.executeQuery("SELECT username " + "from user WHERE username='" + account.getUsername() + "'");
        if (rsUser.next()) {
            return true;
        }
        return false;
    // stmt closes when garbage collected
    }

    private boolean existsDevice(Account account) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rsDevice = stmt.executeQuery("SELECT id " + "from device WHERE id='" + account.getDeviceID() + "'");
        if (rsDevice.next()) {
            return true;
        }
        return false;
    // stmt closes when garbage collected
    }

    private void addAccount(Account account) throws SQLException {
        // update user table
        Statement stmt = connection.createStatement();
        stmt.execute("INSERT INTO user VALUES " + "('" + account.getUsername() + "','" + account.getPassword() + "')");
        // update userrole table
        stmt = connection.createStatement();
        stmt.execute("INSERT INTO userrole VALUES " + "('tmblog','" + account.getUsername() + "')");
        // update user table
        stmt = connection.createStatement();
        stmt.execute("INSERT INTO device VALUES " + "('" + account.getDeviceID() + "','" + account.getUsername() + "')");
    }
}
