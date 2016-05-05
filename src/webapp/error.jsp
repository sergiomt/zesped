<%@ page import="java.util.MissingResourceException" language="java" session="false" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" %><%@ include file="inc/atril.jspf" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%

final String sErrCode = nullif(request.getParameter("e"),"unknown");

%><stripes:layout-render name="/inc/default.jsp" pageTitle="Acceso">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
			<h2>Error</h2>
			<div id="errormessagebox">
<%		try {
	      out.write(StripesResources.getString("error."+sErrCode));
      } catch (MissingResourceException mre) {
	      out.write(StripesResources.getString("error.unknown"));    	  
      }
%>	
			</div>
			<br/><br/>
			<form><table align="center" class="loginbox">
        <tr>
			    <td></td>
			    <td>
			      <input type="button" class="login" value="Continuar" onclick="window.history.back()" />
			    </td>
			  </tr>
			</table></form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>