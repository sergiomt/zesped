<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/barelogo.jsp" pageTitle="Comprar Creditos">
  <stripes:layout-component name="contents">
      <div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
        <h2><stripes:label for="buycredits.form" /></h2>
        <p>Antes de empezar a utilizar zesped, debe seleccionar un paquete de cr&eacute;ditos de uso.<br/>
           Puede seleccionar un paquete gratuito si s&oacute;lo desea evaluar zesped sin coste alguno para usted.</p>
        <stripes:messages/>
        <stripes:errors/>
				<stripes:form name="buycredits" beanclass="com.zesped.action.BuyCredits">
				  <table>
				    <tr>
				      <td class="formlabelrequired">
				      </td>
				      <td class="formlabelrequired">
							<stripes:select id="selectedProduct" name="selectedProduct">
							  <stripes:option value=""><stripes:label for="buycredits.selectAProduct" /></stripes:option>
							  <stripes:options-collection collection="${actionBean.products}" value="id" label="label" />
							</stripes:select>
						</td>
				    <tr>
				      <td class="formlabelrequired"></td>
				      <td class="formlabelrequired"><stripes:submit class="submit" name="buy" id="buy" style="display:none" /></td>
				    </tr>
				  </table>
				  <script type="text/javascript">
				    YUI().use('event', function (Y) {
				    	var selp = Y.one("#selectedProduct");
				    	selp.on('change', function (e) {
					    	var frm = document.forms["buycredits"];
					    	var prd = frm.selectedProduct;
					    	if (prd.selectedIndex==0) {
					    		frm.buy.style.display="none";
					    	} else {
						    	if (prd.options[prd.selectedIndex].value.substr(0,9)=="FREETRIAL")
						    		frm.buy.value='<stripes:label for="buycredits.freeTrial" />';
						    	else
						    		frm.buy.value='<stripes:label for="buycredits.buyCredits" />';
						    	if (frm.buy.value.indexOf(">")>=0 && frm.buy.value.indexOf("<")>=0)
						    	  frm.buy.value = frm.buy.value.substring(frm.buy.value.indexOf(">")+1,frm.buy.value.indexOf("<",1));
					    		frm.buy.style.display="block";
					    	} // fi
				    	});    	
				    });
				  </script>
				</stripes:form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>