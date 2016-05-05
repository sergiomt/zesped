<%@ page import="es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item,es.ipsa.atril.sec.authentication.AtrilSession,com.zesped.DAO,com.zesped.Log,com.knowgate.dfs.StreamPipe" %><%

final String sItemId = request.getParameter("i");
final String sNickN = (String) session.getAttribute("nickname");
final String sPassw = (String) session.getAttribute("password");

if (null==sItemId) return;
if (sItemId.length()==0) return;

AtrilSession oSes = null;
try {
	    oSes = DAO.getSession("item", sNickN, sPassw);
	    Dms oDms = oSes.getDms();
	    Document oDoc = oDms.getDocument(sItemId);
	    Item oItm = oDoc.item();
	    response.setContentType("image/jpeg");
	    new StreamPipe().between(oItm.getInputStreamTranscodedToMime("image/jpeg"), response.getOutputStream());
} catch (Exception xcpt) {
		  Log.out.error("item.jsp "+xcpt.getClass()+" "+xcpt.getMessage());
	} finally {
	  if (oSes!=null) {
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
  }
}

if (true) return;
%>