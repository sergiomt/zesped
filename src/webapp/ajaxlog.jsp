<%@ page import="java.util.Date,java.io.File,java.io.FileWriter" language="java" session="false" contentType="text/plain" %><%

  final String m = request.getParameter("m");

  if (m!=null) {
	  if (m.length()>0) {
		  final String l = new Date().toString()+" "+request.getRemoteAddr()+" "+m+"\n";
		  File oFle = new File("C:\\Program Files\\Tomcat\\logs\\ajax.log");	
		  FileWriter oWrt = new FileWriter(oFle,true);
		  oWrt.write(l, 0, l.length());
		  oWrt.close();
	  }
  }
%>