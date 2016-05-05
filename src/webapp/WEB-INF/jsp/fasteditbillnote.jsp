<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/lightbox.jsp" pageTitle="Notas de gasto pendientes de liquidación">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>
			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox"><div class="tablebox">
			<stripes:form id="ticketslist" beanclass="com.zesped.action.FastEditBillNote">
			<stripes:hidden name="id" />
			<c:forEach var="i" begin="0" end="${actionBean.ticketCount==0 ? 0 : actionBean.ticketCount-1}"><stripes:hidden name="ticketIds[${i}]" /></c:forEach>
			<h3>Tickets para la nota de gasto: ${actionBean.concept}</h3>
			<h3>de ${actionBean.employeeName}</h3>
		  <table class="yui3-skin-night yui3-datatable">
		    <tr>
		      <td class="yui3-datatable-header">N&ordm; Ticket</td>
		      <td class="yui3-datatable-header">Fecha</td>
		      <td class="yui3-datatable-header">Cuenta</td>
		      <td class="yui3-datatable-header">Emisor</td>
		      <td class="yui3-datatable-header">Base</td>
		      <td class="yui3-datatable-header">%IVA</td>
		      <td class="yui3-datatable-header">IVA</td>
		      <td class="yui3-datatable-header">Total</td>
		      <td class="yui3-datatable-header"></td>
		    </tr>
				<c:forEach var="i" begin="0" end="${actionBean.ticketCount==0 ? 0 : actionBean.ticketCount-1}">
				  <c:choose>
            <c:when test="${actionBean.isOpen}" >
						  <tr>
						    <td class="yui3-datatable-cell"><stripes:text name="ticketNumbers[${i}]" class="lightamount" /></td>
						    <td class="yui3-datatable-cell"><stripes:text name="ticketDates[${i}]" class="lightdate" /></td>
						    <td class="yui3-datatable-cell">
						      <stripes:select id="accounts${i}" name="accounts[${i}]"><option value=""></option><stripes:options-collection collection="${actionBean.accountingAccounts}" value="id" label="description" /></stripes:select>
						    </td>
						    <td class="yui3-datatable-cell">
						      <stripes:select id="billers${i}" name="billers[${i}]" class="lightcombo"><option value=""></option><stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" /></stripes:select>
						    </td>
						    <td class="yui3-datatable-cell" nowrap="nowrap"><stripes:text id="baseAmounts${i}" name="baseAmounts[${i}]" class="lightamount" />
						      &nbsp;${actionBean.currencySymbols[i]}<stripes:hidden id="currencies${i}" name="currencies[${i}]"  />
						    </td>
						    <td class="yui3-datatable-cell">
						      <stripes:select id="vatPcts${i}" name="vatPcts[${i}]" class="lightamount"><stripes:options-collection collection="${actionBean.vatPercents}" value="value" label="percentage" /></stripes:select>
						    </td>
						    <td class="yui3-datatable-cell"><stripes:text id="vats${i}" name="vats[${i}]" class="lightshortamount" tabindex="-1" /></td>
						    <td class="yui3-datatable-cell"><stripes:text id="totalAmounts${i}" name="totalAmounts[${i}]" class="lightamount" /></td>
						    <td nowrap="nowrap">
						      <a href="itemlightbox.jsp?i=${actionBean.thumbnails[i]}" rel="lightbox" title="Visualizar Ticket"><img src="img/eye16.gif" width="23" height="17" alt="Visualizar Ticket" border="0" /></a>
						      <a href="EditBillNote.action?id=${actionBean.ticketIds[i]}" target="${actionBean.ticketIds[i]}" title="Editar Ticket"><img src="img/pencil16.gif" width="23" height="17" alt="Editar Ticket" border="0" /></a>
						    </td>
						  </tr>
						</c:when>
			      <c:otherwise>
						  <tr>
						    <td class="yui3-datatable-cell"><stripes:text name="ticketNumbers[${i}]" class="lightamount" tabindex="-1" disabled="disabled" /></td>
						    <td class="yui3-datatable-cell"><stripes:text name="ticketDates[${i}]" class="lightdate" tabindex="-1" disabled="disabled" /></td>
						    <td class="yui3-datatable-cell">
						      <stripes:select id="accounts${i}" name="accounts[${i}]" tabindex="-1" disabled="disabled"><option value=""></option><stripes:options-collection collection="${actionBean.accountingAccounts}" value="id" label="description" /></stripes:select>
						    </td>
						    <td class="yui3-datatable-cell">
						      <stripes:select id="billers${i}" name="billers[${i}]" class="lightcombo" tabindex="-1" disabled="disabled"><option value=""></option><stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" /></stripes:select>
						    </td>
						    <td class="yui3-datatable-cell" nowrap="nowrap"><stripes:text id="baseAmounts${i}" name="baseAmounts[${i}]" class="lightamount" tabindex="-1" disabled="disabled" />
						      &nbsp;${actionBean.currencySymbols[i]}<stripes:hidden id="currencies${i}" name="currencies[${i}]" />
						    </td>
						    <td class="yui3-datatable-cell">
						      <stripes:select id="vatPcts${i}" name="vatPcts[${i}]" class="lightamount" tabindex="-1" disabled="disabled"><stripes:options-collection collection="${actionBean.vatPercents}" value="value" label="percentage" /></stripes:select>
						    </td>
						    <td class="yui3-datatable-cell"><stripes:text id="vats${i}" name="vats[${i}]" class="lightshortamount" tabindex="-1" disabled="disabled" /></td>
						    <td class="yui3-datatable-cell"><stripes:text id="totalAmounts${i}" name="totalAmounts[${i}]" class="lightamount" tabindex="-1" disabled="disabled" /></td>
						    <td nowrap="nowrap">
						      <a href="itemlightbox.jsp?i=${actionBean.thumbnails[i]}" rel="lightbox" title="Visualizar Ticket"><img src="img/eye16.gif" width="23" height="17" alt="Visualizar Ticket" border="0" /></a>
						    </td>
						  </tr>
			      </c:otherwise>			      
				  </c:choose>			      
				</c:forEach>	    
				  <tr>
				    <td class="yui3-datatable-cell" colspan="3"></td>
				    <td class="yui3-datatable-cell" align="right">Total:&nbsp;</td>
				    <td class="yui3-datatable-cell" nowrap="nowrap"><div id="base"></div></td>
				    <td class="yui3-datatable-cell"></td>
				    <td class="yui3-datatable-cell"><div id="vat"></div></td>
				    <td class="yui3-datatable-cell" nowrap="nowrap"><div id="total"></div></td>
				    <td><c:if test="${actionBean.isOpen}" >				    
				      <a href="#" id="update" title="Actualizar Totales"><img id="recompute" src="img/update16.gif" width="19" height="19" alt="Actualizar" border="0" /></a><img id="computing" src="img/loading.gif" width="32" height="16" hspace="15" alt="filtering" style="display:none" />
				    </c:if></td>
				  </tr>
				  <c:if test="${actionBean.totalAmountAlt!=null}" >
				  <tr>
				    <td class="yui3-datatable-cell" colspan="4"></td>
				    <td class="yui3-datatable-cell" nowrap="nowrap"><div id="baseAlt"></div></td>
				    <td class="yui3-datatable-cell"></td>
				    <td class="yui3-datatable-cell"><div id="vatAlt"></div></td>
				    <td class="yui3-datatable-cell" nowrap="nowrap"><div id="totalAlt"></div></td>
				  </tr>
				  </c:if>				  
		  </table>
		  <stripes:messages/>
      <stripes:errors/>
			<c:choose>
           <c:when test="${actionBean.isOpen}" >
			      <stripes:submit id="save" name="save" class="submit" onclick="return updateTotals(false)" />
		        &nbsp;&nbsp;
		        <c:if test="${sessionScope.can_settle=='1'}">
			      <stripes:submit id="settle" name="settle" class="submit" onclick="return updateTotals(false)" />
		        </c:if>
		      </c:when>
		      <c:otherwise>
			      <stripes:submit id="reopen" name="reopen" class="submit" />
		      </c:otherwise>			      
			</c:choose>
		  &nbsp;&nbsp;
		  <input id="cancel" type="button" class="cancel" value="Cancelar" />
      </stripes:form>
      <script type="text/javascript" src="js/validations.js"></script>
      <script type="text/javascript" language="JavaScript">
      <% com.zesped.action.FastEditBillNote oAbn = (com.zesped.action.FastEditBillNote) request.getAttribute("actionBean"); %>

        var updateTotals = function(e) {
	  		  if (e) e.preventDefault();
	  		  document.getElementById("save").className="disabledbutton";
        	document.getElementById("settle").className="disabledbutton";
        	document.getElementById("save").disabled=true;
        	document.getElementById("settle").disabled=true;
		  		document.getElementById("recompute").style.display="none";
		  		document.getElementById("computing").style.display="block";
					var frm = document.forms["ticketslist"];
					var ticketcount = <%=String.valueOf(oAbn.getTicketCount())%>;
					var base = 0;
					var total = 0;
					var vat = 0;
					var altcurrency = "<%=oAbn.getCurrencyAlt()%>";
			
					for (var n=0; n<ticketcount; n++) {
						var i = String(n);
				    var b = frm.elements["baseAmounts"+i].value;
				    if (isFloatValue(b)) {
					      var p = parseMoney(getCombo(frm.elements["vatPcts"+i]));
					      var c = frm.elements["currencies"+i].value;				    
								var a = parseMoney(b);
					      frm.elements["vats"+i].value = formatMoney(String(a*p));
					      frm.elements["totalAmounts"+i].value = formatMoney(String(a*(1+p)));
								if (c!="EUR")
									a = Number(httpRequestText("currencyconverter.jsp?amount="+String(a)+"&from="+c+"&to=EUR"));	
							  base += a;
				        vat += a*p;
				        total += a*(1+p);
				    } // fi
					} // next
					document.getElementById("base").innerHTML = formatMoney(String(base))+"&nbsp;&euro;";		
					document.getElementById("vat").innerHTML = formatMoney(String(vat))+"&nbsp;&euro;";	
					document.getElementById("total").innerHTML = formatMoney(String(total))+"&nbsp;&euro;";	
					<% if (oAbn.getTotalAmountAlt()!=null) { %>
					  document.getElementById("baseAlt").innerHTML = formatMoney(httpRequestText("currencyconverter.jsp?amount="+String(base)+"&from=EUR&to="+altcurrency))+"&nbsp;<%=oAbn.getCurrencySymbolAlt()%>";		
						document.getElementById("vatAlt").innerHTML = formatMoney(httpRequestText("currencyconverter.jsp?amount="+String(vat)+"&from=EUR&to="+altcurrency))+"&nbsp;<%=oAbn.getCurrencySymbolAlt()%>";		
						document.getElementById("totalAlt").innerHTML = formatMoney(httpRequestText("currencyconverter.jsp?amount="+String(total)+"&from=EUR&to="+altcurrency))+"&nbsp;<%=oAbn.getCurrencySymbolAlt()%>";		
					<% } %>
				  document.getElementById("computing").style.display="none";
			  	document.getElementById("recompute").style.display="block";
	        document.getElementById("save").disabled=false;
	        document.getElementById("settle").disabled=false;
	        document.getElementById("save").className="submit";
	        document.getElementById("settle").className="submit";
	        return true;
			  };

        YUI().use("event", function (Y) {
    	    Y.on("domready", function (e) {
    	    	var frm = document.forms["ticketslist"];
    	    	<c:forEach var="i" begin="0" end="${actionBean.ticketCount==0 ? 0 : actionBean.ticketCount-1}">
    	    	  setCombo(frm.elements["accounts${i}"],"${actionBean.accounts[i]}");
    	    	  setCombo(frm.elements["billers${i}"],"${actionBean.billersIds[i]}");
    	    	  setComboDecimal(frm.elements["vatPcts${i}"],"${actionBean.vatPcts[i]}");
    	    	  Y.one('#vats${i}').on('focus', function (e) { frm.elements["totalAmounts${i}"].focus(); });
							document.getElementById("base").innerHTML = formatMoney("${actionBean.baseAmount}")+"&nbsp;&euro;";		
							document.getElementById("vat").innerHTML = formatMoney("${actionBean.vat}")+"&nbsp;&euro;";	
							document.getElementById("total").innerHTML = formatMoney("${actionBean.totalAmount}")+"&nbsp;&euro;";	
					    <% if (oAbn.getTotalAmountAlt()!=null) { %>	 
					      document.getElementById("baseAlt").innerHTML = formatMoney("${actionBean.baseAmountAlt}")+"&nbsp;${actionBean.currencySymbolAlt}";		
						    document.getElementById("vatAlt").innerHTML = formatMoney("${actionBean.vatAlt}")+"&nbsp;${actionBean.currencySymbolAlt}";
						    document.getElementById("totalAlt").innerHTML = formatMoney("${actionBean.totalAmountAlt}")+"&nbsp;${actionBean.currencySymbolAlt}";
					    <% } %>
  	  			  Y.one('#baseAmounts${i}').on('blur', updateTotals);
	  					Y.one('#totalAmounts${i}').on('blur', updateTotals);
	  					Y.one('#vatPcts${i}').on('change', updateTotals);
	  					<c:if test="${actionBean.isOpen}" >
			    		Y.one('#update').on('click', updateTotals);
	  					</c:if>
					  </c:forEach>
    	    });
    	    Y.one('#cancel').on('click', function (e) { window.history.back(); } );
        });
        </script>
        <!-- can_settle = ${sessionScope.can_settle} -->
		  </div></div> <!-- darkbox -->
		<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div><!-- xsnazzy0 -->
  </stripes:layout-component>
</stripes:layout-render>