package com.zesped;

import java.util.Locale;

public class Localization {

	public static final Locale ES = new Locale("es", "ES");

	public static final Locale DEFAULT_LOCALE = ES;

	public static String getCurrencySign(String sCurrCode) {
		String sCurrSign;
		if (sCurrCode.equalsIgnoreCase("EUR"))
			sCurrSign = "€";
		else if (sCurrCode.equalsIgnoreCase("USD"))
			sCurrSign = "$";
		else if (sCurrCode.equalsIgnoreCase("GBP"))
			sCurrSign = "£";
		else if (sCurrCode.equalsIgnoreCase("YEN"))
			sCurrSign = "¥";
		else
			sCurrSign = "¤";
		return sCurrSign;
	}
	
}
