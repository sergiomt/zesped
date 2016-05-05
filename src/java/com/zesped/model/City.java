package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class City extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("state_code",DataType.STRING,false,true,new ForeignKey(State.class, "code")),
    	new Attr("name",DataType.STRING,false,true,null),
    	new Attr("city_id",DataType.STRING,false,false,null),
    	new Attr("zipcode",DataType.STRING,false,false,null)
    };
	
	public City() {
		super("City");
	}

	public City(Document d) {
		super(d);
	}

	public City(AtrilSession oSes, Document oParent) {
		super("City");
		newDocument(oSes, oParent);
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getName() {
		return getString("name");
	}
	
	@Override
	public void save(AtrilSession oSes) {
		super.save(oSes);
		Cities oCtis = new Cities(oSes.getDms().getDocument(getDocument().parents().get(0).id()));
		oCtis.clearCache(getString("state_code"));
	}
}
