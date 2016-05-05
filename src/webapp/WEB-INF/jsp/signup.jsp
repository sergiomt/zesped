<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/default.jsp" pageTitle="Registro de Nuevo Usuario">
  <stripes:layout-component name="contents">
  		<jsp:include page="/inc/darkmenu.jspf"/>
      <div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
        <h2><stripes:label for="signup.form" /></h2>
        <stripes:messages/>
        <stripes:errors/>
				<stripes:form beanclass="com.zesped.action.SignUpForm">
				  <table summary="Signup Form">
				    <tr><td class="formlabelrequired"><stripes:label for="user.firstName" />:</td><td><stripes:text name="user.firstName" size="40" /></td>
				    <tr><td class="formlabelrequired"><stripes:label for="user.lastName" />:</td><td><stripes:text name="user.lastName" size="40" /></td>
				    <tr><td class="formlabelrequired"><stripes:label for="user.email" />:</td><td><stripes:text name="user.email" size="40" style="text-transform:lowercase" /></td>
				    <tr><td class="formlabelrequired"><stripes:label for="user.password" />:</td><td><stripes:password name="user.password" id="passw" size="18" /></td>
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
      			<tr>
    				  <td align="right"><img width="70" height="25" src="captcha.jsp" alt="Captcha" />&nbsp;</td>
    		      <td><stripes:text name="captcha" size="8" />&nbsp;<stripes:label for="captcha.enter" /></td>
    				</tr>
				    <tr><td class="formlabelrequired"><stripes:checkbox name="accept" value="1"/></td><td><stripes:label for="terms.accept" /></td>
				    <tr><td></td><td><stripes:submit class="submit" name="save" /></td><td></td></tr>
				  </table>				
				</stripes:form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
      <script type="text/javascript" src="js/passwstrength.js" defer="defer"></script>
  </stripes:layout-component>
</stripes:layout-render>