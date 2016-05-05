package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.zesped.Log;
import com.zesped.model.AccountingAccount;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;

public class SaveAccountingAccount extends BaseAjaxBean {

	@ValidationMethod(on="save")
	public void validateCode(ValidationErrors ignore) {
		final String sCode = getParam("accountingAccount.code");
		if (sCode==null)
			addError(new LocalizableError("com.zesped.action.SaveAccountingAccount.code.valueNotPresent"));	    	  
		else if (sCode.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveAccountingAccount.code.valueNotPresent"));	    	  
	}

	@ValidationMethod(on="save")
	public void validateDescription(ValidationErrors ignore) {
		final String sDesc = getParam("accountingAccount.description");
		if (sDesc==null)
			addError(new LocalizableError("com.zesped.action.SaveAccountingAccount.description.valueNotPresent"));	    	  
		else if (sDesc.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveAccountingAccount.description.valueNotPresent"));	    	  
	}

	@ValidationMethod(on="save")
	public void validateNoDuplicatedCodes(ValidationErrors ignore) {
		final String sFormerId = getParam("accountingAccount.id","");
		try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			TaxPayer oTxpr = new TaxPayer(getSession().getDms(), getParam("taxPayer"));
			try {
				AccountingAccount oAaac = oTxpr.accounts(getSession()).seek(getSession(), getParam("accountingAccount.code"));
				if (!sFormerId.equals(oAaac.id()))
					addError(new LocalizableError("com.zesped.action.SaveAccountingAccount.code.valueDuplicated"));
			} catch (ElementNotFoundException enfe) { }
			disconnect();
		} catch (Exception xcpt) {
			Log.out.error(xcpt.getMessage(), xcpt);
			addError(new SimpleError(xcpt.getMessage()));
		} finally {
			close();
		}
	}
	
	@DefaultHandler
	public Resolution save() {
		if (getErrorsCount()==0) {
			final String sFormerId = getParam("accountingAccount.id");
			try {
				AccountingAccount oAacc;
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				if (sFormerId.length()>0) {
					  oAacc = new AccountingAccount();
					  oAacc.load(getSession(), sFormerId);
					  oAacc.setCode(getParam("accountingAccount.code"));
					  oAacc.setDescription(getParam("accountingAccount.description"));
					  oAacc.setActive(getParam("accountingAccount.active","1").equals("1"));
				} else {
					  Dms oDms = getSession().getDms();
					  TaxPayer oTxpr = new TaxPayer(getSession().getDms(), getParam("taxPayer"));
					  Document oDoca = oDms.newDocument(oDms.getDocumentType("AccountingAccount"), oTxpr.accounts(getSession()).getDocument());
					  oDoca.save("");					  
					  oAacc = new AccountingAccount(oDoca);
					  oAacc.setCode(getParam("accountingAccount.code"));
					  oAacc.setDescription(getParam("accountingAccount.description"));
					  oAacc.setActive(getParam("accountingAccount.active","1").equals("1"));
				}
				oAacc.save(getSession());
				disconnect();
	    		addDataLine("taxpayer",getParam("taxPayer"));
	    		addDataLine("id",oAacc.id());
	    		addDataLine("uuid",oAacc.getUuid());
	    		addDataLine("code",oAacc.getCode());
	    		addDataLine("description",oAacc.getDescription());
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
