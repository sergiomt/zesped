<%@ page import="com.zesped.model.Cache" language="java" session="false" contentType="text/plain;charset=UTF-8" %><%

final String sKey = request.getParameter("key");

final String sVal = Cache.getEntryString(sKey);

if (sVal!=null) out.write(sVal);

%>