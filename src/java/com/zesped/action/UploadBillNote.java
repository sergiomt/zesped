package com.zesped.action;

import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.zesped.Log;
import com.zesped.action.EditBillNote;
import com.zesped.model.AccountingAccount;
import com.zesped.model.AccountingAccounts;
import com.zesped.model.BillNote;
import com.zesped.model.Concept;
import com.zesped.model.TaxPayer;
import com.zesped.model.Ticket;
import com.zesped.model.TicketNote;
import com.zesped.util.ThumbnailCreator;

public class UploadBillNote extends EditBillNote {

	private static final String FORM="enter.jsp?e=badhandler";
	
	private List<FileBean> items;

	public List<FileBean> getItems() {
		return items;	
	}
	
	public void setItems(List<FileBean> fbeans) {
		items =  fbeans;
	}

	@Override
	@Validate(required=true,on="upload")
	public String getRecipientTaxPayer() {
		return super.getRecipientTaxPayer();
	}
	
	@Override
	@Validate(required=true, on="upload")
	public String getConcept() {
		return concept;
	}

	@Override
	public void setConcept(String c) {
		concept = new Concept(c==null ? "" : c, new Date()).getName();
	}

	@Override
	@Validate(required=true, on="upload",converter=AccountingAccounts.class)
	public AccountingAccount getAccount() {
		return super.getAccount();
	}

	@Override
	public void setAccount(AccountingAccount a) {
		super.setAccount(a);
	}
	
	@ValidationMethod(on="upload")
	public void requireOneItem(ValidationErrors errors) {
		if (getItems()==null)
			errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.oneItemIsRequired"));
		else if (getItems().size()==0)
			errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.oneItemIsRequired"));
	}
	
	@Override
	@ValidationMethod(on="upload")
	public void validateBillNoteVsEmployee(ValidationErrors errors) {
		super.validateBillNoteVsEmployee(errors);
	}

	public Resolution upload() throws Exception {
		String sFwd;
		Ticket tckt = null;
		BillNote bill = null;
		setCapturedCount(0);
		final int nItems = (getItems()==null ? 0 : getItems().size());
		if (nItems>0) {
			Log.out.debug("UploadBillNote "+String.valueOf(nItems)+" items found");
			try {
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));

				TaxPayer txpy = new TaxPayer(getSession().getDms(), getRecipientTaxPayer());

				bill = txpy.billnotes(getSession()).forConcept(getSession(), getConcept(), getEmployee());
				setCapturedPage1("");

				for (FileBean attachment : getItems()) {
					if (attachment != null) {
						if (attachment.getSize() > 0) {
							if (tckt==null) {
								tckt = bill.createTicket(getSession(), getAccount());
								setId(tckt.id());
								FlashScope oFscope = FlashScope.getCurrent(getContext().getRequest(), true);
								oFscope.put("ticket_docid", tckt.id());
							}
							TicketNote note = tckt.createNote(getSession(), attachment.getInputStream(), incCapturedCount(), attachment.getFileName());
							if (getCapturedPage1().length()==0) setCapturedPage1(note.id());
							attachment.delete();
						} else {
						  ValidationError error = new SimpleError(attachment.getFileName()+ " is not a valid file." );
						  getContext().getValidationErrors().add("items" , error);
					  }
					}
			    } // next
				disconnect();
				if (getCapturedCount()>0)
					ThumbnailCreator.createThumbnailFor(getId(), getCapturedPage1());				
				sFwd = "EditBillNote.action?id="+tckt.id();
			} catch (IllegalStateException e) {
				Log.out.error("BillNoteUpload.upload() "+e.getClass().getName()+" "+e.getMessage(), e);
				getContext().getMessages().add(new SimpleMessage("ERROR "+e.getMessage(), items));
				sFwd = "error.jsp?e=couldnotloadticket";
			} finally { close(); }
			
		} else {
			Log.out.debug("UploadBillNote no items found");
			sFwd = "error.jsp?e=couldnotloadticket";
		}
		return new ForwardResolution(sFwd);
	}
	
	@DefaultHandler
	public Resolution form() {
	  Log.out.debug("UploadBillNote.form()");
	  return new RedirectResolution(FORM);
	}
	
}