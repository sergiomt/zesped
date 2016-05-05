package com.zesped.model;

import es.ipsa.atril.doc.user.Document;

public class Fields extends BaseCollectionObject {

	private static final long serialVersionUID = 1L;

	public Fields() {
		super("Fields", Field.class);
	}

	public Fields(Document d) {
		super("Fields", Field.class, d);
	}
	
	@Override
	public Attr[] attributes() {
		return null;
	}

}
