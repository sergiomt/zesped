<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-cssreset-min-351.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp?f=protomultiselect" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/protoculous-effects-shrinkvars.js" charset="utf-8"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/textboxlist.js" charset="utf-8"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/protomultiselect.js"  charset="utf-8"></script>
  </head>
  <body>
     <form>
     <ol>
       <li id="facebook-list" class="input-text">
         <input type="text" id="facebook-demo" />
         <div id="facebook-auto">
           <div class="default"></div>
             <ul class="feed"><c:forEach items="${actionBean.selectedTaxPayers}" var="stp">
               <li value="${stp.value}">${stp.name}</li>
						 </c:forEach></ul>
         </div>                
      </li>
    </ol>
    </form>
  </body>
  <script type="text/javascript">
    <!--
      var tlist2;
      document.observe('dom:loaded', function() {
    	  tlist2 = new FacebookList('facebook-demo', 'facebook-auto', {fetchFile:'taxpayers_json.jsp'});
      });
    //-->
  </script>
</html>