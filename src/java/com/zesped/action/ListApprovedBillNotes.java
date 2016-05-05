package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.model.CaptureService;

public class ListApprovedBillNotes extends BaseListBean {

	private static final String FORM="/WEB-INF/jsp/listapprovedbillnotes.jsp";

	public CaptureService getCaptureService() {
		return CaptureService.BILLNOTES;
	}
	
	@DefaultHandler
	public Resolution form() {
	  return new ForwardResolution(FORM);
	}	
}
