package com.zesped.action;

import java.sql.SQLException;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.Client;
import com.zesped.model.CustomerAccount;

import es.ipsa.atril.exceptions.ElementNotFoundException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

public class SaveClient extends BaseAjaxBean {

	@ValidationMethod(on="save")
	public void validateId(ValidationErrors ignore) {
		final String sFormerId = getParam("client.id");
		if (sFormerId==null)
  		  	addError("id", new SimpleError("Parameter client.id is required"));
	}

	@ValidationMethod(on="save")
	public void validateEmail(ValidationErrors ignore) {
		final String sEmail = getParam("client.email");
		if (sEmail!=null)
	    	  if (sEmail.length()>0 && !Gadgets.checkEMail(sEmail))
	    		  addError("email", new LocalizableError("com.zesped.action.SaveClient.invalidEmail"));
	}

	@ValidationMethod(on="save")
	public void validateTaxId(ValidationErrors ignore) {
		final String sTaxId = getParam("client.taxId");
		if (sTaxId==null)
			addError(new LocalizableError("com.zesped.action.SaveClient.taxId.valueNotPresent"));	    	  
		else if (sTaxId.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveClient.taxId.valueNotPresent"));	    	  
	}

	@ValidationMethod(on="save")
	public void validateBusinessName(ValidationErrors ignore) {
		final String sBusinessName = getParam("client.businessName");
		final String sTaxId = getParam("client.taxId");
		final String sFormerId = getParam("client.id");
		if (sBusinessName==null) {
			addError(new LocalizableError("com.zesped.action.SaveClient.businessName.valueNotPresent"));
		} else if (sBusinessName.length()==0) {
			addError(new LocalizableError("com.zesped.action.SaveClient.businessName.valueNotPresent"));
		} else {
			try {
				connect();
				CustomerAccount oAcc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
				Client oFoundClient = oAcc.clients(getSession()).seek(getSession(), sBusinessName);
				if (!oFoundClient.id().equals(sFormerId))
					addError(new LocalizableError("com.zesped.action.SaveClient.businessNameAlreadyExists"));
				oFoundClient = oAcc.clients(getSession()).seek(getSession(), sTaxId);
				if (!oFoundClient.id().equals(sFormerId))
					addError(new LocalizableError("com.zesped.action.SaveClient.taxIdAlreadyExists"));
			} catch (ElementNotFoundException clientnotfound) {
			} catch (Exception e) {
				Log.out.error("SaveClient.validateBusinessName("+sBusinessName+") "+e.getClass().getName()+" "+e.getMessage(), e);
			} finally {
				close();
			}
			try {
				Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"accsuppliers");
			} catch (SQLException sqle) { }
		}
	}

	@DefaultHandler
	public Resolution save() {
		final String sFormerId = getParam("client.id");
		if (getErrorsCount()==0) {
			try {
				Client oClnt;
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				if (sFormerId.length()>0) {
					  oClnt = new Client();
					  oClnt.load(getSession(), sFormerId);
				} else {
					  CustomerAccount oCacc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
					  oClnt = new Client(getSession(), oCacc.clients(getSession()));
				}
				saveRequest(oClnt);
				disconnect();
	    		addDataLine("id",oClnt.id());
			} catch (Exception xcpt) {
				Log.out.error(xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getMessage()));
			} finally {
				close();
			}
			try {
				Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"clients");
			} catch (SQLException e) { }
		}
	    return AjaxResponseResolution();
	}
		  
}
