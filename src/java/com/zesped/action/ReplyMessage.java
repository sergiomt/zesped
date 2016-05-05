package com.zesped.action;

import java.util.ArrayList;

import com.zesped.Log;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Message;
import com.zesped.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class ReplyMessage extends ViewMessage {

	private static final String FORM="/WEB-INF/jsp/replymessage.jsp";

	private ArrayList<User> recipients;

	private String id, action;

	public String getAction() {
		return action;
	}
	
	public ArrayList<User> getRecipients() {
		return recipients;
	}

	@Override
	public String getBody() {
		if (getAction().equals("forward"))
			return "\n\n-------- Mensaje original --------\nAsunto: "+getSubject()+"\nFecha: "+getMessage().getDate()+"\nDe: "+getSender()+"\nPara: "+getRecipient()+"\n\n"+getMessage().getBody();
		else
			return "\n\n----------------------------------\n\n"+getMessage().getBody();
	}

	@Override
	public String getRecipient() {
		if (getMessage().getRecipientEmail().equals(getMessage().getRecipientDisplayName()))
			return "<"+getMessage().getRecipientEmail()+">";
		else
			return getMessage().getRecipientDisplayName()+" <"+getMessage().getRecipientEmail()+">";
	}
	
	@Override
	public String getSender() {
		return getSessionAttribute("fullname");
	}

	@Override
	public String getSubject() {
		if (getAction().equals("forward"))
			return "Fwd: "+getMessage().getSubject();
		else
			return "Re: " +getMessage().getSubject();
	}

	@DefaultHandler
	public Resolution form() {
		id = getParam("id");
		action = getParam("a","r").equals("f") ? "forward" : "reply";
		try {
			connect();
			setMessage(new Message(getSession().getDms(), id));
			CustomerAccount cac = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
			recipients = cac.getUsers(getSession());
			disconnect();
	    } catch (Exception xcpt) {
	    	Log.out.error(xcpt.getMessage(), xcpt);
	    } finally {
	    	close();
	    }
		return new ForwardResolution(FORM);
	}
}
