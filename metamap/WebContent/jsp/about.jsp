<%@ page contentType="text/html" pageEncoding="UTF-8" language="java"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<html:html locale="true">
    <head>
        <jsp:include page="/jsp/head.jsp" />
        <%if (session.getAttribute("login.done") == null) {
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
                //prevents caching at the proxy server
                response.setDateHeader("Expires", 0);
                // remember url of request to be able to forward to this target after login
                //session.setAttribute("login.target", request.getRequestURL().toString());
                System.out.println("redirect to login... ");
                response.sendRedirect(response.encodeRedirectURL("loginFwd.do"));
            }
        %>
    </head>
    <body>
        <div id="page">
            <jsp:include page="/jsp/menuMain.jsp" />
            <table class="content">
                <tbody>
                    <jsp:include page="/jsp/header.jsp" />
                    <tr>
                        <td>
                            <div id="content">
                                <p>
                                    about page.
                                </p>
                            </div>
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