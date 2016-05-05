<%@ page import="java.util.ArrayList,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item,com.zesped.Log,com.zesped.DAO,com.zesped.model.Employee,com.zesped.model.TaxPayer,com.zesped.model.BillNote" language="java" session="true" contentType="text/plain" %><%

  final String sTaxPayerId = request.getParameter("taxpayer");
  final String sConcept = request.getParameter("concept");
  final String sNickN = (String) session.getAttribute("nickname");
  final String sPassw = (String) session.getAttribute("password");

  if (null==sTaxPayerId) return;
  if (sTaxPayerId.length()==0) return;
  if (null==sConcept) return;
  if (sConcept.length()==0) return;

  AtrilSession oSes = null;
  Employee oEmpl = null;
  try {
	    oSes = DAO.getSession("employeeforconcept", sNickN, sPassw);
	    TaxPayer oTxpr = new TaxPayer(oSes.getDms(), sTaxPayerId);
	    BillNote oBlte = oTxpr.billnotes(oSes).seek(oSes, sConcept);
			if (!oBlte.isNull("employee_uuid"))
				oEmpl = oTxpr.employees(oSes).seek(oSes, oBlte.getEmployeeUuid());
  } catch (Exception xcpt) {
		  Log.out.error("employeeforconcept.jsp "+xcpt.getClass()+" "+xcpt.getMessage());  
	} finally {
	    if (oSes!=null) {
		    oSes.commit();
		    if (oSes.isConnected()) oSes.disconnect();
	      if (oSes.isOpen())  oSes.close();
      }
  }
  if (oEmpl!=null) out.write(oEmpl.getUuid());
%>