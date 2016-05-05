package com.zesped;

import java.util.Date;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import es.ipsa.atril.sec.authentication.AtrilSession;

public class SessionCache {

	private static ConcurrentHashMap<String,SessionEntry> oSessions = new ConcurrentHashMap<String,SessionEntry>();

	private static Stack<SessionEntry> oAdminSessions = new Stack<SessionEntry>();
	
	private static int iMaxCachedAdminSessions = 10;

	public static AtrilSession getAdminSession(String sContext) {
		if (oAdminSessions.isEmpty()) {
			if (iMaxCachedAdminSessions>0) {
				SessionEntry oSes = new SessionEntry ("admin", DAO.getAdminSession(sContext));
				return oSes.session;
			} else {
				return null;				
			}
		} else {
			SessionEntry oSes = oAdminSessions.pop();
			oSes.lastUse = new Date();
			return oSes.session;
		}
	}
	
	public static AtrilSession getSession(String sId) {
		Log.out.debug("Trying to get cached Atril session for Tomcat session "+sId);
		if (oSessions.containsKey(sId)) {
			SessionEntry oSes = oSessions.get(sId);
			oSes.lastUse = new Date();
			return oSes.session;
		} else {
			return null;
		}
	}
	
	public static void putSession(String sId, AtrilSession oSes)
		throws IllegalStateException {
		if (oSessions.containsKey(sId))
			throw new IllegalStateException("Attemping to cache session "+sId+" which is already cached");
		else {
			oSessions.put(sId, new SessionEntry(sId,oSes));
		}
	}

	public static boolean putAdminSession(AtrilSession oSes)
		throws IllegalStateException {
		if (oAdminSessions.size()<iMaxCachedAdminSessions) {
			oAdminSessions.push(new SessionEntry ("admin", oSes));
			return true;
		} else {
			return false;
		}
	}
	
	public static void disposeSession(String sId) {
		AtrilSession oSes = getSession(sId);
		if (oSes!=null) {
			if (oSessions.containsKey(sId)) oSessions.remove(sId);
			if (oSes.isConnected()) oSes.close();
			if (oSes.isOpen()) oSes.close();
		}
	}
}
