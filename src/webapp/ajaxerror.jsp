<%@ page import="java.util.MissingResourceException" language="java" session="false" %><%@ include file="inc/atril.jspf" %><%

final String sErrCode = nullif(request.getParameter("e"),"unknown");
String sErrMsg;

try {
	sErrMsg = StripesResources.getString("error."+sErrCode);
} catch (MissingResourceException mre) {
	sErrMsg = StripesResources.getString("error.unknown");    	  
}

%><response>
  <header></header>
  <errors count="1">
    <error code="<%="error."+sErrCode%>" field=""><![CDATA[<%=sErrMsg%>]]></error>
  </errors>
  <messages></messages>
  <data></data>
</response>
