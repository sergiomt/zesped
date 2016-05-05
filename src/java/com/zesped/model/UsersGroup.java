package com.zesped.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.zesped.Log;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.AuthorizationException;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;
import es.ipsa.atril.sec.user.AuthorizationManager;
import es.ipsa.atril.sec.user.DocumentRights;
import es.ipsa.atril.sec.user.Group;
import es.ipsa.atril.sec.user.RightsFactory;

public class UsersGroup {
	private AuthorizationManager oAum;
	private Group oGrp;
	
	public UsersGroup(AuthorizationManager a, Group g) {
		oAum = a;
		oGrp = g;
	}

    public ArrayList<User> getMembers(AtrilSession oSes)
    	throws ElementNotFoundException {
    	ArrayList<User> aUsrs = new ArrayList<User>();
    	Set<es.ipsa.atril.sec.user.User> oUsrs = oGrp.getMembers();
    	Iterator<es.ipsa.atril.sec.user.User> oIter = oUsrs.iterator();
    	while (oIter.hasNext()) {
    		final String sUuid = oIter.next().getName();
    		if (!sUuid.equals("admin")) {
        		String sId = "";
        		try {
        			sId = User.forUuid(sUuid);
        			aUsrs.add(new User(oSes, sId));
        		} catch (NotEnoughRightsException nere) {
            		Log.out.error("UserGroup.getMembers() "+nere.getMessage()+" "+sUuid+" "+sId);        			
        		}
    		}
    	}
    	return aUsrs;
    }
	
	public void grantAll(Document... aDocs)
		throws AuthorizationException {
		DocumentRights oGrt = RightsFactory.getDocumentRightsAllGrant();
		if (aDocs!=null)
			for (Document oDoc : aDocs)
				oAum.setDocumentRights(oGrp, oDoc, oGrt);
	}

	public void grantReadOnly(Document... aDocs)
		throws AuthorizationException {
		DocumentRights oGrt = RightsFactory.getDocumentRightsAllDeny();
		oGrt.setRead(true);
		oGrt.setQuery(true);
		oGrt.setConfigure(true);
		if (aDocs!=null)
			for (Document oDoc : aDocs)
			oAum.setDocumentRights(oGrp, oDoc, oGrt);
	}
	
}
