<%@ page import="com.zesped.model.User" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  com.zesped.action.ListUsers oAbn = (com.zesped.action.ListUsers) request.getAttribute("actionBean");
%><stripes:form id="listusers" beanclass="com.zesped.action.ListUsers">
<table align="center" width="790px">
  <tr>
		<td class="tablecontrols" colspan="5">
		<c:choose>
		<c:when test="${empty actionBean.taxPayer}">
	 	  <div style="font-size:medium;float:left">Listado de Usuarios</div>
		  <div style="float:left;margin-left:30px"><a href="EditUser.action" rel="lightbox"><input type="button" class="login" value="Nuevo" /></a></div>
		  <div style="float:right;clear:right">
		    <div style="float:left"><stripes:text name="find" /></div>
		    <div style="float:left;margin-left:10px"><input type="button" id="search" name="search" class="login" value="Buscar" onclick="var fnd = document.forms['listusers'].find.value; var emp = document.getElementById('usuarios'); emp.innerHTML = '<br/><br/><br/><br/><center><div id=usuariosWorkDiv></div></center><br/><br/><br/><br/>'; spinner.spin(document.getElementById('usuariosWorkDiv')); var inh = httpRequestText('ListUsers.action?find='+escape(fnd)); spinner.stop(); emp.innerHTML = inh;" /></div>
		  </div>
		  <div style="float:left;clear:left">
		    <img src="img/refresh16.gif" width="16" height="16" border="0" alt="Refrescar" />
		    <a id="refresh" class="anchorhighlight" href="#" onclick="YUI.zesped.refreshUsers()">Refrescar</a>
		  </div>		
		</c:when>
		<c:otherwise>
	 	  <div style="float:left">Usuarios autorizados en la empresa <%= oAbn.getTaxPayer().getBusinessName()%></div>
		  <div style="float:left;clear:left">
		    <img src="img/refresh16.gif" width="16" height="16" border="0" alt="Refrescar" />
		    <a id="refresh" class="anchorhighlight" href="#" onclick="YUI.zesped.refreshUsers()">Refrescar</a>
		  </div>		
		  <div style="float:right">
		    <img src="img/envelope16.gif" width="21" height="16" border="0" alt="Invitar" />
			  &nbsp;<a id="invite" class="anchorhighlight" href="InviteUser.action?taxpayer=${actionBean.taxPayer}" rel="lightbox">Invitar a otros usuarios a esta empresa</a>		
		  </div>
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">Nombre y Apellidos</td>
    <td class="tableheader">e-mail</td>
    <td class="tableheader">Rol</td>
    <td class="tableheader">Activo</td>
  </tr>
  <% String r = " 0";
     int i = 0;
     for (User u : oAbn.getAll()) { 
       r = (r.equals("0") ? "1" : "0");
  %>
  <tr>
  	<td class="tablerow<%=r%>"><a href="EditUser.action?id=<%=u.getId()%>" rel="lightbox"><%=u.getFirstName()+" "+u.getLastName()%></a></td>
    <td class="tablerow<%=r%>"><%=u.getEmail()%></td>
    <td class="tablerow<%=r%>"><%=u.get("role")%></td>
    <td class="tablerow<%=r%>"><%=u.isActive() ? "Si" : "No"%></td>
  </tr>
  <% } %>
  <tr>
  	<td class="tablefooter" colspan="4"></td>
  </tr>
</table>
</stripes:form>
