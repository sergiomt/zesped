package com.zesped.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.nio.channels.NotYetConnectedException;
import java.util.Date;
import java.util.Locale;

import com.zesped.Localization;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class Product extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("product_id",DataType.STRING,true,true,null),
    	new Attr("product_name",DataType.STRING,true,true,null),
    	new Attr("creation_date",DataType.DATE_TIME,false,true,null),
    	new Attr("is_active",DataType.STRING,false,true,null),
    	new Attr("credits",DataType.NUMBER,false,true,null),
    	new Attr("price",DataType.STRING,true,false,null),
    	new Attr("currency",DataType.STRING,true,false,null)
	};
    
	public Product() {
		super("Product");
	}

	public Product(Document oDoc) {
		super(oDoc);
	}

	public Product(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}

	public Product(AtrilSession oSes, String sId, String sName, long lCredits, String dPrice, String sCurrency) {
		super("Product");
		Document oDoc = exists(oSes, "product_id", sId);
		Document oParent = Products.top(oSes).getDocument();
		if (oDoc==null)
		  newDocument(oSes, oParent);
		else
		  setDocument(oDoc);
		put("product_id",sId);
		put("product_name",sName);
		put("creation_date",new Date());
		put("credits",new Long(lCredits));
		put("price",dPrice);
		put("currency",sCurrency==null ? "EUR" : sCurrency);
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getId() {
		return getString("product_id");
	}

	public String getName() {
		return getString("product_name");
	}

	public String getCurrency() {
		return getString("currency");
	}

	public String getLabel() {
		return getString("product_name")+" ("+getPrice(Localization.DEFAULT_LOCALE)+" "+getCurrency()+")";
	}

	public String getPrice(Locale oLoc) {
		BigDecimal oDec = getPrice();
		try {
			BigInteger oBig = oDec.toBigIntegerExact();
			return oBig.toString();		        
		} catch (ArithmeticException ex) {
			DecimalFormat oFmt = new DecimalFormat("#.##", new DecimalFormatSymbols(oLoc));
			return oFmt.format(oDec);
		}
	}
	
	public BigDecimal getPrice() {
		return new BigDecimal(getString("price"));
	}

	public Long getCredits() {
		Object oCredits = get("credits");
		if (oCredits==null) return null;
		String sCredits = oCredits.toString();
		int iDot = sCredits.indexOf('.');
		if (iDot>=0) sCredits = sCredits.substring(0,iDot);
		return new Long(sCredits);
	}

	@Override
	public void save(AtrilSession oSes) throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, NotYetConnectedException, DmsException {

		if (getDocument()==null)
			newDocument(oSes, Products.top(oSes).getDocument());
		
		if (!containsKey("creation_date")) put("creation_date", new Date());
		if (!containsKey("is_active")) put("is_active", "1");
		if (!containsKey("credits")) put("credits", new Long(0l));
		if (!containsKey("currency")) put("currency", "EUR");

		super.save(oSes);
	}
}
