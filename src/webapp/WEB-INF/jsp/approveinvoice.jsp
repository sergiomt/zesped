<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/lightbox.jsp" pageTitle="Aprobar Factura">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox"><span style="padding:8px"><big><stripes:label for="caption.approveInvoice" /></big></span></div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
      <%
      	com.zesped.action.EditInvoiceBackEnd oAbn = (com.zesped.action.EditInvoiceBackEnd) request.getAttribute("actionBean");
      %>
			
			 <div class="yui3-g">
	 		   <div class="yui3-u-7-12">
				 <c:choose>
         <c:when test="${not empty actionBean.thumbnail}" >
	 		     <img src="item.jsp?i=<%=oAbn.getThumbnail().id()%>" alt="" />
	 		     <table><tr>
	 		     <%	 		     
	 		    	  java.util.ArrayList<String> oPags = oAbn.getPages();
	 		        if (oAbn.getPages().size()>0)
					      out.write("<td><a href=\"item.jsp?i="+oPags.get(0)+"\" target=\"_blank\"><img src=\"img/expand12.png\" alt=\"Expand\" width=\"12\" height=\"12\" border=\"0\"/></a></td><td>&nbsp;<a class=\"texthighlight\" href=\"item.jsp?i="+oPags.get(0)+"\" target=\"_blank\">Ampliar</a></td>");	 		        
	 		     	  if (oAbn.getPages().size()>1)
	 		    	    for (int p=1; p<oAbn.getPages().size(); p++)
								  out.write("<td>&nbsp;<a class=\"texthighlight\" href=\"item.jsp?i="+oAbn.getPages().get(p)+"\" target=\"_blank\">"+String.valueOf(p)+"</a></td>");
	 		     %>
	 		     </tr></table>
         </c:when>
         <c:otherwise>
					 <br/><br/>
           <stripes:label for="label.previewNotAvailable" />
         </c:otherwise>
         </c:choose>	 		   
			   </div>
			   <div class="yui3-u-5-12">

					 <stripes:form name="invoicedata" beanclass="com.zesped.action.EditInvoice">
					 <stripes:hidden name="id" />
	         <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b><div class="textbox">
	         <h2><stripes:label for="caption.invoicedata" /></h2>
           <stripes:messages/>
           <stripes:errors/>
        	 <table class="formfields" summary="Biller and Payer">
					   <tr>
					     <td class="formlabeloptional"><stripes:label for="label.payer" /></td>
					     <td class="formlabeloptional">
							   <stripes:select id="recipient" name="recipientTaxPayer" class="taxpayerlist">
						       <stripes:option value="" />
						       <optgroup label="Empresas del Grupo"><stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" /></optgroup>
						       <optgroup label="Otras Empresas"> <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" /></optgroup>
						     </stripes:select>
					     </td>
					     <td></td>
					   </tr>
					   <tr>
					     <td class="formlabeloptional"><stripes:label for="label.biller" /></td>
					     <td class="formlabeloptional">
							   <stripes:select id="biller" name="billerTaxPayer" class="taxpayerlist">
						       <stripes:option value="" />
						       <optgroup label="Empresas del Grupo"><stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" /></optgroup>
						       <optgroup label="Otras Empresas"> <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" /></optgroup>
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
			     <table sumary="Amounts">
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.baseAmount" />:</td>
			         <td class="formfields">
			           <stripes:text id="baseAmount" name="baseAmount" size="10" />
			         </td>
							 <td>
								 <stripes:select id="currency" name="currency"><option value="EUR" selected="selected">EUR</option><option value="USD">USD</option><option value="GBP">GBP</option><option value="ARS">ARS</option><option value="BRL">BRL</option><option value="CAD">CAD</option><option value="CHF">CHF</option><option value="CLP">CLP</option><option value="MXN">MXN</option><option value="RUB">RUB</option><option value="VEB">VEB</option></stripes:select>
			         </td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.vat" />:</td>
			         <td class="formfields">
			           <stripes:select id="vatPct" name="vatPct"><stripes:options-collection collection="${actionBean.vatPercents}" value="value" label="percentage" /></stripes:select>
			         </td>
			         <td class="formfields"><div id="vat" style="text-align:left"></div></td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.totalAmount" />:</td>
			         <td class="formfields" colspan="2"><stripes:text id="totalAmount" name="totalAmount" size="10" /></td>
			       </tr>
			       <tr>
			         <td class="formlabeloptional"><stripes:label for="label.comments" />:</td>
			         <td colspan="2"><stripes:textarea cols="26" rows="3" name="comments" /></td>
			       </tr>
			     </table>

	     		 </div><b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>

    		   <div class="outerbox"></div>				         		 

	         <stripes:submit name="approve" class="submit" />
	         &nbsp;&nbsp;
	         <stripes:submit name="reject" class="submit" />
	         &nbsp;&nbsp;
	         <stripes:button id="cancel" name="cancel" class="cancel" />
					 </stripes:form>
					 <script type="text/javascript" src="js/validations.js"></script>
    			 <script language="JavaScript" type="text/javascript">
    			 
    			   <c:if test="${not empty actionBean.vat}">
       		     document.getElementById("vat").innerHTML = formatMoney(<c:out value="${actionBean.vat}"/>) + "&nbsp;" + document.getElementById("currency").options[document.getElementById("currency").selectedIndex].text;
    			   </c:if>
    			 
    			    YUI({ filter: "raw" }).use('event','io-form', function (Y) {

    		    	  function computeTotal(e) {
			    		    var val = document.forms["invoicedata"].baseAmount.value;			    
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

    			    	var basea = Y.one("#baseAmount");
    			    	basea.on('blur', function (e) { computeTotal(e); } );

    			    	var vatpct = Y.one("#vatPct");
    			    	vatpct.on('change', function (e) { computeTotal(e); } );
    			    	
    			    	var total = Y.one("#totalAmount");
    			    	total.on('focus', function (e) {
    			    			document.getElementById("baseAmount").focus();
    			    	});

    			    	Y.one("#cancel").on('click', function (e) { window.history.back(); } );
    			    	
    			    });
    			 </script>
			   </div> <!-- yui3-u-5-12 -->
			 </div>
  </stripes:layout-component>
</stripes:layout-render>