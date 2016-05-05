package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;

import com.zesped.Log;
import com.zesped.model.Invoice;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.exceptions.ElementNotFoundException;

public class RejectInvoices extends BaseAjaxBean {

	@DefaultHandler
	public Resolution reject() {
		final String sDocs = getParam("docs","");
		final String sCmmt = getParam("comments");
		if (sDocs.length()>0) {
			String[] aDocs = sDocs.split(",");
			try {
				Invoice oInv;
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				Dms oDms = getSession().getDms();
				for (int d=0; d<aDocs.length; d++) {
					try {
						oInv = new Invoice(oDms, aDocs[d]);
						oInv.reject(getSession(), getSessionAttribute("user_uuid"), sCmmt);
				    	addDataLine("id",oInv.id());
					} catch (ElementNotFoundException enfe) {
						addError(new SimpleError("Invoice not found "+aDocs[d]));
					}
				}
				disconnect();
			} catch (Exception xcpt) {
				Log.out.error(xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getMessage()));
			} finally {
				close();
			}			
		}
	    return AjaxResponseResolution();
	}
}
