<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@ include file="atril.jspf" %>
<stripes:layout-definition>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>Zesped :: ${pageTitle}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/img/Z16.ico" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-cssreset-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-grids-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-ubuntu.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-audiowide.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp?f=datatable">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/assets/skins/sam/gallery-lightbox-skin.css" type="text/css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-config.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-min-351.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/gallery-divbox.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/autosuggest20.js"></script>
  </head>
  <body bgcolor="#6f6f6f">
    <stripes:layout-component name="header">
      <jsp:include page="/inc/header.jsp"/>
    </stripes:layout-component>

		<div id="notificationmessagebox"></div>

    <div id="contents" class="centeredbox">
			<stripes:layout-component name="contents"/>
    </div>

    <stripes:layout-component name="footer">
      <jsp:include page="/inc/footer.jsp"/>
    </stripes:layout-component>
  </body>
  <script type="text/javascript" src="js/combobox.js"></script>
  <script type="text/javascript" src="js/validations.js"></script>
  <script type="text/javascript" src="js/panel.js"></script>
  <script type="text/javascript" src="js/listings.js"></script>
  <script type="text/javascript" src="js/navigationmenu.js" defer="defer"></script>
  <script type="text/javascript" src="js/xslt.js" defer="defer"></script>
	<script type="text/javascript" defer="defer">
		var DivBoxInstance;

		YUI().use("gallery-divbox", function (Y) { DivBoxInstance = Y.Lightbox.init(); });

		function onLoadLightbox() {
		}
	
		function onCloseLightbox() {
		}

		var AutoSuggestTaxPayer = new AutoSuggest("faketaxpayerlink", { script:"'autosuggest.jsp?doctype=TaxPayer&text=business_name&'", varname:"business_name",minchars:1,form:0, callback: function (obj) { if (document.getElementById("businessnamespan").innerHTML!=obj.value) { httpRequestText("switchtaxpayer.jsp?id="+obj.id+"&name="+escape(obj.value)); document.location.reload(); } } });
	</script>    
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/lightbox.js" defer="defer"></script>
</html>
</stripes:layout-definition>