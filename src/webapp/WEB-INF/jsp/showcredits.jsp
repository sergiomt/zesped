<%@ page import="java.text.SimpleDateFormat,com.zesped.model.Order,com.zesped.action.ShowCredits" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  ShowCredits oAbn = (ShowCredits) request.getAttribute("actionBean");
  SimpleDateFormat oDtf = new SimpleDateFormat("dd/MM/yyyy");
%>
<fieldset>
<legend><big>Informaci&oacute;n sobre la cuenta</big></legend>
<table>
<tr>
<td valign="top">
<h2>Cr&eacute;ditos disponibles</h2>
	  <div style="width:200px; padding:8px;">
		<div class="szlider">
			<div class="szliderbar" style="width:<%=oAbn.getCreditsPct()%>"></div>
			<div class="szazalek"><% out.write(String.valueOf(oAbn.getCreditsCreditsLeft())+" ("+String.valueOf(oAbn.getCreditsPct())+"%)"); %></div>
		</div>
	</div>
<h2>Totales</h2>
  <table>
  <tr><td>Facturas b&aacute;sicas&nbsp;</td><td>&nbsp;<%=oAbn.getBasicInvoicesCount()%></td></tr>
  <tr><td>Facturas premium&nbsp;</td><td>&nbsp;<%=oAbn.getPremiumInvoicesCount()%></td></tr>
  <tr><td>Notas de Gasto&nbsp;</td><td>&nbsp;<%=oAbn.getTicketsCount()%></td></tr>
  <tr><td>Consumo total&nbsp;</td><td>&nbsp;<%=oAbn.getCreditsCreditsUsed()%> cr&eacute;ditos</td></tr>
  </table>
  <br/>
</td>
<td><img src="img/spacer.gif" width="20" height="1" alt="" /></td>
<td valign="top">
<h2>Hist&oacute;rico de Contrataci&oacute;n</h2>
<%
  for (Order o : oAbn.getOrders()) {
	  out.write("<b>Pedido</b> "+o.getOrderId().substring(8)+"<br/>");
	  out.write("Fecha "+oDtf.format(o.getOrderDate())+"<br/>");
	  out.write("Cr&eacute;ditos "+String.valueOf(o.getCreditsBought().intValue())+"<br/>");
	  out.write("Importe "+o.getTotalPrice().replace('.',',')+" &euro;<br/><br/>");
  } %>
</td>
</tr>
</table>
</fieldset>