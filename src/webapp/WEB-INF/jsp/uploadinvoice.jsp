<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><stripes:layout-render name="/inc/apppage.jsp" pageTitle="Factura Digitalizada">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>

			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox"><span style="padding:8px"><big>Factura Digitalizada</big></span></div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
			
			 <form>
			 <div class="yui3-g">
	 		   <div class="yui3-u-7-12">
			     <img src="image.jsp?i=${actionBean.capturedPage1}" width="512" />  
			   </div>
			   <div class="yui3-u-5-12">

			     <b class="spiffy"><b class="spiffy1"><b></b></b><b class="spiffy2"><b></b></b><b class="spiffy3"></b><b class="spiffy4"></b><b class="spiffy5"></b></b>
			     <div class="textbox">
			       <h2>Resultado de la Digitalizaci&oacute;n</h2>
        		 <stripes:messages/>
             <stripes:errors/>
						 <table class="formfields">
						   <tr><td class="formlabeloptional">Fecha de Proceso:</td><td><%=new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date())%></td></tr>
						   <tr><td class="formlabeloptional">Docs. Digitalizados:</td><td>${actionBean.capturedCount}</td></tr>
						   <tr>
						     <td class="formlabeloptional">Tipo de servicio:</td>
						     <td>${actionBean.serviceFlavor=='basic' ? 'B&aacute;sico' : 'Premium'}</td>
						   </tr>						   
						 </table>
    			 </div>
      		 <b class="spiffy"><b class="spiffy5"></b><b class="spiffy4"></b><b class="spiffy3"></b><b class="spiffy2"><b></b></b><b class="spiffy1"><b></b></b></b>
      		 
      		 <div class="outerbox"></div>
      		 
			     <input type="button" class="submit" value="Digitalizar otra factura" onclick="document.location='CaptureInvoice.action'" />
			   </div> <!-- yui3-u-5-12 -->
			 </div> <!-- yui3-g -->
			 </form>
  </stripes:layout-component>
</stripes:layout-render>