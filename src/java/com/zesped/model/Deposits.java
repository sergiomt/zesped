package com.zesped.model;

import java.util.ArrayList;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;
import es.ipsa.atril.sec.user.AuthorizationManager;
import es.ipsa.atril.sec.user.RightsFactory;

public class Deposits extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("currency",DataType.STRING,false,false,null),
    	new Attr("disabled",DataType.NUMBER,false,true,null),
    	new Attr("holder",DataType.STRING,false,false,null),
    	new Attr("number",DataType.STRING,false,false,null)
    };	

	public Deposits() {
		super("Deposits");
	}
	
	public Deposits(AtrilSession oSes) {
		super("Deposits");
		newDocument(oSes, oSes.getDms().getRootDocument());
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public static Deposits top(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException {
		Document r = oSes.getDms().getRootDocument();
		Deposits s = new Deposits();
		for (Document d : r.children()) {
			if (d.type().name().equals(s.getTypeName())) {
				s.setDocument(oSes.getDms().getDocument(d.id()));
				break;
			}
		} // next
		if (s.getDocument()==null) throw new ElementNotFoundException(s.getTypeName()+" document not found");
		return s;      
	}
	
	public void grantAll(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		AuthorizationManager oAum = oSes.getAuthorizationManager();
		ArrayList<User> aUsrs = Users.top(oSes).list(oSes);
		Document oDeps = getDocument();
		for (User u : aUsrs)
			oAum.setDocumentRights(oAum.getUser(u.getNickName()), oDeps, RightsFactory.getDocumentRightsAllGrant());
	}
}
