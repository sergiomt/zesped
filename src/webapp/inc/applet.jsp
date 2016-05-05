<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@ include file="atril.jspf" %>
<stripes:layout-definition>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Zesped :: ${pageTitle}</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/img/Z16.ico" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-cssreset-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-grids-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-ubuntu.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-audiowide.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp?f=datatable">    
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/assets/skins/sam/gallery-lightbox-skin.css" type="text/css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/dhtmlxcombo.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-config.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-min-351.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/gallery-divbox.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/autosuggest20.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/spin.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/DocumentListener.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/json/json2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/javascriptStringFunctions.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/AppletFunctions.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/combobox.js"></script>
    <script type="text/javascript">
    var listener = new DocumentListener();
    YUI().use("event", function (Y) {
        Y.on("domready", function (e) {
        	  setInitialButtonState();
            listener.setOnError(errorCallback);
            listener.setOnUserActionNeeded(userActionNeededCallback);
            listener.setOnScannerInitialized(scannerInitializedCallback);
            listener.setOnServerError(serverErrorCallback);
            listener.setOnDepositConfirmed(depositConfirmedCallback);
            listener.setOnViewCodeline(viewCodelineCallback);
            listener.setOnDocumentProcessed(documentProcessedCallback);
            listener.setOnPageProcessed(pageProcessedCallback);
            listener.setOnStartDocumentProcess(startDocumentProcessCallback);
            listener.setOnDepositRolledBack(depositRolledBackCallback);
            listener.setOnFatalError(fatalErrorCallback);
            listener.setOnImageReady(imageReadyCallback);

        });  
    });    
    </script>    
	</head>
  <body>
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
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/navigationmenu.js" defer="defer"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/xslt.js" defer="defer"></script>
		<script type="text/javascript" defer="defer">
		  var DivBoxInstance;

		  var pagecount = 0;

		  var scanning = false;

		  window.dhx_globalImgPath="${pageContext.request.contextPath}/css/";

		  function onLoadLightbox() {
		  }
	
		  function onCloseLightbox() {
		  }
		  
		  var AutoSuggestTaxPayer = new AutoSuggest("faketaxpayerlink", { script:"'autosuggest.jsp?doctype=TaxPayer&text=business_name&'", varname:"business_name",minchars:1,form:0, callback: function (obj) { if (document.getElementById("businessnamespan").innerHTML!=obj.value) { httpRequestText("switchtaxpayer.jsp?id="+obj.id+"&name="+escape(obj.value)); document.location.reload(); } } });
	  
		  YUI().use("gallery-divbox","node","io-form", function (Y) {
			  DivBoxInstance = Y.Lightbox.init();

				function validateBillNote() {
				  var frm = document.forms["depositData"];
				  if (frm.account) {
					  var msg = "";
				    if (frm.account.selectedIndex<=0)
					    msg = "&bull;&nbsp;<%=StripesResources.getString("com.zesped.action.UploadBillNote.account.valueNotPresent")%>.<br/>";
				    if (frm.employee.selectedIndex<=0)
					    msg += "&bull;&nbsp;<%=StripesResources.getString("com.zesped.action.UploadBillNote.employee.valueNotPresent")%>.<br/>";
				    if (cpts.getComboText().length==0)
					    msg += "&bull;&nbsp;<%=StripesResources.getString("com.zesped.action.UploadBillNote.concept.valueNotPresent")%>.<br/>";
					  if (msg.length>0) {
	    			  msg = "<b><%=StripesResources.getString("error.header")%></b><br/>"+msg;
						  Y.one('#alertsidebox .message').setHTML(msg);
	    			  Y.one('#alertsidebox .message').set('className', 'message dialog-info');
	    			  alertsidebox.show();
						  return false;						
					  } else {
						  return true;
					  }
				  } else {
					  return true;
				  }
				}

				var attachInterval;

				commitDepositComplete = function (id, o, args) {
						var xml = o.responseXML;
						if (xml==null) {
						  document.location = "error.jsp?e=expiredsession";
						} else {
						  var rsp = xml.documentElement;
						  var div = document.getElementById("attacherrors");
							var cde = getElementAttribute(rsp.getElementsByTagName("errors")[0], "error", "code");
							var erc = Number(getElementAttribute(rsp, "errors", "count"));
							var ret;
							if (erc==0) {
								document.forms["depositData"].depositId.value="";
								spinner.stop();
								Y.one('#alertsidebox .message').setHTML('Documento digitalizado con &eacute;xito');
	    			    Y.one('#alertsidebox .message').set('className', 'message dialog-sucess');
	    			    alertsidebox.show();
	    			    scanning = false;
	    			    pagecount=0;
	    			    document.getElementById("pagecounter").innerHTML=String(pagecount);
	    			    enableButton("scanButton");
							} else if (cde.substr(0,6)=="error.") {
								document.location = "enter.jsp?e="+cde;            	
								ret = "";
							} else {
								var hgt = 20+erc*20;
								if (hgt>200) hgt = 200;
								loadHTMLFromDoc(rsp, "xsl/errormessages.xsl", div);
								div.style.height=String(hgt)+"px";
								ret = "";
							}
						}
				};
				
				function attachDeposit() {
					ajaxlog("attachDeposit("+document.forms["depositData"].depositId.value+")");
					spinner.spin(document.getElementById("workDiv"));
			    if (document.forms["depositData"].depositId.value.length>0) {
			    	clearInterval(attachInterval);
			    	Y.io("AttachDeposit.action", { method:'POST', form:{id:document.forms['depositData']}, on: {'complete':commitDepositComplete} });						  
			    }
				  scanning = false;
				}
				
			  var commitClick = function (e) {
				  ajaxlog("commitClick("+scanning+")");
				  if (scanning) {
				    Y.one('#confirmsidebox .message').setHTML('Esta accion grabar&aacute; las p&aacute;ginas digitalizadas como un &uacute;nico documento. &iquest;Esta seguro?');
				    confirmsidebox.callback = function() {
				    	setCommitClick();
				    	attachInterval = setInterval(attachDeposit,100);
				    };
				    confirmsidebox.show();
					}
			  };
			  Y.on("click", commitClick, "#commitButton");

			  var rollbackClick = function (e) {
				  if (scanning) {
					  scanning = setRollbackClick("Esta accion descartara las paginas digitalizadas. Esta seguro?");
					}
			  };
			  Y.on("click", rollbackClick, "#rollbackButton");
			  
			  var scanClick = function (e) {
			    ajaxlog("scanClick("+scanning+")");
			    if (validateBillNote()) {
			      if (scanning) {
					    setScanClick();
			      } else {
			    	  setNewDocScanClick();
			    	  scanning = true;
					  }
			    }
			  };
			  Y.on("click", scanClick, "#scanButton");

			  var initClick = function (e) {
				  if (rolled) {
					  new Y.Anim({node:'#settings .yui3-bd',to:{ height: 0 },easing:Y.Easing.backIn}).run();
					  document.getElementById("arrow").innerHTML = "&#9660";
				  }
				  if (!scanning) {
					  setInitButtonClick("es");
					}
			  };
			  Y.on("click", initClick, "#initButton");

			  var stopClick = function (e) {
					  setStopClick();
			  };
			  Y.on("click", stopClick, "#stopButton");

		  });
		  
		  var spinner = new Spinner({
			  lines: 13, // The number of lines to draw
			  length: 7, // The length of each line
			  width: 4, // The line thickness
			  radius: 10, // The radius of the inner circle
			  corners: 1, // Corner roundness (0..1)
			  rotate: 0, // The rotation offset
			  color: '#000', // #rgb or #rrggbb
			  speed: 1, // Rounds per second
			  trail: 60, // Afterglow percentage
			  shadow: false, // Whether to render a shadow
			  hwaccel: false, // Whether to use hardware acceleration
			  className: 'spinner', // The CSS class to assign to the spinner
			  zIndex: 2e9, // The z-index (defaults to 2000000000)
			  top: 'auto', // Top position relative to parent in px
			  left: 'auto' // Left position relative to parent in px
			});
		</script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/panel.js" defer="defer"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/lightbox.js" defer="defer"></script>
</body>
</html>
</stripes:layout-definition>