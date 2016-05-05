<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:form id="configaccount" name="configaccount" beanclass="com.zesped.action.EditConfig">
<div class="yui3-g" style="margin:0px 64px 0px 64px">
  <div id="saveconfigerrors" style="height:0px;line-height:normal" ></div>
	<div class="yui3-u-1-2">
		<fieldset>
      <legend>Informaci&oacute;n de Usuario</legend>
      <table>
        <tr>
          <td class="formlabelrequired">Nombre</td>
          <td class="formfields"><stripes:text name="user.firstName" />
        </tr>
        <tr>
          <td class="formlabelrequired">Apellidos</td>
          <td class="formfields"><stripes:text name="user.lastName" />
        </tr>
        <tr>
          <td class="formlabelrequired">e-mail</td>
          <td class="formfields"><stripes:text name="user.email" />
        </tr>
        <tr>
          <td class="formlabelrequired">Confirmar e-mail</td>
          <td class="formfields"><stripes:text name="email2" />
        </tr>
      </table>
		</fieldset>
  </div>
	<div class="yui3-u-1-2">
		<fieldset>
      <legend>Cambiar Contrase&ntilde;a</legend>
      <table>
        <tr>
          <td class="formlabelrequired">Contrase&ntilde;a actual</td>
          <td class="formfields"><stripes:password name="formerPassword" />
        </tr>
        <tr>
          <td class="formlabelrequired">Nueva contrase&ntilde;a</td>
          <td class="formfields"><stripes:password name="newPassword" />
        </tr>
        <tr>
          <td class="formlabelrequired">Confirmar contrase&ntilde;a</td>
          <td class="formfields"><stripes:password name="newPassword2" />
        </tr>
        <tr>
          <td class="formlabelrequired">&nbsp;</td>
          <td class="formfields"><input type="text" style="visibility:hidden" /></td>
        </tr>
      </table>
		</fieldset>
  </div>
</div>
<div class="yui3-g" style="margin:10px 64px 10px 64px">
	<div class="yui3-u-1-2">
		<fieldset>
      <legend>Preferencias de cuenta</legend>
      <table>
        <tr>
          <td class="formlabelrequired">Iniciar sesion en</td>
          <td class="formfields">
            <stripes:select name="initialScreen">
              <optgroup label="Facturas">
                <option value="CaptureInvoice.action">Digitalizar Factura</option>
                <option value="ListNewInvoices.action">Entrar Datos de Factura</option>
                <option value="ListPendingInvoices.action">Aprobar Facturas</option>
                <option value="ListApprovedInvoices.action">Consultar Facturas</option>
              </optgroup>
              <optgroup label="Notas de Gasto">
                <option value="CaptureBillNote.action">Digitalizar Nota de Gasto</option>
                <option value="ListNewBillNotes.action">Entrar Datos de Nota de Gasto</option>
                <option value="ListPendingInvoices.action">Liquidar Notas de Gasto</option>
                <option value="ListApprovedInvoices.action">Consultar Notas de Gasto</option>
              </optgroup>
            </stripes:select>
        </tr>
      </table>
		</fieldset>
  </div>
	<div class="yui3-u-1-2">
		<fieldset>
      <legend>Preferencias de avisos</legend>
      <table>
        <tr>
          <td class="formlabelrequired">Enviar avisos</td>
          <td class="formfields">
            <stripes:select name="alerts">
                <option value="">Siempre</option>
            </stripes:select>
        </tr>
      </table>
		</fieldset>
  </div>
</div>
<div class="yui3-g">
  <div class="yui3-u-1" style="margin:10px 64px 10px 64px;text-align:right">
    <input id="saveconfigbutton" type="button" class="submit" value="Actualizar" onclick="saveUserConfig()" align="right" />
    <img id="savingconfig" src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" style="display:none" align="right" />
    <br/><br/>
  </div>
</div>
  
</stripes:form>
