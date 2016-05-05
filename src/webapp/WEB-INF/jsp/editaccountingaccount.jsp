<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<div id="clientbox" style="width:480px;height:280px">
  <div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
	  <div class="darkbox" style="height:18px"><div style="padding:6px"></span><big>Editar Cuenta Contable</big></div></div>
	<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	<div id="saveaccountingaccounterrors" style="height:0px" ></div>
	<stripes:form name="accountingAccountData" beanclass="com.zesped.action.EditAccountingAccount">
	<stripes:hidden name="taxPayer" />
	<stripes:hidden name="accountingAccount.id" />
	<stripes:hidden name="accountingAccount.uuid" />
  <table>
    <tr>
      <td class="formlabelrequired">C&oacute;digo</td>
      <td class="formfields"><stripes:text name="accountingAccount.code" size="15" /></td>
    </tr>
    <tr>
      <td class="formlabelrequired">Descripci&oacute;n</td>
      <td class="formfields"><stripes:text name="accountingAccount.description" size="40" /></td>
    </tr>
    <tr>
      <td class="formlabelrequired">Activa</td>
      <td class="formfields"><input type="checkbox" name="accountingAccount.active" value="1" ${actionBean.active ? "checked" : ""} /></td>
    </tr>
    <tr>
      <td class="formlabeloptional"></td>
      <td class="formfields"><input type="button" class="submit" value="Guardar" onclick="saveAccountingAccount()" /></td>
    </tr>    
  </table>
  </stripes:form>
</div>