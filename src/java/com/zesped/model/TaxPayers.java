package com.zesped.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class TaxPayers extends BaseCollectionObject<TaxPayer> {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] { };

	public TaxPayers() {
		super("TaxPayers", TaxPayer.class);
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	private class TaxPayerNameComparator implements Comparator<TaxPayer> {
		public int compare(TaxPayer p1, TaxPayer p2) {
			return p1.getBusinessName().compareTo(p2.getBusinessName());
		}
	}
	
	private static TaxPayerNameComparator oTpnCmp = new TaxPayers().new TaxPayerNameComparator();
	
	@Override
	public ArrayList<TaxPayer> list(AtrilSession oSes) throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		ArrayList<TaxPayer> aPayers = super.list(oSes);
		Collections.sort(aPayers, oTpnCmp);
		return aPayers;
	}

	public TaxPayer byDefault(AtrilSession oSes, User oUsr) {
		final String s1stChild = getDocument().children().get(0).id();
		if (null==oUsr) {
		  Log.out.debug("TaxPayers.byDefault() User is null selecting first tax payer");
		  return new TaxPayer(oSes.getDms(), s1stChild);
		} else {
		  if (oUsr.getAllowedTaxPayers().size()==0) {
			  Log.out.debug("TaxPayers.byDefault() allowed tax payers list is empty selecting first tax payer");
			  return new TaxPayer(oSes.getDms(), s1stChild);			  
		  }
		  else {
			  String sAllowedTaxPayers = "";
			  Iterator<TaxPayer> oIter = oUsr.getAllowedTaxPayers().iterator();
			  while (oIter.hasNext()) sAllowedTaxPayers += (sAllowedTaxPayers.length()==0 ? "" : ",") + oIter.next().getBusinessName();
			  Log.out.debug("Allowed tax payers of "+oUsr.getFirstName()+" "+oUsr.getLastName()+" {"+sAllowedTaxPayers+"}");
			  for (Document t : getDocument().children()) {
				  TaxPayer txpr = new TaxPayer(oSes.getDms(), t.id());
				  if (oUsr.getAllowedTaxPayers().contains(txpr)) {
					  Log.out.debug("TaxPayers.byDefault() selecting tax payer "+txpr.getBusinessName());
					  return txpr;					  
				  }
			  }
			  Log.out.debug("TaxPayers.byDefault() allowed tax payers list does not contain any tax payer from the account selecting first tax payer");
			  return new TaxPayer(oSes.getDms(), s1stChild);			  
		  }
		}
	}

	public TaxPayer seek(AtrilSession oSes, String sTaxPayer)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException,
			   InstantiationException, IllegalAccessException {
		if (sTaxPayer==null) throw new ElementNotFoundException("TaxPayer may not be null");
		if (sTaxPayer.length()==0) throw new ElementNotFoundException("TaxPayer may not be empty");
		final String sSanitizedBusinessName =  Gadgets.removeChars(sTaxPayer, "\"*?").trim().toUpperCase();;
		for (TaxPayer t : super.list(oSes)) {
			if (t.getBusinessName().equals(sSanitizedBusinessName) || t.getBusinessName().equalsIgnoreCase(sTaxPayer) || t.getId().equals(sTaxPayer) || t.getTaxId().equalsIgnoreCase(sTaxPayer))
				return t;
		}
		throw new ElementNotFoundException("TaxPayer "+sTaxPayer+"not found");
	}
	
}
