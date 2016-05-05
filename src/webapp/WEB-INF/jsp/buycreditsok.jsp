<%@page import="java.math.BigDecimal"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@page import="net.sourceforge.stripes.action.ActionBean"%>
<%@page import="com.zesped.action.EngageCredit"%>
<%@page import="com.zesped.idl.data.Tpv"%>
<%EngageCredit oAbn = (EngageCredit) request.getAttribute("actionBean");
BigDecimal status = oAbn.getOrder().getBigDecimal("status_number");%>

<stripes:layout-render name="/inc/default.jsp" pageTitle="Comprar Creditos">
    <stripes:layout-component name="contents" >
        <stripes:layout-render name="/inc/engagecreditok.jsp" pageTitle="Contratación de créditos">
            <stripes:layout-component name="stylecreditheader">

                <div class="outerbox"><b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
                    <div class="textbox">
                    </stripes:layout-component>
                    <stripes:layout-component name="buttonsok">
                        <%if (status!=null && (status.compareTo(Tpv.PAGADO) == 0 || status.compareTo(Tpv.PENDIENTE_CONFIRMACION) == 0)) {%>                         
                        <stripes:button id="endpay" name="send" class="submit" onclick="payPurchasing('false');">Finalizar</stripes:button>
                        <div id="load" style="height: 32px; display: none;"><img  src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" /></div>
                        <%} else {
                        %>                           
                        <stripes:button id="endpay" name="send" class="submit" onclick="payPurchasing('true');">Realizar pago</stripes:button>
                        <div id="load" style="height: 32px; display: none;"><img  src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" /></div>
                        <%   }%>
                    </stripes:layout-component>
                    <stripes:layout-component name="formfoot">
                        <% if (Double.parseDouble(oAbn.getOrder().getString("total_price")) != 0) {%>
                        <form id="payPurchasing" action="<%=Tpv.getTpvUrl()%>" method="post">
                            <input type="hidden" id="peticion" name="peticion" value="">
                        </form>
                        <% }%>
                    </stripes:layout-component>   
                    <stripes:layout-component name="stylecreditfoot">
                    </div>
                    <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b></div>
                </stripes:layout-component>
            </stripes:layout-render>
    </stripes:layout-component>
</stripes:layout-render>
<script>
                function payPurchasing(pay){
                document.getElementById("endpay").style.display="none";
                document.getElementById("load").style.display="block";    
                if(pay=="true"){
                    var order_id = document.getElementById("order_id").value;
					var cardnumber = document.getElementById("cardnumber").value;
                    var cardholder = document.getElementById("cardholder").value;
                    var expiration_month_card = document.getElementById("expiration_month_card").value;
                    var expiration_year_card = document.getElementById("expiration_year_card").value;
                    var cvv2 = document.getElementById("cvv2").value;                
                    document.getElementById("endpay").style.display="none";
                    document.getElementById("load").style.display="block";                    
                        
                        YUI().use('io', function(Y) {
                            Y.io("EngageCredit.action", { method:'POST',
                                data: '_eventName=updatePendigState&order_id='+order_id+
									'&cardnumber='+cardnumber+'&cardholder='+cardholder+
									'&expiration_month_card='+expiration_month_card+'&expiration_year_card='+expiration_year_card+
									'&cvv2='+cvv2,
                                on: {'complete':function (i, o, a) {
										var newpeticion = eval(o.responseText);
										document.getElementById("peticion").value=newpeticion;
                                        document.getElementById("payPurchasing").submit();
                                    } } }
                        )}); 
                }else{
                    window.location.href='CaptureInvoice.action';
                }
               
            }
    
</script>