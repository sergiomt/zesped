<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  <div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
	  <div class="darkbox" style="height:18px"><div style="padding:6px"><big>${actionBean.action=="forward" ? "Reenviar" : "Responder"}</big></div></div>
	<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	<div id="sendmessageerrors" style="height:0px" ></div>
	<stripes:form name="messageData" beanclass="com.zesped.action.ReplyMessage">
	<stripes:hidden name="id" />
	<stripes:hidden name="threadId" />
  <table width="590px">
    <tr height="20"><td width="100px" class="formlabelrequired">Remitente</td><td width="490px" class="formfields">${actionBean.sender}</td></tr>
    <c:choose>
      <c:when test="${actionBean.action=='forward'}">
        <tr height="20">
          <td width="100px" class="formlabelrequired">Destinatario</td>
          <td width="490px" class="formfields"><stripes:select name="recipient"><stripes:options-collection collection="${actionBean.recipients}" value="nickName" label="fullName" /></stripes:select></td>
        </tr>
      </c:when>
      <c:otherwise>
        <tr height="20">
          <td width="100px" class="formlabelrequired">Destinatario</td>
          <td width="490px" class="formfields"><stripes:select name="recipient"><option value="${actionBean.senderId}">${actionBean.sender}</option></stripes:select></td>
        </tr>
      </c:otherwise>
    </c:choose>
    <tr height="20">
      <td width="100px" class="formlabelrequired">Asunto</td>
      <td width="490px" class="formfields"><stripes:text name="subject" size="55" /></td></tr>
    <tr height="10"><td colspan="2"><img src="img/spacer.gif" width="1" height="10" alt="" /></td></tr>
    <tr height="195">
      <td width="100px" class="formlabelrequired"></td>
      <td width="490px" align="left" valign="top">
        <stripes:textarea name="body" rows="10" cols="60" />
      </td>
    </tr>
    <tr height="10"><td colspan="2"><img src="img/spacer.gif" width="1" height="10" alt="" /></td></tr>

    <tr height="50"><td colspan="2" align="center">
      <input type="button" class="submit" value="Enviar" onclick="sendMessage()" />
    </td></tr>
  </table>
  </stripes:form>
