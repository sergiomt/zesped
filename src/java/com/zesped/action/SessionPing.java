package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.ajax.JavaScriptResolution;

import com.zesped.model.CaptureService;

public class SessionPing extends BaseActionBean {

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@DefaultHandler
	public Resolution ping() throws Exception {
		final String sNickName = getSessionAttribute("nickname");
		final String sPassword = getSessionAttribute("password");
		if (sNickName==null || sPassword==null) {
			return new JavaScriptResolution("expiredsession");
		} else {
			try {
				connect (sNickName, sPassword);
				disconnect();
				return new JavaScriptResolution("");
			} catch (Exception xcpt) {
				return new JavaScriptResolution("passwordmismatch");
			} finally {
				close();
			}
		} // fi
	}
	
}
