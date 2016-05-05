<%@ page import="java.util.Date,com.knowgate.misc.Calendar,com.zesped.action.ListNewInvoices" language="java" session="true" pageEncoding="utf-8" contentType="text/html" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%
  ListNewInvoices oAbn = (ListNewInvoices) request.getAttribute("actionBean");
%>
<stripes:layout-render name="/inc/listing.jsp" pageTitle="Facturas pendientes de proceso">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>
			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox">
			 <div class="yui3-g">
			   <div class="yui3-u" id="searchfilter">
			     <stripes:form name="filter" beanclass="com.zesped.action.ListNewInvoices">
				   <div class="columnitem"><h2><stripes:label for="caption.filter" /></h2></div>
			       <div id="datecombos" class="columnitem">
			       <stripes:label for="label.capturedate" /><br/>
			       <stripes:label for="label.from" /><br/>
			       <stripes:select class="darkinput" name="dayStart"><option value=""></option><% for (int d=1; d<=31; d++) out.write("<option value=\""+String.valueOf(d)+"\">"+(d<10 ? "0" : "")+String.valueOf(d)+"</option>"); %></stripes:select>
			       <stripes:select class="darkinput" name="monthStart"><option value=""></option><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
			       <stripes:select class="darkinput" name="yearStart"><option value=""></option><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
			       <br/>
			       <stripes:label for="label.to" /><br/>
			       <stripes:select class="darkinput" name="dayEnd"><option value=""></option><% for (int d=1; d<=31; d++) out.write("<option value=\""+String.valueOf(d)+"\">"+(d<10 ? "0" : "")+String.valueOf(d)+"</option>"); %></stripes:select>
			       <stripes:select class="darkinput" name="monthEnd"><option value=""></option><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
				     <stripes:select class="darkinput" name="yearEnd"><option value=""></option><stripes:options-collection collection="${actionBean.years}" value="value" label="name" /></stripes:select>
			       <br/>			       
			       </div>
			       <div class="columnitem">
			       <stripes:label for="label.status" /><br/>    
			       <select class="darkcombo" name="status">
			         <option value="pending" selected="selected"><stripes:label for="option.pending" /></option>
			         <option value="hasmistakes"><stripes:label for="option.hasmistakes" /></option>
			        </select>
			       </div>
			       <div class="columnitem">
			       <stripes:label for="label.biller" /><br/>
			         <stripes:select class="darkcombo" name="biller">
						   <stripes:option value="" />
							   <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" />
							 </stripes:select>
			       </div>
			       <div class="columnitem" style="display:none">
			       <stripes:label for="label.payer" /><br/>
			         <stripes:select class="darkcombo" name="recipient">
						     <option value="<%=oAbn.getSessionAttribute("taxpayer_docid")%>" selected="selected" />
						     <!--
						     <stripes:option value="" />
							   <optgroup label="Empresas del Grupo"><stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" /></optgroup>
							   <optgroup label="Otras Empresas"> <stripes:options-collection collection="${actionBean.clients}" value="id" label="businessName" /></optgroup>
							   -->
							 </stripes:select>
			       </div>
			       <div class="columnitem">		       			       
			       <center>
			         <input id="filterbutton" name="filterbutton" type="button" class="login" value="Filtrar" />
			         <img id="filtering" src="img/loading.gif" width="32" height="16" hspace="15" alt="filtering" style="display:none" />
			       </center>
			       </div>
			     </stripes:form>
			   </div> <!-- yui3-u -->
			   <div class="yui3-u">
			     <div id="changeview">
			       <table align="right">
							 <tr>
							   <td valign="middle"><img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" /></td>							
							   <td valign="middle">&nbsp;<a id="delete" class="anchorhighlight" href="#"><stripes:label for="label.deleteInvoices" /></a></td>
						   </tr>
			       </table>
			     </div>
			     <div id="datatable" class="yui3-skin-night yui3-datatable"></div>
			     <div style="width:200px;clear:both;margin-left:8px"><div id="showprev" style="float:left;display:none"><a href="#" id="previous"><stripes:label for="label.previous" /></a></div><div id="shownext" style="float:right;display:none"><a href="#" id="next"><stripes:label for="label.next" /></a></div></div>
			   </div> <!-- yui3-u -->
		 </div> <!-- yui3-g -->
		</div> <!-- darkbox -->
		<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div><!-- xsnazzy0 -->
    <br/><br/><br/>
  </stripes:layout-component>
</stripes:layout-render>