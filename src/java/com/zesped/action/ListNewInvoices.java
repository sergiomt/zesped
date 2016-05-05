package com.zesped.action;

import com.zesped.model.CaptureService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class ListNewInvoices extends BaseListBean {

	private static final String FORM="/WEB-INF/jsp/listnewinvoices.jsp";

	private String daystart, dayend;
	
	private String recipient, biller;

	public ListNewInvoices() {
		recipient=biller="";
	}
	
	public CaptureService getCaptureService() {
		return CaptureService.INVOICES;
	}

	public String getDayStart() {
		return daystart;
	}

	public void setDayStart(String m) {
		daystart = m;
	}

	public String getDayEnd() {
		return dayend;
	}

	public void setDayEnd(String d) {
		dayend = d;
	}

	@DefaultHandler
	public Resolution form() {
	  setRecipient(getSessionAttribute("taxpayer_docid"));
	  return new ForwardResolution(FORM);
	}

	public String getRecipient() {
		return recipient==null ? "" : recipient;
		
	}

	public void setRecipient(String r) {
		recipient = r;		
	}
	
	public String getBiller() {
		return biller==null ? "" : biller;
		
	}

	public void setBiller(String b) {
		biller = b;
	}
}
