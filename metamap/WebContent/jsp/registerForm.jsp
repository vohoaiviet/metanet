<%@ page language="java" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<%if (session.getAttribute("login.done") == null) {
                response.sendRedirect(response.encodeRedirectURL("loginFwd.do"));
            }%>

<div id="content">
    <table>
        <tbody>
            <tr>
                <td>
                    <html:form method="POST" action="register.do" focus="usernameR">
                        <table id="inputTable">
                            <tbody>
                                <tr>
                                    <td><bean:message key="register.name" /></td>
                                    <td><html:text property="usernameR" maxlength="10" /></td>
                                </tr>
                                <tr>
                                    <td><bean:message key="register.password" /></td>
                                    <td><html:password property="passwordR" maxlength="10" /></td>
                                </tr>
                                <tr>
                                    <td><bean:message key="register.passwordConfirm" /></td>
                                    <td><html:password property="passwordConfirm" maxlength="10" /></td>
                                </tr>
                                <tr>
                                    <td><bean:message key="register.deviceID" /></td>
                                    <td><html:text property="deviceID" maxlength="12" /></td>
                                </tr>
                                <tr>
                                    <td><div id="submitButton"><html:submit value="Register" /></div></td>
                                </tr>
                            </tbody>
                        </table>
                    </html:form>
                </td>
                <td>
                    <div id="messages">
                        <ul>
                            <html:messages id="error">
                                <li>
                                    <span class="errorText">
                                        <bean:write name="error"/>
                                    </span>
                                </li>
                            </html:messages>
                        </ul>
                        <ul>
                            <html:messages id="message" message="true">
                                <li>
                                    <span class="successText">
                                        <bean:write name="message"/>
                                    </span>
                                </li>
                            </html:messages>
                        </ul>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</div>
