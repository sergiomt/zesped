<%@ page import="" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:form>
<table>
  <tr>
		<td class="tableheader" colspan="5">
		<div style="font-size:medium;">Listado de Usuarios</div>
		<div><input id="newuserbutton" name="newuserbutton" type="button" class="login" value="Nuevo" /></div>
		<div><input type="text" name="find" /></div>
		<div><input type="button" id="searchbutton" name="searchbutton" /></div>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">Nombre</td>
    <td class="tableheader">Apellidos</td>
    <td class="tableheader">e-mail</td>
    <td class="tableheader">Rol</td>
    <td class="tableheader">Activo</td>
  </tr>
</table>
</stripes:form>
