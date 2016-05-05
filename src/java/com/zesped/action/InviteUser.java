package com.zesped.action;

import java.util.ArrayList;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.TaxPayer;

public class InviteUser extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/inviteuser.jsp";
	
	private ArrayList<String> a1stnames, a2ndnames, aemails;
	
	private String bizname, biznames, txps;
	private boolean areemployees, canapprove, cansettle, canpremium;
	
	public InviteUser() {
		a1stnames=new ArrayList<String>(5);
		a2ndnames=new ArrayList<String>(5);
		aemails=new ArrayList<String>(5);
		areemployees=canapprove=cansettle=canpremium=true;
	}

	public String getBusinessName() {
		return bizname;
	}

	public String getAllBusinessNames() {
		return biznames;
	}
	
	public String getTaxPayer() {
		return txps;
	}

	public ArrayList<String> getFirstName() {
		return a1stnames;
	}

	public ArrayList<String> getLastName() {
		return a2ndnames;
	}

	public ArrayList<String> getEmail() {
		return aemails;
	}

	public boolean getEmployee() {
		return areemployees;
	}

	public boolean getApprove() {
		return canapprove;
	}

	public boolean getSettle() {
		return cansettle;
	}

	public boolean getPremium() {
		return canpremium;
	}
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@DefaultHandler
	public Resolution form() {
		try {
			txps = getParam("taxpayer","");
			if (txps.length()==0) {
	    		bizname = "";
	    		biznames = "";				
			} else {
				String[] ids = getParam("taxpayer").split(",");
				connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
				TaxPayer txpr = new TaxPayer(getSession().getDms(), ids[0]);
	    		bizname = Gadgets.HTMLEncode(txpr.getBusinessName());
	    		biznames = "";
	    		if (ids.length>1) {
	    			for (int t=1; t<ids.length; t++)
	    				biznames += (biznames.length()==0 ? "" : ", ")+new TaxPayer(getSession().getDms(), ids[t]).getBusinessName();
	    			bizname += " y <a href=\"#\" class=\"anchorhighlight\" title=\""+biznames.replace('"', ' ')+"\">"+String.valueOf(ids.length-1)+" m&aacute;s</a>";
	    		}
	    		biznames += (biznames.length()==0 ? "" : ", ")+txpr.getBusinessName();
				disconnect();				
			}
    	} catch (Exception xcpt) {
    		Log.out.error(xcpt.getMessage(), xcpt);
    	} finally {
    		close();
    	}			
		return new ForwardResolution(FORM);
	}	
}
