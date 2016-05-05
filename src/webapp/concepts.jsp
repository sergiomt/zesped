<?xml version="1.0" ?><complete><%@ page import="java.util.Collection,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item,com.zesped.Log,com.zesped.DAO,com.zesped.model.Concept,com.zesped.model.TaxPayer,com.zesped.model.BillNotes" language="java" session="true" contentType="text/xml" %><%

  final String sTaxPayerId = request.getParameter("recipient");
  final String sEmployeeUuid = request.getParameter("employee");
  String sOnlyOpen = request.getParameter("onlyopen");
  final String sNickN = (String) session.getAttribute("nickname");
  final String sPassw = (String) session.getAttribute("password");

  if (null==sTaxPayerId) return;
  if (sTaxPayerId.length()==0) return;

  boolean bOnlyOpen;
  if (sOnlyOpen==null)
	  bOnlyOpen = false;
  else
	  bOnlyOpen = sOnlyOpen.equals("1");

  AtrilSession oSes = null;
  try {
	  if (null!=sTaxPayerId) { if (sTaxPayerId.length()>0) { 
	    oSes = DAO.getSession("concepts", sNickN, sPassw);  
	    TaxPayer oTaxPayer = new TaxPayer(oSes.getDms(), sTaxPayerId);
	    Collection<Concept> oConcepts;
	    BillNotes oBln = oTaxPayer.billnotes(oSes);
	    if (bOnlyOpen)
	      oConcepts  = oBln.openConceptsForEmployee(oSes,sEmployeeUuid);
	    else
		      oConcepts  = oBln.allConceptsForEmployee(oSes,sEmployeeUuid);	    	
	    boolean b1st = true;
	    for (Concept c : oConcepts) {
			  out.write("<option value=\""+c.getName()+"\" "+(b1st ? "selected=\"selected\"" : "")+">"+c.getName()+"</option>");
			  b1st = false;
	      }
	  } } // fi
  } catch (Exception xcpt) {
		  Log.out.error("concepts.jsp "+xcpt.getClass()+" "+xcpt.getMessage());
	} finally {
	  if (oSes!=null) {
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
    }
  }
%></complete>