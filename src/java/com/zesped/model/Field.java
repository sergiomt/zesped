package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;

public class Field extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("document_type",DataType.STRING,false,false,null),
    	new Attr("level",DataType.NUMBER,false,false,null),
    	new Attr("modifiable_by_ocr",DataType.STRING,false,false,null),
    	new Attr("modifiable_by_user",DataType.STRING,false,false,null),
    	new Attr("modifiable_scanner",DataType.STRING,false,false,null),
    	new Attr("name",DataType.STRING,false,false,null),
    };

	public Field() {
		super("Field");
	}

	public Field(Document d) {
		super("Field");
		setDocument(d);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

}
