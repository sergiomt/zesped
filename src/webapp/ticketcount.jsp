<%@ page import="es.ipsa.atril.sec.authentication.AtrilSession,com.zesped.DAO,com.zesped.Log,com.zesped.model.AccountingAccount" session="true" contentType="text/plain" %><%

final String sAccountUuid = request.getParameter("uuid");
final String sNickN = (String) session.getAttribute("nickname");
final String sPassw = (String) session.getAttribute("password");

if (null==sAccountUuid) { out.write("0"); return; }
if (sAccountUuid.length()==0) { out.write("0"); return; }

AtrilSession oSes = null;
try {
	    oSes = DAO.getSession("ticketcount", sNickN, sPassw);
	    AccountingAccount oAcc = new AccountingAccount(oSes, sAccountUuid);
	    out.write (String.valueOf(oAcc.tickets(oSes).size()));
} catch (Exception xcpt) {
		  Log.out.error("ticketcount.jsp "+xcpt.getClass()+" "+xcpt.getMessage());
} finally {
	  if (oSes!=null) {
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
    }
}
%>