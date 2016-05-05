package com.zesped.model;

import java.nio.channels.NotYetConnectedException;
import java.util.List;

import com.knowgate.misc.Gadgets;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class AccountingAccount extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	public AccountingAccount() {
		super("AccountingAccount");
	}

	public AccountingAccount(Document d) {
		super(d);
	}

	public AccountingAccount(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}

	public AccountingAccount(AtrilSession oSes, String sUuid) throws ElementNotFoundException {
		super("AccountingAccount");
		List<Document> oLst = oSes.getDms().query("AccountingAccount$account_uuid='" + sUuid + "'");
		if (oLst.isEmpty()) throw new ElementNotFoundException("No accounting account was found with UUID "+sUuid);
		load(oSes, oLst.get(0).id());
	}

	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("account_uuid",DataType.STRING,true,true,null),
    	new Attr("account_code",DataType.STRING,true,true,null),
    	new Attr("account_desc",DataType.STRING,true,true,null),
    	new Attr("is_active",DataType.STRING,false,true,null)
    };

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}
	
	public String getUuid() {
		return getString("account_uuid");
	}

	public String getCode() {
		return getString("account_code");
	}

	public void setCode(String account_code) throws NullPointerException {
		if (null==account_code)
			throw new NullPointerException("account_code cannot be null");
		else
			put("account_code", account_code);
	}
	
	public String getDescription() {
		return getString("account_desc");
	}

	public void setDescription(String account_desc) throws NullPointerException {
		if (null==account_desc)
			remove("account_desc");
		else
			put("account_desc", Gadgets.removeChars(account_desc,"\"\n`'"));
	}

	public boolean getActive() {
		if (isNull("is_active"))
			return true;
		else
			return getString("is_active").equals("1");
	}

	public void setActive(boolean a) {
		put("is_active", a ? "1" : "0");
	}

	public List<Document> tickets(AtrilSession oSes) throws NotYetConnectedException, DmsException {
		return oSes.getDms().query("Ticket$accounting_uuid='" + getString("account_uuid") + "'");
	}

	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException {

		if (!containsKey("account_uuid")) put("account_uuid", Gadgets.generateUUID());
		if (!containsKey("is_active")) put("is_active", "1");

		super.save(oSes);

		Dms oDms = oSes.getDms();
		List<Document> oLst = oDms.query("Ticket$account_uuid='" + getString("account_uuid") + "'");
		for (Document d : oLst) {
			Ticket oTck = new Ticket(oDms.getDocument(d.id()));
			oTck.put("accounting_code", getString("account_code"));
			oTck.put("accounting_desc", Gadgets.removeChars(getString("account_desc"),"\"\n`'"));
			oTck.save(oSes);
		}		
	}

}


