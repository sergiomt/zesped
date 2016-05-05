package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class TicketNote extends BaseItemObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("page",DataType.NUMBER,false,false,null),
    };
	
	public TicketNote() {
		super("Side");
	}

	public TicketNote(Document t) {
		super(t);
	}
	
	public TicketNote(AtrilSession oSes, Ticket oParent, long nPageNum) throws DmsException {
		super("Side");
		newDocument(oSes, oParent.getDocument());
		put("page", new Long(nPageNum));
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}
	
}
