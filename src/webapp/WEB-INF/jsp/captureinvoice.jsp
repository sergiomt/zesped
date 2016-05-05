<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/applet.jsp" pageTitle="Digitalizar Factura">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp" />

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox">
			  <table width="98%"><tr>
			    <td style="padding:8px;font-size:medium;"><stripes:label for="caption.captureinvoices" /></td>
			    <td style="padding:8px" align="right"><%=session.getAttribute("businessname")%></td>
			  </tr></table>
			</div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
			
			 <stripes:form name="depositData" beanclass="com.zesped.action.UploadInvoice">
			 <stripes:hidden name="service" value="${actionBean.captureService}" />			 
			 <stripes:hidden name="depositId" />
			 <stripes:select id="recipient" name="recipientTaxPayer" class="taxpayerlist" style="display:none">
			   <stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" />
			 </stripes:select>
			 <div class="yui3-g">
	 		   <div class="yui3-u-7-12">
	 		      <div id="applet">
            <applet id="atril-capture-applet" width="512px" height="512px" codebase="applet_Digitalizacion/" code="es/ipsa/atril/capture/applet/gui/AppletCapture.class" archive="atril-capture-applet.jar,ScanDeviceTwain.jar" MAYSCRIPT>
            <param name="scanDeviceName" value="${actionBean.scanDeviceName}">
            <param name="scanDeviceLocator" value="">
            <param name="captureType" value="${actionBean.captureType}">
            <param name="keepAliveTimer" value="15">
            <param name="user" value="<%=session.getAttribute("nickname")%>">
            <param name="password" value="<%=session.getAttribute("password")%>">
            <param name="jsessionId" value="${pageContext.session.id}">
            <param name="servletContext" value="">
            <param name="parentId" value="${actionBean.incomingDeposits}">
            <param name="logo_path" value="<% String u=request.getRequestURL().toString(); out.write(u.substring(0,u.indexOf("/zesped/"))); %>/zesped/img/blank_document.png">
            <param name="keep_last_scanned_image" value="1">            
            </applet>
           </div>
           <div id="error" class="errorMessage"></div>
           <div id="message" class="errorMessage"></div>
    			 <div style="display:none"><table id="digitalizationTable"></table></div>
	 		     <stripes:messages/>
           <stripes:errors/>
           <div id="attacherrors"></div>	       
    			 <div style="display:none">
	 		       <table summary="Uploaded Files" width="512" height="256" bgcolor="white" border="0">
	 		         <tr><td width="20" rowspan="7"><img src="img/spacer.gif" width="20" height="1" alt=""></td><td height="32"><stripes:label for="label.capturedpages" /></td></tr>
	 		         <c:forEach var="index" begin="0" end="3">
	 		           <tr><td height="32">
					         <stripes:file name="items[${index}]" />
					       </td></tr>
					     </c:forEach>
					     <tr><td height="20">&nbsp;</td></tr>
					   </table>
					 </div>
			   </div> <!-- yui3-u-7-12 -->
			   
			   <div class="yui3-u-5-12">
			     <div id="hiddenbiller" style="display:none">
				     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
				     <div class="textbox">
				       <h2><stripes:label for="caption.captureprocess"/></h2>
							 <table class="formfields">
							   <tr>
							     <td class="formlabeloptional"><stripes:label for="label.biller" /></td>
							     <td class="formlabeloptional">
									   <stripes:select id="biller" name="billerTaxPayer" class="taxpayerlist">
								       <stripes:option value="" />
								       <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" />
								     </stripes:select>
							     </td>
							     <td><a href="EditClient.action" rel="lightbox"><stripes:label for="label.newm"/></a></td>
							   </tr>
							 </table>
	    			 </div>
	      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
	      		 <div class="outerbox"></div>
					 </div> <!-- hidden biller -->
					 
			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>

			     <div id="panelcenter" class="textbox">
			     <a id="settings" class="yui3-toggle"><font class="h2nolf"><stripes:label for="caption.deviceoptions"/></font>&nbsp;&nbsp;<span id="arrow" class="">&#9660;</span></a>
			     <div id="settings" class="yui3-module">
    			   <div class="yui3-bd" style="height:0px;float:left;clear:right;overflow:hidden;">
    			     <fieldset>
    			       <legend><stripes:label for="label.signature"/></legend>
    			       <input type="radio" id="signed" name="sign" value="1" ${actionBean.sign ? "checked" : "" } />&nbsp;<stripes:label for="label.withsignature" /> del cliente
    			       <br/>
    			       <input type="radio" id="serversigned" name="sign" value="2" ${actionBean.serverSign ? "checked" : "" } />&nbsp;<stripes:label for="label.withsignature" /> de zesped
    			       <br/>
    			       <input type="radio" id="unsigned" name="sign" value="0" ${actionBean.sign ? "" : "checked" } />&nbsp;<stripes:label for="label.withoutsignature" />
    			     </fieldset>
    			     <fieldset>
    			       <legend>Gesti&oacute;n de P&aacute;ginas</legend>
    			       <input type="radio" id="singlePage" name="multiPage" value="0" ${actionBean.multiPage ? "" : "checked" } />&nbsp;<stripes:label for="label.singlePage" />
    			       <br/>
    			       <input type="radio" id="multiplePages" name="multiPage" value="1" ${actionBean.multiPage ? "checked" : "" } />&nbsp;<stripes:label for="label.multiplePages" />
							   <br/>
							   <input type="checkbox" id="fullDuplex" name="fullDuplex" value="1" ${actionBean.fullDuplex ? "checked" : "" } />&nbsp;Capturar reversos
    			     </fieldset>
    			     <fieldset>
    			       <legend>Otras opciones del scanner</legend>
							   <stripes:checkbox id="gui" name="gui" value="1" />&nbsp;<stripes:label for="label.scannergui" />
    			     </fieldset>
    			     <fieldset>
    			       <legend><stripes:label for="label.servicetype"/></legend>
							     <stripes:radio id="basicServiceFlavor" name="serviceFlavor" value="basic" checked="checked" />
							     &nbsp;<stripes:label for="label.servicebasic"/>&nbsp;&nbsp;&nbsp;&nbsp;
							     <c:choose>
							     <c:when test="${sessionScope.can_premium=='true'}">
							       <stripes:radio id="premiumServiceFlavor" name="serviceFlavor" value="premium" tabindex="-1" disabled="disabled" />&nbsp;<font color="dimgray"><stripes:label for="label.servicepremium"/></font></td>
							     </c:when>
							     <c:otherwise>
							       <stripes:radio id="premiumServiceFlavor" name="serviceFlavor" value="premium" />&nbsp;<stripes:label for="label.servicepremium"/></td>
							     </c:otherwise>
							     </c:choose>
    			     </fieldset>
             </div>
           </div> 

			     <script type="text/javascript">

			     var rolled = false;

			     YUI().use('anim','io-form','cookie', function(Y) {
			    	    var roll = new Y.Anim({
			    	        node: '#settings .yui3-bd',
			    	        to: { height: 360 },
			    	        easing: Y.Easing.backIn
			    	    });
			    	    var unroll = new Y.Anim({
			    	        node: '#settings .yui3-bd',
			    	        to: { height: 0 },
			    	        easing: Y.Easing.backIn
			    	    });
			    	 
			    	    var onClick = function(e) {
			    	        e.preventDefault();
			    	        if (rolled) {
			    	        	unroll.run();
			    	        	document.getElementById("arrow").innerHTML = "&#9660";
			    	        } else {
			    	        	roll.run();
			    	        	document.getElementById("arrow").innerHTML = "&#9650";
			    	        }
			    	        rolled=!rolled;
			    	    };
			    	    Y.one('#settings').on('click', onClick);

			    	    saveCaptureOptionsComplete = function (id, o, args) {
							    var xml = o.responseXML;
							    if (xml==null) {
							      document.location = "error.jsp?e=expiredsession";
							    } else {
					    	    document.location.reload();
							    }
						    };
			    	    var onCaptureOptionsChange = function(e) {
			    	        disableButton("initButton");
			    	    	  disableButton("scanButton");
			    	        disableButton("NewDocButton");
			    	        disableButton("commitButton");
			    	        disableButton("rollbackButton");
			    	        disableButton("stopButton");
			    	        spinner.spin(document.getElementById("workDiv"));
			    	    	  if (rolled) {
			    	        	unroll.run();
			    	        	document.getElementById("arrow").innerHTML = "&#9660";
			    	        } 
			    	        Y.io("SaveCaptureOptions.action", { method:'POST', form:{id:document.forms['depositData']}, on: {'complete':saveCaptureOptionsComplete} });
			    	    };
			    	    
			    	    var onServiceFlavorChange = function(e) {
			    	    	Y.Cookie.set("defaultserviceflavor", getCheckedValue(document.forms["depositData"].serviceFlavor));
			    	    };
			    	    	
			    	    Y.one('#signed').on('click', onCaptureOptionsChange);
			    	    Y.one('#unsigned').on('click', onCaptureOptionsChange);
			    	    Y.one('#serversigned').on('click', onCaptureOptionsChange);
			    	    Y.one('#gui').on('click', onCaptureOptionsChange);
			    	    Y.one('#singlePage').on('click', onCaptureOptionsChange);
			    	    Y.one('#multiplePages').on('click', onCaptureOptionsChange);
			    	    Y.one('#fullDuplex').on('click', onCaptureOptionsChange);
			    	    Y.one('#basicServiceFlavor').on('click', onServiceFlavorChange);
			    	    Y.one('#premiumServiceFlavor').on('click', onServiceFlavorChange);
			    	    
			    	    var opt = document.getElementById("recipient").options;
			    	    var len = opt.length;			    	    
			    	    for (var i=0; i<len; i++)
			    	      if (opt[i].value == "<%=session.getAttribute("taxpayer_docid")%>") {
			    	  	      opt.selectedIndex = i; break;
			    	      }
			    	    
			    	    if (Y.Cookie.get("defaultserviceflavor")=="premium")
			    	    	setCheckedValue(document.forms["depositData"].serviceFlavor,"premium");
			    	    else
			    	    	setCheckedValue(document.forms["depositData"].serviceFlavor,"basic");	    	    	
			    	});	     
			     </script>
			      
			     </div>
			      
      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
					
      		 <div class="outerbox"></div>

			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
			     <div class="textbox">
			       <h2><stripes:label for="caption.progress"/></h2>
    			   <stripes:label for="label.capturedpages"/>: <span id="pagecounter">0</span>
    			 </div>
      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
      		 <div class="outerbox"></div>

           <input id="NewDocButton" name="NewDocButton" value="Nuevo" type="button" style="display:none" />
           <table width="326" height="88"><tr><td>	 
					   <tr height="44">
					     <td width="138">
					       <div id="initDiv" class="buttonsgrid"><input id="initButton" name="initButton" value="Iniciar" type="button" class="submit" /></div>
					       <div id="scanDiv" class="buttonsgrid" style="display:none"><input id="scanButton" name="scanButton" value="Digitalizar" class="submit" type="button" /></div>
					     </td>
					     <td width="138">
					       <div id="stopDiv" class="buttonsgrid" style="display:none"><input id="stopButton" name="stopButton" value="Detener" type="button" /></div>
					     </td>
					     <td with="50" rowspan="2"><div id="workDiv"></div></td>
					   </tr>
					   <tr height="44">
					     <td width="138">
					       <div id="commitDiv" class="buttonsgrid" style="display:none"><input id="commitButton" name="attach" class="submit" value="Guardar" type="button" /></div>
					     </td>
					     <td width="138">
					       <div id="rollbackDiv" class="buttonsgrid" style="display:none"><input id="rollbackButton" name="rollbackButton" value="Cancelar" class="cancel" type="button" /></div>
					     </td>
					   </tr>
					 </table>
					 <br/><br/><br/><br/>
			   </div> <!-- yui3-u-5-12 -->
			 </div> <!-- yui3-g -->
			 </stripes:form>
  </stripes:layout-component>
</stripes:layout-render>