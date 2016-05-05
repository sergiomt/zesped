<%@ page import="com.zesped.model.Cache" language="java" session="false" contentType="text/plain;charset=UTF-8" %><%

final String sKey = request.getParameter("key");
final String sVal = request.getParameter("val");

Cache.putEntry(sKey, sVal);

%>