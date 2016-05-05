<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/barelogo.jsp" pageTitle="Crear Empresa">
  <stripes:layout-component name="contents">
      <div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
      <div class="textbox">
        <h2><stripes:label for="activatetaxpayer.form" /></h2>
        <p>Para digitalizar facturas y notas de gasto debe completar los datos fiscales de al menos una empresa.</p>
        <stripes:messages />
        <stripes:errors />
				<stripes:form name="taxpayerData" beanclass="com.zesped.action.ActivateTaxPayer">
					<stripes:hidden name="taxPayer.id" />
					<stripes:hidden name="taxPayer.customerAccount" />
				  <table>
				    <tr><td class="formlabelrequired">Raz&oacute;n Social</td><td class="formfields"><stripes:text name="taxPayer.businessName" size="40" style="text-transform:uppercase" /></td></tr>
				    <tr><td class="formlabelrequired">CIF</td><td class="formfields"><stripes:text name="taxPayer.taxId" size="12" /></td></tr>
				    <tr><td class="formlabeloptional">Pais</td><td class="formfields"><stripes:select id="taxPayer.country" name="taxPayer.country" style="width:240px" onchange="updateTaxPayerStates(this.options[this.selectedIndex].value)"><stripes:option value="" /><stripes:options-collection collection="${actionBean.countries}" value="isoCode" label="name" /></stripes:select></td></tr>
				    <tr><td class="formlabeloptional">Provincia</td>
				        <td class="formfields">
				          <stripes:select id="taxPayer.state" name="taxPayer.state" style="width:240px" onchange="updateTaxPayerCities(getCombo(document.forms['taxpayerData'].elements['taxPayer.country']),this.options[this.selectedIndex].value)"><stripes:option value="" /><stripes:options-collection collection="${actionBean.states}" value="code" label="name" /></stripes:select>
				          <stripes:text name="otherState" style="width:240px;display:none" />
				        </td>
				    </tr>
				    <tr><td class="formlabeloptional">C&oacute;digo Postal</td><td class="formfields"><stripes:text name="taxPayer.zipCode" size="8" /></td></tr>
				    <tr><td class="formlabeloptional">Localidad</td>
				        <td class="formfields">
				          <stripes:select id="taxPayer.city" name="taxPayer.city" style="width:240px"></stripes:select>
				          <stripes:text name="otherCity" style="width:240px;display:none" />
				        </td>
				    </tr>
				    <tr><td class="formlabeloptional">Direcci&oacute;n L&iacute;nea 1</td><td class="formfields"><stripes:text name="taxPayer.address1" size="40" /></td></tr>
				    <tr><td class="formlabeloptional">Direcci&oacute;n L&iacute;nea 2</td><td class="formfields"><stripes:text name="taxPayer.address1" size="40" /></td></tr>
				    <tr><td class="formlabeloptional">Persona de Contacto</td><td class="formfields"><stripes:text name="taxPayer.contactPerson" size="40" /></td></tr>
				    <tr><td class="formlabeloptional">Tel&eacute;fono</td><td class="formfields"><stripes:text name="taxPayer.telephone" size="16" /></td></tr>
				    <tr><td class="formlabeloptional">E-mail</td><td class="formfields"><stripes:text name="taxPayer.email" size="40" /></td></tr>
				    <tr><td class="formlabeloptional"></td><td class="formfields"><stripes:submit name="save" class="submit" /></td></tr>
				  </table>
				</stripes:form>
      </div>
      <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>