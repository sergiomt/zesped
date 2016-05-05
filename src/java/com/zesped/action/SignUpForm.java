package com.zesped.action;

import java.util.Date;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;

import com.zesped.model.User;
import com.zesped.util.AsyncAccountCreator;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.knowgate.acl.ACL;
import com.knowgate.misc.Gadgets;
import com.knowgate.misc.MD5;
import com.knowgate.storage.StorageException;

import es.ipsa.atril.eventLogger.AtrilEvent;

public class SignUpForm extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/signup.jsp" ;

	public CaptureService getCaptureService() {
		return CaptureService.NONE;
	}
	
	private User user;
	
    @ValidateNestedProperties({        
        @Validate(field="email", required=true, on="save", maxlength=200, converter=EmailTypeConverter.class),
        @Validate(field="firstName", required=true, on="save", maxlength=64),
        @Validate(field="lastName", required=true, on="save", maxlength=64),
        @Validate(field="password",required=true, on="save", minlength=6, maxlength=20)
    })
    public User getUser() {
    	return user;
    }

    private boolean accept;

	@Validate(required=true,expression="${accept==true}",on="save")
	public boolean getAccept() {
		return accept;
	}

	public void setAccept(boolean b) {
		accept = b;
	}

    private String passw2;

	public String getPassword2() {
		return passw2;
	}

	public void setPassword2(String sPassw2) {
		passw2 = sPassw2;
	}

    private String captcha;

    @Validate(required=true,on="save")
	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String sCaptcha) {
		captcha = sCaptcha;
	}

	private String businessName;
    
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String sName) {
		businessName = Gadgets.removeChars(sName, "\"*?").trim().toUpperCase();
	}

	private String taxId;
    
	@Validate(maxlength=16, on="save")
	public String getTaxId() {
		return taxId==null ? "" : taxId;
	}

	public void setTaxId(String sTaxId) {
		taxId = sTaxId;
	}
	
	public SignUpForm() {
		user = new User();
	}

	@DefaultHandler
	public Resolution form() {
	  return new ForwardResolution(FORM);
	}

	public Resolution save() {
	  try {
		long lEnd, lStart = new Date().getTime();
		
		connect();

		Log.out.debug("PROFILING: Connection time "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;

		user.create(getSession());

		commit();
		
		Log.out.debug("PROFILING: Create User "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;

		new AsyncAccountCreator(user).start();

		lStart = new Date().getTime();
		
		setSessionAttribute("fullname", user.getFirstName()+" "+user.getLastName());
		setSessionAttribute("email", user.getEmail());
		setSessionAttribute("user_uuid", user.getString("user_uuid"));
		setSessionAttribute("user_docid", user.getDocument().id());

		Log.out.debug("PROFILING: Set session attributes "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		Employee emp = new Employee();
		if (emp.exists(getSession(), "employee_uuid", user.getString("user_uuid"))!=null)
			setSessionAttribute("employee_uuid", user.getString("user_uuid"));
		else
			setSessionAttribute("employee_uuid", "");

		Log.out.debug("PROFILING: Get Employee "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		disconnect();

		Log.out.debug("PROFILING: Disconnection time "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		
		return new RedirectResolution(SignUpConfirmation.class);
		
	  } catch (Exception e) {
		getContext().getMessages().add(new SimpleMessage("ERROR "+e.getMessage(), user));		  
		return new RedirectResolution(SignUpForm.class);
	  } finally { close(); }
	}

	@ValidationMethod(on="save")
	public void validate(ValidationErrors errors) {

		MD5 oMD5 = new MD5(getCaptcha()+ACL.getRC4key());

		if (!oMD5.asHex().equals(getSessionAttribute("captcha_key")) && false) {

			try {
				connect();
				DAO.log(getSession(), user.getClass(), "CAPTCHA MISMATCH", AtrilEvent.Level.WARNING, ";"+user.getEmail());
				disconnect();
			} catch (StorageException ignore) { }

			Log.out.warn("Captch mismatch: signature "+oMD5.asHex()+" for text "+getCaptcha()+" does not match session signature "+getSessionAttribute("captcha_key"));

			errors.add("captcha", new LocalizableError("com.zesped.action.SignUpForm.captchaMismatch"));			 

		} else {
		
			try {
				User.forEmail(user.getEmail());
				errors.add("email", new LocalizableError("com.zesped.action.SignUpForm.emailAlreadyExists"));
			} catch (Exception e) { } 
			try {
				CustomerAccount.forBusinessName(getBusinessName());
				errors.add("business_name", new LocalizableError("com.zesped.action.SignUpForm.businessNameAlreadyExists"));
			} catch (Exception e) { }
		} // fi
	}
	
	@ValidationMethod(on="save")
	public void validatePassw(ValidationErrors errors) {
		if (!user.getPassword().equals(getPassword2()))
			errors.add("password", new LocalizableError("com.zesped.action.SignUpForm.passwMismatch"));
	}
}