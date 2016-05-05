<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <title>Zesped :: Ayuda</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/img/Z16.ico" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-cssreset-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-grids-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-ubuntu.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-audiowide.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-config.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-min-351.js"></script>
    <script type="text/javascript">
      YUI().use('querystring','io-form', function(Y) {
        var qrs = Y.QueryString.parse(document.location.search.substring(1));
    	  var pag = qrs["p"];
				Y.io("help/"+pag+".htm", { method:'GET', on: {'complete':function (id, o, args) { document.getElementById('helptext').innerHTML = o.responseText+"<br/><br/><a href='#' title='Ir a la pagina anterior' onclick='window.history.back()'>Atr&aacute;s</a><br/><br/>"; }} });      
      });
    </script>
  </head>
  <body>
    <jsp:include page="/inc/header.jsp"/>
    <div id="contents" class="centeredbox">
			<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox" id="helptext">
      </div>		  		
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
    </div>
    <jsp:include page="/inc/footer.jsp"/>
  </body>
  <script type="text/javascript" src="js/login.js" defer="defer"></script>
  <script type="text/javascript" src="js/email.js" defer="defer"></script>
</html>
