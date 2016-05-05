<%@page import="net.sourceforge.stripes.action.ActionBean"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/engagecredit.jsp" pageTitle="Contratación de créditos" 
                       hiddenProducts="FREETRIAL2020" beanclass="com.zesped.action.EngageCredit">
    <stripes:layout-component name="headercredit">
            <div class="yui3-gb" style="margin:0px 64px 0px 64px"> 
        <fieldset>
            <legend>Hist&oacute;rico</legend>
            <table>
                <tr>
                    <td class="formlabelrequired">Cr&eacuteditos consumidos: <stripes:text name="creditsused" value="${actionBean.creditsused}" disabled="disabled" style="width:100px;"/></td>
                    <td class="formlabelrequired">Cr&eacuteditos restantes: <stripes:text name="creditsleft" value="${actionBean.creditsleft}" disabled="disabled" style="width:100px;"/></td>
                    <td><a href="HistorialEngageCredit.action" rel="lightbox"><input type="button" class="login" value="Hist&oacute;rico de contratación" /></a></td>
                </tr>
            </table>
        </fieldset>
    </div>
    
    </stripes:layout-component>
</stripes:layout-render>
