package com.zesped.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

public class ListUsers extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/listusers.jsp";
	
	private ArrayList<User> admins, users, guests;
    private TaxPayer taxpr;
	
	private String sortby;

	public ListUsers() {
		admins = new ArrayList<User>();
		users  = new ArrayList<User>();
		guests = new ArrayList<User>();
		sortby = "";
		taxpr = null;
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@Validate(converter=User.class)
	public ArrayList<User> getAdmins() {
		return admins;
	}

	public void setAdmins(ArrayList<User> a) {
		admins = a;
	}

	@Validate(converter=User.class)
	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> u) {
		users = u;
	}

	@Validate(converter=User.class)
	public ArrayList<User> getGuests() {
		return guests;
	}

	public void setGuests(ArrayList<User> g) {
		guests = g;
	}

	public ArrayList<User> getAll() {
		ArrayList<User> aall = new ArrayList<User>();
		aall.addAll(admins);
		aall.addAll(users);
		aall.addAll(guests);
		Comparator<User> cmp = null;
		if (sortby.equals("1"))
			cmp = new FirstNameComparator();
		else if (sortby.equals("2"))
			cmp = new LastNameComparator();
		else if (sortby.equals("3"))
			cmp = new EmailComparator();
		if (cmp!=null)
			Collections.sort(aall, cmp);
		Log.out.debug("ListUsers.all() : "+String.valueOf(aall.size()));
		return aall;
	}

	public TaxPayer getTaxPayer() {
		return taxpr;
	}

	private class FirstNameComparator implements Comparator<User> {
		public int compare(User u1, User u2) {
			final String s1 = u1.getFirstName();
			final String s2 = u2.getFirstName();
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

	private class LastNameComparator implements Comparator<User> {
		public int compare(User u1, User u2) {
			final String s1 = u1.getLastName();
			final String s2 = u2.getLastName();
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

	private class FullNameComparator implements Comparator<User> {
		public int compare(User u1, User u2) {
			final String s1 = (u1.getFirstName()==null ? "" : u1.getFirstName())+" "+(u1.getLastName()==null ? "" : u1.getLastName());
			final String s2 = (u2.getFirstName()==null ? "" : u2.getFirstName())+" "+(u2.getLastName()==null ? "" : u2.getLastName());
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
	
	private class EmailComparator implements Comparator<User> {
		public int compare(User u1, User u2) {
			final String s1 = u1.getEmail();
			final String s2 = u2.getEmail();
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
	
	private void removeUnactiveMembers(ArrayList<User> members) {
		int nCount = members.size();
		for (int u=0; u<nCount; u++) {
			if (!members.get(u).isActive()) {
				members.remove(u--);
				nCount--;
			}
		} //next		
	}

	private void filterMembersByPattern(ArrayList<User> members, String sFind, Pattern oPatt) {
		int nCount = members.size();
		for (int u=0; u<nCount; u++) {
			User oUsr = members.get(u);
			String sFullname = oUsr.getFirstName()+" "+oUsr.getLastName();
			if (!oPatt.matcher(oUsr.getFirstName()).matches() &&
				!oPatt.matcher(oUsr.getLastName()).matches() &&
				!oPatt.matcher(sFullname).matches() &&
				!oUsr.getEmail().equalsIgnoreCase(sFind)) {
				members.remove(u--);
				nCount--;
			}
		} //next
	}

	private void filterMembersByTaxPayer(ArrayList<User> members) {
		int nCount = members.size();
		for (int u=0; u<nCount; u++) {
			User oUsr = members.get(u);
			if (oUsr.getDeniedTaxPayers().contains(taxpr) || (oUsr.getAllowedTaxPayers().size()>0 && !oUsr.getAllowedTaxPayers().contains(taxpr))) {
				members.remove(u--);
				nCount--;
			}
		} //next
	}

	@DefaultHandler
	public Resolution form() {

		final String role = getParam("role","");
		final String txpr = getParam("taxpayer","");

		try {
			connect();
			CustomerAccount cacc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
			if (role.length()==0 || role.equalsIgnoreCase("admin")) setAdmins(cacc.getAdmins(getSession()));
			if (role.length()==0 || role.equalsIgnoreCase("user" )) setUsers(cacc.getUsers(getSession()));
			if (role.length()==0 || role.equalsIgnoreCase("guest")) setGuests(cacc.getGuests(getSession()));
			if (txpr.length()>0) {
				taxpr = new TaxPayer(getSession().getDms(), txpr);
				filterMembersByTaxPayer(getAdmins());
				filterMembersByTaxPayer(getUsers());
				filterMembersByTaxPayer(getGuests());
			}
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error(xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }

		Log.out.debug("ListUsers.form() Total users found before filtering "+String.valueOf(getAdmins().size()+getUsers().size()+getGuests().size()));

		if (getParam("onlyactive","0").equals("1")) {
			removeUnactiveMembers(admins);
			removeUnactiveMembers(users);
			removeUnactiveMembers(guests);
		}

		final String sFind = getParam("find","");
		if (sFind.length()>0) {
			Pattern oPatt = Pattern.compile(sFind+".*", Pattern.CASE_INSENSITIVE);
			filterMembersByPattern(getAdmins(), sFind, oPatt);
			filterMembersByPattern(getUsers(), sFind, oPatt);
			filterMembersByPattern(getGuests(), sFind, oPatt);
		}

		for (int u=0; u<getAdmins().size(); u++)
		    admins.get(u).put("role", "admin");
		for (int u=0; u<getUsers().size(); u++)
			users.get(u).put("role", "user");
		for (int u=0; u<getGuests().size(); u++)
		    guests.get(u).put("role", "guest");

		Log.out.debug("ListUsers.form() Total users left after filtering "+String.valueOf(getAdmins().size()+getUsers().size()+getGuests().size()));
		
		sortby = getParam("sort","");
		Comparator<User> oCmp = null;
		if (sortby.equals("1"))
			oCmp = new FirstNameComparator();
		else if (sortby.equals("2"))
			oCmp = new LastNameComparator();
		else if (sortby.equals("3"))
			oCmp = new FullNameComparator();
		else if (sortby.equals("4"))
			oCmp = new EmailComparator();
		if (oCmp!=null) {
			Collections.sort(getAdmins(), oCmp);			
			Collections.sort(getUsers(), oCmp);			
			Collections.sort(getGuests(), oCmp);			
		}			

		Log.out.debug("ForwardResolution("+FORM+")");

		return new ForwardResolution(FORM);
	}		
}
