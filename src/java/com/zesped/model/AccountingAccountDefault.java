package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class AccountingAccountDefault extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	public AccountingAccountDefault() {
		super("AccountingAccountDefault");
	}

	public AccountingAccountDefault(Document d) {
		super("AccountingAccountDefault");
		setDocument(d);
	}
	
	public AccountingAccountDefault(AtrilSession oSes, String sCode, String sDesc) {
		super("AccountingAccountDefault");
		Document oDoc = exists(oSes, "account_code", sCode);
		Document oParent = AccountingAccountsDefaults.top(oSes).getDocument();
		if (oDoc==null)
		  newDocument(oSes, oParent);
		else
		  setDocument(oDoc);
		put("account_code",sCode);
		put("account_desc",sDesc);
	}

	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("account_code",DataType.STRING,true,true,null),
    	new Attr("account_desc",DataType.STRING,true,true,null),
    };

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getCode() {
		return getString("account_code");
	}

	public String getDescription() {
		return getString("account_desc");
	}
	
}
