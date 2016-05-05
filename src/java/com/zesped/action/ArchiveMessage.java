package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;

import com.zesped.Log;
import com.zesped.model.Message;

public class ArchiveMessage extends BaseAjaxBean {

	@DefaultHandler
	public Resolution archive() {
		final String sId = getParam("id");
		if (getErrorsCount()==0) {
			try {
				connect();
				Message oMsg = new Message(getSession().getDms(), sId);
				oMsg.archive(getSession());
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
