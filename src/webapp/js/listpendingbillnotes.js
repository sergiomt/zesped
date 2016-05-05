	var rangerolled = false;
	var maxrows = 100;
	var offset = 0;
	var rowcount = 0;
	var ids = new Array();

    function toogleCheckBoxes(e) {
    	for (var i=0; i<ids.length; i++) {
    	  var c = document.getElementById(ids[i]);
    	  c.checked = !c.checked;
        }
    }
	
    YUI().use("anim", "datatable", "datasource", "datasource-io", "datasource-textschema", "datatable-datasource", "io-form", "panel", function(Y) {

    	var frm = document.forms["filter"];

    	var table = new Y.DataTable({
            columns: [
                      { key: "id", allowHTML: true, label: "Nombre de la Nota de Gasto", sortable: true, formatter: function (o) { return "<a href=FastEditBillNote.action?id="+o.value+">"+o.data.concept+"</a>"; } },
                      { key: "creation_date", label: "Fecha", sortable: true,
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
                      { key: "employee_name", label: "Empleado", sortable: true },
                      { key: "total_amount",  allowHTML: true, label: "Importe", sortable: true, formatter: function (o) { return "<center>"+(o.value.length>0 ? formatMoney(o.value) : "")+"</center>"; } },
                      { key: "is_open", allowHTML: true, label: "Liquidada", formatter: function (o) { return "<center>"+(o.value=="1" ? "No" : "Si")+"</center>"; } },
                      { key: "chk", allowHTML: true, label: "<a href=# id=fakecheckbox onclick='toogleCheckBoxes()'><img src=img/fakecheckbox16.gif width=12 height=12 border=0></a>", sortable: false, formatter:  function (o) { return '<center><input type=checkbox id='+o.value+' '+(o.data.is_open=='0' ? 'disabled=disabled' : '')+'></center>'; } }
                  ],

            caption: "Liquidaci&oacute;n de notas de gasto",

            width: 740,
            
            // and/or a summary (table attribute)
            summary: "BillNote Settling"
        });

        var dts = new Y.DataSource.IO({
		    source: "QueryBillNotes.action?format=csv"
		});    		

        dts.plug({fn: Y.Plugin.DataSourceTextSchema, cfg: {
		    schema: {
		      resultDelimiter: "\n",
		      fieldDelimiter: "`",
		      resultFields: [
		        {key: "id"},
		        {key: "creation_date"},
		        {key: "employee_uuid"},
		        {key: "employee_id"},
		        {key: "employee_name"},
		        {key: "concept"},
		        {key: "base_amount"},
		        {key: "vat"},
		        {key: "total_amount"},
		        {key: "comments"},
		        {key: "is_open"},
		        {key: "chk"}
		      ]
		    } }
		});

    	function composeQueryString() {
    		var frm = document.forms["filter"];
    		var qry = "&maxrows="+String(maxrows)+"&offset="+String(offset);
    		var sts = getCombo(frm.status);
    		if (sts.length>0)
    			qry += "&isopen="+(sts=="open" ? "1" : "0");
    		if (frm.recipient.selectedIndex>0)
    			qry += "&recipient="+getCombo(frm.recipient);
    		qry += "&searchstr="+escape(frm.search.value);
    		if (frm.elements["employee"])
    			if (frm.employee.selectedIndex>0)
    	    		qry += "&employee="+getCombo(frm.employee);
    		if (frm.elements["concept"])
    			if (frm.concept.selectedIndex>0)
    	    		qry += "&concept="+escape(getCombo(frm.concept));
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
    	  if (rowcount==1) {
    		  zrows = true;
    		  for (var c=0; c<lin[0].length && zrows; c++)
    			  if (lin[0].charAt(c)!='`') zrows = false;
    	  }
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

    	dts.sendRequest({
            request: "&maxrows=0",
            callback: {
              success: function(e) {
                table.plug(Y.Plugin.DataTableDataSource, { datasource: dts }).render("#datatable");                    
                table.datasource.load({request:composeQueryString()});
              },
              failure: function(e) {
                // alert("Error "+e.error.message);
              }
            }
        });
    	
        var sendSearchRequest = function (e) {
        	document.getElementById("filterbutton").style.display="none";
        	document.getElementById("filtering").style.display="block";
            dts.sendRequest({
                request: "&maxrows=0",
                callback: {
                  success: function(e) {
                    table.datasource.load({request:composeQueryString()});
                    document.getElementById("filterbutton").style.display="block";
                	document.getElementById("filtering").style.display="none";                    
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
	    
		function deleteBillNotesComplete(id, o, args) {
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
			    	    Y.one('#alertbox .message').setHTML('Se produjo un error durante la eliminaci&oacute;n de notas de gasto');
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
	    
	    var deleteBillNotes = function(e) {
	    	e.preventDefault();
    		var chked = "";
    		for (var c=0; c<ids.length; c++) {
    			var chk = document.getElementById(ids[c]);
    			if (chk.checked)
    				chked += (chked.length==0 ? "" : ",") + ids[c];
    		}
	    	if (chked.length==0) {
	    	    Y.one('#alertbox .message').setHTML('Debe seleccionar al menos una nota de gasto a eliminar');
	    	    alertbox.show();	    
	    	} else {
		    	Y.one('#confirmbox .message').setHTML('&iquest;Est&aacute; seguro de que desea eliminar las notas de gasto seleccionadas?');
		    	confirmbox.callback = function() { Y.io("DeleteDocuments.action?type=BillNote&docs="+chked, { method:'GET', on: {'complete':deleteBillNotesComplete} }); };
		    	confirmbox.show();
	    	}
	    };
	    Y.one('#delete').on('click', deleteBillNotes);
    });	     
    