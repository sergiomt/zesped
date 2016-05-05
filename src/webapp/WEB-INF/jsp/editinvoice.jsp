<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/lightbox.jsp" pageTitle="Entrar Datos de Factura">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox"><span style="padding:8px"><big><stripes:label for="caption.editInvoice" /></big></span></div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
      <%
      	com.zesped.action.EditInvoice oAbn = (com.zesped.action.EditInvoice) request.getAttribute("actionBean");
      	   java.util.ArrayList<String> oPags = oAbn.getPages();
      %>
			 <div class="yui3-g">
	 		   <div class="yui3-u-7-12">
				 <c:choose>
         <c:when test="${not empty actionBean.thumbnail}" >
	 		     <%	
	 		        if (oPags.size()>0) { %>
	 		     <a href="itemjpeg.jsp?i=<%=oPags.get(0)%>" target="_blank"><img src="itemjpeg.jsp?i=<%=oAbn.getThumbnail().id()%>" alt="" border="0" /></a> <% } %>
	 		     <table summary="Pages"><tr><tr><td colspan="<%=String.valueOf(oPags.size())%>"><img src="img/spacer.gif" width="1" height="6" alt="" /></td></tr><tr>
	 		     <%	 		     
	 		        if (oPags.size()>0) {
					      out.write("<td><a href=\"itemraw.jsp?i="+oPags.get(0)+"\" target=\"_blank\"><img src=\"img/x_pdf.gif\" alt=\"Expand\" width=\"16\" height=\"16\" border=\"0\"/></a></td><td>&nbsp;<a class=\"texthighlight\" href=\"itemjpeg.jsp?i="+oPags.get(0)+"\" target=\"_blank\">Ampliar</a></td>");
	 		    	    for (int p=0; p<oPags.size(); p++)
								  out.write("<td>&nbsp;<a class=\"texthighlight\" href=\"itemjpeg.jsp?i="+oPags.get(p)+"\" target=\"_blank\">"+String.valueOf(p+1)+"</a></td>");
	 		        }
	 		     %>
	 		     </tr></table>
	 		     <br/>
	 		     ___________________________________________________
         </c:when>
         <c:otherwise>
					 <br/><br/>
           <stripes:label for="label.previewNotAvailable" />
	 		     <br/><br/>
	 		     <table summary="Pages"><tr><tr><td colspan="<%=String.valueOf(oPags.size())%>"><img src="img/spacer.gif" width="1" height="6" alt="" /></td></tr><tr>
	 		     <%	 		     
	 		        if (oPags.size()>0) {
					      out.write("<td><a href=\"itemraw.jsp?i="+oPags.get(0)+"\" target=\"_blank\"><img src=\"img/x_pdf.gif\" alt=\"Expand\" width=\"16\" height=\"16\" border=\"0\"/></a></td><td>&nbsp;<a class=\"texthighlight\" href=\"itemjpeg.jsp?i="+oPags.get(0)+"\" target=\"_blank\">Ampliar</a></td>");
	 		    	    for (int p=0; p<oPags.size(); p++)
								  out.write("<td>&nbsp;<a class=\"texthighlight\" href=\"itemjpeg.jsp?i="+oPags.get(p)+"\" target=\"_blank\">"+String.valueOf(p+1)+"</a></td>");
	 		        }
	 		     %>
	 		     </tr></table>
         </c:otherwise>
         </c:choose>	 		   
			   </div>
			   <div class="yui3-u-5-12">

					 <stripes:form name="invoicedata" beanclass="com.zesped.action.EditInvoice">
					 <stripes:hidden name="id" />
					 <stripes:hidden name="serviceFlavor" />
	         <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b><div class="textbox">
	         <table width="95%">
	           <tr>
	             <td valign="middle"><h2><stripes:label for="caption.invoicedata" /></h2></td>
	             <td align="right">
	             <c:if test="${actionBean.serviceFlavor=='premium'}">
	               <a title="Premium"><img src="img/premiumstarred24.png" width="24" height="24" alt="Premium" border="0" /></a>
 	             </c:if>
	             </td>
	           </tr>
	         </table>
           <stripes:messages/>
           <stripes:errors/>
        	 <table class="formfields" summary="Biller and Payer">
					   <tr>
					     <td class="formlabeloptional"><stripes:label for="label.payer" /></td>
					     <td class="formlabeloptional">
							   <stripes:select id="recipient" name="recipientTaxPayer" class="taxpayerlist">
						       <stripes:option value="" />
						       <stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" />
						     </stripes:select>
					     </td>
					     <td></td>
					   </tr>
					   <tr>
					     <td class="formlabeloptional"><stripes:label for="label.biller" /></td>
					     <td class="formlabeloptional">
							   <stripes:select id="biller" name="billerTaxPayer" class="taxpayerlist">
						       <stripes:option value="" />
						       <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" />
						     </stripes:select>
					     </td>
					     <td><a href="EditClient.action" rel="lightbox"><stripes:label for="label.newm" /></a></td>
					   </tr>
					 </table>
			     <table summary="Concept and dates">
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.invoiceNumber" />:</td>
			         <td class="formlabeloptional"><stripes:text name="invoiceNumber" size="10" /></td>
			         <td class="formlabeloptional" colspan="2"></td>
			       </tr>
			     </table>
			     <table>
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.billDate" />:</td>
			         <td>
			         <stripes:select name="billDay"><option value=""><stripes:options-collection collection="${actionBean.days}" value="value" label="name" /></stripes:select>
			         <stripes:select name="billMonth"><option value=""><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
			         <stripes:select name="billYear"><option value=""><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
			         </td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.dueDate" />:</td>
			         <td>
			         <stripes:select name="dueDay"><option value=""><stripes:options-collection collection="${actionBean.days}" value="value" label="name" /></stripes:select>
			         <stripes:select name="dueMonth"><option value=""><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
			         <stripes:select name="dueYear"><option value=""><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
			         </td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.concept" />:</td>
			         <td><stripes:text size="30" name="concept" /></td>
			       </tr>
			     </table>
			     
					 <div id="tabs" class="yui3-skin-night">
	    		   <ul>
	             <li><a id="tab1" href="#iva1">IVA 1</a></li>
	        		 <li><a id="tab2" href="#iva2">IVA 2</a></li>
	             <li><a id="tab3" href="#iva3">IVA 3</a></li>
	             <li><a id="tab4" href="#iva4">IVA 4</a></li>
	             <li><a id="tab5" href="#irpf">IRPF</a></li>
	           </ul>
	           <div>
	             <c:forEach var="v" begin="0" end="3">
	               <div id="iva${v}">
			     			   <table sumary="Amounts ${v}">
			               <tr>
			                 <td class="formlabeloptional" width="128px"><stripes:label for="label.baseAmount" /> ${v+1}:</td>
			         				 <td class="formfields">
			                   <stripes:text id="baseAmount${v}" name="baseAmounts[${v}]" size="10" onblur="computeTotal()" />
			                 </td>
							         <td></td>
			               </tr>
			               <tr>
			                 <td class="formlabeloptional" width="128px"><stripes:label for="label.vat" /> ${v+1}:</td>
			                 <td class="formfields">
			                   <stripes:select id="vatPct${v}" name="vatPcts[${v}]" onchange="computeTotal()"><stripes:options-collection collection="${actionBean.vatPercents}" value="value" label="percentage" /></stripes:select>
			         				 </td>
			         				 <td class="formfields"><div id="vat${v}" style="text-align:left">${actionBean.getVatN(v)}</div></td>
			       				 </tr>
			       		   </table>
	               </div>
	             </c:forEach>
	             <div id="irpf">
			     			   <table sumary="Withholding">
			               <tr>
			                 <td class="formlabeloptional" width="128px"><stripes:label for="label.withholding" />:</td>
			                 <td class="formfields">
			                   <stripes:select id="withholdingPct" name="withholdingPct" onchange="computeTotal()">
			                     <stripes:option value="0"></stripes:option>
			                     <stripes:option value="0.21">21%</stripes:option>
			                   </stripes:select>
			         				 </td>
			       				 </tr>
			               <tr>
			                 <td class="formlabeloptional" width="128px"></td>
			                 <td class="formfields">
			                   <stripes:text id="withholding" name="withholding" size="10" tabindex="-1" />
			         				 </td>
			       				 </tr>
			       		   </table>
	             </div> <!-- irpf -->
	    			 </div>
	         </div>		     
			     
			     <c:choose>
           <c:when test="${actionBean.approveMode}">
			     <table sumary="Total">
			       <tr>
			         <td class="formlabeloptional" width="128px"><stripes:label for="label.totalAmount" />:</td>
			         <td class="formfields"><stripes:text id="totalAmount" name="totalAmount" size="10" tabindex="-1" /></td>
			         <td><stripes:select id="currency" name="currency"><option value="EUR" selected="selected">EUR</option><option value="USD">USD</option><option value="GBP">GBP</option><option value="ARS">ARS</option><option value="BRL">BRL</option><option value="CAD">CAD</option><option value="CHF">CHF</option><option value="CLP">CLP</option><option value="MXN">MXN</option><option value="RUB">RUB</option><option value="VEB">VEB</option></stripes:select></td>			         
			       </tr>
			       <tr>
			         <td class="formlabeloptional" width="128px"><stripes:label for="label.comments" />:</td>
			         <td colspan="2"><stripes:textarea cols="26" rows="3" id="comments" name="comments" /></td>
			       </tr>
			     </table>
			     <div style="display:none">
			       <stripes:checkbox id="processed" name="processed" />
			       <stripes:checkbox id="approved" name="approved" />
			       <stripes:checkbox id="mistakes" name="mistakes" />
			     </div>
           </c:when>
           <c:otherwise>
			     <table sumary="Total">
			       <tr>
			         <td class="formlabeloptional" width="128px"><stripes:label for="label.totalAmount" />:</td>
			         <td class="formfields"><stripes:text id="totalAmount" name="totalAmount" size="10" tabindex="-1" /></td>
			         <td><stripes:select id="currency" name="currency"><option value="EUR" selected="selected">EUR</option><option value="USD">USD</option><option value="GBP">GBP</option><option value="ARS">ARS</option><option value="BRL">BRL</option><option value="CAD">CAD</option><option value="CHF">CHF</option><option value="CLP">CLP</option><option value="MXN">MXN</option><option value="RUB">RUB</option><option value="VEB">VEB</option></stripes:select></td>			         
			       </tr>
			       <tr>
			         <td class="formlabeloptional" width="128px"><stripes:checkbox id="processed" name="processed" /></td>
			         <td class="formfields" colspan="2"><stripes:label for="label.markAsProcessed" /></td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional" width="128px"><stripes:checkbox id="approved" name="approved" /></td>
			         <td class="formfields" colspan="2"><stripes:label for="label.markAsApproved" /></td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional" width="128px"><stripes:checkbox id="mistakes" name="mistakes" /></td>
			         <td class="formfields" colspan="2"><stripes:label for="label.hasMistakes" /></td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional" width="128px"><stripes:label for="label.comments" />:</td>
			         <td colspan="2"><stripes:textarea cols="26" rows="3" id="comments" name="comments" /></td>
			       </tr>
			     </table>
           </c:otherwise>
           </c:choose>

	     		 </div><b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>

    		   <div class="outerbox"></div>				         		 
					 
			     <c:choose>
           <c:when test="${actionBean.approveMode}">
	           <stripes:submit id="approve" name="approve" class="submit" />&nbsp;&nbsp;<stripes:submit id="reject" name="reject" class="submit" />&nbsp;&nbsp;<stripes:button id="cancel" name="cancel" class="cancel" />
	         </c:when>
	         <c:otherwise>
	           <stripes:submit id="save" name="save" class="submit" />&nbsp;&nbsp;<stripes:button id="cancel" name="cancel" class="cancel" />
	         </c:otherwise>
	         </c:choose>
					 <br/><br/><br/><br/>
					 <script type="text/javascript" src="js/validations.js"></script>
					 <script type="text/javascript" src="js/panel.js" defer="defer"></script>
    			 <script language="JavaScript" type="text/javascript" defer="defer">
    			 
    				  var frm = document.forms["invoicedata"];
    				  <% if (!oAbn.getMistakes() && !oAbn.getApproveMode()) {
    					   out.write("frm.processed.checked=true;\n");
    					   if ("basic".equals(oAbn.getServiceFlavor()))
    					     out.write("frm.approved.checked=true;\n");
    				  } %>
    				  
    			    YUI({ filter: "raw" }).use('event','io-form','tabview', 'io', function (Y) {

    			    	var total = Y.one("#totalAmount");
    			    	total.on('focus', function (e) {
    			    			document.getElementById("comments").focus();
    			    	});

    			    	var withh = Y.one("#withholding");
    			    	withh.on('focus', function (e) {
    			    			document.getElementById("comments").focus();
    			    	});

    				    <c:choose>
    			        <c:when test="${actionBean.approveMode}">
		    			    	Y.one("#approve").on('click', function (e) {
									    var frm = document.forms["invoicedata"].elements;
									    frm["processed"].checked = true;
									    frm["approved"].checked = true;
									    frm["mistakes"].checked = false;							  
						    		  for (var b=0; b<4; b++)
					    			    if (frm["baseAmount"+String(b)].value.length==0 || !isFloatValue(frm["baseAmount"+String(b)].value))
					    				    frm["baseAmount"+String(b)].value = "0";
						    		  computeTotal();
					    			} );
		
		  			    	  Y.one("#reject").on('click', function (e) {
									    var frm = document.forms["invoicedata"].elements;
									    frm["processed"].checked = false;
									    frm["approved"].checked = false;
									    frm["mistakes"].checked = true;							  
						    		  for (var b=0; b<4; b++)
					    			    if (frm["baseAmount"+String(b)].value.length==0 || !isFloatValue(frm["baseAmount"+String(b)].value))
					    				    frm["baseAmount"+String(b)].value = "0";
						    		  computeTotal();
					    		  } );
    			         </c:when>
    			         <c:otherwise>
     			    	     Y.one("#save").on('click', function (e) {
							         var frm = document.forms["invoicedata"].elements;
			    		         for (var b=0; b<4; b++)
			    			         if (frm["baseAmount"+String(b)].value.length==0 || !isFloatValue(frm["baseAmount"+String(b)].value))
			    				         frm["baseAmount"+String(b)].value = "0";
					    		     computeTotal();
			    	         } );
    			         </c:otherwise>
    			      </c:choose>
    			    	
    			    	Y.one("#cancel").on('click', function (e) { window.history.back(); } );

    			    	Y.one("#processed").on('click', function (e) {
					         var frm = document.forms["invoicedata"];
					         if (frm.processed.checked)
					           frm.mistakes.checked = false;
					         else
						         frm.approved.checked = <% out.write("basic".equals(oAbn.getServiceFlavor()) ? "true" : "false"); %>;
    			    	} );

    			    	Y.one("#approved").on('click', function (e) {
					         var frm = document.forms["invoicedata"];
					         if (frm.approved.checked) {
						           frm.processed.checked = true;
							         frm.mistakes.checked = false;					        	 
					         }
   			    	  } );

    			    	Y.one("#mistakes").on('click', function (e) {
					         var frm = document.forms["invoicedata"];
					         if (frm.mistakes.checked) {
					        	 frm.approved.checked = false;
					        	 frm.processed.checked = false;	 
					         }
  			    	  } );
    			    	
    			      tabview = new Y.TabView({
    			        srcNode: '#tabs'
    			      });
    			      tabview.render();
    			      
    			      <c:if test="${not empty actionBean.formerId}" >
    			        Y.one('#alertbox .message').setHTML('Factura ${actionBean.formerId} grabada con &eacute;xito. Editando la siguiente factura pendiente.');
	    			      Y.one('#alertbox .message').set('className', 'message dialog-info');
	    			      alertbox.show();
	    			    </c:if>    			    
    			    });

      			setComboDecimal (document.forms["invoicedata"].withholdingPct, "${actionBean.withholdingPct}");

		    	  function computeTotal() {
							var frm = document.forms["invoicedata"].elements;
		    		  var vals = new Array (frm["baseAmount0"].value,frm["baseAmount1"].value,frm["baseAmount2"].value,frm["baseAmount3"].value);			    
		    		  var wht = Number(getCombo(frm["withholdingPct"]));
		    		  if ((vals[0].length>0 && !isFloatValue(vals[0])) || (vals[1].length>0 && !isFloatValue(vals[1])) || (vals[2].length>0 && !isFloatValue(vals[2])) || (vals[3].length>0 && !isFloatValue(vals[3]))) {
		        		  document.getElementById("totalAmount").value = "";
		        	  } else {
		        		  var cur = document.getElementById("currency");
		        		  var bas = 0;
		        		  var tot = 0;
		        		  for (var v=0; v<4; v++) {
		        			  if (vals[v].length>0) {
				        		  var val = parseMoney(vals[v]);
				        		  var pct = document.getElementById("vatPct"+String(v));
				       			  var vat = Math.round(val*parseMoney(pct.options[pct.selectedIndex].value)*100)/100;
		        			    bas += val;
				        		  tot += val+vat;
				        		  document.getElementById("vat"+String(v)).innerHTML = formatMoney(vat);
		        			  } else {
		        				  document.getElementById("vat"+String(v)).innerHTML = "";
		        			  }
		        		  } // next
		        		  frm["withholding"].value = formatMoney(bas*wht);
		        		  document.getElementById("totalAmount").value = formatMoney(tot-(bas*wht));
		        	  } // fi
		    	    } // computeTotal
    			    
    			 </script>
    			 </stripes:form>   			 
			   </div> <!-- yui3-u-5-12 -->
			 </div>
  </stripes:layout-component>
</stripes:layout-render>