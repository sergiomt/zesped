package com.zesped.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Client;
import com.zesped.model.CustomerAccount;

public class ListClients extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/listclients.jsp";

	private ArrayList<Client> clients;
	
	private String sortby;
	
	public ListClients() {
		clients=null;
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public ArrayList<Client> getClients() {
		return clients;
	}

	private class BusinessNameComparator implements Comparator<Client> {
		public int compare(Client c1, Client c2) {
			final String s1 = c1.getBusinessName();
			final String s2 = c2.getBusinessName();
			if (s1==null && s2==null)
				return 0;
			else if (s1==null && s2!=null)
				return -1;
			else if (s1!=null && s2==null)
				return 1;
			else
				return s1.compareTo(s2);
		}
	}
		
	private void removeUnactiveClients() {
		int nCount = clients.size();
		for (int u=0; u<nCount; u++) {
			if (!clients.get(u).getActive()) {
				clients.remove(u--);
				nCount--;
			}
		} //next		
	}

	private void filterClientsBySubstr(String sFind) {
		int nCount = clients.size();
		if (sFind==null) sFind="";
		for (int u=0; u<nCount; u++) {
			Client oClt = clients.get(u);
			if (oClt.getBusinessName().toUpperCase().indexOf(sFind.toUpperCase())<0 &&
				!sFind.equalsIgnoreCase(oClt.getTaxId())) {
				clients.remove(u--);
				nCount--;
			}
		} //next
	}
	
	@DefaultHandler
	public Resolution form() {		
		try {
  		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
  		  CustomerAccount cacc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
  		  clients=cacc.clients(getSession()).list(getSession());
  		  disconnect();
  	  } catch (Exception xcpt) {
  		  Log.out.error(xcpt.getMessage(), xcpt);
  	  } finally {
  		  close();
  	  }

	  if (getParam("onlyactive","0").equals("1")) {
		removeUnactiveClients();
	  }

	  final String sFind = getParam("find","");
	  if (sFind.length()>0) {
		  filterClientsBySubstr(sFind);
	  }
		
	  sortby = getParam("sort","");
	  Comparator<Client> oCmp = null;
	  if (sortby.equals("1"))
		  oCmp = new BusinessNameComparator();
	  if (oCmp!=null) {
		  Collections.sort(clients, oCmp);			
	  }			
		
	  return new ForwardResolution(FORM);
	}
}
