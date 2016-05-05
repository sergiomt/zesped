package com.zesped.action;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.knowgate.dfs.StreamPipe;
import com.knowgate.misc.Gadgets;
import com.zesped.Log;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;
import com.zesped.util.AsyncSendInvitation;

import es.ipsa.atril.exceptions.ElementNotFoundException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

public class SendInvitations extends BaseAjaxBean {
	
	@ValidationMethod(on="send")
	public void validateEmailSyntax(ValidationErrors ignore) {
		for (int e=0; e<=6; e++) {
			final String sEmail = getParam("email["+String.valueOf(e)+"]","");
			if (sEmail.trim().length()>0)
				if (!Gadgets.checkEMail(sEmail))
					addError(new LocalizableError("com.zesped.action.SendInvitations.email.invalidSyntax",sEmail));
		}
	}

	@ValidationMethod(on="send")
	public void validateNames(ValidationErrors ignore) {
		for (int e=0; e<=6; e++) {
			final String sEmail = getParam("email["+String.valueOf(e)+"]","");
			final String sFirstName = getParam("firstName["+String.valueOf(e)+"]","");
			if (sEmail.trim().length()>0 && sFirstName.trim().length()==0)
				addError(new LocalizableError("com.zesped.action.SendInvitations.firstName.valueNotPresent",sEmail));
		}
	}

	@ValidationMethod(on="send")
	public void validatePreviousPermissions(ValidationErrors ignore) {
		String[] aTaxPayer = getParam("taxPayer").split(",");
		try {
			connect();
			for (int t=0; t<aTaxPayer.length; t++) {
				TaxPayer oTxp = new TaxPayer (getSession().getDms(), aTaxPayer[t]);
				ArrayList<User> aAlu = oTxp.allowedUsers(getSession());
				for (int e=0; e<=6; e++) {
					final String sEmail = getParam("email["+String.valueOf(e)+"]","").trim().toLowerCase();
					if (sEmail.trim().length()>0) {
						try {
							User.forEmail(sEmail);
							for (User u : aAlu) {
								if (u.getEmail().equals(sEmail))
									addError(new LocalizableError("com.zesped.action.SendInvitations.user.alreadyAuthorized",sEmail,oTxp.getBusinessName()));
							}
						} catch (ElementNotFoundException enfe) { }
					}
				} // next				
			} // next
			disconnect();
		} catch (Exception xcpt) {
			Log.out.error("SendInvitations.validatePreviousPermissions() "+xcpt.getMessage(), xcpt);
			addError(new SimpleError(xcpt.getMessage()));
		} finally {
			close();
		}
	}
	
	@DefaultHandler
	public Resolution send() {
		if (getErrorsCount()==0) {
			final boolean bCreateEmployee = getParam("employee","0").equals("1");
			try {
				connect();
				User oSender = new User(getSession(), getSessionAttribute("user_docid"));
				disconnect();
				String[] aTaxPayers = getParam("taxPayer").split(",");
				ByteArrayOutputStream oTxt = new ByteArrayOutputStream();
				ByteArrayOutputStream oHtm = new ByteArrayOutputStream();
				new StreamPipe().between(getClass().getResourceAsStream("Invitation.txt"), oTxt);
				new StreamPipe().between(getClass().getResourceAsStream("Invitation.html"),oHtm);
				
				for (int e=0; e<=6; e++) {
					final String sEmail = getParam("email["+String.valueOf(e)+"]","").trim().toLowerCase();
					if (sEmail.trim().length()>0) {
						new AsyncSendInvitation(sEmail, getParam("email["+String.valueOf(e)+"]"), getParam("firstName["+String.valueOf(e)+"]"), getParam("lastName["+String.valueOf(e)+"]"), oSender.getFirstName(), oSender.getLastName(), aTaxPayers, getSessionAttribute("customer_account_docid"), bCreateEmployee, getParam("approve","").length()>0, getParam("settle","").length()>0, getParam("premium","").length()>0,new StringBuffer(new String(oTxt.toByteArray())),new StringBuffer(new String(oHtm.toByteArray()))).start();
					} //fi
				} // next
			} catch (Exception xcpt) {
				Log.out.error("SendInvitations.send() "+xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getMessage()));
			} finally {
				close();
			}
		} else {
			Log.out.debug("SendInvitations.send() "+String.valueOf(getErrorsCount())+" validation errors found");			
		}
	    return AjaxResponseResolution();
	}	
	
}
