package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.BillNote;
import com.zesped.model.CaptureService;
import com.zesped.model.QueryResultSet;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.doc.user.Dms;

public class QueryBillNotes extends BaseActionBean {

	private static final String FORM = "/WEB-INF/jsp/billnotes_";
	
	private QueryResultSet<BillNote> aLst;
	private String sFmt;
	private int iMaxRows, iOffset;
	
	public QueryBillNotes() {
		iMaxRows = 0;
		iOffset = 0;
	}

	@Override
	public CaptureService getCaptureService() {
		return CaptureService.BILLNOTES;
	}

	public String getOutputFormat() {
		return sFmt;
	}

	public int getMaxRows() {
		return iMaxRows;
	}

	public int getOffset() {
		return iOffset;
	}
	
	public void setOutputFormat(String sOutFmt) {
		sFmt = sOutFmt;
	}
	
	public QueryResultSet<BillNote> getResultSet() {
		return aLst;
	}
	
	@DefaultHandler
	public Resolution query() {
		final String sNickN = getSessionAttribute("nickname");
		final String sPassw = getSessionAttribute("password");
		
		try {
			iMaxRows = Integer.parseInt(getParam("maxrows","0"));
		} catch (NumberFormatException nfe) { }
		try {
			iOffset = Integer.parseInt(getParam("offset","0"));
		} catch (NumberFormatException nfe) { }
		  
		final String sTaxPayer = getParam("taxpayer", getSessionAttribute("taxpayer_docid"));
		final String sEmployee = getParam("employee","");
		final String sConcept = getParam("concept","");
		final String sOpen = getParam("isopen","");
		final String sSearchStr = getParam("searchstr","").trim();
		
		setOutputFormat(getParam("format","csv"));

		if (iMaxRows>0) {
			Boolean bOpen = (sOpen.length()==0 ? null : sOpen.equals("1") ? Boolean.TRUE : Boolean.FALSE);			
			try {
				connect(sNickN, sPassw);
				Dms oDms = getSession().getDms();
				aLst = new TaxPayer(oDms, sTaxPayer).queryBillNotes(getSession(), bOpen, sEmployee, sConcept, sSearchStr, iMaxRows, iOffset);				
				disconnect();
			} catch (Exception xcpt) {
				Log.out.error("QueryTickets "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());
			} finally {
				close();
			}		
		} // fi
		
		return new ForwardResolution(FORM+getOutputFormat()+".jsp");
	}		
	
}
