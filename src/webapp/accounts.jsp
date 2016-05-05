<%@ page import="java.util.ArrayList,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item,com.zesped.Log,com.zesped.DAO,com.zesped.model.AccountingAccount,com.zesped.model.TaxPayer" language="java" session="true" contentType="text/plain" %><%

  final String sTaxPayerId = request.getParameter("recipient");
  final String sNickN = (String) session.getAttribute("nickname");
  final String sPassw = (String) session.getAttribute("password");

  if (null==sTaxPayerId) return;
  if (sTaxPayerId.length()==0) return;

  AtrilSession oSes = null;
  try {
	  if (null!=sTaxPayerId) { if (sTaxPayerId.length()>0) { 
	    oSes = DAO.getSession("concepts", sNickN, sPassw);  
	    TaxPayer oTaxPayer = new TaxPayer(oSes.getDms(), sTaxPayerId);
	    ArrayList<AccountingAccount> oAccounts = oTaxPayer.accounts(oSes).list(oSes);
	    boolean b1st = true;
	    for (AccountingAccount a : oAccounts) {
			  if (b1st) b1st = false; else out.write("\n");
	    	out.write(a.getUuid()+"`"+a.getDescription());	    	
	    }
	  } } // fi
  } catch (Exception xcpt) {
		  out.write("error`"+xcpt.getClass()+" "+xcpt.getMessage());
	} finally {
	  if (oSes!=null) {
		  oSes.commit();
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
    }
  }
%>