<%@ page import="com.zesped.model.Role" pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="userbox" style="width:480px;height:540px">
  <div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
	  <div class="darkbox" style="height:18px"><div style="padding:6px"><big>Editar usuario</big></div></div>
	<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	<div id="saveusererrors" style="height:0px;line-height:normal" ></div>
	<stripes:form name="userData" beanclass="com.zesped.action.EditUser">
	<stripes:hidden name="id" />
	<stripes:hidden name="user.nickName" />
	<stripes:hidden name="selectedTaxPayers" />
  <table>
    <tr><td class="formlabelrequired">Nombre</td><td class="formfields"><stripes:text name="user.firstName" size="40" /></td></tr>
    <tr><td class="formlabelrequired">Apellidos</td><td class="formfields"><stripes:text name="user.lastName" size="40" /></td></tr>
    <tr><td class="formlabelrequired">E-mail:</td><td class="formfields"><stripes:text name="user.email" size="40" /></td></tr>
    <c:if test="${empty actionBean.user}">
      <tr><td class="formlabelrequired">Contrase&ntilde;a:</td><td class="formfields"><stripes:password name="user.password" /></td></tr>
      <tr><td class="formlabelrequired">Confirmar:</td><td class="formfields"><stripes:password name="password2" /></td></tr>
    </c:if>
    <tr><td class="formlabelrequired">Rol:</td><td class="formfields">
      <stripes:select name="role" onchange="onChangeRole()">
      <option value="<%=Role.admin%>" ${actionBean.role=='admin' ? 'selected' : '' }>Propietario</option>
      <option value="<%=Role.user%>" ${actionBean.role=='user' && !actionBean.isEmployee ? 'selected' : '' }>Colaborador</option>
      <option value="<%=Role.user%>" ${actionBean.role=='user' && actionBean.isEmployee ? 'selected' : '' }>Empleado</option>
      <option value="<%=Role.guest%>" ${actionBean.role=='guest' ? 'selected' : '' }>Lector</option></stripes:select>
      &nbsp;&nbsp;<stripes:checkbox name="active" />&nbsp;Activo
    </td></tr>
    <tr>
      <td class="formlabelrequired"><span id="companyLabel" style="display:none">Empresa</span></td>
      <td>
        <stripes:select style="width:200px;display:none" name="taxPayer" onchange="onChangeTaxPayer()">
          <option value=""></option><stripes:options-collection collection="${actionBean.taxPayers}" value="id" label="businessName" />
        </stripes:select>
      </td>
    </tr>
    <tr>
      <td></td>
      <td>
      	<div id="employeedata" style="display:${empty actionBean.isEmployee ? 'block' : 'none'}">
      	  <table>
    				<tr>
    				  <td class="formlabeloptional">Id.</td><td class="formfields"><stripes:text name="employee.employeeId" size="8" /></td>
    			    <td class="formlabeloptional">NIF</td><td class="formfields"><stripes:text name="employee.taxId" size="8" /></td>
    			  </tr>
      	  </table>
      	</div>
      </td>
    </tr>
    <c:choose>
      <c:when test="${actionBean.role=='admin'}">
        <tr><td class="formlabeloptional"><div id="chk1c" style="display:none"><stripes:checkbox name="approve" checked="checked" /></div></td><td class="formfields"><div id="chk1l" style="display:none">Puede aprobar facturas</div></td></tr>   
        <tr><td class="formlabeloptional"><div id="chk2c" style="display:none"><stripes:checkbox name="settle" checked="checked" /></div></td><td class="formfields"><div id="chk2l" style="display:none">Puede liquidar notas de gasto</div></td></tr>   
        <tr><td class="formlabeloptional"><div id="chk3c" style="display:none"><stripes:checkbox name="premium" checked="checked" /></div></td><td class="formfields"><div id="chk3l" style="display:none">Puede consumir cr&eacute;ditos premium</div></td></tr>
        <tr><td colspan="2"><img src="img/spacer.gif" width="1" height="20" alt="" /></td></tr>
      </c:when>
      <c:otherwise>
        <tr><td class="formlabeloptional"><div id="chk1c"><stripes:checkbox name="approve" /></div></td><td class="formfields"><div id="chk1l">Puede aprobar facturas</div></td></tr>   
        <tr><td class="formlabeloptional"><div id="chk2c"><stripes:checkbox name="settle" /></div></td><td class="formfields"><div id="chk2l">Puede liquidar notas de gasto</div></td></tr>   
        <tr><td class="formlabeloptional"><div id="chk3c"><stripes:checkbox name="premium" /></div></td><td class="formfields"><div id="chk3l">Puede consumir cr&eacute;ditos premium</div></td></tr>
        <tr><td colspan="2"><img src="img/spacer.gif" width="1" height="20" alt="" /></td></tr>
      </c:otherwise>
    </c:choose>
    
    <tr><td colspan="2"><img src="img/spacer.gif" width="1" height="20" alt="" /></td></tr>
    </table>
    <div id="companyList">
    <table>
    <tr>
      <td colspan="2" class="formfields">Empresas que puede editar este usuario
      &nbsp;<!--  ${actionBean.permissions} -->
      <stripes:select name="permissions" onchange="document.getElementById('taxpayersmultiselect').src=this.selectedIndex<=0 ? 'blank.htm' : 'MultiSelectTaxPayers.action?id=${actionBean.id}'"><option value="all" ${actionBean.permissions=='all' ? 'selected' : '' }>Todas</option><option value="allow" ${actionBean.permissions=='allow' ? 'selected' : '' }>S&oacute;lo las siguientes</option><option value="deny" ${actionBean.permissions=='deny' ? 'selected' : '' }>Todas excepto las siguientes</option></stripes:select>
      </td>
    </tr>
    <tr><td colspan="2"><img src="img/spacer.gif" width="1" height="10" alt="" /></td></tr>
    </table></div>
    <iframe id="taxpayersmultiselect" width="460px" height="140px" frameborder="0" border="0" ${actionBean.permissions!='all' ? 'src=MultiSelectTaxPayers.action?id=' : ''}${actionBean.permissions!='all' ? actionBean.id : ''}></iframe>
    <center><img id="savinguser" style="display:none" src="img/loading.gif" width="32" height="16" /><input id="saveuserbutton" type="button" class="submit" value="Guardar" onclick="saveUser()" /></center>
  </stripes:form>
</div>