<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/listing.jsp" pageTitle="Tickets de gasto pendientes de proceso">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox">
			 <div class="yui3-g">
			   <div class="yui3-u" id="searchfilter">
			     <stripes:form name="filter" beanclass="com.zesped.action.ListNewBillNotes">
				   <div class="columnitem"><h2>Filtro</h2></div>
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
			       <stripes:label for="label.status" /><br/>	       
			       <select class="darkcombo" name="status">
			         <option value=""><stripes:label for="option.any" /></option>
			         <option value="pending" selected="selected"><stripes:label for="option.pending" /></option>
			         <option value="filled"><stripes:label for="option.filled" /></option>
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
			       <div class="columnitem">
			       <stripes:label for="label.payer" /><br/>
						 <stripes:select class="darkcombo" id="recipient" name="recipient">
						   <stripes:option value="" />
						   <stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" />
						 </stripes:select>
			       </div>
			       <div class="columnitem">
			       <stripes:label for="label.employee" /><br/>
			       <stripes:select class="darkcombo" id="employee" name="employee"></stripes:select>
			       </div>
			       <div class="columnitem">
			       <stripes:label for="label.concept" /><br/>
			       <stripes:select class="darkcombo" name="concept"></stripes:select>
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
    			   <script type="text/javascript" src="js/listbillnotes.js" defer="defer"></script>
			     </stripes:form>
			   </div> <!-- yui3-u -->
			   <div class="yui3-u">
			     <div id="changeview">
			       <table align="right">
							 <tr>
							   <td valign="middle"><img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" /></td>							
							   <td valign="middle">&nbsp;<a id="delete" class="anchorhighlight" href="#"><stripes:label for="label.deleteTickets" /></a></td>
						   </tr>
			       </table>
			     </div>
			     <div id="datatable" class="yui3-skin-night yui3-datatable"></div>
			     <div style="clear:both;"><div id="showprev" style="margin-left:8px;margin-right:8px;float:left;display:none"><a href="#" id="previous"><stripes:label for="label.previous" /></a></div><div id="shownext" style="margin-left:8px;display:none"><a href="#" id="next"><stripes:label for="label.next" /></a></div></div>
			   </div> <!-- yui3-u -->
		 </div> <!-- yui3-g -->
		</div> <!-- darkbox -->
		<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div><!-- xsnazzy0 -->
		<br/><br/><br/>
  </stripes:layout-component>
</stripes:layout-render>