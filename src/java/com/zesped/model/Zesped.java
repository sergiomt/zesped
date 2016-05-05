package com.zesped.model;

import com.zesped.Log;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.admin.AdministeredGroup;
import es.ipsa.atril.sec.admin.AuthorizationAdminManager;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;
import es.ipsa.atril.sec.user.AuthorizationManager;
import es.ipsa.atril.sec.user.Group;

public class Zesped extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final String ZESPED_USERS = "Zesped Users";
	private static final String BACKEND_OPERATORS = "Back-End operators";
		
	private static final Attr[] aAttrs = new Attr[] { };

	public Zesped() {
		super("Zesped");
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public static void createGroups(AtrilSession oSes)  throws NullPointerException, IllegalArgumentException {
		Log.out.debug("Begin Zesped.createGroups()");

		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		
		AdministeredGroup oBeo=null;
	
	    try {
	  	  oAam.getGroup(ZESPED_USERS);
	  	} catch (ElementNotFoundException enf) {
	      oAam.createGroup(ZESPED_USERS, "All Zesped Platform Users", false, null, oAam.getGroup("All"));
	  	}

		AdministeredGroup oGrp = oAam.getGroup(ZESPED_USERS);
	    
		try {
		  oBeo = oAam.getGroup(BACKEND_OPERATORS);
		} catch (ElementNotFoundException enf) {
		  if (null==oBeo) {
			  Log.out.debug("AuthorizationAdminManager.createGroup("+BACKEND_OPERATORS+")");
			  oBeo = oAam.createGroup(BACKEND_OPERATORS, "", false, null, oGrp);
		  }
		}
		Log.out.debug("End Zesped.createGroups()");
	}

    public static Group getUsersGroup(AuthorizationManager oAum) {
    	return oAum.getGroup(ZESPED_USERS);
    }

    public static AdministeredGroup getUsersAdministeredGroup(AuthorizationAdminManager oAam) {
    	return oAam.getGroup(ZESPED_USERS);
    }
    
    public static Group getOperatorsGroup(AuthorizationManager oAum) {
    	return oAum.getGroup(BACKEND_OPERATORS);
    }

    public static AdministeredGroup getOperatorsAdministeredGroup(AuthorizationAdminManager oAam) {
    	return oAam.getGroup(BACKEND_OPERATORS);
    }

	public static Zesped top(AtrilSession oSes)
	  throws ElementNotFoundException, NotEnoughRightsException {
      Log.out.debug("Begin com.zesped.model.Zesped.top()");
	  Document r = oSes.getDms().getRootDocument();
	  Zesped z = new Zesped();
	  for (Document d : r.children()) {
		  if (d.type().name().equals(z.getTypeName())) {
			  z.setDocument(oSes.getDms().getDocument(d.id()));
			  break;
		  }
	  } // next
	  if (z.getDocument()==null) throw new ElementNotFoundException(z.getTypeName()+" document not found");
	  Log.out.debug("End com.zesped.model.Zesped.top() : " + z);
	  return z;      
	}
	
}
