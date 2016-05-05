package com.zesped.action;


import java.util.List;

import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.knowgate.storage.StorageException;
import com.zesped.Log;
import com.zesped.model.CaptureServiceFlavor;
import com.zesped.model.Invoice;
import com.zesped.model.InvoicePage;
import com.zesped.model.Invoices;
import com.zesped.model.TaxPayer;
import com.zesped.util.ThumbnailCreator;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;

public class UploadInvoice extends EditInvoice {

	private static final String FORM="/WEB-INF/jsp/uploadinvoice.jsp";
	
	private String capureddocid;
	
	public String getCapturedDocId() {
		return capureddocid;
	}
	
	private String capuredpage1;

	public String getCapturedPage1() {
		return capuredpage1;
	}

	private int capturedCount;

	public int getCapturedCount() {
		return capturedCount;
	}
	
	private String serviceflavor;

	@Validate(required=true,on="upload")
	public String getServiceFlavor() {
		return serviceflavor;
	}

	public void setServiceFlavor(String sFlavor) {
		serviceflavor = sFlavor;
	}
		
	private List<FileBean> items;

	public List<FileBean> getItems() {
		return items;	
	}
	
	public void setItems(List<FileBean> fbeans) {
		items = fbeans;
	}

	@ValidationMethod(on="upload")
	public void requireOneItem(ValidationErrors errors) {
		if (getItems()==null)
			errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.oneItemIsRequired"));
		else if (getItems().size()==0)
			errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.oneItemIsRequired"));
	}

	@ValidationMethod(on="upload")
	public void checkClientAndSupplier(ValidationErrors errors) {
		try {
			if (getRecipientTaxPayer().length()==0) {
				errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.clientIsRequired"));
			} else if (getBillerTaxPayer().length()>0) {
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				Dms oDms = getSession().getDms();
				Document r,b;
				try {
				  r = oDms.getDocument(getRecipientTaxPayer());
				  try {
					b = oDms.getDocument(getBillerTaxPayer());
					if (r.type().name().equals(b.type().name()))
						errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.incompatibleClientAndSupplier"));
				  } catch (NumberFormatException nfe) {
					Log.out.error("NumberFormatException getting biller "+getBillerTaxPayer(), nfe);
					errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.incompatibleClientAndSupplier"));
			  	  }
				} catch (NumberFormatException nfe) {
					Log.out.error("NumberFormatException getting recipient "+getRecipientTaxPayer(), nfe);
					errors.add("items", new LocalizableError("com.zesped.action.InvoceUpload.incompatibleClientAndSupplier"));
				}
				disconnect();
				
			}
		} catch (StorageException e) { }
	}
	
	public Resolution upload() throws Exception {
		Invoice invc = null;
		capturedCount = 0;
		final int nItems = (getItems()==null ? 0 : getItems().size());
		if (nItems>0) {
			Log.out.debug("InvoiceUpload "+String.valueOf(nItems)+" items found");
			try {
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));

				Dms oDms = getSession().getDms();
				Document rcpt = oDms.getDocument(getRecipientTaxPayer());
				String sTaxPayerId = rcpt.type().name().equals("TaxPayer") ? getRecipientTaxPayer() : getSessionAttribute("taxpayer_docid");
				TaxPayer txpy = new TaxPayer(getSession().getDms(), sTaxPayerId);

				Invoices invs = txpy.invoices(getSession());
				
				invc = invs.create(getSession(), getSessionAttribute("user_uuid"), getServiceFlavor(),
								   sTaxPayerId, getBillerTaxPayer(), getRecipientTaxPayer());
				capureddocid = invc.id();
				
				for (FileBean attachment : getItems()) {
					if (attachment != null) {
						if (attachment.getSize() > 0) {
							InvoicePage page = invc.createPage(getSession(), attachment.getInputStream(), ++capturedCount, attachment.getFileName());
							if (capturedCount==1) {
								capuredpage1 = page.id();
							}
							attachment.delete();
						} else {
						  ValidationError error = new SimpleError(attachment.getFileName()+ " is not a valid file." );
						  getContext().getValidationErrors().add("items" , error);
					  }
					}
			    } // next
				disconnect();
				Log.out.debug("Done uploading items");
				if (capturedCount>0) {
					ThumbnailCreator.createThumbnailFor(capureddocid, capuredpage1);
				}
			} catch (Exception e) {
				Log.out.error("InvoiceUpload.upload() "+e.getClass().getName()+" "+e.getMessage(), e);
				getContext().getMessages().add(new SimpleMessage("ERROR "+e.getMessage(), items));		  
			} finally { close(); }
			
		} else {
			Log.out.debug("InvoiceUpload no items found");
		}
		if (invc==null)
			return new RedirectResolution("/error.jsp");
		if (invc.getServiceFlavor().equals(CaptureServiceFlavor.BASIC))
			return new ForwardResolution("EditInvoice.action?id="+invc.id());
		else
			return new ForwardResolution(FORM);
	}
	
}
