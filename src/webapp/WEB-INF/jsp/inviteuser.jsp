<%@ page import="com.zesped.model.Role" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="invitebox" style="width:600px;height:400px">
	<stripes:form name="userData" beanclass="com.zesped.action.InviteUser">
  <stripes:hidden name="taxPayer" />
  <c:choose>
  <c:when test="${empty actionBean.taxPayer}">
  </c:when>
  <c:otherwise>
	<table width="590px">
	  <tr>
	    <td class="tablecontrols" colspan="3"><div style="padding:6px">Invitar usuarios a la empresa ${actionBean.businessName}</div></td>
	  </tr>
    <tr>
      <td class="tableheader"><div style="padding:6px">E-mail:</div></td>
      <td class="tableheader"><div style="padding:6px">Nombre</div></td>
      <td class="tableheader"><div style="padding:6px">Apellidos</div></td>
    </tr>
    <tr><td colspan="3"><img src="img/spacer.gif" width="1" height="20" alt="" /></td></tr>
    <c:forEach var="i" begin="0" end="6">
    <tr>
      <td class="tablerow0"><stripes:text id="email${i}" name="email[${i}]" size="18" style="width:190px" /></td>
      <td class="tablerow0"><stripes:text id="firstName[${i}]" name="firstName[${i}]" style="width:190px" size="18" /></td>
      <td class="tablerow0"><stripes:text id="lastName[${i}]" name="lastName[${i}]" style="width:190px" size="18" /></td>
    </tr>
    </c:forEach>
    <tr><td colspan="3"><img src="img/spacer.gif" width="1" height="20" alt="" /></td></tr>
  </table>
  <div>
  <table width="590px" bgcolor="Gainsboro">
    <tr><td class="tablerow1"><stripes:radio id="isemployee" name="employee" value="1" checked="1" /></td><td class="tablerow1"><img src='img/spacer.gif' width='1' height='20' alt='' />Los usuarios son empleados de la empresa</td></tr>   
    <tr><td class="tablerow1"><stripes:radio id="iscollab" name="employee" value="0" /></td><td class="tablerow1"><img src='img/spacer.gif' width='1' height='20' alt='' />Los usuarios son colaboradores externos de la empresa</td></tr>  
    <tr><td class="tablerow1" colspan="2"><img src='img/spacer.gif' width='1' height='20' alt='' /></td></tr>
    <tr><td class="tablerow1"><stripes:checkbox name="approve" value="1" /></td><td class="tablerow1"><img src='img/spacer.gif' width='1' height='20' alt='' />Pueden aprobar facturas</td></tr>   
    <tr><td class="tablerow1"><stripes:checkbox name="settle" value="1" /></td><td class="tablerow1"><img src='img/spacer.gif' width='1' height='20' alt='' />Pueden liquidar notas de gasto</td></tr>   
    <tr><td class="tablerow1"><stripes:checkbox name="premium" value="1" /></td><td class="tablerow1"><img src='img/spacer.gif' width='1' height='20' alt='' />Pueden consumir cr&eacute;ditos premium</td></tr>
    <tr><td colspan="2"><img src="img/spacer.gif" width="1" height="20" alt="" /></td></tr>
  </table>
  </div>
  <div id="inviteusererrors" style="height:0px;clear:both;line-height:normal" ></div>
  <div style="margin-top:20px">
    <center><input id="invitebutton" type="button" class="submit" value="Invitar" onclick="inviteUsers()" /><img id="sending" src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" style="display:none" /></center>
  </div>
  </c:otherwise>
  </c:choose>
  </stripes:form>
</div>