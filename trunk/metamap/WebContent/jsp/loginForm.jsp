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
                    <html:form method="POST" action="login.do" focus="username">    
                        <table id="inputTable">
                            <tbody>
                                <tr>
                                    <td><bean:message key="login.name" /></td>
                                    <td><html:text property="username" maxlength="10" /></td>
                                </tr>
                                <tr>
                                    <td><bean:message key="login.password" /></td>
                                    <td><html:password property="password" maxlength="10" /></td>
                                </tr>
                                <tr>
                                    <td><div id="submitButton"><html:submit value="Login" /></div></td>
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
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</div>
