package com.zesped.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.TaxPayer;

public class ListEmployees extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/listemployees.jsp";

	private HashMap<String,String> bnames;
	private ArrayList<Employee> employees;
	
    private TaxPayer taxpr;
	
	private String sortby;

	public ListEmployees() {
		sortby = "";
		taxpr = null;
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public ArrayList<Employee> getEmployees() {
		return employees;
	}

	public TaxPayer getTaxPayer() {
		return taxpr;
	}

	public String getBusinessName(String e) {
		if (bnames==null)
			return "";
		else if (bnames.containsKey(e))
			return bnames.get(e);
		else
			return "";
	}

	private class NameComparator implements Comparator<Employee> {
		public int compare(Employee e1, Employee e2) {
			final String s1 = e1.getName();
			final String s2 = e2.getName();
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
		
	private void removeUnactiveMembers() {
		int nCount = employees.size();
		for (int u=0; u<nCount; u++) {
			if (!employees.get(u).getActive()) {
				employees.remove(u--);
				nCount--;
			}
		} //next		
	}

	private void filterMembersByPattern(String sFind, Pattern oPatt) {
		int nCount = employees.size();
		if (sFind==null) sFind="";
		for (int u=0; u<nCount; u++) {
			Employee oEmp = employees.get(u);
			if (!oPatt.matcher(oEmp.getName()).matches() &&
				!sFind.equalsIgnoreCase(oEmp.getEmployeeId()) &&
				!sFind.equalsIgnoreCase(oEmp.getTaxId())) {
				employees.remove(u--);
				nCount--;
			}
		} //next
	}

	@DefaultHandler
	public Resolution form() {

		final String txpr = getParam("taxpayer","");

		try {
			connect();
			if (txpr.length()>0) {
				taxpr = new TaxPayer(getSession().getDms(), txpr);
				employees = taxpr.employees(getSession()).list(getSession());
			} else {
				CustomerAccount cacc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
				employees = new ArrayList<Employee>();
				bnames = new HashMap<String,String>();
				for (TaxPayer t : cacc.taxpayers(getSession()).list(getSession())) {
					ArrayList<Employee> emps = t.employees(getSession()).list(getSession());
					employees.addAll(emps);
					for (Employee e : emps)
						bnames.put(e.id(), t.getBusinessName());
				}
			}
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error(xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }

		if (getParam("onlyactive","0").equals("1")) {
			removeUnactiveMembers();
		}

		final String sFind = getParam("find","");
		if (sFind.length()>0) {
			Pattern oPatt = Pattern.compile(sFind+".*", Pattern.CASE_INSENSITIVE);
			filterMembersByPattern(sFind, oPatt);
		}
		
		sortby = getParam("sort","");
		Comparator<Employee> oCmp = null;
		if (sortby.equals("1"))
			oCmp = new NameComparator();
		if (oCmp!=null) {
			Collections.sort(employees, oCmp);			
		}			

		Log.out.debug("ForwardResolution("+FORM+")");

		return new ForwardResolution(FORM);
	}		
}
