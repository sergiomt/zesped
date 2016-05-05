package com.zesped.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.mail.Message.RecipientType;

import com.knowgate.dataxslt.FastStreamReplacer;
import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.Cache;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.Employees;
import com.zesped.model.Role;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class AsyncSendInvitation extends Thread {

	private String sEmail, sSender1stName, sSender2ndName, sAccId, sRecEmail, sRec1stName, sRec2ndName;
	private String[] aTxprs;
	private boolean bCreateEmp, bApprove, bSettle, bPremium;
	private StringBuffer oTxt, oHtm;

	public AsyncSendInvitation(String sUserEmail, String sRecipientEmail, String sRecipientFirstName, String sRecipientLastName, String sSenderFirstName, String sSenderLastName, String[] aTaxPayers, String sCustomerAccountId, boolean bCreateEmployee, boolean bCanApprove, boolean bCanSettle, boolean bCanPremium, StringBuffer oTxtBody, StringBuffer oHtmBody) {
		sEmail = sUserEmail;
		sRecEmail = sRecipientEmail;
		sRec1stName = sRecipientFirstName;
		sRec2ndName = sRecipientLastName;
		sSender1stName = sSenderFirstName;
		sSender2ndName = sSenderLastName;
		aTxprs = aTaxPayers;
		sAccId = sCustomerAccountId;
		bCreateEmp = bCreateEmployee;
		bApprove = bCanApprove;
		bSettle = bCanSettle;
		bPremium = bCanPremium;
		oTxt = oTxtBody;
		oHtm = oHtmBody;
	}
	
	public void run() {
		User oUsr = null;
		String sUid;
		FastStreamReplacer oRpl = new FastStreamReplacer();
  		MailSessionHandler oHlr = null;
		HashMap<String,Object> oActivationInfo = new HashMap<String,Object>();

  		Log.out.debug("Begin AsyncSendInvitation()");
  		
  		try {
			oHlr = new MailSessionHandler();
		} catch (IOException ioe) {
			Log.out.error("AsyncSendInvitation new MailSessionHandler() "+ioe.getClass().getName()+" "+ioe.getMessage(), ioe);
		}
  		
  		AtrilSession oSes = DAO.getAdminSession("AsyncSendInvitation");
  		oSes.autoCommit(true);
  		Dms oDms = oSes.getDms();
		CustomerAccount oAcc = new CustomerAccount(oDms, sAccId);
  		
		for (int t=0; t<aTxprs.length; t++) {
			String sTxpId = aTxprs[t];
	  		TaxPayer oTxp = new TaxPayer (oDms, sTxpId);

	  		try {

	  			sUid = User.forEmail(sEmail);
				oUsr = new User(oSes, sUid);
				try {
					oUsr.allowTaxPayer(oSes, oTxp.id());
				} catch (Exception xcpt) {
					Log.out.error("AsyncSendInvitation User.allowTaxPayer() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
				}

	  		} catch (ElementNotFoundException enfe) {

				String sPwd = Gadgets.generateRandomId(6, "abcdefghjkmnpqrtuvwxyz", Character.LOWERCASE_LETTER);
				sUid = User.create(oSes, sRec1stName, sRec2ndName, sRecEmail, sPwd, false).id();
				oUsr = new User(oSes, sUid);
				oUsr.canApproveInvoices(bApprove);
				oUsr.canSettleBillNotes(bSettle);
				oUsr.canUsePremiumCaptureServiceFlavor(bPremium);
				oUsr.setRole(oSes, oAcc, Role.user);
				oUsr.save(oSes);			

				try {
					oUsr.allowTaxPayer(oSes, oTxp.id());
				} catch (Exception xcpt) {
					Log.out.error("AsyncSendInvitation User.allowTaxPayer() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
				}
				
				HashMap oMap = FastStreamReplacer.createMap(new String[] {"1","2","3","4","5","6"},
															new String[] {oUsr.getFirstName(), sSender1stName, sSender2ndName, oTxp.getBusinessName() + (aTxprs.length>1 ? " y a otras " + String.valueOf(aTxprs.length-1) + " más": ""), oUsr.id(), sPwd});
		  		ByteArrayOutputStream oByOut = new ByteArrayOutputStream();
		  		PrintStream oPrt = new PrintStream(oByOut);				  		
		  		try {
		  	  		Log.out.debug("sending email");
					oHlr.sendMessage("Acceso a zesped", "Zesped", "noreply@zesped.com", "noreply@zesped.com",
					                 new String[] {sEmail}, RecipientType.TO, oRpl.replace(oTxt, oMap), oRpl.replace(oHtm, oMap),
					                 "ISO8859_1", null, null, null, oPrt);
				} catch (Exception xcpt) {
					Log.out.error("AsyncSendInvitation MailSessionHandler.sendMessage() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
				}
		  		oPrt.close();
		  		Log.out.info(oByOut.toString());
		  		try {
					oByOut.close();
				} catch (IOException ignore) { }
				DAO.log(oSes, oUsr.getDocument(), User.class, "SEND INVITATION", AtrilEvent.Level.INFO, sEmail);
			}
	  		if (bCreateEmp) {
				Employees oEms = oTxp.employees(oSes);
				try {
					oEms.seek(oSes, oUsr.getNickName());
				} catch (InstantiationException xcpt) {
					Log.out.error("AsyncSendInvitation Employees.seek() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
				} catch (IllegalAccessException xcpt) {
					Log.out.error("AsyncSendInvitation Employees.seek() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);			
				} catch (ElementNotFoundException enfe) {
		  	  		Log.out.debug("creating employee");
					Employee oEmp = new Employee(oSes, oEms);
	    			oEmp.setUuid(oUsr.getNickName());
	    			oEmp.setName(oUsr.getFirstName()+" "+oUsr.getLastName());
	    			oEmp.setEmployeeId("");
	    			oEmp.setTaxId("");
	    			oEmp.setActive(true);
	    			oEmp.save(oSes);
	    			if (0==t) oActivationInfo.put("employee_uuid", oUsr.getNickName());
				}
			}						
		} // next

		oActivationInfo.put("nickname", oUsr.getNickName());
		oActivationInfo.put("password", oUsr.getPassword());
		oActivationInfo.put("fullname", oUsr.getFirstName()+" "+oUsr.getLastName());
		oActivationInfo.put("email", oUsr.getEmail());
		oActivationInfo.put("user_docid", oUsr.id());
		oActivationInfo.put("user_uuid", oUsr.getString("user_uuid"));
		oActivationInfo.put("businessname", oAcc.getBusinessName());
		oActivationInfo.put("customer_acount", oAcc.getUuid());
		oActivationInfo.put("customer_account_docid", oAcc.id());
		oActivationInfo.put("taxpayer_docid", aTxprs[0]);
		oActivationInfo.put("can_approve", oUsr.getString("can_approve"));
		oActivationInfo.put("can_settle", oUsr.getString("can_settle"));
		oActivationInfo.put("can_premium", oUsr.getString("can_premium"));
		oActivationInfo.put("role", Role.user);
		oActivationInfo.put("hasorders", new Boolean(oAcc.orders(oSes).count()>0));
  		
		try {
			Cache.putEntry(oUsr.id()+"activationinfo", oActivationInfo);
		} catch (Exception xcpt) {
			Log.out.error("AsyncSendInvitation Cache.putEntry() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
		}
		
		oSes.disconnect();
		oSes.close();
  		Log.out.debug("End AsyncSendInvitation()");
	}
}
