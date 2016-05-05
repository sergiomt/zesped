package com.zesped.action;

import javax.servlet.http.HttpServletRequest;

import com.zesped.model.CaptureService;
import com.zesped.model.Role;
import com.zesped.util.AsyncAccountCache;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class MyAccount extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/myaccount.jsp";

	public CaptureService getCaptureService() {
		return CaptureService.NONE;
	}
	
	public Role getRole() {
		return (Role) getContext().getRequest().getSession().getAttribute("role");
	}
	
	@DefaultHandler
	public Resolution form() {
		HttpServletRequest oReq = getContext().getRequest();
		String sUrl = oReq.getRequestURL().toString();
		sUrl = sUrl.substring(0,sUrl.indexOf("/zesped/")+8);
		new AsyncAccountCache(getSessionAttribute("nickname"),
							  getSessionAttribute("password"),
							  getSessionAttribute("customer_account_docid"), sUrl).start();
	  return new ForwardResolution(FORM);
	}
	
}