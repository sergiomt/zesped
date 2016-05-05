package com.zesped.action;

import java.sql.SQLException;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.CustomerAccount;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.exceptions.ElementNotFoundException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

public class SaveTaxPayer extends BaseAjaxBean {

	@ValidationMethod(on="save")
	public void validateId(ValidationErrors ignore) {
		final String sFormerId = getParam("taxPayer.id");
		if (sFormerId==null)
  		  	addError("id", new SimpleError("Parameter taxPayer.id is required"));
	}

	@ValidationMethod(on="save")
	public void validateEmail(ValidationErrors ignore) {
		final String sEmail = getParam("taxPayer.email");
		if (sEmail!=null)
	    	  if (sEmail.length()>0 && !Gadgets.checkEMail(sEmail))
	    		  addError("email", new LocalizableError("com.zesped.action.SaveClient.invalidEmail"));
	}

	@ValidationMethod(on="save")
	public void validateTaxId(ValidationErrors ignore) {
		final String sTaxId = getParam("taxPayer.taxId");
		if (sTaxId==null)
			addError(new LocalizableError("com.zesped.action.SaveClient.taxId.valueNotPresent"));	    	  
		else if (sTaxId.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveClient.taxId.valueNotPresent"));	    	  
	}

	@ValidationMethod(on="save")
	public void validateBusinessName(ValidationErrors ignore) {
		final String sBusinessName = getParam("taxPayer.businessName");
		final String sTaxId = getParam("taxPayer.taxId");
		final String sFormerId = getParam("taxPayer.id");
		Log.out.debug("Validating TaxPayer with business name "+sBusinessName+" and tax id. "+sTaxId+" for former id "+sFormerId);
		if (sBusinessName==null) {
			addError(new LocalizableError("com.zesped.action.SaveClient.businessName.valueNotPresent"));
		} else if (sBusinessName.length()==0) {
			addError(new LocalizableError("com.zesped.action.SaveClient.businessName.valueNotPresent"));
		} else {
			try {
				connect();
				CustomerAccount oAcc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
				TaxPayer oFoundTaxPayer = oAcc.taxpayers(getSession()).seek(getSession(), sBusinessName);
				if (!oFoundTaxPayer.id().equals(sFormerId))
					addError(new LocalizableError("com.zesped.action.SaveClient.businessNameAlreadyExists"));
				oFoundTaxPayer = oAcc.taxpayers(getSession()).seek(getSession(), sTaxId);
				if (!oFoundTaxPayer.id().equals(sFormerId))
					addError(new LocalizableError("com.zesped.action.SaveClient.taxIdAlreadyExists"));
			} catch (ElementNotFoundException clientnotfound) {
			} catch (Exception e) {
				Log.out.error("SaveTaxPayer.validateBusinessName("+sBusinessName+") "+e.getClass().getName()+" "+e.getMessage(), e);
			} finally {
				close();
			}
		}
	}

	@DefaultHandler
	public Resolution save() {
		final String sFormerId = getParam("taxPayer.id");
		Log.out.debug("Saving TaxPayer "+sFormerId);
		if (getErrorsCount()==0) {
			try {
				TaxPayer oTxpr;
				connect();
				if (sFormerId.length()>0) {
					  oTxpr = new TaxPayer();
					  oTxpr.load(getSession(), sFormerId);
				} else {
					  CustomerAccount oCacc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
					  oTxpr = TaxPayer.create(getSession(), oCacc, getParam("taxPayer.businessName"), getParam("taxPayer.contactPerson"), getParam("taxPayer.email"), DAO.getVolumesMountBase());
				}
				saveRequest(oTxpr);
				disconnect();
	    		addDataLine("id",oTxpr.id());
	    		if (getSessionAttribute("taxpayer_docid").equals(oTxpr.getId()))
	    			setSessionAttribute("businessname", oTxpr.getBusinessName());
			} catch (Exception xcpt) {
				Log.out.error(xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getMessage()));
			} finally {
				close();
			}
			try {
				Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"acctaxpayers");
				Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"taxpayers");
				Cache.deleteEntry("taxpayers");
			} catch (SQLException sqle) { }
		}
	    return AjaxResponseResolution();
	}
		  
}
