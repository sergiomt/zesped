package com.zesped.action;

import java.util.ArrayList;
import java.util.Collection;

import com.knowgate.storage.StorageException;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.AccountingAccount;
import com.zesped.model.AccountingAccounts;
import com.zesped.model.CaptureService;
import com.zesped.model.Client;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.validation.Validate;

public class CaptureBillNote extends BaseEditBean {

	private static final String FORM="/WEB-INF/jsp/capturebillnote.jsp";

	private String recipient, biller;

	private AccountingAccount account;

	public Collection<Client> getClients() throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		AtrilSession oSes = DAO.getAdminSession("CaptureInvoice");
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

	private String employeeuuid;
	
	public String getEmployee() {
		Log.out.debug("CaptureBillNote.getEmployee() : "+employeeuuid);
		return employeeuuid;
	}

	public void setEmployee(String uuid) {
		Log.out.debug("CaptureBillNote.setEmployee("+uuid+")");
		employeeuuid = uuid;
	}
	
	public Collection<Employee> getEmployees() throws StorageException {
		Collection<Employee> oEmps;
		try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			TaxPayer oTxp = new TaxPayer(getSession().getDms().getDocument(getRecipientTaxPayer()));
			oEmps = oTxp.employees(getSession()).list(getSession());
		} catch (Exception xcpt) {
			Log.out.error(xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
			oEmps = new ArrayList<Employee>();
		} finally {
			disconnect();			
		}
		return oEmps;
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
	
	@Validate(converter=AccountingAccounts.class)
	public AccountingAccount getAccount() {
		return account;
	}

	public void setAccount(AccountingAccount a) {
		account = a;
	}
	
	@DefaultHandler
	public Resolution form() {
		Log.out.debug("CaptureBillNote.form()");
		try {
			setRecipientTaxPayer(getSessionAttribute("taxpayer_docid"));
			setEmployee(getSessionAttribute("employee_uuid"));
			 if (getSessionAttribute("incoming_deposits")==null) {
			   connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			   TaxPayer oTxp = new TaxPayer (getSession().getDms(), getSessionAttribute("taxpayer_docid"));
			   setSessionAttribute("incoming_deposits", oTxp.incomingDeposits(getSession()));
			   disconnect();
			}
			return new ForwardResolution(FORM);
		} catch (Exception e) {
		  Log.out.error("CaptureBillNote.form() "+e.getClass().getName()+" "+e.getMessage());
		  getContext().getMessages().add(new SimpleMessage("ERROR "+e.getMessage()));
		  return new RedirectResolution(CaptureBillNote.class);
		} finally { close(); }
	}

}
