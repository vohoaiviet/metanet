<%-- 
    Document   : header
    Created on : 27.01.2008, 23:57:34
    Author     : Hannes
--%>

<%@ page language="java"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<%if (session.getAttribute("login.done") == null) {
                response.sendRedirect(response.encodeRedirectURL("loginFwd.do"));
            }%>

<div id="menu">
    <ul>
        <li><html:link forward="loginFwd"><bean:message key="menu.login"/></html:link></li>
        <li><html:link forward="registerFwd"><bean:message key="menu.register"/></html:link></li>
    </ul>
</div>
