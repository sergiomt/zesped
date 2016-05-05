<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" session="true" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<stripes:layout-render name="/inc/tabview.jsp" pageTitle="Mi Cuenta">
  <stripes:layout-component name="contents">
			<jsp:include page="/inc/navigationmenu.jsp"/>
			<div class="xsnazzy0"><b class="xtop"><b class="xb1"></b><b class="xb2d"></b><b class="xb3d"></b><b class="xb4d"></b></b>
			<div class="darkbox"><span style="padding:8px"><big>Mi Cuenta</big></span></div>
			<b class="xbottom"><b class="xb4d"></b><b class="xb3d"></b><b class="xb2d"></b><b class="xb1"></b></b></div>
	  <c:choose>
		<c:when test="${actionBean.role=='admin'}">
	    <div id="tabs" class="yui3-skin-night">
	    <ul>
	        <li><a id="acccredits" href="#creditos">Informaci&oacute;n</a></li>
	        <li><a id="acctaxpayers" href="#empresas">Empresas</a></li>
	        <li><a id="accusers" href="#usuarios">Usuarios</a></li>
	        <li><a id="accsuppliers" href="#proveedores">Proveedores</a></li>
	        <li><a id="accpurchasing" href="#contratacion">Contrataci&oacute;n</a></li>
	        <li><a id="accinfo" href="#informacion">Configuraci&oacute;n</a></li>
	        <li><a id="accnotifications" href="#notificaciones">Notificaciones</a></li>
	    </ul>
	    <div>
	        <div id="creditos"></div>
	        <div id="empresas"></div>
	        <div id="usuarios"></div>
	        <div id="proveedores"></div>
	        <div id="contratacion"></div>
	        <div id="informacion"></div>
	        <div id="notificaciones"><h2>Notificaciones</h2></div>
	    </div>
	    </div>
    </c:when>
    <c:otherwise>
	    <div id="tabs" class="yui3-skin-night">
	    <ul>
	        <li><a id="accsuppliers" href="#proveedores">Proveedores</a></li>
	        <li><a id="accinfo" href="#informacion">Configuraci&oacute;n</a></li>
	        <li><a id="accnotifications" href="#notificaciones">Notificaciones</a></li>
	    </ul>
	    <div>
	        <div id="proveedores"></div>
	        <div id="informacion"></div>
	        <div id="notificaciones"><h2>Notificaciones</h2></div>
	    </div>
	    </div>
    </c:otherwise>
    </c:choose>    
  <script type="text/javascript">
  YUI().use('tabview', 'event', 'io', function(Y) {
	
    tabview = new Y.TabView({
        srcNode: '#tabs'
    });
    tabview.render();
    
    /*
    document.getElementById("empresas").innerHTML = "<br/><br/><br/><br/><center><div id='empresasWorkDiv'></div></center><br/><br/><br/><br/>";
		spinner.spin(document.getElementById("empresasWorkDiv"));

    Y.io("ListTaxPayers.action", { method:'GET', on: {'complete':function (i, o, a) {
    	spinner.stop();
			document.getElementById("empresas").innerHTML = o.responseText; } } });
    */

    Y.one("#accsuppliers").on('click', function(e) {
    	if (httpRequestText("sessionping.jsp")=="1") {
    	  document.getElementById("proveedores").innerHTML = "<br/><br/><br/><br/><center><div id='proveedoresWorkDiv'></div></center><br/><br/><br/><br/>";
   		  
    	  Y.io("cacheread.jsp?key=<%=session.getAttribute("customer_account_docid")+"accsuppliers"%>",
    			  { method:'GET', on: {'complete':function (i, p, a) {
     	    	  var htm = p.responseText;
     	    	  if (htm.length==0) {
     	       	  spinner.spin(document.getElementById("proveedoresWorkDiv"));
     	       	  Y.io("ListClients.action", { method:'GET', on: {'complete':function (i, o, a) {
     	    	      spinner.stop();
     	    	      htm = o.responseText;
     	    			  document.getElementById("proveedores").innerHTML = htm;
     	    			  document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"accsuppliers"%>";
     	    			  document.forms["cache"].val.value = htm;
     	    	      Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
     	    	    } } });     	    		
     	    	  } else {
       			    document.getElementById("proveedores").innerHTML = htm;     	    		
     	    	  }
     			  } } });   		  
   		  
      } else {
      	document.location = "error.jsp?e=expiredsession";
      }    
    } );

    Y.one("#accinfo").on('click', function(e) {
    	if (httpRequestText("sessionping.jsp")=="1") {
   		  Y.io("EditConfig.action", { method:'GET', on: {'complete':function (i, o, a) {
   			  document.getElementById("informacion").innerHTML = o.responseText; } } });    	
      } else {
      	document.location = "error.jsp?e=expiredsession";
      }    
    } );

    Y.one("#accnotifications").on('click', function(e) {
    	if (httpRequestText("sessionping.jsp")=="1") {
        document.getElementById("notificaciones").innerHTML = "<br/><br/><br/><br/><center><div id='notificacionesWorkDiv'></div></center><br/><br/><br/><br/>";
        spinner.spin(document.getElementById("notificacionesWorkDiv"));
   		  Y.io("ListMessages.action?f=2&o=0", { method:'GET', on: {'complete':function (i, o, a) {
       	  spinner.stop();
   			  document.getElementById("notificaciones").innerHTML = o.responseText;
   			  document.getElementById("faketab2").className="faketabhighlight";
   		  } } });    	
      } else {
      	document.location = "error.jsp?e=expiredsession";
      }    
    } );

    Y.one("#acccredits").on('click', function(e) {
    	if (httpRequestText("sessionping.jsp")=="1") {
    	  document.getElementById("creditos").innerHTML = "<br/><br/><br/><br/><center><div id='creditosWorkDiv'></div></center><br/><br/><br/><br/>";
    	  spinner.spin(document.getElementById("creditosWorkDiv"));
   		  Y.io("ShowCredits.action", { method:'GET', on: {'complete':function (i, o, a) {
   	    	spinner.stop();
   			  document.getElementById("creditos").innerHTML = o.responseText; } } });    	
      } else {
      	document.location = "error.jsp?e=expiredsession";
      }    
    } );
    
    Y.one("#accusers").on('click', function(e) {
    	if (httpRequestText("sessionping.jsp")=="1") {
      	document.getElementById("usuarios").innerHTML = "<br/><br/><br/><br/><center><div id='usuariosWorkDiv'></div></center><br/><br/><br/><br/>";

  	  Y.io("cacheread.jsp?key=<%=session.getAttribute("customer_account_docid")+"accusers"%>",
			  { method:'GET', on: {'complete':function (i, p, a) {
 	    	  var htm = p.responseText;
 	    	  if (htm.length==0) {
 	    	    spinner.spin(document.getElementById("usuariosWorkDiv"));
 	    	    Y.io("ListUsers.action", { method:'GET', on: {'complete':function (i, o, a) {
 	    	      spinner.stop();
 	    	      htm = o.responseText;
 	    			  document.getElementById("usuarios").innerHTML = htm;
 	    			  document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"accusers"%>";
 	    			  document.forms["cache"].val.value = htm;
 	    	      Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
 	    	    } } });     	    		
 	    	  } else {
   			    document.getElementById("usuarios").innerHTML = htm;     	    		
 	    	  }
 			  } } });   		  
    	
    	} else {
    	  document.location = "error.jsp?e=expiredsession";
      }
    }	);

    Y.one("#acctaxpayers").on('click', function(e) {
    	if (httpRequestText("sessionping.jsp")=="1") {
        document.getElementById("empresas").innerHTML = "<br/><br/><br/><br/><center><div id='empresasWorkDiv'></div></center><br/><br/><br/><br/>";

    	  Y.io("cacheread.jsp?key=<%=session.getAttribute("customer_account_docid")+"acctaxpayers"%>",
    			  { method:'GET', on: {'complete':function (i, p, a) {
     	    	  var htm = p.responseText;
     	    	  if (htm.length==0) {
     	    	    spinner.spin(document.getElementById("empresasWorkDiv"));
     	    		  Y.io("ListTaxPayers.action", { method:'GET', on: {'complete':function (i, o, a) {
     	    	      spinner.stop();
     	    	      htm = o.responseText;
     	    			  document.getElementById("empresas").innerHTML = htm;
     	    			  document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"acctaxpayers"%>";
     	    			  document.forms["cache"].val.value = htm;
     	    	      Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
     	    	    } } });     	    		
     	    	  } else {
       			    document.getElementById("empresas").innerHTML = htm;     	    		
     	    	  }
     			  } } });   		  

    	} else {
      	document.location = "error.jsp?e=expiredsession";
      }    
    } );

    Y.one("#accpurchasing").on('click', function(e) {
        document.getElementById("contratacion").innerHTML = "<br/><br/><br/><br/><center><div id='empresasWorkDiv'></div></center><br/><br/><br/><br/>";
        spinner.spin(document.getElementById("empresasWorkDiv"));
        if (httpRequestText("sessionping.jsp")=="1") {
            Y.io("EngageCredit.action", { method:'GET', on: {'complete':function (i, o, a) {
                        spinner.stop();
                        document.getElementById("contratacion").innerHTML = o.responseText;
                        } } });    	
        } else {
            document.location = "error.jsp?e=expiredsession";
        }    
    } );    
  });

  function sendPurchasing(control) {
      document.getElementById("load").style.display="block";
      document.getElementById("dopay").style.display="none";            
      YUI().use('io', function(Y) {
          var form = control.form;
          Y.io(form.action, { method:'POST', 
              form: {id: form},
              on: {'complete':function (i, o, a) {
                      document.getElementById("contratacion").innerHTML = o.responseText;
                      javascript:scroll(0,0);
                  } } }
      )});                 
  }
     
  function updateCountry(control) {
      YUI().use('io', function(Y) {
          var form = control.DOMelem_input.form;
          Y.io(form.action, { method:'POST',
              data: '_eventName=states',
              form: {id: form},
              on: {'complete':function (i, o, a) {
                      document.getElementById("state").innerHTML = o.responseText;
                      dhtmlXComboFromSelect("state").enableFilteringMode(true);
                  } } }
      )});           
  }   
  
  function updateState(control) {
      YUI().use('io', function(Y) {
          var form = control.DOMelem_input.form;
          Y.io(form.action, { method:'POST',
              data: '_eventName=cities',
              form: {id: form},
              on: {'complete':function (i, o, a) {
                      document.getElementById("city").innerHTML = o.responseText;
                      dhtmlXComboFromSelect("city").enableFilteringMode(true);
                  } } }
      )});           
  }
    
  function payPurchasing(pay){
      document.getElementById("endpay").style.display="none";
      document.getElementById("load").style.display="block";
      if (pay=="true"){
          var order_id = document.getElementById("order_id").value;
          var peticion = document.getElementById("peticion").value;
          var cardnumber = document.getElementById("cardnumber").value;
          var cardholder = document.getElementById("cardholder").value;
          var expiration_month_card = document.getElementById("expiration_month_card").value;
          var expiration_year_card = document.getElementById("expiration_year_card").value;
          var cvv2 = document.getElementById("cvv2").value;
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
          document.getElementById("payPurchasing").submit();
      }
     
  }

  function showPay(item){
      if (item.value!="FREETRIAL2020") {
          document.getElementById("pay").style.display="block";
      } else {
          document.getElementById("pay").style.display="none";
      }
  }
  
  </script>
  </stripes:layout-component>
</stripes:layout-render>