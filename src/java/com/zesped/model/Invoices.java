package com.zesped.model;

import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public class Invoices extends BaseCustomerAccountFolder {

	public Invoices() {
		super("Invoices");
	}

	public Invoices(Document d) {
		super(d);
	}
	
	public Invoices(Dms oDms, TaxPayer oTaxp) {
		super("Invoices");
		for (Document d : oTaxp.getDocument().children()) {
			if (d.type().name().equals(getTypeName())) {
				  setDocument(oDms.getDocument(d.id()));
				  break;
			}  
		} // next
	}

	@Override
	public Attr[] attributes() {
		return null;
	}
	
	public Invoice create(AtrilSession oSes, String sUserUuid, String sServiceType, String sTaxPayer, String sBiller, String sRecipient) {

		Invoice invc = new Invoice(oSes, this);
		invc.put("service_type", sServiceType);
		if (sUserUuid!=null) if (sUserUuid.length()>0) invc.put("captured_by", sUserUuid);
		if (sTaxPayer!=null) if (sTaxPayer.length()>0) invc.put("taxpayer", sTaxPayer);
		if (sBiller!=null) if (sBiller.length()>0) invc.put("biller_taxpayer", sBiller);
		if (sRecipient!=null) if (sRecipient.length()>0) invc.put("recipient_taxpayer", sRecipient);
		invc.save(oSes);
		
		DAO.log(oSes, invc.getDocument(), Invoice.class, "CREATE INVOICE", AtrilEvent.Level.INFO, "");

		oSes.commit();
		try {
			Dms oDms = oSes.getDms();
			TaxPayer oTxp = new TaxPayer(oDms, sTaxPayer);
			try {
				oTxp.incPendingInvoicesCount(oSes,1l);
			} catch(Exception e) {
				Log.out.error(e.getClass()+" "+e.getMessage(), e);
			}
			customerAccount(oDms).grant(invc);		
			oSes.commit();			
		} catch (DmsException dmse) {
			Log.out.error("DmsException updating TaxPayer invoices count "+dmse.getMessage(), dmse);
		}
		return invc;
	}

	public Invoice[] create(AtrilSession oSes, String sUserUuid, String sServiceType, String sTaxPayer, String sBiller, String sRecipient, int nInvoices) {
		Log.out.debug("Begin Invoices.create("+sUserUuid+","+sServiceType+","+sTaxPayer+"."+sBiller+","+sRecipient+","+String.valueOf(nInvoices)+")");
		Invoice[] invs = new Invoice[nInvoices];
		for (int i=0; i<nInvoices; i++) {
			Invoice invc = new Invoice(oSes, this);
			invs[i] = invc;
			invc.put("service_type", sServiceType);
			if (sUserUuid!=null) if (sUserUuid.length()>0) invc.put("captured_by", sUserUuid);
			if (sTaxPayer!=null) if (sTaxPayer.length()>0) invc.put("taxpayer", sTaxPayer);
			if (sBiller!=null) if (sBiller.length()>0) invc.put("biller_taxpayer", sBiller);
			if (sRecipient!=null) if (sRecipient.length()>0) invc.put("recipient_taxpayer", sRecipient);
			invc.save(oSes);
			DAO.log(oSes, invc.getDocument(), Invoice.class, "CREATE INVOICE", AtrilEvent.Level.INFO, "");
		}
		oSes.commit();
		try {
		Dms oDms = oSes.getDms();
		TaxPayer oTxp = new TaxPayer(oDms, sTaxPayer);
		try {
			oTxp.incPendingInvoicesCount(oSes,nInvoices);
		} catch(Exception e) {
			Log.out.error(e.getClass()+" "+e.getMessage(), e);
		}
		customerAccount(oDms).grant(invs);		
		oSes.commit();			
		} catch (DmsException dmse) {
			Log.out.error("DmsException updating TaxPayer invoices count "+dmse.getMessage(), dmse);
		}
		Log.out.debug("End Invoices.create()");
		return invs;
	}
	
}
