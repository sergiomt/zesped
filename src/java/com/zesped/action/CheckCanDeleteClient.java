package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;

import com.zesped.Log;
import com.zesped.model.Client;

import es.ipsa.atril.doc.user.Dms;

public class CheckCanDeleteClient extends BaseAjaxBean {

	@DefaultHandler
	public Resolution form() {

		final String id = getParam("id","");

		try {
  		    connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
  		    Dms oDms = getSession().getDms();
			Client clnt = new Client(oDms, id);
			if (clnt.invoicesCount(oDms)>0)
				addError(new LocalizableError("com.zesped.action.CheckCanDeleteClient.hasInvoices",clnt.getBusinessName()));
			else if (clnt.ticketsCount(oDms)>0)
				addError(new LocalizableError("com.zesped.action.CheckCanDeleteClient.hasTickets",clnt.getBusinessName()));
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error("CheckCanDeleteClient.form() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }

		return AjaxResponseResolution();
	}		
	
}
