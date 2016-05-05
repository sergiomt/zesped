package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;

public class Scanner extends BaseItemObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("localizador",DataType.STRING,false,false,null),
    	new Attr("name",DataType.STRING,false,false,null)
    };
	
	public Scanner() {
		super("Scanner");
	}

	public Scanner(Document t) {
		super(t);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}
	
	public String name() {
		return getString("name");
	}
}
