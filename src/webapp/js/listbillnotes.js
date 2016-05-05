function populateEmployees(id, o, args) {
	var data = o.responseText.split("\n");
	var emps = document.forms["filter"].employee;
	clearCombo(emps);
	comboPush (emps, "", "", true, true);
	for (var e=0; e<data.length; e++) {
		var emp = data[e].split("`");
		comboPush (emps, emp[1], emp[0], false, false);
	}
}

function populateConcepts(id, o, args) {
	var data = o.responseXML;
	var cpts = document.forms["filter"].concept;
	var opts = data.documentElement.getElementsByTagName("option");
	clearCombo(cpts);
	comboPush (cpts, "", "", true, true);
	for (var c=0; c<opts.length; c++) {
		comboPush (cpts, opts[0].firstChild.nodeValue, opts[c].attributes.item("value").value , false, false);
	}
}
			
YUI({ filter: "raw" }).use('event','io-form', function (Y) {    		    	  
  
  	var rcpt = Y.one("#recipient");
	rcpt.on('change', function (e) {
		var frm = document.forms["filter"];
		if (frm.recipient.selectedIndex>0)
    	  Y.io("employees.jsp?taxpayer="+getCombo(frm.recipient), { method:'GET', on: {'complete':populateEmployees} });
		else
		  clearCombo(frm.employee);
    } );
  
	var emps = Y.one("#employee");
	  emps.on('change', function (e) {
		    var frm = document.forms["filter"];
    		Y.io("concepts.jsp?onlyopen=1&recipient="+getCombo(frm.recipient)+
    			(frm.employee.selectedIndex>0 ? "&employee="+getCombo(frm.employee) : ""),
    			{ method:'GET', on: {'complete':populateConcepts} });
    } );

	if (document.forms["filter"].recipient.selectedIndex>0)
    	Y.io("employees.jsp?taxpayer="+getCombo(document.forms["filter"].recipient), { method:'GET', on: {'complete':populateEmployees} });		
});
