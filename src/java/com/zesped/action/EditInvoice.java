package com.zesped.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import com.knowgate.misc.Calendar;
import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.Invoice;
import com.zesped.model.InvoicePage;
import com.zesped.model.InvoiceThumbnail;
import com.zesped.model.Messages;
import com.zesped.model.User;
import com.zesped.model.VatPercent;
import com.zesped.model.VatPercents;
import com.zesped.util.BigDecimalTypeConverter;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.eventLogger.AtrilEvent.Level;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

public class EditInvoice extends CaptureInvoice {

	private static final String EDITFORM="/WEB-INF/jsp/editinvoice.jsp";
	private static final String APPROVEFORM="/WEB-INF/jsp/editinvoice.jsp";

	private static final Locale ES = new Locale("es","ES");

	private int billday, billmonth, billyear, dueday, duemonth, dueyear;
	
	protected String invoicenum, currency, concept, comments, captureserviceflavor;
	
	private InvoiceThumbnail thumbnail;
	
	private ArrayList<String> pages;

	private BigDecimal totalamount, withholding, withholdingpct;

	private ArrayList<BigDecimal> baseamounts;

	private ArrayList<BigDecimal> vats;

	private ArrayList<BigDecimal> vatpcts;
	
	private boolean processed, approved, mistakes;
	
	public EditInvoice() {
		pages=new ArrayList<String>();
		baseamounts=new ArrayList<BigDecimal>();
		vats=new ArrayList<BigDecimal>();
		vatpcts=new ArrayList<BigDecimal>();
	}

	@Validate(on="save,approve", maxlength=30)
	public String getInvoiceNumber() {
		return invoicenum==null ? "" : invoicenum;
	}

	public void setInvoiceNumber(String n) {
		invoicenum = n;
	}

	@Validate(on="save,approve", maxlength=3)
	public String getCurrency() {
		if (currency==null)
			return "EUR";
		else if (currency.length()==0)
			return "EUR";
		else
			return currency;
	}

	public void setCurrency (String c) {
		currency = c;
	}

	@Validate(on="save,approve", maxlength=50)
	public String getConcept() {
		return concept;
	}

	public void setConcept (String c) {
		concept = c;
	}

	@Validate(on="save,approve,reject", maxlength=2000)
	public String getComments() {
		return comments==null ? "" : comments;
	}
	
	public void setComments (String c) {
		comments = c;
	}
	
    @Validate(on="save,approve", converter=BigDecimalTypeConverter.class)
	public ArrayList<BigDecimal> getBaseAmounts() {
		return baseamounts;
	}

	public void setBaseAmounts (ArrayList<BigDecimal> b) {
		baseamounts = b;
	}
	
    @Validate(on="save,approve", converter=BigDecimalTypeConverter.class)
	public BigDecimal getTotalAmount() {
		return totalamount;
	}

	public void setTotalAmount (BigDecimal t) {
		totalamount = t;
	}

	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getWithholding() {
		return withholding==null ? BigDecimal.ZERO : withholding;
	}

	public void setWithholding(BigDecimal w) {
		withholding = (w==null ? BigDecimal.ZERO : w);
	}

	@Validate(converter=BigDecimalTypeConverter.class)
	public BigDecimal getWithholdingPct() {
		return withholdingpct==null ? BigDecimal.ZERO : withholdingpct;
	}

	public void setWithholdingPct(BigDecimal p) {
		withholdingpct = (p==null ? BigDecimal.ZERO : p);
	}

	public String getVatN(int n) throws NumberFormatException {
		if (vats==null)
			return "";
		else if (n>=vats.size())
			return "";
		else if (vats.get(n)==null)
			return "";
		else
			return Gadgets.formatCurrency(vats.get(n), getCurrency(), ES);
	}

