function saveRecordComplete(rsp, div) {
	var cde = getElementAttribute(rsp.getElementsByTagName("errors")[0], "error", "code");
	var erc = Number(getElementAttribute(rsp, "errors", "count"));
	var ret;
	if (erc==0) {
		DivBoxInstance.end();
		ret = getElementValue(rsp.getElementsByTagName("line")[0]);
	} else if (cde.substr(0,6)=="error.") {
		document.location = "enter.jsp?e="+cde;            	
		ret = "";
	} else {
		var hgt = 20+erc*20;
		if (hgt>200) hgt = 200;
		div.style.height=String(hgt)+"px";
		var lin = document.createElement('div'); 
		lin.style.height = "10px"; 
		lin.innerHTML = "<img src='img/spacer.gif' width='1' height='10' alt='' />";
		div.appendChild(lin);
		lin = document.createElement('div'); 
		lin.style.height = "20px"; 
		lin.style.paddingTop = "0.2em"; 
		lin.innerHTML = "<img src='img/spacer.gif' width='1' height='20' alt='' /><b>"+getElementValue(rsp.getElementsByTagName("header")[0])+"</b>";
		div.appendChild(lin);
		var errs = rsp.getElementsByTagName("error");
		for (var e=0; e<errs.length; e++) {
			var lin = document.createElement('div'); 
			lin.style.height = "20px";
			lin.style.paddingTop = "0.2em";
			lin.innerHTML = "<img src='img/spacer.gif' width='1' height='20' alt='' />"+getElementValue(errs[e]);
			div.appendChild(lin);			
		}
		ret = "";
	}
	return ret;
} // saveRecordComplete

function collapseDiv(div) {
	div.innerHTML="";
	div.style.height="0px";				
}

function rejectInvoices() {
	collapseDiv(document.getElementById("rejectionerrors"));
	YUI().use("io-form", function(Y) {
		function rejectInvoicesComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("rejectionerrors"));
			  if (doc.length>0)
				  window.location.reload();
			}
		};
		Y.io("RejectInvoices.action", { method:'POST', form:{id:document.forms['rejectionData']}, on: {'complete':rejectInvoicesComplete} });
	});
};

function saveAccountingAccount() {
	collapseDiv(document.getElementById("saveaccountingaccounterrors"));
	YUI().use("io-form", function(Y) {
		function saveAccountingAccountComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("saveaccountingaccounterrors"));
			  document.getElementById('empresas').innerHTML = httpRequestText('ListAccounts.action?taxpayer='+doc);
			}
		};

		Y.io("SaveAccountingAccount.action", { method:'POST', form:{id:document.forms['accountingAccountData']}, on: {'complete':saveAccountingAccountComplete} });
	});
};

function saveUserConfig() {
	collapseDiv(document.getElementById("saveconfigerrors"));
	document.getElementById("savingconfig").style.display="block";
	document.getElementById("saveconfigbutton").style.display="none";
	YUI().use("io-form", function(Y) {
		function saveUserConfigComplete(id, o, args) {
			document.getElementById("saveconfigbutton").style.display="block";
			document.getElementById("savingconfig").style.display="none";
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("saveconfigerrors"));
			  document.getElementById('informacion').innerHTML = httpRequestText('EditConfig.action');
			}
		};

		Y.io("SaveConfig.action", { method:'POST', form:{id:document.forms['configaccount']}, on: {'complete':saveUserConfigComplete} });
	});
};

function saveClient() {	    	          	    		
	collapseDiv(document.getElementById("saveclienterrors"));
	YUI().use("io-form", function(Y) {
		function saveClientComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("saveclienterrors"));
			  if (doc.length>0)
			    comboPush (document.getElementById("biller"), document.forms['clientData'].elements["client.businessName"].value, doc, true, true);
			}
		};

		Y.io("SaveClient.action", { method:'POST', form:{id:document.forms['clientData']}, on: {'complete':saveClientComplete} });
	});
};

