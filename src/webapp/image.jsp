<%@ page import="java.io.InputStream,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item,com.zesped.DAO,com.knowgate.dfs.StreamPipe" language="java" session="true" contentType="image/jpeg" %><%

  final String sItmId = request.getParameter("i");
  final String sNickN = (String) session.getAttribute("nickname");
  final String sPassw = (String) session.getAttribute("password");

  if (null==sItmId) return;
  if (sItmId.length()==0) return;

  AtrilSession oSes = null;
  try {
	  oSes =   DAO.getSession("image", sNickN, sPassw);  
	  Dms oDms = oSes.getDms();
	  Document oDoc = oDms.getDocument(sItmId);
	  Item oItm = oDoc.item();
	  InputStream oIns = oItm.getInputStream();
	  StreamPipe oPipe = new StreamPipe();
	  oPipe.between(oIns, response.getOutputStream());
	  oIns.close();
  } finally {
	  if (oSes!=null) {
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
    }
  }
%>