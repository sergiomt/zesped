<%@ page import="java.io.InputStream,java.util.zip.ZipOutputStream,java.util.zip.ZipEntry,java.text.SimpleDateFormat,java.util.Date,com.knowgate.misc.NameValuePair,com.zesped.Log,com.zesped.DAO,com.zesped.model.Ticket,com.zesped.model.TicketNote,com.zesped.model.QueryResultSet,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.doc.user.Dms,es.ipsa.atril.doc.user.Document,es.ipsa.atril.doc.user.Item" language="java" session="true" contentType="application/zip" %><%

    final String sNickN = (String) session.getAttribute("nickname");
    final String sPassw = (String) session.getAttribute("password");

    SimpleDateFormat oDtFtm = new SimpleDateFormat("yyyyMMdd");

    response.setHeader("Content-Disposition","attachment; filename=\"zesped-tickets-"+oDtFtm.format(new Date())+".zip\"");

    com.zesped.action.QueryTickets oAbn = (com.zesped.action.QueryTickets) request.getAttribute("actionBean");

	  QueryResultSet<Ticket> oInvs = oAbn.getResultSet();	  
    
	  ZipOutputStream oZip = new ZipOutputStream(response.getOutputStream());
	  
	  AtrilSession oSes = null;
	  try {
		    oSes = DAO.getSession("tickets_zip", sNickN, sPassw);
		    Dms oDms = oSes.getDms();
		    for (Ticket t : oInvs) {
		    	Ticket oTck = new Ticket(oDms, t.id());
					for (TicketNote oPag : oTck.pages(oSes)) {
						String sExt = "jpg";
						String sMime = oPag.item().mimeType();
						if (sMime!=null) {
							int iSlash = sMime.indexOf('/');
							if (iSlash>0)
								sExt = sMime.substring(++iSlash);
						}
						ZipEntry oEnt = new ZipEntry(oPag.id()+"_"+oPag.number()+(sExt.length()>0 ? "."+sExt : "")); 
						oZip.putNextEntry(oEnt);
						byte[] byDoc = oPag.item().getBytes();
						if (byDoc!=null)
							oZip.write(byDoc, 0, byDoc.length); 							
						oZip.closeEntry();
					}
		    }
		    oZip.finish();
	  } catch (Exception xcpt) {
			  Log.out.error("tickets_zip.jsp "+xcpt.getClass()+" "+xcpt.getMessage());
		} finally {
		  if (oSes!=null) {
			  if (oSes.isConnected()) oSes.disconnect();
		    if (oSes.isOpen())  oSes.close();
	    }
	  }  
	  
	  if (true) return;		
%>