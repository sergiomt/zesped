package com.zesped.model;

import es.ipsa.atril.doc.user.Document;

@SuppressWarnings("serial")
public class IncomingDeposits extends BaseCustomerAccountFolder {

	public IncomingDeposits() {
		super("IncomingDeposits");
	}

	public IncomingDeposits(Document d) {
		super(d);
	}
	
	@Override
	public Attr[] attributes() {
		return null;
	}

}
