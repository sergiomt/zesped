<%@ page import="java.util.Date,java.text.SimpleDateFormat,com.zesped.model.Message,com.zesped.action.ListMessages" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  ListMessages oAbn = (ListMessages) request.getAttribute("actionBean");
  SimpleDateFormat oDtt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
  SimpleDateFormat oDtm = new SimpleDateFormat("HH:mm");
  Date dtToday = new Date();
  final int y=dtToday.getYear(), l=dtToday.getMonth(), d=dtToday.getDate();
%><stripes:form id="listmessages" name="listmessages" beanclass="com.zesped.action.ListMessages">
<table align="center" width="790px">
  <tr>
		<td class="tabletabbedcontrols" colspan="4">
		<div style="font-size:medium;float:left">Notificaciones</div>
		<div style="float:left;clear:left;">
		  <table>
		    <tr>
		      <td id="faketab2" class="faketab"><a class="anchorhighlight" href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') { document.getElementById('notificaciones').innerHTML = httpRequestText('ListMessages.action?f=2&o=0');document.getElementById('faketab2').className='faketabhighlight'; } else { document.location = 'error.jsp?e=expiredsession'; }">Recibidas</a></td>
		      <td id="faketab1" class="faketab"><a class="anchorhighlight" href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') { document.getElementById('notificaciones').innerHTML = httpRequestText('ListMessages.action?f=1&o=0');document.getElementById('faketab1').className='faketabhighlight'; } else { document.location = 'error.jsp?e=expiredsession'; }">Enviadas</a></td>
		      <td id="faketab3" class="faketab"><a class="anchorhighlight" href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') { document.getElementById('notificaciones').innerHTML = httpRequestText('ListMessages.action?f=3&o=0');document.getElementById('faketab3').className='faketabhighlight'; } else { document.location = 'error.jsp?e=expiredsession'; }">Archivadas</td>
		  </table>
		</div>
		<div style="float:right">
			&nbsp;<a id="archive" class="anchorhighlight" href="#">Archivar mensajes seleccionados</a>		
		</div>
		</td>
	</tr>
  <tr>
  	<td class="tableheader">&nbsp;&nbsp;&nbsp;Asunto</td>
    <td class="tableheader">${actionBean.folder==2 ? "Remitente" : "Destinatario"}</td>
    <td class="tableheader">Fecha</td>
    <td class="tableheader" align="center"><a href="#" id="fakecheckbox"><img src="img/fakecheckbox16.gif" width="12" height="12" border="0"></a></td>
  </tr>
  <% String r = " 0";
     int n = 0;
     for (Message m : oAbn.getMessages()) { 
         r = (r.equals("0") ? "1" : "0"); %>         
  <tr>
  	<td class="tablerow<%=r%>">&nbsp;<a href="ViewMessage.action?id=<%=m.getId()%>" rel="lightbox"><%=m.getReaded() ? m.getSubject() : "<strong>"+m.getSubject()+"</strong>"%></a></td>
    <td class="tablerow<%=r%>"><%=oAbn.getFolder()==2 ? m.getSenderDisplayName() : m.getRecipientDisplayName() %></td>
    <td class="tablerow<%=r%>"><% if (m.getDate().getYear()==y && m.getDate().getMonth()==l && m.getDate().getDate()==d) out.write(oDtm.format(m.getDate())); else out.write(oDtt.format(m.getDate())); %></td>
    <td class="tablerow<%=r%>" align="center"><stripes:checkbox name="checkedValues<%=String.valueOf(n)%>" value="<%=m.getId()%>" /></td>
  </tr>
  <%  n++; }%>
  <tr>
  	<td class="tablefooter" colspan="4"></td>
  </tr>
</table>
</stripes:form>
