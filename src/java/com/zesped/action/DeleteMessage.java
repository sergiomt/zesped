package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;

import com.zesped.Log;
import com.zesped.model.Message;

public class DeleteMessage extends BaseAjaxBean {

	@DefaultHandler
	public Resolution archive() {
		final String sId = getParam("id");
		final String sUid = getSessionAttribute("user_uuid");
		if (getErrorsCount()==0) {
			try {
				connect();
				Message oMsg = new Message(getSession().getDms(), sId);
				if (oMsg.getStringNull("sender_id", "").equals(sUid) || oMsg.getStringNull("recipient_id", "").equals(sUid))
					Message.delete(getSession(), sId);
				disconnect();
	    		addDataLine("id",sId);
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
