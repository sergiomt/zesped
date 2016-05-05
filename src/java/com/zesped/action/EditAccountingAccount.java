package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.AccountingAccount;

public class EditAccountingAccount extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/editaccountingaccount.jsp";

	private AccountingAccount account;
	
	private String id, taxpayer;
	
	public EditAccountingAccount() {
		id=taxpayer="";
		account=null;
	}

    @ValidateNestedProperties({        
        @Validate(on="save", field="code", required=true, maxlength=50),
        @Validate(on="save", field="description", required=true, maxlength=100),
    })	
	public AccountingAccount getAccountingAccount() {
		return account;
	}

	public void setAccountingAccount(AccountingAccount a) {
		account = a;
	}

	public String getId() {
		return id;
	}

	public void setId(String i) {
		id = i;
	}
	
	public String getTaxPayer() {
		return taxpayer;
	}

	public void setTaxPayer(String t) {
		taxpayer = t;
	}

	public boolean getActive() {
		return account.getActive();
	}
	
	@Override
	public CaptureService getCaptureService() {
		return CaptureService.BILLNOTES;
	}
	
	@DefaultHandler
	public Resolution form() {
		if (getParam("id")==null) {
			account = new AccountingAccount();
			account.setActive(true);
		} else {
			try {
	    		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
	    		  setId(getParam("id"));
	    		  setAccountingAccount(new AccountingAccount(getSession().getDms(), getId()));
	    		  disconnect();
	    	  } catch (Exception xcpt) {
	    		  Log.out.error(xcpt.getMessage(), xcpt);
	    	  } finally {
	    		  close();
	    	  }			
		}
		setTaxPayer(getParam("taxpayer",""));	  
		return new ForwardResolution(FORM);
	}
}