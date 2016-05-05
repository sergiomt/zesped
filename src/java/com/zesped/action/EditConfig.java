package com.zesped.action;

import java.util.Collection;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.CaptureService;
import com.zesped.model.Client;
import com.zesped.model.CustomerAccount;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class EditConfig extends BaseActionBean {
	
	private static final String FORM="/WEB-INF/jsp/editconfig.jsp";

	private User usr;
	private String email2;
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public String getEmail2() {
		return email2;
	}
	
	public User getUser() {
		return usr;
	}
	
	public void setUser(User u) {
		usr = u;
	}

	public Collection<Client> getClients() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		Log.out.debug("Begin EditConfig.getClients("+getSessionAttribute("customer_account_docid")+")");
		Collection<Client> clients = null;
		try {
			clients = (Collection<Client>) Cache.getObject(getSessionAttribute("customer_account_docid")+"clients");
		} catch (Exception e) {
			Log.out.error("EditConfig.getClients() Cache.getObject("+getSessionAttribute("customer_account_docid")+"clients) "+e.getClass().getName()+" "+e.getMessage(), e);
		}
		if (null==clients) {
			AtrilSession oSes = DAO.getAdminSession("CaptureInvoice");
			CustomerAccount oAcc = new CustomerAccount(oSes.getDms().getDocument(getSessionAttribute("customer_account_docid")));
			clients = oAcc.clients(oSes).list(oSes);
			for (Client oCli : clients)
				if (oCli.getTaxId().length()>0)
					oCli.setBusinessName(oCli.getBusinessName()+" ("+oCli.getTaxId()+")");
			oSes.disconnect();
			oSes.close();
			try {
				Cache.putEntry(getSessionAttribute("customer_account_docid")+"clients", clients);
			} catch (Exception e) {
				Log.out.error("CaptureInvoice.getClients() Cache.putEntry("+getSessionAttribute("customer_account_docid")+"clients) "+e.getClass().getName()+" "+e.getMessage(), e);
			}
		}
		Log.out.debug("End EditConfig.getClients("+getSessionAttribute("customer_account_docid")+")");
		return clients;
	}
	
	@DefaultHandler
	public Resolution form() {
		try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			usr = new User(getSession(), getSessionAttribute("user_docid"));
			email2 = usr.getEmail();
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error(xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }			
		return new ForwardResolution(FORM);
	}	
	
}
