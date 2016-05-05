<%@ page import="com.zesped.model.Client" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  com.zesped.action.ListClients oAbn = (com.zesped.action.ListClients) request.getAttribute("actionBean");
%><stripes:form id="listclients" beanclass="com.zesped.action.ListClients">
<table align="center" width="790px">
  <tr>
		<td class="tablecontrols" colspan="6">
		<div style="font-size:medium;float:left">Listado de Proveedores</div>
		<div style="float:left;margin-left:30px"><a href="EditClient.action" rel="lightbox"><input type="button" class="login" value="Nuevo" /></a></div>
		<div style="float:right;clear:right">
		  <div style="float:left"><stripes:text name="find" /></div>
		  <div style="float:left;margin-left:10px"><input type="button" id="search" name="search" class="login" value="Buscar" onclick="var fnd = document.forms['listclients'].find.value; var emp = document.getElementById('proveedores'); emp.innerHTML = '<br/><br/><br/><br/><center><div id=proveedoresWorkDiv></div></center><br/><br/><br/><br/>'; spinner.spin(document.getElementById('proveedoresWorkDiv')); var inh = httpRequestText('ListClients.action?find='+escape(fnd)); spinner.stop(); emp.innerHTML = inh;" /></div>
		</div>
		<div style="float:left;clear:left">
		  <img src="img/refresh16.gif" width="16" height="16" border="0" alt="Refrescar" />
		  <a id="refresh" class="anchorhighlight" href="#" onclick="YUI.zesped.refreshClients()">Refrescar</a>
		</div>		
		<div style="float:right">
		  <img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" />
			&nbsp;<a id="delete" class="anchorhighlight" href="#" onclick="YUI.zesped.deleteSelectedClients()">Eliminar proveedores seleccionados</a>		
		</div>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">Raz&oacute;n Social</td>
    <td class="tableheader">CIF</td>
    <td class="tableheader">Poblaci&oacute;n</td>
    <td class="tableheader">C&oacute;d. Postal</td>
    <td class="tableheader">Provincia</td>
    <td class="tableheader" align="center"></td>
  </tr>
  <% String r = " 0";
     int n = 0;
     for (Client u : oAbn.getClients()) { 
         r = (r.equals("0") ? "1" : "0"); %>         
  <tr>
  	<td class="tablerow<%=r%>"><a href="EditClient.action?client.id=<%=u.getId()%>" rel="lightbox"><%=u.getBusinessName()%></a></td>
    <td class="tablerow<%=r%>"><%=u.getTaxId()==null ? "" : u.getTaxId()%></td>
    <td class="tablerow<%=r%>"><%=u.getCity()==null ? "" : u.getCity()%></td>
    <td class="tablerow<%=r%>"><%=u.getZipCode()==null ? "" : u.getZipCode()%></td>
    <td class="tablerow<%=r%>"><%=u.getState()==null ? "" : u.getStateName()%></td>
    <td class="tablerow<%=r%>" align="center"><stripes:checkbox name="checkedValues<%=String.valueOf(n)%>" value="<%=u.getId()%>" onclick="changeSelectedClients()" /></td>
  </tr>
	<%  n++; } %>
  <tr>
  	<td class="tablefooter" colspan="6"></td>
  </tr>
</table>
</stripes:form>