function saveEmployee() {
	collapseDiv(document.getElementById("saveemployeeerrors"));
	YUI().use("io-form", function(Y) {
		function saveEmployeeComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("saveemployeeerrors"));
			  if (doc.length>0)
			    comboPush (document.getElementById("employee"), document.forms['employeeData'].elements["employee.name"].value, doc, true, true);
			}
		};

		Y.io("SaveEmployee.action", { method:'POST', form:{id:document.forms['employeeData']}, on: {'complete':saveEmployeeComplete} });
	});
};

function saveUser() {
	collapseDiv(document.getElementById("saveusererrors"));
	YUI().use("io-form", function(Y) {
		function saveUserComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  document.getElementById("savinguser").style.display="none";
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("saveusererrors"));
			  if (doc.length>0)
				document.getElementById("usuarios").innerHTML = httpRequestText("ListUsers.action");
			  else
			    document.getElementById("saveuserbutton").style.display="block";
			}
		};
		var frm = document.forms['userData'];
		document.getElementById("saveuserbutton").style.display="none";
		document.getElementById("savinguser").style.display="block";
		if (window.frames[0].tlist2) window.frames[0].tlist2.update();
		if (frm.permissions.options[frm.permissions.selectedIndex].value=="all")
		  frm.selectedTaxPayers.value = "";
		else
			frm.selectedTaxPayers.value = window.frames[0].document.getElementById("facebook-demo").value;
		Y.io("SaveUser.action", { method:'POST', form:{id:document.forms['userData']}, on: {'complete':saveUserComplete} });
	});
};

function saveTaxPayer() {
	collapseDiv(document.getElementById("savetaxpayererrors"));
	YUI().use("io-form", function(Y) {
		function saveTaxPayerComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  alert (o.responseText);
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("savetaxpayererrors"));
			  if (doc.length>0)
				  document.getElementById("empresas").innerHTML = httpRequestText("ListTaxPayers.action");
			}
		};
		Y.io("SaveTaxPayer.action", { method:'POST', form:{id:document.forms['taxpayerData']}, on: {'complete':saveTaxPayerComplete} });
	});
};

function updateClientStates(c) {
	var frm = document.forms["clientData"];
	var ste = frm.elements["client.state"];
	var otr = frm.elements["otherState"];
	var cty = frm.elements["client.city"];
	var oct = frm.elements["otherCity"];
	clearCombo (ste);
	var states = httpRequestText("states.jsp?c="+c).split("\n");
	var count = states.length;
	if (count>1) {
		ste.style.display="block";
		otr.style.display="none";		
		if (c=="es") {
	      cty.style.display="block";
		  oct.style.display="none";		
		}
		for (var s=0; s<count; s++) {
			if (states[s].length>0) {
				var state = states[s].split("`");
				comboPush(ste, state[1], state[0], false, false);
			}
		}
		
	} else {
		ste.style.display="none";
		otr.style.display="block";
		cty.style.display="none";
		oct.style.display="block";		
	}
}

function updateClientCities(c,s) {
	var frm = document.forms["clientData"];
	var cty = frm.elements["client.city"];
	var oct = frm.elements["otherCity"];
	clearCombo (cty);
	if (c=="es") {
      cty.style.display="block";
	  oct.style.display="none";
	  var cities = httpRequestText("cities.jsp?c="+c+"&s="+s).split("\n");
	  var count = cities.length;
	  for (var c=0; c<count; c++) {
		var city = cities[c];
		if (cities[c].length>0) comboPush(cty, cities[c], cities[c], false, false);
	  }
	} else {
	  cty.style.display="none";
	  oct.style.display="block";			
	}
}

function updateTaxPayerStates(c) {
	var frm = document.forms["taxpayerData"];
	var ste = frm.elements["taxPayer.state"];
	var otr = frm.elements["otherState"];
	var cty = frm.elements["taxPayer.city"];
	var oct = frm.elements["otherCity"];
	clearCombo (ste);
	var states = httpRequestText("states.jsp?c="+c).split("\n");
	var count = states.length;
	if (count>1) {
		ste.style.display="block";
		otr.style.display="none";		
		if (c=="es") {
	      cty.style.display="block";
		  oct.style.display="none";		
		}
		for (var s=0; s<count; s++) {
			if (states[s].length>0) {
				var state = states[s].split("`");
				comboPush(ste, state[1], state[0], false, false);
			}
		}
		
	} else {
		ste.style.display="none";
		otr.style.display="block";
		cty.style.display="none";
		oct.style.display="block";		
	}
}

