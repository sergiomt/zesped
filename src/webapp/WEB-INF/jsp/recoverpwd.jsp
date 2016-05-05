<%@ page import="java.util.MissingResourceException" language="java" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" session="false" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/default.jsp" pageTitle="Recuperar Contraseña">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
			<h2><stripes:label for="recoverpassword.header" /></h2>
			<div id="errormessagebox"><stripes:label for="recoverpassword.form" /></div>
			<br/><br/>
			<stripes:form name="pwdrecfrm" beanclass="com.zesped.action.RecoverPassword">
			<stripes:messages/>
      <stripes:errors/>
			<table align="center" class="loginbox">
		    <tr>
			    <td class="formlabelrequired"><stripes:label for="user.email" /></td>
			    <td><stripes:text name="email" size="25" class="formfields" style="text-transform:lowercase" /></td>
			  </tr>
        <tr>
			    <td></td>
			    <td>
			      <stripes:submit class="login" name="send" />
			    </td>
			  </tr>			  
        <tr>
			    <td colspan="2">
          <br/><br/>
			    <a href="SignUpForm.action"><stripes:label for="user.registernewuser" /></a>
			    </td>
			  </tr>			  
			</table>
			</stripes:form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>