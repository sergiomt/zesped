function createXMLHttpRequest() {
  var req = false;
  // branch for native XMLHttpRequest object
  if (window.XMLHttpRequest) {
    try {
      req = new XMLHttpRequest();
    } catch(e) {
      alert("new XMLHttpRequest() failed"); req = false;
    }
  } else if (window.ActiveXObject) {
    try {
      req = new ActiveXObject("Msxml2.XMLHTTP");
    } catch(e) {
      try {
        req = new ActiveXObject("Microsoft.XMLHTTP");
      } catch(e) {
      	alert("ActiveXObject(Microsoft.XMLHTTP) failed"); req = false; }
      }
   } // fi
   return req;
} // createXMLHttpRequest

function httpRequestXML(fromurl) {
  var PrivateTextRequest = createXMLHttpRequest();
  PrivateTextRequest.open("GET",fromurl,false);
  PrivateTextRequest.send(null);
  return PrivateTextRequest.responseXML;
}

function httpRequestText(fromurl) {
  var PrivateTextRequest = createXMLHttpRequest();
  PrivateTextRequest.open("GET",fromurl,false);
  PrivateTextRequest.send(null);
  return PrivateTextRequest.responseText;
}

function loadXSL(url) {
  var xmldoc;
  if (navigator.userAgent.toLowerCase().indexOf('chrome')>-1) {
    var xmlhttp = new window.XMLHttpRequest();
    xmlhttp.open("GET", url, false);
    xmlhttp.send(null);			
    xmldoc = xmlhttp.responseXML.documentElement;    
  } else {
	if (navigator.userAgent.toUpperCase().indexOf('MSIE')>-1) {
	  // This is the IE way to do it
	  // Create an empty document as an ActiveX object
      // If there is no root element, this is all we have to do
      xmldoc = new ActiveXObject("MSXML2.DOMDocument");
    }
    else { 
      // This is the W3C standard way to do it
      xmldoc = document.implementation.createDocument("", "", null);
    }
	xmldoc.async = false;
    xmldoc.load(url);	
  }
  return xmldoc;
};

function parseXML(text) {
  if (window.DOMParser) {
    var parser = new DOMParser();
    xmldoc = parser.parseFromString(text,"text/xml");
  } else {
	xmldoc=new ActiveXObject("Microsoft.XMLDOM");
	xmldoc.async=false;
	xmldoc.loadXML(text);
  }
  return xmldoc;
}

function loadHTMLFromDoc(xmldoc, stylesheet, element) {
  var processor;

  // Load the stylesheet if necessary. 
  if (typeof stylesheet == "string") stylesheet = loadXSL(stylesheet); 
  
  // In Mozilla-based browsers, create an XSLTProcessor object and tell it about the stylesheet. 
  if (typeof XSLTProcessor != "undefined") { 
	processor = new XSLTProcessor(); 
	processor.importStylesheet(stylesheet); 
  } 

  // If element is specified by id, look it up. 
  if (typeof element == "string") element = document.getElementById(element); 

  if (processor) { 
      // If we've created an XSLTProcessor (i.e., we're in Mozilla) use it. 
      // Transform the node into a DOM DocumentFragment. 
      var fragment = processor.transformToFragment(xmldoc, document); 
      
      element.innerHTML = "";

      if (navigator.userAgent.toLowerCase().indexOf('firefox')>-1) {
        var inHtml = new XMLSerializer().serializeToString(fragment);
        element.innerHTML = inHtml.replace(/&amp;/gi, "&").replace(/&lt;/gi, "<").replace(/&gt;/gi, ">");
      } else {
        element.appendChild(fragment);
      }
      // Erase the existing content of element. 
      // element.innerHTML = ""; 
      // And insert the transformed nodes.         
      // element.appendChild(fragment);
      
      // Very dirty workaround for fixing the bug that Firefox does not support disable-output-escaping
      //element.innerHTML = element.innerHTML.replace(/&amp;/gi, "&").replace(/&lt;/gi, "<").replace(/&gt;/gi, ">");
  } 
  else if ("transformNode" in xmldoc) { 
      // If the node has a transformNode() function (in IE), use that. 
      // Note that transformNode() returns a string. 
      element.innerHTML = xmldoc.transformNode(stylesheet);
      ajaxlog("element.innerHTML = "+element.innerHTML);
  } 
  else { 
      // Otherwise, we're out of luck. 
      throw "XSLT is not supported in this browser"; 
  }     
} 

function loadHTMLFromURL(xmlurl, stylesheet, element) { 
  return loadHTMLFromText(httpRequestXML(xmlurl), stylesheet, element);
} 

function getElementValue(node) {
  if (node.childNodes) {
	if (node.childNodes.length > 1)
	  if (node.childNodes[1])
	    return node.childNodes[1].nodeValue;
	  else
	    return null;
	else
	  if (node.childNodes[0])
	    return node.childNodes[0].nodeValue;
	  else
	    return null;    	
  } else {
	if (node.firstChild)
	  return node.firstChild.nodeValue;
	else
	return null;
  }
}

function getElementAttribute(parent, name, attr) {
	  var node = parent.getElementsByTagName(name)[0];
	  if (node) {
	    return node.attributes.getNamedItem(attr).value;
	  } else {
	  return null;
	  }
} // getElementAttribute

function ajaxlog (m) {
	 httpRequestText("ajaxlog.jsp?m="+m);
}