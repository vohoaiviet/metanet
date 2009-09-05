<%@ page language="java"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<html:html locale="true">
    <head>
        <jsp:include page="/jsp/head.jsp" />
        <!--avoid a navigation back to the register page while logged in-->
        <%if (session.getAttribute("login.done") != null) {
                System.out.println("redirect to main... ");
                response.sendRedirect(response.encodeRedirectURL("mainFwd.do"));
            }
        %>
    </head>
    <body>
        <div id="page">
            <jsp:include page="/jsp/menu.jsp" />
            <table class="content">
                <tbody>
                    <jsp:include page="/jsp/logout.jsp" />
                    <tr>
                        <td>
                            <jsp:include page="/jsp/registerForm.jsp" />
                        </td>
                    </tr>
                </tbody>
            </table>
            <div id="footer">
                <p><bean:message key="footer.content"/></p>
            </div>
        </div>
    </body>
    </html:html>
        
        
        