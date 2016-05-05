package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class AllowedTaxPayer extends BaseCompanyObject {

	private static final long serialVersionUID = 1L;

	private static class ExistsTaxPayer implements CustomConstraint {
		public boolean check (AtrilSession oSes, DocumentIndexer oIdx, BaseModelObject oObj) {
			boolean bRetVal = true;
			TaxPayer oTxpr = new TaxPayer();
			try {
				oTxpr.load(oSes, oObj.getString("taxpayer"));
				bRetVal = oTxpr.getCustomerAccount().equals(oObj.getString("customer_acount"));
			} catch (ElementNotFoundException enfe) {
				bRetVal = false;
			}
			return bRetVal;
		}
	}
	
	private static CustomConstraint checkTaxPayerConstraint() {
		return new ExistsTaxPayer();
	}	

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("taxpayer",DataType.STRING,false,true,null,checkTaxPayerConstraint()),
    	new Attr("customer_acount",DataType.STRING,false,true,new ForeignKey(CustomerAccount.class, "account_id")),
    	new Attr("creation_date",DataType.DATE_TIME,false,true),
    	new Attr("business_name",DataType.STRING,true,true),
    	new Attr("tax_id",DataType.STRING,true,false),
    };
	
	public AllowedTaxPayer() {
		super("AllowedTaxPayer");
	}

	public AllowedTaxPayer(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

}
