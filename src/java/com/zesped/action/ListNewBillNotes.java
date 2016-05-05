package com.zesped.action;

import com.zesped.model.CaptureService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class ListNewBillNotes extends BaseListBean {

	private static final String FORM="/WEB-INF/jsp/listnewbillnotes.jsp";

	public CaptureService getCaptureService() {
		return CaptureService.BILLNOTES;
	}
	
	@DefaultHandler
	public Resolution form() {
	  return new ForwardResolution(FORM);
	}	
}
