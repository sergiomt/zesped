package com.zesped.model;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Configurations extends BaseModelObject {
	
	private static final long serialVersionUID = 1L;

	public Configurations() {
		super("Configurations");
	}

	public Configurations(Document d) {
		super(d);
	}
	
	@Override
	public Attr[] attributes() {
		return null;
	}

	public static Configurations top(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException {
		Document r = oSes.getDms().getRootDocument();
		Configurations c = new Configurations();
		for (Document d : r.children()) {
			if (d.type().name().equals(c.getTypeName())) {
				c.setDocument(oSes.getDms().getDocument(d.id()));
			    break;
			}
		} // next
		if (c.getDocument()==null) throw new ElementNotFoundException(c.getTypeName()+" document not found");
		return c;      
	}
	
}
