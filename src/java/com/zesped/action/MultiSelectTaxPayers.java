package com.zesped.action;

import java.util.ArrayList;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.knowgate.misc.NameValuePair;
import com.zesped.Log;
import com.zesped.model.BaseCompanyObject;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;
import com.zesped.model.CaptureService;

public class MultiSelectTaxPayers extends BaseActionBean {

	private static final String FORM="/WEB-INF/jsp/taxpayers_multiselect.jsp";
	
	public ArrayList<NameValuePair> selectedTaxPayers;
	
	public MultiSelectTaxPayers() {
		selectedTaxPayers = new ArrayList<NameValuePair>();
	}

	public ArrayList<NameValuePair> getSelectedTaxPayers() {
		return selectedTaxPayers;
	}

	public void setSelectedTaxPayers(ArrayList<NameValuePair> stp) {
		selectedTaxPayers = stp;
	}
	
	@Override
	public CaptureService getCaptureService() {
		return null;
	}

	@DefaultHandler
	public Resolution form() {
		final String sUid = getParam("id","");
		final String sTid = getParam("tid","");
		Log.out.debug("Begin MultiSelectTaxPayers.form({id:"+sUid+",tid:"+sTid+"})");
		if (sUid.length()>0) {
			try {
		  		  connect();
		  		  User oUsr = new User(getSession(), sUid);
		  		  if (oUsr.getAllowedTaxPayers().size()>0)
		  			  for (BaseCompanyObject oAtp : oUsr.getAllowedTaxPayers())
		  				  selectedTaxPayers.add(new NameValuePair(oAtp.getBusinessName(), oAtp.getString("taxpayer")));		  		
		  		  else if (oUsr.getDeniedTaxPayers().size()>0)
		  			  for (BaseCompanyObject oDtp : oUsr.getDeniedTaxPayers())
		  				  selectedTaxPayers.add(new NameValuePair(oDtp.getBusinessName(), oDtp.getString("taxpayer")));		  				  
		  		  disconnect();
		  	  } catch (Exception xcpt) {
		  		  Log.out.error(xcpt.getMessage(), xcpt);
		  	  } finally {
		  		  close();
		  	  }					
		} else if (sTid.length()>0) {
			try {
	  		  connect();
	  		  TaxPayer oTxp = new TaxPayer(getSession().getDms(), sTid);
			  selectedTaxPayers.add(new NameValuePair(oTxp.getBusinessName(), sTid));		  		
	  		  disconnect();
	  	  } catch (Exception xcpt) {
	  		  Log.out.error(xcpt.getMessage(), xcpt);
	  	  } finally {
	  		  close();
	  	  }								
		}
		Log.out.debug("End MultiSelectTaxPayers.form() : "+String.valueOf(selectedTaxPayers.size()));
 		return new ForwardResolution(FORM);
	}
}
