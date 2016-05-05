package com.zesped.action;

import java.util.ArrayList;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.Order;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.TaxPayer;

public class ShowCredits extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/showcredits.jsp";
	
	private ArrayList<Order> ordrs;
	private int cused, cleft, cbought;
	private int cbasic=0, cpremium=0, ctickets=0;
	
	public ArrayList<Order> getOrders() {
		return ordrs;
	}

	public int getCreditsCreditsUsed() {
		return cused;
	}

	public int getCreditsCreditsLeft() {
		return cleft>=0 ? cleft : 0;
	}

	public int getCreditsPct() {
		if (cbought>0)
			return (cleft*100)/cbought;
		else
			return 0;
	}

	public int getBasicInvoicesCount() {
		return cbasic;
	}

	public int getPremiumInvoicesCount() {
		return cpremium;
	}

	public int getTicketsCount() {
		return ctickets;
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@DefaultHandler
	public Resolution form() {
		try {			
  		  connect();
  		  CustomerAccount oAcc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
  		  cused = oAcc.getCreditsUsed().intValue();
  		  cleft = oAcc.getCreditsLeft().intValue();
  		  ordrs = oAcc.orders(getSession()).list(getSession());
  		  if (ordrs.size()>0) {
  			cbought = ordrs.get(0).getCreditsBought().intValue();
  		  } else {
  			cbought = 0;
  		  }
  		  ArrayList<TaxPayer> oTxps = oAcc.taxpayers(getSession()).list(getSession());
  		  for (TaxPayer p : oTxps) {
  			cbasic   += p.basicInvoices(getSession()).size();
  		  	cpremium += p.premiumInvoices(getSession()).size();
  		  	ctickets += p.allTickets(getSession()).size();
  		  }  		  
  		  disconnect();
  	    } catch (Exception xcpt) {
  		  Log.out.error(xcpt.getMessage(), xcpt);
  	    } finally {
  		  close();
  	    }
		return new ForwardResolution(FORM);
	}	
}
