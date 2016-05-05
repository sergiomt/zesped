<%@ page import="com.zesped.model.Product"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zesped.action.EngageCredit"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="net.sourceforge.stripes.action.ActionBean"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% EngageCredit oAbn = (EngageCredit) request.getAttribute("actionBean"); %>
<stripes:layout-definition>

    <stripes:layout-component name="stylecreditheader">

    </stripes:layout-component>
    <stripes:messages/>
    <stripes:errors/>
    <stripes:form id="engagecredit" beanclass="${beanclass}">
        <stripes:layout-component name="headercredit">
        </stripes:layout-component>
        <div class="yui3-gb" style="margin:0px 64px 0px 64px"> 
            <fieldset>
                <legend>C&oacute;digo Promocional</legend>
                <table>
                    <tr>
                        <td class="formlabelrequired">Código:</td>
                        <td class="formfields" ><stripes:text name="promo_code" />
                    </tr>
                </table>
            </fieldset>
        </div>

        <div class="yui3-gb" style="margin:0px 64px 0px 64px">
            <fieldset>
                <legend>Contrataci&oacute;n</legend>
                <table>                
                    <c:forEach items="${actionBean.products}" var="i">

                        <c:set var="contains" value="false" />
                        <c:forEach var="item" items="${hiddenProducts}">
                            <c:if test="${i.id==item}">
                                <c:set var="contains" value="true" />
                            </c:if>
                        </c:forEach>                    
                        <c:if test="${contains == false}">
                            <tr> 
                                <td><stripes:radio name="selectedProduct" value="${i.id}" onclick="showPay(this)"/></td>
                                <td>${i.product_name} - ${i.price} ${i.currency}</td> 
                            </tr>
                        </c:if>
                    </c:forEach>
                </table>
            </fieldset>
        </div>

        <div class="yui3-gb" style="margin:10px 64px 10px 64px">
            <fieldset>
                <legend>Datos Facturaci&oacute;n</legend>
                <table style="width: 100%">
                    <tr>
                        <td class="formlabelrequired">Raz&oacute;n social o titular:(*)</td>
                        <td class="formfields" colspan="3"><stripes:text name="name" style="width:100%"/>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">CIF o NIF:(*)</td>
                        <td class="formfields"><stripes:text name="cif" style="width:100%;"/>
                        <td class="formlabelrequired">Tel&eacute;fono de contacto:(*)</td>
                        <td class="formfields"><stripes:text name="phone" style="width:100%;"/>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Email:(*)</td>
                        <td class="formfields" colspan="3"><stripes:text name="mail"  style="width:100%"/>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Direcci&oacute;n:(*)</td>
                        <td class="formfields" colspan="3"><stripes:text name="address" style="width:100%"/>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Poblaci&oacute;n:(*)</td>
                        <td class="formfields">
                            <stripes:text name="city"  style="width:100%"/>
                        </td>
                        <td class="formlabelrequired">C&oacute;digo Postal:(*)</td>
                        <td class="formfields"><stripes:text name="postcode" style="width:100%;"/>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Provincia:(*)</td>
                        <td class="formfields">
                            <stripes:text name="state"  style="width:100%"/>
                        </td>
                        <td class="formlabelrequired">Pa&iacute;s:(*)</td>
                        <td class="formfields">
                            <stripes:text name="country"  style="width:100%"/>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </div>
        <div class="yui3-gb" id="pay" style="margin:10px 64px 10px 64px; 
             <%
            Product oProduct = oAbn.getSelectedProduct();
                if (oProduct != null) {
                    BigDecimal obd = new BigDecimal(oProduct.getString("price"));
                    if (obd.compareTo(BigDecimal.ZERO) > -1) {
                        out.print("display:none");
                    }
                }
            %>
            " 
             >
            <fieldset>
                <legend>Datos de pago</legend>
                <table style="width: 100%">
                    <tr>
                        <td class="formlabelrequired">N&uacute;mero Tarjeta:(*)</td>
                        <td class="formfields"><stripes:text name="cardnumber" style="width:100%"/>
                        <td class="formlabelrequired">Nombre Titular:(*)</td>
                        <td class="formfields"><stripes:text name="cardholder" style="width:100%"/>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Fecha Caducidad:(*)</td>
                        <td class="formfields">
                            <stripes:select id="expiration_month_card" name="expiration_month_card" ><stripes:options-collection collection="${actionBean.months}" value="value" label="name" /></stripes:select>
                            <stripes:select id="expiration_year_card" name="expiration_year_card" ><stripes:options-collection collection="${actionBean.yearslater}" value="value" label="name" /></stripes:select>
                            </td>
                            <td class="formlabelrequired">C&oacute;digo Seguridad:(*)</td>
                            <td class="formfields"><stripes:password id="cvv2" name="cvv2" style="width:100%"/>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Aceptamos</td>
                        <td><img src="img/visa.png"/></td>
                    </tr>
                </table>
            </fieldset>
        </div>
        <div class="yui3-g">
            <div class="yui3-u-1" style="margin:10px 64px 10px 64px;text-align:right">
                <stripes:button id="dopay" name="save" class="submit" onclick="sendPurchasing(this);">Pedir</stripes:button>
                <div id="load" style="height: 32px; display: none;"><img  src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" /></div>
                <input type="hidden" name="_eventName" value="save">
            </div>
        </div>
        <br/><br/>
    </stripes:form>
    <stripes:layout-component name="stylecreditfoot">

    </stripes:layout-component>
</stripes:layout-definition>