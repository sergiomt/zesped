<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@ include file="atril.jspf" %>
<stripes:layout-definition>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Zesped :: ${pageTitle}</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/img/Z16.ico" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-cssreset-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/yui-grids-min-351.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-ubuntu.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fonts-audiowide.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp?f=tabview">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.jsp?f=protomultiselect">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/assets/skins/sam/gallery-lightbox-skin.css" type="text/css" />    
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-config.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/yui-min-351.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/protoculous-effects-shrinkvars.js" charset="utf-8"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/textboxlist.js" charset="utf-8"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/protomultiselect.js" charset="utf-8"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/gallery-divbox.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/autosuggest20.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/spin.js"></script>
    <script type="text/javascript">
  	  var spinner = new Spinner({
			  lines: 13, // The number of lines to draw
			  length: 7, // The length of each line
			  width: 4, // The line thickness
			  radius: 10, // The radius of the inner circle
			  corners: 1, // Corner roundness (0..1)
			  rotate: 0, // The rotation offset
			  color: '#000', // #rgb or #rrggbb
			  speed: 1, // Rounds per second
			  trail: 60, // Afterglow percentage
			  shadow: false, // Whether to render a shadow
			  hwaccel: false, // Whether to use hardware acceleration
			  className: 'spinner', // The CSS class to assign to the spinner
			  zIndex: 2e9, // The z-index (defaults to 2000000000)
			  top: 'auto', // Top position relative to parent in px
			  left: 'auto' // Left position relative to parent in px
		  });
    </script>  
	</head>
  <body bgcolor="#6f6f6f">
    <stripes:layout-component name="header">
      <jsp:include page="/inc/header.jsp"/>
    </stripes:layout-component>

    <div id="contents" class="centeredbox">
			<stripes:layout-component name="contents"/>
    </div>

    <stripes:layout-component name="footer">
      <jsp:include page="/inc/footer.jsp"/>
    </stripes:layout-component>
    
    <form name="cache">
      <input type="hidden" name="key" />
      <input type="hidden" name="val" />
    </form>
  </body>
  <script type="text/javascript" src="js/email.js" defer="defer"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/xslt.js" defer="defer"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/lightbox.js" defer="defer"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/combobox.js" defer="defer"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/panel.js" defer="defer"></script>
  <script type="text/javascript" defer="defer">
		  var DivBoxInstance;
		  var tabview;

		  YUI().use("gallery-divbox", function (Y) {
			  
			  DivBoxInstance = Y.Lightbox.init();

		  });
		  
		  function onLoadLightbox() {
		    	if (document.getElementById("permittedTaxPayersDiv"))
		    		var fbl = new FacebookList('facebook-list', 'facebook-auto', {fetchFile:'taxpayers_json.jsp'});			  
		    	if (document.getElementById("invitebox")) {
		    		YUI().use("node","io-form", "panel", function (Y) {
		    			 var lookupEmailComplete = function (id, o, args) {
		    				 if (o.responseText.lenght>0) {
			    				 var txt = o.responseText.split("`");
				    			 var frm = document.forms["userData"];
				    			 var tln = txt[5].length;
				    			 if (tln>0) {
					    			 var i = txt[5].charAt(tln.length-1);
					    			 frm.elements["firstName["+i+"]"].value = txt[3];
					    			 frm.elements["lastName["+i+"]"].value = txt[4];		    				   
				    			 } // fi	    					 
		    				 } // fi
		    			 };
		    			 if (document.forms["userData"].taxPayer.value.length>0) {
		 		    		 document.forms["userData"].employee.checked=true;
			    			 var blurClick = function (e) {
				 			       Y.io("lookupemail.jsp?email="+this.get("value")+"&args="+this.get("id"), { method:'GET', on: {'complete':lookupEmailComplete} });
				    			 };
				    			 for (var i=0; i<=6; i++)
				    				 Y.on("blur", blurClick, "#email"+String(i));		    				 
		    			 } else {
		 			    	 Y.one('#alertbox .message').setHTML('Debe seleccionar una empresa a la que invitar');
					    	 alertbox.show();
					    	 DivBoxInstance.end();
		    			 }
		    			 setCheckedValue(document.forms["userData"].employee,"1");
		    		 });
		    	}
		  }
		  
		  function onCloseLightbox() {
			  var idx = tabview.indexOf(tabview.get('selection'));
			  if (httpRequestText("sessionping.jsp")=="1") {
				  if (document.getElementById("listusers") && idx==1)
				   		document.getElementById("usuarios").innerHTML = httpRequestText("ListUsers.action");	
				  if (document.getElementById("listtaxpayers") && !document.getElementById("invitebox") && idx==2)
				   		document.getElementById("empresas").innerHTML = httpRequestText("ListTaxPayers.action");	
				  if (document.getElementById("listemployees") && idx==2)
				   		document.getElementById("empresas").innerHTML = httpRequestText("ListEmployees.action");	
				  if (document.getElementById("listclients") && idx==3)
				   		document.getElementById("proveedores").innerHTML = httpRequestText("ListClients.action");				  
			  } else {
			      document.location = "error.jsp?e=expiredsession";
			  }
		  }
		  
		  function fakeCheckboxClick() {
			  for (var f in document.forms) {
				  var frm = document.forms[f];
				  for (var e in frm.elements) {
					  var elm = frm.elements[e];
					  if (elm.type=="checkbox")
						  if (elm.name.length>13)
							  if (elm.name.substr(0,13)=="checkedValues")
								  elm.checked = !elm.checked;
				  }
			  }
		  }

		  var selectedTaxPayers = "";

		  function changeSelectedTaxPayers() {
			  var frm = document.forms["listtaxpayers"];
			  selectedTaxPayers = "";
			  for (var e in frm.elements) {
				  var elm = frm.elements[e];
				  if (elm.type=="checkbox")
				    if (elm.checked)
				    	selectedTaxPayers += (selectedTaxPayers.length==0 ? "" : ",") + elm.value;
			  } // next
			  document.getElementById("invite").href = "InviteUser.action?taxpayer="+selectedTaxPayers;
		  }

		  var selectedClients = "";

		  function changeSelectedClients() {
			  var frm = document.forms["listclients"];
			  selectedClients = "";
			  for (var e in frm.elements) {
				  var elm = frm.elements[e];
				  if (elm.type=="checkbox")
				    if (elm.checked)
				    	selectedClients += (selectedClients.length==0 ? "" : ",") + elm.value;
			  } // next
		  }
	  
		  YUI().use("io-form", "panel", function(Y) {
	
			  function deleteTaxPayersComplete(id, o, args) {
					var xml = o.responseXML;
					if (xml==null) {
					  document.location = "error.jsp?e=expiredsession";
					} else {
						var rsp = xml.documentElement;
						var ers = rsp.getElementsByTagName("errors")[0];
						var erc = Number(ers.attributes.item("count").value);
						if (erc>0) {
							spinner.stop();
							var cde = ers.getElementsByTagName("error")[0].attributes.item("code").value;
							if (cde.substr(0,6)=="error.") {
								document.location = "enter.jsp?e="+cde;            	
							} else {
					    	Y.one('#alertbox .message').setHTML('Se produjo un error durante la eliminaci&oacute;n de empresas');
					    	Y.one('#alertbox .message').set('className', 'message dialog-stop');
					    	alertbox.show();
							}
						} else {
		     	    Y.io("cachedelete.jsp?key=<%=session.getAttribute("customer_account_docid")+"acctaxpayers"%>", { method:'GET' });    	
							var inh = httpRequestText("ListTaxPayers.action");
							spinner.stop();
							emp.innerHTML = inh;
			    		document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"acctaxpayers"%>";
			    	  document.forms["cache"].val.value = inh;
			    	  Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
						}
					}
				};
				
				YUI.zesped.deleteSelectedTaxPayers = function () {
				  if (selectedTaxPayers.length==0) {
			    	Y.one('#alertbox .message').setHTML('Debe seleccionar al menos una empresa a eliminar');
			    	alertbox.show();	    
				  } else {
					  if (httpRequestText("sessionping.jsp")=='1') {
					    Y.one('#confirmbox .message').setHTML('&iquest;Est&aacute; seguro de que desea eliminar las empresas seleccionadas y todos sus documentos?');
					    confirmbox.callback = function() {
							  var emp = document.getElementById("empresas");
							  emp.innerHTML = "<br/><br/><br/><br/><center><div id='empresasWorkDiv'></div></center><br/><br/><br/><br/>";
							  spinner.spin(document.getElementById("empresasWorkDiv"));					    	
					    	Y.io("DeleteDocuments.action?type=TaxPayer&docs="+selectedTaxPayers,
					    			{ method:'GET', on: {'complete':deleteTaxPayersComplete} });
					      };
					    confirmbox.show();
						} else {
							document.location = "error.jsp?e=expiredsession";
						}
				  } // fi
			  }; // deleteSelectedTaxPayers

				YUI.zesped.refreshTaxPayers = function () {
			    if (httpRequestText("sessionping.jsp")=="1") {
			      document.getElementById("empresas").innerHTML = "<br/><br/><br/><br/><center><div id='empresasWorkDiv'></div></center><br/><br/><br/><br/>";
 	    	    Y.io("cachedelete.jsp?key=<%=session.getAttribute("customer_account_docid")+"acctaxpayers"%>", { method:'GET' });
 	    	    spinner.spin(document.getElementById("empresasWorkDiv"));
	    		  Y.io("ListTaxPayers.action", { method:'GET', on: {'complete':function (i, o, a) {
	    	      spinner.stop();
	    	      htm = o.responseText;
	    			  document.getElementById("empresas").innerHTML = htm;
	    			  document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"acctaxpayers"%>";
	    			  document.forms["cache"].val.value = htm;
	    	      Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
	    		  } } } );
			    } else {
			      document.location = "error.jsp?e=expiredsession";
			    }    
				};
			  
				YUI.zesped.listUsersForTaxPayer = function (id) {
				  if (httpRequestText('sessionping.jsp')=='1') {
				    document.getElementById("usuarios").innerHTML = "<br/><br/><br/><br/><center><div id='usuariosWorkDiv'></div></center><br/><br/><br/><br/>";
					  tabview.selectChild(2);
				    spinner.spin(document.getElementById("usuariosWorkDiv"));
	 	    	    Y.io("ListUsers.action?taxpayer="+id, { method:'GET', on: {'complete':function (i, o, a) {
	 	    	      spinner.stop();
	 	    	      htm = o.responseText;
	 	    			  document.getElementById("usuarios").innerHTML = htm;
	 	    	    } } });	 	    	    
					} else {
						document.location='error.jsp?e=expiredsession';
				  }
				};

				YUI.zesped.refreshUsers = function () {
				    if (httpRequestText("sessionping.jsp")=="1") {
					    document.getElementById("usuarios").innerHTML = "<br/><br/><br/><br/><center><div id='usuariosWorkDiv'></div></center><br/><br/><br/><br/>";
	 	    	    Y.io("cachedelete.jsp?key=<%=session.getAttribute("customer_account_docid")+"accusers"%>", { method:'GET' });
	 	    	    spinner.spin(document.getElementById("usuariosWorkDiv"));
	 	    	    Y.io("ListUsers.action", { method:'GET', on: {'complete':function (i, o, a) {
		    	      spinner.stop();
		    	      htm = o.responseText;
		    			  document.getElementById("usuarios").innerHTML = htm;
		    			  document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"accusers"%>";
		    			  document.forms["cache"].val.value = htm;
		    	      Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
		    		  } } } );
				    } else {
				      document.location = "error.jsp?e=expiredsession";
				    }    
				};

				YUI.zesped.refreshClients = function () {
				    if (httpRequestText("sessionping.jsp")=="1") {
				    	  document.getElementById("proveedores").innerHTML = "<br/><br/><br/><br/><center><div id='proveedoresWorkDiv'></div></center><br/><br/><br/><br/>";
	 	    	    Y.io("cachedelete.jsp?key=<%=session.getAttribute("customer_account_docid")+"accsuppliers"%>", { method:'GET' });
	 	    	    spinner.spin(document.getElementById("proveedoresWorkDiv"));
	     	      Y.io("ListClients.action", { method:'GET', on: {'complete':function (i, o, a) {
		    	      spinner.stop();
		    	      htm = o.responseText;
		    			  document.getElementById("proveedores").innerHTML = htm;
		    			  document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"accsuppliers"%>";
		    			  document.forms["cache"].val.value = htm;
		    	      Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
		    		  } } } );
				    } else {
				      document.location = "error.jsp?e=expiredsession";
				    }    
				};

				function deleteClientsComplete(id, o, args) {
						var xml = o.responseXML;
						if (xml==null) {
						  document.location = "error.jsp?e=expiredsession";
						} else {
							var rsp = xml.documentElement;
							var ers = rsp.getElementsByTagName("errors")[0];
							var erc = Number(ers.attributes.item("count").value);
							if (erc>0) {
								spinner.stop();
								var cde = ers.getElementsByTagName("error")[0].attributes.item("code").value;
								if (cde.substr(0,6)=="error.") {
									document.location = "enter.jsp?e="+cde;            	
								} else {
						    	Y.one('#alertbox .message').setHTML('Se produjo un error durante la eliminaci&oacute;n de clientes');
						    	Y.one('#alertbox .message').set('className', 'message dialog-stop');
						    	alertbox.show();
								}
							} else {
			     	    Y.io("cachedelete.jsp?key=<%=session.getAttribute("customer_account_docid")+"accsuppliers"%>", { method:'GET' });    	
								var inh = httpRequestText("ListClients.action");
								spinner.stop();
								document.getElementById("proveedores").innerHTML = inh;
				    		document.forms["cache"].key.value = "<%=session.getAttribute("customer_account_docid")+"accsuppliers"%>";
				    	  document.forms["cache"].val.value = inh;
				    	  Y.io("cachestore.jsp", { method:'POST', form:{id:document.forms['cache']} });    	
							}
						}
			  };
				
				YUI.zesped.deleteSelectedClients = function () {
					  if (selectedClients.length==0) {
				    	Y.one('#alertbox .message').setHTML('Debe seleccionar al menos un proveedor a eliminar');
				    	alertbox.show();
					  } else {
						  if (httpRequestText("sessionping.jsp")=='1') {
						    Y.one('#confirmbox .message').setHTML('&iquest;Est&aacute; seguro de que desea eliminar los proveedores seleccionados?');
						    confirmbox.callback = function() {
								  var emp = document.getElementById("proveedores");
								  emp.innerHTML = "<br/><br/><br/><br/><center><div id='proveedoresWorkDiv'></div></center><br/><br/><br/><br/>";
								  spinner.spin(document.getElementById("proveedoresWorkDiv"));					    	
						    	var clients = selectedClients.split(",");
						    	for (var c=0; c<clients.length; c++) {
						    		var xml = httpRequestXML("CheckCanDeleteClient.action?id="+clients[c]);
						    		var rsp = xml.documentElement;
									  var ers = rsp.getElementsByTagName("errors")[0];
									  var erc = Number(ers.attributes.item("count").value);
									  if (erc>0) {
										  spinner.stop();
									    Y.one('#alertbox .message').setHTML(getElementValue(ers.getElementsByTagName("error")[0]));
									    alertbox.show();
									    YUI.zesped.refreshClients();
									    return;
									  }
						    	} // next
								  Y.io("DeleteDocuments.action?type=Client&docs="+selectedClients,
						    			{ method:'GET', on: {'complete':deleteClientsComplete} });
						    };
						    confirmbox.show();
							} else {
								document.location = "error.jsp?e=expiredsession";
							}
					  } // fi
				  }; // deleteSelectedClients

					YUI.zesped.refreshAccountingAccounts = function (txpr) {
					    if (httpRequestText("sessionping.jsp")=="1") {
					    	  document.getElementById("empresas").innerHTML = "<br/><br/><br/><br/><center><div id='empresasWorkDiv'></div></center><br/><br/><br/><br/>";
		 	    	    spinner.spin(document.getElementById("empresasWorkDiv"));
		     	      Y.io("ListAccounts.action?taxpayer="+txpr, { method:'GET', on: {'complete':function (i, o, a) {
			    	      spinner.stop();
			    	      htm = o.responseText;
			    			  document.getElementById("empresas").innerHTML = htm;
			    		  } } } );
					    } else {
					      document.location = "error.jsp?e=expiredsession";
					    }    
					};
				  
					function deleteAccountingAccountsComplete(id, o, args) {
						var xml = o.responseXML;
						if (xml==null) {
						  document.location = "error.jsp?e=expiredsession";
						} else {
							var rsp = xml.documentElement;
							var ers = rsp.getElementsByTagName("errors")[0];
							var erc = Number(ers.attributes.item("count").value);
							if (erc>0) {
								spinner.stop();
								var cde = ers.getElementsByTagName("error")[0].attributes.item("code").value;
								if (cde.substr(0,6)=="error.") {
									document.location = "enter.jsp?e="+cde;            	
								} else {
						    	Y.one('#alertbox .message').setHTML('Se produjo un error durante la eliminaci&oacute;n de cuentas contables');
						    	Y.one('#alertbox .message').set('className', 'message dialog-stop');
						    	alertbox.show();
								}
							} else {
								var inh = httpRequestText("ListAccounts.action?taxpayer="+args['taxpayer']);
								spinner.stop();
								document.getElementById("empresas").innerHTML = inh;
							}
						}
			    };
			    
				  YUI.zesped.deleteAccountingAccounts = function (txpr) {
					  var chked = new Array();
					  for (var f in document.forms) {
						  var frm = document.forms[f];
						  for (var e in frm.elements) {
							  var elm = frm.elements[e];
							  if (elm.type=="checkbox")
								  if (elm.name.length>13)
									  if (elm.name.substr(0,13)=="checkedValues")
										  if (elm.checked)
											  chked.push(elm.value);
						  }
					  }
					  if (chked.length==0) {
					    	Y.one('#alertbox .message').setHTML('Debe seleccionar al menos una cuenta a eliminar');
					    	alertbox.show();
						  } else {
							  if (httpRequestText("sessionping.jsp")=='1') {
							    Y.one('#confirmbox .message').setHTML('&iquest;Est&aacute; seguro de que desea eliminar las cuentas seleccionadas?');
							    confirmbox.callback = function() {
									  var emp = document.getElementById("empresas");
									  emp.innerHTML = "<br/><br/><br/><br/><center><div id='empresasWorkDiv'></div></center><br/><br/><br/><br/>";
									  spinner.spin(document.getElementById("empresasWorkDiv"));			    	
							    	for (var c=0; c<chked.length; c++) {
							    		var xml = httpRequestXML("CheckCanDeleteAccountingAccount.action?id="+chked[c]);
							    		var rsp = xml.documentElement;
										  var ers = rsp.getElementsByTagName("errors")[0];
										  var erc = Number(ers.attributes.item("count").value);
										  if (erc>0) {
											  spinner.stop();
										    Y.one('#alertbox .message').setHTML(getElementValue(ers.getElementsByTagName("error")[0]));
										    alertbox.show();
										    YUI.zesped.refreshAccountingAccounts(txpr);
										    return;
										  }
							    	} // next
									  Y.io("DeleteDocuments.action?type=AccountingAccount&docs="+chked.join(","),
							    			{ method:'GET', on:{'complete':deleteAccountingAccountsComplete}, arguments:{'taxpayer':txpr} });
							    };
							    confirmbox.show();
								} else {
									document.location = "error.jsp?e=expiredsession";
								}
						  } // fi
				  } ;
				  
		  });

		  
		  function onChangeRole() {
			  var frm=document.forms['userData'];
			  document.getElementById('chk1c').style.display=document.getElementById('chk2c').style.display=document.getElementById('chk3c').style.display=document.getElementById('chk1l').style.display=document.getElementById('chk2l').style.display=document.getElementById('chk3l').style.display=(frm.role.selectedIndex<=0 || frm.role.selectedIndex>1 ? 'none' : 'block');
			  if (frm.role.selectedIndex<=0) {
				  document.getElementById("employeedata").style.display="none";
				  document.getElementById("companyLabel").style.display="none";
				  frm.taxPayer.style.display="none";
				  frm.premium.checked=frm.settle.checked=frm.approve.checked=true;
				  setCombo(document.forms['userData'].permissions,"all");
				  document.getElementById("taxpayersmultiselect").src="blank.htm";
			  } else if (frm.role.selectedIndex==1) {
				    frm.taxPayer.selectedIndex=0;
					  frm.taxPayer.style.display="none";
					  document.getElementById("companyLabel").style.display="none";
				  	document.getElementById('taxpayersmultiselect').src="MultiSelectTaxPayers.action?id="+frm.id.value;
			  } else if (frm.role.selectedIndex==2) {
				  document.getElementById("employeedata").style.display="block";
				  document.getElementById("companyLabel").style.display="block";
				  frm.taxPayer.style.display="block";
				  frm.premium.checked=frm.settle.checked=frm.approve.checked=true;
				  setCombo(document.forms['userData'].permissions,'allow');
				  if (frm.taxPayer.selectedIndex>0)
				  	document.getElementById('taxpayersmultiselect').src="MultiSelectTaxPayers.action?tid="+getCombo(frm.taxPayer);
				  else
					  document.getElementById('taxpayersmultiselect').src="blank.htm";
			  } else if (frm.role.selectedIndex==3) {
				  document.getElementById("employeedata").style.display="none";
				  document.getElementById("companyLabel").style.display="none";
				  frm.taxPayer.style.display="none";
				  frm.premium.checked=frm.settle.checked=frm.approve.checked=false;				  
				  if (frm.taxPayer.selectedIndex>0)
					  	document.getElementById('taxpayersmultiselect').src="MultiSelectTaxPayers.action?tid="+getCombo(frm.taxPayer);
					  else
						  document.getElementById('taxpayersmultiselect').src="blank.htm";
			  }
		  }
		  
		  function onChangeTaxPayer() {
			  var frm = document.forms['userData'];
			  var tms = document.getElementById("taxpayersmultiselect");
			  setCombo(document.forms['userData'].permissions,"allow");
			  if (frm.taxPayer.selectedIndex>0)
			  	tms.src="MultiSelectTaxPayers.action?tid="+getCombo(frm.taxPayer);
			  else
				  tms.src="MultiSelectTaxPayers.action?id="+frm.id.value;				  
		  }

		  var AutoSuggestTaxPayer = new AutoSuggest("faketaxpayerlink", { script:"'autosuggest.jsp?doctype=TaxPayer&text=business_name&'", varname:"business_name",minchars:1,form:0, callback: function (obj) { if (document.getElementById("businessnamespan").innerHTML!=obj.value) { httpRequestText("switchtaxpayer.jsp?id="+obj.id+"&name="+escape(obj.value)); document.location.reload(); } } });
		</script>
</html>
</stripes:layout-definition>