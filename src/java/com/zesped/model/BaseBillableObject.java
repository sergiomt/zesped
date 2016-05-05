package com.zesped.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.volumes.Volume;
import es.ipsa.atril.doc.volumes.VolumeManager;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public abstract class BaseBillableObject extends BaseModelObject {
		
	public BaseBillableObject(String sType) {
		super(sType);
	}
	
	public BaseBillableObject(Document c) {
		super(c);
	}

	public BaseBillableObject(Dms oDms, String sDocId) throws ElementNotFoundException {
		super(oDms, sDocId);
	}
	
	public Date getCreationDate() {
		if (isNull("creation_date"))
			return null;
		else
			return getDate("creation_date");
	}

	public Date getProcessDate() {
		if (isNull("process_date"))
			return null;
		else
			return getDate("process_date");
	}

	public void setProcessDate(Date process_date) {
		if (null==process_date)
			remove("process_date");
		else
			put("process_date", process_date);
	}

	public int getYear() throws NullPointerException {
		if (isNull("year"))
			throw new NullPointerException("Year not set");
		else
			return getBigDecimal("year").intValue();
	}

	public void setYear(int y) {
		put("year", new Long(y));
	}

	public int getMonth() throws NullPointerException {
		if (isNull("month"))
			throw new NullPointerException("Month not set");
		else
			return getBigDecimal("month").intValue();
	}

	public void setMonth(int m) {
		put("month", new Long(m));
	}
	
	public String getTaxPayer() {
		if (isNull("taxpayer"))
			return null;
		else
			return getString("taxpayer");
	}

	public void setTaxPayer(String taxPayer) {
		if (null==taxPayer)
			remove("taxpayer");
		else
			put("taxpayer", taxPayer);
	}

	public String getBillerTaxPayer() {
		if (isNull("biller_taxpayer"))
			return null;
		else
			return getString("biller_taxpayer");
	}

	public void setBillerTaxPayer(String taxPayer) {
		if (null==taxPayer)
			remove("biller_taxpayer");
		else
			put("biller_taxpayer", taxPayer);
	}
	
	public BigDecimal getBaseAmount() throws NumberFormatException {
		if (isNull("base_amount")) {
			return null;
		} else if (isArray("base_amount")) {
			String[] aStrs = getStrings("base_amount");
			int nBases = aStrs.length;
			BigDecimal oSum = new BigDecimal("0");
			for (int b=0; b<nBases; b++)
				oSum = oSum.add(new BigDecimal(aStrs[b]));
			return oSum;
		} else if (getString("base_amount").length()==0) {
			return BigDecimal.ZERO;
		} else {
			String[] aStrs = getString("base_amount").split(";");
			BigDecimal oSum = new BigDecimal("0");
			for (int d=0; d<aStrs.length; d++)
				oSum = oSum.add(new BigDecimal(aStrs[d]));
			return oSum;
		}
	}

	public void setBaseAmount(BigDecimal base_amount) {
		if (null==base_amount)
			remove("base_amount");
		else
			put("base_amount", base_amount.toString());
	}

	public BigDecimal getTotalAmount() throws NumberFormatException {
		if (isNull("final_amount"))
			return null;
		else
			return new BigDecimal(getString("final_amount"));
	}

	public void setTotalAmount(BigDecimal total_amount) {
		if (null==total_amount)
			remove("final_amount");
		else
			put("final_amount", total_amount.toString());
	}
		
	public BigDecimal getVat() throws NumberFormatException {
		if (isNull("vat")) {
			return null;
		} else if (isArray("vat")) {
			String[] aStrs = getStrings("vat");
			int nVats = aStrs.length;
			BigDecimal oSum = new BigDecimal("0");
			for (int v=0; v<nVats; v++)
				oSum = oSum.add(new BigDecimal(aStrs[v]));
			return oSum;
		} else if (getString("vat").length()==0) {
			return BigDecimal.ZERO;
		} else {
			String[] aStrs = getString("vat").split(";");
			BigDecimal oSum = new BigDecimal("0");
			for (int d=0; d<aStrs.length; d++)
				oSum = oSum.add(new BigDecimal(aStrs[d]));
			return oSum;
		}
	}

	public void setVat(BigDecimal vat) {
		if (null==vat)
			remove("vat");
		else
			put("vat", vat.toString());
	}

	public BigDecimal getVatPct() throws NumberFormatException {
		if (isNull("vatpct")) {
			return null;
		} else if (isArray("vatpct")) {
			String[] aStrs = getStrings("vatpct");
			int nVats = aStrs.length;
			BigDecimal oSum = new BigDecimal("0");
			for (int v=0; v<nVats; v++)
				oSum = oSum.add(new BigDecimal(aStrs[v]));
			return oSum.divide(new BigDecimal(nVats), 2, RoundingMode.HALF_UP);
		} else if (getString("vatpct").length()==0) {
			return BigDecimal.ZERO;
		} else {
			String[] aStrs = getString("vatpct").split(";");
			BigDecimal oSum = new BigDecimal("0");
			for (int d=0; d<aStrs.length; d++)
				oSum = oSum.add(new BigDecimal(aStrs[d]));
			return oSum.divide(new BigDecimal(aStrs.length), 2, RoundingMode.HALF_UP);
		}
	}

	public void setVatPct(BigDecimal vatpct) {
		if (null==vatpct)
			remove("vatpct");
		else
			put("vatpct", vatpct.toString());
	}

	public String getCurrency() {
		if (isNull("currency"))
			return null;
		else
			return getString("currency");
	}

	public void setCurrency(String currency) {
		if (null==currency)
			remove("currency");
		else
			put("currency", currency);
	}
	
	public String getPaymentMean() {
		if (isNull("payment_mean"))
			return null;
		else
			return getString("payment_mean");
	}

	public void setPaymentMean(String payment_mean) {
		if (null==payment_mean)
			remove("payment_mean");
		else
			put("payment_mean", payment_mean);
	}

	public String getConcept() {
		if (isNull("concept"))
			return null;
		else
			return getString("concept");
	}

	public void setConcept(String concept) {
		if (null==concept)
			remove("concept");
		else
			put("concept", concept);
	}

	public String getComments() {
		if (isNull("comments"))
			return null;
		else
			return getString("comments");
	}

	public void setComments(String comments) {
		if (null==comments)
			remove("comments");
		else
			put("comments", comments);
	}

	public boolean isProcessed() {
		return getStringNull("is_processed").equals("1");
	}

	public void isProcessed(boolean p) {
		put("is_processed", p ? "1" : "0");
	}

	public boolean hasMistakes() {
		return getStringNull("has_mistakes").equals("1");
	}

	public void hasMistakes(boolean m) {
		put("has_mistakes", m ? "1" : "0");
	}
	
	public CustomerAccount customerAccount(Dms oDms) {
		return taxPayer(oDms).customerAccount(oDms);
	}

	@Override
	public Volume getVolume(AtrilSession oSes) throws ElementNotFoundException {
		Document oDoc = getDocument();
		if (oDoc==null) {
			return null;
		} else {
			VolumeManager oVolm = oSes.getDms().getVolumeManager();
			if (oVolm.hasVolume(oDoc))
				return oVolm.getVolume(oDoc);
			else {
				Document oTxp = oSes.getDms().getDocument(getTaxPayer());
				if (oVolm.hasVolume(oTxp))
					return oVolm.getVolume(oTxp);
				else
					return null;				
			}
		}			
	}
	
	public boolean isWithinRanges(BigDecimal[] aAmountRange, Integer[] aMonthsRange, Integer[] aYearsRange) {
		boolean bWithinRange = true;
		if (aAmountRange!=null) {
			if (getStringNull("final_amount","").length()==0) {
				bWithinRange = false;
			} else {
				if (aAmountRange[0]!=null)
					bWithinRange &= (getTotalAmount().compareTo(aAmountRange[0])>=0);								
				if (aAmountRange[1]!=null)
					bWithinRange &= (getTotalAmount().compareTo(aAmountRange[1])<=0);								
			} 
		}
		if (aMonthsRange!=null && aYearsRange!=null) {
			if (isNull("month") || isNull("year")) {
				bWithinRange = false;
			} else {
				Date dtStart = new Date(aYearsRange[0].intValue()-1900, aMonthsRange[0].intValue()-1, 1);
				Date dtEnd = new Date(aYearsRange[1].intValue()-1900, aMonthsRange[1].intValue()-1, 28);
				Date dtInv = new Date(getYear()-1900, getMonth()-1, 15);
				bWithinRange &= (dtInv.compareTo(dtStart)>0 && dtInv.compareTo(dtEnd)<0);
			}
		}
		return bWithinRange;
	}	
	
	public abstract TaxPayer taxPayer(Dms oDms);

	public abstract BaseCompanyObject biller(AtrilSession oSes);

	protected final int ACTION_PROCESS = 1;
	protected final int ACTION_APPROVE = 2;
	protected final int ACTION_REJECT = 3;
	protected final int ACTION_DELETE = 4;
	
}
