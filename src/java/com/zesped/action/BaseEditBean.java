package com.zesped.action;

import java.util.Collection;

import com.knowgate.storage.StorageException;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.CustomerAccount;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public abstract class BaseEditBean extends BaseDatableBean {
	
	private String id;
	
	public BaseEditBean() {
	}	

	public String getId() {
		return id;
	}

	public void setId(String i) {
		id = i;
	}

	public abstract String getRecipientTaxPayer();

	public abstract void setRecipientTaxPayer(String s);

	public abstract String getBillerTaxPayer();

	public abstract void setBillerTaxPayer(String b);

	public Collection<TaxPayer> getTaxPayers()
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException, StorageException {
		Collection<TaxPayer> taxpayers = null;
		String ckey = getSessionAttribute("customer_account_docid")+"_"+getSessionAttribute("user_docid")+"taxpayers";
		Log.out.debug("Begin BaseEditBean.getTaxPayers("+getSessionAttribute("customer_account_docid")+")");
		try {
			taxpayers = (Collection<TaxPayer>) Cache.getObject(ckey+"taxpayers");
		} catch (Exception e) {
			Log.out.error("BaseEditBean.getTaxPayers() Cache.getObject("+ckey+") "+e.getClass().getName()+" "+e.getMessage(), e);
		}
		if (null==taxpayers) {
	  		connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			CustomerAccount oAcc = new CustomerAccount(getSession().getDms().getDocument(getSessionAttribute("customer_account_docid")));
			taxpayers = oAcc.taxpayers(getSession()).list(getSession());
			for (TaxPayer oTxp : taxpayers)
				if (oTxp.getTaxId().length()>0)
					oTxp.setBusinessName(oTxp.getBusinessName()+" ("+oTxp.getTaxId()+")");
			disconnect();
			try {
				Cache.putEntry(ckey, taxpayers);
			} catch (Exception e) {
				Log.out.error("BaseEditBean.getTaxPayers() Cache.putEntry("+getSessionAttribute("customer_account_docid")+"taxpayers) "+e.getClass().getName()+" "+e.getMessage(), e);
			}			
		}
		Log.out.debug("End BaseEditBean.getTaxPayers("+getSessionAttribute("customer_account_docid")+")");
		return taxpayers;
	}
		
}
