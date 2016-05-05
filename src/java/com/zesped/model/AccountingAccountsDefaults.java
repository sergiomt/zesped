package com.zesped.model;

import java.util.ArrayList;
import java.util.Collection;

import com.zesped.DAO;
import com.zesped.model.BaseModelObject;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public class AccountingAccountsDefaults extends BaseModelObject {

	private static ArrayList<AccountingAccountDefault> oCache = null;
	
	public AccountingAccountsDefaults() {
		super("AccountingAccountsDefaults");
	}
	
	@Override
	public Attr[] attributes() {
		return null;
	}

	public static AccountingAccountsDefaults top(AtrilSession oSes) throws ElementNotFoundException {
		  Zesped z = Zesped.top(oSes);
		  AccountingAccountsDefaults a = new AccountingAccountsDefaults();
		  for (Document d : z.getDocument().children()) {
			  if (d.type().name().equals(a.getTypeName())) {
				  a.setDocument(oSes.getDms().getDocument(d.id()));
				  break;
			  }
		  } // next
		  if (a.getDocument()==null) throw new ElementNotFoundException(a.getTypeName()+" document not found");
		  return a;
    }	

	public static Collection<AccountingAccountDefault> list() {
		if (oCache==null) {
			AtrilSession oSes = DAO.getAdminSession("AccountingAccountsDefaults");
			oCache = new ArrayList<AccountingAccountDefault>();
			for (Document d : top(oSes).getDocument().children())
				oCache.add(new AccountingAccountDefault(d));
			oSes.disconnect();
			oSes.close();
		}
		return oCache;
	}

}
