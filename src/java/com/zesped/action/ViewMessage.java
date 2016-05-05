package com.zesped.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.oro.text.regex.MalformedPatternException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Message;
import com.knowgate.misc.Gadgets;

public class ViewMessage extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/viewmessage.jsp";
	
	private Message msg;
	private String lnk;

	private static SimpleDateFormat oDtt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static SimpleDateFormat oDtm = new SimpleDateFormat("HH:mm");

	Date dtToday;
	int y,m,d;

	public ViewMessage() {
		dtToday = new Date();
		y=dtToday.getYear();
		m=dtToday.getMonth();
		d=dtToday.getDate();
		lnk = "";
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public String getId() {
		return msg.id();
	}
	
	public Message getMessage() {
		return msg;
	}

	public void setMessage(Message m) {
		msg=m;
	}

	public boolean getArchived() {
		return msg.archived();
	}
	
	public String getRelatedDocumentLink() {
		return lnk;
	}
	
	public String getDateFormatted() {
		try {
			if (msg.getDate().getYear()==y && msg.getDate().getMonth()==m && msg.getDate().getDate()==d)
				return oDtm.format(msg.getDate());
			else
				return oDtt.format(msg.getDate());
		} catch (NullPointerException npe) {
				return "";
		}
	}

	public String getSubject() {
		return Gadgets.HTMLEncode(msg.getSubject());
	}

	public String getBody() {
		String sBdy = "";
		try {
			sBdy =Gadgets.replace(Gadgets.HTMLEncode(msg.getBody()),"\n","<br/>");
		} catch (MalformedPatternException neverthrown) {			
		} catch (NullPointerException npe) {
		}
		return sBdy;
	}

	public String getRecipientId() {
		return msg.getStringNull("recipient_id","");
	}

	public String getSenderId() {
		return msg.getStringNull("sender_id","");
	}
	
	public String getRecipient() {
		try {
			if (msg.getRecipientEmail().equals(msg.getRecipientDisplayName()))
				return "&lt;"+msg.getRecipientEmail()+"&gt;";
			else
				return Gadgets.HTMLEncode(msg.getRecipientDisplayName())+" &lt;"+msg.getRecipientEmail()+"&gt;";
		} catch (NullPointerException npe) {
			return "";
		}
	}

	public String getSender() {
		try {
			if (msg.getSenderEmail().equals(msg.getSenderDisplayName()))
				return "&lt;"+msg.getSenderEmail()+"&gt;";
			return Gadgets.HTMLEncode(msg.getSenderDisplayName())+" &lt;"+msg.getSenderEmail()+"&gt;";
		} catch (NullPointerException npe) {
			return "";
		}
	}

	public String getThreadId() {
		return msg.getThreadId();
	}
	
	@DefaultHandler
	public Resolution form() {
		final String sId = getParam("id");
		final String sUid = getSessionAttribute("user_uuid");
		try {
			connect();
	    	msg = new Message(getSession().getDms(), sId);
	    	if (!msg.getReaded()) {
	    		msg.setReaded(true);
	    		msg.save(getSession());
	    	}
	    	if (msg.getRelatedDocument()!=null) {
	    		if (msg.getRelatedDocument().length()>0) {
	    			lnk = "Edit"+getSession().getDms().getDocument(msg.getRelatedDocument()).type().name()+".action?a=0&id="+msg.getRelatedDocument();
	    		}
	    	}
	    	disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error(xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }
		if (!msg.getStringNull("sender_id", "").equals(sUid) && !msg.getStringNull("recipient_id", "").equals(sUid)) {
	    	Log.out.error("SecurityException user "+sUid+" is trying to view message "+sId+" which is not for him");
			msg = new Message();
		}
		return new ForwardResolution(FORM);
	}
}
