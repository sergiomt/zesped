package com.zesped.model;

import com.zesped.model.BaseModelObject;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;

@SuppressWarnings("serial")
public abstract class BaseCustomerAccountFolder extends BaseModelObject {

	public BaseCustomerAccountFolder(String sDocType) {
		super(sDocType);
	}
	
	public BaseCustomerAccountFolder(Document d) {
		super(d);
	}
	
	public TaxPayer taxPayer(Dms oDms) {
		return new TaxPayer(oDms, getDocument().parents().get(0).id());
	}

	public CustomerAccount customerAccount(Dms oDms) {
		return taxPayer(oDms).customerAccount(oDms);
	}
	
}
