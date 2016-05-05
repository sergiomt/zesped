<%@ page import="java.io.InputStream,java.util.zip.ZipOutputStream,java.util.zip.ZipEntry,java.text.SimpleDateFormat,java.util.Date,com.knowgate.misc.NameValuePair,com.zesped.Log,com.zesped.DAO,com.zesped.model.Invoice,com.zesped.model.InvoicePage,com.zesped.model.QueryResultSet,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item" language="java" session="true" contentType="application/zip" %><%

    final String sNickN = (String) session.getAttribute("nickname");
    final String sPassw = (String) session.getAttribute("password");

    SimpleDateFormat oDtFtm = new SimpleDateFormat("yyyyMMdd");

    response.setHeader("Content-Disposition","attachment; filename=\"zesped-facturas-"+oDtFtm.format(new Date())+".zip\"");

    com.zesped.action.QueryInvoices oAbn = (com.zesped.action.QueryInvoices) request.getAttribute("actionBean");

	  QueryResultSet<Invoice> oInvs = oAbn.getResultSet();	  
    
	  ZipOutputStream oZip = new ZipOutputStream(response.getOutputStream());
	  
	  AtrilSession oSes = null;
	  try {
		    oSes = DAO.getSession("invoices_zip", sNickN, sPassw);
		    Dms oDms = oSes.getDms();
		    for (Invoice i : oInvs) {
					Invoice oInv = new Invoice(oDms, i.id());
					int p = 0;
					for (InvoicePage oPag : oInv.pages(oSes)) {
						String sExt = "jpg";
						String sMime = oPag.item().mimeType();
						if (sMime!=null) {
							int iSlash = sMime.indexOf('/');
							if (iSlash>0)
								sExt = sMime.substring(++iSlash);
						}
						ZipEntry oEnt = new ZipEntry(oPag.id()+"_"+String.valueOf(++p)+(sExt.length()>0 ? "."+sExt : "")); 
						oZip.putNextEntry(oEnt);
						byte[] byDoc = oPag.item().getBytes();
						if (byDoc!=null)
							oZip.write(byDoc, 0, byDoc.length); 							
						oZip.closeEntry();
					}
		    }
		    oZip.finish();
	  } catch (Exception xcpt) {
			  Log.out.error("invoices_zip.jsp "+xcpt.getClass()+" "+xcpt.getMessage(),xcpt);
		} finally {
		  if (oSes!=null) {
			  if (oSes.isConnected()) oSes.disconnect();
		    if (oSes.isOpen())  oSes.close();
	    }
	  }  
	  
	  if (true) return;		
%>