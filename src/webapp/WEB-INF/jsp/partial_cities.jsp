<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:form partial="true"  beanclass="com.zesped.action.EngageCredit">
    <stripes:select name="city">
        <stripes:options-collection collection="${actionBean.getState().get()}" value="code" label="name" />
    </stripes:select>
</stripes:form>