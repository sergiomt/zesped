<%@page import="java.math.BigDecimal"%>
<%@page import="com.zesped.action.EngageCredit"%>
<%@page import="com.zesped.idl.data.Tpv"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@page import=" net.sourceforge.stripes.action.ActionBean"%>
<%EngageCredit oAbn = (EngageCredit) request.getAttribute("actionBean");
    BigDecimal status = oAbn.getOrder().getBigDecimal("status_number");%>
<stripes:layout-render name="/inc/engagecreditok.jsp" pageTitle="Contratación de créditos">
    <stripes:layout-component name="headercredit">
        <div class="yui3-gb" style="margin:0px 64px 0px 64px"> 
            <fieldset>
                <legend>Hist&oacuterico</legend>
                <table>
                    <tr>
                        <td class="formlabelrequired">Cr&eacuteditos consumidos: <stripes:text name="creditsused" value="${actionBean.creditsused}" disabled="disabled" style="width:100px;"/></td>
                        <td class="formlabelrequired">Cr&eacuteditos restantes: <stripes:text name="creditsleft" value="${actionBean.creditsleft}" disabled="disabled" style="width:100px;"/></td>
                        <td><a href="HistorialEngageCredit.action" rel="lightbox"><input type="button" class="login" value="Histórico de contratación" /></a></td>
                    </tr>
                </table>
            </fieldset>
        </div>                        
    </stripes:layout-component>
    <stripes:layout-component name="formfoot">
        <%
            if (status!=null && (status.compareTo(Tpv.PAGADO) == 0 || status.compareTo(Tpv.PENDIENTE_CONFIRMACION) == 0)) {
        %>
        <form id="payPurchasing" action="MyAccount.action" method="post">
            <input type="hidden" name="engage" value="true"/>
        </form>
        <%} else {%>
        <form id="payPurchasing" action="<%=Tpv.getTpvUrl()%>" method="post">
            <input type="hidden" id="peticion" name="peticion" value="">
        </form>
        <% }%>
    </stripes:layout-component>   

</stripes:layout-render>
