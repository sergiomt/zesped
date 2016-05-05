<%@ page import="java.util.MissingResourceException" language="java" session="true" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/barelogo.jsp" pageTitle="Registro Confirmado">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
			<h2>Confirmaci&oacute;n de registro</h2>
			<div id="errormessagebox">
			Gracias por registrarse en zesped.
			<br/>
			En unos minutos recibir&aacute; un correo electr&oacute;nico de confirmaci&oacute;n.
			<br/>
			Haga clic en el enlace que encontrar&aacute; en el correo para acceder a zesped.
			</div>
			<br/><br/>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>