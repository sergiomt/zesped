
function isFloatValue(str) {
  var len;
  var txt;
  var dot = false;

  if (str==null) return false;
    
  if (len==0) return false;
 
  len = str.length;
  
  // trim input string first
  var lpatt = new RegExp( "^ *(.*)$" );
  var rpatt = new RegExp( "^(.*[^ ]) *$" );
  var parse;
  
  parse=str.match(rpatt);   
  txt = (parse==null ? "" : parse[1]);
  parse=str.match(lpatt);   
  txt = (parse==null ? "" : parse[1]);
  
  for (var c=0; c<len; c++) {
    if (((txt.charCodeAt(c)<48 || txt.charCodeAt(c)>57) && txt.charAt(c)!='.' && txt.charAt(c)!=',' && txt.charAt(c)!='-' ) || 
       (txt.charAt(c)=='.' && dot) || (txt.charAt(c)==',' && dot) || (txt.charAt(c)=='-' && c>0) ||
       (c==len-1 && txt.charCodeAt(c)<48))
      return false;
    dot |= (txt.charAt(c)=='.' || txt.charAt(c)==',');
  }    
  return true;

} // isFloatValue

// ----------------------------------------------------------

function parseMoney(s) {    
    if (!isFloatValue(s)) {
	  return Number.NaN;
  }	else {
	  var d = s.indexOf('.');
	  var c = s.indexOf(',');

	  if (d!=0 && d!=0) {
		  if (d>c)
			  s = s.replace(/\x2C/g,"");
	      else
		      s = s.replace(/\x2E/g,"");
	  } // fi

	  s = s.replace(/\x2C/g,".");
	  return Number(s);
  }
}

// ----------------------------------------------------------

function formatMoney(m) {
	var s = String(m).replace(/\x2E/g,",");
	var c = s.indexOf(",");
	if (c>0 && c<s.length-2)
		s = s.substring(0,c+3);
	return s;
}

/**
 * Get last day of month taking into account leap years.
 * @param month [0..11]
 * @param year  (4 digits)
*/
 function getLastDay(month, year) {

   switch(month) {
     case 0:
     case 2:
     case 4:
     case 6:
     case 7:
     case 9:
     case 11:
       return 31;
     case 3:
     case 5:
     case 8:
     case 10:
       return 30;
     case 1:
	      return ( (year%400==0) || ((year%4==0) && (year%100!=0)) ) ? 29 : 28;
   } // end switch()
   return 0;
 } // getLastDay()
 
 // ----------------------------------------------------------
 
 /**
   * Verify that a string represents a valid date
   * @param Input string
   * @param Date format. "d"  for dates with format "yyyy-MM-dd"
                         "s"  for dates with format "dd/MM/yyyy"
                         "ts" for dates with format "yyyy-MM-dd HH:mm:ss"
 */
 function isDate (dtexpr, dtformat) {
   var exp;
   var ser;
   var ret;
   var yy;
   var mm;
   var dd;
 
   if (dtformat=="d") {
     exp = new RegExp("[0-9]{4}-[0-9]{2}-[0-9]{2}");
     if (exp.test(dtexpr)) {
       ser = dtexpr.split("-");
       yy = parseInt(ser[0],10);
       mm = parseFloat(ser[1],10)-1;
       dd = parseFloat(ser[2],10);
     
       if (mm<0 || mm>12) {
         ret = false;
       }
       else if (dd>getLastDay(mm,yy)) {
         ret = false;
       }
       else
         ret = true;                
     }
     else {
       ret = false;
     }
   } else if (dtformat=="s") {
     exp = new RegExp("[0-9]{2}/[0-9]{2}/[0-9]{4}");
     if (exp.test(dtexpr)) {
       ser = dtexpr.split("/");
       yy = parseInt(ser[2],10);
       mm = parseFloat(ser[1],10)-1;
       dd = parseFloat(ser[0],10);
     
       if (mm<0 || mm>12) {
         ret = false;
       }
       else if (dd>getLastDay(mm,yy)) {
         ret = false;
       }
       else
         ret = true;                
     }
     else {
       ret = false;
     }
   } else if (dtformat=="ts") {
     exp = new RegExp("[0-9]{4}-[0-9]{2}-[0-9]{2}.[0-9]{2}:[0-9]{2}:[0-9]{2}");
     if (exp.test(dtexpr)) {
       ret = isDate(dtexpr.substr(0,10), "d");
     } else {
       ret = false;
     }      
   } else {
     ret = false;
   }
   
   return ret;
 } // isDate()
 
 // ----------------------------------------------------------
