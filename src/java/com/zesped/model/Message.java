package com.zesped.model;

import java.util.Date;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class Message extends BaseModelObject {
	
	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("thread_id",DataType.STRING,false,false,null),
    	new Attr("mime_id",DataType.STRING,false,false,null),
    	new Attr("sent_date",DataType.DATE_TIME,false,false,null),
    	new Attr("sender_id",DataType.STRING,false,false,new ForeignKey(User.class,"user_uuid")),
    	new Attr("sender_email",DataType.STRING,false,false,null),
    	new Attr("sender_displayname",DataType.STRING,false,false,null),
    	new Attr("recipient_id",DataType.STRING,false,false,new ForeignKey(User.class,"user_uuid")),
    	new Attr("recipient_email",DataType.STRING,false,false,null),
    	new Attr("recipient_displayname",DataType.STRING,false,false,null),
    	new Attr("message_subject",DataType.STRING,false,false,null),
    	new Attr("message_type",DataType.STRING,false,false,null),
    	new Attr("message_body",DataType.STRING,false,false,null),
    	new Attr("is_archived",DataType.STRING,false,false,null),
    	new Attr("is_readed",DataType.STRING,false,false,null),
    	new Attr("related_document",DataType.STRING,false,false,null,ExistsDocument.INSTANCE)
    };
	
	public Message() {
		super("Message");
	}

	public Message(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}
	
	public Message(Document d) {
		super(d);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public void archive(AtrilSession oSes) {
		Dms oDms = oSes.getDms();
		if (!isNull("thread_id")) {
			if (getString("thread_id").length()>0) {
				SortableList<Document> oThd = oDms.query("Message$thread_id='"+getString("thread_id")+"'");
				if (!oThd.isEmpty()) {
					for (Document m : oThd) {
						Document oMsg = oDms.getDocument(m.id());
						oMsg.attribute("is_archived").set("1");
						oMsg.save("archive");
					}					
				}
			}
		}
	}

	public boolean archived() {
		return getStringNull("is_archived","0").equals("1");
	}

	public boolean getReaded() {
		return getStringNull("is_readed","0").equals("1");
	}

	public void setReaded(boolean r) {
		put("is_readed",r ? "1" : "0");
	}

	public Date getDate() {
		return getDate("sent_date");
	}
	
	public String getSenderEmail() {
		return getString("sender_email");
	}
	
	public String getSenderDisplayName() {
		if (isNull("sender_displayname"))
			return getString("sender_email");
		else if (getString("sender_displayname").length()==0)
			return getString("sender_email");
		else
			return getString("sender_displayname");
	}

	public String getRecipientEmail() {
		return getString("recipient_email");
	}

	public String getRecipientDisplayName() {
		if (isNull("recipient_displayname"))
			return getString("recipient_email");
		else if (getString("recipient_displayname").length()==0)
			return getString("recipient_email");
		else
			return getString("recipient_displayname");
	}
	
	public String getSubject() {
		return getStringNull("message_subject","");
	}

	public String getBody() {
		return getStringNull("message_body","");
	}

	public String getType() {
		return getString("message_type");
	}

	public String getRelatedDocument() {
		if (isNull("related_document"))
			return null;
		else
			return getString("related_document");
	}

	public String getThreadId() {
		return getString("thread_id");
	}
}
