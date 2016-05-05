<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/default.jsp" pageTitle="Contacto">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
			<h2>CONTACTO</h2>
			<stripes:form beanclass="com.zesped.action.ContactForm">
			<stripes:messages/>
      <stripes:errors/>
			<table>
			  <tr>
			    <td class="formlabelrequired"><stripes:label for="message.displayName" /></td>
			    <td class="formfields"><stripes:text name="name" style="width:400px" /></td>
			  </tr>
			  <tr>
			    <td class="formlabelrequired"><stripes:label for="message.email" /></td>
			    <td class="formfields"><stripes:text name="email" style="width:400px" /></td>
			  </tr>
			  <tr>
			    <td class="formlabelrequired"><stripes:label for="message.subject" /></td>
			    <td class="formfields"><stripes:text name="subject" style="width:400px" /></td>
			  </tr>
			  <tr>
			    <td class="formlabelrequired"><stripes:label for="message.text" /></td>
			    <td class="formfields"><stripes:textarea name="text" cols="70" rows="8" /></td>
			  </tr>
			  <tr>
			    <td></td>
			    <td class="formfields"><stripes:submit name="send" class="submit" /></td>
			  </tr>
			</table>
      </stripes:form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>