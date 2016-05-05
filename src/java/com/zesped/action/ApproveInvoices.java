package com.zesped.action;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;

import com.knowgate.storage.StorageException;
import com.zesped.Log;
import com.zesped.model.Invoice;

import es.ipsa.atril.doc.user.Dms;

public class ApproveInvoices extends BaseAjaxBean {

	@DefaultHandler
	public Resolution approve() {
		HttpServletRequest oReq = getContext().getRequest();
		final String sDocs = oReq.getParameter("docs");
		Log.out.debug("Begin ApproveInvoices.approve("+sDocs+")");
		if (sDocs!=null) {
			if (sDocs.length()>0) {
				String[] aDocs = sDocs.split(",");
				try {
					connect();
					Dms oDms = getSession().getDms();
					for (int d=0; d<aDocs.length; d++) {
						try {
							Invoice oInv = new Invoice(oDms, aDocs[d]);
							oInv.approve(getSession(), getSessionAttribute("user_uuid"));
							addDataLine("docid", aDocs[d]);
							commit();
						} catch (Exception xcpt) {
							rollback();
							addError("document", new SimpleError (xcpt.getClass().getName()+" "+xcpt.getMessage()));
							Log.out.error("ApproveInvoices.approve("+aDocs[d]+")  "+xcpt.getClass().getName()+" "+xcpt.getMessage());
						}
					}
					disconnect();
				} catch (StorageException stox) {
					addError("document", new SimpleError ("DMS exception"));
					Log.out.error("ApproveInvoices.approve() StorageException "+stox.getMessage());
				} finally {
					close();					
				}
			}
		}
		Log.out.debug("End ApproveInvoices.approve("+sDocs+")");
		return AjaxResponseResolution();
	}	
}
