package com.zesped.action;

import java.util.ArrayList;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.AccountingAccounts;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.TaxPayer;
import com.zesped.model.TaxPayers;

public class ListTaxPayers extends BaseListBean {

	private static final String FORM="/WEB-INF/jsp/listtaxpayers.jsp";

	private ArrayList<Integer> totalInvoices;
	private ArrayList<Integer> totalTickets;
	private ArrayList<Integer> allowed;
	private ArrayList<Integer> employees;
	private ArrayList<Integer> accounts;
	
	public ListTaxPayers() {
		totalInvoices = new ArrayList<Integer>();
		totalTickets = new ArrayList<Integer>();
		allowed = new ArrayList<Integer>();
		employees = new ArrayList<Integer>();
		accounts = new ArrayList<Integer>();
	}
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public ArrayList<Integer> getTotalInvoicesCounts() {
		return totalInvoices;
	}

	public ArrayList<Integer> getTotalTicketsCounts() {
		return totalTickets;
	}

	public int getTotalInvoicesCount(int n) {
		return totalInvoices.get(n).intValue();
	}

	public int getTotalTicketsCount(int n) {
		return totalTickets.get(n).intValue();
	}
	
	public ArrayList<Integer> getAllowedUsersCounts() {
		return allowed;
	}

	public ArrayList<Integer> getEmployeesCounts() {
		return employees;
	}

	public ArrayList<Integer> getAccountingAccountsCounts() {
		return accounts;
	}
	
	@DefaultHandler
	public Resolution form() {
		try {
  		  connect();
  		  TaxPayers txprs = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid")).taxpayers(getSession());
  		  for (TaxPayer t : txprs.list(getSession())) {
  			totalInvoices.add(new Integer((int) t.getTotalInvoicesCount()));
  			totalTickets.add(new Integer((int) t.getTotalTicketsCount()));
  			allowed.add(new Integer(t.allowedUsers(getSession()).size()));
  			employees.add(new Integer(t.employees(getSession()).count()));
  			AccountingAccounts oAacs = t.accounts(getSession());
  			if (oAacs==null)
  				accounts.add(new Integer(0));
  			else
  				accounts.add(new Integer(oAacs.count()));
  		  } // next
  		  disconnect();
  	  } catch (Exception xcpt) {
  		  Log.out.error(xcpt.getMessage(), xcpt);
  	  } finally {
  		  close();
  	  }
	  return new ForwardResolution(FORM);
	}

}
