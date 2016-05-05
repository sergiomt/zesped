package com.zesped.action;

import java.util.Collection;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.Role;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class EditUser extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/edituser.jsp";

	private User usr;
	private Employee emp;
	private Role rle;
	private String txp;
	private boolean act;
	private boolean ise;
	private String pw2;
	private String prm;
	
	public EditUser() {
		usr = null;
		rle = null;
		txp = "";
		act = true;
	}

	public String getId() {
		return getParam("id","");
	}

	public boolean getActive() {
		return act;
	}

	public void setActive(boolean a) {
		act = a;
	}

	public boolean getApprove() {
		return usr.canApproveInvoices();
	}

	public void setApprove(boolean a) {
		usr.canApproveInvoices(a);
	}

	public boolean getSettle() {
		return usr.canSettleBillNotes();
	}

	public void setSettle(boolean s) {
		usr.canSettleBillNotes(s);
	}

	public boolean getPremium() {
		return usr.canUsePremiumCaptureServiceFlavor();
	}

	public void setPremium(boolean p) {
		usr.canUsePremiumCaptureServiceFlavor(p);
	}
	
	public Role getRole() {
		Log.out.debug("Getting user role "+rle);
		return rle;
	}

	public void setRole(Role r) {
		Log.out.debug("Setting user role "+r);
		rle = r;
	}

	public User getUser() {
		return usr;
	}

	public void setUser(User u) {
		usr = u;
	}

	public boolean getIsEmployee() {
		return ise;
	}
	
	public Employee getEmployee() {
		return emp;
	}

	public void setEmployee(Employee e) {
		emp = e;
	}
	
	public String getTaxPayer() {
		return txp;
	}

	public void setTaxPayer(String t) {
		txp = t;
	}

	public String getPassword2() {
		return pw2;
	}

	public void setPassword2(String p) {
		pw2 = p;
	}

	public String getPermissions() {
		return prm;
	}

	public void setPermissions(String p) {
		prm = p;
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public Collection<TaxPayer> getTaxPayers() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		AtrilSession oSes = DAO.getAdminSession("EditUser");
		CustomerAccount oAcc = new CustomerAccount(oSes.getDms().getDocument(getSessionAttribute("customer_account_docid")));
		Collection<TaxPayer> taxpayers = oAcc.taxpayers(oSes).list(oSes);
		oSes.disconnect();
		oSes.close();
		return taxpayers;
	}

	@DefaultHandler
	public Resolution form() {
		Log.out.debug("Begin Edituser.form()");
		if (getParam("id")==null) {
			ise = false;
			usr = new User();
  		    emp = new Employee();
  		    setActive(true);
  		    setRole(Role.user);
  		    setPermissions("all");
		} else {
			try {
	    		  connect();
	    		  setUser(new User(getSession(), getParam("id")));
	    		  setActive(getUser().isActive());
	    		  setRole(getUser().getRole(new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"))));
	    		  if (getUser().getAllowedTaxPayers().size()>0)
	    			  setPermissions("allow");
	    		  else if (getUser().getDeniedTaxPayers().size()>0)
	    			  setPermissions("deny");
	    		  else
	    			  setPermissions("all");
	    		  emp = new Employee();
	    		  Document e = emp.exists(getSession(), "employee_uuid", usr.getNickName());
	    		  if (e!=null) {
	    			  ise = true;
	    			  emp.load(getSession(), e.id());
		    		  setTaxPayer(emp.getTaxPayer(getSession()).id());
	    		  }
	    		  disconnect();
	    	  } catch (Exception xcpt) {
	    		  Log.out.error(xcpt.getMessage(), xcpt);
	    	  } finally {
	    		  close();
	    	  }			
		}
		Log.out.debug("End Edituser.form()");
		return new ForwardResolution(FORM);
	}	

}
