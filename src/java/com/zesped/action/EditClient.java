package com.zesped.action;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Client;
import com.zesped.model.Countries;
import com.zesped.model.Country;
import com.zesped.model.State;

public class EditClient extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/editclient.jsp";

	private Client client;
	
	public EditClient() {
		client = null;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client c) {
		client = c;
	}

	public Collection<Country> getCountries() {
		return Countries.list();
	}

	public Collection<State> getStates() {
		Collection<State> oStates = null;
		if (client!=null) {
			if (!client.isNull("country")) {
				try {
					connect();
		    		Country oCntr = Countries.top(getSession()).getCountry(getSession(), client.getString("country"));
		    		if (oCntr!=null)
		    		  oStates = oCntr.states(getSession()).list(getSession());
		    		else
		    		  oStates = new ArrayList<State>();
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
		if (getParam("client.id")==null) {
			client = new Client();
			client.setTaxPayer(getSessionAttribute("taxpayer_docid"));
			client.setCountry("es");
		} else {
			try {
	    		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
	    		  setClient(new Client(getSession().getDms(), getParam("client.id")));
	    		  disconnect();
	    	  } catch (Exception xcpt) {
	    		  Log.out.error("EditClient.form() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
	    	  } finally {
	    		  close();
	    	  }			
		}
		return new ForwardResolution(FORM);
	}	
}
