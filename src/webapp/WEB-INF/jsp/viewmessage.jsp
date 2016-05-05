<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<div id="messagebox" style="width:600px;height:400px">
  <div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
	  <div class="darkbox" style="height:18px"><div style="padding:6px"><big>Notificaci&oacute;n</big></div></div>
	<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	<div id="viewmessageerrors" style="height:0px;line-height:normal" ></div>
	<stripes:form name="messageData" beanclass="com.zesped.action.ViewMessage">
	<stripes:hidden name="id" />
	<stripes:hidden name="threadId" />
  <table width="590px">
    <tr height="20"><td width="100px" class="formlabelrequired">Remitente</td><td width="490px" class="formfields">${actionBean.sender}</td></tr>
    <tr height="20"><td width="100px" class="formlabelrequired">Destinatario</td><td width="490px" class="formfields">${actionBean.recipient}</td></tr>
    <tr height="20"><td width="100px" class="formlabelrequired">Fecha</td><td width="490px" class="formfields">${actionBean.dateFormatted}</td></tr>
    <tr height="20"><td width="100px" class="formlabelrequired">Asunto</td><td width="490px" class="formfields">${actionBean.subject}</td></tr>
    <tr height="10"><td colspan="2"><img src="img/spacer.gif" width="1" height="10" alt="" /></td></tr>
    <tr height="195"><td width="100px" class="formlabelrequired"></td><td width="490px" align="left" valign="top"><div style="overflow:scroll:width:100%;line-height:normal">${actionBean.body}</div></td></tr>
    <tr height="10"><td colspan="2"><img src="img/spacer.gif" width="1" height="10" alt="" /></td></tr>
    <c:if test="${not empty actionBean.relatedDocumentLink}">
    <tr height="20"><td></td><td align="left"><a href="${actionBean.relatedDocumentLink}">Documento&nbsp;relacionado</a></td></tr>
    </c:if>
    <tr height="50"><td colspan="2" align="center">
      <c:if test "${actionBean.senderId!=actionBean.recipientId}">
      <input type="button" class="submit" value="Responder" onclick="replyMessage('r','${actionBean.id}')" />&nbsp;&nbsp;&nbsp;&nbsp;
      </c:if>      
      <input type="button" class="submit" value="Reenviar" onclick="replyMessage('f','${actionBean.id}')" />&nbsp;&nbsp;&nbsp;&nbsp;
      <c:if test "${not actionBean.archived}">
      <input type="button" class="submit" value="Archivar" onclick="archiveMessage('${actionBean.id}')" />&nbsp;&nbsp;&nbsp;&nbsp;
      </c:if>      
      <input type="button" class="submit" value="Eliminar" onclick="deleteMessage('${actionBean.id}')" />
    </td></tr>
  </table>
  </stripes:form>
</div>