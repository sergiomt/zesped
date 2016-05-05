<%@ page import="com.zesped.model.Employee" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  com.zesped.action.ListEmployees oAbn = (com.zesped.action.ListEmployees) request.getAttribute("actionBean");
%><stripes:form id="listemployees" beanclass="com.zesped.action.ListEmployees">
<table align="center" width="790px">
		<c:choose>
		<c:when test="${empty actionBean.taxPayer}">
    <tr><td class="tablecontrols" colspan="5">
	 	  <div style="font-size:medium;float:left">Listado de Empleados</div>
		  <!-- <div style="float:left;margin-left:30px"><a href="EditEmployee.action" rel="lightbox"><input type="button" class="login" value="Nuevo" /></a></div>  -->
		  <div style="float:right;clear:right">
		    <div style="float:left"><input type="text" name="find" /></div>
		    <div style="float:left;margin-left:10px"><input type="button" id="searchbutton" name="searchbutton" class="login" value="Buscar" /></div>
		  </div>
		  <div style="float:right;clear:left">
		    <img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" />
			  &nbsp;<a id="delete" class="anchorhighlight" href="#">Eliminar empleados seleccionados</a>		
		  </div>
		</td></tr>
	  <tr>
	  	<td class="tableheader">Nombre y Apellidos</td>
	    <td class="tableheader">NIF</td>
	    <td class="tableheader">Empresa</td>
	    <td class="tableheader">Activo</td>
	    <td class="tableheader" align="center"><a href="#" id="fakecheckbox"><img src="img/fakecheckbox16.gif" width="12" height="12" border="0"></a></td>
	  </tr>
  <% String r = " 0";
     int n = 0;
     for (Employee u : oAbn.getEmployees()) { 
         r = (r.equals("0") ? "1" : "0"); %>         
	  <tr>
	  	<td class="tablerow<%=r%>"><a href="EditEmployee.action?id=<%=u.getId()%>" rel="lightbox"><%=u.getName()%></a></td>
	    <td class="tablerow<%=r%>"><%=u.getTaxId()==null ? "" : u.getTaxId()%></td>
	    <td class="tablerow<%=r%>"><%=oAbn.getBusinessName(u.getId())%></td>
	    <td class="tablerow<%=r%>"><%=u.getActive() ? "Si" : "No"%></td>
	    <td class="tablerow<%=r%>" align="center"><stripes:checkbox name="chekedValues<%=String.valueOf(n)%>" value="<%=u.getId()%>" /></td>
	  </tr>
	  <%  n++; } %>
	  <tr>
	  	<td class="tablefooter" colspan="5"></td>
	  </tr>
		</c:when>
		<c:otherwise>
    <tr><td class="tablecontrols" colspan="4">
	 	  <div style="font-size:small;float:left">Empleados de la empresa <%=oAbn.getTaxPayer().getBusinessName()%></div>
		  <div style="float:left;margin-left:30px"><a href="EditEmployee.action?taxpayer=<%=oAbn.getTaxPayer().getId()%>" rel="lightbox"><input type="button" class="login" value="Nuevo" /></a></div>
		  <div style="float:right;clear:right">
		    <div style="float:left"><input type="text" name="find" /></div>
		    <div style="float:left;margin-left:10px"><input type="button" id="searchbutton" name="searchbutton" class="login" value="Buscar" /></div>
		  </div>
		  <div style="float:right;clear:left">
		    <img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" />
			  &nbsp;<a id="delete" class="anchorhighlight" href="#">Eliminar empleados seleccionados</a>		
		  </div>
		</td></tr>
	  <tr>
	  	<td class="tableheader">Nombre y Apellidos</td>
	    <td class="tableheader">NIF</td>
	    <td class="tableheader">Activo</td>
	    <td class="tableheader" align="center"><a href="#" id="fakecheckbox"><img src="img/fakecheckbox16.gif" width="12" height="12" border="0"></a></td>
	  </tr>
    <% String r = " 0";
     int n = 0;
     for (Employee u : oAbn.getEmployees()) { 
         r = (r.equals("0") ? "1" : "0"); %>         
	  <tr>
	  	<td class="tablerow<%=r%>"><a href="EditEmployee.action?id=<%=u.getId()%>" rel="lightbox"><%=u.getName()%></a></td>
	    <td class="tablerow<%=r%>"><%=u.getTaxId()==null ? "" : u.getTaxId()%></td>
	    <td class="tablerow<%=r%>"><%=u.getActive() ? "Si" : "No"%></td>
	    <td class="tablerow<%=r%>" align="center"><stripes:checkbox name="chekedValues<%=String.valueOf(n)%>" value="<%=u.getId()%>" /></td>
	  </tr>
	  <%  n++; } %>
	  <tr>
	  	<td class="tablefooter" colspan="4"></td>
	  </tr>
		</c:otherwise>
		</c:choose>
</table>
</stripes:form>
