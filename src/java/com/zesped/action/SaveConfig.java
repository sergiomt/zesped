package com.zesped.action;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;
import com.zesped.model.User;

import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.exceptions.RecentlyUsedPasswordException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

public class SaveConfig extends BaseAjaxBean {

	@ValidationMethod(on="save")
	public void validatePasswords(ValidationErrors ignore) {
		final String sFormerPassword = getParam("formerPassword","");
		final String sNewPassword1 = getParam("newPassword","");
		final String sNewPassword2 = getParam("newPassword2","");
		try {
			connect();
			User oUsr = new User(getSession(), getSessionAttribute("user_docid"));
			if (!oUsr.getPassword().equals(sFormerPassword) && sFormerPassword.length()>0)
				addError("password", new LocalizableError("com.zesped.action.SaveConfig.formerPassWordMismatch"));
			else if (!sNewPassword1.equals(sNewPassword2))
				addError("password", new LocalizableError("com.zesped.action.SaveConfig.newPassWordsMismatch"));
			else if (sNewPassword1.length()<6 && sNewPassword1.length()>0)
				addError("password", new LocalizableError("com.zesped.action.SaveConfig.newPassWordTooShort"));
			disconnect();
		} catch (Exception e) {
			Log.out.error("SaveConfig.validatePasswords( "+e.getClass().getName()+" "+e.getMessage(), e);		
		} finally {
			close();
		}
	}

	@ValidationMethod(on="save")
	public void validateEmail(ValidationErrors ignore) {
		final String sEmail1 = getParam("user.email","");
		final String sEmail2 = getParam("email2","");
		try {
			if (!sEmail1.equals(sEmail2))
				addError("email", new LocalizableError("com.zesped.action.SaveConfig.emailMismatch"));
			else if (!Gadgets.checkEMail(sEmail1))
				addError("email", new LocalizableError("com.zesped.action.SaveConfig.emailSyntaxError"));
			try {
				if (!User.forEmail(sEmail1).equals(getSessionAttribute("user_docid")))
					addError("email", new LocalizableError("com.zesped.action.SaveConfig.emailAlreadyInUse"));
			} catch (ElementNotFoundException enfe) { }			
		} catch (Exception e) {
			Log.out.error("SaveConfig.validatePasswords( "+e.getClass().getName()+" "+e.getMessage(), e);		
		} finally {
			close();
		}
	}
	
	@DefaultHandler
	public Resolution save() {
		if (getErrorsCount()==0) {
			final String sFirstName = getParam("user.firstName","");
			final String sLastName = getParam("user.lastName","");
			final String sEmail = getParam("user.email","");
			final String sFormerPassword = getParam("formerPassword","");
			final String sNewPassword1 = getParam("newPassword","");
			try {
				connect();
				User oUsr = new User(getSession(), getSessionAttribute("user_docid"));
				if (sNewPassword1.length()>0 && sFormerPassword.length()>0) {
					oUsr.setPassword(sNewPassword1);
					LoginInterceptor.expire(oUsr.getNickName(), oUsr.getPassword());
					setSessionAttribute("password", sNewPassword1);
				}
				oUsr.setFirstName(sFirstName);
				oUsr.setLastName(sLastName);
				oUsr.setEmail(sEmail);
				oUsr.save(getSession());					
				setSessionAttribute("fullname", oUsr.getFirstName()+" "+oUsr.getLastName());
				disconnect();
				addDataLine("id",oUsr.id());
			} catch (RecentlyUsedPasswordException rupe) {
				addError("password", new LocalizableError("com.zesped.action.SaveConfig.recentlyUsedPassword"));
			} catch (Exception xcpt) {
				Log.out.error("SaveConfig.save( "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);		
			} finally {
				close();
			}
		}
	    return AjaxResponseResolution();
	}	
}
