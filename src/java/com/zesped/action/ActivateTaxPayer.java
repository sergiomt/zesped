package com.zesped.action;

import java.util.ArrayList;
import java.util.Collection;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Countries;
import com.zesped.model.Country;
import com.zesped.model.CustomerAccount;
import com.zesped.model.State;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.doc.volumes.Volume;
import es.ipsa.atril.exceptions.ElementNotFoundException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;


public class ActivateTaxPayer extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/activatetaxpayer.jsp";

	protected TaxPayer txp;

	public ActivateTaxPayer() {
		setTaxPayer(new TaxPayer());
	}

	@Validate(on="save", converter=TaxPayer.class)
	public TaxPayer getTaxPayer() {
		return txp;
	}

	public void setTaxPayer(TaxPayer t) {
		txp = t;
	}
	
	public Collection<Country> getCountries() {
		return Countries.list();
	}

	public Collection<State> getStates() {
		Collection<State> oStates = null;
		if (txp!=null) {
			if (!txp.isNull("country")) {
				try {
					connect();
		    		Country oCntr = Countries.top(getSession()).getCountry(getSession(), txp.getString("country"));
		    		oStates = oCntr.states(getSession()).list(getSession());
		    		disconnect();
		    	  } catch (Exception xcpt) {
		    		Log.out.error(xcpt.getMessage(), xcpt);
		    	  } finally {
		    		close();
		    	  }							
			} else
				oStates = new ArrayList<State>();
		} else
			oStates = new ArrayList<State>();
		return oStates;
	}
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@ValidationMethod(on="save")
	public void validateEmail(ValidationErrors errors) {
		final String sEmail = getTaxPayer().getEmail();
		if (sEmail!=null)
	    	  if (sEmail.length()>0 && !Gadgets.checkEMail(sEmail))
	    		  errors.add("email", new LocalizableError("com.zesped.action.SaveClient.invalidEmail"));
	}

	@ValidationMethod(on="save")
	public void validateId(ValidationErrors errors) {
		final String sId = getTaxPayer().getId();
		if (sId==null) {
		  Log.out.error("ActivateTaxPayer.validateId() TaxPayer id is null");
		  errors.add("Id", new LocalizableError("com.zesped.action.ActivateTaxPayer.id.valueNotPresent"));
		} else if (sId.length()==0) {
		  Log.out.error("ActivateTaxPayer.validateId() TaxPayer id is empty");
	      errors.add("Id", new LocalizableError("com.zesped.action.ActivateTaxPayer.id.valueNotPresent"));
		}
	}
	
	@ValidationMethod(on="save")
	public void validateTaxId(ValidationErrors errors) {
		final String sTaxId = getTaxPayer().getTaxId();
		if (sTaxId!=null)
	    	  if (sTaxId.trim().length()==0)
	    		  errors.add("CIF", new LocalizableError("com.zesped.action.ActivateTaxPayer.taxId.valueNotPresent"));
	}
	
	@ValidationMethod(on="save")
	public void validateBusinessName(ValidationErrors errors) {
		final String sBusinessName = getTaxPayer().getBusinessName().toUpperCase();
		final String sTaxId = getTaxPayer().getTaxId();
		final String sFormerId = getTaxPayer().getId();
		Log.out.debug("Validating TaxPayer with business name "+sBusinessName+" and tax id. "+sTaxId+" for former id "+sFormerId);
		if (sBusinessName==null) {
			errors.add("business name", new LocalizableError("com.zesped.action.SaveClient.businessName.valueNotPresent"));
		} else if (sBusinessName.length()==0) {
			errors.add("business name", new LocalizableError("com.zesped.action.SaveClient.businessName.valueNotPresent"));
		} else {
			try {
				connect();
				CustomerAccount oAcc = new CustomerAccount(getSession().getDms(), getSessionAttribute("customer_account_docid"));
				TaxPayer oFoundTaxPayer = oAcc.taxpayers(getSession()).seek(getSession(), sBusinessName);
				if (!oFoundTaxPayer.id().equals(sFormerId))
					errors.add("business name", new LocalizableError("com.zesped.action.SaveClient.businessNameAlreadyExists"));
				oFoundTaxPayer = oAcc.taxpayers(getSession()).seek(getSession(), sTaxId);
				if (!oFoundTaxPayer.id().equals(sFormerId))
					errors.add("business name", new LocalizableError("com.zesped.action.SaveClient.taxIdAlreadyExists"));
			} catch (ElementNotFoundException clientnotfound) {
			} catch (Exception e) {
				Log.out.error("ActivateTaxPayer.validateBusinessName("+sBusinessName+") "+e.getClass().getName()+" "+e.getMessage(), e);
			} finally {
				close();
			}
		}
	}

	public Resolution save() {
		Log.out.debug("Begin ActivateTaxPayer.save()");
		Resolution oRes;
		try {
			connect();
			TaxPayer oTxpr = new TaxPayer(getSession().getDms(), getTaxPayer().getId());
			String sFormerTemporaryName = oTxpr.getBusinessName();
			bindObject(getTaxPayer(), oTxpr);
			oTxpr.setRequiresActivation(false);
			oTxpr.save(getSession());
			Volume oVol = oTxpr.getVolume(getSession());
			if (oVol==null) {
				Log.out.debug("TaxPayer has no volume assigned");
			} else {
				Log.out.debug("TaxPayer volume is "+oVol.name());
				if (sFormerTemporaryName.equalsIgnoreCase(oVol.name())) {
					oVol.setDescription(oTxpr.getBusinessName());
					oVol.save();
				}
			}
			disconnect();
			setSessionAttribute("businessname", getTaxPayer().getBusinessName());
			oRes = new RedirectResolution(CaptureInvoice.class);
		} catch (Exception e) {
			close();
			Log.out.error("ActivateTaxPayer.save() "+e.getClass().getName()+" "+e.getMessage(), e);
			getContext().getMessages().add(new SimpleError(e.getClass().getName()+" "+e.getMessage()));
			oRes = new RedirectResolution(ActivateTaxPayer.class);
		}
		Log.out.debug("End ActivateTaxPayer.save()");
		return oRes;
	}
	
	@DefaultHandler
	public Resolution form() {
		Log.out.debug("Begin ActivateTaxPayer.form()");
		try {
  		  connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
  		  Log.out.debug("getTaxPayer().load("+getSessionAttribute("taxpayer_docid")+")");
  		  getTaxPayer().load(getSession(), getSessionAttribute("taxpayer_docid"));
  		  getTaxPayer().setBusinessName("");
  		  disconnect();
  	  	} catch (Exception xcpt) {
  		  Log.out.error(xcpt.getMessage(), xcpt);
  		  return new RedirectResolution("/error.jsp?e=couldnotloadtaxpayer");
  	  	} finally {
  		  close();
  	  	}			
		Log.out.debug("End ActivateTaxPayer.form()");
		return new ForwardResolution(FORM);
	}	

}
