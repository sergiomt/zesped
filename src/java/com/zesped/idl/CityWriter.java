package com.zesped.idl;

import com.zesped.DAO;

import es.ipsa.atril.sec.authentication.AtrilSession;

public class CityWriter {

	  public static void main(String[] args) throws Exception {
			ModelManager oMan = new ModelManager();
			DAO oDao = new DAO();    
			oDao.init(oMan.getConnectionProperties());
			AtrilSession oSes = DAO.getAdminSession("CityWriter");
			oSes.autoCommit(true);
			oMan.writeCities(oSes, "es");
			oSes.disconnect();
			oSes.close();
			oDao.destroy();
		  }
}
