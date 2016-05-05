package com.zesped.action;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.knowgate.storage.StorageException;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.sec.exceptions.AuthenticationException;

public class ActivateUser extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/activateuser.jsp" ;
	
	private String id,uid,email,passw,passw2;
	private boolean changepassw, hasorders;
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String i) {
		id = i;
	}

	public String getEmail() {
		return email;
	}

	public boolean getHasOrders() {
		return hasorders;
	}
	
	public boolean getChangePassword() {
		return changepassw;
	}

    @Validate(required=true, on="save", minlength=6, maxlength=20)
	public String getPassword() {
		return passw;
	}

	public void setPassword(String p) {
		passw = p;
	}

	public String getPassword2() {
		return passw2;
	}

	public void setPassword2(String p) {
		passw2 = p;
	}

	@ValidationMethod(on="save")
	public void validatePassw(ValidationErrors errors) {
		if (!getPassword().equals(getPassword2()))
			errors.add("password", new LocalizableError("com.zesped.action.SignUpForm.passwMismatch"));
	}
	
	private void setSessionAttributes(HashMap<String,Object> oSessionInfo) {
		Iterator<String> oIter = oSessionInfo.keySet().iterator();
		HttpSession oSes = getContext().getRequest().getSession();
		while (oIter.hasNext()) {
			String sKey = oIter.next();
			if (!sKey.equals("hasorders"))
				oSes.setAttribute(sKey, oSessionInfo.get(sKey));
		}
	}

	private void setSessionAttributes(User user, CustomerAccount cacc, TaxPayer txpy, String emp) {
		setSessionAttribute("nickname", user.getNickName());
		setSessionAttribute("password", user.getPassword());
		setSessionAttribute("fullname", user.getFirstName()+" "+user.getLastName());
		setSessionAttribute("email", user.getEmail());
		setSessionAttribute("businessname", cacc.getBusinessName());
		setSessionAttribute("user_uuid", user.getString("user_uuid"));
		setSessionAttribute("user_docid", user.getDocument().id());
		setSessionAttribute("customer_acount", cacc.getUuid());
		setSessionAttribute("customer_account_docid", cacc.getDocument().id());
		setSessionAttribute("taxpayer_docid", txpy.getDocument().id());
		if (emp!=null) setSessionAttribute("employee_uuid", emp);
		getContext().getRequest().getSession().setAttribute("role", user.getRole(cacc));
	}

	@DefaultHandler
	public Resolution form() {

		User oUser;
		HashMap<String, Object> oSessionInfo;
		
		id = getParam("id","");
		uid = getParam("uid","");
		passw = getParam("pwd","");
		changepassw = passw.length()>0;
		
		if (id.length()==0)
			return new RedirectResolution("/error.jsp?e=usernotfound");
		if (uid.length()==0 && passw.length()==0)
			return new RedirectResolution("/error.jsp?e=userdatanotfound");
		
		try {
			oSessionInfo = Cache.getEntryMap(id+"activationinfo");
		} catch (Exception xcpt) {
			oSessionInfo = null;
		}

		if (oSessionInfo!=null) {

			setSessionAttributes(oSessionInfo);
			email = (String) oSessionInfo.get("email");
			hasorders = ((Boolean) oSessionInfo.get("hasorders")).booleanValue();
			if (!changepassw) {
				try {
					connect();
					oUser = new User(getSession(), id);
					oUser.activate();
					oUser.save(getSession());
					DAO.log(User.class, "ACTIVATE USER", AtrilEvent.Level.INFO, email+";"+id+";"+uid);
					disconnect();
				} catch (StorageException stge) {
					Log.out.error("ActivateUser.form() "+stge.getClass().getName()+" "+stge.getMessage(), stge);
				} finally {
					close();
				}
			}
			try {
				Cache.deleteEntry(id+"activationinfo");
			} catch (SQLException ignore) { }
			
		} else {
			try {
				connect();
				oUser = new User(getSession(), id);
				if (!oUser.getNickName().equals(uid) && !oUser.getPassword().equals(passw))
					throw new AuthenticationException("User Id. does not match UUID or Password");
				email = oUser.getEmail();
				if (!changepassw) {
					oUser.activate();
					oUser.save(getSession());
					DAO.log(User.class, "ACTIVATE USER", AtrilEvent.Level.INFO, email+";"+id+";"+uid);
				}
				CustomerAccount oCacc = new CustomerAccount(getSession(), oUser);
				Employee oEmpl = new Employee();
				String sEmpl = null;
				if (oEmpl.exists(getSession(), "employee_uuid", oUser.getNickName())!=null)
					sEmpl = oUser.getNickName();
				setSessionAttributes(oUser, oCacc, oCacc.taxpayers(getSession()).byDefault(getSession(),oUser),sEmpl);
				hasorders = (oCacc.orders(getSession()).count()>0);
				disconnect();				
				} catch (AuthenticationException acpt) {
					DAO.log(User.class, "ACTIVATE USER", AtrilEvent.Level.WARNING, email+";"+id+";"+uid);
					Log.out.warn("ActivateUser.form() "+acpt.getClass().getName()+" "+acpt.getMessage(), acpt);
					return new RedirectResolution("/error.jsp?e=systemsecurity");
				} catch (StorageException stge) {
					Log.out.error("ActivateUser.form() "+stge.getClass().getName()+" "+stge.getMessage(), stge);
					return new RedirectResolution("/error.jsp?e=errorindatabaseaccess");
				} finally {
					close();
				}
			
			}
		return new ForwardResolution(FORM);
	}

	public Resolution save() {
		try {
			connect();
			User oUser = new User(getSession(), getId());
			oUser.setPassword(getPassword());
			oUser.activate();
			oUser.save(getSession());
			DAO.log(User.class, "ACTIVATE USER", AtrilEvent.Level.INFO, email+";"+id+";"+uid);
			disconnect();
			setSessionAttribute("password", getPassword());
		} catch (Exception xcpt) {
			Log.out.error("ActivateUser.save() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
			return new RedirectResolution("/error.jsp?e=errorindatabaseaccess");
		} finally {
			close();
		}
		return new RedirectResolution(CaptureInvoice.class);
	}
	
}
