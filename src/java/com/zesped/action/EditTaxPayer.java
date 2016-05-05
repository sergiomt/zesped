package com.zesped.action;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Countries;
import com.zesped.model.Country;
import com.zesped.model.State;
import com.zesped.model.TaxPayer;

public class EditTaxPayer extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/edittaxpayer.jsp";

	protected TaxPayer txp;
	private String acc;
	
	public EditTaxPayer() {
		txp = null;
	}

	public TaxPayer getTaxPayer() {
		return txp;
	}

	public void setTaxPayer(TaxPayer t) {
		txp = t;
	}

	public String getCustomerAccount() {
		if (acc==null)
			return getSessionAttribute("customer_acount");
		else if (acc.length()==0)
			return getSessionAttribute("customer_acount");
		else
			return acc;
	}

	public void setCustomerAccount(String a) {
		acc = a;
	}

	public Collection<Country> getCountries() {
		return Countries.list();
	}

	public Collection<State> getStates() {
		Collection<State> oStates = null;
		if (txp!=null) {
			if (!txp.isNull("country")) {
				try {
					connect();
		    		Country oCntr = Countries.top(getSession()).getCountry(getSession(), txp.getString("country"));
		    		oStates = oCntr.states(getSession()).list(getSession());
		    		disconnect();
		    	  } catch (Exception xcpt) {
		    		Log.out.error(xcpt.getMessage(), xcpt);
		    	  } finally {
		    		close();
		    	  }							
			} else
				oStates = new ArrayList<State>();
		} else
			oStates = new ArrayList<State>();
		return oStates;
	}
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@DefaultHandler
	public Resolution form() {
		if (getParam("id")==null) {
			txp = new TaxPayer();
			setCustomerAccount(getSessionAttribute("customer_acount"));
		} else {
			try {
	    		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
	    		  txp = new TaxPayer(getSession().getDms(), getParam("id"));
	    		  setCustomerAccount(txp.customerAccount(getSession().getDms()).getUuid());
	    		  disconnect();
	    	  } catch (Exception xcpt) {
	    		  Log.out.error(xcpt.getMessage(), xcpt);
	    	  } finally {
	    		  close();
	    	  }			
		}		
		return new ForwardResolution(FORM);
	}	

}
