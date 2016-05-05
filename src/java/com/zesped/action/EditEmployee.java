package com.zesped.action;

import java.util.Collection;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class EditEmployee extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/editemployee.jsp";

	private Employee emplyee;
	
	private String id, taxpayer;
	
	public EditEmployee() {
		id=taxpayer="";
		emplyee=null;
	}

    @ValidateNestedProperties({        
        @Validate(field="name", maxlength=100),
        @Validate(field="taxId", maxlength=64),
    })	
	public Employee getEmployee() {
		return emplyee;
	}

	public void setEmployee(Employee e) {
		emplyee = e;
	}

	public String getId() {
		return id;
	}

	public void setId(String i) {
		id = i;
	}
	
	public String getTaxPayer() {
		return taxpayer;
	}

	public void setTaxPayer(String t) {
		taxpayer = t;
	}
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public Collection<TaxPayer> getTaxPayers() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		AtrilSession oSes = DAO.getAdminSession("EditEmployee");
		CustomerAccount oAcc = new CustomerAccount(oSes.getDms().getDocument(getSessionAttribute("customer_account_docid")));
		Collection<TaxPayer> taxpayers = oAcc.taxpayers(oSes).list(oSes);
		oSes.disconnect();
		oSes.close();
		return taxpayers;
	}
	
	@DefaultHandler
	public Resolution form() {
		if (getParam("id")==null) {
			emplyee = new Employee();
		} else {
			try {
	    		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
	    		  setId(getParam("id"));
	    		  setEmployee(new Employee(getSession().getDms(), getId()));
	    		  setTaxPayer(getParam("taxpayer",""));	    			  
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
