<%@ page import="es.ipsa.atril.SortableList,es.ipsa.atril.eventLogger.AtrilEvent,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.documentindexer.DocumentIndexer,com.zesped.DAO,com.zesped.model.User,com.zesped.SessionCache" language="java" session="true" contentType="text/plain;charset=UTF-8" %><%

final String sNick = (String) request.getSession().getAttribute("nickname");
final String sPass = (String) request.getSession().getAttribute("password");

request.getSession().removeAttribute("nickname");
request.getSession().removeAttribute("password");
request.getSession().removeAttribute("fullname");
request.getSession().removeAttribute("businessname");
request.getSession().removeAttribute("user_uuid");
request.getSession().removeAttribute("user_docid");
request.getSession().removeAttribute("customer_acount");
request.getSession().removeAttribute("customer_account_docid");
request.getSession().removeAttribute("taxpayer_docid");
request.getSession().removeAttribute("employee_uuid");
request.getSession().removeAttribute("taxpayer_docid");
request.getSession().removeAttribute("role");

if (sNick!=null) {
	
	AtrilSession oSes = null;
	try {
	  oSes = DAO.getSession("logout", sNick, sPass);
	  Dms oDms = oSes.getDms();
		SortableList<Document> oLst = oDms.query("User$user_uuid='"+sNick+"'");
		if (oLst.isEmpty()) {
			DAO.log(oSes, User.class, "LOGOUT", AtrilEvent.Level.WARNING, sNick+" not found");
		} else {
		  Document oDoc = oLst.get(0);
		  DAO.log(oSes, oDoc, User.class, "LOGOUT", AtrilEvent.Level.INFO, oDoc.attribute("email").toString());
		}
    SessionCache.disposeSession(session.getId());
	} catch (Exception xcpt) {
		if (oSes!=null) DAO.log(oSes, User.class, "LOGOUT", AtrilEvent.Level.ERROR, xcpt.getClass().getName()+" "+sNick+" "+xcpt.getMessage());		
	} finally {
		if (oSes!=null) {
			if (oSes.isConnected()) oSes.disconnect();
			if (oSes.isOpen()) oSes.close();
	  }
  }
}

response.sendRedirect(response.encodeRedirectUrl("index.jsp"));
%>