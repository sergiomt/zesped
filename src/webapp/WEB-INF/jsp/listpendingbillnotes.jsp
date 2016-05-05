<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/lightbox.jsp" pageTitle="Notas de gasto pendientes de liquidación">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox">
			 <div class="yui3-g">
			   <div class="yui3-u" id="searchfilter">
			     <stripes:form name="filter" beanclass="com.zesped.action.ListPendingBillNotes">
				   <div class="columnitem"><h2>Filtro</h2></div>
			       <div class="columnitem">
			       <stripes:label for="label.status" /><br/>	       
			       <select class="darkcombo" name="status">
			         <option value=""><stripes:label for="option.any" /></option>
			         <option value="open" selected="selected"><stripes:label for="option.openff" /></option>
			         <option value="closed"><stripes:label for="option.closedff" /></option>
			        </select>
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
			       <stripes:label for="label.search" /><br/>
			       <input type="text" name="search" class="darkcombo"><br/>
			       <br/>
			       <center>
			         <input id="filterbutton" name="filterbutton" type="button" class="login" value="Filtrar" />
			         <img id="filtering" src="img/loading.gif" width="32" height="16" hspace="15" alt="filtering" style="display:none" />
			       </center>
			       </div>
    			   <script type="text/javascript" src="js/panel.js"></script>
    			   <script type="text/javascript" src="js/validations.js"></script>
    			   <script type="text/javascript" src="js/listbillnotes.js" defer="defer"></script>
    			   <script type="text/javascript" src="js/listpendingbillnotes.js"></script>
			     </stripes:form>
			   </div> <!-- yui3-u -->
			   <div class="yui3-u">
			     <div id="changeview">
			       <table align="right">
							 <tr>
							   <td valign="middle"><img src="img/dustbin16.gif" width="11" height="14" border="0" alt="Papelera" /></td>							
							   <td valign="middle">&nbsp;<a id="delete" class="anchorhighlight" href="#"><stripes:label for="label.deleteBillNotes" /></a></td>
						   </tr>
			       </table>
			     </div>
			     <div id="datatable" class="yui3-skin-night yui3-datatable"></div>
			     <div style="clear:both;"><div id="showprev" style="margin-left:8px;margin-right:8px;float:left;display:none"><a href="#" id="previous"><stripes:label for="label.previous" /></a></div><div id="shownext" style="margin-left:8px;display:none"><a href="#" id="next"><stripes:label for="label.next" /></a></div></div>
			   </div> <!-- yui3-u -->
		 </div> <!-- yui3-g -->
		</div> <!-- darkbox -->
		<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div><!-- xsnazzy0 -->
  </stripes:layout-component>
</stripes:layout-render>