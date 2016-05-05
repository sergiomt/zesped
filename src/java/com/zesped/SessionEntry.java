package com.zesped;

import java.util.Date;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class SessionEntry {

	public String id;
	public AtrilSession session;
	public Date lastUse;

	public SessionEntry(String sId, AtrilSession oSes) {
		id = sId;
		session = oSes;
		lastUse = new Date();
	}
}
