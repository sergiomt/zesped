package com.zesped.model;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public class AllowedTaxPayers extends BaseCollectionObject<AllowedTaxPayer> {

	public AllowedTaxPayers() {
		super("AllowedTaxPayers", AllowedTaxPayer.class);
	}

	public AllowedTaxPayers(Document oDoc) {
		super(oDoc, AllowedTaxPayer.class);
	}
	
	@Override
	public Attr[] attributes() {
		return null;
	}

	public void clear(AtrilSession oSes)
		throws ElementNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IllegalStateException {
		for (Document t : getDocument().children())
			AllowedTaxPayer.delete(oSes, t.id());
	}
}
