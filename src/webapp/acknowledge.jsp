<%@ page language="java" session="false" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" %><%@ include file="inc/atril.jspf" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%

%><stripes:layout-render name="/inc/default.jsp" pageTitle="Mensaje enviado">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
			<h2>Confirmaci&oacute;n</h2>
			<div id="acknowledgemessagebox">
			Su mensaje ha sido enviado con &eacute;xito, en breve nos pondremos en contacto con usted para atenderle.
			</div>
			<br/><br/>
			<form><table align="center" class="loginbox">
        <tr>
			    <td></td>
			    <td>
			      <input type="button" class="login" value="Continuar" onclick="window.location='index.jsp'" />
			    </td>
			  </tr>
			</table></form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>