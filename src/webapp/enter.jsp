<%@ page import="java.util.MissingResourceException" language="java" session="false" %><%@ include file="inc/atril.jspf" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%

final String sErrCode = nullif(request.getParameter("e"),"unknown");

%><stripes:layout-render name="/inc/default.jsp" pageTitle="Acceso">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
			<h2>Acceso</h2>
			<div id="errormessagebox">
<%		try {
	      out.write(StripesResources.getString("error."+sErrCode));
      } catch (MissingResourceException mre) {
	      out.write(StripesResources.getString("error.unknown"));    	  
      }
%>	
			</div>
			<br/><br/>
			<form name="logfrm" method="POST">
			<table align="center" class="loginbox">
		    <tr>
			    <td class="formlabelrequired"><%=StripesResources.getString("user.email") %></td>
			    <td><input type="text" name="email" size="25" class="formfields" style="text-transform:lowercase" /></td>
			  </tr>
			  <tr> 
			    <td class="formlabelrequired"><%=StripesResources.getString("user.password") %></td>
			    <td><input type="password" name="passw" size="12" class="formfields" /></td>
			  </tr>
        <tr>
			    <td></td>
			    <td>
			      <input id="loginbutton" type="button" class="login" value="Entrar" />
			      <img id="loading" src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" style="display:none" />
			    </td>
			  </tr>			  
        <tr>
			    <td colspan="2">
	     		<br/><br/>
			    <a href="RecoverPassword.action"><%=StripesResources.getString("user.forgotpassword") %></a>
          <br/><br/>
			    <a href="SignUpForm.action"><%=StripesResources.getString("user.registernewuser") %></a>
			    </td>
			  </tr>			  
			</table>
			</form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>