	@Validate(converter=BigDecimalTypeConverter.class)
	public ArrayList<BigDecimal> getVats() throws ArrayIndexOutOfBoundsException {
		vats.clear();
		if (getVatPcts().size()!=getBaseAmounts().size())
			throw new ArrayIndexOutOfBoundsException("Mismatch between VAT percentages and base amounts array sizes. There are "+String.valueOf(getVatPcts().size())+" percentages but "+String.valueOf(getBaseAmounts().size())+" base amounts");
		int a = 0;
		for (BigDecimal oDec : getVatPcts())
			vats.add(oDec.multiply(getBaseAmounts().get(a++)));
		return vats;
	}

	public void setVats (ArrayList<BigDecimal> v) {
		vats = v;
	}
	
	public void setVat (int n, BigDecimal v) {
		vats.set(n, v);
	}
	
	public ArrayList<BigDecimal> getVatPcts() {
		return vatpcts;
	}

	public void setVatPcts (ArrayList<BigDecimal> p) {
		vatpcts = p;
	}
	
	public Collection<VatPercent> getVatPercents() {
		if (getVatPcts()==null)
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

	public int getDueDay() {
		return dueday;
	}

	public void setDueDay(int d) {
		dueday = d;
	}

	public int getDueMonth() {
		return duemonth;
	}

	public void setDueMonth(int m) {
		duemonth = m;
	}

	public int getDueYear() {
		return dueyear;
	}

	public void setDueYear(int y) {
		dueyear = y;
	}

	public boolean getProcessed() {
		return getApproveMode() ? true : processed;
	}

	public void setProcessed(boolean p) {
		processed = p;
	}

	public boolean getApproved() {
		return approved;
	}

	public String getFormerId() {
		return getParam("former_id");
	}
	
	public boolean getApproveMode() {
		Log.out.debug("EditInvoice.getParam(\"a\", \"0\")="+getParam("a", "0"));
		return getParam("a", "0").equals("1");
	}

	public void setApproved(boolean a) {
		approved = a;
	}

	public boolean getMistakes() {
		return mistakes;
	}

	public void setMistakes(boolean m) {
		mistakes = m;
	}

	public String getServiceFlavor() {
		return captureserviceflavor;
	}

	public void setServiceFlavor(String sCaptureServiceFlavor) {
		captureserviceflavor = sCaptureServiceFlavor;
	}
	
	public InvoiceThumbnail getThumbnail() {
		Log.out.debug("InvoiceThumbnail getThumbnail("+getId()+")");
		if (thumbnail==null) {
			try {
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
		  		Invoice oInv = new Invoice(getSession().getDms(), getId());
		  		thumbnail = oInv.thumbnail(getSession());
		  	} catch (Exception xcpt) {
		  		  Log.out.error(xcpt.getMessage(), xcpt);
		  	} finally {
		  		close();
		  	}			
		} // fi	
		return thumbnail;
	}

	@Validate(converter=InvoiceThumbnail.class)
	public void setThumbnail(InvoiceThumbnail t) {
		thumbnail = t;
	}
	
	public ArrayList<String> getPages() {
		return pages;
	}

	@ValidationMethod(on="reject")
	public void validateComments(ValidationErrors errors) {
		if (getComments().trim().length()==0)
			errors.add("password", new LocalizableError("com.zesped.action.EditInvoice.rejectionCauseIsRequired"));
	}
	
	@ValidationMethod(priority=-1)
	public void validateId() {
	  Log.out.debug("setParam(id, "+getParam("id")+")");;
	  setParam("id", getParam("id"));		
	}

	@ValidationMethod(on="save")
	public void validateBillerAndRecipient(ValidationErrors errors) {
		Log.out.debug("EditInvoice.validateBillerAndRecipient()");
		if (getBillerTaxPayer().length()>0 && getRecipientTaxPayer().length()>0 && getBillerTaxPayer().equals(getRecipientTaxPayer())) {
			errors.add("emisor", new LocalizableError("com.zesped.action.EditInvoice.BillerAndRecipientMayNotBeTheSame"));
		}
	}

	@ValidationMethod(on="save")
	public void validateBillDate(ValidationErrors errors) {
		Log.out.debug("EditInvoice.validateBillDate()");
		if (getBillDay()>0 || getBillMonth()>0 || getBillYear()>0) {
			if (getBillDay()==0 || getBillMonth()==0 || getBillYear()==0)
				errors.add("fechaFactura", new LocalizableError("com.zesped.action.EditInvoice.invalidBillDate"));
			else if (getBillDay()>Calendar.LastDay(getBillMonth()-1, getBillYear())) {
				errors.add("fechaFactura", new LocalizableError("com.zesped.action.EditInvoice.invalidBillDate"));				
			}	
		}
	}

	@ValidationMethod(on="save")
	public void validateDueDate(ValidationErrors errors) {
		Log.out.debug("EditInvoice.validateDueDate()");
		if (getDueDay()>0 || getDueMonth()>0 || getDueYear()>0) {
			if (getDueDay()==0 || getDueMonth()==0 || getDueYear()==0)
				errors.add("fechaVencimiento", new LocalizableError("com.zesped.action.EditInvoice.invalidDueDate"));
			else if (getDueDay()>Calendar.LastDay(getDueMonth()-1, getDueYear())) {
				errors.add("fechaVencimiento", new LocalizableError("com.zesped.action.EditInvoice.invalidDueDate"));				
			}	
		}
	}

	@ValidationMethod(on="save")
	public void validateAmounts(ValidationErrors errors) {
		Log.out.debug("EditInvoice.validateAmounts()");
		if (getBaseAmounts()!=null && getVatPcts()!=null) {
			BigDecimal oBasesSum = new BigDecimal("0");
			for (BigDecimal oBase : getBaseAmounts())
				oBasesSum = oBasesSum.add(oBase);
			BigDecimal oVatSum = new BigDecimal("0");
			for (BigDecimal oVat : getVats())
				oVatSum = oVatSum.add(oVat);
			if (getTotalAmount()==null) {
				setTotalAmount(oBasesSum.add(oVatSum));
			} else {
				Log.out.debug("bases count = "+getBaseAmounts().size());
				Log.out.debug("vats count = "+getVats().size());
				Log.out.debug("vat pcts count = "+getVatPcts().size());
				Log.out.debug("base amount = "+oBasesSum);
				Log.out.debug("vat = "+oVatSum);
				Log.out.debug("withholding = "+getWithholding());
				Log.out.debug("total amount = "+getTotalAmount());
				BigDecimal oComputedTotal = oBasesSum.add(oVatSum).subtract(getWithholding());
				Log.out.debug("computed total = "+oComputedTotal);
				if (getTotalAmount().subtract(oComputedTotal).abs().floatValue()>0.04f) {
					errors.add("importe", new LocalizableError("com.zesped.action.EditInvoice.baseTotalVatMismatch", oBasesSum.add(oVatSum).toString(), getTotalAmount().toString()));
				}
			}				
		}
	}

	@ValidationMethod(on="save")
	public void validateCompleteness(ValidationErrors errors) {
		Log.out.debug("EditInvoice.validateCompleteness()");
		if (getProcessed() || getApproved()) {
			if (getBillerTaxPayer().length()==0 || getInvoiceNumber().length()==0 || getTotalAmount()==null)
				errors.add("proceso", new LocalizableError("com.zesped.action.EditInvoice.notComplete"));
			if (getBillDay()==0 || getBillMonth()==0 || getBillYear()==0)
				errors.add("fechaFactura", new LocalizableError("com.zesped.action.EditInvoice.invalidBillDate"));
			else if (getBillDay()>Calendar.LastDay(getBillMonth()-1, getBillYear())) {
				errors.add("fechaFactura", new LocalizableError("com.zesped.action.EditInvoice.invalidBillDate"));				
			}
		}
	}
	
	private Invoice saveInvoice() {
		  Log.out.debug("Begin EditInvoice.saveInvoice()");
		  Invoice invc = null;
		  try {
	  		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
	  		  invc = new Invoice(getSession().getDms(), getId());
	  		  invc.setBaseAmounts(getBaseAmounts());
	  		  bindObject(this,invc);
	  		  if (getBillDay()>0 && getBillMonth()>0 && getBillYear()>0) {
	  			  invc.setInvoiceDate(new Date(getBillYear()-1900, getBillMonth()-1, getBillDay()));
	  		  } else {
	  			  invc.remove("year");
	  			  invc.remove("quarter");
	  			  invc.remove("month");
	  			  invc.remove("invoice_date");
	  		  }
	  		  if (getDueDay()>0 && getDueMonth()>0 && getDueYear()>0)
	  			  invc.setDueDate(new Date(getDueYear()-1900, getDueMonth()-1, getDueDay()));
	  		  invc.save(getSession(), getSessionAttribute("user_uuid"), getProcessed(), getApproved(), getMistakes());
	  		  BigDecimal total = invc.computeTotal();
	  		  if (getTotalAmount().subtract(total).abs().floatValue()>0.04f) {
	  			  Log.out.warn("Invoice "+invc.id()+" mismatch between supplied ("+getTotalAmount().toString()+") and computed ("+total.toString()+") total amounts");
	  			  DAO.log(getSession(), invc.getDocument(), Invoice.class, "AMOUNT MISMATCH", Level.WARNING, "mismatch between supplied ("+getTotalAmount().toString()+") and computed ("+total.toString()+") total amounts");
	  		  }
	  		  disconnect();	  			  
	  	  } catch (Exception xcpt) {
	  		  Log.out.error(xcpt.getMessage(), xcpt);
	  	  } finally {
	  		  close();
	  	  }
		  Log.out.debug("End EditInvoice.saveInvoice()");
		  return invc;
	}

	@DefaultHandler
	public Resolution form() {
	  setId(getParam("id"));
	  String sFwd;
	  Log.out.debug("EditInvoice.form("+getId()+")");
	  if (getId()==null) {
		  sFwd = "error.jsp?e=parameternotset";
	  } else if (getId().length()==0) {
		  sFwd = "error.jsp?e=parameternotset";
	  } else {
		  setParam("id", getId());
		  Date dtToday = new Date();
		  try {
	  		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
	  		  Invoice oInv = new Invoice(getSession().getDms(), getId());
	  		  bindObject(oInv,this);
	  		  setProcessed(oInv.isProcessed());
	  		  setApproved(oInv.isApproved());
	  		  setMistakes(oInv.hasMistakes());
	  		  for (InvoicePage p : oInv.pages(getSession()))
	  			  pages.add(p.id());
	  		  if (oInv.getInvoiceDate()==null) {
	  			  setBillMonth(dtToday.getMonth()+1);
	  			  setBillYear(dtToday.getYear()+1900);
	  		  } else {
	  			  setBillDay(oInv.getInvoiceDate().getDate());
	  			  setBillMonth(oInv.getInvoiceDate().getMonth()+1);
	  			  setBillYear(oInv.getInvoiceDate().getYear()+1900);
	  		  }
	  		  if (oInv.getDueDate()!=null) {
	  			  setDueDay(oInv.getDueDate().getDate());
	  			  setDueMonth(oInv.getDueDate().getMonth()+1);
	  			  setDueYear(oInv.getDueDate().getYear()+1900);
	  		  }
	  		  disconnect();
	  		  sFwd = getParam("a","0").equals("1") ? APPROVEFORM : EDITFORM;
	  	  } catch (Exception xcpt) {
	  		  Log.out.error(xcpt.getMessage(), xcpt);
	  		  sFwd = "error.jsp?e=couldnotloadinvoice";
	  	  } finally {
	  		  close();
	  	  }			
		  Log.out.debug("EditInvoice forward "+sFwd);
	  }
	  return new ForwardResolution(sFwd);
	}

	@DontValidate
	public Resolution cancel() {
		return new RedirectResolution(CaptureInvoice.class);
	}
	
	public Resolution save() {
		Log.out.debug("Begin EditInvoice.save()");
		Invoice oInv = saveInvoice();
		Dms oDms;
		Resolution oRes = new RedirectResolution(ListNewInvoices.class);
		try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			oDms = getSession().getDms();
			SortableList<Document> oNext = oDms.query("(Invoice$is_processed='0') & (Invoice$recipient_taxpayer='"+getRecipientTaxPayer()+"')");
			if (!oNext.isEmpty()) {
				oRes = new RedirectResolution("/EditInvoice.action?a=0&id="+oNext.get(0).id());
				setParam("former_id", getId());
			}
			disconnect();
			if (oInv.hasMistakes()) {
				connect();
				User oMe = new User(getSession(), getSessionAttribute("user_docid"));
				User oTo = new User(getSession(), User.forUuid(oInv.getString("captured_by")));
				Messages.notify(getSession(), oMe.getFirstName()+" "+oMe.getLastName(),
						  		oMe.getEmail(), oMe.getNickName(), oTo.getEmail(), oTo.getNickName(),
						  		"Incidencia con la factura "+getId(), oInv.getComments(), getId());			
				disconnect();
			}
		} catch (Exception e) {
			Log.out.error("Invoice.save() "+e.getClass().getName()+" "+e.getMessage());
		} finally {
			close();
		}
		Log.out.debug("End EditInvoice.save()");
		return oRes;		  
	}

