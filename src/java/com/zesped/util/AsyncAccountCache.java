package com.zesped.util;

import java.util.ArrayList;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.User;

import com.knowgate.dfs.FileSystem;
import com.knowgate.dfs.HttpRequest;
import com.knowgate.misc.NameValuePair;

import es.ipsa.atril.sec.authentication.AtrilSession;

public class AsyncAccountCache extends Thread {

	private String sAccId;
	private String sBaseUrl;
	private String sUsrNick;
	private String sUsrPwd;
	

	public AsyncAccountCache(String sUserNickName, String sUserPassword, String sCustomerAccountId, String sBaseHref) {
		sAccId = sCustomerAccountId;
		sBaseUrl = sBaseHref;
		sUsrNick = sUserNickName;
		sUsrPwd = sUserPassword;
	}

	public void run() {
		FileSystem oFs = new FileSystem();
		HttpRequest oReq;
		
		try {
			AtrilSession oSes = DAO.getAdminSession("AsyncAccountCache");
			User oUsr = new User(oSes, User.forUuid(sUsrNick));
			oSes.disconnect();
			oSes.close();

			oReq = new HttpRequest(sBaseUrl+"login.jsp", null, "post",
								   new NameValuePair[]{new NameValuePair("email", oUsr.getEmail()),
					            					   new NameValuePair("passw", sUsrPwd),
					            					   new NameValuePair("format", "session")});
			oReq.post();
			ArrayList<NameValuePair> aCookies = oReq.getCookies();

			NameValuePair[] aCachedPages = new NameValuePair[] {
				new NameValuePair("acctaxpayers", "ListTaxPayers.action"),
				new NameValuePair("accsuppliers", "ListClients.action"),
				new NameValuePair("accusers", "ListUsers.action")
			};
			
			for (int p=0; p<aCachedPages.length; p++) {
				oReq = new HttpRequest(sBaseUrl+aCachedPages[p].getValue());
				oReq.setCookies(aCookies);
				oReq.get();
				String sDyna = oReq.src();

				oReq = new HttpRequest(sBaseUrl+"cacheread.jsp?key="+sAccId+aCachedPages[p].getName());
				oReq.setCookies(aCookies);
				oReq.get();			
				String sCach = oReq.src();
				
				if (!sDyna.equals(sCach)) {
					oReq = new HttpRequest(sBaseUrl+"cachestore.jsp", null, "post",
					new NameValuePair[]{new NameValuePair("key", sAccId+aCachedPages[p].getName()), new NameValuePair("val", sDyna)});
					oReq.setCookies(aCookies);
					oReq.start();
				}		
			} // next

		} catch (Exception xcpt) {
			Log.out.error("AsyncAccountCache.run() "+xcpt.getClass().getName()+" "+xcpt.getMessage());
		}
	}
	
}
