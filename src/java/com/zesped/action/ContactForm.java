package com.zesped.action;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;

import javax.mail.MessagingException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.Validate;

import com.knowgate.storage.StorageException;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Messages;
import com.zesped.model.User;

public class ContactForm extends BaseActionBean {

	private String name, email, subject, text, uuid;
	
	private static final String FORM="/WEB-INF/jsp/contactform.jsp" ;
	
	public CaptureService getCaptureService() {
		return CaptureService.NONE;
	}

	@Validate(required=true,on="send", maxlength=128)
	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}

	@Validate(required=true, on="send", maxlength=200, converter=EmailTypeConverter.class)
	public String getEmail() {
		return email;
	}

	public void setEmail(String e) {
		email = e;
	}

	@Validate(required=true, on="send", maxlength=100)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String s) {
		subject = s;
	}
	
	@Validate(required=true, on="send", maxlength=2000)
	public String getText() {
		return text;
	}

	public void setText(String t) {
		text = t;
	}
	
	public Resolution send() {	
		try {
			connect();
			Messages.inquire(getSession(), getName(), getEmail(), uuid, getSubject(), getText());
			disconnect();
			return new RedirectResolution("/acknowledge.jsp");
		} catch (Exception e) {
			Log.out.error(e.getClass().getName()+" "+e.getMessage(), e);
			return new RedirectResolution("/error.jsp?e=messagingexception");
		} finally {
			close();
		}
	}

	@DefaultHandler
	public Resolution form() {
		final String sUsrId = getSessionAttribute("user_docid");
		if (sUsrId!=null) {
			try {
				connect();
				User oUsr = new User(getSession(), sUsrId);
				setName(oUsr.getFirstName()+" "+oUsr.getLastName());
				setEmail(oUsr.getEmail());
				uuid = oUsr.getString("user_uuid");
				disconnect();
			} catch (StorageException e) {
			} finally {
				close();
			}
		} else {
			name = email = subject = text = uuid = "";
		}
		return new ForwardResolution(FORM);
	}
}