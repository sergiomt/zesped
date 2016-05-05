package com.zesped.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.knowgate.misc.Calendar;
import com.knowgate.storage.StorageException;

import com.zesped.Log;

import com.zesped.model.BillNote;
import com.zesped.model.TaxPayer;
import com.zesped.model.TicketNote;
import com.zesped.model.TicketThumbnail;
import com.zesped.model.CaptureService;
import com.zesped.model.Concept;
import com.zesped.model.Ticket;
import com.zesped.model.VatPercent;
import com.zesped.model.VatPercents;
import com.zesped.util.BigDecimalTypeConverter;

import es.ipsa.atril.doc.user.Dms;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

public class EditBillNote extends CaptureBillNote {

	private static final String FORM="/WEB-INF/jsp/editbillnote.jsp";

	private int billday, billmonth, billyear;

	private TicketThumbnail thumbnail;

	private ArrayList<String> pages;
	
	private boolean processed=false, isopen=true;

	public EditBillNote() {
		pages = new ArrayList<String>();
		billnote = null;
	}


	@Override
	@Validate(required=true, on="upload")
	public String getEmployee() {
		return super.getEmployee();
	}

	private String capuredpage1;

	public String getCapturedPage1() {
		return capuredpage1==null ? "" : capuredpage1;
	}

	public void setCapturedPage1(String p) {
		capuredpage1 = p;
	}

	private int capturedCount = 1;

	public int getCapturedCount() {
		return capturedCount;
	}
	
	public void setCapturedCount(int c) {
		capturedCount = c;
	}

	public int incCapturedCount() {
		return ++capturedCount;
	}
	
	public CaptureService getCaptureService() {
		return CaptureService.BILLNOTES;
	}
	
	private String number;
	
	public String getNumber() {
		return number==null ? "" : number;
	}

	public void setNumber(String n) {
		number = n;
	}

	@Validate(on="save", maxlength=3)
	public String getCurrency() {
		return currency;
	}

	public void setCurrency (String c) {
		currency = c;
	}
	
	protected String concept;

	@Validate(required=true, maxlength=50, on="save")
	public String getConcept() {
		return concept;
	}

	public void setConcept(String c) {
		concept = new Concept(c==null ? "" : c, new Date()).getName();
	}

	private String description;

	@Validate(maxlength=2000, on="save")
	public String getDescription() {
		return description;
	}

	public void setDescription(String d) {
		description = d;
	}
	
	public String comments;

	public String getComments() {
		return comments;
	}
	
	public void setComments(String c) {
		comments = c;
	}

	private String paymentMean;

	public String getPaymentMean() {
		return paymentMean;
	}

	public void setPaymentMean(String m) {
		paymentMean = m;
	}
	
	public String currency;
	
	public BigDecimal baseAmount, totalAmount, vat, vatPct;
	
	@Validate(on="save", converter=BigDecimalTypeConverter.class)
	public BigDecimal getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(BigDecimal a) {
		baseAmount = a;
	}

