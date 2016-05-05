<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/applet.jsp" pageTitle="Digitalizar Nota de Gasto">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox">
			<table width="98%"><tr>
			  <td style="padding:8px;font-size:medium;"><stripes:label for="label.capturebillnotes" /></td>
			  <td style="padding:8px" align="right"><%=session.getAttribute("businessname")%></td>
			</tr></table>
			</div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
			
			 <stripes:form name="depositData" id="billnotedata" beanclass="com.zesped.action.UploadBillNote">
			 <stripes:hidden name="service" value="${actionBean.captureService}" />	
			 <stripes:hidden name="depositId" />
			 <stripes:select id="recipient" name="recipientTaxPayer" class="taxpayerlist" style="display:none">
			   <stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" />
			 </stripes:select>
			 <input type="radio" id="singlePage" name="multiPage" value="0" checked style="display:none" />
    	 <input type="radio" id="multiplePages" name="multiPage" value="1" style="display:none" />
			 <div class="yui3-g">
	 		   <div class="yui3-u-7-12">
	 		      <div id="applet">
            <applet id="atril-capture-applet" width="512px" height="512px" codebase="applet_Digitalizacion/" code="es/ipsa/atril/capture/applet/gui/AppletCapture.class" archive="atril-capture-applet.jar,ScanDeviceTwain.jar" MAYSCRIPT>
            <param name="scanDeviceName" value="Twain">
            <param name="scanDeviceLocator" value="">
            <param name="captureType" value="UnsignedSinglePageHalfDuplexNoGUI">
            <param name="keepAliveTimer" value="15">
            <param name="user" value="admin">
            <param name="password" value="admin">
            <param name="jsessionId" value="${pageContext.session.id}">
            <param name="servletContext" value="">
            <param name="parentId" value="${actionBean.incomingDeposits}">
            <param name="logo_path" value="<% String u=request.getRequestURL().toString(); out.write(u.substring(0,u.indexOf("/zesped/"))); %>/zesped/img/blank_document.png">
            <param name="keep_last_scanned_image" value="1">
            </applet>
           </div>
           <div id="error" class="errorMessage"></div>
           <div id="message" class="errorMessage"></div>
    			 <div style="display:none">
             <table id="digitalizationTable"></table>
    			 </div>
	 		     <stripes:messages/>
           <stripes:errors/>
	 		     <div style="display:none">
	 		     <table summary="Uploaded Files" width="512" height="448" bgcolor="white" border="0">
	 		     <tr><td width="20" rowspan="7"><img src="img/spacer.gif" width="20" height="1" alt=""></td><td height="32"><stripes:label for="label.capturedtickets" /></td></tr>
	 		     <c:forEach var="index" begin="0" end="3">
	 		       <tr><td height="32">
					   <stripes:file name="items[${index}]" />
					   </td></tr>
					 </c:forEach>
					 <tr><td height="288">&nbsp;</td></tr>
					 </table>
					 </div>
			   </div>
			   <div class="yui3-u-5-12">
			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
			     <div class="textbox" id="panelcenter">
			       <h2><stripes:label for="label.captureprocess" /></h2>
						 <table class="formfields">
						   <tr>
						     <td class="formlabelrequired"><stripes:label for="label.accountingaccount" /></td>
						     <td class="formfields">
    							   <stripes:select class="taxpayerlist" id="account" name="account"></stripes:select>
						     </td>
						   </tr>
							 <tr>
							   <td class="formlabelrequired"><stripes:label for="label.employee" /></td>
							   <td class="formfields">
									   <stripes:select id="employee" name="employee" class="taxpayerlist">
									     <option value=""></option>
									     <stripes:options-collection collection="${actionBean.employees}" value="uuid" label="name" />
								     </stripes:select>
							   </td>
							   <td><% if (session.getAttribute("role").equals(com.zesped.model.Role.admin)) { %>							   
							   <a href="EditEmployee.action" rel="lightbox"><stripes:label for="label.newm" /></a>
							   <% } %></td>
							 </tr>
						   <tr>
						     <td class="formlabelrequired"><stripes:label for="label.billNote" /></td>
						     <td class="formfields">
    							   <stripes:select class="taxpayerlist" id="concept" name="concept"></stripes:select>
						     </td>
						   </tr>
						 </table>
						 <div id="pagecounter" style="display:none"></div>
    			 </div> <!-- textbox -->
      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
      		 
      		 <div class="outerbox"></div>

					 <script type="text/javascript" src="js/combobox.js"></script>
			     <script type="text/javascript" defer="defer">
			     var cpts;
			     var rolled = false;
			     
			     YUI().use('event','io-form', function(Y) {
			    	    function populateAccounts(id, o, args) {
			    	    	var accs = document.forms["billnotedata"].account;
			    	    	var data = o.responseText.split("\n");
			    	    	clearCombo(accs);
		    	    		comboPush (accs, "", "", true, true);
			    	    	for (var a=0; a<data.length; a++) {
			    	    		var acc = data[a].split("`");
			    	    		comboPush (accs, acc[1], acc[0], false, false);
			    	    	}
			    	    }

			    	    var frm = document.forms["billnotedata"];
			    	    setCombo(frm.recipient, "<%=session.getAttribute("taxpayer_docid")%>");
			    	    setCombo(frm.employee, "${actionBean.employee}");
		    	    
				        cpts = dhtmlXComboFromSelect("concept");
			    	    cpts.loadXML("concepts.jsp?onlyopen=1&recipient=<%=session.getAttribute("taxpayer_docid")%>");
								
			    	    Y.io("accounts.jsp?recipient="+getCombo(document.forms["billnotedata"].recipient), { method:'GET', on: {'complete':populateAccounts} });
			    	    
			    	    var onChangeRecipient = function(evnt) {
			    	        evnt.preventDefault();
			    	        var rcpt = getCombo(document.forms["billnotedata"].recipient);
			    	        cpts.clearAll(true);
				    	      cpts.loadXML("concepts.jsp?onlyopen=1&recipient="+rcpt);
				    	      Y.io("accounts.jsp?recipient="+rcpt, { method:'GET', on: {'complete':populateAccounts} });
			    	    };

			    	    Y.one('#recipient').on('change', onChangeRecipient);
		     
			    	    Y.one('#billnotedata').on('submit', function(e) { cpts.confirmValue(); return true; } );			    	    
			     });	     
			     </script>

					 <!--
			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
			     <div class="textbox">
			       <h2><stripes:label for="caption.certificate" /></h2>
			       <stripes:button name="selectCertificate" class="login" />
			     </div>
      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
      		 
      		  -->
      		  
      		 <div class="outerbox"></div>

           <input id="NewDocButton" name="NewDocButton" value="Nuevo" type="button" style="display:none" />
           
					 <div id="initDiv" class="buttonsgrid"><input id="initButton" name="initButton" value="Iniciar" type="button" class="submit" /></div>
					 <div id="scanDiv" class="buttonsgrid" style="display:none"><input id="scanButton" name="scanButton" value="Digitalizar" class="submit" type="button" /></div>
					 <div id="commitDiv" class="buttonsgrid" style="display:none"><input id="commitButton" name="attach" class="submit" value="Guardar" type="button" /></div>
					 <div id="rollbackDiv" class="buttonsgrid" style="display:none"><input id="rollbackButton" name="rollbackButton" value="Cancelar" class="cancel" type="button" /></div>
					 <div id="stopDiv" class="buttonsgrid" style="display:none"><input id="stopButton" name="stopButton" value="Detener" type="button" /></div>
      		 
      		 <!-- 
			     <stripes:submit name="upload" class="submit" />
					 -->
					  
			   </div>			   
			 </div>
			 </stripes:form>
  </stripes:layout-component>
</stripes:layout-render>