package com.zesped.action;

import java.util.ArrayList;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.AccountingAccount;
import com.zesped.model.TaxPayer;

public class ListAccounts extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/listaccounts.jsp";

	private ArrayList<AccountingAccount> accounts;
	
    private TaxPayer taxpr;	

    @Override
	public CaptureService getCaptureService() {
		return CaptureService.BILLNOTES;
	}

	public TaxPayer getTaxPayer() {
		return taxpr;
	}
    
	public ArrayList<AccountingAccount> getAccountingAccounts() {
		return accounts;
	}

	@DefaultHandler
	public Resolution form() {

		final String txpr = getParam("taxpayer","");

		try {
			connect();
			taxpr = new TaxPayer(getSession().getDms(), txpr);
			accounts = taxpr.accounts(getSession()).list(getSession());
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error(xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }

		return new ForwardResolution(FORM);
	}	
}
