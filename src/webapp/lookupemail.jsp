<%@ page import="es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.exceptions.ElementNotFoundException,com.zesped.Log,com.zesped.DAO,com.zesped.model.User" language="java" session="true" contentType="text/plain" %><%

final String sEmail = request.getParameter("email");
final String sArgs = request.getParameter("args");

AtrilSession oSes = null;
try {
	if (null!=sEmail) { if (sEmail.length()>0) { 
	    String sDocId = User.forEmail(sEmail);
	    oSes = DAO.getAdminSession("lookupemail");
	    User oUsr = new User(oSes, sDocId);
	    out.write(oUsr.id()+"`"+oUsr.getNickName()+"`"+oUsr.getEmail()+"`"+oUsr.getFirstName()+"`"+oUsr.getLastName()+"`"+sArgs);
	} }
} catch (ElementNotFoundException enfe) {
  out.write("`````");
} catch (Exception xcpt) {
	  Log.out.error("lookupemail.jsp?email="+sEmail+" "+xcpt.getClass()+" "+xcpt.getMessage());
} finally {
    if (oSes!=null) {
	    if (oSes.isConnected()) oSes.disconnect();
      if (oSes.isOpen())  oSes.close();
    }
}
%>