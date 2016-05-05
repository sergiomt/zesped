package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.zesped.Log;
import com.zesped.model.Employee;
import com.zesped.model.TaxPayer;

public class SaveEmployee extends BaseAjaxBean {

	@ValidationMethod(on="save")
	public void validateName(ValidationErrors ignore) {
		final String sName = getParam("employee.name");
		if (sName==null)
			addError(new LocalizableError("com.zesped.action.SaveEmployee.name.valueNotPresent"));	    	  
		else if (sName.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveEmployee.name.valueNotPresent"));	    	  
	}

	@ValidationMethod(on="save")
	public void validateIds(ValidationErrors ignore) {
		final String sTaxId = getParam("employee.taxId","");
		final String sEmpId = getParam("employee.employeeId","");
		if (sTaxId.length()>20)
			addError(new LocalizableError("com.zesped.action.SaveEmployee.taxId.valueTooLong"));
		if (sEmpId.length()>20)
			addError(new LocalizableError("com.zesped.action.SaveEmployee.employeeId.valueTooLong"));
	}
	
	@DefaultHandler
	public Resolution save() {
		final String sFormerId = getParam("employee.id");
		if (getErrorsCount()==0) {
			try {
				Employee oEmpl;
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				if (sFormerId.length()>0) {
					  oEmpl = new Employee();
					  oEmpl.load(getSession(), sFormerId);
				} else {
					  TaxPayer oTxpr = new TaxPayer(getSession().getDms(), getParam("taxPayer"));
					  oEmpl = new Employee(getSession(), oTxpr.employees(getSession()));
				}
				saveRequest(oEmpl);
				disconnect();
	    		addDataLine("uuid",oEmpl.getUuid());
	    		addDataLine("id",oEmpl.id());
			} catch (Exception xcpt) {
				Log.out.error(xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getMessage()));
			} finally {
				close();
			}
		}
	    return AjaxResponseResolution();
	}	
}
