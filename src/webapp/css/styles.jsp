<%@ page import="java.io.File" language="java" contentType="text/css; charset=ISO-8859-1" %><jsp:useBean id="GlobalLESSCache" scope="application" class="com.knowgate.dataxslt.LESSCache"/><%

String sCSSDir = request.getRealPath(request.getServletPath());
sCSSDir = sCSSDir.substring(0,sCSSDir.lastIndexOf(File.separator)+1);

String sFileName = request.getParameter("f");
if (sFileName==null) sFileName="styles";

if (sFileName.length()==0){
	out.write("No .less file name may not be empty");
} else {
	// GlobalLESSCache.clear();
	File oLess = new File (sCSSDir+sFileName+".less");
	if (oLess.exists())
	  out.write(GlobalLESSCache.render(oLess));
	else
		out.write(sFileName+".less file does not exist");		
}
%>