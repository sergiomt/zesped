package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;

import com.zesped.Log;
import com.zesped.model.AccountingAccount;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;

public class CheckCanDeleteAccountingAccount extends BaseAjaxBean {

	@DefaultHandler
	public Resolution form() {

		final String id = getParam("id","");

		try {
  		    connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
  		    Dms oDms = getSession().getDms();
  		    AccountingAccount oAacc = new AccountingAccount(oDms, id);
  		    SortableList<Document> oTcks = oDms.query("Ticket$accounting_uuid='"+oAacc.getUuid()+"'");
			if (!oTcks.isEmpty()) {
				addError(new LocalizableError("com.zesped.action.CheckCanDeleteAccountingAccount.hasTickets"));				
			}
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error("CheckCanDeleteAccountingAccount.form() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }

		return AjaxResponseResolution();
	}		
	
}
