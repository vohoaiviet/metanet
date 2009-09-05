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

<!-- start header -->
<div id="menu">
    <ul>
        <li><html:link forward="mainFwd"><bean:message key="menu.blogs"/></html:link></li>
        <li><html:link forward="aboutFwd"><bean:message key="menu.about"/></html:link></li>
        <li><html:link forward="contactFwd"><bean:message key="menu.contact"/></html:link></li>
        <li><html:link forward="logoutFwd"><bean:message key="menu.logout"/></html:link></li>
    </ul>
</div>
<!-- end header -->

