package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.TaxPayer;

public class CountInvoices extends BaseActionBean {
	
	private final static String FORM = "/WEB-INF/jsp/countinvoices.jsp";
	
	private String bizname;
	private long pending, processed, approved, total;
	
	@Override
	public CaptureService getCaptureService() {
		return CaptureService.INVOICES;
	}

	public String getBusinessName() {
		return bizname;
	}
	
	public long getPending() {
		return pending;
	}

	public long getProcessed() {
		return processed;
	}

	public long getApproved() {
		return approved;
	}

	public long getTotal() {
		return total;
	}
	
	@DefaultHandler
	public Resolution form() {

		final String txpr = getParam("taxpayer","");

		try {
			connect();
			TaxPayer taxpr = new TaxPayer(getSession().getDms(), txpr);
			taxpr.refreshCounters(getSession());
			taxpr.save(getSession());
			bizname = taxpr.getBusinessName();
			pending = taxpr.getPendingInvoicesCount();
			processed = taxpr.getProcessedInvoicesCount();
			approved = taxpr.getApprovedInvoicesCount();
			total = taxpr.getTotalInvoicesCount();
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error("CountInvoices.form() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }

		return new ForwardResolution(FORM);
	}		
}
