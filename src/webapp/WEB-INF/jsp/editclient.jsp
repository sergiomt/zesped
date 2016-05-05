<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<div id="clientbox" style="width:600px;height:600px">
  <div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
	  <div class="darkbox" style="height:18px"><div style="padding:6px"><big>Editar datos de empresa</big></div></div>
	<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	<div id="saveclienterrors" style="height:0px;line-height:normal" ></div>
	<stripes:form name="clientData" beanclass="com.zesped.action.EditClient">
	<stripes:hidden name="client.id" />
	<stripes:hidden name="client.taxPayer" />
  <table>
    <tr><td class="formlabelrequired">Raz&oacute;n Social</td><td class="formfields"><stripes:text name="client.businessName" size="40" style="text-transform:uppercase" /></td></tr>
    <tr><td class="formlabelrequired">CIF</td><td class="formfields"><stripes:text name="client.taxId" size="12" /></td></tr>
    <tr><td class="formlabeloptional">Pais</td><td class="formfields"><stripes:select id="client.country" name="client.country" style="width:240px" onchange="updateClientStates(this.options[this.selectedIndex].value)"><stripes:option value="" /><stripes:options-collection collection="${actionBean.countries}" value="isoCode" label="name" /></stripes:select></td></tr>
    <tr><td class="formlabeloptional">Provincia</td>
        <td class="formfields">
          <stripes:select id="client.state" name="client.state" style="width:240px" onchange="updateClientCities(getCombo(document.forms['clientData'].elements['client.country']),this.options[this.selectedIndex].value)"><stripes:option value="" /><stripes:options-collection collection="${actionBean.states}" value="code" label="name" /></stripes:select>
          <stripes:text name="otherState" style="width:240px;display:none" />
        </td>
    </tr>
    <tr><td class="formlabeloptional">C&oacute;digo Postal</td><td class="formfields"><stripes:text name="client.zipCode" size="8" /></td></tr>
    <tr><td class="formlabeloptional">Localidad</td>
        <td class="formfields">
          <stripes:select id="client.city" name="client.city" style="width:240px"></stripes:select>
          <stripes:text name="otherCity" style="width:240px;display:none" />
        </td>
    </tr>
    <tr><td class="formlabeloptional">Direcci&oacute;n L&iacute;nea 1</td><td class="formfields"><stripes:text name="client.address1" size="40" /></td></tr>
    <tr><td class="formlabeloptional">Direcci&oacute;n L&iacute;nea 2</td><td class="formfields"><stripes:text name="client.address2" size="40" /></td></tr>
    <tr><td class="formlabeloptional">Persona de Contacto</td><td class="formfields"><stripes:text name="client.contactPerson" size="40" /></td></tr>
    <tr><td class="formlabeloptional">Tel&eacute;fono</td><td class="formfields"><stripes:text name="client.telephone" size="16" /></td></tr>
    <tr><td class="formlabeloptional">E-mail</td><td class="formfields"><stripes:text name="client.email" size="40" /></td></tr>
    <tr><td class="formlabeloptional"></td><td class="formfields"><input type="button" class="submit" value="Guardar" onclick="saveClient()" /></td></tr>
  </table>
  </stripes:form>
</div>