	public Resolution approve() {
		  Log.out.debug("EditInvoice.approve()");
		  setApproved(true);
		  setProcessed(true);
		  setMistakes(false);
		  saveInvoice();
		  Dms oDms;
		  Resolution oRes = new RedirectResolution(ListPendingInvoices.class);
		  try {
			  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			  oDms = getSession().getDms();
			  SortableList<Document> oNext = oDms.query("(Invoice$is_processed='1') & (Invoice$is_approved='0') & (Invoice$recipient_taxpayer='"+getRecipientTaxPayer()+"')");
			  if (!oNext.isEmpty()) {
				  oRes = new RedirectResolution("/EditInvoice.action?a=1&id="+oNext.get(0).id());
				  setParam("former_id", getId());
			  }
			  disconnect();
			} catch (Exception e) {
				Log.out.error("Invoice.approve()"+e.getClass().getName()+" "+e.getMessage());
			} finally {
				close();
		  }
		  return oRes;
	}

	public Resolution reject() {
		  Log.out.debug("Begin EditInvoice.reject()");
		  setApproved(false);
		  setProcessed(false);
		  setMistakes(true);
		  Invoice oInv = saveInvoice();
		  Dms oDms;
		  Resolution oRes = new RedirectResolution(ListPendingInvoices.class);
		  try {
			  connect();
			  oDms = getSession().getDms();
			  Log.out.debug("Dms.query((Invoice$is_processed='1') & (Invoice$is_approved='0') & (Invoice$recipient_taxpayer='"+getRecipientTaxPayer()+"'))");
			  SortableList<Document> oNext = oDms.query("(Invoice$is_processed='1') & (Invoice$is_approved='0') & (Invoice$recipient_taxpayer='"+getRecipientTaxPayer()+"')");
			  if (!oNext.isEmpty()) {
				  oRes = new RedirectResolution("/EditInvoice.action?a=1&id="+oNext.get(0).id());
				  setParam("former_id", getId());
			  }
			  User oMe = new User(getSession(), getSessionAttribute("user_docid"));
			  User oTo = new User(getSession(), User.forUuid(oInv.getString("captured_by")));
			  Messages.notify(getSession(), oMe.getFirstName()+" "+oMe.getLastName(),
					  		  oMe.getEmail(), oMe.getNickName(), oTo.getEmail(), oTo.getNickName(),
					  		  "Incidencia con la factura "+getId(), oInv.getComments(), getId());
			  disconnect();
			} catch (Exception e) {
				Log.out.error("Invoice.reject()"+e.getClass().getName()+" "+e.getMessage());
			} finally {
				close();
		  }
		  Log.out.debug("End EditInvoice.reject()");
		  return oRes;
	}
	
}
