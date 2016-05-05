package com.zesped.model;

import java.nio.channels.NotYetConnectedException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.stripes.format.Formatter;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Client extends BaseCompanyObject implements TypeConverter<Client>,Formatter<Client> {

	private static final long serialVersionUID = 1L;

	private Country oCntr;
	private State oStat;

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("taxpayer",DataType.STRING,false,false,null),
    	new Attr("creation_date",DataType.DATE_TIME,false,true,null),
    	new Attr("active",DataType.STRING,false,true,null),
    	new Attr("client_code",DataType.STRING,false,false,null),
    	new Attr("business_name",DataType.STRING,true,true,null),
    	new Attr("tax_id",DataType.STRING,true,false,null),
    	new Attr("address1",DataType.STRING,false,false,null),
    	new Attr("address2",DataType.STRING,false,false,null),
    	new Attr("city",DataType.STRING,false,false,null),
    	new Attr("zipcode",DataType.STRING,false,false,null),
    	new Attr("state",DataType.STRING,false,false,null),
    	new Attr("country",DataType.STRING,false,false,null),
    	new Attr("contact_person",DataType.STRING,false,false,null),
    	new Attr("telephone",DataType.STRING,false,false,null),
    	new Attr("email",DataType.STRING,false,false,null,BaseCompanyObject.checkEmailSyntaxConstraint()),
    	new Attr("bank_brandname",DataType.STRING,false,false,null),
    	new Attr("bank_account",DataType.STRING,false,false,null),
    	new Attr("comments",DataType.STRING,false,false,null)
    };
		
	public Client() {
		super("Client");
		oCntr = null;
		oStat = null;
	}

	public Client(Document c) {
		super(c);
		loadCountryAndStateNames();
	}

	public Client(Dms oDms, String sDocId) throws ElementNotFoundException {
		super(oDms, sDocId);
		loadCountryAndStateNames();
	}

	public Client(AtrilSession oSes, Clients oParent) {
		super("Client");
		newDocument(oSes, oParent.getDocument());
		oCntr = null;
		oStat = null;
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public CustomerAccount customerAccount(Dms oDms) {
		for (Document c : getDocument().parents()) {
			if (c.type().name().equals("Clients")) {
				c = oDms.getDocument(c.id());
				for (Document a : c.parents()) 
					if (a.type().name().equals("CustomerAccount"))
						return new CustomerAccount(oDms.getDocument(a.id()));
			}
		}
		return null;
	}
	
	public String getTaxPayer() {
		return getString("taxpayer");
	}

	public String getCountry() {
		return getString("country");
	}
	
	public String getState() {
		return getString("state");
	}

	public String getStateName() {
		if (null==oStat)
			return null;
		else
			return oStat.getName();
	}

	public void setTaxPayer(String t) throws NullPointerException {
		if (t==null) throw new NullPointerException("Tax payer for client cannot be null");
	  	put("taxpayer", t);
	}
	
	public static String forTaxId(String sTaxId)  throws ElementNotFoundException, NullPointerException, IllegalArgumentException {
		return forTaxId(sTaxId, "Client");
	}

	private void loadCountryAndStateNames() throws IllegalStateException {
		if (!isNull("country")) {
			AtrilSession oSes = DAO.getAdminSession("Client");
			oCntr = new Countries().getCountry(oSes, getCountry());
			if (!isNull("state"))
				oStat = oCntr.states(oSes).getState(oSes, getState());
			else
				oStat = null;
			oSes.disconnect();
			oSes.close();
		} else {
			oCntr = null;
			oStat = null;
		}
		
	}

	@Override
	public void load(AtrilSession oSes, String sDocId) throws ElementNotFoundException, NotEnoughRightsException , DmsException {
		super.load(oSes, sDocId);
		loadCountryAndStateNames();
	}
	
	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException,
		NullPointerException, NotYetConnectedException, DmsException {

		if (!containsKey("creation_date")) put("creation_date", new Date());
		if (!containsKey("active")) put("active", "1");

		super.save(oSes);
	}

	public int invoicesCount(Dms oDms) {
		SortableList<Document> oLst = oDms.query("Invoice & (($taxpayer='"+id()+"') | ($biller_taxpayer='"+id()+"') | ($recipient_taxpayer='"+id()+"'))");	
		if (oLst.isEmpty())
			return 0;
		else
			return oLst.size();
	}

	public int ticketsCount(Dms oDms) {
		SortableList<Document> oLst = oDms.query("Ticket & (($taxpayer='"+id()+"') | ($biller_taxpayer='"+id()+"'))");
		if (oLst.isEmpty())
			return 0;
		else
			return oLst.size();
	}

	@Override
	protected void delete(AtrilSession oSes, Dms oDms)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException,
		ClassCastException, IllegalStateException, DmsException {
		if (invoicesCount(oDms)>0)
			throw new DmsException ("Foreign key constraint violation. Client is referenced by Invoices.");
		else if (ticketsCount(oDms)>0)
			throw new DmsException ("Foreign key constraint violation. Client is referenced by Tickets.");
		else
			super.delete(oSes, oDms);
	}

	public static void delete(AtrilSession oSes, String sDocId)
		throws DmsException, ElementNotFoundException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, ClassCastException, IllegalStateException {
		Dms oDms = oSes.getDms();
		new Client(oDms, sDocId).delete(oSes, oDms);
	}	

	@Override
	public Client convert(String sClientId, Class<? extends Client> clientClass,
						  Collection<ValidationError> conversionErrors) {
		if (sClientId==null) return null;
		if (sClientId.length()==0) return null;
		try {
			AtrilSession oSes = DAO.getAdminSession("Client");
			Client oClnt = new Client(oSes.getDms(), sClientId);
			oSes.disconnect();
			oSes.close();
			oSes = null;
			if (null==oClnt) {
				Log.out.error("No client with id "+sClientId+" was found");
				conversionErrors.add(new SimpleError("No client with id "+sClientId+" was found"));
			}
			return oClnt;
		} catch (Exception exc) {
			Log.out.error(exc.getClass().getName()+" Client.convert("+sClientId+") "+exc.getMessage());
			conversionErrors.add(new SimpleError(exc.getMessage()));
			return null;
		}
	}
		
	@Override
	public void setLocale(Locale locale) { }

	public String format(Client c) {
		return c.id();
	}

	public void init() { }

	@Override
	public void setFormatType(String type) { }

	@Override
	public void setFormatPattern(String pattern) { }	
}
