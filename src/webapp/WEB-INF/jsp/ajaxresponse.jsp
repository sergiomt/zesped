<%@ page language="java" session="true" contentType="text/xml" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><response>
  <header>Por favor corrija los siguientes errores:</header>
  <errors count="${actionBean.errorsCount}">
  <c:forEach var="e" items="${actionBean.errors}">
    <error code="" field="${e.fieldName==null ? '' : e.fieldName}"><![CDATA[${e.message}]]></error>
  </c:forEach>
  </errors>
  <messages>
  <c:forEach var="m" items="${actionBean.informationMessages}">
    <message code="" field="${m.fieldName==null ? '' : m.fieldName}"><![CDATA[${m.message}]]></message>
  </c:forEach>
  </messages>
  <data>
  <c:forEach var="d" items="${actionBean.responseData}">
    <line field="d.name"><![CDATA[${d.value}]]></line>
  </c:forEach>
  </data>
</response>