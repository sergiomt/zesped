<%@ page import="java.text.SimpleDateFormat,java.util.Date,com.zesped.DAO,com.zesped.model.User,es.ipsa.atril.sec.authentication.AtrilSession" contentType="text/plain;charset=UTF-8" language="java" %><%

final String e = request.getParameter("email");

Date d = new Date();
SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
AtrilSession oSes = DAO.getAdminSession("devservice");
String sId = User.forEmail(e);
User oUsr = new User(oSes, sId);
oUsr.setEmail(f.format(d)+"@hotmail.com");
oUsr.save(oSes);
oSes.commit();
oSes.disconnect();
oSes.close();
out.write("User updated"); 
%>