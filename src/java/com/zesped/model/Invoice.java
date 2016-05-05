package com.zesped.model;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

public class Invoice extends BaseBillableObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("service_type",DataType.STRING,false,true,null),
    	new Attr("taxpayer",DataType.STRING,false,false,null),
    	new Attr("biller_taxpayer",DataType.STRING,false,false,null),
    	new Attr("recipient_taxpayer",DataType.STRING,false,false,null),
    	new Attr("creation_date",DataType.DATE_TIME,false,true,null),
    	new Attr("process_date",DataType.DATE,false,false,null),
    	new Attr("invoice_number",DataType.STRING,false,false,null),
    	new Attr("invoice_date",DataType.DATE,false,false,null),
    	new Attr("year",DataType.NUMBER,false,false,null),
    	new Attr("quarter",DataType.NUMBER,false,false,null),
    	new Attr("month",DataType.NUMBER,false,false,null),
    	new Attr("due_date",DataType.DATE,false,false,null),
    	new Attr("base_amount",DataType.STRING,false,false,null),
    	new Attr("vat",DataType.STRING,false,false,null),
    	new Attr("vatpct",DataType.STRING,false,false,null),
    	new Attr("withholding",DataType.STRING,false,false,null),
    	new Attr("withholdingpct",DataType.STRING,false,false,null),
    	new Attr("final_amount",DataType.STRING,false,false,null),
    	new Attr("currency",DataType.STRING,false,false,null),
    	new Attr("payment_mean",DataType.STRING,false,false,null),
    	new Attr("bank_account",DataType.STRING,false,false,null),
    	new Attr("concept",DataType.STRING,false,false,null),
    	new Attr("is_processed",DataType.STRING,false,true,null),
    	new Attr("is_approved",DataType.STRING,false,true,null),
    	new Attr("has_mistakes",DataType.STRING,false,true,null),
    	new Attr("captured_by",DataType.STRING,false,false,new ForeignKey(User.class,"user_uuid")),
    	new Attr("processed_by",DataType.STRING,false,false,new ForeignKey(User.class,"user_uuid")),
    	new Attr("approved_by",DataType.STRING,false,false,new ForeignKey(User.class,"user_uuid")),
    	new Attr("certificate_info",DataType.STRING,false,false,null),
    	new Attr("comments",DataType.STRING,false,false,null)
	};
	
	public Invoice() {
		super("Invoice");
	}

	public Invoice(Document d) {
		super(d);
	}
	
	public Invoice(Dms oDms, String sDocId) throws ElementNotFoundException {
		super(oDms, sDocId);
	}

	public Invoice(AtrilSession oSes, Invoices oParent) {
		super("Invoice");
		newDocument(oSes, oParent.getDocument());
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getRecipientTaxPayer() {
		if (isNull("recipient_taxpayer"))
			return null;
		else
			return getString("recipient_taxpayer");
	}

	public void setRecipientTaxPayer(String taxPayer) {
		if (null==taxPayer)
			remove("recipient_taxpayer");
		else
			put("recipient_taxpayer", taxPayer);
	}

	public Date getInvoiceDate() {
		if (isNull("invoice_date"))
			return null;
		else
			return getDate("invoice_date");
	}

	public void setInvoiceDate(Date invoice_date) {
		if (null==invoice_date)
			remove("invoice_date");
		else {
			put("invoice_date", invoice_date);
			put("year", invoice_date.getYear()+1900);
			put("month", invoice_date.getMonth()+1);
			int m = getDate("invoice_date").getMonth();
			if (m<3)
				put ("quarter", 1);
			else if (m<6)
				put ("quarter", 2);
			else if (m<9)
				put ("quarter", 3);
			else
				put ("quarter", 4);
			
		}
	}

	public Date getDueDate() {
		if (isNull("due_date"))
			return null;
		else
			return getDate("due_date");
	}

	public void setDueDate(Date due_date) {
		if (null==due_date)
			remove("due_date");
		else
			put("due_date", due_date);
	}
	
	public String getInvoiceNumber() {
		if (isNull("invoice_number"))
			return null;
		else
			return getString("invoice_number");
	}

	public void setInvoiceNumber(String invoice_number) {
		if (null==invoice_number)
			remove("invoice_number");
		else
			put("invoice_number", invoice_number);
	}

	@Override
	public int getYear() throws NullPointerException {
		if (isNull("year"))
			if (isNull("invoice_date"))
				throw new NullPointerException("Cannot get year because invoice date is null");
			else
				return getDate("invoice_date").getYear()+1900;
		else
			return super.getYear();
	}

	@Override
	public int getMonth() throws NullPointerException {
		if (isNull("month"))
			if (isNull("invoice_date"))
				throw new NullPointerException("Cannot get month because invoice date is null");
			else
				return getDate("invoice_date").getMonth()+1;
		else
			return super.getMonth();
	}

	public int getQuarter() throws NullPointerException {
		if (isNull("invoice_date"))
			throw new NullPointerException("Cannot get quarter because invoice date is null");
		else {
			int m = getDate("invoice_date").getMonth();
			if (m<3)
				return 1;
			else if (m<6)
				return 2;
			else if (m<9)
				return 3;
			else
				return 4;
		}
	}

	public String getBankAccount() {
		if (isNull("bank_account"))
			return null;
		else
			return getString("bank_account");
	}

	public void setBankAccount(String bank_account) {
		if (null==bank_account)
			remove("bank_account");
		else
			put("bank_account", bank_account);
	}

	public BigDecimal getWithholding() {
		if (isNull("withholding"))
			return null;
		else
			return new BigDecimal(getString("withholding"));
	}

	public void setWithholding(BigDecimal wthh) {
		if (wthh==null)
			remove("withholding");
		else
			put ("withholding", wthh.toString());
	}
	
	public BigDecimal getWithholdingPct() {
		if (isNull("withholdingpct"))
			return null;
		else
			return new BigDecimal(getString("withholdingpct"));
	}

	public void setWithholdingPct(BigDecimal wthh) {
		if (wthh==null)
			remove("withholdingpct");
		else
			put ("withholdingpct", wthh.toString());
	}
	
	public ArrayList<BigDecimal> getBaseAmounts() throws NumberFormatException {
		if (isNull("base_amount"))
			return null;
		else {
			String[] aStrs = getString("base_amount").split(";");			
			int nBases = aStrs.length;
			ArrayList<BigDecimal> aDecs = new ArrayList<BigDecimal>(nBases);
			for (int b=0; b<nBases; b++) aDecs.add(new BigDecimal(aStrs[b]));
			return aDecs;
		}
	}

	public void setBaseAmounts(ArrayList<BigDecimal> aBases) {
		if (null==aBases) {
			remove("base_amount");
		} else if (aBases.size()==0) {
			remove("base_amount");
		} else {
			StringBuffer oStrs = new StringBuffer(aBases.get(0).toString());
			for (int v=1; v<aBases.size(); v++) oStrs.append(";"+aBases.get(v).toString());
			put("base_amount", oStrs.toString());
		}
	}
	
	public ArrayList<BigDecimal> getVats() throws NumberFormatException {
		if (isNull("vat"))
			return null;
		else {
			String[] aStrs = getString("vat").split(";");
			int nVats = aStrs.length;
			ArrayList<BigDecimal> aDecs = new ArrayList<BigDecimal>(nVats);
			for (int v=0; v<nVats; v++) aDecs.add(new BigDecimal(aStrs[v]));
			return aDecs;
		}
	}

	public void setVats(ArrayList<BigDecimal> aVats)
		throws ArrayIndexOutOfBoundsException {
		if (null==aVats) {
			remove("vat");
		} else if (aVats.size()==0) {
			remove("vat");
		} else {
			ArrayList<BigDecimal> aBases = getBaseAmounts();
			if (aBases.size()!=aVats.size()) throw new ArrayIndexOutOfBoundsException("Invoice.setVats() for Invoice "+id()+" base amounts and VATs count mismatch. There are "+String.valueOf(aBases.size())+" base amounts {"+getString("base_amount")+"} and "+String.valueOf(aVats.size())+" {"+getString("vat")+"} VATs");
			StringBuffer oStrs = new StringBuffer(aVats.get(0).toString());
			for (int v=1; v<aVats.size(); v++)
				oStrs.append(";"+aVats.get(v).toString());
			put("vat", oStrs.toString());
			if (aBases!=null) {
				StringBuffer oPcts = new StringBuffer();
				for (int b=0; b<aVats.size(); b++)
					if (aBases.get(b).signum()!=0)
						oPcts.append((b==0 ? "" : ";")+aVats.get(b).divide(aBases.get(b),2, RoundingMode.HALF_EVEN).toString());
					else
						oPcts.append("0");
				put("vatpct", oPcts.toString());
			}
		}
	}

	public ArrayList<BigDecimal> getVatPcts() throws NumberFormatException {
		if (isNull("vatpct"))
			return null;
		else {
			String[] aStrs = getString("vatpct").split(";");
			int nVats = aStrs.length;
			ArrayList<BigDecimal> aDecs = new ArrayList<BigDecimal>(nVats);
			for (int v=0; v<nVats; v++) aDecs.add(new BigDecimal(aStrs[v]));
			return aDecs;
		}
	}

	public void setVatPcts(ArrayList<BigDecimal> aPcts)
		throws ArrayIndexOutOfBoundsException {
		if (null==aPcts) {
			remove("vatpct");
		} else if (aPcts.size()==0) {
			remove("vatpct");
		} else {
			ArrayList<BigDecimal> aBases = getBaseAmounts();
			StringBuffer oStrs = new StringBuffer(aPcts.get(0).toString());
			for (int v=1; v<aPcts.size(); v++) oStrs.append(";"+aPcts.get(v).toString());
			put("vatpct", oStrs.toString());
			if (aBases!=null) {
				StringBuffer oVats = new StringBuffer(aPcts.get(0).multiply(aBases.get(0)).toString());
				for (int b=1; b<aPcts.size(); b++)
					oVats.append(";"+aPcts.get(b).multiply(aBases.get(b)).toString());
				put("vat", oVats.toString());
			}
		}
	}

	public boolean isApproved() {
		return getStringNull("is_approved").equals("1");
	}
	
	public String getServiceFlavor() {
		return getStringNull("service_type");
	}

	public void setServiceFlavor(String flavor) throws NullPointerException,IllegalArgumentException {
		if (null==flavor) throw new NullPointerException("Service Flavor cannot be null");
		if (flavor.length()==0) throw new NullPointerException("Service Flavor cannot empty");
		boolean bIsAllowedFlavor = false;
		String[] aFlavs = CaptureServiceFlavor.flavors();
		for (int f=0; f<aFlavs.length; f++)
			if (bIsAllowedFlavor=aFlavs[f].equalsIgnoreCase(flavor)) break;
		if (!bIsAllowedFlavor)
		put("service_type", flavor.toLowerCase());
	}

	public BigDecimal computeTotal() throws ArrayIndexOutOfBoundsException {
		ArrayList<BigDecimal> baseamounts = getBaseAmounts();
		final int n = baseamounts.size();
		BigDecimal[] bases = getBaseAmounts().toArray(new BigDecimal[n]);
		BigDecimal[] vats = getVats().toArray(new BigDecimal[n]);
		BigDecimal[] pcts = getVatPcts().toArray(new BigDecimal[n]);
		BigDecimal withh = getWithholding();
		BigDecimal withp = getWithholdingPct();
		BigDecimal total;
		if (bases==null) {
			total = null;
			remove("final_amount");
		} else {
			total = new BigDecimal("0");
			for (int b=0; b<bases.length; b++) {
				if (bases[b]!=null)
					if (vats[b]==null)
						if (pcts[b]==null)
							total = total.add(bases[b]);
						else
							total = total.add(bases[b].multiply(pcts[b]));
					else
						total = total.add(bases[b].add(vats[b]));
				Log.out.debug("subtotal="+total);
			}
			if (withp!=null)					
				total = total.subtract(getBaseAmount().multiply(withp));
			else if (withh!=null)
				total = total.subtract(withh);
			put("final_amount", total.toString());
		}
		return total;
	}
	
	private void updateCounters(AtrilSession oSes, int iAction)
		throws DmsException, ClassNotFoundException, NullPointerException, IllegalStateException, SQLException, IOException {
		TaxPayer oTxp = taxPayer(oSes.getDms());
		if (!isProcessed() && ACTION_PROCESS==iAction) {
			oTxp.incPendingInvoicesCount(oSes, -1l);
			oTxp.incProcessedInvoicesCount(oSes, 1l);
		} else if (!isProcessed() && ACTION_APPROVE==iAction) {
			oTxp.incPendingInvoicesCount(oSes, -1l);
			oTxp.incApprovedInvoicesCount(oSes, 1l);
		} else if (isProcessed() && !isApproved() && ACTION_APPROVE==iAction) {
			oTxp.incProcessedInvoicesCount(oSes, -1l);
			oTxp.incApprovedInvoicesCount(oSes, 1l);
		} else if (isProcessed() && !isApproved() && ACTION_REJECT==iAction) {
			oTxp.incProcessedInvoicesCount(oSes, -1l);
			oTxp.incPendingInvoicesCount(oSes, 1l);
		} else if (isApproved() &&  ACTION_REJECT==iAction) {
			oTxp.incApprovedInvoicesCount(oSes, -1l);
			oTxp.incPendingInvoicesCount(oSes, 1l);
		} else if (!isProcessed() && ACTION_DELETE==iAction) {
			oTxp.incPendingInvoicesCount(oSes, -1l);
			oTxp.incTotalInvoicesCount(oSes, -1l);
		} else if (isProcessed() && !isApproved() && ACTION_DELETE==iAction) {
			oTxp.incProcessedInvoicesCount(oSes, -1l);
			oTxp.incTotalInvoicesCount(oSes, -1l);
		} else if (isApproved() && ACTION_DELETE==iAction) {
			oTxp.incApprovedInvoicesCount(oSes, -1l);
			oTxp.incTotalInvoicesCount(oSes, -1l);
		}
	}
	
	public void process(AtrilSession oSes, String sUserUuid)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException {
		try {
			updateCounters(oSes, ACTION_PROCESS);
		} catch(Exception e) {
			Log.out.error(e.getClass()+" "+e.getMessage(), e);
		}
		put("is_processed","1");
		put("process_date",new Date());
		put("processed_by",sUserUuid);
		save(oSes);
	}

	public void approve(AtrilSession oSes, String sUserUuid)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException {
		try {
			updateCounters(oSes, ACTION_APPROVE);
		} catch(Exception e) {
			Log.out.error(e.getClass()+" "+e.getMessage(), e);
		}
		put("is_approved","1");
		put("is_processed","1");
		put("has_mistakes","0");
		put("approved_by",sUserUuid);
		save(oSes);
	}

	public void reject(AtrilSession oSes, String sUserUuid, String sComments)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException {
		try {
			updateCounters(oSes, ACTION_REJECT);
		} catch(Exception e) {
			Log.out.error(e.getClass()+" "+e.getMessage(), e);
		}
		put("has_mistakes","1");
		put("is_approved","0");
		put("is_processed","0");
		put("approved_by",sUserUuid);
		if (sComments!=null) {
			if (isNull("comments"))
				put("comments",sComments);
			else
				put("comments",getString("comments")+"\n"+sComments);					
		}
		save(oSes);
	}
	
	public InvoiceThumbnail thumbnail(AtrilSession oSes) {
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("InvoiceThumbnail")) {
				return new InvoiceThumbnail(oSes.getDms().getDocument(d.id()));
			}
		}
		return null;		
	}
	
	private class PageNumComparator implements Comparator<InvoicePage> {
		public int compare(InvoicePage p1, InvoicePage p2) {
			return p1.number()-p2.number();
		}
	}

	private static PageNumComparator oPgnCmp = new Invoice().new PageNumComparator();
		
	public ArrayList<InvoicePage> pages(AtrilSession oSes) {
		ArrayList<InvoicePage> aPages = new ArrayList<InvoicePage>();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Side")) {
				aPages.add(new InvoicePage(oSes.getDms().getDocument(d.id())));
			}
		}
		Collections.sort(aPages, oPgnCmp);
		return aPages;
	}

	public int pageCount() {
		int nCount = 0;
		for (Document d : getDocument().children())
			if (d.type().name().equals("Side")) nCount++;
		return nCount;
	}
	
	public TaxPayer taxPayer(Dms oDms) {
		return new Invoices(oDms.getDocument(getDocument().parents().get(0).id())).taxPayer(oDms);
	}

	public BaseCompanyObject biller(AtrilSession oSes) {
		BaseCompanyObject oObj;
		if (isNull("biller_taxpayer")) {
			oObj=null;
		} else {
			Document d = oSes.getDms().getDocument(getString("biller_taxpayer"));
			if (d.type().name().equals("TaxPayer"))
				oObj=new TaxPayer(d);
			else if (d.type().name().equals("Client"))
				oObj=new Client(d);
			else
				oObj=null;
		}
		return oObj;
	}
	
	public InvoicePage createPage(AtrilSession oSes, InputStream oIns, int nPageNum, String sFileName)
		throws DmsException, NotYetConnectedException, ElementNotFoundException, ClassCastException,
		IllegalStateException, NullPointerException, RuntimeException, IOException {
		InvoicePage oPage = new InvoicePage(oSes, this, nPageNum);					
		oPage.save(oSes);
		oPage.insertContentFromInputStream(oSes, oIns, sFileName);
		oPage.save(oSes);
		DAO.log(oSes, oPage.getDocument(), InvoicePage.class, "CREATE INVOICE PAGE", AtrilEvent.Level.INFO, oPage.getDocument().id()+";"+id());		
		oSes.commit();
		customerAccount(oSes.getDms()).grant(oPage);
		return oPage;
	}

	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException, DmsDocumentModificationException {

		if (!containsKey("creation_date")) put("creation_date", new Date());
		if (!containsKey("is_processed")) put("is_processed", "0");
		if (!containsKey("is_approved")) put("is_approved", "0");
		if (!containsKey("has_mistakes")) put("has_mistakes", "0");

		super.save(oSes);
		Volume oVol = getVolume(oSes);
		
		if (oVol==null) {
			Log.out.debug("Getting default volume");
			oVol = DAO.defaultVolume(oSes.getDms().getVolumeManager());
			if (oVol==null) throw new DmsException("No default volume is set");
			oVol.addDocument(getDocument());
			oVol.save();
			Log.out.debug("Added Invoice "+getDocument().id()+" to Volume "+oVol.name());
		} else {
			Log.out.debug("Got volume "+oVol.name());			
		}
	}
	
	public void save(AtrilSession oSes, String sUserUuid, boolean bProcess, boolean bApprove, boolean bHasMistakes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException, DmsDocumentModificationException {

		put("is_processed", bProcess ? "1" : "0");
		put("is_approved", bApprove ? "1" : "0");
		put("has_mistakes", bHasMistakes ? "1" : "0");
		if (bProcess) {
			put("process_date",new Date());
			put("processed_by",sUserUuid);
		}
		if (bApprove) {
			put("approved_by",sUserUuid);
		}
		if (bHasMistakes) {
			put("approved_by",sUserUuid);
		}		
		save(oSes);			
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
		CaptureServiceFlavor oFlv = CaptureServiceFlavors.getCaptureServiceFlavor("INVOICES", getServiceFlavor());
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
		new Invoice(oDms, sDocId).delete(oSes, oDms);
	}	
}
