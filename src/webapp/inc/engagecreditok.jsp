<%@ page import="java.util.MissingResourceException" language="java" session="false" %>
<%@page import="com.zesped.idl.data.Tpv"%>
<%@page import="com.sun.istack.internal.localization.Localizable"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.zesped.action.EngageCredit"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.zesped.model.OrderLine"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="net.sourceforge.stripes.action.ActionBean"%>
<%@ include file="atril.jspf" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%EngageCredit oAbn = (EngageCredit) request.getAttribute("actionBean");
	BigDecimal oStatus = oAbn.getOrder().getBigDecimal("status_number");
	String sStatus = "";
	boolean noVolverPagar = false;
	if (oStatus != null) {
		sStatus = StripesResources.getString("TPV.STATUS"+oStatus.intValue());
		if (oStatus.compareTo(Tpv.PAGADO) == 0 || oStatus.compareTo(Tpv.PENDIENTE_CONFIRMACION) == 0) {
			noVolverPagar = true;
		}
	}%>
<stripes:layout-definition>
    <stripes:layout-component name="stylecreditheader">

    </stripes:layout-component>

    <stripes:messages/>
    <stripes:errors/>

    <stripes:form beanclass="com.zesped.action.EngageCredit">
        <stripes:layout-component name="headercredit">
        </stripes:layout-component>
        <div class="yui3-gb" style="margin:0px 64px 0px 64px"> 
            <fieldset>
                <legend>Estado del pedido</legend>
                <div class="formfields">
                    El estado de su pedido es: <%=sStatus%>
                </div>
            </fieldset>
        </div>
        <div class="yui3-gb" style="margin:0px 64px 0px 64px">
            <fieldset>
                <legend>Resumen del pedido</legend>
                <table>                
                    <tr>
                        <td class="formlabelrequired">Número: </td>
                        <td class="formfields" >${actionBean.order.order_id}<input id="order_id" type="hidden" value="${actionBean.order.getId()}"/></td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Fecha: </td>
                        <td class="formfields" >
                            <%
								Date date = oAbn.getOrder().getDate("creation_date");
								SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
								String fecha = sdf.format(date);
								out.print(fecha);
                            %>
                        </td>                        
                    </tr>
                </table>
                <br/>
                <table style="width: 100%">
                    <tr>
                        <td class="formlabelrequired" style="text-align: left">Producto</td><td class="formlabelrequired" style="text-align: center">Cantidad</td><td class="formlabelrequired" >Precio</td><td class="formlabelrequired">Total</td></tr>
                        <% for (OrderLine ol : oAbn.getOrder().lines()) {%>
                    <tr>   
                        <td class="formfields" style="text-align: left"><%=ol.getString("product_name")%></td>
                        <td class="formfields" style="text-align: center">1</td>
                        <td class="formfields" style="text-align: right">
                            <%
								double dTotal = Double.parseDouble(ol.getString("base_price"));
								DecimalFormat df = new DecimalFormat("0.00");
								String s = df.format(dTotal);
								out.print(s);
                            %>
                            <%=ol.getString("currency")%></td>
                        <td class="formfields" style="text-align: right">
                            <%
								dTotal = Double.parseDouble(ol.getString("base_price"));
								df = new DecimalFormat("0.00");
								s = df.format(dTotal);
								out.print(s);
                            %>
                            <%=ol.getString("currency")%></td>
                    </tr>
                    <%}%>

                </table>
                <br/>
                <table style="float: right">
                    <tr>
                        <td class="formlabelrequired">Subtotal</td>
                        <td class="formfields" style="text-align: right">
                            <%
								double dTotal = Double.parseDouble(oAbn.getOrder().getString("base_price"));
								DecimalFormat df = new DecimalFormat("0.00");
								String s = df.format(dTotal);
								out.print(s);
                            %>      
                            ${actionBean.order.currency}</td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">IVA (${actionBean.order.taxespct})</td>
                        <td class="formfields" style="text-align: right">
                            <%
								dTotal = Double.parseDouble(oAbn.getOrder().getString("taxes"));
								df = new DecimalFormat("0.00");
								s = df.format(dTotal);
								out.print(s);
                            %>     
                            ${actionBean.order.currency}
                        </td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Total</td>
                        <td class="formfields" style="text-align: right">
                            <%
								dTotal = Double.parseDouble(oAbn.getOrder().getString("total_price"));
								df = new DecimalFormat("0.00");
								s = df.format(dTotal);
								out.print(s);
                            %>
                            ${actionBean.order.currency}</td>
                    </tr>

                </table>
            </fieldset>
        </div>

        <div class="yui3-gb" style="margin:10px 64px 10px 64px">
            <fieldset>
                <legend>Datos Facturación</legend>
                <table style="width: 100%">
                    <tr>
                        <td class="formlabelrequired">Razón social o titular:(*)</td>
                        <td class="formfields" colspan="3">${actionBean.order.name}</td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">CIF o NIF:(*)</td>
                        <td class="formfields">${actionBean.order.cif}</td>
                        <td class="formlabelrequired">Teléfono de contacto:(*)</td>
                        <td class="formfields">${actionBean.order.phone}</td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Email:(*)</td>
                        <td class="formfields" colspan="3">${actionBean.order.mail}</td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Dirección:(*)</td>
                        <td class="formfields" colspan="3">${actionBean.order.address}</td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Población:(*)</td>
                        <td class="formfields">${actionBean.order.city}</td>
                        <td class="formlabelrequired">Código Postal:(*)</td>
                        <td class="formfields">${actionBean.order.postcode}</td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Provincia:(*)</td>
                        <td class="formfields">${actionBean.order.state}</td>
                        <td class="formlabelrequired">País:(*)</td>
                        <td class="formfields">${actionBean.order.country}</td>
                    </tr>
                </table>
            </fieldset>
        </div>
        <div class="yui3-gb" style="margin:10px 64px 10px 64px; <%if (Double.parseDouble(oAbn.getOrder().getString("total_price")) == 0) {
				out.print("display: none");
			}%>" >
            <fieldset>
                <legend>Datos de pago</legend>

				<% if (oStatus!=null && (oStatus.compareTo(Tpv.PAGADO) == 0 || oStatus.compareTo(Tpv.PENDIENTE_CONFIRMACION) == 0)) { %>
                <table style="width: 100%">
                    <tr>
                        <td class="formlabelrequired">Número Tarjeta:(*)</td>
                        <td class="formfields">${actionBean.order.cardnumber}</td>
                        <td class="formlabelrequired">Nombre Titular:(*)</td>
                        <td class="formfields">${actionBean.order.cardholder}</td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Fecha Caducidad:(*)</td>
                        <td class="formfields">
							<%=com.knowgate.misc.Calendar.MonthName(new Integer(oAbn.getOrder().getExpiration_month_card())-1,oAbn.getContext().getLocale().getLanguage())%>
							${actionBean.order.expiration_year_card}
                        </td>
                        <td class="formlabelrequired">Código Seguridad:(*)</td>
                        <td class="formfields">${actionBean.order.cvv2}</td>
                    </tr>
                </table>
				<% } else { %>
                <table style="width: 100%">
                    <tr>
                        <td class="formlabelrequired">Número Tarjeta:(*)</td>
                        <td class="formfields">
							<input id="cardnumber" name="cardnumber" style="width:100%" value="${actionBean.order.cardnumber}"/>
						</td>
                        <td class="formlabelrequired">Nombre Titular:(*)</td>
                        <td class="formfields"><input id="cardholder" name="cardholder" style="width:100%" value="${actionBean.order.cardholder}"/></td>
                    </tr>
                    <tr>
                        <td class="formlabelrequired">Fecha Caducidad:(*)</td>
                        <td class="formfields">
                            <stripes:select id="expiration_month_card" name="expiration_month_card" value="${actionBean.order.expiration_month_card}">
                                <stripes:options-collection collection="${actionBean.months}" value="value" label="name" />
                            </stripes:select>
                            <stripes:select id="expiration_year_card" name="expiration_year_card" value="${actionBean.order.expiration_year_card}">
                                <stripes:options-collection collection="${actionBean.yearslater}" value="value" label="name" />
                            </stripes:select>
                        </td>
                        <td class="formlabelrequired">Código Seguridad:(*)</td>
                        <td class="formfields"><input type="password" id="cvv2" name="cvv2" style="width:100%" value="${actionBean.order.cvv2}"/>
                    </tr>
                </table>
				<% } %>
            </fieldset>
        </div>

        <div class="yui3-g">
            <div class="yui3-u-1" style="margin:10px 64px 10px 64px;text-align:right">
                <stripes:layout-component name="buttonsok">
					<%
						if (oStatus!=null && (oStatus.compareTo(Tpv.PAGADO) == 0 || oStatus.compareTo(Tpv.PENDIENTE_CONFIRMACION) == 0)) {
					%>
					<stripes:button id="endpay" name="send" class="submit" onclick="payPurchasing('false');">Finalizar</stripes:button>
						<div id="load" style="height: 32px; display: none;"><img  src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" /></div>
						<%} else {%>
						<stripes:button id="endpay" name="send" class="submit" onclick="payPurchasing('true');">Realizar pago</stripes:button>
						<div id="load" style="height: 32px; display: none;"><img  src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" /></div>
						<% }%>
						<br/><br/>
					</stripes:layout-component>
            </div>
        </div>

    </stripes:form>

    <stripes:layout-component name="formfoot">
    </stripes:layout-component>
    <stripes:layout-component name="stylecreditfoot">

    </stripes:layout-component>
</stripes:layout-definition>