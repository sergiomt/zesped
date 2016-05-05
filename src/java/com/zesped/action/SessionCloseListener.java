package com.zesped.action;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.zesped.Log;
import com.zesped.SessionCache;

public final class SessionCloseListener implements HttpSessionListener {

    public SessionCloseListener() {
    }

    @Override
    public void sessionCreated(final HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
    	String sId = se.getSession().getId();
    	SessionCache.disposeSession(sId);
        Log.out.info("Session "+sId+" closed by listener");
    }
}