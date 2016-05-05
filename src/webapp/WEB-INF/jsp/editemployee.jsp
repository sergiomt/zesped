<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<div id="clientbox" style="width:480px;height:280px">
  <div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
	  <div class="darkbox" style="height:18px"><div style="padding:6px"></span><big>Editar empleado</big></div></div>
	<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	<div id="saveemployeeerrors" style="height:0px;line-height:normal" ></div>
	<stripes:form name="employeeData" beanclass="com.zesped.action.EditEmployee">
	<stripes:hidden name="employee.id" />
	<stripes:hidden name="employee.uuid" />
  <table>
    <tr>
      <td class="formlabelrequired">Empresa</td>
      <td class="formfields">
			  <stripes:select name="taxPayer" class="taxpayerlist">
				<stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" />
		    </stripes:select>
      </td>
    </tr>
    <tr><td class="formlabelrequired">Nombre</td><td class="formfields"><stripes:text name="employee.name" size="30" /></td></tr>
    <tr><td class="formlabelrequired">N&ordm; empleado</td><td class="formfields"><stripes:text name="employee.employeeId" size="12" /></td></tr>
    <tr><td class="formlabelrequired">Doc. Identidad</td><td class="formfields"><stripes:text name="employee.taxId" size="12" /></td></tr>
    <tr><td class="formlabelrequired"><stripes:checkbox name="employee.active" /></td><td class="formfields">Activo</td></tr>
    <tr><td class="formlabeloptional"></td><td class="formfields"><input type="button" class="submit" value="Guardar" onclick="saveEmployee()" /></td></tr>
  </table>
  </stripes:form>
</div>