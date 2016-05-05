package com.zesped.action;

import java.util.ArrayList;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Message;
import com.zesped.model.Messages;

public class ListMessages extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/listmessages.jsp";

	private ArrayList<Message> oMsgs;
	private int nTotalMsgs, iOffset, iMaxMsgs, iFolder;
	private String sSort;
	private boolean bAsc;
	
	public ListMessages() {
		oMsgs = new ArrayList<Message>();
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	public ArrayList<Message> getMessages() {
		return oMsgs;
	}

	public int getTotal() {
		return nTotalMsgs;
	}

	public int getFolder() {
		return iFolder;
	}
	
	public int getOffset() {
		return iOffset;
	}

	public int getMaxMessages() {
		return iMaxMsgs;
	}
	
	@DefaultHandler
	public Resolution form() {
		Log.out.debug("Begin ListMessages.form()");
		iFolder = Integer.parseInt(getParam("f","0"));
		sSort = getParam("s","sent_date");
		bAsc = getParam("a","0").equals("1");	
		iOffset = Integer.parseInt(getParam("o","0"));
		iMaxMsgs = Integer.parseInt(getParam("m","20"));
		try {			
  		  connect();
  		  oMsgs = Messages.list(getSession(), getSessionAttribute("user_uuid"), iFolder, iOffset, iMaxMsgs, sSort, bAsc);
  		  disconnect();
  	    } catch (Exception xcpt) {
  		  Log.out.error(xcpt.getMessage(), xcpt);
  	    } finally {
  		  close();
  	    }
		Log.out.debug("End ListMessages.form()");
		return new ForwardResolution(FORM);
	}
}
