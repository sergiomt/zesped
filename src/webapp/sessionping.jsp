<%@ page language="java" session="true" contentType="text/plain" %><%
  final Object sNickN = session.getAttribute("nickname");
  final Object sPassw = session.getAttribute("password");
  out.write(sNickN==null || sPassw==null ? "0" : "1");
%>