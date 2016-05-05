package com.zesped.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.BaseCompanyObject;
import com.zesped.model.CaptureService;
import com.zesped.model.Invoice;
import com.zesped.model.QueryResultSet;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class QueryInvoices extends BaseActionBean {

	private static final String FORM = "/WEB-INF/jsp/invoices_";
	
	private QueryResultSet<Invoice> aLst;
	private HashMap<String,BaseCompanyObject> oCompanies;
	private String sFmt;
	private int iMaxRows, iOffset;

	public QueryInvoices() {
		iMaxRows = 0;
		iOffset = 0;
		oCompanies = new HashMap<String,BaseCompanyObject>();
	}
	
	@Override
	public CaptureService getCaptureService() {
		return CaptureService.INVOICES;
	}

	public String getOutputFormat() {
		return sFmt;
	}

	public void setOutputFormat(String sOutFmt) {
		sFmt = sOutFmt;
	}

	public int getMaxRows() {
		return iMaxRows;
	}

	public int getOffset() {
		return iOffset;
	}
	
	public QueryResultSet<Invoice> getResultSet() {
		return aLst;
	}

	public HashMap<String,BaseCompanyObject> getCompanyMap() {
		return oCompanies;
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
		  
		final String sDocId = getParam("docid","");
		final String sTaxPayer = getParam("taxpayer", getSessionAttribute("taxpayer_docid"));
		final String sRecipient = getParam("recipient",sTaxPayer);
		final String sBiller = getParam("biller","");
		final String sStatus = getParam("status","");
		final String sDayStart = getParam("dayStart","");
		final String sDayEnd = getParam("dayEnd","");
		final String sMonthStart = getParam("monthStart","");
		final String sMonthEnd = getParam("monthEnd","");
		final String sYearStart = getParam("yearStart","");
		final String sYearEnd = getParam("yearEnd","");
		final String sSearchStr = getParam("searchstr","").trim();
		final String sAmountFrom = getParam("amountfrom","");
		final String sAmountTo = getParam("amountto","");
		final String sServiceFlavor = getParam("serviceflavor","");
		
		Date dtFrom = null;
		Date dtTo = null;
		
		if (sDayStart.length()>0 && sMonthStart.length()>0 && sYearStart.length()>0)
			dtFrom = new Date(Integer.parseInt(sYearStart)-1900, Integer.parseInt(sMonthStart)-1,Integer.parseInt(sDayStart),0,0,0);
		if (sDayEnd.length()>0 && sMonthEnd.length()>0 && sYearEnd.length()>0)
			dtTo = new Date(Integer.parseInt(sYearEnd)-1900, Integer.parseInt(sMonthEnd)-1,Integer.parseInt(sDayEnd),23,59,59);
		
		setOutputFormat(getParam("format","csv"));

		if (iMaxRows>0) {
			
			Boolean bProcessed=null, bApproved=null, bHasMistakes=null;
			if (sStatus.length()>0) {
				  if (sStatus.equals("pending")) {
					  bProcessed = new Boolean(false);
				  } else if (sStatus.equals("processed")) {
					  bProcessed = new Boolean(true);
					  bApproved = new Boolean(false);
				  } else if (sStatus.equals("approved")) {
					  bApproved = new Boolean(true);
				  } else if (sStatus.equals("hasmistakes")) {
					  bHasMistakes = new Boolean(true);
				  }
			}
			
			Integer[] aMonthsRange=null;
			if (sMonthStart.length()>0 && sMonthEnd.length()>0)
				aMonthsRange = new Integer[]{new Integer(sMonthStart), new Integer(sMonthEnd)};

			Integer[] aYearsRange=null;
			if (sYearStart.length()>0 && sYearEnd.length()>0)
				aYearsRange = new Integer[]{new Integer(sYearStart), new Integer(sYearEnd)};
			
		  BigDecimal[] aAmountRange = null;
		  if (sAmountFrom.length()==0 && sAmountTo.length()==0)
			  aAmountRange = null;
		  else if (sAmountFrom.length()>0 && sAmountTo.length()>0)
			  aAmountRange = new BigDecimal[] {new BigDecimal(sAmountFrom),new BigDecimal(sAmountTo)};
		  else if (sAmountFrom.length()>0)
			  aAmountRange = new BigDecimal[] {new BigDecimal(sAmountFrom),null};
		  else
			  aAmountRange = new BigDecimal[]{null,new BigDecimal(sAmountTo)};
		   
			try {
				connect(sNickN, sPassw);
				AtrilSession oSes = getSession();
				Dms oDms = oSes.getDms();
				if (sDocId.length()>0) {
					aLst = new QueryResultSet<Invoice>();
					aLst.add(new Invoice(oDms, sDocId));
				} else {
				  TaxPayer oTxpr = new TaxPayer(oDms, sTaxPayer);
				  if (dtFrom!=null || dtTo!=null)
					  aLst = oTxpr.queryInvoices(oSes, bProcessed, bApproved, bHasMistakes, dtFrom, dtTo, sRecipient, sBiller, sServiceFlavor, iMaxRows, iOffset);
				  else if (aAmountRange!=null || aMonthsRange!=null || aYearsRange!=null || sSearchStr.length()>0)
					  aLst = oTxpr.queryInvoices(oSes, bProcessed, bApproved, bHasMistakes, aAmountRange, aMonthsRange, aYearsRange, sRecipient, sBiller, sServiceFlavor, sSearchStr, iMaxRows, iOffset);
				  else
					  aLst = oTxpr.queryInvoices(oSes, bProcessed, bApproved, bHasMistakes, dtFrom, dtTo, sRecipient, sBiller, sServiceFlavor, iMaxRows, iOffset);
				}
				for (Invoice i : aLst) {
				  try {
					if (!i.isNull("biller_taxpayer")) {
						if (!oCompanies.containsKey(i.getString("biller_taxpayer"))) {
							oCompanies.put(i.getBillerTaxPayer(), i.biller(oSes));
						}				
					}
					if (!i.isNull("recipient_taxpayer")) {
						if (!oCompanies.containsKey(i.getString("recipient_taxpayer"))) {
							oCompanies.put(i.getRecipientTaxPayer(), i.taxPayer(oDms));
						}				
					}
				  } catch (IllegalStateException ise) {
					Log.out.error("QueryInvoices IllegalStateException "+ise.getMessage()+" document "+i.id());
				  }
				} // next
				disconnect();
			} catch (Exception xcpt) {
				Log.out.error("QueryInvoices "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());
			} finally {
				close();
			}		
		} else {
			aLst = new QueryResultSet<Invoice>();
		}
		return new ForwardResolution(FORM+getOutputFormat()+".jsp");
	}		
	
}
