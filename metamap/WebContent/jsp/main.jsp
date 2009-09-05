<%@ page language="java" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<html:html locale="true">
    <head>
        <jsp:include page="/jsp/head.jsp" />
        <!--avoid map access when not logged in-->
        <%if (session.getAttribute("login.done") == null) {
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
                //prevents caching at the proxy server
                response.setDateHeader("Expires", 0);
                System.out.println("redirect to login... ");
                response.sendRedirect(response.encodeRedirectURL("loginFwd.do"));
            } %>

        <!-- location of google maps -->
        <script src="http://maps.google.com/maps?file=api&v=2&key=ABQIAAAAZmcWZbwEW3idJHPHRe_p6hSgzKlASHxJk7ZGgmzwgtigo4b-bxSUDvJQiI4_8ETwu3jUE3kkVpSH8A" type="text/javascript"></script>
        <!-- client logic - location of js files -->
        <script type="text/javascript" src="../js/GZoomControl.js"></script>
        <script type="text/javascript" src="../js/Blog.js"></script>
        <script type="text/javascript" src="../js/BlogManager.js"></script>
        <script type="text/javascript" src="../js/WaypointManager.js"></script>
        <script type="text/javascript" src="../js/BlogLoader.js"></script>
    </head>
    <body>
        <script type="text/javascript" src="../js/Tooltip.js"></script>
        <div id="page">
            <jsp:include page="/jsp/menuMain.jsp" />
            <table class="content">
                <tbody>
                    <tr>
                        <td>
                            <div id="sidebar">
                                <div class="logo" id="logoCompact"></div>
                                <select id="blogList" multiple="multiple"></select>
                                <jsp:include page="/jsp/info.jsp" />
                            </div>
                        </td>
                        <td>
                            <div id="map"></div>
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
        
        