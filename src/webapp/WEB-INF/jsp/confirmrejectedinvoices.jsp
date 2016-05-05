<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="clientbox" style="width:600px;height:600px">
  <div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
	  <div class="darkbox" style="height:18px"><div style="padding:6px"></span><big>Rechazar Facturas</big></div></div>
	<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	<div id="rejectionerrors" style="height:0px" ></div>
	<stripes:form name="rejectionData" beanclass="com.zesped.action.ConfirmRejectedInvoices">
	<stripes:hidden name="docs" value="${actionBean.ids}" />
	<table width="590">
	<tr><td width="130"></td><td width="460"></td></tr>
	<tr><td class="formfields" colspan="2" height="20px">Est&aacute; a punto de rechazar las siguientes facturas:</td></tr>
	<tr><td>
	<table>
	<c:forEach items="${actionBean.invoices}" var="i">
	  <tr height="20px">
	  	<td class="formfields" nowrap="nowrap">&bull;&nbsp;</td>
	  	<td class="formfields" nowrap="nowrap">${i.invoiceNumber}</td>
	    <td class="formfields" nowrap="nowrap">${i.concept}</td>
	    <td class="formfields" nowrap="nowrap">${i.totalAmount} ${i.currency}</td>
	  </tr>
	</c:forEach>
	</table>
	</td></tr>
	<tr><td class="formfields" colspan="2" height="20px">Por favor especifique la causa de su rechazo.</td></tr>
	<tr><td class="formfields" colspan="2"><stripes:textarea name="comments" cols="64" rows="4"/></td></tr>
	<tr>
	  <td class="formfields"><input type="button" class="submit" value="Rechazar" onclick="rejectInvoices()" /></td>
	  <td class="formfields"><input type="button" class="cancel" value="Cancelar" onclick="DivBoxInstance.end()" /></td>
	</tr>
	</table>
  </stripes:form>
</div>