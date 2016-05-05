package com.zesped.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;

public class VatPercent extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	public VatPercent() {
		super("VatPercent");
	}

	public VatPercent(Document d) {
		super(d);
	}
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("percentage",DataType.STRING,true,true,null),
    	new Attr("active",DataType.STRING,false,true,null),
    	new Attr("description",DataType.STRING,false,false,null)    	
	};
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getPercentage() {
		BigDecimal oPercentage = getValue().multiply(new BigDecimal("100"));
		String sPercentage;
		try {
			BigInteger oIntPercentage = oPercentage.toBigIntegerExact();
			sPercentage = oIntPercentage.toString();
		} catch (ArithmeticException ex) {
			sPercentage = oPercentage.toString();
		}		
		return sPercentage+"%";
	}

	public BigDecimal getValue() {
		if (isNull("percentage"))
			return null;
		else
			return new BigDecimal(getString("percentage"));
	}

	public void setValue(BigDecimal p) throws NullPointerException {
		if (p==null)
			throw new NullPointerException("VAT percentage cannot be null");
		else
			put("percentage", p.toString());
	}

	public boolean isActive() {
		if (isNull("active"))
			return true;
		else
			return getString("active").equals("1");
	}

	public void activate() {
		put("active","1");
	}

	public void deactivate() {
		put("active","0");
	}

	public String getDescription() {
		if (isNull("description"))
			return null;
		else
			return getString("description");		
	}

	public void setDescription(String d) {
		put("description",d);
	}
	
}
