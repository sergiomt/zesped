package com.zesped.util;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Employee;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.sec.authentication.AtrilSession;

public class AsyncAccountCreator extends Thread {

	private User oUsr;
	public AsyncAccountCreator(User u) {
		oUsr = u;
	}

	public void run() {
		AtrilSession oSes = null;
		try {
			oSes = DAO.getAdminSession("AsyncAccountCreator");
			final String sTemporaryBusinessName = Gadgets.generateUUID();			
			CustomerAccount oCac = CustomerAccount.create(oSes, oUsr, sTemporaryBusinessName);
			oSes.commit();
			TaxPayer oTxp = TaxPayer.create(oSes, oCac, sTemporaryBusinessName, "", oUsr.getFirstName()+" "+oUsr.getLastName(), oUsr.getEmail(), true, DAO.getVolumesMountBase());
			oSes.commit();
			Employee oEmp = new Employee(oSes, oTxp.employees(oSes));
			oEmp.setActive(true);
			oEmp.setUuid(oUsr.getNickName());
			oEmp.setName(oUsr.getFirstName()+" "+oUsr.getLastName());
			oEmp.save(oSes);
			oSes.commit();
			oSes.disconnect();
			oSes.close();
			oSes=null;
		} catch (Exception xcpt) {
			Log.out.error("AsyncAccountCreator.run() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
		} finally {
			if (oSes!=null) {
				if (oSes.isConnected()) oSes.disconnect();
				if (oSes.isOpen()) oSes.close();
			}
		}
	}
}
