<%@ page import="com.zesped.model.TaxPayer,com.zesped.action.CountTickets" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:form beanclass="com.zesped.action.CountTickets">
<table align="center" width="790px">
  <tr>
		<td class="tablecontrols" colspan="4">
		  <div style="font-size:medium;float:left">Notas de Gasto de la Empresa ${actionBean.businessName}</div>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">Total</td>
    <td class="tableheader">Pte. Proceso</td>
    <td class="tableheader">Procesadas</td>
    <td class="tableheader">Liquidadas</td>
  </tr>         
  <tr>
    <td class="tablerow0">${actionBean.total}</td>
    <td class="tablerow0">${actionBean.pending}</td>
    <td class="tablerow0">${actionBean.processed}</td>
    <td class="tablerow0">${actionBean.settled}</td>
  </tr>
  <tr>
  	<td class="tablefooter" colspan="4"></td>
  </tr>
</table>
</stripes:form>
