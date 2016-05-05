package com.zesped.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.knowgate.storage.StorageException;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Invoice;

import es.ipsa.atril.doc.user.Dms;

public class ConfirmRejectedInvoices extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/confirmrejectedinvoices.jsp";
	
	private ArrayList<Invoice> aInvoices;

	private String sIds = "";

	@Override
	public CaptureService getCaptureService() {
		return CaptureService.INVOICES;
	}

	public String getIds() {
		return sIds;
	}
	
	public ArrayList<Invoice> getInvoices() {
		return aInvoices;
	}
	
	@DefaultHandler
	public Resolution form() {
		HttpServletRequest oReq = getContext().getRequest();
		final String sDocs = oReq.getParameter("docs");
		Log.out.debug("Begin ConfirmRejectedInvoices.form("+sDocs+")");
		aInvoices = new ArrayList<Invoice>();
		if (sDocs!=null) {
			if (sDocs.length()>0) {
				String[] aDocs = sDocs.split(",");
				try {
					connect();
					Dms oDms = getSession().getDms();
					for (int d=0; d<aDocs.length; d++) {
						Invoice i = new Invoice(oDms, aDocs[d]);
						aInvoices.add(i);
						sIds += (sIds.length()==0 ? "" : ",")+i.id();
					}
					disconnect();
				} catch (StorageException e) {						
				} finally {
					close();
				}
			}
		}
		Log.out.debug("End ConfirmRejectedInvoices.form("+sDocs+")");
		return new ForwardResolution(FORM);
	}	
	
}
