<%@ page import="java.util.Date,com.zesped.DAO,com.zesped.model.*" contentType="text/html;charset=UTF-8" language="java" %><% 

    User u = new User();
		// u.load(1);
		out.write(u.getFirstName()+" "+u.getLastName()+"<br/>");
		
		out.write(new Date().toString()+" 1.0:OK");
%>