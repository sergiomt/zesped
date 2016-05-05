package com.zesped.model;

import com.knowgate.misc.Gadgets;
import com.zesped.model.BaseModelObject;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class State extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("country_id",DataType.STRING,false,true,new ForeignKey(Country.class, "id")),
    	new Attr("code",DataType.STRING,false,false,null),
    	new Attr("name",DataType.STRING,true,true,null)
    };
	
	public State() {
		super("State");
	}

	public State(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}

	public State(AtrilSession oSes, Document oParent) {
		super("State");
		newDocument(oSes, oParent);
	}
	
	public State(Document d) {
		super(d);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}
	
	public String getCode() {
		return getString("code");
	}

	public String getName() {
		return getString("name");
	}

	public String getNameHtml() {
		return Gadgets.HTMLEncode(getString("name"));
	}
	
	public Cities cities(AtrilSession oSes) {
		Cities c = null;
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Cities")) {
				c = new Cities(oSes.getDms().getDocument(d.id()));
				break;
			}
		}
		return c;
	}

	@Override
	public void save(AtrilSession oSes) {
		super.save(oSes);
		States oStts = new States(oSes.getDms().getDocument(getDocument().parents().get(0).id()));
		oStts.clearCache(getString("country_id"));
	}	
}
