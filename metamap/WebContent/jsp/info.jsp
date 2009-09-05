<%@ page language="java" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<div id="info">
    <table>
        <tbody>
             <tr>
                <td>
                    <div class="infoLabel" id="userInfoLabel">
                        <span><bean:message key="status.user"/>: </span>
                    </div>
                </td>
                <td>
                    <div class="infoValue" id="userInfoValue">
                        <span class="userName"><%=session.getAttribute("login.done")%></span>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="infoLabel" id="blogInfoLabel"></div>
                </td>
                <td>
                    <div class="infoValue" id="blogInfoValue"></div>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="infoLabel" id="waypointInfoLabel"></div>
                </td>
                <td>
                    <div class="infoValue" id="waypointInfoValue"></div>
                </td>
            </tr>
        </tbody>
    </table>
</div>