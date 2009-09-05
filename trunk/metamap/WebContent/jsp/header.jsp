<%@ page language="java" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<%if (session.getAttribute("login.done") == null) {
                response.sendRedirect(response.encodeRedirectURL("loginFwd.do"));
            }%>

<tr>
    <td>
        <div class="logo" id="logoExpand"></div>
    </td>
</tr>
<tr>
    <td>
        <div id="status">
            <span><bean:message key="status.user"/>: </span>
            <span class="userName"><%=session.getAttribute("login.done")%></span>
        </div>
    </td>
</tr>