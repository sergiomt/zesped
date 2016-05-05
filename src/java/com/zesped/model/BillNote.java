package com.zesped.model;

import java.math.BigDecimal;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.knowgate.math.CurrencyCode;
import com.knowgate.math.Money;
import com.knowgate.misc.Gadgets;
import com.knowgate.misc.NameValuePair;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class BillNote extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("service_type",DataType.STRING,false,true,null),
    	new Attr("taxpayer",DataType.STRING,false,false,null),
    	new Attr("employee_uuid",DataType.STRING,false,false,new ForeignKey(Employee.class, "employee_uuid")),
    	new Attr("employee_id",DataType.STRING,false,false,null),
    	new Attr("employee_name",DataType.STRING,false,false,null),
    	new Attr("creation_date",DataType.DATE_TIME,false,true,null),
    	new Attr("process_date",DataType.DATE,false,false,null),
    	new Attr("close_date",DataType.DATE,false,false,null),
    	new Attr("concept",DataType.STRING,false,true,null),
    	new Attr("is_open",DataType.STRING,false,true,null),
    	new Attr("base_amount",DataType.STRING,false,false,null),
    	new Attr("vat",DataType.STRING,false,false,null),
    	new Attr("final_amount",DataType.STRING,false,false,null),
    	new Attr("closed_by",DataType.STRING,false,false,new ForeignKey(User.class,"user_uuid")),    	
    	new Attr("comments",DataType.STRING,false,false,null)
    };
	
	public BillNote() {
		super("BillNote");
	}

	public BillNote(AtrilSession oSes, BillNotes oParent) {
		super("BillNote");
		newDocument(oSes, oParent.getDocument());
	}

	public BillNote(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}
	
	public BillNote(Document d) {
		super(d);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getTaxPayer() {
		if (isNull("taxpayer"))
			return null;
		else
			return getString("taxpayer");
	}

	public void setTaxPayer(String taxPayer) {
		if (null==taxPayer)
			remove("taxpayer");
		else
			put("taxpayer", taxPayer);
	}

	public void setEmployee(Employee e) {
		setEmployeeId(e.getEmployeeId());
		setEmployeeUuid(e.getUuid());
		setEmployeeName(e.getName());
	}
		
	public String getEmployeeUuid() {
		if (isNull("employee_uuid"))
			return null;
		else
			return getString("employee_uuid");
	}

	public void setEmployeeUuid(String uuid) throws NullPointerException {
		if (null==uuid)
			throw new NullPointerException("employee_uuid cannot be null");
		else if (uuid.trim().length()==0) 
			throw new NullPointerException("employee_uuid cannot be empty");
		else
			put("employee_uuid", uuid.trim());
	}

	public String getEmployeeId() {
		if (isNull("employee_id"))
			return null;
		else
			return getString("employee_id");
	}

	public void setEmployeeId(String eid) throws NullPointerException {
		if (null==eid)
			remove("employee_id");
		else
			put("employee_id", eid.trim());
	}
	
	public String getEmployeeName() {
		return getString("employee_name");
	}

	public void setEmployeeName(String name) throws NullPointerException {
		if (null==name)
			throw new NullPointerException("employee_name cannot be null");
		else if (name.trim().length()==0) 
			throw new NullPointerException("employee_name cannot be empty");
		else
			put("employee_name", name.trim());
	}

	public Date getCreationDate() {
		if (isNull("creation_date"))
			return null;
		else
			return getDate("creation_date");
	}

	public Date getCloseDate() {
		if (isNull("close_date"))
			return null;
		else
			return getDate("close_date");
	}
	
	public Date getProcessDate() {
		if (isNull("process_date"))
			return null;
		else
			return getDate("process_date");
	}

	public void setProcessDate(Date process_date) {
		if (null==process_date)
			remove("process_date");
		else
			put("process_date", process_date);
	}

	public BigDecimal getBaseAmount() {
		if (isNull("base_amount"))
			return null;
		else
			return new BigDecimal(getString("base_amount"));
	}

	public void setBaseAmount(BigDecimal base_amount) {
		if (null==base_amount)
			remove("base_amount");
		else
			put("base_amount", base_amount.toString());
	}

	public BigDecimal getTotalAmount() {
		if (isNull("final_amount"))
			return null;
		else
			return new BigDecimal(getString("final_amount"));
	}

	public void setTotalAmount(BigDecimal total_amount) {
		if (null==total_amount)
			remove("final_amount");
		else
			put("final_amount", total_amount.toString());
	}
	
	public BigDecimal getVat() {
		if (isNull("vat"))
			return null;
		else
			return new BigDecimal(getString("vat"));
	}

	public void setVat(BigDecimal vat) {
		if (null==vat)
			remove("vat");
		else
			put("vat", vat.toString());
	}

	public String getConcept() {
		if (isNull("concept"))
			return null;
		else
			return getString("concept");
	}

	public void setConcept(String concept) {
		if (null==concept)
			remove("concept");
		else
			put("concept", concept);
	}

	public String getComments() {
		if (isNull("comments"))
			return null;
		else
			return getString("comments");
	}

	public void setComments(String comments) {
		if (null==comments)
			remove("comments");
		else
			put("comments", comments);
	}

	public boolean isOpen() {
		return getStringNull("is_open","1").equals("1");
	}

	public void isOpen(boolean o) {
		put("is_open", o ? "1" : "0");
	}
	
	public CustomerAccount customerAccount(Dms oDms) {
		return taxPayer(oDms).customerAccount(oDms);
	}

	public ArrayList<Ticket> tickets(AtrilSession oSes) {
		ArrayList<Ticket> aTcks = new ArrayList<Ticket>();
		Dms oDms = oSes.getDms();
		for (Document t : getDocument().children()) {
			if (t.type().name().equals("Ticket")) {
				aTcks.add(new Ticket(oDms.getDocument(t.id())));
			}
		}
		return aTcks;
	}
	
	public TaxPayer taxPayer(Dms oDms) {
		return new BillNotes(oDms.getDocument(getDocument().parents().get(0).id())).taxPayer(oDms);
	}

	public String getServiceFlavor() {
		return getStringNull("service_type");
	}

	public boolean canBeSettled(AtrilSession oSes) {
		boolean bCanBeClosed = true;
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Ticket")) {
				Ticket oTck = new Ticket(d);
				if (oTck.isNull("base_amount") || oTck.isNull("final_amount")) {
					bCanBeClosed = false;
					break;
				} // fi
			} // if
		} // next
		return bCanBeClosed;
	}

	public void open(AtrilSession oSes, String sUserUuid)
			throws ClassCastException, RuntimeException, IllegalStateException,
			NullPointerException, NotYetConnectedException, DmsException, NumberFormatException {
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Ticket")) {
				Ticket oTck = new Ticket(d);
				oTck.put("is_open", "1");
				oTck.save(oSes);
				DAO.log(oSes, oTck.getDocument(), Ticket.class, "OPEN TICKET", AtrilEvent.Level.INFO, oTck.getDocument().id()+";"+oTck.getString("taxpayer"));
			}
		}
		put ("is_open","1");
		save(oSes);
		DAO.log(oSes, getDocument(), BillNote.class, "OPEN BILLNOTE", AtrilEvent.Level.INFO, getDocument().id()+";"+getString("taxpayer"));
	}

	public void updateSettling(AtrilSession oSes)
		throws NotYetConnectedException, DmsException, NumberFormatException, IllegalStateException {
		if (!isOpen()) throw new IllegalStateException("Cannot update amounts of a settled bill note");
		BigDecimal oBaseAmount = new BigDecimal(0);
		BigDecimal oTotalAmount = new BigDecimal(0);
	    BigDecimal oVAT = new BigDecimal(0);
		for (Document d : getDocument().children()) {
			String sTypeName = "";
			try { sTypeName = d.type().name(); } catch (Exception cannotgettype) { }
			if (sTypeName.equals("Ticket")) {
				Ticket oTck = new Ticket(d);
				if (!oTck.isNull("base_amount")) 
					oBaseAmount = oBaseAmount.add(oTck.getBaseAmount());
				if (!oTck.isNull("final_amount")) 
					oTotalAmount = oTotalAmount.add(oTck.getTotalAmount());
				if (!oTck.isNull("vat"))
					oVAT = oVAT.add(oTck.getVat());
			}
		}
		setVat(oVAT);
		setBaseAmount(oBaseAmount);
		setTotalAmount(oTotalAmount);
		save(oSes);
	}

	public void computeSettling(AtrilSession oSes)
		throws NotYetConnectedException, DmsException, NumberFormatException {
		BigDecimal oBaseAmount = BigDecimal.ZERO;
		BigDecimal oTotalAmount = BigDecimal.ZERO;
		BigDecimal oVAT = BigDecimal.ZERO;
		String sCur;
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Ticket")) {
				Ticket oTck = new Ticket(d);
				sCur = oTck.getStringNull("currency","EUR").toUpperCase();
				if (sCur.equals("EUR")) {
					if (oTck.getStringNull("vat","").length()>0)
						if (!oTck.getVat().equals(BigDecimal.ZERO))
							oVAT = oVAT.add(oTck.getVat());
					if (oTck.getStringNull("base_amount","").length()>0)
						if (!oTck.getBaseAmount().equals(BigDecimal.ZERO))
							oBaseAmount = oBaseAmount.add(oTck.getBaseAmount());
					if (oTck.getStringNull("final_amount","").length()>0)
						if (!oTck.getTotalAmount().equals(BigDecimal.ZERO))
							oTotalAmount = oTotalAmount.add(oTck.getTotalAmount());
				} else {				
					if (oTck.getStringNull("vat","").length()>0)
						if (!oTck.getVat().equals(BigDecimal.ZERO))
							oVAT = oVAT.add(new Money(oTck.getVat(), sCur).convertTo(CurrencyCode.EUR));
					if (oTck.getStringNull("base_amount","").length()>0)
						if (!oTck.getBaseAmount().equals(BigDecimal.ZERO))					
							oBaseAmount = oBaseAmount.add(new Money(oTck.getBaseAmount(), sCur).convertTo(CurrencyCode.EUR));
					if (oTck.getStringNull("final_amount","").length()>0)
						if (!oTck.getTotalAmount().equals(BigDecimal.ZERO))
							oTotalAmount = oTotalAmount.add(new Money(oTck.getTotalAmount(), sCur).convertTo(CurrencyCode.EUR));
				}
			}
		} // next
		Log.out.debug("VAT is "+oVAT.toString());
		Log.out.debug("Base is "+oBaseAmount.toString());
	    
		if (oVAT.equals(BigDecimal.ZERO)) setVat(BigDecimal.ZERO); else setVat(Gadgets.round2(oVAT));
		if (oBaseAmount.equals(BigDecimal.ZERO)) setBaseAmount(BigDecimal.ZERO); else setBaseAmount(Gadgets.round2(oBaseAmount));
		if (oTotalAmount.equals(BigDecimal.ZERO)) setTotalAmount(BigDecimal.ZERO); else setTotalAmount(Gadgets.round2(oTotalAmount));
	}

	public void settle(AtrilSession oSes, String sUserUuid)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException, NumberFormatException {
		BigDecimal oBaseAmount = BigDecimal.ZERO;
		BigDecimal oTotalAmount = BigDecimal.ZERO;
		BigDecimal oVAT = BigDecimal.ZERO;
		String sCur;
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Ticket")) {
				Ticket oTck = new Ticket(d);
				if (oTck.getStringNull("base_amount","").length()==0) throw new NullPointerException("base_amount is empty for ticket with doc id "+d.id());
				if (oTck.getStringNull("final_amount","").length()==0) throw new NullPointerException("final_amount is empty for ticket with doc id "+d.id());
				sCur = oTck.getStringNull("currency","EUR").toUpperCase();
				if (sCur.equals("EUR")) {
					if (oTck.getStringNull("vat","").length()>0)
						if (!oTck.getVat().equals(BigDecimal.ZERO))					
							oVAT = oVAT.add(oTck.getVat());
					oBaseAmount = oBaseAmount.add(oTck.getBaseAmount());
					oTotalAmount = oTotalAmount.add(oTck.getTotalAmount());
				} else {				
					if (oTck.getStringNull("vat","").length()>0)
						if (!oTck.getVat().equals(BigDecimal.ZERO))					
							oVAT = oVAT.add(new Money(oTck.getVat(), sCur).convertTo(CurrencyCode.EUR));
					oBaseAmount = oBaseAmount.add(new Money(oTck.getBaseAmount(), sCur).convertTo(CurrencyCode.EUR));
					oTotalAmount = oTotalAmount.add(new Money(oTck.getTotalAmount(), sCur).convertTo(CurrencyCode.EUR));
				}
				oTck.put("is_open", "0");
				oTck.save(oSes);
				DAO.log(oSes, oTck.getDocument(), Ticket.class, "CLOSE TICKET", AtrilEvent.Level.INFO, oTck.getDocument().id()+";"+oTck.getString("taxpayer"));
			}
		}
		if (oVAT.equals(BigDecimal.ZERO)) setVat(BigDecimal.ZERO); else setVat(Gadgets.round2(new BigDecimal(oVAT.toString().replace(",", "."))));
		if (oBaseAmount.equals(BigDecimal.ZERO)) setBaseAmount(BigDecimal.ZERO); else setBaseAmount(Gadgets.round2(new BigDecimal(oBaseAmount.toString().replace(",", "."))));
		if (oTotalAmount.equals(BigDecimal.ZERO)) setTotalAmount(BigDecimal.ZERO); else setTotalAmount(Gadgets.round2(new BigDecimal(oTotalAmount.toString().replace(",", "."))));
		put ("is_open","0");
		put ("closed_by", sUserUuid);
		put ("close_date", new Date());
		save(oSes);
		DAO.log(oSes, getDocument(), BillNote.class, "SETTLE BILLNOTE", AtrilEvent.Level.INFO, getDocument().id()+";"+getString("taxpayer"));
	}

	public void unsettle(AtrilSession oSes, String sUserUuid)
			throws ClassCastException, RuntimeException, IllegalStateException,
			NullPointerException, NotYetConnectedException, DmsException, NumberFormatException {
			put ("is_open","1");
			put ("closed_by", sUserUuid);
			put ("close_date", new Date());
			save(oSes);
			DAO.log(oSes, getDocument(), BillNote.class, "UNSETTLE BILLNOTE", AtrilEvent.Level.INFO, getDocument().id()+";"+getString("taxpayer"));
	}
	
	public Ticket createTicket(AtrilSession oSes, AccountingAccount oAac)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException {
		Dms oDms = oSes.getDms();
		Ticket oTck = new Ticket(oSes, this);
		oTck.put("taxpayer", taxPayer(oDms).id());
		oTck.put("employee_uuid", getEmployeeUuid());
		oTck.put("accounting_uuid", oAac.getUuid());
		oTck.put("accounting_code", oAac.getCode());
		oTck.put("accounting_desc", oAac.getDescription());
		oTck.put("is_open", "1");
		oTck.save(oSes);
		DAO.log(oSes, oTck.getDocument(), Ticket.class, "CREATE TICKET", AtrilEvent.Level.INFO, oTck.getDocument().id()+";"+oTck.getString("taxpayer"));
		oSes.commit();
		try {
			oTck.taxPayer(oDms).incPendingTicketsCount(oSes,1l);
		} catch (Exception e) {
		  Log.out.error(e.getClass().getName()+" "+e.getMessage(), e);
		}
		customerAccount(oDms).grant(oTck);
		oSes.commit();
		return oTck;
	}

	public Ticket createTicket(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException, ElementNotFoundException, InstantiationException, IllegalAccessException {
		Iterator<AccountingAccount> oItr = taxPayer(oSes.getDms()).accounts(oSes).list(oSes).iterator();
		if (oItr.hasNext())
			return createTicket(oSes, oItr.next());
		else
			throw new ElementNotFoundException("No default accounting account found for ticket");
	}

	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException {

		if (!containsKey("creation_date")) put("creation_date", new Date());
		if (!containsKey("is_open")) put("is_open", "1");
		if (!isNull("concept")) put("concept", Gadgets.removeChars(getString("concept"),"\"\n'"));

		Document oBil = exists(oSes, new NameValuePair("concept", getString("concept")), new NameValuePair("employee_uuid", getString("employee_uuid")));
		
		if (oBil!=null)
			if (oBil.id().equals(id()))
				throw new DmsException("A previous bill note with the same concept for the same employee already exists");

		super.save(oSes);
	}

	@Override
	protected void delete(AtrilSession oSes, Dms oDms)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException,
		IllegalStateException, DmsException  {
		ArrayList<String> aTckts = new ArrayList();
		for (Document t : getDocument().children())
			if (t.type().name().equals("Ticket"))
				aTckts.add(t.id());
		for (String s : aTckts)
			Ticket.delete(oSes, s);
		super.delete(oSes, oDms);
	}

	public static void delete(AtrilSession oSes, String sDocId)
		throws DmsException, ElementNotFoundException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, ClassCastException, IllegalStateException {
		Dms oDms = oSes.getDms();
		new BillNote(oDms, sDocId).delete(oSes, oDms);
	}	
	
}
