package com.zesped.action;

import java.math.BigDecimal;
import java.util.HashMap;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.BaseCompanyObject;
import com.zesped.model.BillNote;
import com.zesped.model.CaptureService;
import com.zesped.model.Employee;
import com.zesped.model.QueryResultSet;
import com.zesped.model.TaxPayer;
import com.zesped.model.Ticket;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.documentindexer.DocumentIndexer;

public class QueryTickets extends BaseActionBean {

	private static final String FORM = "/WEB-INF/jsp/tickets_";
	
	private QueryResultSet<Ticket> aLst;
	private HashMap<String,Employee> oEmployees;
	private HashMap<String,BaseCompanyObject> oCompanies;
	private HashMap<String,BillNote> oBillNotes;
	private String sFmt;
	private int iMaxRows, iOffset;
	
	public QueryTickets() {
		iMaxRows = 0;
		iOffset = 0;
		oEmployees = new HashMap<String,Employee>();
		oCompanies = new HashMap<String,BaseCompanyObject>();
		oBillNotes = new HashMap<String,BillNote>();
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
	
	public QueryResultSet<Ticket> getResultSet() {
		return aLst;
	}

	public HashMap<String,Employee> getEmployeeMap() {
		return oEmployees;
	}

	public HashMap<String,BaseCompanyObject> getCompanyMap() {
		return oCompanies;
	}

	public HashMap<String,BillNote> getBillNoteMap() {
		return oBillNotes;
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
		final String sBillNote = getParam("billnote","");
		final String sEmployee = getParam("employee","");
		final String sConcept = getParam("concept","");
		final String sOpen = getParam("isopen","");
		final String sStatus = getParam("status","");
		final String sBiller = getParam("biller","");
		  
		final String sMonthStart = getParam("monthStart","");
		final String sMonthEnd = getParam("monthEnd","");
		final String sYearStart = getParam("yearStart","");
		final String sYearEnd = getParam("yearEnd","");
		final String sSearchStr = getParam("searchstr","").trim();
		final String sAmountFrom = getParam("amountfrom","");
		final String sAmountTo = getParam("amountto","");
		
		setOutputFormat(getParam("format","csv"));

		if (iMaxRows>0) {
			
			Boolean bProcessed=null;
			Boolean bHasMistakes=null;
			if (sStatus.length()>0) {
				  if (sStatus.equals("pending"))
					  bProcessed = Boolean.FALSE;
				  else if (sStatus.equals("processed"))
					  bProcessed = Boolean.TRUE;
				  else if (sStatus.equals("hasmistakes"))
					  bHasMistakes = Boolean.TRUE;
			}

			Boolean bOnlyOpen = (sOpen.length()==0 ? null : sOpen.equals("1") ? Boolean.TRUE : Boolean.FALSE);
			
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
				Dms oDms = getSession().getDms();
				DocumentIndexer oIdx = getSession().getDocumentIndexer();
				oIdx.setMaximumNumberOfDocumentReturned(iMaxRows);

				if (sBillNote.length()>0) {
					aLst = new QueryResultSet<Ticket>();
					aLst.addAll(new BillNote(oDms, sBillNote).tickets(getSession()));
				} else if (sEmployee.length()>0) {
					Employee oEmpl = new TaxPayer(oDms, sTaxPayer).employees(getSession()).seek(getSession(), sEmployee);
				  if (null!=oEmpl)	
						aLst = oEmpl.queryTickets(getSession(), bProcessed, bHasMistakes, bOnlyOpen, aAmountRange, aMonthsRange, aYearsRange, sBiller, sSearchStr, iMaxRows, iOffset);
				  else
					  aLst = new QueryResultSet<Ticket>();
				} else {			
					aLst = new TaxPayer(oDms, sTaxPayer).queryTickets(getSession(), bProcessed, bHasMistakes, bOnlyOpen, aAmountRange, aMonthsRange, aYearsRange, sBiller, sConcept, sSearchStr, iMaxRows, iOffset);
				}
				for (Ticket i : aLst) {
					if (!i.isNull("biller_taxpayer")) {
						if (!oCompanies.containsKey(i.getString("biller_taxpayer")))
							oCompanies.put(i.getBillerTaxPayer(), i.biller(getSession()));
					}
					if (!i.isNull("taxpayer")) {
						if (!oCompanies.containsKey(i.getString("taxpayer")))
							oCompanies.put(i.getTaxPayer(), i.taxPayer(oDms));
					}
					if (i.getStringNull("employee_uuid","").length()>0) {
						if (!oEmployees.containsKey(i.getString("employee_uuid")))
							oEmployees.put(i.getString("employee_uuid"), i.employee(getSession()));
					}
					if (!oBillNotes.containsKey(i.parentId())) {
						oBillNotes.put(i.parentId(), new BillNote(oDms,i.parentId()));
					}					
				}
				disconnect();
			} catch (Exception xcpt) {
				Log.out.error("QueryTickets "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());
			} finally {
				close();
			}		
		} else {
			aLst = new QueryResultSet<Ticket>();			
		}
		
		return new ForwardResolution(FORM+getOutputFormat()+".jsp");
	}		
}
