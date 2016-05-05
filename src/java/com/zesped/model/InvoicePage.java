package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class InvoicePage extends BaseItemObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("page",DataType.NUMBER,true,true,null)
	};

	public InvoicePage() {
		super("Side");
	}

	public InvoicePage(Document p) {
		super(p);
	}

	public InvoicePage(AtrilSession oSes, Invoice oParent, long nPageNum) 
		throws DmsException {
		super("Side");
		newDocument(oSes, oParent.getDocument());
		put("page", new Long(nPageNum));
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}
	
}
