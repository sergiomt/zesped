<%@ page import="java.util.MissingResourceException" language="java" session="true" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/barelogo.jsp" pageTitle="Activacion">
  <stripes:layout-component name="contents">
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
			<h2>Confirmaci&oacute;n de activaci&oacute;n</h2>
			<div id="errormessagebox">
			<c:choose>
			<c:when test="${actionBean.changePassword }">
			  Bienvenido a zesped.
			  <br/>
			  Para acceder su cuenta ${actionBean.email} debe cambiar la contrase&ntilde;a de acceso.
			  <br/>
			  <stripes:form beanclass="com.zesped.action.ActivateUser">
			    <stripes:hidden name="id" />
			    <table align="center" class="loginbox">
				    <tr><td class="formlabelrequired"><stripes:label for="user.password" />:</td><td><stripes:password name="password" id="passw" size="18" /></td>
				    <tr><td class="formlabelrequired"><stripes:label for="user.password.confirm" />:</td><td><stripes:password name="password2" size="18" /></td>
				    <tr><td colspan="2">
				      <table summary="password strength metter"><tr>
				        <td><stripes:label for="user.password.strength" />:</td>
				        <td>&nbsp;</td>
				        <td><div id="strengthMeter" class="strengthMeter"><div id="scoreBar" class="scoreBar"></div></div></td>
				        <td>&nbsp;&nbsp;</td>
				        <td><div id='fuerza' class='textlowlight'></div></td>
				      </tr></table>
				      </td>
				    </tr>
				    <tr><td></td><td><stripes:submit class="submit" name="save" /></td><td></td></tr>
			    </table>
			  </stripes:form>
			</c:when>
			<c:otherwise>
			  Su cuenta ${actionBean.email} ha sido correctamente activada.
			  <br/>
			  Haga clic <a href="${ actionBean.hasOrders ? 'CaptureInvoice.action' : 'BuyCredits.action' }">aqu&iacute;</a> para acceder a zesped.
			</c:otherwise>
			</c:choose>
			</div>
			<br/><br/>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
      <script type="text/javascript" src="js/passwstrength.js" defer="defer"></script>
  </stripes:layout-component>
</stripes:layout-render>