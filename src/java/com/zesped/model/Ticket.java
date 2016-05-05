package com.zesped.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.NotYetConnectedException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsDocumentModificationException;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.doc.volumes.Volume;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Ticket extends BaseBillableObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("taxpayer",DataType.STRING,false,false,null),
    	new Attr("biller_taxpayer",DataType.STRING,false,false,null),
    	new Attr("employee_uuid",DataType.STRING,false,false,new ForeignKey(Employee.class, "employee_uuid")),
    	new Attr("creation_date",DataType.DATE_TIME,false,true,null),
    	new Attr("process_date",DataType.DATE,false,false,null),
    	new Attr("processed_by",DataType.STRING,false,false,new ForeignKey(User.class,"user_uuid")),
    	new Attr("ticket_number",DataType.STRING,false,false,null),
    	new Attr("ticket_date",DataType.DATE,false,false,null),
    	new Attr("year",DataType.NUMBER,false,false,null),
    	new Attr("month",DataType.NUMBER,false,false,null),
    	new Attr("base_amount",DataType.STRING,false,false,null),
    	new Attr("vat",DataType.STRING,false,false,null),
    	new Attr("vatpct",DataType.STRING,false,false,null),
    	new Attr("final_amount",DataType.STRING,false,false,null),
    	new Attr("currency",DataType.STRING,false,false,null),
    	new Attr("payment_mean",DataType.STRING,false,false,null),
    	new Attr("concept",DataType.STRING,false,false,null),
    	new Attr("is_open",DataType.STRING,false,true,null),    	
    	new Attr("accounting_uuid",DataType.STRING,false,true,new ForeignKey(AccountingAccount.class, "account_uuid")),
    	new Attr("accounting_code",DataType.STRING,false,true,null),
    	new Attr("accounting_desc",DataType.STRING,false,true,null),
    	new Attr("is_processed",DataType.STRING,false,false,null),
    	new Attr("has_mistakes",DataType.STRING,false,false,null),
    	new Attr("comments",DataType.STRING,false,false,null)
    };
	
	private String sBillNoteId;

	public Ticket() {
		super("Ticket");
	}

	public Ticket(Document d) {
		super(d);
		sBillNoteId = d.parents().get(0).id();
	}

	public Ticket(Dms oDms, String sDocId) throws ElementNotFoundException {
		super(oDms, sDocId);
		sBillNoteId = getDocument().parents().get(0).id();
	}
	
	public Ticket(AtrilSession oSes, BillNote oParent) {
		super("Ticket");
		newDocument(oSes, oParent.getDocument());
		sBillNoteId = oParent.id();
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getAccountingAccountCode() {
		return getStringNull("accounting_code", "");
	}

	public void setAccountingAccountCode(String code) {
		put("accounting_code", code);
	}

	public String getAccountingAccountDescription() {
		return getStringNull("accounting_desc", "");
	}

	public void setAccountingAccountDescription(String desc) {
		put("accounting_desc", desc);
	}
	
	public String getAccountingAccountUuid() {
		return getStringNull("accounting_uuid", "");
	}

	public void setAccountingAccountUuid(String uuid) {
		put("accounting_uuid", uuid);
	}

	public String getConcept() {
		if (isNull("concept"))
			return null;
		else
			return getString("concept");
	}

	public void setConcept(String c) {
		put("concept",c);
	}

	public String getComments() {
		if (isNull("comments"))
			return null;
		else
			return getString("comments");
	}

	public void setComments(String c) {
		put("comments",c);
	}
	
	public Date getTicketDate() {
		if (isNull("ticket_date"))
			return null;
		else
			return getDate("ticket_date");
	}

	public void setTicketDate(Date ticket_date) {
		if (null==ticket_date)
			remove("ticket_date");
		else {
			put("ticket_date", ticket_date);
			put("year", ticket_date.getYear()+1900);
			put("month", ticket_date.getMonth()+1);
		}
	}

	public String getTicketNumber() {
		if (isNull("ticket_number"))
			return null;
		else
			return getString("ticket_number");
	}

	public void setTicketNumber(String ticket_number) {
		if (null==ticket_number)
			remove("ticket_number");
		else
			put("ticket_number", ticket_number);
	}
	
	@Override
	public int getYear() throws NullPointerException {
		if (isNull("year"))
			if (isNull("ticket_date"))
				throw new NullPointerException("Cannot get year because ticket date is null");
			else
				return getDate("invoice_date").getYear()+1900;
		else
			return super.getYear();
	}

	@Override
	public int getMonth() throws NullPointerException {
		if (isNull("month"))
			if (isNull("ticket_date"))
				throw new NullPointerException("Cannot get month because ticket date is null");
			else
				return getDate("invoice_date").getMonth()+1;
		else
			return super.getMonth();
	}

	public int getDay() throws NullPointerException {
		if (isNull("ticket_date"))
			throw new NullPointerException("Cannot get day because ticket date is null");
		else
			return getDate("ticket_date").getDate();
	}

	public boolean isOpen() {
		return getStringNull("is_open").equals("1");
	}

	public void isOpen(boolean b) {
		put ("is_open", b ? "1" : "0");
	}

	@Override
	public String parentId() throws IllegalStateException, ElementNotFoundException {
		return sBillNoteId;
	}
	
	public TicketThumbnail thumbnail(AtrilSession oSes) {
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("TicketThumbnail")) {
				return new TicketThumbnail(oSes.getDms().getDocument(d.id()));
			}
		}
		return null;		
	}
	
	private class NoteNumComparator implements Comparator<TicketNote> {
		public int compare(TicketNote p1, TicketNote p2) {
			return p1.number()-p2.number();
		}
	}

	private static NoteNumComparator oNtnCmp = new Ticket().new NoteNumComparator();
	
	public ArrayList<TicketNote> pages(AtrilSession oSes) {
		ArrayList<TicketNote> aPages = new ArrayList<TicketNote>();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Side")) {
				aPages.add(new TicketNote(oSes.getDms().getDocument(d.id())));
			}
		}
		Collections.sort(aPages, oNtnCmp);
		return aPages;
	}

	public int pageCount() {
		int nCount = 0;
		for (Document d : getDocument().children())
			if (d.type().name().equals("Side")) nCount++;
		return nCount;
	}
	
	public AccountingAccount account(AtrilSession oSes) throws ElementNotFoundException,NullPointerException {
		if (isNull("accounting_uuid"))
			throw new NullPointerException("Ticket accounting_uuid is null");
		else
			return new AccountingAccount(oSes, getString("accounting_uuid"));
	}
	
	public TaxPayer taxPayer(Dms oDms) {
		return new TaxPayer(oDms, getString("taxpayer"));
	}

	public BaseCompanyObject biller(AtrilSession oSes) {
		if (isNull("biller_taxpayer"))
			return null;
		else			
			return new Client(oSes.getDms().getDocument(getString("biller_taxpayer")));
	}
	
	public BillNote billNote(AtrilSession oSes) {
		return new BillNote(oSes.getDms().getDocument(parentId()));
	}

	public Employee employee(AtrilSession oSes) {
		Employee oEmp = null;
		if (getStringNull("employee_uuid","").length()>=0) {
			oEmp = new Employee();
			Document d = oEmp.exists(oSes, "employee_uuid", getStringNull("employee_uuid"));
			if (d!=null)
				oEmp.setDocument(d);
			else
				oEmp = null;
		}
		return oEmp;
	}
	
	private void updateCounters(AtrilSession oSes, int iAction)
		throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		TaxPayer oTxp = taxPayer(oSes.getDms());
		if (!isProcessed() && isOpen() && ACTION_PROCESS==iAction) {
			oTxp.incPendingTicketsCount(oSes, -1l);
			oTxp.incProcessedTicketsCount(oSes, 1l);
		} else if (!isProcessed() && isOpen() && ACTION_APPROVE==iAction) {
			oTxp.incPendingTicketsCount(oSes, -1l);
			oTxp.incSettledTicketsCount(oSes, 1l);
		} else if (isProcessed() && isOpen() && ACTION_APPROVE==iAction) {
			oTxp.incProcessedTicketsCount(oSes, -1l);
			oTxp.incSettledTicketsCount(oSes, 1l);
		} else if (isProcessed() && isOpen() && ACTION_REJECT==iAction) {
			oTxp.incProcessedTicketsCount(oSes, -1l);
			oTxp.incPendingTicketsCount(oSes, 1l);
		} else if (!isOpen() && ACTION_REJECT==iAction) {
			oTxp.incSettledTicketsCount(oSes, -1l);
			oTxp.incPendingTicketsCount(oSes, 1l);
		} else if (!isProcessed() && ACTION_DELETE==iAction) {
			oTxp.incPendingTicketsCount(oSes, -1l);
			oTxp.incTotalTicketsCount(oSes, -1l);
		} else if (isProcessed() && isOpen() && ACTION_DELETE==iAction) {
			oTxp.incProcessedTicketsCount(oSes, -1l);
			oTxp.incTotalTicketsCount(oSes, -1l);
		} else if (!isOpen() && ACTION_DELETE==iAction) {
			oTxp.incSettledTicketsCount(oSes, -1l);
			oTxp.incTotalTicketsCount(oSes, -1l);
		}
	}
	
	public void changeBillNote(AtrilSession oSes, String sNewBillNoteId) throws ElementNotFoundException, DmsException, NotYetConnectedException {
		Dms oDms = oSes.getDms();
		BillNote oBln = new BillNote(oDms.getDocument(sNewBillNoteId));
		getDocument().parents().replace(oDms.getDocument(parentId()), oBln.getDocument());
		if (oBln.isNull("employee_uuid"))
			put("employee_uuid","");
		else
			put("employee_uuid",oBln.getEmployeeId());			
	}

	public TicketNote createNote(AtrilSession oSes, InputStream oIns, int nNoteNum, String sFileName)
		throws DmsException, NotYetConnectedException, ElementNotFoundException, ClassCastException,
		IllegalStateException, NullPointerException, RuntimeException, IOException {

		TicketNote oNte = new TicketNote(oSes, this, nNoteNum);
		oNte.save(oSes);
		oNte.insertContentFromInputStream(oSes, oIns, sFileName);
		oNte.save(oSes);
		DAO.log(oSes, oNte.getDocument(), TicketNote.class, "CREATE TICKET NOTE", AtrilEvent.Level.INFO, oNte.getDocument().id()+";"+id());		
		oSes.commit();
		customerAccount(oSes.getDms()).grant(oNte);
		return oNte;
	}

	@Override
	public void load(AtrilSession oSes, String sDocId) throws ElementNotFoundException, NotEnoughRightsException , DmsException {
		super.load(oSes, sDocId);
		sBillNoteId = getDocument().parents().get(0).id();
	}
	
	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException, DmsDocumentModificationException{

		if (!containsKey("creation_date")) put("creation_date", new Date());
		if (!containsKey("is_open")) put("is_open", "1");
		if (!containsKey("is_processed")) put("is_processed", "0");
		if (!containsKey("has_mistakes")) put("has_mistakes", "0");

		BillNote oBln = billNote(oSes);
		if (oBln.isNull("employee_uuid"))
			put ("employee_uuid", "");
		else
			put ("employee_uuid", oBln.getEmployeeUuid());
		
		if (getStringNull("base_amount","").length()>0 || getStringNull("final_amount","").length()>0)
			oBln.updateSettling(oSes);
		
		super.save(oSes);

		if (getVolume(oSes)==null) {
			Log.out.debug("Getting default volume");
			Volume oVol = DAO.defaultVolume(oSes.getDms().getVolumeManager());
			if (oVol==null) throw new DmsException("No default volume is set");
			oVol.addDocument(getDocument());
			oVol.save();
			Log.out.debug("Added Ticket "+getDocument().id()+" to Volume "+oVol.name());
		}
	}

	public void save(AtrilSession oSes, String sUserUuid, boolean bProcess, boolean bHasMistakes)
			throws ClassCastException, RuntimeException, IllegalStateException,
			NullPointerException, NotYetConnectedException, DmsException, DmsDocumentModificationException {

			try {
				if (bProcess)
					updateCounters(oSes, ACTION_PROCESS);
				else if (bHasMistakes)
					updateCounters(oSes, ACTION_REJECT);
			} catch (Exception e) {
				Log.out.error(e.getClass()+" "+e.getMessage(),e);
			}
		
			put("is_processed", bProcess ? "1" : "0");
			put("has_mistakes", bHasMistakes ? "1" : "0");

			if (bProcess) {
				put("process_date",new Date());
				put("processed_by",sUserUuid);
			}
			save(oSes);			
	}

	public void reopen(AtrilSession oSes, String sUserUuid)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException, DmsDocumentModificationException {

		BillNote blnt = billNote(oSes);
		blnt.unsettle(oSes, sUserUuid);
		try {
			updateCounters(oSes, ACTION_REJECT);
		} catch (Exception e) {
			Log.out.error(e.getClass()+" "+e.getMessage(),e);
		}
		isOpen(true);
		save(oSes, sUserUuid, false, false);			
		DAO.log(oSes, getDocument(), Ticket.class, "REOPEN TICKET", AtrilEvent.Level.INFO, getDocument().id()+";"+this.getTaxPayer());
	}

	@Override
	protected void delete(AtrilSession oSes, Dms oDms)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException,
		IllegalStateException, DmsException  {
		try {
			updateCounters(oSes, ACTION_DELETE);
		} catch(Exception e) {
			Log.out.error(e.getClass()+" "+e.getMessage(), e);
		}
		super.delete(oSes, oDms);
		CaptureServiceFlavor oFlv = CaptureServiceFlavors.getCaptureServiceFlavor("BILLNOTESBASIC");
		TaxPayer oTxp = new TaxPayer(oDms, getTaxPayer());
		CustomerAccount oAcc = oTxp.customerAccount(oDms);
		oAcc.restoreCredits(oSes, oFlv.credits());
		oAcc.save(oSes);
		DAO.log(oSes, oAcc.getDocument(), CustomerAccount.class, "RESTORE CREDITS", AtrilEvent.Level.INFO, oFlv.credits().toString()+";"+oFlv.uid());
	}

	public static void delete(AtrilSession oSes, String sDocId)
		throws DmsException, ElementNotFoundException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, ClassCastException, IllegalStateException {
		Dms oDms = oSes.getDms();
		new Ticket(oDms, sDocId).delete(oSes, oDms);
	}	
	
}
