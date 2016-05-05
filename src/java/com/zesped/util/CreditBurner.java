package com.zesped.util;

import java.math.BigDecimal;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CaptureServiceFlavor;
import com.zesped.model.CaptureServiceFlavors;
import com.zesped.model.CustomerAccount;

import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class CreditBurner extends Thread {

	private String sUser,sCustAcc;
	private CaptureServiceFlavor oSrvFlv;
	
    public CreditBurner(String sUserUid, String sCustomerAccount, String sCaptureService, String sFlavor)
    	throws ElementNotFoundException, NullPointerException {
    	oSrvFlv = CaptureServiceFlavors.getCaptureServiceFlavor(sCaptureService, sFlavor);
    	sCustAcc = sCustomerAccount;
    	sUser = sUserUid;
    }

    public void run() {
		AtrilSession oSes = null;
    	try {
        	BigDecimal oCredits = oSrvFlv.credits();
    		oSes = DAO.getAdminSession("CreditBurner");
    		oSes.autoCommit(false);
    		CustomerAccount oAcc = new CustomerAccount(oSes.getDms(), sCustAcc);
    		oAcc.burnCredits(oSes, oCredits);
    		oAcc.save(oSes);
    		DAO.log(oSes, oAcc.getDocument(), CustomerAccount.class, "BURN CREDITS", AtrilEvent.Level.INFO, oCredits.toString()+";"+oSrvFlv.uid()+";"+sUser);
    		switch (oAcc.getCreditsLeft().signum()) {
    			case 0:
    	    		DAO.log(oSes, oAcc.getDocument(), CustomerAccount.class, "NO CREDITS LEFT", AtrilEvent.Level.WARNING, "0;"+oSrvFlv.uid()+";"+sUser);
    	    		break;
    			case -1:
    	    		DAO.log(oSes, oAcc.getDocument(), CustomerAccount.class, "NEGATIVE CREDITS", AtrilEvent.Level.WARNING, oAcc.getCreditsLeft().toString()+";"+oSrvFlv.uid()+";"+sUser);
    	    		break;
    		}
    		oSes.commit();
    		oSes.disconnect();
    		oSes.close();
    		oSes = null;
    	} catch (Exception xcpt) {
    		Log.out.error("CreditBurner "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
    	} finally {
    		if (oSes!=null) {
        		if (oSes.isConnected()) oSes.disconnect();
        		if (oSes.isOpen()) oSes.close();    			
    		}
    		
    	}
    }
}
