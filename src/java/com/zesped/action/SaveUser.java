package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.Employees;
import com.zesped.model.Role;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;

public class SaveUser extends BaseAjaxBean {

	@ValidationMethod(on="save")
	public void validateAtLeastOneAdmin(ValidationErrors ignore) {
		final String sFormerId = getParam("id","");
		final String sRole = getParam("role","");
		if (sFormerId.length()>0 && !sRole.equals("admin")) {					  
			try {
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				CustomerAccount oAcc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
				int nAdminCount = 0;
				for (User oUsr : oAcc.getAdmins(getSession())) {
					if (!oUsr.id().equals(sFormerId)) nAdminCount++;
				}
				if (nAdminCount==0)
					addError(new LocalizableError("com.zesped.action.SaveUser.atLeastOneAdminIsRequired"));
				disconnect();
			} catch (Exception xcpt) {
				Log.out.error(xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getClass().getName()+" "+xcpt.getMessage()));
			} finally {
				close();
			}
		}		
	}	

	@ValidationMethod(on="save")
	public void validateRole(ValidationErrors ignore) {
		final String sRole = getParam("role","");
		if (sRole.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveUser.role.valueNotPresent"));
	}

	@ValidationMethod(on="save")
	public void validateUserData(ValidationErrors ignore) {
		final String s1stName = getParam("user.firstName","");
		final String s2ndName = getParam("user.lastName","");
		final String sEmail = getParam("user.email","");
		if (s1stName.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveUser.user.firstName.valueNotPresent"));
		if (s2ndName.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveUser.user.lastName.valueNotPresent"));
		if (s1stName.length()>64)
			addError(new LocalizableError("com.zesped.action.SaveUser.user.firstName.valueTooLong"));
		if (s2ndName.length()>64)
			addError(new LocalizableError("com.zesped.action.SaveUser.user.lastName.valueTooLong"));
		if (sEmail.length()==0)
			addError(new LocalizableError("com.zesped.action.SaveUser.user.email.valueNotPresent"));
		if (!Gadgets.checkEMail(sEmail))
			addError(new LocalizableError("com.zesped.action.SaveUser.user.email.invalidEmail"));
	}

	@ValidationMethod(on="save")
	public void validateEmployeeData(ValidationErrors ignore) {
		final String sTaxId = getParam("employee.taxId","");
		final String sEmpId = getParam("employee.employeeId","");
		final String sTxpr = getParam("taxPayer","");
		final String sPerms = getParam("permissions","all");
		final String sSelected = getParam("selectedTaxPayers","");
		if (sTaxId.length()>20)
			addError(new LocalizableError("com.zesped.action.SaveUser.employee.taxId.valueTooLong"));
		if (sEmpId.length()>20)
			addError(new LocalizableError("com.zesped.action.SaveUser.employee.employeeId.valueTooLong"));
		if (sPerms.equals("deny") && sSelected.length()>0) {
			if (Gadgets.search(sSelected.split("###"), sTxpr)>=0)
				addError(new LocalizableError("com.zesped.action.SaveUser.cannotDenyAccessToEmployee"));
		}
	}	

	@ValidationMethod(on="save")
	public void validatePassword(ValidationErrors ignore) {
		final String sPassw1 = getParam("user.password");
		final String sPassw2 = getParam("password2");
		if (sPassw2!=null) {
			if (sPassw2.length()<6)
				addError(new LocalizableError("com.zesped.action.SaveUser.password.valueTooShort"));
			if (sPassw2.length()>20)
				addError(new LocalizableError("com.zesped.action.SaveUser.password.valueTooLong"));
			if (!sPassw2.equals(sPassw1))
				addError(new LocalizableError("com.zesped.action.SaveUser.passwMismatch"));
		}
	}	
	
	@DefaultHandler
	public Resolution save() {
		final String sFormerId = getParam("id","");
		final String sRole = getParam("role","");
		final String sTxpr = getParam("taxPayer","");
		final String sPerms = getParam("permissions","all");
		final String sSelected = getParam("selectedTaxPayers","");
		final boolean bActive = getParam("active","").length()>0;		
		if (getErrorsCount()==0) {
			Log.out.debug("SaveUser.save("+sFormerId+")");
			Log.out.debug("permissions is "+sPerms+" selected TaxPayers are "+sSelected);
			try {
				User oUsr;
				connect();
				CustomerAccount oAcc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
				Log.out.debug("Got customer account "+oAcc.id());
				if (sFormerId.length()>0) {
					oUsr = new User(getSession(), sFormerId);
					LoginInterceptor.expire(oUsr.getNickName(), oUsr.getPassword());
					oUsr.setFirstName(getParam("user.firstName"));
					oUsr.setLastName(getParam("user.lastName"));
					oUsr.setEmail(getParam("user.email"));
					if (bActive) oUsr.activate(); else oUsr.deactivate();
					if (!Role.valueOf(sRole).equals(Role.admin)) {
						oUsr.canApproveInvoices(getParam("approve","").length()>0);
						oUsr.canSettleBillNotes(getParam("settle","").length()>0);
						oUsr.canUsePremiumCaptureServiceFlavor(getParam("premium","").length()>0);
					} else {
						oUsr.canApproveInvoices(true);
						oUsr.canSettleBillNotes(true);
						oUsr.canUsePremiumCaptureServiceFlavor(true);
					}
					oUsr.save(getSession());
					if (!oUsr.getRole(oAcc).equals(Role.valueOf(sRole)))
						oUsr.setRole(getSession(), oAcc, Role.valueOf(sRole));
				} else {
					String sUid = User.create(getSession(), getParam("user.firstName"), getParam("user.lastName"), getParam("user.email"), getParam("user.password"), bActive).id();
					commit();
					oUsr = new User(getSession(), sUid);
					oUsr.canApproveInvoices(getParam("approve","").length()>0);
					oUsr.canSettleBillNotes(getParam("settle","").length()>0);
					oUsr.canUsePremiumCaptureServiceFlavor(getParam("premium","").length()>0);
					oUsr.setRole(getSession(), oAcc, Role.valueOf(sRole));
				}
				if (sTxpr.length()>0) {
					Dms oDms = getSession().getDms();
	    			TaxPayer oTxp = new TaxPayer(oDms, sTxpr);
					Employees oEms = oTxp.employees(getSession());
					Employee oEmp = new Employee();
					Document e = oEmp.exists(getSession(), "employee_uuid", oUsr.getNickName());
		    		if (e!=null) {
						Log.out.debug("Employee already exists");
		    			oEmp.load(getSession(), e.id());
		    			if (!oEmp.parentId().equals(oEms.id()))
		    				oEmp.getDocument().parents().replace(oDms.getDocument(oEmp.parentId()), oDms.getDocument(oEms.id()));
		    		} else {
						Log.out.debug("Employee does not exist");
		    			oEmp = new Employee(getSession(), oEms);
		    			oEmp.setUuid(oUsr.getNickName());
		    		}
		    		oEmp.setName(oUsr.getFirstName()+" "+oUsr.getLastName());
		    		oEmp.setEmployeeId(getParam("employee.employeeId"));
		    		oEmp.setTaxId(getParam("employee.taxId"));
		    		oEmp.setActive(bActive);
		    		oEmp.save(getSession());
				}
				oUsr.allowAll(getSession());
				if (sSelected.length()>0) {
					String[] aSelected = sSelected.split("###");
					if (sPerms.equals("allow"))
						for (int t=0; t<aSelected.length; t++)
							oUsr.allowTaxPayer(getSession(), aSelected[t]);
					else if (sPerms.equals("deny"))
						for (int t=0; t<aSelected.length; t++)
							if (!sTxpr.equals(aSelected[t]))
								oUsr.denyTaxPayer(getSession(), aSelected[t]);
					if (sTxpr.length()>0)
						if (Gadgets.search(aSelected, sTxpr)<0)
							oUsr.allowTaxPayer(getSession(), sTxpr);
				}
				disconnect();
	    		addDataLine("id",oUsr.id());
			} catch (Exception xcpt) {
				Log.out.error("SaveUser.save() "+xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getMessage()));
			} finally {
				close();
			}
		} else {
			Log.out.debug("SaveUser.save() "+String.valueOf(getErrorsCount())+" validation errors found");			
		}
	    return AjaxResponseResolution();
	}	
}
