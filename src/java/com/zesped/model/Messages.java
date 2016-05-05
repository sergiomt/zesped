package com.zesped.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;
import com.zesped.util.MailSessionHandler;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Messages extends BaseCollectionObject {

	private static final long serialVersionUID = 1L;

	public Messages() {
		super("Messages", Message.class);
	}

	@Override
	public Attr[] attributes() {
		return null;
	}

	public static Messages top(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException {
		Zesped z = Zesped.top(oSes);
		Messages m = new Messages();
		for (Document d : z.getDocument().children()) {
			  if (d.type().name().equals(m.getTypeName())) {
				  m.setDocument(oSes.getDms().getDocument(d.id()));
				  break;
			  }
		} // next
		if (m.getDocument()==null) throw new ElementNotFoundException(m.getTypeName()+" document not found");
		return m;
	}

	
	private static void sendEmail(String sSubject, String sSenderDisplayName, String sSenderEmail, String[] aRecipients, String sBody, String sDocType, String sDocId)
			throws IOException, NullPointerException, IllegalArgumentException, SecurityException, MessagingException {
  		Log.out.debug("Begin Messages.sendEmail("+sSubject+","+sSenderDisplayName+","+sSenderEmail+")");
		String sBodyTxt, sBodyHtml;
  		sBodyTxt=sBodyHtml=sBody;
		if (sDocType!=null && sDocType!=null)
  			if (sDocType.equals("Invoice")) {
  				sBodyTxt  += "\nFactura relacionada: http://www.zesped.com/zesped/EditInvoice.action?a=0&id="+sDocId;
  				sBodyHtml += "<br/><a href=\"http://www.zesped.com/zesped/EditInvoice.action?a=0&id="+sDocId+"\">Factura relacionada</a>";
  			} else if (sDocType.equals("BillNote")) {
  				sBodyTxt  += "\nNota de gasto relacionada: http://www.zesped.com/zesped/EditBillNote.action?a=0&id="+sDocId;
  				sBodyHtml += "<br/><a href=\"http://www.zesped.com/zesped/EditBillNote.action?a=0&id="+sDocId+"\">Nota de gasto relacionada</a>";  				
  			}
		ByteArrayOutputStream oByOut = new ByteArrayOutputStream();
  		PrintStream oPrt = new PrintStream(oByOut);
  		MailSessionHandler oHlr = new MailSessionHandler();
  		oHlr.sendMessage(sSubject, sSenderDisplayName, sSenderEmail, sSenderEmail,
	                    aRecipients, RecipientType.TO,
	                     sBodyTxt, "<html><body>"+sBodyHtml+"</body></html>",
	                     "UTF-8", null, null, null, oPrt);
  		oPrt.close();
  		Log.out.info(oByOut.toString());
  		oByOut.close();
  		Log.out.debug("End Messages.sendEmail()");
	}

	public static Message inquire(AtrilSession oSes, String sSenderDisplayName, String sSenderEmail, String sSenderUuid, String sSubject, String sBody)
		throws RuntimeException, NullPointerException, NotYetConnectedException, DmsException, NotEnoughRightsException, IOException, MessagingException {
  		Log.out.debug("Begin Messages.inquire("+sSenderDisplayName+","+sSenderEmail+","+sSubject+")");
		Dms oDms = oSes.getDms();
		Message m = new Message();
		m.setDocument(oDms.newDocument(oDms.getDocumentType("Message"), top(oSes).getDocument()));
		m.put("thread_id", Gadgets.generateUUID());
		m.put("sent_date", new Date());
		m.put("sender_displayname", sSenderDisplayName);
		m.put("sender_email", sSenderEmail);
		if (sSenderUuid!=null) if (sSenderUuid.length()>0) m.put("sender_id", sSenderUuid);
		m.put("message_subject", sSubject);
		m.put("message_body", sBody);
		m.put("is_archived", "0");
		m.put("message_type", "inquiry");
		m.save(oSes);
		sendEmail(sSubject, sSenderDisplayName, sSenderEmail,new String[] {"sergiom@knowgate.com"},sBody,null,null);
  		Log.out.debug("End Messages.inquire()");
		return m;
	}

	public static Message reply(AtrilSession oSes, String sThreadId,
								String sSenderDisplayName, String sSenderEmail, String sSenderUuid,
								String sRecipientEmail, String sRecipientUuid,
								String sSubject, String sBody)
			throws RuntimeException, NullPointerException, NotYetConnectedException, DmsException, NotEnoughRightsException, IOException, MessagingException {
  			Log.out.debug("Begin Messages.reply("+sThreadId+","+sSenderDisplayName+","+sSenderEmail+","+sSubject+")");
			Dms oDms = oSes.getDms();
			Message m = new Message();
			m.setDocument(oDms.newDocument(oDms.getDocumentType("Message"), top(oSes).getDocument()));
			m.put("thread_id", sThreadId);
			m.put("sent_date", new Date());
			m.put("sender_displayname", sSenderDisplayName);
			m.put("sender_email", sSenderEmail);
			if (sSenderUuid!=null)
				if (sSenderUuid.length()>0)
					m.put("sender_id", sSenderUuid);
			m.put("recipient_email", sRecipientEmail);
			if (sRecipientUuid!=null)
				if (sRecipientUuid.length()>0)
					m.put("recipient_id", sRecipientUuid);
			m.put("message_subject", sSubject);
			m.put("message_body", sBody);
			m.put("is_archived", "0");
			m.put("message_type", "reply");
			m.save(oSes);
			sendEmail(sSubject, sSenderDisplayName, sSenderEmail, new String[] {sRecipientEmail}, sBody, null, null);
	  		Log.out.debug("End Messages.reply()");
			return m;
		}
	
	public static Message notify(AtrilSession oSes,
								 String sSenderDisplayName, String sSenderEmail, String sSenderUuid,
								 String sRecipientEmail, String sRecipientUuid,
								 String sSubject, String sBody, String sDocId)
		throws RuntimeException, NullPointerException, NotYetConnectedException, DmsException, NotEnoughRightsException, IOException, MessagingException, ElementNotFoundException {
		Log.out.debug("Begin Messages.notify("+sSenderDisplayName+","+sSenderEmail+","+sSenderUuid+","+sRecipientEmail+","+sRecipientUuid+","+sSubject+","+sDocId+")");
		if (sDocId==null) sDocId = "";
		Dms oDms = oSes.getDms();
		Message m = new Message();
		m.setDocument(oDms.newDocument(oDms.getDocumentType("Message"), top(oSes).getDocument()));
		m.put("thread_id", Gadgets.generateUUID());
		m.put("sent_date", new Date());
		m.put("sender_displayname", sSenderDisplayName);
		m.put("sender_email", sSenderEmail);
		if (sSenderUuid!=null) if (sSenderUuid.length()>0) m.put("sender_id", sSenderUuid);
		m.put("recipient_email", sRecipientEmail);
		if (sRecipientUuid!=null) if (sRecipientUuid.length()>0) m.put("recipient_id", sRecipientUuid);
		m.put("message_subject", sSubject);
		m.put("message_body", sBody);
		m.put("is_archived", "0");
		if (sDocId.length()>0) {
			m.put("message_type", "incident");
			m.put("related_document", sDocId);
			m.save(oSes);
			sendEmail(sSubject, sSenderDisplayName, sSenderEmail,new String[] {sRecipientEmail},sBody,oDms.getDocument(sDocId).type().name(),sDocId);
		} else {
			m.put("message_type", "notification");
			m.save(oSes);
			sendEmail(sSubject, sSenderDisplayName, sSenderEmail,new String[] {sRecipientEmail},sBody,null,null);
		}
  		Log.out.debug("End Messages.notify()");
		return m;
	}
	
	public static ArrayList<Message> list(AtrilSession oSes, String sUserUuid,
										  int iFolder, int iOffset, int iMaxMsgs,
										  String sSortBy, boolean bAscending)
	    throws IllegalArgumentException, IllegalStateException, DmsException {
		String sQry;
		Log.out.debug("Begin Messages.list("+sUserUuid+","+String.valueOf(iFolder)+","+sSortBy+")");
		switch (iFolder) {
			case Messages.FOLDER_SENT:
				sQry = "Message & ($sender_id='"+sUserUuid+"' & $is_archived='0')";
				break;
			case Messages.FOLDER_RECEIVED:
				sQry = "Message & ($recipient_id='"+sUserUuid+"' & $is_archived='0')";
				break;
			case Messages.FOLDER_ARCHIVED:
				sQry = "Message & (($sender_id='"+sUserUuid+"' | $recipient_id='"+sUserUuid+"') & $is_archived='1')";
				break;
			default:
				throw new IllegalArgumentException("Folder must be SENT, RECEIVED or ARCHIVED");
		}
		Dms oDms = oSes.getDms();
		Log.out.debug("Dms.query("+sQry+")");
		SortableList<Document> oLst = oDms.query(sQry);
		int nTotalMsgs = oLst.size();
		ArrayList<Message> aMsgs = new ArrayList<Message>(nTotalMsgs==0 ? 1 : nTotalMsgs);
		ArrayList<Message> aSubl = new ArrayList<Message>(iMaxMsgs==0 ? 1 : iMaxMsgs);
		if (!oLst.isEmpty())
			for (Document d : oLst)
				aMsgs.add(new Message(oDms.getDocument(d.id())));
		if (sSortBy!=null)
			if (sSortBy.length()>0)
				Collections.sort(aMsgs, new AttrComparator(sSortBy, bAscending));
		for (int m=iOffset; m-iOffset<=iMaxMsgs && m<nTotalMsgs; m++)
			aSubl.add(aMsgs.get(m));
		Log.out.debug("End Messages.list() : "+String.valueOf(aSubl.size()));
		return aSubl;
	}
	
	public final static int FOLDER_SENT = 1;
	public final static int FOLDER_RECEIVED = 2;
	public final static int FOLDER_ARCHIVED = 3;
	
}
