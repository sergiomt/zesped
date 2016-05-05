package com.zesped.action;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.mail.Message.RecipientType;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.User;

import com.zesped.util.MailSessionHandler;

import es.ipsa.atril.eventLogger.AtrilEvent;

public class RecoverPassword extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/recoverpwd.jsp";
	
	private String sEmail, sUId;
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}


	@Validate(required=true,on="send")
	public String getEmail() {
		return sEmail;
	}

	public void setEmail(String e) {
		sEmail = e;
	}

	@ValidationMethod(on="send")
	public void emailExists(ValidationErrors errors) {
		try {
	  		  connect();
	  		  sUId = User.forEmail(getEmail());
	  		  disconnect();
	  	  } catch (Exception xcpt) {
	  		errors.add("password", new LocalizableError("com.zesped.action.RecoverPassword.emailDoesNotExist"));
	  	  } finally {
	  		  close();
	  	  }			
	}

	public Resolution send() {
		try {
  		  connect();
  		  User oUsr = new User(getSession(),sUId);
  		  String sPwd = oUsr.getPassword();
  		  disconnect();
  		  DAO.log(User.class, "SEND PASSWORD", AtrilEvent.Level.INFO, getEmail());
  		  ByteArrayOutputStream oByOut = new ByteArrayOutputStream();
  		  PrintStream oPrt = new PrintStream(oByOut);  		
  		  MailSessionHandler oHlr = new MailSessionHandler();
  		  oHlr.sendMessage("Su contraseña de acceso", "Zesped", "noreply@zesped.com", "noreply@zesped.com",
	                 new String[] {getEmail()}, RecipientType.TO,
	                 "Su contraseña de acceso a Zesped es "+sPwd, "<html><body>Su contraseña de acceso a Zesped es "+sPwd+"</body></html>",
	                 "ISO8859_1", null, null, null, oPrt);
  		  oPrt.close();
  		  Log.out.info(oByOut.toString());
  		  oByOut.close();
  		  return new ForwardResolution("/enter.jsp?e=pwdsent");
  	  } catch (Exception xcpt) {
  		  Log.out.error(xcpt.getMessage(), xcpt);
  	  } finally {
  		  close();
  	  }			
		return new ForwardResolution(FORM);
	}
	
	@DefaultHandler
	public Resolution form() {
		return new ForwardResolution(FORM);
	}
}
