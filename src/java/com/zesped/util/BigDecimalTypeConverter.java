package com.zesped.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.knowgate.misc.Gadgets;

public class BigDecimalTypeConverter implements TypeConverter<BigDecimal>  {

	@Override
	public BigDecimal convert(String sAmount, Class<? extends BigDecimal> bigdecClass, Collection<ValidationError> conversionErrors) {

		BigDecimal oRetVal = null;
	    if (null!=sAmount) {	    	
	    	if (sAmount.length()>0) {
	    		final int iDot = sAmount.indexOf('.');
	    		final int iCom = sAmount.indexOf(',');

	    		if (iDot!=0 && iCom!=0) {
	    			if (iDot>iCom)
	    				sAmount = Gadgets.removeChar(sAmount,',');
	    			else
	    				sAmount = Gadgets.removeChar(sAmount,'.');
	    		} // fi
	    
	    		sAmount = sAmount.replace(',','.');
		    	try {
		    		oRetVal = new BigDecimal(sAmount);
		    	} catch (NumberFormatException nfe) {
		    		conversionErrors.add(new LocalizableError("converter.bigDecimal.numberFormatException", sAmount));
		    	}
	    	}
	    }
	    return oRetVal;
	}

	@Override
	public void setLocale(Locale arg0) {		
	}
}
