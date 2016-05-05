<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="/inc/default.jsp" pageTitle="Inicio">
  <stripes:layout-component name="contents">
  		<jsp:include page="/inc/darkmenu.jspf"/>
			<div class="xsnazzy"><b class="xtop"><b class="xb1"></b><b class="xb2g"></b><b class="xb3g"></b><b class="xb4g"></b></b>
      <div id="welcome" class="yui-g">
        <div style="float:left;width:420px">
        <br/>
        <h1>Bienvenido a zesped</h1>
        <br/><br/>
        <span class="claim">El lugar seguro y accesible donde procesar y gestionar tu documentaci&oacute;n contable y tributaria</span>
        <div style="position:relative;top:50px;left:50px">
          <% if (session.getAttribute("nickname")==null) { %>
          <a href="SignUpForm.action"><img src="img/btn-registrate.png" alt="Registrate" width="280" height="97" border="0" /></a>
          <% } else { %>
          <a href="CaptureInvoice.action"><img src="img/btn-entra.png" alt="Entra" width="280" height="97" border="0" /></a>
          <% } %>
			  </div>
        </div>
        <div id="video" style="position:relative;float:right;overflow:hidden;width:439px;height:323px;background-image:url('img/video-home.jpg')" alt="">
          <div id="ourservices" style="position:relative;left:20px;top:325px"><h1>Nuestros Servicios</h1></div>
          <div id="firstclaim" style="height:320px">
          <div id="invoices" style="position:relative;left:440px;top:50px"><h1><font color="#92d050">facturas</font></h1></div>
          <div id="certifiedclaim" style="position:relative;left:20px;top:70px;opacity:0;visibility:hidden"><h2><font color="#c3d69b">Digitalizaci&oacute;n certificada<br/>y procesamiento de facturas</font></h2></div>
          <div id="certifiedtext" style="width:439px;position:relative;left:0px;top:90px;opacity:0;visibility:hidden;background-color:#4f6228;padding:8px 8px 8px 8px"><font color="#c3d69b">
          Te ayudamos a capturar los datos de tus facturas para introducirlos<br/>en tu contabilidad.<br/>Adem&aacute;s mantenemos en la nube una imagen digitalizada con el<br/>mismo valor que el original.</font></div>
					</div> <!-- firstclaim -->
          <div id="secondclaim" style="position:relative;top:-320px">
          <div id="expensesmanagement" style="position:relative;left:440px;top:50px"><h1><font color="#92d050">gesti&oacute;n de gastos</font></h1></div>
          <div id="expensesclaim" style="position:relative;left:20px;top:70px;opacity:0;visibility:hidden"><h2><font color="#c3d69b">Digitalizaci&oacute;n Certificada y Liquidaci&oacute;n de Gastos</font></h2></div>
          <div id="expensestext" style="width:439px;position:relative;left:0px;top:90px;opacity:0;visibility:hidden;background-color:#4f6228;padding:8px 8px 8px 8px"><font color="#c3d69b">
          Te ayudamos a capturar los datos de tus justificantes de gastos y generar liquidaciones para tu gesti&oacute;n contable.
					<br/>Tambi&eacute;n dispondr&aacute;s en la nube de las im&aacute;genes de los documentos.</font></div>
					</div> <!-- secondclaim -->
        </div> <!-- video -->
      </div>
      <script type="text/javascript">
      YUI().use("anim", function (Y) {
    	  Y.on("domready", function (e) {
    		      		  
    		  if (navigator.userAgent.toLowerCase().indexOf('chrome')>-1)
    			  document.getElementById("welcome").style.width="866px";

          var a1 = new Y.Anim({node:'#ourservices', duration:3, easing: Y.Easing.easeIn, to: { top:20 } });
          var a2 = new Y.Anim({node:'#invoices', duration:2, easing: Y.Easing.easeIn, to: { left:20 } });
          var a3 = new Y.Anim({node:'#certifiedclaim', duration:2, to: { opacity:1 } });
          var a4 = new Y.Anim({node:'#certifiedtext', duration:2, to: { opacity:1 } });
          var a5 = new Y.Anim({node:'#firstclaim', duration:1, to: { opacity:0 } });
          var a6 = new Y.Anim({node:'#expensesmanagement', duration:2, easing: Y.Easing.easeIn, to: { left:20 } });
          var a7 = new Y.Anim({node:'#expensesclaim', duration:2, to: { opacity:1 } });
          var a8 = new Y.Anim({node:'#expensestext', duration:2, to: { opacity:1 } });
          var a9 = new Y.Anim({node:'#secondclaim', duration:1, to: { opacity:0 } });
					var i1, i2;

          a1.on("end", function (e) { a2.run(); } );
          a2.on("end", function (e) { document.getElementById("certifiedclaim").style.visibility="visible"; a3.run(); } );
          a3.on("end", function (e) { document.getElementById("certifiedtext").style.visibility="visible"; a4.run(); } );
          a4.on("end", function (e) { i1 = setInterval(function() { a5.run(); document.getElementById("invoices").style.visibility=document.getElementById("certifiedtext").style.visibility=document.getElementById("certifiedclaim").style.visibility="hidden"; clearInterval(i1); }, 4000); } );
          a5.on("end", function (e) { a6.run(); } );
          a6.on("end", function (e) { document.getElementById("expensesclaim").style.visibility="visible"; a7.run(); } );
      	  a7.on("end", function (e) { document.getElementById("expensestext").style.visibility="visible"; a8.run(); } );
          a8.on("end", function (e) { i2 = setInterval(function() { a9.run(); document.getElementById("expensesmanagement").style.visibility=document.getElementById("expensestext").style.visibility=document.getElementById("expensesclaim").style.visibility="hidden"; clearInterval(i1); }, 4000); } );
      	  a9.on("end", function (e) { document.getElementById("ourservices").style.display="none"; } );
          a1.run();
          
    	  });
      });
      </script>
      <div class="yui3-g">
        <div class="yui3-u-1-4 slideshow"><div class="slidebox">
          <img src="img/btn-ico-1.jpg" width="102" height="102" /><br/><br/>
          <span class="allcaps">Digitalizaci&oacute;n Certificada</span>
          <br/>
          <!-- <img src="img/logo-aeat.jpg" align="right" alt="AEAT"/> -->
				  &hellip;de tus documentos de naturaleza tributaria homologada por la AEAT y las instituciones tributarias del Pa&iacute;s Vasco y Navarra.
        </div><!-- slidebox --></div> <!-- slideshow -->
        <div class="yui3-u-1-4 slideshow"><div class="slidebox">
          <img src="img/btn-ico-3.jpg" width="102" height="102" /><br/><br/>
				  <span class="allcaps">Captura de datos</span>
          <br/>
          Servicio ON-LINE de captura  de datos con personal especializado para que no tengas que pulsar una sola tecla .
          O Si lo prefieres, utiliza nuestras utilidades para la grabaci&oacute;n sencilla y fiable de datos.
				</div><!-- slidebox --></div><!-- slideshow -->
        <div class="yui3-u-1-4 slideshow"><div class="slidebox">
          <img src="img/btn-ico-2.jpg" width="102" height="102" /><br/><br/>
				  <span class="allcaps">Valor legal</span>
          <br/>
				  &hellip;de las im&aacute;genes de tus facturas y documentos sustitutivos conforme a la reglamentaci&oacute;n vigente que les otorga el mismo valor que los documentos f&iacute;sicos originales.
				</div><!-- slidebox --></div><!-- slideshow -->
        <div class="yui3-u-1-4 slideshow"><div class="slidebox">
          <img src="img/btn-ico-4.jpg" width="102" height="102" /><br/><br/>
				  <span class="allcaps">Gesti&oacute;n protegida</span>
				  <br/>
				  &hellip;de tus facturas y documentaci&oacute;n tributaria durante el tiempo que exija la legislaci&oacute;n contractual. Herramientas para facilitar el intercambio de archivos contables con tus programas de contabilidad.
			  </div><!-- slidebox --></div><!-- slideshow -->
      </div>
			<b class="xbottom"><b class="xb4w"></b><b class="xb3w"></b><b class="xb2w"></b><b class="xb1"></b></b></div>
  </stripes:layout-component>
</stripes:layout-render>