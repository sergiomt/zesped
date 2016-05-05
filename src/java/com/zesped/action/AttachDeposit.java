package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.SimpleError;

import com.zesped.Log;
import com.zesped.model.AccountingAccount;
import com.zesped.model.BillNote;
import com.zesped.model.CaptureService;
import com.zesped.model.TaxPayer;
import com.zesped.model.Ticket;
import com.zesped.util.CreditBurner;
import com.zesped.util.DepositToZespedBridge;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;

public class AttachDeposit extends BaseAjaxBean {

	public CaptureService getCaptureService() {
		return CaptureService.INVOICES;
	}
	
	@DefaultHandler
	public Resolution attach() {

		try {
			Log.out.debug("Begin AttachDeposit.attach("+getParam("depositId","")+","+getParam("recipientTaxPayer","")+","+getParam("service","")+")");
			final long lDepositId = Long.parseLong(getParam("depositId",""));
			final String sRecipient = getParam("recipientTaxPayer","");
			final String sBiller = getParam("billerTaxPayer","");
			final String sFlavor = getParam("serviceFlavor","basic");
			final String sService = getParam("service","");
			final String sEmployee = getParam("employee","");
			final String sConcept = getParam("concept","");
			final String sAccount = getParam("account","");

			connect();

			Log.out.debug("Begin attaching sides");
			
			Dms oDms = getSession().getDms();
			Document rcpt = oDms.getDocument(sRecipient);
			String sTaxPayerId = rcpt.type().name().equals("TaxPayer") ? sRecipient : getSessionAttribute("taxpayer_docid");
			if (sService.equals("INVOICES")) {
				disconnect();
				DepositToZespedBridge oDzb = new DepositToZespedBridge(CaptureService.INVOICES, lDepositId, getSessionAttribute("user_uuid"), sFlavor, sTaxPayerId, sBiller, sRecipient);
				oDzb.start();
				Log.out.debug("Done attaching sides");
			} else {
				TaxPayer txpy = new TaxPayer(getSession().getDms(), sTaxPayerId);
				BillNote bill = txpy.billnotes(getSession()).forConcept(getSession(), sConcept, sEmployee);
				Ticket tckt = bill.createTicket(getSession(), new AccountingAccount(getSession(), sAccount));
				disconnect();
				DepositToZespedBridge oDzb = new DepositToZespedBridge(CaptureService.BILLNOTES, lDepositId, Long.parseLong(tckt.getId()));
				oDzb.start();
				Log.out.debug("Done attaching sides");
				FlashScope oFscope = FlashScope.getCurrent(getContext().getRequest(), true);
				oFscope.put("ticket_docid", tckt.id());
			}			
			new CreditBurner(getSessionAttribute("user_uuid"),
							 getSessionAttribute("customer_account_docid"),
							 sService, sFlavor).start();
		} catch (Exception e) {
			Log.out.error("AttachDeposit.attach() "+e.getClass().getName()+" "+e.getMessage(), e);
			addError(new SimpleError(e.getMessage()));
		} finally { close(); }
		Log.out.debug("End AttachDeposit.attach()");
		return AjaxResponseResolution();
	}	
	
}
