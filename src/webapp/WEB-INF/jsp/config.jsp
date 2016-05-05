<%@ page import="com.zesped.model.Client" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%com.zesped.action.EditConfig oAbn = (com.zesped.action.EditConfig) request.getAttribute("actionBean");%><stripes:form id="configaccount" beanclass="com.zesped.action.Config">
<table align="center" width="790px">
  <tr>
		<td class="tablecontrols" colspan="6">
		<div style="font-size:medium;float:left">Listado de Proveedores</div>
		<div style="float:left;margin-left:30px"><a href="EditClient.action" rel="lightbox"><input type="button" class="login" value="Nuevo" /></a></div>
		<div style="float:right;clear:right">
		  <div style="float:left"><input type="text" name="find" /></div>
		  <div style="float:left;margin-left:10px"><input type="button" id="searchbutton" name="searchbutton" class="login" value="Buscar" /></div>
		</div>
		<div style="float:right;clear:left">
		  <img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" />
			&nbsp;<a id="delete" class="anchorhighlight" href="#">Eliminar proveedores seleccionados</a>		
		</div>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">Raz&oacute;n Social</td>
    <td class="tableheader">CIF</td>
    <td class="tableheader">Poblaci&oacute;n</td>
    <td class="tableheader">C&oacute;d. Postal</td>
    <td class="tableheader">Provincia</td>
    <td class="tableheader" align="center"><a href="#" id="fakecheckbox"><img src="img/fakecheckbox16.gif" width="12" height="12" border="0"></a></td>
  </tr>
  <% String r = " 0";
     int n = 0;
     for (Client u : oAbn.getClients()) { 
         r = (r.equals("0") ? "1" : "0"); %>         
  <tr>
  	<td class="tablerow<%=r%>"><a href="EditClient.action?id=<%=u.getId()%>" rel="lightbox"><%=u.getBusinessName()%></a></td>
    <td class="tablerow<%=r%>"><%=u.getTaxId()==null ? "" : u.getTaxId()%></td>
    <td class="tablerow<%=r%>"><%=u.getCity()==null ? "" : u.getCity()%></td>
    <td class="tablerow<%=r%>"><%=u.getZipCode()==null ? "" : u.getZipCode()%></td>
    <td class="tablerow<%=r%>"><%=u.getState()==null ? "" : u.getState()%></td>
    <td class="tablerow<%=r%>" align="center"><stripes:checkbox name="chekedValues<%=String.valueOf(n)%>" value="<%=u.getId()%>" /></td>
  </tr>
	<%  n++; } %>
  <tr>
  	<td class="tablefooter" colspan="6"></td>
  </tr>
</table>
</stripes:form>
