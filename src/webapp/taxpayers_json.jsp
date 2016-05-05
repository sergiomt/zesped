[<%@ page import="java.util.Collection,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,com.zesped.model.Cache,com.zesped.Log,com.zesped.DAO,com.zesped.model.CustomerAccount,com.zesped.model.TaxPayer" language="java" session="true" contentType="text/json;charset=UTF-8" %><%

  final String sNickN = (String) session.getAttribute("nickname");
  final String sPassw = (String) session.getAttribute("password");
  final String sKeyword = request.getParameter("keyword").toUpperCase();

  Log.out.debug("taxpayers_json.jsp?keyword="+sKeyword);

  AtrilSession oSes = null;
  StringBuffer oBuf = new StringBuffer();
  boolean b1st = true;
  try {
			Collection<TaxPayer> oTxprs = null;
			try {
				oTxprs = (Collection<TaxPayer>) Cache.getEntry(session.getAttribute("customer_account_docid")+"taxpayers");
			} catch (Exception e) {
				Log.out.error("Cache.getEntry("+session.getAttribute("customer_account_docid")+"taxpayers) "+e.getClass().getName()+" "+e.getMessage(), e);
			}
			if (null==oTxprs) {
			    oSes = DAO.getSession("taxpayers_json", sNickN, sPassw);
				  Dms oDms = oSes.getDms();
				  CustomerAccount oCac = new CustomerAccount(oDms, (String) session.getAttribute("customer_account_docid"));
				  oTxprs = oCac.taxpayers(oSes).list(oSes);
				  oSes.disconnect();
				  oSes.close();
					Cache.putEntry(session.getAttribute("customer_account_docid")+"taxpayers", oTxprs);
			}
			for (TaxPayer t : oTxprs) {		
				if (t.getBusinessName().toUpperCase().startsWith(sKeyword))
				  oBuf.append((b1st ? "" : ",")+"{\"caption\":\""+t.getBusinessName()+"\",\"value\":\""+t.id()+"\"}");
				b1st = false;				
			}
  } catch (Exception xcpt) {
		  Log.out.error("taxpayers_json.jsp "+xcpt.getClass()+" "+xcpt.getMessage());
	} finally {
	  if (oSes!=null) {
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
    }
  }
	out.write(oBuf.toString());
%>]