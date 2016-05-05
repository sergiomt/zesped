<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><stripes:layout-render name="/inc/dhtmlxcombo.jsp" pageTitle="Nota de Gasto Digitalizada">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox"><span style="padding:8px;font-size:medium;"><stripes:label for="label.capturedticket" /></span></div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
			
			 <div class="yui3-g">
	 		   <div class="yui3-u-7-12">
			     <img src="image.jsp?i=${actionBean.capturedPage1}" width="512" />  
			   </div>
			   <div class="yui3-u-5-12">

			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
			     <div class="textbox">
			       <h2><stripes:label for="caption.captureresult" /></h2>
        		 <stripes:messages/>
             <stripes:errors/>
						 <table class="formfields">
						   <tr><td class="formlabeloptional"><stripes:label for="label.processdate" />:</td><td><%=new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date())%></td></tr>
						   <tr><td class="formlabeloptional"><stripes:label for="label.capturedtickets" />:</td><td>${actionBean.capturedCount}</td></tr>
						 </table>
    			 </div>
      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>

      		 <div class="outerbox"></div>

			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
			     <stripes:form id="billnotedata" beanclass="com.zesped.action.EditBillNote">
			     <stripes:hidden name="recipient" />
			     <div class="textbox">
			       <h2><stripes:label for="label.ticketdata" /></h2>

					     <table>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.ticketnumber" />:</td>
					         <td class="formlabeloptional"><stripes:text name="number" size="8" /></td>
					       </tr>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.date" />:</td>
					         <td>
			       			   <stripes:select name="day"><%
			       			     for (int d=1; d<=31; d++) {
			       			    	 out.write("<option value=\""+(d<10 ? "0" : "")+String.valueOf(d)+"\">"+String.valueOf(d)+"</option>");
			       			     }
			       					%></stripes:select>
			       			   <stripes:select name="month"><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
			       				 <stripes:select name="year"><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
					         </td>
					       </tr>
					     </table>
					     <table>
					       <tr>
					         <td class="formlabelrequired"><stripes:label for="label.accountingaccount" /></td>
					         <td>
    							   <stripes:select style='width:200px;' id="account" name="account">
    							   <stripes:options-collection collection="${actionBean.accounts}" value="uuid" label="description" />
    							   </stripes:select>
					        </td>
					       </tr>
					       <tr>
					         <td class="formlabelrequired"><stripes:label for="label.concept" /></td>
					         <td>
    							   <stripes:select style='width:200px;' id="concept" name="concept"></stripes:select>
 									   <script type="text/javascript">
      							   var cpts=dhtmlXComboFromSelect("concept");
      							   cpts.setComboText("Gastos de Viaje");
      							   setCombo(document.getElementById("account"), "${actionBean.account.uuid}");
    							   </script>      
					        </td>
					       </tr>
					       <tr>
					         <td class="formlabelrequired"><stripes:label for="label.paymentMean" /></td>
					         <td><stripes:select name="paymentMean"><option value=""></option><option value="card">Tarjeta</option><option value="cash">Efectivo</option></stripes:select>
					        </td>
					       </tr>
					       <tr>
					         <td class="formlabelrequired"><stripes:label for="label.description" /></td>
					         <td><stripes:text name="description" />
					        </td>
					       </tr>
					     </table>
					     <table>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.baseAmount" />:</td>
					         <td class="formlabeloptional"><stripes:text id="baseAmount" name="baseAmount" size="8" /></td>
					         <td class="formlabeloptional"><stripes:label for="label.currency" />:</td>
					         <td class="formlabeloptional">
					           <stripes:select name="currency">
					             <option value=""></option>
					             <option value="EUR" selected="selected">EUR</option>
					             <option value="USD">USD</option>
					             <option value="GBP">GBP</option>
					           </stripes:select>
					         </td>
					       </tr>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.vat" />:</td>
					         <td class="formlabeloptional">
					           <stripes:select id="vatpct" name="vatpct">
					           <option value="0">0%</option>
					           <option value="0.04">4%</option>
					           <option value="0.08">8%</option>
					           <option value="0.18" selected="selected">18%</option>
					           </stripes:select>
					         </td>
					         <td colspan="2"><div id="vat"></div></td>
					       <tr>
					         <td class="formlabeloptional"><stripes:label for="label.totalAmount" />:</td>
					         <td class="formlabeloptional"><stripes:text id="totalAmount" name="totalAmount" size="8" tabindex="-1" /></td>
					         <td colspan="2"></td>
					       </tr>
					     </table>
					     <stripes:submit name="save" class="submit" />
    			   </div>
    			   <script type="text/javascript" src="js/validations.js" defer="defer"></script>
    			   <script type="text/javascript" defer="defer">
    			    YUI().use('event', function (Y) {

    			    	var basea = Y.one("#baseAmount");
    			    	basea.on('blur', function (e) {
    			        	if (this.get("value").length>0 && !isFloatValue(this.get("value"))) {
    			        		alert ("${actionBean.resource('alert.invalidBaseAmount')}");
    			        	} else {    			        		
    			        		var cur = document.getElementById("currency");
    			        		var val = Number(this.get("value"));
    			        		var pct = document.getElementById("vatpct");
    			       			var vat = Math.round(val*Number(pct.options[pct.selectedIndex].value)*100)/100;

    			        		document.getElementById("vat").innerHTML = String(vat) + "&nbsp;" + cur.options[cur.selectedIndex].text;
    			        		document.getElementById("totalAmount").value = String(val+vat);
    			        	}
    			    	});
    			    	var total = Y.one("#totalAmount");
    			    	basea.on('focus', function (e) {
    			    			document.getElementById("baseAmount").focus();
    			    	});    			    	
    			    });
    			    
    			    var frm = document.forms["billnotedata"];
    			    setCombo(frm.day,"29");
    			    setCombo(frm.month,"06");
    			    setCombo(frm.year,"2012");
    			    
    			   </script>
			     </stripes:form>
      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
      		 
      		 <div class="outerbox"></div>
      		 
			     <form><input type="button" class="submit" value="Digitalizar otra nota" onclick="document.location='CaptureBillNote.action'" /></form>
			   </div> <!-- yui3-u-5-12 -->
			 </div> <!-- yui3-g -->
  </stripes:layout-component>
</stripes:layout-render>