package com.zesped.action;

import java.util.Collection;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.CaptureService;
import com.zesped.model.CaptureServiceFlavor;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Client;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;

public class CaptureInvoice extends BaseEditBean {

	private static final String FORM="/WEB-INF/jsp/captureinvoice.jsp";
	
	private String recipient, biller, scandevice, capturetype, serviceflavor;
	
	private boolean fullduplex, gui, sign, serversign, multipage;

	public Collection<Client> getClients() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		Log.out.debug("Begin CaptureInvoice.getClients("+getSessionAttribute("customer_account_docid")+")");
		Collection<Client> clients = null;
		try {
			clients = (Collection<Client>) Cache.getObject(getSessionAttribute("customer_account_docid")+"clients");
		} catch (Exception e) {
			Log.out.error("CaptureInvoice.getClients() Cache.getObject("+getSessionAttribute("customer_account_docid")+"clients) "+e.getClass().getName()+" "+e.getMessage(), e);
		}
		if (null==clients) {
			AtrilSession oSes = DAO.getAdminSession("CaptureInvoice");
			CustomerAccount oAcc = new CustomerAccount(oSes.getDms().getDocument(getSessionAttribute("customer_account_docid")));
			clients = oAcc.clients(oSes).list(oSes);
			for (Client oCli : clients)
				if (oCli.getTaxId().length()>0)
					oCli.setBusinessName(oCli.getBusinessName()+" ("+oCli.getTaxId()+")");
			oSes.disconnect();
			oSes.close();
			try {
				Cache.putEntry(getSessionAttribute("customer_account_docid")+"clients", clients);
			} catch (Exception e) {
				Log.out.error("CaptureInvoice.getClients() Cache.putEntry("+getSessionAttribute("customer_account_docid")+"clients) "+e.getClass().getName()+" "+e.getMessage(), e);
			}
		}
		Log.out.debug("End CaptureInvoice.getClients("+getSessionAttribute("customer_account_docid")+")");
		return clients;
	}
	
	@Override
	public CaptureService getCaptureService() {
		return CaptureService.INVOICES;
	}

	@Validate(required=true,on="save")
	public String getRecipientTaxPayer() {
		if (recipient==null)
			return getSessionAttribute("taxpayer_docid");
		else if (recipient.length()==0)
			return getSessionAttribute("taxpayer_docid");
		else
			return recipient;
	}

	public void setRecipientTaxPayer(String r) {
		recipient = r;
	}

	public String getBillerTaxPayer() {
		return biller==null ? "" : biller;
	}

	public void setBillerTaxPayer(String b) {
		biller = b;
	}

	public String getIncomingDeposits() {
		return getSessionAttribute("incoming_deposits");
	}
	
	public String getScanDeviceName() {
		return scandevice;
	}

	public void setScanDeviceName(String s) {
		scandevice = s;
	}
	
	public String getCaptureType() {
		return capturetype;
	}

	public void setCaptureType(String t) {
		capturetype = t;
	}
	
	public boolean getFullDuplex() {
		return fullduplex;
	}

	public void setFullDuplex(boolean b) {
		fullduplex = b;
	}

	public boolean getGUI() {
		return gui;
	}

	public void setGUI(boolean g) {
		gui = g;
	}

	public boolean getSign() {
		return sign;
	}

	public void setSign(boolean s) {
		sign = s;
	}

	public boolean getServerSign() {
		return serversign;
	}

	public void setServerSign(boolean s) {
		serversign = s;
	}

	public boolean getMultiPage() {
		return multipage;
	}

	public void setMultiPage(boolean p) {
		multipage = p;
	}
	
	public String getServiceFlavor() {
		return serviceflavor==null ? CaptureServiceFlavor.BASIC : serviceflavor;
	}

	@DefaultHandler
	public Resolution form() {
	  AtrilSession oSes = DAO.getAdminSession("CaptureInvoice");
	  User oUsr = new User();
	  try {
		  oUsr.load(oSes, getSessionAttribute("user_docid"));
		  setScanDeviceName(oUsr.defaultScanner(oSes).name());
		  setCaptureType(oUsr.defaultCaptureType(oSes).name());
		  if (getSessionAttribute("incoming_deposits")==null) {
			  TaxPayer oTxp = new TaxPayer (oSes.getDms(), getSessionAttribute("taxpayer_docid"));
			  setSessionAttribute("incoming_deposits", oTxp.incomingDeposits(oSes));
		  }
		  oSes.disconnect();
		  oSes.close();
	  } catch (Exception xcpt) {
		  Log.out.error("CaptureInvoice.form() "+xcpt.getClass().getName()+" "+xcpt.getMessage());
		  setScanDeviceName("Twain");
		  setCaptureType("UnsignedSinglePageFullDuplexNoGUI");
		  if (oSes.isConnected()) oSes.disconnect();
		  if (oSes.isOpen()) oSes.close();
	  }
	  setFullDuplex(getCaptureType().indexOf("FullDuplex")>0);
	  setGUI(getCaptureType().indexOf("NoGUI")<0);
	  setSign(getCaptureType().startsWith("Signed"));
	  setServerSign(getCaptureType().startsWith("ServerSigned"));
	  setMultiPage(getCaptureType().indexOf("MultiPage")>0);
	  serviceflavor = oUsr.defaultCaptureServiceFlavor().getString("captureservice");
	  return new ForwardResolution(FORM);
	}

}