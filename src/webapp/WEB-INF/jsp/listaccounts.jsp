<%@ page import="com.zesped.model.AccountingAccount" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  com.zesped.action.ListAccounts oAbn = (com.zesped.action.ListAccounts) request.getAttribute("actionBean");
%><stripes:form id="listaccounts" beanclass="com.zesped.action.ListAccounts">
<stripes:hidden name="taxpayer" value="<%=oAbn.getTaxPayer().id()%>" />
<table align="center" width="790px">
  <tr>
		<td class="tablecontrols" colspan="4">
		<div style="font-size:medium;float:left">Listado de Cuentas Contables</div>
		<div style="float:left;margin-left:30px"><a href="EditAccountingAccount.action?taxpayer=<%=oAbn.getTaxPayer().id()%>" rel="lightbox"><input type="button" class="login" value="Nueva" /></a></div>
		<div style="float:right;clear:left">
		  <img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" />
			&nbsp;<a id="delete" class="anchorhighlight" href="#" onclick="YUI.zesped.deleteAccountingAccounts(document.forms['listaccounts'].taxpayer.value)">Eliminar cuentas seleccionadas</a>		
		</div>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">C&oacute;digo</td>
    <td class="tableheader">Descripci&oacute;n</td>
    <td class="tableheader">Estado</td>
    <td class="tableheader"><a href="#" onclick="fakeCheckboxClick()"><img src="img/fakecheckbox16.gif" width="12" height="12" border="0"></a></td>
  </tr>
  <% String r = " 0";
     int n = 0;
     for (AccountingAccount u : oAbn.getAccountingAccounts()) { 
         r = (r.equals("0") ? "1" : "0"); %>         
  <tr>
  	<td class="tablerow<%=r%>"><a href="EditAccountingAccount.action?taxpayer=<%=oAbn.getTaxPayer().id()%>&id=<%=u.getId()%>" rel="lightbox"><%=u.getCode()%></a></td>
    <td class="tablerow<%=r%>"><%=u.getDescription()%></td>
    <td class="tablerow<%=r%>"><%=u.getActive() ? "Activa" : "Inactiva" %></td>
    <td class="tablerow<%=r%>" align="center"><stripes:checkbox name="checkedValues<%=String.valueOf(n)%>" value="<%=u.getId()%>" /></td>
  </tr>
  <%  n++; }%>
  <tr>
  	<td class="tablefooter" colspan="4"></td>
  </tr>
</table>
</stripes:form>