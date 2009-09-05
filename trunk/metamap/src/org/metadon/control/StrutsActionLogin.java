/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.metadon.beans.Credentials;
import org.metadon.view.StrutsActionLoginForm;


/**
 *
 * @author Hannes
 */
public class StrutsActionLogin extends Action {

    /* forward name="success" path="" */
    private final static String SUCCESS = "mainFwd";
    private final static String FAILED = "loginFwd";
    private javax.sql.DataSource dataSource = null;
    private java.sql.Connection connection = null;
    private static SessionBindingListener sessionListener;

    /**
     * This is the action called from the Struts framework.
     * @param mapping The ActionMapping used to select this instance.
     * @param form The optional ActionForm bean for this request.
     * @param request The HTTP Request we are processing.
     * @param response The HTTP Response we are processing.
     * @throws java.lang.Exception
     * @return
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        StrutsActionLoginForm loginForm = (StrutsActionLoginForm) form;

        ActionMessages errors = new ActionErrors();
        Credentials credentials = new Credentials();
        credentials.setUsername(loginForm.getUsername());
        credentials.setPassword(loginForm.getPassword());
        sessionListener = null;

        try {
            this.dataSource = getDataSource(request);
            this.connection = dataSource.getConnection();

            // db interaction
            if (this.isAuthorizedUser(credentials)) {
                System.out.println("user: " + credentials.getUsername() + " is authenticated.");

                // get session
                HttpSession session = request.getSession();
                session.setAttribute("login.done", credentials.getUsername());
                // maintain inactive session for half an hour
                session.setMaxInactiveInterval(1800);

                // here an initial redirect to the protected site may be performed
                
                // add a session binding listener
                sessionListener =  new SessionBindingListener(this.dataSource, credentials.getUsername());
                session.setAttribute("bindings.listener", sessionListener);

                return mapping.findForward(SUCCESS);
            } else {
                // inform user
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.login.failed"));
                System.out.println("user: " + credentials.getUsername() + " is not authorized.");
                // TODO: log access attempt

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
        // save error in request
        saveErrors(request, errors);
        return mapping.findForward(FAILED);
    }

    private boolean isAuthorizedUser(Credentials credentials) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rsUser = stmt.executeQuery("SELECT username, password " + "from user WHERE username='" + credentials.getUsername() + "'");
        if (rsUser.next()) {
            // get password from result
            String password = rsUser.getString(2);
            if (password.trim().equals(credentials.getPassword().trim())) {
                return true;
            }
        }
        return false;
    // stmt closes when garbage collected
    }
    
    public static SessionBindingListener getSessionListener() {
        return sessionListener;
    }
}
