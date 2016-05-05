package com.zesped.model;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class Country extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private String sStates = null;

	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("id",DataType.STRING,true,true,null),
    	new Attr("es",DataType.STRING,false,false,null),
    	new Attr("en",DataType.STRING,false,false,null)
    };

	public Country() {
		super("Country");
	}

	public Country(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}

	public Country(AtrilSession oSes, Document oParent) {
		super("Country");
		newDocument(oSes, oParent);
	}

	public Country(Document oDoc) {
		super("Country");
		setDocument(oDoc);
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	@Override
	protected void setDocument(Document oDoc) {
		super.setDocument(oDoc);
		AtrilSession oSes = DAO.getAdminSession("Country");
		states(oSes);
		oSes.disconnect();
		oSes.close();		
	}
	
	public String getIsoCode() {
		return getString("id");
	}

	public String getName() {
		return getString("es");
	}

	public String getNameHtml() {
		return Gadgets.HTMLEncode(getString("es"));
	}
	
	public States states(AtrilSession oSes) throws IllegalStateException {
		States s = null;
		Dms oDms = oSes.getDms();
		if (null==sStates) {
			for (Document d : oDms.getDocument(id()).children()) {
				if (d.type().name().equals("States")) {
					s = new States(oDms.getDocument(d.id()));
					sStates = s.id();
					break;
				}
			}
		} else {
			s = new States(oDms.getDocument(sStates));		
		}
		return s;
	}
}
