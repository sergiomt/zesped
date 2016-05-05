package com.zesped.model;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public class DeniedTaxPayers extends BaseCollectionObject<DeniedTaxPayer> {

	public DeniedTaxPayers() {
		super("DeniedTaxPayers", AllowedTaxPayer.class);
	}

	public DeniedTaxPayers(Document oDoc) {
		super(oDoc, DeniedTaxPayer.class);
	}
	
	@Override
	public Attr[] attributes() {
		return null;
	}

	public void clear(AtrilSession oSes)
		throws ElementNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IllegalStateException {
		for (Document t : getDocument().children())
			DeniedTaxPayer.delete(oSes, t.id());
	}
	
}
