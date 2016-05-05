package com.zesped.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;

import com.knowgate.dataxslt.FastStreamReplacer;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion.User;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.util.MailSessionHandler;

import es.ipsa.atril.eventLogger.AtrilEvent;

public class SendDelayedMail extends Thread {

	private String fullname, email, uid, uuid;
	private long delay;
	
	public SendDelayedMail(String sFullName, String sEmail, String sUserId, String sUserUuid, long iDelay) {
		fullname = sFullName;
		email = sEmail;
		uid = sUserId;
		uuid = sUserUuid;
		delay = iDelay;
	}

	public void run() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) { }
		FastStreamReplacer oRpl = new FastStreamReplacer();
  		try {
			MailSessionHandler oHlr = new MailSessionHandler();
			HashMap oMap = FastStreamReplacer.createMap(new String[] {"1","2","3"},
			new String[] {fullname,uid,uuid});
			ByteArrayOutputStream oByOut = new ByteArrayOutputStream();
			PrintStream oPrt = new PrintStream(oByOut);
			InputStream oTxt = getClass().getResourceAsStream("Confirmation.txt");
			InputStream oHtm = getClass().getResourceAsStream("Confirmation.html");
			oHlr.sendMessage("Acceso a zesped", "Zesped", "noreply@zesped.com", "noreply@zesped.com",
			new String[] {email}, RecipientType.TO, oRpl.replace(oTxt, oMap), oRpl.replace(oHtm, oMap),"ISO8859_1", null, null, null, oPrt);
			oPrt.close();
			Log.out.info(oByOut.toString());
			oByOut.close();
			DAO.log(User.class, "SEND ACTIVATION", AtrilEvent.Level.INFO, oByOut.toString());
		} catch (IOException ioe) {
			Log.out.error("SendInvitations.send() IOException "+ioe.getMessage(), ioe);
		} catch (NullPointerException npe) {
			Log.out.error("SendInvitations.send() NullPointerException "+npe.getMessage(), npe);
		} catch (IllegalArgumentException iae) {
			Log.out.error("SendInvitations.send() IllegalArgumentException "+iae.getMessage(), iae);
		} catch (SecurityException sec) {
			Log.out.error("SendInvitations.send() SecurityException "+sec.getMessage(), sec);
		} catch (MessagingException mse) {
			Log.out.error("SendInvitations.send() MessagingException "+mse.getMessage(), mse);
		}
		
	}
}
