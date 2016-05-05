package com.zesped.model;

import java.math.BigDecimal;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;

public class CustomerAccountCredits extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("account_id",DataType.STRING,true,true),
    	new Attr("credits_used",DataType.NUMBER,false,false),
    	new Attr("credits_left",DataType.NUMBER,false,false)
    };

	public CustomerAccountCredits() {
		super("CustomerAccountCredits");
	}

	public CustomerAccountCredits(Document d) {
		super(d);
	}
	
	public CustomerAccountCredits(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public BigDecimal getCreditsUsed() throws NumberFormatException {
		if (isNull("credits_used"))
			return null;
		else
			return getBigDecimal("credits_used");
	}

	public void setCreditsUsed(BigDecimal credits_used) {
		if (null==credits_used)
			remove("credits_used");
		else
			put("credits_used", credits_used);
	}

	public BigDecimal getCreditsLeft() throws NumberFormatException {
		if (isNull("credits_left"))
			return null;
		else
			return getBigDecimal("credits_left");
	}

	public void setCreditsleft(BigDecimal credits_left) {
		if (null==credits_left)
			remove("credits_left");
		else
			put("credits_used", credits_left);
	}

	public void burnCredits(BigDecimal credits) throws IllegalArgumentException {
		// No limitar de ninguna forma el que pueda haber creditos negativos
		if (credits.signum()==-1)
			throw new IllegalArgumentException("Burnt credits must be a positive integer value");
		if (isNull("credits_left"))
			put("credits_left", BigDecimal.ZERO.subtract(credits));
		else
			put("credits_left", getBigDecimal("credits_left").subtract(credits));
		if (isNull("credits_used"))
			put("credits_used", credits);
		else
			put("credits_used", getBigDecimal("credits_used").add(credits));
	}

	public void restoreCredits(BigDecimal credits) throws IllegalArgumentException {
		if (credits.signum()==-1)
			throw new IllegalArgumentException("REstored credits must be a positive integer value");
		if (isNull("credits_left"))
			put("credits_left", credits);
		else
			put("credits_left", getBigDecimal("credits_left").add(credits));
		if (isNull("credits_used"))
			put("credits_used", credits);
		else
			put("credits_used", getBigDecimal("credits_used").subtract(credits));
	}
	
}