function updateTaxPayerCities(c,s) {
	var frm = document.forms["taxpayerData"];
	var cty = frm.elements["taxPayer.city"];
	var oct = frm.elements["otherCity"];
	clearCombo (cty);
	if (c=="es") {
      cty.style.display="block";
	  oct.style.display="none";
	  var cities = httpRequestText("cities.jsp?c="+c+"&s="+s).split("\n");
	  var count = cities.length;
	  for (var c=0; c<count; c++) {
		var city = cities[c];
		if (cities[c].length>0) comboPush(cty, cities[c], cities[c], false, false);
	  }
	} else {
	  cty.style.display="none";
	  oct.style.display="block";			
	}
}

function inviteUsers() {
	collapseDiv(document.getElementById("inviteusererrors"));
	document.getElementById("invitebutton").style.display="none";
	document.getElementById("sending").style.display="block";
	YUI().use("io-form", function(Y) {
		function inviteUsersComplete(id, o, args) {
			document.getElementById("sending").style.display="none";
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("inviteusererrors"));
			  document.getElementById("invitebutton").style.display="block";
			  if (doc.length>0)
				  window.location.reload();
			}
		};
		Y.io("SendInvitations.action", { method:'POST', form:{id:document.forms['userData']}, on: {'complete':inviteUsersComplete} });
	});
}

function replyMessage(a,id) {
	YUI().use("io-form", function(Y) {
		function replyMessageComplete(id, o, args) {
			spinner.stop();
			var txt = o.responseText;
			if (txt==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
				document.getElementById("messagebox").innerHTML=txt;
			}
		};
		document.getElementById("messagebox").innerHTML="<div id='replyMessageSpin' style='width:64px;height:64px;position:absolute;left:50%;top:50%;margin:-32px 0 0 -32px;'></div>";
		spinner.spin(document.getElementById("replyMessageSpin"));		
		Y.io("ReplyMessage.action?a="+a+"&id="+id, { method:'GET', on: {'complete':replyMessageComplete} });
	});
}

function sendMessage() {
	collapseDiv(document.getElementById("sendmessageerrors"));
	YUI().use("io-form", function(Y) {
		function sendMessageComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("sendmessageerrors"));
			  if (doc.length>0)
				  Y.io("ListMessages.action?f=1&o=0", { method:'GET', on: {'complete':function (i, o, a) {
			   		   document.getElementById("notificaciones").innerHTML = o.responseText;
			   		   document.getElementById("faketab1").className="faketabhighlight";
			   	  } } });	
			}
		};
		Y.io("SendMessage.action", { method:'POST', form:{id:document.forms['messageData']}, on: {'complete':sendMessageComplete} });
	});
}

function archiveMessage(id) {
	YUI().use("io-form", function(Y) {
		function archiveMessageComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("viewmessageerrors"));
			  if (doc.length>0)
				  Y.io("ListMessages.action?f=2&o=0", { method:'GET', on: {'complete':function (i, o, a) {
			   		   document.getElementById("notificaciones").innerHTML = o.responseText;
			   		   document.getElementById("faketab2").className="faketabhighlight";
			   	  } } });	
			}
		};
		Y.io("ArchiveMessage.action?id="+id, { method:'GET', on: {'complete':archiveMessageComplete} });
	});
}

function deleteMessage(id) {
	YUI().use("io-form", function(Y) {
		function deleteMessageComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
			  var doc = saveRecordComplete(xml.documentElement, document.getElementById("viewmessageerrors"));
			  if (doc.length>0)
				  Y.io("ListMessages.action?f=2&o=0", { method:'GET', on: {'complete':function (i, o, a) {
			   		   document.getElementById("notificaciones").innerHTML = o.responseText;
			   		   document.getElementById("faketab2").className="faketabhighlight";
			   	  } } });	
			}
		};
		Y.io("DeleteMessage.action?id="+id, { method:'GET', on: {'complete':deleteMessageComplete} });
	});
}
