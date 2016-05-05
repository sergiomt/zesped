<%@ page import="java.util.ArrayList,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item,com.zesped.Log,com.zesped.DAO,com.zesped.model.Employee,com.zesped.model.TaxPayer,com.zesped.model.Employees" language="java" session="true" contentType="text/plain" %><%

  final String sTaxPayerId = request.getParameter("taxpayer");
  final String sNickN = (String) session.getAttribute("nickname");
  final String sPassw = (String) session.getAttribute("password");

  if (null==sTaxPayerId) return;
  if (sTaxPayerId.length()==0) return;

  AtrilSession oSes = null;
  try {
	  if (null!=sTaxPayerId) { if (sTaxPayerId.length()>0) { 
	    oSes = DAO.getSession("employees", sNickN, sPassw);  
	    TaxPayer oTaxPayer = new TaxPayer(oSes.getDms(), sTaxPayerId);
	    Employees oEmps = oTaxPayer.employees(oSes);
	    ArrayList<Employee> oEmployees;
	    if (oEmps==null)
	    	oEmployees = new ArrayList<Employee>();
	    else
	    	oEmployees = oEmps.list(oSes);
	    boolean b1st = true;
	    for (Employee e : oEmployees) {
			  if (b1st) b1st = false; else out.write("\n");
	    	out.write(e.getUuid()+"`"+e.getName());
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