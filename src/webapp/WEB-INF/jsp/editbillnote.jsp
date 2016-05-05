<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
  com.zesped.action.EditBillNote oAbn = (com.zesped.action.EditBillNote) request.getAttribute("actionBean");
%><stripes:layout-render name="/inc/lightbox.jsp" pageTitle="Entrar Datos de Ticket de Gasto">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox"><span style="padding:8px;font-size:medium;"><stripes:label for="caption.editTicket" /></span></div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>

			 <div class="yui3-g">
	 		   <div class="yui3-u-7-12">
				 <c:choose>
         <c:when test="${not empty actionBean.thumbnail}" >
            <% java.util.ArrayList<String> oPags = oAbn.getPages(); 
               if (oPags.size()>0) { %>
	 		      <a href="itemjpeg.jsp?i=<%=oPags.get(0)%>" target="_blank"><img src="itemjpeg.jsp?i=<%=oAbn.getThumbnail().id()%>" alt="" border="0" /></a> <% } %>	 		     
	 		     <table><tr>
	 		     <%
	 		        if (oPags.size()>0) {
					      out.write("<td><a href=\"itemraw.jsp?i="+oPags.get(0)+"\" target=\"_blank\"><img src=\"img/x_pdf.gif\" alt=\"Expand\" width=\"16\" height=\"16\" border=\"0\"/></a></td><td>&nbsp;<a class=\"texthighlight\" href=\"itemjpeg.jsp?i="+oPags.get(0)+"\" target=\"_blank\">Ampliar</a></td>");
	 		    	    for (int p=0; p<oPags.size(); p++)
								  out.write("<td>&nbsp;<a class=\"texthighlight\" href=\"itemraw.jsp?i="+oPags.get(p)+"\" target=\"_blank\">"+String.valueOf(p+1)+"</a></td>");
	 		        }
	 		     %>
	 		     </tr></table>
	 		     <br/>
	 		     ___________________________________________________
         </c:when>
         <c:otherwise>
					 <br/><br/>
           <stripes:label for="label.previewNotAvailable" />
         </c:otherwise>
         </c:choose>	 		   
			   </div>
			   <div class="yui3-u-5-12">
				   <stripes:form id="billnotedata" beanclass="com.zesped.action.EditBillNote">
			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
			     <div class="textbox">
					 <stripes:hidden name="id" />
	 		     <div class="fullpanel">
	 		       <stripes:messages/>
             <stripes:errors/>			       
				   </div>
					  
					 <div class="fullpanel">					     
					     <h2><stripes:label for="caption.ticketdata" /></h2>
		        	 <table class="formfields" summary="Biller and Payer">
							   <tr>
							     <td class="formlabeloptional"><stripes:label for="label.payer" /></td>
							     <td class="formfields">
									   <stripes:select id="recipient" name="recipientTaxPayer" class="taxpayerlist">
								       <stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" />
								     </stripes:select>
							     </td>
							     <td></td>
							   </tr>
							   <tr>
							     <td class="formlabeloptional"><stripes:label for="label.biller" /></td>
							     <td class="formfields">
									   <stripes:select id="biller" name="billerTaxPayer" class="taxpayerlist">
								       <stripes:option value="" />
								       <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" />
								     </stripes:select>
							     </td>
							     <td><a href="EditClient.action" rel="lightbox"><stripes:label for="label.newm" /></a></td>
							   </tr>
							   <tr>
							     <td class="formlabeloptional"><stripes:label for="label.employee" /></td>
							     <td class="formfields">
									   <stripes:select id="employee" name="employee" class="taxpayerlist">
									     <option value=""></option>
									     <stripes:options-collection collection="${actionBean.employees}" value="uuid" label="name" />
								     </stripes:select>
							     </td>
							     <td><a href="EditEmployee.action" rel="lightbox"><stripes:label for="label.newm" /></a></td>
							   </tr>
							 </table>

					     <table>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.ticketnumber" />:</td>
					         <td><stripes:text id="number" name="number" size="8" /></td>
					       </tr>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.date" />:</td>
					         <td>
			       				<stripes:select name="billDay"><option value=""><stripes:options-collection collection="${actionBean.days}" value="value" label="name" /></stripes:select>
			       				<stripes:select name="billMonth"><option value=""></option><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
			       				<stripes:select name="billYear"><option value=""></option><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
					         </td>
					       </tr>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.accountingaccount" /></td>
					         <td>
    							   <stripes:select style='width:200px;' id="account" name="account"></stripes:select>
					        </td>
					       </tr>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.billNote" /></td>
					         <td>
    							   <table><tr><td><stripes:select style='width:200px;' id="concept" name="concept"></stripes:select></td><td>&nbsp;<a href="FastEditBillNote.action?id=${actionBean.billNoteId}"><stripes:label for="label.show" /></a></td></tr></table>
					         </td>
					       </tr>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.description"  />:</td>
					         <td><stripes:text size="30" id="description" name="description" /></td>
					       </tr>
					     </table>
					 </div>

					 <div class="fullpanel">
					     <table>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.baseAmount" />:</td>
					         <td class="formlabeloptional"><stripes:text id="baseAmount" name="baseAmount" size="8" value="${actionBean.baseAmount}" /></td>
					         <td class="formlabeloptional"><stripes:label for="label.currency" />:</td>
					         <td class="formlabeloptional"><stripes:select id="currency" name="currency"><option value="EUR" selected="selected">EUR</option><option value="USD">USD</option><option value="GBP">GBP</option><option value="ARS">ARS</option><option value="BRL">BRL</option><option value="CAD">CAD</option><option value="CHF">CHF</option><option value="CLP">CLP</option><option value="MXN">MXN</option><option value="RUB">RUB</option><option value="VEB">VEB</option></stripes:select></td>
					       </tr>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.vat" />:</td>
					         <td class="formlabeloptional">
					           <stripes:select id="vatPct" name="vatPct"><stripes:options-collection collection="${actionBean.vatPercents}" value="value" label="percentage" /></stripes:select>
					         </td>
					         <td colspan="2"><div id="vat"></div></td>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.totalAmount" />:</td>
					         <td class="formlabeloptional"><stripes:text id="totalAmount" name="totalAmount" size="8" value="${actionBean.totalAmount}" /></td>
					         <td colspan="2"></td>
					       </tr>
			       		 <tr>
			             <td class="formlabeloptional"><stripes:checkbox id="processed" name="processed" /></td>
			             <td class="formfields" colspan="3"><stripes:label for="label.markAsProcessed" /></td>
			           </tr>
					     </table>
					 </div>					 
			   </div> <!-- textbox -->
			   <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
				   <div class="outerbox"></div>
				   <c:choose>
           <c:when test="${actionBean.open}" ><stripes:submit id="save" name="save" class="submit" /></c:when>
           <c:otherwise><stripes:submit id="reopen" name="reopen" class="submit" /></c:otherwise>
				   </c:choose>
			     &nbsp;&nbsp;
			     <input id="cancel" type="button" class="cancel" value="Cancelar" />
			   </stripes:form>
			   </div> <!-- yui3-u-5-12 -->
			 </div>
			 <script type="text/javascript" src="js/validations.js"></script>
  			 <script type="text/javascript">

         var cpts = dhtmlXComboFromSelect("concept");
  			 
  			   function populateAccounts(id, o, args) {
    	    	var accs = document.forms["billnotedata"].account;
    	    	var data = o.responseText.split("\n");
    	    	clearCombo(accs);
   	    		comboPush (accs, "", "", true, true);
    	    	for (var a=0; a<data.length; a++) {
    	    		var acc = data[a].split("`");
    	    		comboPush (accs, acc[1], acc[0], false, false);
    	    	}
    	    	setCombo(accs, "<%=oAbn.getAccount().getUuid()%>");
    	    }

    	    function populateEmployees(id, o, args) {
    	    	var emps = document.forms["billnotedata"].employee;
    	    	var data = o.responseText.split("\n");
    	    	clearCombo(emps);
   	    		comboPush (emps, "", "", true, true);
    	    	for (var e=0; e<data.length; e++) {
    	    		var emp = data[e].split("`");
    	    		comboPush (emps, emp[1], emp[0], false, false);
    	    	}
    	    }

    	    function switchEmployee(id, o, args) {
    	    	var empl = o.responseText;
    	    	if (empl.length>0)
    	    		setCombo (document.forms["billnotedata"].employee, empl);
    	    }
    	    							
  			    YUI().use('event','io-form', function (Y) {
  		    
	    	    var onChangeRecipient = function(evnt) {
	    	        evnt.preventDefault();
	    	        var rcpt = getCombo(document.forms["billnotedata"].recipient);
 			    		  cpts.setComboText("");
	    	        cpts.clearAll(true);
		    	      cpts.loadXML("concepts.jsp?onlyopen=1&recipient="+rcpt);
		    	      Y.io("accounts.jsp?recipient="+rcpt, { method:'GET', on: {'complete':populateAccounts} });
	    	    };

	    	    Y.one('#recipient').on('change', onChangeRecipient);
  		    	  
  		    	  function computeTotal(e) {
	    		    var val = document.forms["billnotedata"].baseAmount.value;			    
	    		    if (!isFloatValue(val)) {
	        		  document.getElementById("totalAmount").value = "";			        		
	        	  } else {
	        		  val = parseMoney(val);
	        		  var cur = document.getElementById("currency");
	        		  var pct = document.getElementById("vatPct");
	       			  var vat = Math.round(val*parseMoney(pct.options[pct.selectedIndex].value)*100)/100;

	        		  document.getElementById("vat").innerHTML = formatMoney(vat) + "&nbsp;" + cur.options[cur.selectedIndex].text;
	        		  document.getElementById("totalAmount").value = formatMoney(val+vat);
	        	  }
	    	    }

  		    	  Y.on("domready", function (e) {
  				        	var frm = document.forms["billnotedata"];
  			    	    	cpts.loadXML("concepts.jsp?onlyopen=0&recipient="+frm.recipient.options[frm.recipient.selectedIndex].value+(frm.employee.selectedIndex==0 ? "" : "&employee="+frm.employee.options[frm.employee.selectedIndex].value));
  							  	cpts.setComboText("${actionBean.concept}");
  						    	cpts.setComboValue("${actionBean.concept}");
  							    cpts.attachEvent("onChange", function() {
  								    Y.io("employeeforconcept.jsp?taxpayer="+getCombo(document.forms["billnotedata"].recipient)+"&concept="+escape(cpts.getSelectedText()), { method:'GET', on: {'complete':switchEmployee} });
  							    });

  	    		    	  Y.io("accounts.jsp?recipient="+getCombo(document.forms["billnotedata"].recipient), { method:'GET', on: {'complete':populateAccounts} });
  	    		    	  
  	    		    <%  if (!oAbn.getOpen()) { %>
  		    	    			document.getElementById("number").disabled=true;
  	    		    	    document.getElementById("recipient").disabled=true;
  	    		    	    document.getElementById("biller").disabled=true;
  	    		    	    document.getElementById("employee").disabled=true;
  	    		    	    document.getElementById("baseAmount").disabled=true;
  	    		    	    document.getElementById("totalAmount").disabled=true;
  	    		    	    document.getElementById("vatPct").disabled=true;
    	    					document.getElementById("currency").disabled=true;
  	    		    	    document.getElementById("account").disabled=true;
  	    		    	    document.getElementById("description").disabled=true;
  	    		    	    document.getElementById("processed").disabled=true;
  	    		    <%  } %>
  		    	  });
  		    	  
			    	  var empys = Y.one("#employee");
			    	  empys.on('change', function (e) {
			    		  var frm = document.forms["billnotedata"];
			    		  var rtp = frm.recipientTaxPayer.options[frm.recipientTaxPayer.selectedIndex].value;
			    		  var emp = null;
			    		  if (frm.employee.selectedIndex>0)
			    		    emp = frm.employee.options[frm.employee.selectedIndex].value;
			    		  if (emp.length==0) emp = null;
			    		  cpts.setComboText("");
			    		  cpts.clearAll(true);
			    	    cpts.loadXML("concepts.jsp?onlyopen=1&recipient="+rtp+(emp==null ? "" : "&employee="+emp));
	    	    } );
  		    	  
  			    	var basea = Y.one("#baseAmount");
  			    	basea.on('blur', function (e) { computeTotal(e); } );

  			    	var vatpct = Y.one("#vatPct");
  			    	vatpct.on('change', function (e) { computeTotal(e); } );
  			    	
  			    	var total = Y.one("#totalAmount");
  			    	total.on('focus', function (e) {
  			    			document.getElementById("baseAmount").focus();
  			    	});
  			    	
  			    	Y.one("#cancel").on('click', function (e) {
  			    		document.location="CaptureBillNote.action";
  			    	});

  			    	Y.one("#cancel").on('click', function (e) { window.history.back(); } );

	    	    Y.one('#billnotedata').on('submit', function(e) { cpts.confirmValue(); return true; } );			    	    
  			    	
  			    });
  			 </script>
  </stripes:layout-component>
</stripes:layout-render>