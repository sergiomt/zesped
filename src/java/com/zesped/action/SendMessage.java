package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;

import com.zesped.Log;
import com.zesped.model.Message;
import com.zesped.model.Messages;
import com.zesped.model.User;

public class SendMessage extends BaseAjaxBean {

	@DefaultHandler
	public Resolution send() {
		final String sId = getParam("id");
		final String sThreadId = getParam("threadId");
		final String sReciptId = getParam("recipient");
		final String sSubject = getParam("subject");
		final String sBody = getParam("body");
		if (getErrorsCount()==0) {
			try {
				connect();
				User oMe = new User(getSession(), getSessionAttribute("user_docid"));
				User oRe = new User(getSession(), User.forUuid(sReciptId));
				Message oMsg = Messages.reply(getSession(), sThreadId,
											  oMe.getFullName(), oMe.getEmail(), oMe.getNickName(),
						       				  oRe.getEmail(), oRe.getNickName(), sSubject, sBody);
				disconnect();
	    		addDataLine("id",oMsg.id());
			} catch (Exception xcpt) {
				Log.out.error(xcpt.getMessage(), xcpt);
				addError(new SimpleError(xcpt.getMessage()));
			} finally {
				close();
			}
		}
	    return AjaxResponseResolution();
	}	
}
