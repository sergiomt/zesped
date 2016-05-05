package com.zesped.model;

import java.math.BigDecimal;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class CaptureServiceFlavor extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("id",DataType.STRING,false,false,null),
    	new Attr("captureservice",DataType.STRING,false,false,null),
    	new Attr("name",DataType.STRING,false,false,null),
    	new Attr("is_active",DataType.STRING,false,false,null),
    	new Attr("credits",DataType.NUMBER,false,false,null)
	};

	public CaptureServiceFlavor() {
		super("CaptureServiceFlavor");
	}

	public CaptureServiceFlavor(Document d) {
		super(d);
	}
	
	public CaptureServiceFlavor(AtrilSession oSes, String sId, CaptureService eCaptureServ, String sName, int iCredits) {
		super("CaptureServiceFlavor");
		Document oDoc = exists(oSes, "id", sId);
		if (oDoc==null)
		  newDocument(oSes, Zesped.top(oSes).getDocument());
		else
		  setDocument(oDoc);
		put("id",sId);
		put("captureservice",eCaptureServ.toString());
		put("name",sName);
		put("credits",new Long((long) iCredits));
		put("is_active","1");
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public CaptureService getCaptureService() {
		return CaptureService.valueOf(getString("captureservice"));
	}

	public String uid() {
		return getString("id");
	}
	
	public String name() {
		return getString("name");
	}

	public BigDecimal credits() {
		return getBigDecimal("credits");
	}
	
	public static String[] flavors() {
		return aFlavors;
	}
	
	public static final String BASIC = "basic";
	public static final String PREMIUM = "premium";
	
	private static final String[] aFlavors = new String[]{BASIC,PREMIUM};
}
