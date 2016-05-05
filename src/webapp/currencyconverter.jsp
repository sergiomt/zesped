<%@ page import="com.zesped.Log,com.knowgate.math.Money" language="java" session="true" contentType="text/plain" %><%

	final String sAmount = request.getParameter("amount");
  final String sCurrencyFrom = request.getParameter("from");
  final String sCurrencyTo = request.getParameter("to");

  try {
    out.write(new Money (sAmount.replace(',','.'), sCurrencyFrom).convertTo(sCurrencyTo).round2().toString());
  } catch (Exception e) {
	  out.write("NaN");
	  Log.out.error("Currency Converter "+e.getClass().getName()+" "+e.getMessage()+" for "+sAmount+" from "+sCurrencyFrom+" to "+sCurrencyTo);
  }
%>