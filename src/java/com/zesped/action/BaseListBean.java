package com.zesped.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.knowgate.misc.Calendar;
import com.knowgate.misc.Gadgets;
import com.knowgate.storage.StorageException;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.Client;
import com.zesped.model.CustomerAccount;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public abstract class BaseListBean extends BaseDatableBean {
	
	private int maxrows, offset;

	private String monthstart, monthend, yearstart, yearend;

	private String status, searchstr;
	
	private CustomerAccount cacc;
	private Collection<Client> cnts;
	private Collection<TaxPayer> txpr;
	
	public BaseListBean() {
		cacc = null;
		cnts = null;
		txpr = null;
		status = "";
		offset = 0;
		maxrows = 100;
	}

	public String getSearchString() {
		return searchstr==null ? "" : searchstr;
	}

	public void setSearchString(String s) {
		if (s==null) 
			searchstr = "";
		else
			searchstr = Gadgets.removeChars(s.replace('\n', ' ').replace('\t', ' '), "*?~\"'%");
	}
	
	public String getStatus() {
		return status==null ? "" : status;
	}

	public void setStatus(String s) {
		status = s;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int o) {
		offset = o;
	}
	
	public int getMaxRows() {
		return maxrows;
	}

	public void setMaxRows(int m) {
		maxrows = m;
	}

	public Date getDateStart() {
		return new Date(Integer.parseInt(getYearStart())-1900, Integer.parseInt(getMonthStart())-1, 1, 0, 0, 0);
	}

	public Date getDateEnd() {
		return new Date(Integer.parseInt(getYearEnd())-1900, Integer.parseInt(getMonthEnd())-1, Calendar.LastDay(Integer.parseInt(getMonthEnd())-1, Integer.parseInt(getYearEnd())), 23, 59, 59);
	}
	
	public String getMonthStart() {
		return monthstart;
	}

	public void setMonthStart(String m) {
		monthstart = m;
	}

	public String getMonthEnd() {
		return monthend;
	}

	public void setMonthEnd(String m) {
		monthend = m;
	}

	public String getYearStart() {
		return yearstart;
	}

	public void setYearStart(String y) {
		yearstart = y;
	}

	public String getYearEnd() {
		return yearend;
	}

	public void setYearEnd(String y) {
		yearend = y;
	}

	private boolean containsSubstr(TaxPayer t, String s) {
	  if (s.length()==0)
		  return true;
	  else
		  return t.getBusinessName().toUpperCase().indexOf(s.toUpperCase())>=0;
	}
	
	private void preload() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		final String f = getParam("find", "");
		Log.out.debug("Begin BaseListbean.preload("+f+")");

		try {
			cnts = (Collection<Client>) Cache.getObject(getSessionAttribute("customer_account_docid")+"clients");
		} catch (Exception e) {
			Log.out.error("BaseListBean.preload() Cache.getObject("+getSessionAttribute("customer_account_docid")+"clients) "+e.getClass().getName()+" "+e.getMessage(), e);
		}

		try {
			txpr = (Collection<TaxPayer>) Cache.getObject(getSessionAttribute("customer_account_docid")+"taxpayers");
		} catch (Exception e) {
			Log.out.error("BaseListBean.preload() Cache.getObject("+getSessionAttribute("customer_account_docid")+"taxpayers) "+e.getClass().getName()+" "+e.getMessage(), e);
		}
		
		try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			Dms oDms = getSession().getDms();
			cacc = new CustomerAccount(oDms, getSessionAttribute("customer_account_docid"));
			if (cnts==null) {
				cnts = cacc.clients(getSession()).list(getSession());
				try {
					Cache.putEntry(getSessionAttribute("customer_account_docid")+"clients", cnts);
				} catch (Exception e) { }
			}
			if (txpr==null) {
				txpr = cacc.taxpayers(getSession()).list(getSession());
				try {
					Cache.putEntry(getSessionAttribute("customer_account_docid")+"taxpayers", txpr);
				} catch (Exception e) { }				
			}
			disconnect();
			connect();
			User usr = new User(getSession(), getSessionAttribute("user_docid"));
			ArrayList<TaxPayer> ftxpr = new ArrayList<TaxPayer>();
			if (usr.getAllowedTaxPayers().size()>0 || usr.getDeniedTaxPayers().size()>0) {
				for (TaxPayer t : txpr)
					if (usr.getAllowedTaxPayers().contains(t) && !usr.getDeniedTaxPayers().contains(t) &&
						containsSubstr(t, f))
						ftxpr.add(t);
			} else {
				for (TaxPayer t : txpr)
					if (containsSubstr(t, f))
						ftxpr.add(t);				
			}
			txpr = ftxpr;
			disconnect();
		} catch (StorageException e) {
			Log.out.error(e.getMessage(), e);
		} finally { close(); }
		Log.out.debug("End BaseListbean.preload()");
	}
	
	public CustomerAccount getCustomerAccount() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		if (cacc==null) preload();
		return cacc;
	}

	protected void setCustomerAccount(CustomerAccount ca) {
		cacc = ca;
	}
	
	public Collection<Client> getClients() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		if (cnts==null) preload();
		return cnts;
	}

	public Collection<TaxPayer> getTaxPayers() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		if (txpr==null) preload();
		return txpr;
	}

	protected void setTaxPayers(Collection<TaxPayer> c) {
		txpr = c;
	}
	
}
