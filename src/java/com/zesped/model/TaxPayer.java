package com.zesped.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.NotYetConnectedException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.DocumentType;
import es.ipsa.atril.doc.user.exceptions.DmsDocumentModificationException;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.documentindexer.exceptions.DocumentIndexerException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class TaxPayer extends BaseCompanyObject implements TypeConverter<TaxPayer> {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("customer_acount",DataType.STRING,false,true,new ForeignKey(CustomerAccount.class, "account_id")),
    	new Attr("creation_date",DataType.DATE_TIME,false,true),
    	new Attr("active",DataType.STRING,false,false),
    	new Attr("customer_code",DataType.STRING,false,false),
    	new Attr("business_name",DataType.STRING,true,true),
    	new Attr("tax_id",DataType.STRING,true,false),
    	new Attr("address1",DataType.STRING,false,false),
    	new Attr("address2",DataType.STRING,false,false),
    	new Attr("city",DataType.STRING,false,false),
    	new Attr("zipcode",DataType.STRING,false,false),
    	new Attr("state",DataType.STRING,false,false),
    	new Attr("country",DataType.STRING,false,false),
    	new Attr("contact_person",DataType.STRING,false,false),
    	new Attr("telephone",DataType.STRING,false,false),
    	new Attr("email",DataType.STRING,false,false,null,BaseCompanyObject.checkEmailSyntaxConstraint()),
    	new Attr("comments",DataType.STRING,false,false)
    	/*
    	new Attr("pending_invoices",DataType.NUMBER,false,false),
    	new Attr("processed_invoices",DataType.NUMBER,false,false),
    	new Attr("approved_invoices",DataType.NUMBER,false,false),
    	new Attr("total_invoices",DataType.NUMBER,false,false),
    	new Attr("pending_invoices",DataType.NUMBER,false,false),
    	new Attr("processed_invoices",DataType.NUMBER,false,false),
    	new Attr("approved_invoices",DataType.NUMBER,false,false),
    	new Attr("total_invoices",DataType.NUMBER,false,false),
    	new Attr("pending_tickets",DataType.NUMBER,false,false),
    	new Attr("processed_tickets",DataType.NUMBER,false,false),
    	new Attr("settled_tickets",DataType.NUMBER,false,false),
    	new Attr("total_tickets",DataType.NUMBER,false,false)
    	*/
    };

	public TaxPayer() {
		super("TaxPayer");
	}

	public TaxPayer(Document d) {
		super(d);
	}
	
	public TaxPayer(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}

	public TaxPayer(AtrilSession oSes, TaxPayers oParent) {
		super("TaxPayer");
		newDocument(oSes, oParent.getDocument());
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}
	
	public String getCustomerAccount() {
		return getString("customer_acount");
	}

	public void setCustomerAccount(String customer_account) throws NullPointerException {
		if (null==customer_account)
			throw new NullPointerException("customer_account cannot be null");
		else
			put("customer_acount", customer_account);
	}

	public String getCustomerCode() {
		if (isNull("customer_code"))
			return null;
		else
			return getString("customer_code");
	}

	public void setCustomerCode(String customer_code)  {
		if (null==customer_code)
			remove("customer_code");
		else
			put("customer_code", customer_code);
	}
	
	public long getPendingInvoicesCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("pending_invoices")).longValue();
	}

	public long getProcessedInvoicesCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("processed_invoices")).longValue();
	}

	public long getApprovedInvoicesCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("approved_invoices")).longValue();
	}
	
	public long getTotalInvoicesCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("total_invoices")).longValue();
	}

	public long getPendingTicketsCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("pending_tickets")).longValue();
	}

	public long getProcessedTicketsCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("processed_tickets")).longValue();
	}

	public long getSettledTicketsCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("settled_tickets")).longValue();
	}

	public long getTotalTicketsCount() throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters) {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
		    oCounters = refreshCounters(oSes);
		    oSes.disconnect();
		    oSes.close();
		}
		return ((BigDecimal) oCounters.get("total_tickets")).longValue();
	}

	public void incPendingInvoicesCount(AtrilSession oSes, long lInc)
		throws DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oPending = (BigDecimal) oCounters.get("pending_invoices");
		oPending = oPending.add(new BigDecimal(lInc));
		oCounters.remove("pending_invoices");
		oCounters.put("pending_invoices", oPending);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}

	public void incProcessedInvoicesCount(AtrilSession oSes, long lInc)
		throws DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oProcessed = (BigDecimal) oCounters.get("processed_invoices");
		oProcessed = oProcessed.add(new BigDecimal(lInc));
		oCounters.remove("processed_invoices");
		oCounters.put("processed_invoices", oProcessed);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}

	public void incApprovedInvoicesCount(AtrilSession oSes, long lInc)
		throws DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oApproved = (BigDecimal) oCounters.get("approved_invoices");
		oApproved = oApproved.add(new BigDecimal(lInc));
		oCounters.remove("approved_invoices");
		oCounters.put("approved_invoices", oApproved);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}

	public void incTotalInvoicesCount(AtrilSession oSes, long lInc)
		throws DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oTotal = (BigDecimal) oCounters.get("total_invoices");
		oTotal = oTotal.add(new BigDecimal(lInc));
		oCounters.remove("total_invoices");
		oCounters.put("total_invoices", oTotal);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}
	
	public void incPendingTicketsCount(AtrilSession oSes, long lInc) throws
		DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oPending = (BigDecimal) oCounters.get("pending_tickets");
		oPending = oPending.add(new BigDecimal(lInc));
		oCounters.remove("pending_tickets");
		oCounters.put("pending_tickets", oPending);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}

	public void incProcessedTicketsCount(AtrilSession oSes, long lInc) throws
	DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oProcessed = (BigDecimal) oCounters.get("processed_tickets");
		oProcessed = oProcessed.add(new BigDecimal(lInc));
		oCounters.remove("processed_tickets");
		oCounters.put("processed_tickets", oProcessed);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}

	public void incSettledTicketsCount(AtrilSession oSes, long lInc) throws
		DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oSettled = (BigDecimal) oCounters.get("settled_tickets");
		oSettled = oSettled.add(new BigDecimal(lInc));
		oCounters.remove("settled_tickets");
		oCounters.put("settled_tickets", oSettled);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}

	public void incTotalTicketsCount(AtrilSession oSes, long lInc) throws
		DmsException, NullPointerException, IllegalStateException, SQLException, IOException, ClassNotFoundException {
		HashMap<String,Object> oCounters = Cache.getEntryMap(id()+"TaxPayerCounters");
		if (null==oCounters)
			oCounters = refreshCounters(oSes);
		BigDecimal oTotal = (BigDecimal) oCounters.get("total_tickets");
		oTotal = oTotal.add(new BigDecimal(lInc));
		oCounters.remove("total_tickets");
		oCounters.put("total_tickets", oTotal);
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);		
	}

	public HashMap<String,Object> refreshCounters(AtrilSession oSes)
		throws NullPointerException, IllegalStateException, SQLException, IOException {
		HashMap<String,Object> oCounters = new HashMap<String,Object>();
		oCounters.put ("pending_invoices", new BigDecimal(pendingInvoices(oSes).size()));		
		oCounters.put ("processed_invoices", new BigDecimal(processedInvoices(oSes).size()));
		oCounters.put ("approved_invoices", new BigDecimal(approvedInvoices(oSes).size()));
		oCounters.put ("total_invoices", new BigDecimal(allInvoices(oSes).size()));
		oCounters.put ("pending_tickets", new BigDecimal(pendingTickets(oSes).size()));		
		oCounters.put ("processed_tickets", new BigDecimal(processedTickets(oSes).size()));
		oCounters.put ("settled_tickets", new BigDecimal(settledTickets(oSes).size()));
		oCounters.put ("total_tickets", new BigDecimal(allTickets(oSes).size()));
		Cache.putEntry(id()+"TaxPayerCounters", oCounters);
		return oCounters;
	}

	public Invoices invoices(AtrilSession oSes) {
		Invoices i = new Invoices();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals(i.getTypeName())) {
				i.setDocument(oSes.getDms().getDocument(d.id()));
				return i;
			}  
		} // next
		return null;
	}

	public String incomingDeposits(AtrilSession oSes) {
		for (Document d : getDocument().children())
			if (d.type().name().equals("IncomingDeposits"))
				return d.id();
		Dms oDms = oSes.getDms();
		Document oDoc = oDms.newDocument(oDms.getDocumentType("IncomingDeposits"), getDocument());
		oDoc.save("");
		oSes.commit();
		return oDoc.id();
	}
	
	public BillNotes billnotes(AtrilSession oSes) {
		BillNotes b = new BillNotes();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals(b.getTypeName())) {
				b.setDocument(oSes.getDms().getDocument(d.id()));
				return b;
			}  
		} // next
		b.newDocument(oSes, getDocument());
		b.save(oSes);
		return b;
	}

	public Employees employees(AtrilSession oSes) {
		Employees e = new Employees();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals(e.getTypeName())) {
				e.setDocument(oSes.getDms().getDocument(d.id()));
				return e;
			}  
		} // next
		e.newDocument(oSes, getDocument());
		e.save(oSes);
		return e;
	}

	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException, DmsDocumentModificationException {

		if (!containsKey("creation_date")) put("creation_date", new Date());
		if (!containsKey("active")) put("active", "1");

		super.save(oSes);
	}

	public CustomerAccount customerAccount(Dms oDms) {
		Document txps = oDms.getDocument(getDocument().parents().get(0).id());
		return new CustomerAccount(oDms, txps.parents().get(0).id());
	}

	public AccountingAccounts accounts(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalStateException, IllegalAccessException {
		AccountingAccounts oAccountingAccounts = null;
		for (Document c : getDocument().children()) {
			 if (c.type().name().equals("AccountingAccounts")) {
				 oAccountingAccounts = new AccountingAccounts(oSes.getDms().getDocument(c.id()));
				 break;
			  }
		} // next
		
		if (null==oAccountingAccounts) {
			Document n = oSes.getDms().newDocument(oSes.getDms().getDocumentType("AccountingAccounts"), getDocument());
			n.save("");
			DocumentIndexer oIdx = oSes.getDocumentIndexer();
			oIdx.indexDocument(n);
			oAccountingAccounts = new AccountingAccounts(n);
			populateAccountingAccountsWithDefaultValues(oSes, oSes.getDms(), oIdx, customerAccount(oSes.getDms()));
		}
		return oAccountingAccounts;
	}

	public AccountingAccount account(AtrilSession oSes, String sAccountId) throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		for (AccountingAccount a : accounts(oSes).list(oSes)) {
			if (a.getUuid().equals(sAccountId) || a.getCode().equals(sAccountId))
				return a;
		}
		throw new ElementNotFoundException("No accounting account with id. or code "+sAccountId+" was found for tax payer "+getDocument().id());
	}

	public SortableList<Document> pendingInvoices(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		SortableList<Document> oDocs = oSes.getDms().query("Invoice & (($taxpayer='"+id()+"') & ($is_processed='0'))");
		if (oDocs.isEmpty())
			Log.out.debug("Dms.query(Invoice & (($taxpayer='"+id()+"') & ($is_processed='0'))) : no invoices found");
		else
			Log.out.debug("Dms.query(Invoice & (($taxpayer='"+id()+"' & $is_processed='0'))) : "+String.valueOf(oDocs.size())+" invoices found");
		return oDocs;
	}

	public SortableList<Document> processedInvoices(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Invoice & (($taxpayer='"+id()+"') & ($is_processed='1') & ($is_approved='0'))");
	}

	public SortableList<Document> approvedInvoices(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Invoice & (($taxpayer='"+id()+"') & ($is_approved='1'))");
	}

	public SortableList<Document> allInvoices(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Invoice$taxpayer='"+id()+"'");
	}

	public SortableList<Document> basicInvoices(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Invoice & (($taxpayer='"+id()+"') & ($service_type='basic'))");
	}

	public SortableList<Document> premiumInvoices(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Invoice & (($taxpayer='"+id()+"') & ($service_type='premium'))");
	}

	public SortableList<Document> pendingTickets(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Ticket & (($taxpayer='"+id()+"') & ($is_processed='0'))");
	}

	public SortableList<Document> processedTickets(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Ticket & (($taxpayer='"+id()+"') & ($is_processed='1'))");
	}

	public SortableList<Document> settledTickets(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Ticket & (($taxpayer='"+id()+"') & ($is_open='0'))");
	}

	public SortableList<Document> allTickets(AtrilSession oSes)
		throws DocumentIndexerException,DmsException,IllegalStateException {
		return oSes.getDms().query("Ticket$taxpayer='"+id()+"'");
	}
	
	public ArrayList<User> allowedUsers(AtrilSession oSes)
		throws DocumentIndexerException {
		ArrayList<User> aUsrs = new  ArrayList<User>();
		List<Document> oAccUsrs = oSes.getDms().query("User$customer_acount='"+getCustomerAccount()+"'");
		if (!oAccUsrs.isEmpty()) {
			for (Document u : oAccUsrs) {
				User oUsr = new User(oSes, u.id());
				if (oUsr.getAllowedTaxPayers().contains(this) || (oUsr.getAllowedTaxPayers().size()==0 && !oUsr.getDeniedTaxPayers().contains(this)))
					aUsrs.add(oUsr);
			}
		}
		return aUsrs;
	}
	
	public QueryResultSet<BillNote> queryBillNotes(AtrilSession oSes, Boolean bOpen, String sEmployee, String sConcept, String sSearchStr, int iMaxRows, int iOffset)
		throws DocumentIndexerException {
		QueryResultSet<BillNote> aLst = new QueryResultSet<BillNote>();
		Dms oDms = oSes.getDms();
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		String sQry = DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "BillNote AND taxpayer:\""+id()+"\"";
		if (bOpen!=null)
			sQry += " AND is_open:\""+(bOpen.booleanValue() ? "1" : "0")+"\"";
		if (sEmployee!=null) {
			if (sEmployee.length()>0) {
				sQry += " AND (employee_uuid:\""+sEmployee+"\" OR employee_id:\""+sEmployee+"*\")";
			}
		}
		if (sConcept!=null) {
			if (sConcept.length()>0) {
				sQry += " AND concept:\""+Gadgets.removeChars(sConcept,"\"\n'")+"\"";
			}
		}
		if (sSearchStr!=null) {
			if (sSearchStr.length()>0) {
				String sSanitizedSearchStr = Gadgets.removeChars(sSearchStr,"\"\n'"); 
				sQry += " AND (concept:\""+sSanitizedSearchStr+"\"* OR employee_name:\""+sSanitizedSearchStr+"\"* OR comments:\""+sSanitizedSearchStr+"\"*)";
			}
		}
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query(sQry);
		if (!oLst.isEmpty()) {
			int o = 0, n = 0;
			for (Document d : oLst) {
				if (iOffset<=o++) {
					try {
						BillNote oBln = new BillNote(oDms, d.id());
						aLst.add(oBln);
						if (++n>=iMaxRows) break;							
					} catch (ElementNotFoundException enfe) {
						Log.out.error("TaxPayer.queryBillNotes() ElementNotFoundException "+enfe.getMessage());	
					}
				}				
			} // next
		} // fi
		aLst.bof(iOffset==0);
		aLst.eof(aLst.size()<iMaxRows);
		return aLst;
	}
	
	public QueryResultSet<Ticket> queryTickets(AtrilSession oSes, Boolean bProcessed, Boolean bHasMistakes,
											   Boolean bOnlyOpen, BigDecimal[] aAmountRange, Integer[] aMonthsRange, Integer[] aYearsRange,
											   String sBiller, String sConcept, String sSearchStr, int iMaxRows, int iOffset)
		throws DocumentIndexerException {
		Log.out.debug("Begin TaxPayer.queryTickets("+bProcessed+","+bHasMistakes+","+bOnlyOpen+","+aAmountRange+","+aMonthsRange+","+aYearsRange+","+sBiller+","+sSearchStr+","+String.valueOf(iMaxRows)+","+String.valueOf(iOffset)+")");
		QueryResultSet<Ticket> aLst = new QueryResultSet<Ticket>();
		Dms oDms = oSes.getDms();
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		if (aAmountRange==null && aMonthsRange==null && aYearsRange==null)
			oIdx.setMaximumNumberOfDocumentReturned(iOffset+iMaxRows);
		String sQry = DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "Ticket AND taxpayer:\""+id()+"\"";
		if (bOnlyOpen!=null)
		  sQry += " AND is_open:\""+(bOnlyOpen.booleanValue() ? "1" : "0")+"\"";
		if (bProcessed!=null)
			  sQry += " AND is_processed:\""+(bProcessed.booleanValue() ? "1" : "0")+"\"";
		if (bHasMistakes!=null)
			  sQry += " AND has_mistakes:\""+(bHasMistakes.booleanValue() ? "1" : "0")+"\"";
		if (sBiller!=null)
			if (sBiller.length()>0)
				sQry += " AND biller_taxpayer:\""+sBiller+"\"";
		if (sConcept!=null) {
			if (sConcept.length()>0) {
				sQry += " AND concept:\""+sConcept +"\"";
			}
		}
		if (sSearchStr!=null) {
			if (sSearchStr.length()>0) {
				String sSanitizedSearchStr = Gadgets.removeChars(sSearchStr, "\"\n");
				sQry += " AND (ticket_number:\""+sSanitizedSearchStr +"\" OR concept:\""+sSanitizedSearchStr +"*\" OR comments:\""+sSanitizedSearchStr +"*\")";
			}
		}
		Log.out.debug("DocumentIndexer.query("+sQry+")");
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query(sQry);
		if (!oLst.isEmpty()) {
			int o = 0, n = 0;
			for (Document d : oLst) {
				if (iOffset<=o++) {
					try {
						Ticket oTck = new Ticket(oDms, d.id());
						if (oTck.isWithinRanges(aAmountRange, aMonthsRange, aYearsRange)) {
							aLst.add(oTck);
							if (++n>=iMaxRows) break;							
						}
					} catch (ElementNotFoundException enfe) {
						Log.out.error("TaxPayer.queryTickets() ElementNotFoundException "+enfe.getMessage());	
					}
				}				
			} // next
		} // fi
		aLst.bof(iOffset==0);
		aLst.eof(aLst.size()<iMaxRows);
		Log.out.debug("End TaxPayer.queryTickets() : "+String.valueOf(aLst.size()));
		return aLst;
	}

	public QueryResultSet<Invoice> queryInvoices(AtrilSession oSes, Boolean bProcessed, Boolean bApproved, Boolean bHasMistakes, BigDecimal[] aAmountRange, Integer[] aMonthsRange, Integer[] aYearsRange, String sRecipient, String sBiller, String sServiceFlavor, String sSearchStr, int iMaxRows, int iOffset)
		throws DocumentIndexerException {
		Log.out.debug("Begin TaxPayer.queryInvoices("+bProcessed+","+bApproved+","+bHasMistakes+","+aAmountRange+","+aMonthsRange+","+aYearsRange+","+sBiller+","+sSearchStr+","+String.valueOf(iMaxRows)+","+String.valueOf(iOffset)+")");
		QueryResultSet<Invoice> aLst = new QueryResultSet<Invoice>();
		Dms oDms = oSes.getDms();
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		if (aAmountRange==null && aMonthsRange==null && aYearsRange==null)
			oIdx.setMaximumNumberOfDocumentReturned(iOffset+iMaxRows);

		String sQry = DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "Invoice AND taxpayer:\""+id()+"\"";
		if (bProcessed!=null)
			  sQry += " AND is_processed:\""+(bProcessed.booleanValue() ? "1" : "0")+"\"";
		if (bApproved!=null)
			  sQry += " AND is_approved:\""+(bApproved.booleanValue() ? "1" : "0")+"\"";
		if (bHasMistakes!=null)
			  sQry += " AND has_mistakes:\""+(bHasMistakes.booleanValue() ? "1" : "0")+"\"";
		if (sRecipient!=null)
			if (sRecipient.length()>0)
				sQry += " AND recipient_taxpayer:\""+sRecipient+"\"";
		if (sBiller!=null)
			if (sBiller.length()>0)
				sQry += " AND biller_taxpayer:\""+sBiller+"\"";
		if (sServiceFlavor!=null) {
			if (sServiceFlavor.length()>0) {
				sQry += " AND service_type:\""+sServiceFlavor+"\"";
			}
		}
		if (sSearchStr!=null) {
			if (sSearchStr.length()>0) {
				String sSanitizedSearchStr = Gadgets.removeChars(sSearchStr, "\"\n");
				sQry += " AND (invoice_number:\""+sSanitizedSearchStr +"\" OR concept:\""+sSanitizedSearchStr +"*\" OR comments:\""+sSanitizedSearchStr +"*\")";
			}
		}
		
		Log.out.debug("DocumentIndexer.query("+sQry+")");
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query(sQry);
		if (!oLst.isEmpty()) {
			int o = 0, n = 0;
			for (Document d : oLst) {
				if (iOffset<=o++) {
				  try {
					Invoice oInv = new Invoice(oDms, d.id());
					if (oInv.isWithinRanges(aAmountRange, aMonthsRange, aYearsRange)) {
						aLst.add(oInv);
						if (++n>=iMaxRows) break;
					}
				  } catch (ElementNotFoundException enfe) {
					  Log.out.error("TaxPayer.queryInvoices() ElementNotFoundException "+enfe.getMessage());
				  }
				}
			} // next
		} // fi
		aLst.bof(iOffset==0);
		aLst.eof(aLst.size()<iMaxRows);
		Log.out.debug("End TaxPayer.queryInvoices() : "+String.valueOf(aLst.size()));
		return aLst;
	}

	public QueryResultSet<Invoice> queryInvoices(AtrilSession oSes, Boolean bProcessed, Boolean bApproved, Boolean bHasMistakes,
			                                     Date dtCreationFrom, Date dtCreationTo, String sRecipient, String sBiller,
			                                     String sServiceFlavor, int iMaxRows, int iOffset)
	    throws DmsException {
		Log.out.debug("Begin TaxPayer.queryInvoices("+bProcessed+","+bApproved+","+bHasMistakes+","+dtCreationFrom+","+dtCreationTo+","+sBiller+","+String.valueOf(iMaxRows)+","+String.valueOf(iOffset)+")");
		SimpleDateFormat oFmt = new SimpleDateFormat("yyyyMMddHHmmss");
		QueryResultSet<Invoice> aLst = new QueryResultSet<Invoice>();
		
		Dms oDms = oSes.getDms();
		
		String sQry = "";
		if (bProcessed!=null)
			sQry += "(Invoice$is_processed='"+(bProcessed.booleanValue() ? "1" : "0")+"')";
		if (bApproved!=null)
			sQry += (sQry.length()==0 ? "" : " & ") + "(Invoice$is_approved='"+(bApproved.booleanValue() ? "1" : "0")+"')";
		if (bHasMistakes!=null)
			sQry += (sQry.length()==0 ? "" : " & ") + "(Invoice$has_mistakes='"+(bHasMistakes.booleanValue() ? "1" : "0")+"')";
		if (sRecipient!=null)
			if (sRecipient.length()>0)
				sQry += (sQry.length()==0 ? "" : " & ") + "(Invoice$recipient_taxpayer='"+sRecipient+"')";
		if (sBiller!=null)
			if (sBiller.length()>0)
				sQry += (sQry.length()==0 ? "" : " & ") + "(Invoice$biller_taxpayer='"+sBiller+"')";
		if (sServiceFlavor!=null)
			if (sServiceFlavor.length()>0)
				sQry += (sQry.length()==0 ? "" : " & ") + "(Invoice$service_type='"+sServiceFlavor+"')";
		if (dtCreationFrom!=null)
			sQry += (sQry.length()==0 ? "" : " & ") + "(Invoice$creation_date>='"+oFmt.format(dtCreationFrom)+"')";
		if (dtCreationTo!=null)
			sQry += (sQry.length()==0 ? "" : " & ") + "(Invoice$creation_date<='"+oFmt.format(dtCreationTo)+"')";
		Log.out.debug("Dms.query("+sQry+")");
		SortableList<Document> oLst = oDms.query(sQry);
		if (!oLst.isEmpty()) {
			int o = 0, n = 0;
			for (Document d : oLst) {
				if (iOffset<=o++) {
					try {
						Invoice oInv = new Invoice(oDms, d.id());
							aLst.add(oInv);
							if (++n>=iMaxRows) break;
					} catch (ElementNotFoundException enfe) {
						  Log.out.error("TaxPayer.queryInvoices() ElementNotFoundException "+enfe.getMessage());
					}
				}
			} // next
		} // fi
		aLst.bof(iOffset==0);
		aLst.eof(aLst.size()<iMaxRows);
		Log.out.debug("End TaxPayer.queryInvoices() : "+String.valueOf(aLst.size()));
		return aLst;
	}
	
	private void populateAccountingAccountsWithDefaultValues(AtrilSession oSes, Dms oDms, DocumentIndexer oIdx, CustomerAccount oAcc)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalStateException, IllegalAccessException {
		Log.out.debug("Begin TaxPayer.populateAccountingAccountsWithDefaultValues(AtrilSession, Dms, DocumentIndexer, "+oAcc.getBusinessName()+")");

		long lEnd, lStart = new Date().getTime();
		
		AccountingAccounts oAccountingAccounts = accounts(oSes);

		Log.out.debug("PROFILING: List AccountingAccounts "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		Collection<AccountingAccountDefault> aDefs = AccountingAccountsDefaults.list();

		Log.out.debug("PROFILING: List Default AccountingAccounts "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		int nAacs = aDefs.size();
		if (nAacs>0) {
			DocumentType oDtp = oDms.getDocumentType("AccountingAccount");
			Document p = oAccountingAccounts.getDocument();
			for (AccountingAccountDefault d : aDefs) {
				try {
					oAccountingAccounts.seek(oSes, d.getString("account_code"));
				} catch (ElementNotFoundException enfe) {
					Document a = oDms.newDocument(oDtp, p);
					a.attribute("account_code").set(d.getString("account_code"));
					a.attribute("account_desc").set(d.getString("account_desc"));
					a.attribute("account_uuid").set(Gadgets.generateUUID());
					a.attribute("is_active").set("1");
					a.save("");
					oIdx.indexDocument(a);					
				}
			}		

			Log.out.debug("PROFILING: Copy Default AccountingAccounts to Customer AccountingAccounts "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
			lStart = lEnd;
			
			oSes.commit();

			for (Document c : getDocument().children()) {
				if (c.type().name().equals("AccountingAccounts")) {
					ArrayList<AccountingAccount> oAacs = new AccountingAccounts(oSes.getDms().getDocument(c.id())).list(oSes);
					oAcc.grant(oSes, oAacs);
					oSes.commit();
					nAacs = oAacs.size();
					break;
				}
			}

			Log.out.debug("PROFILING: Grant permissions on Customer AccountingAccounts "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
			lStart = lEnd;
		}
	    Log.out.debug("End TaxPayer.populateAccountingAccountsWithDefaultValues() : "+String.valueOf(nAacs));
	}

	@Override
	public TaxPayer convert(String sTaxPayerId, Class<? extends TaxPayer> taxPayerClass,
						    Collection<ValidationError> conversionErrors) {
		Log.out.debug("Begin TaxPayer.convert("+sTaxPayerId+")");
		if (sTaxPayerId==null) return null;
		if (sTaxPayerId.length()==0) return null;
		TaxPayer oTxpr = null;
		try {
			AtrilSession oSes = DAO.getAdminSession("TaxPayer");
			oTxpr = new TaxPayer(oSes.getDms(), sTaxPayerId);
			oSes.disconnect();
			oSes.close();
			oSes = null;
			if (null==oTxpr) {
				Log.out.error("No tax payer with id "+sTaxPayerId+" was found");
				conversionErrors.add(new SimpleError("No tax payer with id "+sTaxPayerId+" was found"));
			}
		} catch (Exception exc) {
			Log.out.error(exc.getClass().getName()+" TaxPayer.convert("+sTaxPayerId+") "+exc.getMessage());
			conversionErrors.add(new SimpleError(exc.getMessage()));
		}
		Log.out.debug("End TaxPayer.convert("+sTaxPayerId+") : " + oTxpr);
		return oTxpr;
	}
		
	@Override
	public void setLocale(Locale locale) { }

	public static TaxPayer create(AtrilSession oSes, CustomerAccount oAcc, String sBusinessName, String sTaxId, String sContactPerson, String sEmail, boolean bRequiresActivation, String sVolumesMountBase)
		throws NullPointerException, IllegalArgumentException, ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalStateException, IllegalAccessException,
			   FileNotFoundException, IOException, Exception {
		Dms oDms = oSes.getDms();
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		
		Log.out.debug("Begin TaxPayer.create("+sBusinessName+","+sTaxId+","+sEmail+String.valueOf(bRequiresActivation)+","+sVolumesMountBase+")");
		
		long lEnd, lStart = new Date().getTime();
		
		TaxPayer oPayer = new TaxPayer();
		oPayer.setDocument(oDms.newDocument(oDms.getDocumentType("TaxPayer"), oAcc.taxpayers(oSes).getDocument()));
		oPayer.put("customer_acount", oAcc.getString("account_id"));
		oPayer.put("creation_date", new Date());
		oPayer.put("active", bRequiresActivation ? "-1" : "1");
		oPayer.put("business_name", sBusinessName);
		oPayer.put("tax_id", sTaxId);
		oPayer.put("contact_person", sContactPerson);
		oPayer.put("email", sEmail);
		oPayer.save(oSes);

		oSes.commit();

		Log.out.debug("PROFILING: Save TaxPayer "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		Document oInvoices = oDms.newDocument(oDms.getDocumentType("Invoices"), oPayer.getDocument());
		oInvoices.save("");
		oIdx.indexDocument(oInvoices);

		Document oBillNotes = oDms.newDocument(oDms.getDocumentType("BillNotes"), oPayer.getDocument());
		oBillNotes.save("");
		oIdx.indexDocument(oBillNotes);

		Document oEmployees = oDms.newDocument(oDms.getDocumentType("Employees"), oPayer.getDocument());
		oEmployees.save("");
		oIdx.indexDocument(oEmployees);

		Log.out.debug("PROFILING: Create TaxPayer child documents "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		oAcc.grant(oSes, oPayer.getDocument(), oInvoices, oBillNotes);

		Log.out.debug("PROFILING: Grant access to children "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		oPayer.populateAccountingAccountsWithDefaultValues(oSes, oDms, oIdx, oAcc);

		if (sVolumesMountBase!=null) {
			if (sVolumesMountBase.length()>0) {
				oPayer.setNewVolume(oDms, Gadgets.ASCIIEncode(sBusinessName), Gadgets.chomp(sVolumesMountBase, File.separator), oPayer.id(), "data", 50*MB, TB);
				oSes.commit();
			}
		}
		
		Log.out.debug("End TaxPayer.create() : "+oPayer.id());
		
		return oPayer;
	}
	
	public static TaxPayer create(AtrilSession oSes, CustomerAccount oAcc, String sBusinessName, String sContactPerson, String sEmail, String sVolumesMountBase)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, NullPointerException, IllegalArgumentException, InstantiationException, IllegalStateException, IllegalAccessException, IOException, Exception {
		return TaxPayer.create(oSes, oAcc, sBusinessName, "", sContactPerson, sEmail, false, sVolumesMountBase);
	}

	public static void delete(AtrilSession oSes, String sDocId)
		throws DmsException, ElementNotFoundException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, ClassCastException, IllegalStateException {
		Dms oDms = oSes.getDms();
		Document d = oDms.getDocument(sDocId);
		Log.out.debug("TaxPayer.delete(AtrilSession, "+sDocId+")");
		if (null==d) throw new ElementNotFoundException("Document "+sDocId+" not found");
		List <Document> oAlwed = oDms.query("AllowedTaxPayer$taxpayer='"+sDocId+"'");
		List <Document> oDnied = oDms.query("DeniedTaxPayer$taxpayer='"+sDocId+"'");
		for(Document a : oAlwed)
			BaseModelObject.delete(oSes, a.id());
		for(Document n : oDnied)
			BaseModelObject.delete(oSes, n.id());
		BaseModelObject o = new TaxPayer();
		o.setDocument(d);
		o.delete(oSes, oDms);
	}
	
}
