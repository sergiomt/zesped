<%@ page import="java.util.Date,com.knowgate.misc.Calendar" language="java" session="true" pageEncoding="utf-8" contentType="text/html" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/listing.jsp" pageTitle="Consulta de Facturas">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox">
			 <div class="yui3-g">
			   <div class="yui3-u" id="searchfilter">
			     <stripes:form name="filter" beanclass="com.zesped.action.ListApprovedInvoices">
				   <div class="columnitem"><h2><stripes:label for="caption.filter" /></h2></div>
			       <div class="columnitem">
			       <stripes:label for="label.period" /><br/>
			       <jsp:include page="/inc/periods.jsp"/>
			       </div>
			       <div id="datecombos" class="columnitem0">
			       <stripes:label for="label.from" /><br/>
			       <stripes:select class="darkinput" name="monthStart"><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
			       <stripes:select class="darkinput" name="yearStart"><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
			       <br/>
			       <stripes:label for="label.to" /><br/>
			       <stripes:select class="darkinput" name="monthEnd"><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
				     <stripes:select class="darkinput" name="yearEnd"><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
			       <br/>			       
			       </div>
			       <div class="columnitem">
			       <stripes:label for="label.biller" /><br/>
			         <stripes:select class="darkcombo" name="biller">
						   <stripes:option value="" />
							   <optgroup label="Empresas del Grupo"><stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" /></optgroup>
							   <optgroup label="Otras Empresas"> <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" /></optgroup>
							 </stripes:select>
			       </div>
			       <div class="columnitem">
			       <stripes:label for="label.payer" /><br/>
			         <stripes:select class="darkcombo" name="recipient">
						   <stripes:option value="" />
							   <optgroup label="Empresas del Grupo"><stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" /></optgroup>
							   <optgroup label="Otras Empresas"> <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" /></optgroup>
							 </stripes:select>
			       </div>
			       <div class="columnitem">
			       <stripes:label for="label.amount" /><br/>
			       <stripes:label for="label.de" />&nbsp;<input type="text" name="amountFrom" class="darkamount">&nbsp;<stripes:label for="label.a" />&nbsp;<input type="text" name="amountTo" class="darkamount">
			       </div>
			       <div class="columnitem">		       			       
			       <stripes:label for="label.search" /><br/>
			       <input type="text" name="search" class="darkcombo"><br/>
			       <br/>
			       <center>
			         <input id="filterbutton" name="filterbutton" type="button" class="login" value="Filtrar" />
			         <img id="filtering" src="img/loading.gif" width="32" height="16" hspace="15" alt="filtering" style="display:none" />
			       </center>
			       </div>
			     </stripes:form>
			   </div> <!-- yui3-u -->
			   <div class="yui3-u">
			     <div id="changeview" style="display:none">
			       <table align="right">
							 <tr>
							   <td valign="middle" class="textdisplay"><stripes:label for="label.downloadas" />:</span>
							   <td valign="middle"><img src="img/spacer.gif" width="16" height="1" border="0" alt="" /></td>
							   <td valign="middle"><img src="img/X16.gif" width="16" height="12" border="0" alt="Excel" /></td>						
							   <td valign="middle">&nbsp;<a id="exceldump" class="anchorhighlight" href="#" target="_blank"><stripes:label for="label.exceldump" /></a></td>
							   <td valign="middle"><img src="img/spacer.gif" width="16" height="1" border="0" alt="" /></td>
							   <td valign="middle"><img src="img/Z16.gif" width="16" height="16" border="0" alt="Zip" /></td>						
							   <td valign="middle">&nbsp;<a id="zipdump" class="anchorhighlight" href="#" target="_blank"><stripes:label for="label.zipdump" /></a></td>
							   <td valign="middle"><img src="img/spacer.gif" width="16" height="1" border="0" alt="" /></td>
							   <!--
							   <td valign="middle"><img src="img/E16.gif" width="16" height="13" border="0" alt="Factura-e" /></td>						
							   <td valign="middle">&nbsp;<a id="factedump" class="anchorhighlight" href="#" target="_blank"><stripes:label for="label.factedump" /></a></td>
						     -->
						   </tr>
			       </table>
			     </div>
			     <div id="filtersomething" style="margin-left:8px">Aplique un filtro especificando los criterios para recuperar las facturas deseadas</div>
			     <div id="datatable" class="yui3-skin-night yui3-datatable"></div>
			     <div style="width:200px;clear:both;margin-left:8px"><div id="showprev" style="float:left;display:none"><a href="#" id="previous"><stripes:label for="label.previous" /></a></div><div id="shownext" style="float:right;display:none"><a href="#" id="next"><stripes:label for="label.next" /></a></div></div>
			   </div> <!-- yui3-u -->
		 </div> <!-- yui3-g -->
		</div> <!-- darkbox -->
		<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div><!-- xsnazzy0 -->
  </stripes:layout-component>
</stripes:layout-render>