<%@ page import="com.knowgate.misc.Gadgets,com.zesped.DAO,com.zesped.Log,com.zesped.model.User,com.zesped.model.TaxPayer,es.ipsa.atril.SortableList,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.sec.exceptions.AuthenticationException" language="java" session="true" contentType="text/xml;charset=UTF-8" %><%

  response.addHeader ("Pragma", "no-cache");
  response.addHeader ("cache-control", "no-store");
  response.setIntHeader("Expires", 0);

  final String sNickN = (String) session.getAttribute("nickname");
  final String sPassw = (String) session.getAttribute("password");
  
  String doctype = request.getParameter("doctype");
  String textcolumn = request.getParameter("text");
  String infocolumn = request.getParameter("info");
  String lookfor = request.getParameter("lookfor");
  
  AtrilSession oSes = null;
  
  try {
	  oSes = DAO.getAdminSession("autosuggest");
	  User oUser = new User(oSes, (String) session.getAttribute("user_docid"));
    oSes.disconnect();
	  oSes.close();	  
	  oSes = DAO.getSession("autosuggest", sNickN, sPassw);
	  Dms oDms = oSes.getDms();
	  String sQry = "(" + doctype + "$customer_acount='"+session.getAttribute("customer_acount")+"')";
		if (lookfor!=null) if (lookfor.length()>0) sQry+=" & (" + doctype + "$"+textcolumn+"='"+lookfor+"')";
		SortableList<Document> oLst = (SortableList<Document>) oDms.query(sQry);
		if (!oLst.isEmpty()) {
			out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?><results count=\""+String.valueOf(oLst.size())+"\">");
			for (Document d : oLst)	{
			  if (doctype.equals("TaxPayer")) {
				  if (oUser.isAllowedAt(d.id()))
						out.write("<rs id=\""+d.id()+"\" info=\"\"><![CDATA["+d.attribute(textcolumn).toString()+"]]></rs>");				  				  
			  } else {
					out.write("<rs id=\""+d.id()+"\" info=\"\"><![CDATA["+d.attribute(textcolumn).toString()+"]]></rs>");				  
			  }
			}
			out.write("</results>");
		} else {
			out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?><results count=\"0\" />");
		}
  } catch (NumberFormatException xcpt) {
		out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?><results count=\"0\" />");
	  Log.out.error("concepts.jsp "+xcpt.getClass()+" "+xcpt.getMessage());
  } catch (AuthenticationException acpt) {
		out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?><results count=\"0\" />");
		Log.out.error("concepts.jsp "+acpt.getClass()+" "+acpt.getMessage());	  
  } finally {
    if (oSes!=null) {
	    if (oSes.isConnected()) oSes.disconnect();
      if (oSes.isOpen())  oSes.close();
    }
  }

%>