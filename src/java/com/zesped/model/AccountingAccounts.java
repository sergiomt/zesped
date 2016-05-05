package com.zesped.model;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.zesped.DAO;
import com.zesped.Log;

import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

@SuppressWarnings("serial")
public class AccountingAccounts extends BaseCollectionObject<AccountingAccount> implements TypeConverter<AccountingAccount> {

	public AccountingAccounts() {
		super("AccountingAccounts", AccountingAccount.class);
	}

	public AccountingAccounts(Document d) {
		super("AccountingAccounts", AccountingAccount.class, d);
	}
	
	@Override
	public Attr[] attributes() {
		return null;
	}

	@Override
	public AccountingAccount convert(String sUuid, Class<? extends AccountingAccount> accountClass,
						   Collection<ValidationError> conversionErrors) {
		try {
			AccountingAccount oAacc = null;
			AtrilSession oSes = DAO.getAdminSession("AccountingAccounts");
			List<Document> oLst = oSes.getDms().query("AccountingAccount$account_uuid='"+sUuid+"'");
			if (!oLst.isEmpty()) {
				oAacc = new AccountingAccount(oSes.getDms(), oLst.get(0).id());
			}
			oSes.disconnect();
			oSes.close();
			oSes = null;
			if (null==oAacc) {
				Log.out.error("No accounting account with uuid "+sUuid+" was found");
				conversionErrors.add(new SimpleError("No accounting account with uuid "+sUuid+" was found"));
			}
			return oAacc;
		} catch (Exception exc) {
			Log.out.error(exc.getClass().getName()+" AccountingAccount.convert("+sUuid+") "+exc.getMessage());
			conversionErrors.add(new SimpleError(exc.getMessage()));
			return null;
		}
	}
		
	@Override
	public void setLocale(Locale locale) { }

	public AccountingAccount seek(AtrilSession oSes, String sIdOrCode)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException,
			   InstantiationException, IllegalAccessException,NullPointerException {
		if (sIdOrCode==null) throw new NullPointerException("AccountingAccount may not be null");
		if (sIdOrCode.length()==0) throw new NullPointerException("AccountingAccount may not be empty");
		for (AccountingAccount a : list(oSes))
			if (sIdOrCode.equals(a.getId()) || sIdOrCode.equals(a.getUuid()) || sIdOrCode.equals(a.getCode()))
				return a;
		throw new ElementNotFoundException("AccountingAccount "+sIdOrCode+" not found");
	}

}
