package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;

public class Side extends BaseItemObject {

	private static final long serialVersionUID = 1L;

	public Side() {
		super("Side");		
	}

	public Side(Document c) {
		super(c);
	}

	public Side(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("page",DataType.NUMBER,false,true,null)
    };
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}
}
