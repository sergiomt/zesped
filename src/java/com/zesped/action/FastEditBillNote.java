package com.zesped.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.knowgate.math.CurrencyCode;
import com.knowgate.math.Money;
import com.knowgate.misc.Calendar;
import com.knowgate.storage.StorageException;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.AccountingAccount;
import com.zesped.model.BillNote;
import com.zesped.model.CaptureService;
import com.zesped.model.Client;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.TaxPayer;
import com.zesped.model.VatPercent;
import com.zesped.model.VatPercents;

import com.zesped.model.Ticket;
import com.zesped.util.BigDecimalTypeConverter;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class FastEditBillNote extends BaseActionBean {

	private static final String FORM = "/WEB-INF/jsp/fasteditbillnote.jsp";
	
	private ArrayList<String> ticketIds;
	private ArrayList<String> ticketNumbers;
	private ArrayList<String> ticketDates;
	private ArrayList<Client> billers;
	private ArrayList<String> billersids;
	private ArrayList<BigDecimal> baseAmounts;
	private ArrayList<BigDecimal> vats;
	private ArrayList<BigDecimal> vatPcts;
	private ArrayList<BigDecimal> totalAmounts;
	private ArrayList<String> currencies;
	private ArrayList<String> currencySymbols;
	private ArrayList<String> accounts;
	private ArrayList<String> thumbnails;
	private ArrayList<AccountingAccount> accaccounts;
	private ArrayList<Employee> employees;
    private String concept;
	private String employeeName;
	private String employeeUuid;
	private BigDecimal base;
	private BigDecimal total;
	private BigDecimal vat;
	private boolean isopen;
	private String id;
	
	private SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

	public FastEditBillNote() {
		ticketIds = new ArrayList<String>();
		thumbnails = new ArrayList<String>();
		billers = new ArrayList<Client>();
		billersids = new ArrayList<String>();
		ticketNumbers = new ArrayList<String>();
		ticketDates = new ArrayList<String>();
		accounts = new ArrayList<String>();
		baseAmounts = new ArrayList<BigDecimal>();
		totalAmounts = new ArrayList<BigDecimal>();
		vats = new ArrayList<BigDecimal>();
		vatPcts = new ArrayList<BigDecimal>();
		currencies = new ArrayList<String>();
		currencySymbols = new ArrayList<String>();		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String i) {
		id = i;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}

	public String getEmployeeUuid() {
		return employeeUuid;
	}

	public int getTicketCount() {
		return ticketIds.size();
	}
	
	public boolean getIsOpen() {
		return isopen;
	}

	public void setIsOpen(boolean o) {
		isopen = o;
	}
	
	public ArrayList<String> getTicketIds() {
		return ticketIds;
	}

	public void setTicketIds(ArrayList<String> a) {
		ticketIds = new ArrayList<String>(a);
	}

	public ArrayList<String> getThumbnails() {
		return thumbnails;
	}
	
	public String getConcept() {
		return concept;
	}

	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal v) {
		vat = v;
	}

	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getVatAlt() {
		if (getVat()==null) return null;
		for (String c : currencies) {
			if (!"EUR".equalsIgnoreCase(c)) {
				return new Money(getVat(), CurrencyCode.EUR).convertTo(c).round2();
			}
		}
		return null;
	}
	
	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getBaseAmount() {
		return base;
	}

	public void setBaseAmount(BigDecimal b) {
		base = b;
	}

	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getBaseAmountAlt() {
		if (getBaseAmount()==null) return null;
		for (String c : currencies) {
			if (!"EUR".equalsIgnoreCase(c)) {
				return new Money(getBaseAmount(), CurrencyCode.EUR).convertTo(c).round2();
			}
		}
		return null;
	}
	
	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getTotalAmount() {
		return total;
	}

	public void setTotalAmount(BigDecimal t) {
		total = t;
	}

	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getTotalAmountAlt() {
		if (getTotalAmount()==null) return null;
		for (String c : currencies) {
			if (!"EUR".equalsIgnoreCase(c)) {
				return new Money(getTotalAmount(), CurrencyCode.EUR).convertTo(c).round2();
			}
		}
		return null;
	}

	public String getCurrencyAlt() {
		for (String c : currencies)
			if (!"EUR".equalsIgnoreCase(c)) return c;
		return "";
	}
	
	public String getCurrencySymbolAlt() {
		if (getCurrencyAlt().length()>0)
		  return CurrencyCode.currencyCodeFor(getCurrencyAlt()).singleCharSign();
		else
		  return "";
	}
	
	public ArrayList<String> getTicketNumbers() {
		return ticketNumbers;
	}

	public void setTicketNumbers(ArrayList<String> n) {
		ticketNumbers = new ArrayList<String>(n);
	}

	public ArrayList<String> getTicketDates() {
		return ticketDates;
	}
	
	public void setTicketDates(ArrayList<String> d) {
		ticketDates = new ArrayList<String>(d);
	}
	
	@Validate(converter=Client.class)
	public ArrayList<Client> getBillers() {
		return billers;
	}

	public ArrayList<String> getBillersIds() {
		return billersids;
	}
	
	@Validate(converter=BigDecimalTypeConverter.class)
	public ArrayList<BigDecimal> getBaseAmounts() {
		return baseAmounts;
	}

	public void setBaseAmounts(ArrayList<BigDecimal> b) {
		baseAmounts = new ArrayList<BigDecimal>(b);
	}
	
	@Validate(converter=BigDecimalTypeConverter.class)
	public ArrayList<BigDecimal> getTotalAmounts() {
		return totalAmounts;
	}

	public void setTotalAmounts(ArrayList<BigDecimal> t) {
		totalAmounts = new ArrayList<BigDecimal>(t);
	}
	
	@Validate(converter=BigDecimalTypeConverter.class)
	public ArrayList<BigDecimal> getVats() {
		return vats;
	}

	public void setVats(ArrayList<BigDecimal> v) {
		vats = new ArrayList<BigDecimal>(v);
	}
	
	public ArrayList<BigDecimal> getVatPcts() {
		return vatPcts;
	}

	public void setVatPcts(ArrayList<BigDecimal> p) {
		vatPcts = new ArrayList<BigDecimal>(p);
	}

	public ArrayList<String> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(ArrayList<String> c) {
		currencies = c;
	}

	public ArrayList<String> getCurrencySymbols() {
		return currencySymbols;
	}

	public void setCurrencySymbols(ArrayList<String> s) {
		currencySymbols = s;
	}
	
	public ArrayList<String> getAccounts() {
		return accounts;
	}

	public void getAccounts(ArrayList<String> a) {
		accounts = new ArrayList<String>(a);
	}

	public ArrayList<Employee> getEmployees() {
		return employees;
	}
	
	public Collection<VatPercent> getVatPercents() {
		return VatPercents.listActive();
	}

	public ArrayList<AccountingAccount> getAccountingAccounts() {
		return accaccounts;
	}

	public Collection<Client> getClients() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		AtrilSession oSes = DAO.getAdminSession("BaseEditBean");
		CustomerAccount oAcc = new CustomerAccount(oSes.getDms().getDocument(getSessionAttribute("customer_account_docid")));
		Collection<Client> clients = oAcc.clients(oSes).list(oSes);
		oSes.disconnect();
		oSes.close();
		return clients;
	}

	@Override
	public CaptureService getCaptureService() {
		return CaptureService.BILLNOTES;
	}

	@DefaultHandler
	public Resolution form() {
		final String sNickN = getSessionAttribute("nickname");
		final String sPassw = getSessionAttribute("password");
		final String sBillNoteId = getParam("id");
		try {
			connect(sNickN, sPassw);
			Dms oDms = getSession().getDms();
			BillNote oBln = new BillNote(oDms, sBillNoteId);
			setId(sBillNoteId);
			oBln.computeSettling(getSession());
			setIsOpen(oBln.isOpen());
			base = oBln.getBaseAmount();
			total = oBln.getTotalAmount();
			vat = oBln.getVat();
			concept = oBln.getConcept();
			ArrayList<Ticket> oTcs = oBln.tickets(getSession());
			int nTcs = oTcs.size();
			Log.out.debug("FastEditBillNote "+String.valueOf(nTcs)+" tickets found");
			TaxPayer oTxp = oBln.taxPayer(oDms);
			employees = oTxp.employees(getSession()).list(getSession());
			accaccounts = oTxp.accounts(getSession()).list(getSession());
			employeeName = oBln.getEmployeeName();
			employeeUuid = oBln.getEmployeeUuid();
			for (Ticket oTck : oTcs) {
				Log.out.debug("Reading ticket "+oTck.id());
				ticketIds.add(oTck.id());
				thumbnails.add(oTck.thumbnail(getSession()).id());
				if (oTck.isNull("biller_taxpayer")) {
					Log.out.debug("Ticket "+oTck.id()+" has no biller");
					billers.add(new Client());
					billersids.add("");
				} else {
					Client oClt = new Client(oDms, oTck.getBillerTaxPayer());
					billers.add(oClt);
					billersids.add(oClt.id());
					Log.out.debug("Ticket "+oTck.id()+" has biller "+oTck.getBillerTaxPayer());
				}
				ticketNumbers.add(oTck.getTicketNumber());
				if (oTck.isNull("ticket_date"))
					ticketDates.add("");
				else
					ticketDates.add(fmt.format(oTck.getTicketDate()));
				accounts.add(oTck.account(getSession()).getId());
				baseAmounts.add(oTck.getBaseAmount());
				totalAmounts.add(oTck.getTotalAmount());
				vats.add(oTck.getVat());
				Log.out.debug("adding vat pct "+oTck.getVatPct());
				if (oTck.isNull("vatpct"))
					vatPcts.add(BigDecimal.ZERO);
				else
					vatPcts.add(oTck.getVatPct());
				String sCurr = oTck.getStringNull("currency","EUR");
				if (sCurr.equals("EUR") || sCurr.length()==0) {
					currencies.add("EUR");
					currencySymbols.add(new String(new char[]{(char) 8364}));
				} else {
					currencies.add(sCurr);
					currencySymbols.add(CurrencyCode.currencyCodeFor(sCurr).singleCharSign());
				}
			} //next
			disconnect();
		} catch (InstantiationException xcpt) {
			Log.out.error("FastEditBillNote.form() "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());			
		} catch (IllegalAccessException xcpt) {
			Log.out.error("FastEditBillNote.form() "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());			
		} catch (StorageException xcpt) {
			Log.out.error("FastEditBillNote.form() "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());
		} finally {
			close();
		}
		return new ForwardResolution(FORM);
	}

	@ValidationMethod(on="save,settle")
	public void validateDates(ValidationErrors errors) {		
		for (String sDate : getTicketDates())
			if (sDate.length()>0 && !Calendar.isDate(sDate, "s"))
				errors.add("fecha", new LocalizableError("com.zesped.action.FastEditBillNote.invalidDate", sDate));
	}

	@ValidationMethod(on="settle")
	public void validateSettlingPreconditions(ValidationErrors errors) {
		final int nTickets = getTicketIds().size();
		for (int t=0; t<nTickets; t++) {
			if (getBaseAmounts().get(t)==null)
				errors.add("base", new LocalizableError("com.zesped.action.FastEditBillNote.missingTicketBase"));
			if (getTotalAmounts().get(t)==null)
				errors.add("total", new LocalizableError("com.zesped.action.FastEditBillNote.missingTicketTotal"));
			if (getVats().get(t)==null)
				errors.add("iva", new LocalizableError("com.zesped.action.FastEditBillNote.missingTicketVat"));
		}
	}
	
	private void write(boolean bSettle) {
		final String sNickN = getSessionAttribute("nickname");
		final String sPassw = getSessionAttribute("password");
		Log.out.debug("Writting Bill Note "+getId()+(bSettle ? " with settling" : " without settling"));
		try {
			connect(sNickN, sPassw);
			Dms oDms = getSession().getDms();
			final int nTickets = getTicketIds().size();
			Log.out.debug(String.valueOf(nTickets)+" tickets found");
			Log.out.debug(String.valueOf(getBillers().size())+" billers found");
			for (int t=0; t<nTickets; t++) {
				Log.out.debug("Updating Ticket "+getTicketIds().get(t));
				Ticket oTck = new Ticket(oDms, getTicketIds().get(t));
				if (getTicketNumbers().size()>t)
					if (getTicketNumbers().get(t)==null)
						oTck.remove("ticket_number");
					else
						oTck.put("ticket_number", getTicketNumbers().get(t));				
				else
					oTck.remove("ticket_number");					
				if (getAccounts().get(t).length()==0) {
					oTck.remove("accounting_uuid");
					oTck.remove("accounting_code");
					oTck.remove("accounting_desc");
				} else {
					AccountingAccount oAcc = new AccountingAccount(oDms,getAccounts().get(t));
					oTck.put("accounting_uuid", oAcc.getUuid());
					oTck.put("accounting_code", oAcc.getCode());
					oTck.put("accounting_desc", oAcc.getDescription());
				}
				if (getBillers().size()>t) {
					Log.out.debug("Ticket biller is "+getBillers().get(t));
					if (getBillers().get(t)!=null)
						if (getBillers().get(t).id()!=null)
							oTck.setBillerTaxPayer(getBillers().get(t).id());					
						else
							oTck.setBillerTaxPayer("");										
					else
						oTck.setBillerTaxPayer("");										
				} else {
					oTck.setBillerTaxPayer("");										
				}
				Log.out.debug("Ticket base amount "+getBaseAmounts().get(t));
				if (getBaseAmounts().get(t)!=null)
					oTck.setBaseAmount(getBaseAmounts().get(t));
				if (getTotalAmounts().get(t)!=null)
					oTck.setTotalAmount(getTotalAmounts().get(t));
				if (getVats().get(t)!=null)
					oTck.setVat(getVats().get(t));
				if (getVatPcts().get(t)!=null)
					oTck.setVatPct(getVatPcts().get(t));				
				if (bSettle) {
					oTck.isOpen(false);
					oTck.isProcessed(true);
				}
				oTck.save(getSession());
			}
			Log.out.debug("Updating Bill Note");
			BillNote oBln = new BillNote(oDms, getId());
			oBln.setProcessDate(new Date());
			if (bSettle) {
				oBln.settle(getSession(), getSessionAttribute("user_uuid"));
			} else {
				oBln.computeSettling(getSession());
				oBln.save(getSession());
			}
			disconnect();
		} catch (StorageException xcpt) {
			Log.out.error("FastEditBillNote.write() "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());
		} finally {
			close();
		}
	}

	public Resolution reopen() {
		final String sNickN = getSessionAttribute("nickname");
		final String sPassw = getSessionAttribute("password");

		try {
			connect(sNickN, sPassw);
			Dms oDms = getSession().getDms();
			BillNote oBln = new BillNote(oDms, getId());
			oBln.isOpen(true);
			oBln.save(getSession());
			DAO.log(getSession(), oBln.getDocument(), BillNote.class, "REOPEN BILLNOTE", AtrilEvent.Level.INFO, oBln.id()+";"+oBln.getTaxPayer());
			disconnect();
		} catch (Exception xcpt) {
			Log.out.error("FastEditBillNote.write() "+sNickN+" "+xcpt.getClass()+" "+xcpt.getMessage());
		} finally {
			close();
		}
		return new RedirectResolution("/FastEditBillNote.action?id="+getId());
	}
	
	public Resolution save() {
		write(false);
		return new RedirectResolution("/FastEditBillNote.action?id="+getId());
	}

	public Resolution settle() {
		write(true);
		return new RedirectResolution(ListPendingBillNotes.class);
	}
	
}
