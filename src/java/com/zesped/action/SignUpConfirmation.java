package com.zesped.action;

import com.zesped.model.CaptureService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class SignUpConfirmation extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/signupconfirmation.jsp" ;
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@DefaultHandler
	public Resolution form() {
		
		new SendDelayedMail(getSessionAttribute("fullname"), getSessionAttribute("email"), getSessionAttribute("user_docid"), getSessionAttribute("user_uuid"), 90000l).start();
		
		removeSessionAttribute("nickname");
		removeSessionAttribute("password");
		removeSessionAttribute("fullname");
		removeSessionAttribute("businessname");
		removeSessionAttribute("user_uuid");
		removeSessionAttribute("user_docid");
		removeSessionAttribute("customer_acount");
		removeSessionAttribute("customer_account_docid");
		removeSessionAttribute("taxpayer_docid");
		removeSessionAttribute("employee_uuid");
		removeSessionAttribute("taxpayer_docid");
		removeSessionAttribute("role");

		return new ForwardResolution(FORM);
	}
}