	@Validate(on="save", converter=BigDecimalTypeConverter.class)
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal a) {
		totalAmount = a;
	}

	@Validate(on="save", converter=BigDecimalTypeConverter.class)
	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal v) {
		vat = v;
	}

	public BigDecimal getVatPct() {
		return vatPct;
	}

	public void setVatPct(BigDecimal p) {
		vatPct = p;
	}

	public Collection<VatPercent> getVatPercents() {
		if (getVatPct()==null)
			return VatPercents.listActive();
		else
			return VatPercents.listAll();			
	}
	
	public int getBillDay() {
		return billday;
	}

	public void setBillDay(int d) {
		billday = d;
	}

	public int getBillMonth() {
		return billmonth;
	}

	public void setBillMonth(int m) {
		billmonth = m;
	}

	public int getBillYear() {
		return billyear;
	}

	public void setBillYear(int y) {
		billyear = y;
	}

	public boolean getOpen() {
		return isopen;
	}

	public void setOpen(boolean o) {
		isopen = o;
	}
	
	public boolean getProcessed() {
		return processed;
	}

	public void setProcessed(boolean p) {
		processed = p;
	}
	
	public TicketThumbnail getThumbnail() {
		Log.out.debug("EditBillNote.getThumbnail("+getId()+")");
		if (thumbnail==null) {
			try {
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				Ticket oTck = new Ticket(getSession().getDms(), getId());
		  		thumbnail = oTck.thumbnail(getSession());
		  	} catch (Exception xcpt) {
		  		  Log.out.error(xcpt.getMessage(), xcpt);
		  	} finally {
		  		close();
		  	}			
		} // fi	
		return thumbnail;
	}

	@Validate(converter=TicketThumbnail.class)
	public void setThumbnail(TicketThumbnail t) {
		thumbnail = t;
	}

	private String billnote;
	
	public String getBillNoteId() {
		return billnote;
	}
	
	private void setBillNoteId(String b) {
		billnote = b;
	}
	
	
	public ArrayList<String> getPages() {
		return pages;
	}
	
	@DefaultHandler
	public Resolution form() {
	  setId(getParam("id"));
	  String sFwd;
	  Log.out.debug("EditBillNote.form("+getId()+")");
	  if (getId()==null) {
		  sFwd = "error.jsp?e=parameternotset";
	  } else if (getId().length()==0) {
		  sFwd = "error.jsp?e=parameternotset";
	  } else {
		  setParam("id", getId());
		  Date dtToday = new Date();
		  try {
	  		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
	  		  Ticket oTck = new Ticket(getSession().getDms(), getId());
	  		  setId(oTck.id());
	  		  setOpen(oTck.isOpen());
	  		  setNumber(oTck.getTicketNumber());
	  		  setRecipientTaxPayer(oTck.getTaxPayer());
	  		  setProcessed(oTck.isProcessed());
	  		  setAccount(oTck.account(getSession()));
	  		  setBaseAmount(oTck.getBaseAmount());
	  		  setBillerTaxPayer(oTck.getBillerTaxPayer());
	  		  setCurrency(oTck.getCurrency());
	  		  setPaymentMean(oTck.getPaymentMean());
	  		  setTotalAmount(oTck.getTotalAmount());
	  		  setVat(oTck.getVat());
	  		  setVatPct(oTck.getVatPct());
	  		  setDescription(oTck.getConcept());
	  		  setComments(oTck.getComments());
	  		  if (oTck.getTicketDate()==null) {
	  			  setBillMonth(dtToday.getMonth()+1);
	  			  setBillYear(dtToday.getYear()+1900);
	  		  } else {
	  			  setBillDay(oTck.getTicketDate().getDate());
	  			  setBillMonth(oTck.getTicketDate().getMonth()+1);
	  			  setBillYear(oTck.getTicketDate().getYear()+1900);
	  		  }
	  		  BillNote oBln = oTck.billNote(getSession());
	  		  setBillNoteId(oBln.id());
	  		  setConcept(oBln.getConcept());
	  		  setEmployee(oBln.getEmployeeUuid());
	  		  ArrayList<TicketNote> oPgs = oTck.pages(getSession());
	  		  setCapturedCount(oPgs.size());
	  		  if (oPgs.size()>0) setCapturedPage1(oPgs.get(0).id());
	  		  for (TicketNote n : oPgs)
	  			  pages.add(n.id());
	  		  disconnect();
	  		  sFwd = FORM;
	  	  } catch (Exception xcpt) {
	  		  Log.out.error(xcpt.getMessage(), xcpt);
	  		  sFwd = "error.jsp?e=couldnotloadticket";
	  	  } finally {
	  		  close();
	  	  }			
		  Log.out.debug("EditBillNote forward "+sFwd);
	  }
	  return new ForwardResolution(sFwd);
	}

	@DontValidate
	public Resolution cancel() {
		return new RedirectResolution(CaptureBillNote.class);
	}

	@ValidationMethod(priority=-1)
	public void validateId() {
	  setParam("id", getParam("id"));		
	}

	@ValidationMethod(on="save")
	public void validateBillerAndRecipient(ValidationErrors errors) {
		if (getBillerTaxPayer().length()>0 && getRecipientTaxPayer().length()>0 && getBillerTaxPayer().equals(getRecipientTaxPayer())) {
			errors.add("emisor", new LocalizableError("com.zesped.action.EditInvoice.BillerAndRecipientMayNotBeTheSame"));
		}
	}

	@ValidationMethod(on="save")
	public void validateBillDate(ValidationErrors errors) {
		if (getBillDay()>0 || getBillMonth()>0 || getBillYear()>0) {
			if (getBillDay()==0 || getBillMonth()==0 || getBillYear()==0)
				errors.add("fechaNota", new LocalizableError("com.zesped.action.EditBillNote.invalidDate"));
			else if (getBillDay()>Calendar.LastDay(getBillMonth()-1, getBillYear())) {
				errors.add("fechaNota", new LocalizableError("com.zesped.action.EditBillNote.invalidDate"));				
			}
				
		}
	}

	@ValidationMethod(on="save")
	public void validateBillNoteVsEmployee(ValidationErrors errors) {
		final String sBeanEmployeeUUID = getEmployee()==null ? "" : getEmployee();
		String sConcept = getConcept()==null ? "" : getConcept();
		Log.out.debug("validateBillNoteVsEmployee concept="+sConcept+" uuid="+sBeanEmployeeUUID);
		try {
			if (sConcept.length()==0)
				errors.add("concepto", new LocalizableError("com.zesped.action.EditBillNote.concept.valueNotPresent"));
			if (sBeanEmployeeUUID.length()==0)
				errors.add("empleado", new LocalizableError("com.zesped.action.EditBillNote.employee.valueNotPresent"));				
			if (sConcept.length()>0 && sBeanEmployeeUUID.length()>0) {
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				Dms oDms = getSession().getDms();				
				TaxPayer txpy = new TaxPayer(oDms, getRecipientTaxPayer());
				BillNote bill = txpy.billnotes(getSession()).forConcept(getSession(), sConcept, sBeanEmployeeUUID);
				Log.out.debug("BillNote with Id "+bill.id()+" found for concept "+sConcept+" and employee "+sBeanEmployeeUUID);
				if (bill.isOpen()) {
					final String sBillEmployeeUUID = (bill.getEmployeeUuid()==null ? "" : bill.getEmployeeUuid());
					if (sBillEmployeeUUID.length()==0) {
						Log.out.debug("Assigning BillNote "+bill.id()+" to employee "+sBeanEmployeeUUID);
						bill.setEmployee(txpy.employees(getSession()).seek(getSession(), sBeanEmployeeUUID));
						bill.save(getSession());
						setBillNoteId(bill.id());
					} else if (sBillEmployeeUUID.equals(sBeanEmployeeUUID)) {
						setBillNoteId(bill.id());
					} else {
						errors.add("empleado", new LocalizableError("com.zesped.action.EditBillNote.employeeConceptMismatch"));					
					}					
				} else {
					errors.add("billnote", new LocalizableError("com.zesped.action.EditBillNote.billNoteIsClosed"));
				}
				disconnect();
			} else {
				setBillNoteId(null);
			}
		} catch (Exception e) {
			Log.out.error("EditBillNote.validateBillNoteVsEmployee() "+e.getClass().getName()+" "+e.getMessage());
			errors.add("employee", new SimpleError(e.getClass().getName()+" "+e.getMessage()));
		} finally { close(); }
	}

	@ValidationMethod(on="save")
	public void validateAmounts(ValidationErrors errors) {
		if (getBaseAmount()!=null && getVatPct()!=null) {
			if (getVatPct()==BigDecimal.ZERO) {
				setVat(BigDecimal.ZERO);
			} else {
				setVat(getBaseAmount().multiply(getVatPct()));
			}
			if (getTotalAmount()==null) {
				setTotalAmount(getBaseAmount().add(getVat()));
			} else {
				if (getTotalAmount().subtract(getBaseAmount().add(getVat())).abs().floatValue()>0.02f) {
					errors.add("importe", new LocalizableError("com.zesped.action.EditBillNote.baseTotalVatMismatch", getBaseAmount().add(getVat()).toString(), getTotalAmount().toString()));
				}
			}
		}
	}
	
	public Resolution save() {
		  try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			Ticket tckt = new Ticket(getSession().getDms(), getId());
	    	tckt.put("accounting_uuid", getAccount().getUuid());
	    	tckt.put("accounting_code", getAccount().getCode());
	    	tckt.put("accounting_desc", getAccount().getDescription());
	    	tckt.isOpen(getOpen());
	    	tckt.isProcessed(getProcessed());
	    	tckt.setBaseAmount(getBaseAmount());
			tckt.setBillerTaxPayer(getBillerTaxPayer());
			tckt.setConcept(getDescription());
			tckt.setComments(getComments());
			tckt.setCurrency(getCurrency());
			tckt.setPaymentMean(getPaymentMean());
			tckt.setTaxPayer(getRecipientTaxPayer());
			tckt.setTicketNumber(getNumber());
			tckt.setTotalAmount(getTotalAmount());
			tckt.setVat(getVat());
			tckt.setVatPct(getVatPct());
			if (getBillDay()>0 && getBillMonth()>0 && getBillYear()>0) {
	  			  tckt.setTicketDate(new Date(getBillYear()-1900, getBillMonth()-1, getBillDay()));	
			} else {
				tckt.remove("year");
				tckt.remove("month");
				tckt.remove("ticket_date");
			}
			if (getBillNoteId()!=null) {
				if (!getBillNoteId().equals(tckt.billNote(getSession()).id())) {
					tckt.changeBillNote(getSession(), getBillNoteId());
				}
			}
			tckt.save(getSession(), getSessionAttribute("user_uuid"), getProcessed(), false);
			disconnect();
			return new RedirectResolution(ListNewBillNotes.class);
		  } catch (StorageException e) {
			setParam("id", getId());
		    Log.out.error("EditBillNote.save() "+e.getClass().getName()+" "+e.getMessage());
			getContext().getMessages().add(new SimpleMessage("ERROR "+e.getMessage()));
			return new RedirectResolution(CaptureBillNote.class);
		  } finally { close(); }
		}

	public Resolution reopen() {
		  try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			Ticket tckt = new Ticket(getSession().getDms(), getId());
			tckt.reopen(getSession(), getSessionAttribute("user_uuid"));
			disconnect();
		  } catch (StorageException e) {
			setParam("id", getId());
		    Log.out.error("EditBillNote.reopen() "+e.getClass().getName()+" "+e.getMessage());
			getContext().getMessages().add(new SimpleMessage("ERROR "+e.getMessage()));
		  } finally { close(); }
		  return new RedirectResolution("/EditBillNote.action?id="+getId());
		}
	
}
