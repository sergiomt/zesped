<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ include file="atril.jspf" %>
<div id="header">
  <div class="centeredbox"><div class="yui3-g">
    <div class="yui3-u-1-5" id="logo"><img src="img/logo-zesped.gif" alt="zesped" /></div>
    <div class="yui3-u-4-5" id="login">
<%  if (!sPageName.equals("signup") && !sPageName.equals("enter")) { 
    if (session.getAttribute("nickname")==null) { %>
    <form name="logfrm" method="POST">
      <table summary="Registered Users" align="right" class="loginbox">
        <tr><td colspan="3"></td></tr>
        <tr>
			    <td class="texthighlight"><stripes:label for="user.email" /></td>
			    <td class="texthighlight"><stripes:label for="user.password" /></td>
			    <td width="64px"></td>
			  </tr>
			  <tr> 
			    <td><input type="text" name="email" size="25" class="formfields" style="text-transform:lowercase" /></td>
			    <td><input type="password" name="passw" size="12" class="formfields" /></td>
			    <td width="64px">
			      <input id="loginbutton" type="button" class="login" value="Entrar" />
			      <img id="loading" src="img/loading.gif" width="32" height="16" hspace="15" alt="loading" style="display:none" />
			    </td>
			  </tr>
        <tr>
			    <td></td>
			    <td><a href="RecoverPassword.action" class="anchorhighlight"><small><stripes:label for="user.forgotpassword" /></small></a></td>
			    <td width="64px"></td>
			  </tr>
			</table>
    </form>
		<% } else { %>
				 <form name="headerfrm">
		     <table id="loggedinfo" align="right">
		       <tr><td colspan="7"><img src="img/spacer.gif" width="1" height="8" alt="" /></td></tr>
		       <tr>
		         <td><img src="img/currentcompanyd.gif" width="19" height="16" alt="Company" /></td>
		         <td><img src="img/spacer.gif" width="4" height="1" alt="" /></td>
		         <td>
		           <input type="text" id="faketaxpayerlink" name="faketaxpayerlink" value="<%=StripesResources.getString("user.currentcompany")%>" tabindex="-1" onfocus="document.getElementById('logoutlink').focus()" />&nbsp;
		           <span id="businessnamespan" class="textlowlight"><%=session.getAttribute("businessname")%></span>&nbsp;&nbsp;&nbsp;&nbsp;
		         </td>
		     		 <td><img src="img/myaccd.gif" width="9" height="16" alt="My account" /></td>
		         <td><img src="img/spacer.gif" width="4" height="1" alt="" /></td>
		     		 <td>
		     		   <span class="textdisplay"><stripes:label for="user.myaccount" />:</span>
		     		   <a class="anchorlowlight" href="MyAccount.action"><%=session.getAttribute("fullname")%></a>&nbsp;&nbsp;&nbsp;&nbsp;
		     		 </td>
		     		 <td>
		     		   [&nbsp;<a id="logoutlink" href="logout.jsp" class="anchorhighlight"><stripes:label for="link.logout" /></a>&nbsp;]
		     		 </td>
		       </tr>
					 <c:if test="${actionBean.captureService!='NONE'}" >
		       <tr>
		         <td colspan="8"><img src="img/spacer.gif" width="1" height="4" alt="" /></td>
		       </tr>
		       <tr>
		         <td colspan="2"><img src="img/spacer.gif" width="1" height="28" alt="" /></td>
		     		 <td colspan="6">
		     		   <table summary="Select App" align="right"><tr>
		         	   <td id="fratd1"><a rel="fra" href="#"><img id="fraico" src="img/fraicod.gif" width="14" height="17" alt="Invoice" border="0" /></a></td>
		             <td id="fratd2"><img src="img/spacer.gif" width="4" height="1" alt="" /></td>
		         	   <td id="fratd3"><a rel="fra" id="fratxt" class="stronganchorlowlight" href="#"><stripes:label for="label.captureinvoices" /></a></td>
		             <td><img src="img/spacer.gif" width="12" height="1" alt="" /></td>
		         	   <td id="blntd1"><a rel="bln" href="#"><img id="blnico" src="img/blnicod.gif" width="17" height="19" alt="Bill Note" border="0" /></a></td>
		             <td id="blntd2"><img src="img/spacer.gif" width="4" height="1" alt="" /></td>
		         	   <td id="blntd3"><a id="blntxt" class="stronganchorlowlight" rel="bln" href="#"><stripes:label for="label.capturebillnotes" /></a></td>
		     		   <tr></table>
		         </td>
		       </tr>
		       <script type="text/javascript">		       
		       <% if (sActionBean!=null || sPageName.equals("index")) { %>
		       YUI({ filter: 'raw' }).use("node", "event-hover", function (Y) {
		    	      var switchToBillNotes = function (e) {
		    	    	  document.location = "CaptureBillNote.action";
		    	      };
		    	      var switchToInvoices = function (e) {
		    	    	  document.location = "CaptureInvoice.action";
		    	      };
		    	      function highlightBillNotes(e)  {
		    	    	  var blntxt = document.getElementById("blntxt");
		    	    	  var blnico = document.getElementById("blnico");
		    	    	  if (blntxt.className.indexOf("lowlight")>0)
		    	    		  blntxt.className="stronganchorbrightlight";
		    	    	  if (blnico.src.indexOf("d.gif")>0)
		    	    		  blnico.src="img/blnicow.gif";		    	    	  
		    	      };
		    	      function lowlightBillNotes(e) {
		    	    	  var blntxt = document.getElementById("blntxt");
		    	    	  var blnico = document.getElementById("blnico");
		    	    	  if (blnico.src.indexOf("g.gif")<0) {
		    	    		  blntxt.className="stronganchorlowlight";
		    	    		  blnico.src="img/blnicod.gif";		    	    	  		    	    		  
		    	    	  }
		    	      };
		    	      function highlightInvoices(e)  {
		    	    	  var fratxt = document.getElementById("fratxt");
		    	    	  var fraico = document.getElementById("fraico");
		    	    	  if (fratxt.className.indexOf("lowlight")>0)
		    	    		  fratxt.className="stronganchorbrightlight";
		    	    	  if (fraico.src.indexOf("d.gif")>0)
		    	    		  fraico.src="img/fraicow.gif";		    	    	  
		    	      };
		    	      function lowlightInvoices(e) {
		    	    	  var fratxt = document.getElementById("fratxt");
		    	    	  var fraico = document.getElementById("fraico");
		    	    	  if (fraico.src.indexOf("g.gif")<0) {
		    	    		  fratxt.className="stronganchorlowlight";
		    	    		  fraico.src="img/fraicod.gif";		    	    	  		    	    		  
		    	    	  }
		    	      };
		    	      Y.on("click", switchToBillNotes, "#blnico");
		    	      Y.on("click", switchToBillNotes, "#blntxt");
		    	      Y.on("click", switchToInvoices , "#fraico");
		    	      Y.on("click", switchToInvoices , "#fratxt");
		    	      Y.all('a[rel=bln]').on('hover', highlightBillNotes, lowlightBillNotes);
		    	      Y.all('a[rel=fra]').on('hover', highlightInvoices, lowlightInvoices);
		    	    });
		       <% } %>
					 </script>
		       <c:if test="${actionBean.captureService=='INVOICES'}"> <script type="text/javascript">document.getElementById("fratxt").className="strongbrightlight"; document.getElementById("fraico").src="img/fraicog.gif";</script></c:if> 
		       <c:if test="${actionBean.captureService=='BILLNOTES'}"> <script type="text/javascript">document.getElementById("blntxt").className="strongbrightlight"; document.getElementById("blnico").src="img/blnicog.gif";</script></c:if> 
					 </c:if>     
		     </table>
		     </form>
		<% } } %>
    </div>
  </div></div>
</div>