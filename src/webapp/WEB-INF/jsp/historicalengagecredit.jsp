<%@page import="com.zesped.idl.data.Tpv"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.zesped.model.Order"%>
<%@page import="com.zesped.action.EngageCredit"%>
<%@page import="com.zesped.action.HistorialEngageCredit"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../inc/atril.jspf" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%HistorialEngageCredit oAbn = (HistorialEngageCredit) request.getAttribute("actionBean");%>

<div id="engageCredits" style="width:720px;height:200px;overflow-x: scroll">
    <table style="width: 100%">
    <tr>
        <td class="tableheader"><div style="padding:6px">Pedido</div></td>
        <td class="tableheader"><div style="padding:6px">Fecha</div></td>
        <td class="tableheader"><div style="padding:6px">Base</div></td>
        <td class="tableheader"><div style="padding:6px">IVA</div></td>
        <td class="tableheader"><div style="padding:6px">Total</div></td>
        <td class="tableheader"><div style="padding:6px">Estado</div></td>
        <td></td>
    </tr>
    <% String r = " 0";
   int n = 0;
   for (Order u : oAbn.getOrders()) { 
       r = (r.equals("0") ? "1" : "0"); %>         
    <tr id="tr<%=u.getId()%>">
        <td class="tablerow<%=r%>">
            <div>
                <a href="#" onclick="if (httpRequestText('sessionping.jsp')=='1') {document.getElementById('contratacion').innerHTML = httpRequestText('EngageCredit.action?order_id=<%=u.getId()%>'); DivBoxInstance.end();} else document.location='error.jsp?e=expiredsession';"><%=u.get("order_id")%></a>
            </div>
        </td>
        <td class="tablerow<%=r%>">
            <div style="padding:6px; text-align: center"><%
                Date date = u.getDate("creation_date");
                SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
                String fecha = sdf.format(date);
                out.print(fecha);
                %>
            </div>
        </td>
        <td class="tablerow<%=r%>">
            <div style="padding:6px; text-align: right">
                <%
                double dBase = Double.parseDouble(u.get("base_price").toString());
                DecimalFormat df = new DecimalFormat("0.00");
                String s = df.format(dBase);
                out.print(s);
                %>
            </div></td>
        <td class="tablerow<%=r%>">
            <div style="padding:6px; text-align: right">
                <%
                double iva = Double.parseDouble(u.get("taxespct").toString())*100;
                df = new DecimalFormat("0.00");
                s = df.format(iva);
                out.print(s);
                %>
            </div>
        </td>
        <td class="tablerow<%=r%>">
            <div style="padding:6px; text-align: right">
                <%
                double dTotal = Double.parseDouble(u.get("total_price").toString());
                df = new DecimalFormat("0.00");
                s = df.format(dTotal);
                out.print(s);
                %> 
                <%=u.get("currency")%>
            </div>
        </td>
		<%
			BigDecimal oStatus = u.getBigDecimal("status_number");
			if (oStatus != null) {
				if (oStatus.compareTo(Tpv.PAGADO) != 0 && oStatus.compareTo(Tpv.PENDIENTE_CONFIRMACION) != 0) {%>
        <td class="tablerow<%=r%>">
			<div style="padding:6px">
                <%=StripesResources.getString("TPV.STATUS" + oStatus.intValue())%>
			</div>
		</td>		
        <td class="tablerow<%=r%>">
			<div style="padding:6px">
				<input type="image" class="login" src="img/dustbin16.gif" onclick="deleteCredit('<%=u.getId()%>')"/>
			</div>
		</td>								 
		<% } else {%>
        <td class="tablerow<%=r%>">
			<div style="padding:6px">
                <%=StripesResources.getString("TPV.STATUS" + oStatus.intValue())%>
			</div>
		</td>			
		<%	}
			}%>
    </tr>
    <%  n++; }%>
  </table>
</div>
