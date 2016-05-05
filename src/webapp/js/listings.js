	var rangerolled = false;
	var maxrows = 100;
	var offset = 0;
	var rowcount = 0;
	var ids = new Array();
	var doctype;

    function toogleCheckBoxes(e) {
    	for (var i=0; i<ids.length; i++) {
    	  var c = document.getElementById(ids[i]);
    	  c.checked = !c.checked;
        }
    }
	
    YUI().use("anim", "datatable", "datasource", "datasource-io", "datasource-textschema", "datatable-datasource", "io-form", "panel", function(Y) {

    	var frm = document.forms["filter"];

    	var rollDateRange = new Y.Anim({
	        node: '.columnitem0',
	        to: { height: 80 },
	        easing: Y.Easing.backIn
	    });

    	var unrollDateRange = new Y.Anim({
	        node: '.columnitem0',
	        to: { height: 0 },
	        easing: Y.Easing.backIn
	    });
	 
	    var onChangePeriod = function(e) {
	        e.preventDefault();
	        var p = document.forms["filter"].period;
			if (p.options[p.selectedIndex].value=="custom") {
	        	if (!rangerolled) {
	    	        rangerolled=!rangerolled;			    	        		
    	        	rollDateRange.run();
	        	}
	        } else {
	        	if (p.options[p.selectedIndex].value.length==0) {
		        	frm.monthStart.selectedIndex = 0;
		        	frm.yearStart.selectedIndex = 0;
		        	frm.monthEnd.selectedIndex = frm.monthEnd.options.length-1;
		        	frm.yearEnd.selectedIndex = frm.yearEnd.options.length-1;	        		
	        	} else {
	        		var range = p.options[p.selectedIndex].value.split("-");
	        		var astart = range[0].split("/");
	        		var aend = range[1].split("/");
	        		setCombo(frm.monthStart, Number(astart[0])<10 ? "0"+astart[0] : astart[0]);
	        		setCombo(frm.monthEnd, Number(aend[0])<10 ? "0"+aend[0] : aend[0]);
	        		setCombo(frm.yearStart, astart[1]);
	        		setCombo(frm.yearEnd, aend[1]);
	        	}
	        	if (rangerolled) {
	    	        rangerolled=!rangerolled;			    	        		
    	        	unrollDateRange.run();
    	        }
	        }
	    };

	    if (Y.one('#period')!=null) {
	    	Y.one('#period').on('change', onChangePeriod);
	    	if (getCombo(frm.period)=="custom") {
	    		rangerolled = true;
	    		rollDateRange.run();
	    	}
	    }

    	var dts;
    	var table;
    	var invoicesFields = [
    	      		        {key: "id"},
    	    		        {key: "creation_date"},
    	    		        {key: "invoice_date"},
    	    		        {key: "biller_taxpayer"},
    	    		        {key: "business_name"},
    	    		        {key: "invoice_number"},
    	    		        {key: "total_amount"},
    	    		        {key: "currency"},
    	    		        {key: "concept"},
    	    		        {key: "is_processed"},
    	    		        {key: "is_approved"},
    	    		        {key: "has_mistakes"},
    	    		        {key: "chk"} ];

    	var ticketsFields = [
    	      		        {key: "id"},
    	    		        {key: "creation_date"},
    	    		        {key: "biller_taxpayer"},
    	    		        {key: "business_name"},
    	    		        {key: "ticket_number"},
    	    		        {key: "billnote"},
    	    		        {key: "concept"},
    	    		        {key: "description"},
    	    		        {key: "base_amount"},
    	    		        {key: "vat"},
    	    		        {key: "total_amount"},
    	    		        {key: "currency"},
    	    		        {key: "employee_uuid"},
    	    		        {key: "employee_name"},
    	    		        {key: "is_processed"},
    	    		        {key: "chk"} ];

    	if (document.location.pathname=="/zesped/ListNewInvoices.action") {
            
    		doctype = "Invoice";
    		
    		table = new Y.DataTable({
                columns: [
                          { key: "id", allowHTML: true, label: "N&ordm; Doc.", sortable: true, formatter: function (o) { return (o.data.has_mistakes=="1" ? "<a title=\"Esta factura tiene incidencias\"><font color=red><b>!</b></font></a>&nbsp;" : "")+"<a href=EditInvoice.action?a=0&id="+o.data.id+">"+o.data.id+"</a>"; } },
                          { key: "creation_date", label: "Fecha Digitalizaci&oacute;n", sortable: true,
                        	  sortFn: function (a, b, desc) {
                        		  var order;
                        		  if (a==b) {
                        			  order = 0;
                        		  } else {
                            		  var sorted = [a, b].sort();
                            		  order = sorted[0]==a ? -1 : 1;
                            		  if (desc) order = -order;
                        		  }
                        		  return order;
                        	  },
                        	  formatter: function (o) { if (o) if (o.value) if (o.value.length==10) return o.value.substr(8,2)+"/"+o.value.substr(5,2)+"/"+o.value.substr(0,4); else return ""; else return ""; else return ""; },
                          },
                          { key: "invoice_number", label: "N&ordm; Factura", sortable: true },
                          { key: "business_name", allowHTML: true, label: "Emisor", sortable: true, formatter: function (o) { return o.value.substring(0,20); } },
                          { key: "chk", allowHTML: true, label: "<a href=# id=fakecheckbox onclick='toogleCheckBoxes()'><img src=img/fakecheckbox16.gif width=12 height=12 border=0></a>", sortable: false, formatter: '<center><input type="checkbox" id="{value}"></center>' },
                      ],

                caption: "Entrada de datos de facturas",

                width: 740,
                
                // and/or a summary (table attribute)
                summary: "Invoice Listing"
            });

            dts = new Y.DataSource.IO({
    		    source: "QueryInvoices.action?format=csv"
    		});

    		dts.plug({fn: Y.Plugin.DataSourceTextSchema, cfg: {
    		    schema: {
    		      resultDelimiter: "\n",
    		      fieldDelimiter: "`",
    		      resultFields: invoicesFields
    		    } }
    		});

    	} else if (document.location.pathname=="/zesped/ListPendingInvoices.action")  {

    		doctype = "Invoice";
    		
            table = new Y.DataTable({
                columns: [
                          { key: "invoice_number", allowHTML: true, label: "N&ordm; Factura", sortable: true, formatter: function (o) { return (o.data.has_mistakes=="1" ? "<a title=\"Esta factura tiene incidencias\"><font color=red><b>!</b></font></a>&nbsp;" : "")+"<a href=EditInvoice.action?a="+o.data.is_processed+"&id="+o.data.id+">"+(o.value.length>0 ? o.value : "n/d")+"</a>"; } },
                          { key: "invoice_date", label: "Fecha Factura", sortable: true,
                        	  sortFn: function (a, b, desc) {
                        		  var order;
                        		  if (a==b) {
                        			  order = 0;
                        		  } else {
                            		  var sorted = [a, b].sort();
                            		  order = sorted[0]==a ? -1 : 1;
                            		  if (desc) order = -order;
                        		  }
                        		  return order;
                        	  },
                        	  formatter: function (o) { return (o ? (o.value.length==10 ? o.value.substr(8,2)+"/"+o.value.substr(5,2)+"/"+o.value.substr(0,4) : "") : ""); },
                          },
                          { key: "business_name", allowHTML: true, label: "Emisor", sortable: true, formatter: function (o) { return o.value.substring(0,20); } },
                          { key: "total_amount", label: "Total", allowHTML: true, sortable: true, formatter: function (o) { return o.value.length>0 ? "<div style=float:right>"+formatMoney(Number(o.value))+" "+o.data.currency+"</div>" : ""; } },
                          { key: "concept", label: "Concepto", allowHTML: false, sortable: true, formatter: '{value}' },
                          { key: "is_processed", allowHTML: true, label: "Procesada", formatter: function (o) { return "<center>"+(String(o.value)=="1" ? "Si" : "No")+"</center>"; } },
                          { key: "is_approved", allowHTML: true, label: "Aprobada", formatter: function (o) { return "<center>"+(String(o.value)=="1" ? "Si" : "No")+"</center>"; } },
                          { key: "chk", allowHTML: true, label: "<a href=# id=fakecheckbox onclick='toogleCheckBoxes()'><img src=img/fakecheckbox16.gif width=12 height=12 border=0></a>", sortable: false, formatter: '<center><input type="checkbox" id="{value}"></center>' }
                      ],

                caption: "Aprobaci&oacute;n de datos de facturas premium",

                width: 740,
                
                // and/or a summary (table attribute)
                summary: "Invoice Listing"
            });
            
            dts = new Y.DataSource.IO({
    		    source: "QueryInvoices.action?format=csv&serviceflavor=premium"
    		});    		

            dts.plug({fn: Y.Plugin.DataSourceTextSchema, cfg: {
    		    schema: {
    		      resultDelimiter: "\n",
    		      fieldDelimiter: "`",
    		      resultFields: invoicesFields
    		    } }
    		});

    		function approveInvoicesComplete(id, o, args) {
    			var xml = o.responseXML;
    			if (xml==null) {
    			  document.location = "error.jsp?e=expiredsession";
    			} else {
    				var rsp = xml.documentElement;
    				var ers = rsp.getElementsByTagName("errors")[0];
    				var erc = Number(ers.attributes.item("count").value);
    				if (erc>0) {
    					var cde = ers.getElementsByTagName("error")[0].attributes.item("code").value;
    					if (cde.substr(0,6)=="error.") {
    						document.location = "enter.jsp?e="+cde;            	
    					} else {
    			    	    Y.one('#alertbox .message').setHTML('Se produjo un error durante la aprobaci&oacute;n de facturas');
    			    	    Y.one('#alertbox .message').set('className', 'message dialog-stop');
    					}
    				}
    	    		dts.sendRequest({
    	                request: "&maxrows=0",
    	                callback: {
    	                  success: function(e) {                	  
    	                    table.datasource.load({request:composeQueryString()});
    	                  },
    	                  failure: function(e) {
    	                    alert(e.error.message);
    	                  }
    	                }
    	            });	    		
    			}
    		};
    	    
    	    var approveInvoices = function(e) {
    	    	e.preventDefault();
        		var chked = "";
        		for (var c=0; c<ids.length; c++) {
        			var chk = document.getElementById(ids[c]);
        			if (chk.checked)
        				chked += (chked.length==0 ? "" : ",") + ids[c];
        		}
    	    	if (chked.length==0) {
    	    	    Y.one('#alertbox .message').setHTML('Debe seleccionar al menos una factura a aprobar');
    	    	    alertbox.show();	    
    	    	} else {
    		    	Y.one('#confirmbox .message').setHTML('&iquest;Est&aacute; seguro de que desea aprobar las facturas seleccionadas?');		    	
    		    	confirmbox.callback = function() { Y.io("ApproveInvoices.action?docs="+chked, { method:'GET', on: {'complete':approveInvoicesComplete} }); };
    		    	confirmbox.show();
    	    	}
    	    };
    	    Y.one('#approve').on('click', approveInvoices);
            
    	    var confirmRejection = function(e) {
        		var chked = "";
        		for (var c=0; c<ids.length; c++) {
        			var chk = document.getElementById(ids[c]);
        			if (chk.checked)
        				chked += (chked.length==0 ? "" : ",") + ids[c];
        		}
    	    	if (chked.length==0) {
        	    	e.preventDefault();
        	    	e.stopPropagation();
    	    	    Y.one('#alertbox .message').setHTML('Debe seleccionar al menos una factura a rechazar');
    	    	    alertbox.show();	    
    	    	} else {
    	    		var rjt = document.getElementById("reject");
    	    		var hrf = rjt.href;
    	    		var qst = hrf.indexOf("?");
    	    		if (qst>0) hrf = hrf.substring(0, qst);
    	    		rjt.href = rjt.href+"?docs="+chked;
    	    	}
    	    };
    	    Y.one('#reject').on('click', confirmRejection );

    	} else if (document.location.pathname=="/zesped/ListApprovedInvoices.action")  {

    		doctype = "Invoice";
    		
            table = new Y.DataTable({
                columns: [
                          { key: "invoice_number", allowHTML: true, label: "N&ordm; Factura", sortable: true, formatter: function (o) { return (o.data.has_mistakes=="1" ? "<a title=\"Esta factura tiene incidencias\"><font color=red><b>!</b></font></a>&nbsp;" : "")+"<a href=EditInvoice.action?id="+o.data.id+">"+(o.value.length>0 ? o.value : "n/d")+"</a>"; } },
                          { key: "invoice_date", label: "Fecha Factura", sortable: true,
                        	  sortFn: function (a, b, desc) {
                        		  var order;
                        		  if (a==b) {
                        			  order = 0;
                        		  } else {
                            		  var sorted = [a, b].sort();
                            		  order = sorted[0]==a ? -1 : 1;
                            		  if (desc) order = -order;
                        		  }
                        		  return order;
                        	  },
                        	  formatter: function (o) { return (o ? (o.value.length==10 ? o.value.substr(8,2)+"/"+o.value.substr(5,2)+"/"+o.value.substr(0,4) : "") : ""); },
                          },
                          { key: "business_name", allowHTML: true, label: "Emisor", sortable: true, formatter: function (o) { return o.value.substring(0,20); } },
                          { key: "total_amount", label: "Total", allowHTML: true, sortable: true, formatter: function (o) { return o.value.length>0 ? "<div style=float:right>"+formatMoney(Number(o.value))+" "+o.data.currency+"</div>" : ""; } },
                          { key: "concept", label: "Concepto", allowHTML: false, sortable: true, formatter: '{value}' },
                      ],

                caption: "Consulta de facturas",

                width: 740,
                
                // and/or a summary (table attribute)
                summary: "Invoice Listing"
            });
            
            dts = new Y.DataSource.IO({
    		    source: "QueryInvoices.action?format=csv"
    		});    		

            dts.plug({fn: Y.Plugin.DataSourceTextSchema, cfg: {
    		    schema: {
    		      resultDelimiter: "\n",
    		      fieldDelimiter: "`",
    		      resultFields: invoicesFields
    		    } }
    		});
            
    	} else if (document.location.pathname=="/zesped/ListNewBillNotes.action") {

    		doctype = "Ticket";

    		table = new Y.DataTable({
                columns: [
                          { key: "id", allowHTML: true, label: "&nbsp;", sortable: true, formatter: '<a href=EditBillNote.action?id={value}>Editar</a>' },
                          { key: "creation_date", label: "Fecha Entrada", sortable: true,
                        	  sortFn: function (a, b, desc) {
                        		  var order;
                        		  if (a==b) {
                        			  order = 0;
                        		  } else {
                            		  var sorted = [a, b].sort();
                            		  order = sorted[0]==a ? -1 : 1;
                            		  if (desc) order = -order;
                        		  }
                        		  return order;
                        	  },
                        	  formatter: function (o) { return (o ? o.value.substr(8,2)+"/"+o.value.substr(5,2)+"/"+o.value.substr(0,4) : ""); },
                          },
                          { key: "ticket_number", label: "N&ordm; Ticket", sortable: true },
                          { key: "employee_name", label: "Empleado", sortable: true },
                          { key: "description",  label: "Descripci&oacute;n" },
                          { key: "is_processed", allowHTML: true, label: "Procesado", formatter: function (o) { return "<center>"+(String(o.value)=="1" ? "Si" : "No")+"</center>"; } },
                          { key: "chk", allowHTML: true, label: "<a href=# id=fakecheckbox onclick='toogleCheckBoxes()'><img src=img/fakecheckbox16.gif width=12 height=12 border=0></a>", sortable: false, formatter: '<center><input type="checkbox" id="{value}"></center>' }
                      ],

                caption: "Entrada de datos de tickets de gasto",

                width: 740,
                
                // and/or a summary (table attribute)
                summary: "BillNote Listing"
            });

            dts = new Y.DataSource.IO({
    		    source: "QueryTickets.action?format=csv"
    		});    		

            dts.plug({fn: Y.Plugin.DataSourceTextSchema, cfg: {
    		    schema: {
    		      resultDelimiter: "\n",
    		      fieldDelimiter: "`",
    		      resultFields: ticketsFields
    		    } }
    		});
            
    	} else if (document.location.pathname=="/zesped/ListApprovedBillNotes.action") {

    		doctype = "Ticket";

    		table = new Y.DataTable({
                columns: [
                          { key: "ticket_number", label: "N&ordm; Ticket", sortable: true },
                          { key: "concept", allowHTML: true, label: "Nota de Gasto", formatter: function (o) { return "<a href=\"FastEditBillNote.action?id="+o.data.billnote+"\">"+o.value+"</a>"; } },
                          { key: "description",  label: "Descripci&oacute;n" },
                          { key: "base_amount", allowHTML: true, label: "Base",formatter: function (o) { return o.value.length>0 ? "<div style=float:right>"+formatMoney(Number(o.value))+" "+o.data.currency+"</div>" : ""; } },
                          { key: "vat", allowHTML: true, label: "IVA", formatter: function (o) { return o.value.length>0 ? "<div style=float:right>"+formatMoney(Number(o.value))+" "+o.data.currency+"</div>" : ""; } },
                          { key: "total_amount", allowHTML: true, label: "Total", formatter: function (o) { return o.value.length>0 ? "<div style=float:right>"+formatMoney(Number(o.value))+" "+o.data.currency+"</div>" : ""; } }
                      ],

                caption: "Tickets de gasto aprobados",

                width: 740,
                
                // and/or a summary (table attribute)
                summary: "BillNote Listing"
            });

            dts = new Y.DataSource.IO({
    		    source: "QueryTickets.action?format=csv"
    		});    		

            dts.plug({fn: Y.Plugin.DataSourceTextSchema, cfg: {
    		    schema: {
    		      resultDelimiter: "\n",
    		      fieldDelimiter: "`",
    		      resultFields: ticketsFields
    		    } }
    		});
    	} 

    	function composeQueryString() {
    		var frm = document.forms["filter"];
    		var qry = "&maxrows="+String(maxrows)+"&offset="+String(offset);
    		if (frm.status)
    			qry += "&status="+getCombo(frm.status);
    		else
    			qry += "&status=approved";
    		if (frm.recipient.selectedIndex>0)
    			qry += "&recipient="+getCombo(frm.recipient);
    		if (frm.biller.selectedIndex>0)
    			qry += "&biller="+getCombo(frm.biller);
    		if (frm.period) {
    			var per = getCombo(frm.period);    		
    			if (per=="custom") {
    				qry += "&monthStart="+getCombo(frm.monthStart) + "&yearStart="+getCombo(frm.yearStart) + "&monthEnd="+getCombo(frm.monthEnd) + "&yearEnd="+getCombo(frm.yearEnd);
    			} else if (per.length>0) {
    				var startend = getCombo(frm.period).split("-");
    				var startdt = startend[0].split("/");
    				var enddt = startend[1].split("/");
    				qry += "&monthStart="+startdt[0] + "&yearStart="+startdt[1] + "&monthEnd="+enddt[0] + "&yearEnd="+enddt[1];
    			}
    		} else {
				qry += "&dayStart="+getCombo(frm.dayStart) + "&monthStart="+getCombo(frm.monthStart) + "&yearStart="+getCombo(frm.yearStart) + "&dayEnd="+getCombo(frm.dayEnd) + "&monthEnd="+getCombo(frm.monthEnd) + "&yearEnd="+getCombo(frm.yearEnd);    			
    		}
    		if (frm.amountFrom)
    			if (frm.amountFrom.value.length>0)
    				qry += "&amountfrom="+String(parseMoney(frm.amountFrom.value));
    		if (frm.amountTo)
    			if (frm.amountTo.value.length>0)
    				qry += "&amountto="+String(parseMoney(frm.amountTo.value));
    		if (frm.search)
    			qry += "&searchstr="+escape(frm.search.value);
    		if (frm.elements["employee"])
    			if (frm.employee.selectedIndex>0)
    	    		qry += "&employee="+getCombo(frm.employee);
    		if (frm.elements["concept"])
    			if (frm.concept.selectedIndex>0)
    	    		qry += "&billnote="+escape(getCombo(frm.concept));

    		if (document.getElementById("exceldump")) document.getElementById("exceldump").href = "Query"+doctype+"s.action?format=xls"+qry;
    		if (document.getElementById("zipdump")) document.getElementById("zipdump").href = "Query"+doctype+"s.action?format=zip"+qry;
    		if (document.getElementById("factedump")) document.getElementById("factedump").href = "Query"+doctype+"s.action?format=xml"+qry;

    		return qry;
    	}

    	dts.on('data', function (e) { 
    	  var raw = e.data.responseText;
    	  var lin;
    	  if (raw.length==0) {
    		rowcount = 0;
    	  } else {
    	  	lin = raw.split("\n");
    	  	rowcount = lin.length;
    	  }
    	  ids = new Array();
    	  var zrows = (rowcount==0);
    	  // alert ("rowcount="+String(rowcount));
    	  // alert ("zrows="+String(zrows));
    	  if (rowcount==1) {
    		  zrows = true;
    		  for (var c=0; c<lin[0].length && zrows; c++)
    			  if (lin[0].charAt(c)!='`') zrows = false;
    	  }
    	  // alert ("zrows="+String(zrows));
    	  if (zrows) {
        	  document.getElementById("showprev").style.display = "none";
        	  document.getElementById("shownext").style.display = "none";    		  
    	  } else {
        	  for (var r=0; r<rowcount; r++) {
        		  var l = lin[r];
        		  ids.push(l.substring(0,l.indexOf("`")));
        	  }
    		  document.getElementById("showprev").style.display = (offset>0 ? "block" : "none");
        	  document.getElementById("shownext").style.display = (rowcount==maxrows ? "block" : "none");
    	  }
    	});
    	
        var sendSearchRequest = function (e) {
        	
        	var afrom = "";
        	var ato = "";        	
        	var frm = document.forms["filter"];
        	if (frm.amountFrom)
        		afrom = frm.amountFrom.value;
        	if (frm.amountTo)
        		ato = frm.amountTo.value;
        	if (afrom.length>0 && !isFloatValue(afrom)) {
	    	    Y.one('#alertbox .message').setHTML('El importe m&iacute;nimo no es v&aacute;lido');
	    	    Y.one('#alertbox .message').set('className', 'message dialog-stop');
	    	    alertbox.show();
        		document.forms["filter"].amountFrom.focus();
        		return false;
        	}
        	if (ato.length>0 && !isFloatValue(ato)) {
	    	    Y.one('#alertbox .message').setHTML('El importe m&aacute;ximo no es v&aacute;lido');
	    	    Y.one('#alertbox .message').set('className', 'message dialog-stop');
	    	    alertbox.show();
        		document.forms["filter"].amountTo.focus();
        		return false;
        	}
        	if (frm.dayStart) {
        		if (getCombo(frm.dayStart).length>0 || getCombo(frm.monthStart).length>0 || getCombo(frm.yearStart).length>0) {
        			if (!isDate(getCombo(frm.yearStart)+"-"+(getCombo(frm.monthStart).length<2 ? "0" : "")+getCombo(frm.monthStart)+"-"+getComboText(frm.dayStart), "d")) {
        	    	    Y.one('#alertbox .message').setHTML('La fecha Desde no es v&aacute;lida');
        	    	    Y.one('#alertbox .message').set('className', 'message dialog-stop');
        	    	    alertbox.show();
                		return false;        				
        			}
        		}
        	}
        	if (frm.dayEnd) {
        		if (getCombo(frm.dayEnd).length>0 || getCombo(frm.monthEnd).length>0 || getCombo(frm.yearEnd).length>0) {
        			if (!isDate(getCombo(frm.yearEnd)+"-"+(getCombo(frm.monthEnd).length<2 ? "0" : "")+getCombo(frm.monthEnd)+"-"+getComboText(frm.dayEnd), "d")) {
        	    	    Y.one('#alertbox .message').setHTML('La fecha Hasta no es v&aacute;lida');
        	    	    Y.one('#alertbox .message').set('className', 'message dialog-stop');
        	    	    alertbox.show();
                		return false;        				
        			}
        		}
        	}
        	document.getElementById("filterbutton").style.display="none";
        	document.getElementById("filtering").style.display="block";
            if (document.getElementById("filtersomething"))
                document.getElementById("filtersomething").style.display="none";
            dts.sendRequest({
                request: "&maxrows=0",
                callback: {
                  success: function(e) {
                    table.datasource.load({request:composeQueryString()});
                    document.getElementById("filterbutton").style.display="block";
                	document.getElementById("filtering").style.display="none";                    
                    document.getElementById("changeview").style.display="block";
                  },
                  failure: function(e) {
                    alert(e.error.message);
                    document.getElementById("filterbutton").style.display="block";
                	document.getElementById("filtering").style.display="none";
                  }
                }
            });
        };    	
    	Y.on("click", sendSearchRequest, "#filterbutton"); 

	    var showPrevious = function(e) {
	    	e.preventDefault();
	    	offset -= maxrows;
	    	if (offset<0) offset=0;
            dts.sendRequest({
                request: "&maxrows=0",
                callback: {
                  success: function(e) {                	  
                    table.datasource.load({request:composeQueryString()});
                  },
                  failure: function(e) {
                    alert(e.error.message);
                  }
                }
            });
	    };
	    Y.one('#previous').on('click', showPrevious);

	    var showNext = function(e) {
	    	e.preventDefault();
	    	offset += maxrows;
            dts.sendRequest({
                request: "&maxrows=0",
                callback: {
                  success: function(e) {                	  
                    table.datasource.load({request:composeQueryString()});
                  },
                  failure: function(e) {
                    alert(e.error.message);
                  }
                }
            });
	    };
	    Y.one('#next').on('click', showNext);
	    
		function deleteDocumentsComplete(id, o, args) {
			var xml = o.responseXML;
			if (xml==null) {
			  document.location = "error.jsp?e=expiredsession";
			} else {
				var rsp = xml.documentElement;
				var ers = rsp.getElementsByTagName("errors")[0];
				var erc = Number(ers.attributes.item("count").value);
				if (erc>0) {
					var cde = ers.getElementsByTagName("error")[0].attributes.item("code").value;
					if (cde.substr(0,6)=="error.") {
						document.location = "enter.jsp?e="+cde;            	
					} else {
			    	    Y.one('#alertbox .message').setHTML('Se produjo un error durante la eliminaci&oacute;n de documentos');
			    	    Y.one('#alertbox .message').set('className', 'message dialog-stop');
			    	    alertbox.show();
					}
				}
	    		dts.sendRequest({
	                request: "&maxrows=0",
	                callback: {
	                  success: function(e) {                	  
	                    table.datasource.load({request:composeQueryString()});
	                  },
	                  failure: function(e) {
	                    alert(e.error.message);
	                  }
	                }
	            });	    		
			}
		};
	    
	    var deleteDocuments = function(e) {
	    	e.preventDefault();
    		var chked = "";
    		for (var c=0; c<ids.length; c++) {
    			var chk = document.getElementById(ids[c]);
    			if (chk.checked)
    				chked += (chked.length==0 ? "" : ",") + ids[c];
    		}
	    	if (chked.length==0) {
	    	    Y.one('#alertbox .message').setHTML('Debe seleccionar al menos un documento a eliminar');
	    	    alertbox.show();	    
	    	} else {
		    	Y.one('#confirmbox .message').setHTML('&iquest;Est&aacute; seguro de que desea eliminar los documentos seleccionados?');
		    	confirmbox.callback = function() { Y.io("DeleteDocuments.action?type="+doctype+"&docs="+chked, { method:'GET', on: {'complete':deleteDocumentsComplete} }); };
		    	confirmbox.show();
	    	}
	    };

	    if (document.location.pathname=="/zesped/ListApprovedInvoices.action" || document.location.pathname=="/zesped/ListApprovedBillNotes.action")  {
            table.plug(Y.Plugin.DataTableDataSource, { datasource: dts }).render("#datatable");	    	
	    } else {
		    Y.one('#delete').on('click', deleteDocuments);

	    	dts.sendRequest({
	            request: "&maxrows=0",
	            callback: {
	              success: function(e) {
	                table.plug(Y.Plugin.DataTableDataSource, { datasource: dts }).render("#datatable");                    
	                table.datasource.load({request:composeQueryString()});
	              },
	              failure: function(e) {
	                alert("Error "+e.error.message);
	              }
	            }
	        });
	    }
    });	     
    