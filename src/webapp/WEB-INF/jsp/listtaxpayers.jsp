<%@ page import="com.zesped.model.TaxPayer,com.zesped.action.ListTaxPayers" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  ListTaxPayers oAbn = (ListTaxPayers) request.getAttribute("actionBean");
%><stripes:form id="listtaxpayers" name="listtaxpayers" beanclass="com.zesped.action.ListTaxPayers">
<input type="hidden" name="count" value="<% out.write(String.valueOf(oAbn.getTaxPayers().size())); %>" />
<table align="center" width="790px">
  <tr>
		<td class="tablecontrols" colspan="7">
		<div style="font-size:medium;float:left">Listado de Empresas</div>
		<div style="float:left;margin-left:30px"><a href="EditTaxPayer.action" rel="lightbox"><input type="button" class="login" value="Nueva" /></a></div>
		<div style="float:right;clear:right">
		  <div style="float:left"><stripes:text id="find" name="find" /></div>
		  <div style="float:left;margin-left:10px"><input type="button" name="search" class="login" value="Buscar" onclick="var fnd = document.forms['listtaxpayers'].find.value; var emp = document.getElementById('empresas'); emp.innerHTML = '<br/><br/><br/><br/><center><div id=empresasWorkDiv></div></center><br/><br/><br/><br/>'; if (httpRequestText('sessionping.jsp')=='1') { spinner.spin(document.getElementById('empresasWorkDiv')); var inh = httpRequestText('ListTaxPayers.action?find='+escape(fnd)); spinner.stop(); emp.innerHTML = inh;" } else { document.location = 'error.jsp?e=expiredsession'; } /></div>
		</div>
		<div style="float:left;clear:left">
		  <img src="img/refresh16.gif" width="16" height="16" border="0" alt="Refrescar" />
		  <a id="refresh" class="anchorhighlight" href="#" onclick="YUI.zesped.refreshTaxPayers()">Refrescar</a>
		</div>
		<div style="float:right">
		  <img src="img/envelope16.gif" width="21" height="16" border="0" alt="Invitar" />
			&nbsp;<a id="invite" class="anchorhighlight" href="InviteUser.action?taxpayer=" rel="lightbox">Invitar usuarios a las empresas seleccionadas</a>		
		  &nbsp;&nbsp;&nbsp;&nbsp;
		  <img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" />
			&nbsp;<a id="delete" class="anchorhighlight" href="#" onclick="YUI.zesped.deleteSelectedTaxPayers()">Eliminar empresas seleccionadas</a>		
		</div>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">Raz&oacute;n Social</td>
    <td class="tableheader">CIF</td>
    <td class="tableheader">Facturas</td>
    <td class="tableheader">Notas de Gasto</td>
    <!--<td class="tableheader">Empleados</td> -->
    <td class="tableheader">Usuarios</td>
    <td class="tableheader">Cuentas</td>
    <td class="tableheader"></td>
  </tr>
  <% String r = " 0";
     int n = 0;
     for (TaxPayer u : oAbn.getTaxPayers()) { 
         r = (r.equals("0") ? "1" : "0"); %>         
  <tr>
  	<td class="tablerow<%=r%>"><a href="EditTaxPayer.action?id=<%=u.getId()%>" rel="lightbox"><%=u.getBusinessName()%></a></td>
    <td class="tablerow<%=r%>"><%=u.getTaxId()%></td>
    <td class="tablerow<%=r%>"><% out.write(String.valueOf(oAbn.getTotalInvoicesCount(n))); %>&nbsp;<a href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') document.getElementById('empresas').innerHTML = httpRequestText('CountInvoices.action?taxpayer=<%=u.getId()%>'); else document.location='error.jsp?e=expiredsession';">mostrar</a></td>
    <td class="tablerow<%=r%>"><% out.write(String.valueOf(oAbn.getTotalTicketsCount(n))); %>&nbsp;<a href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') document.getElementById('empresas').innerHTML = httpRequestText('CountTickets.action?taxpayer=<%=u.getId()%>'); else document.location='error.jsp?e=expiredsession';">mostrar</a></td>
    <!--<td class="tablerow<%=r%>"><a href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') document.getElementById('empresas').innerHTML = httpRequestText('ListEmployees.action?taxpayer=<%=u.getId()%>'); else document.location='error.jsp?e=expiredsession';"><%=oAbn.getEmployeesCounts().get(n)%></a></td>-->
    <td class="tablerow<%=r%>"><%=oAbn.getAllowedUsersCounts().get(n)%>&nbsp;<a href="#" onclick="YUI.zesped.listUsersForTaxPayer('<%=u.getId()%>')">mostrar</a></td>
    <td class="tablerow<%=r%>"><%=oAbn.getAccountingAccountsCounts().get(n)%>&nbsp;<a href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') document.getElementById('empresas').innerHTML = httpRequestText('ListAccounts.action?taxpayer=<%=u.getId()%>'); else document.location='error.jsp?e=expiredsession';">mostrar</a></td>
    <td class="tablerow<%=r%>" align="center"><stripes:checkbox name="checkedValues<%=String.valueOf(n)%>" value="<%=u.getId()%>" onclick="changeSelectedTaxPayers()" /></td>
  </tr>
  <%  n++; }%>
  <tr>
  	<td class="tablefooter" colspan="7"></td>
  </tr>
</table>
</stripes:form>
