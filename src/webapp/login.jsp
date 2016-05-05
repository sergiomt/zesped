<%@ page import="java.util.HashMap,es.ipsa.atril.eventLogger.AtrilEvent,es.ipsa.atril.sec.authentication.AtrilSession,es.ipsa.atril.sec.authentication.AuthenticationManagerFactory,es.ipsa.atril.util.persistence.exceptions.PersistenceManagerException,es.ipsa.atril.sec.exceptions.AuthenticationException,es.ipsa.atril.sec.exceptions.SecuritySystemException,es.ipsa.atril.exceptions.ElementNotFoundException,com.zesped.Log,com.zesped.DAO,com.zesped.model.User,com.zesped.model.Role,com.zesped.model.CustomerAccount,com.zesped.model.TaxPayer,com.zesped.model.Employee" language="java" session="true" %><jsp:useBean id="LoginCache" scope="application" class="com.knowgate.cache.DistributedCachePeer"/><%

final String sEmail = request.getParameter("email");
final String sPassw = request.getParameter("passw");
String sFrmt = request.getParameter("format");

if (sFrmt==null) sFrmt="session";
if (sFrmt.length()==0) sFrmt="session";

if (sFrmt.equalsIgnoreCase("session")) {
	response.setContentType("text/plain;charset=UTF-8");
} else if (sFrmt.equalsIgnoreCase("xml")) {
	response.setContentType("text/xml;charset=UTF-8");
	out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response>");
}

if (sEmail==null) {
	if (sFrmt.equalsIgnoreCase("session"))
	  out.write("KO`nullemail");
  else if (sFrmt.equalsIgnoreCase("xml"))
	  out.write("<errors count=\"1\"><error>nullemail</error></errors><data></data></response>");
} else if (sPassw==null) {
	if (sFrmt.equalsIgnoreCase("session"))
	  out.write("KO`nullpassw");	
	else if (sFrmt.equalsIgnoreCase("xml"))
		out.write("<errors count=\"1\"><error>nullpassw</error></errors><data></data></response>");
} else if (sEmail.length()==0) {
	if (sFrmt.equalsIgnoreCase("session"))
	  out.write("KO`emptyemail");	
	else if (sFrmt.equalsIgnoreCase("xml"))
		out.write("<errors count=\"1\"><error>emptyemail</error></errors><data></data></response>");
} else if (sPassw.length()==0) {
	if (sFrmt.equalsIgnoreCase("session"))
	  out.write("KO`emptypassw");
	else if (sFrmt.equalsIgnoreCase("xml"))
		out.write("<errors count=\"1\"><error>emptypassw</error></errors><data></data></response>");
} else {
	boolean bCachedCredentials = false;
	HashMap<String,Object> oSessionInfo = (HashMap<String,Object>) LoginCache.get(sEmail);
	if (oSessionInfo!=null) {
	  	if (oSessionInfo.get("password").equals(sPassw)) {
	  		bCachedCredentials = true;
			  if (sFrmt.equalsIgnoreCase("session")) {
				    request.getSession().setAttribute("nickname", oSessionInfo.get("nickname"));
				    request.getSession().setAttribute("password", oSessionInfo.get("password"));
					  request.getSession().setAttribute("fullname", oSessionInfo.get("fullname"));
					  request.getSession().setAttribute("businessname", oSessionInfo.get("businessname"));
					  request.getSession().setAttribute("user_uuid", oSessionInfo.get("user_uuid"));
					  request.getSession().setAttribute("user_docid", oSessionInfo.get("user_docid"));
					  request.getSession().setAttribute("customer_acount", oSessionInfo.get("customer_acount"));
					  request.getSession().setAttribute("customer_account_docid", oSessionInfo.get("customer_account_docid"));
					  request.getSession().setAttribute("taxpayer_docid", oSessionInfo.get("taxpayer_docid"));
					  request.getSession().setAttribute("employee_uuid", oSessionInfo.get("employee_uuid"));
					  request.getSession().setAttribute("can_approve", oSessionInfo.get("can_approve"));
					  request.getSession().setAttribute("can_settle", oSessionInfo.get("can_settle"));
					  request.getSession().setAttribute("can_premium", oSessionInfo.get("can_premium"));
					  request.getSession().setAttribute("role", oSessionInfo.get("role"));
						out.write("OK`"+oSessionInfo.get("nickname")+"`"+oSessionInfo.get("firstname")+" "+oSessionInfo.get("lastname")+"`"+oSessionInfo.get("businessname"));
				  } else if (sFrmt.equalsIgnoreCase("xml")) {
					  out.write("<errors count=\"0\"></errors>");
					  out.write("<data>");
					  out.write("<nickname>"+oSessionInfo.get("nickname")+"</nickname>");
					  out.write("<password><![CDATA["+oSessionInfo.get("password")+"]]></password>");
					  out.write("<fullname><![CDATA["+oSessionInfo.get("firstname")+" "+oSessionInfo.get("lastname")+"]]></fullname>");
					  out.write("<businessname><![CDATA["+oSessionInfo.get("businessname")+"]]></businessname>");
					  out.write("<user_uuid>"+oSessionInfo.get("user_uuid")+"</user_uuid>");
					  out.write("<user_docid>"+oSessionInfo.get("user_docid")+"</user_docid>");					  
					  out.write("<customer_acount>"+oSessionInfo.get("customer_acount")+"</customer_acount>");
					  out.write("<customer_account_docid>"+oSessionInfo.get("customer_account_docid")+"</customer_account_docid>");
					  out.write("<taxpayer_docid>"+oSessionInfo.get("taxpayer_docid")+"</taxpayer_docid>");
					  out.write("<employee_uuid>"+oSessionInfo.get("employee_uuid")+"</employee_uuid>");
					  out.write("<can_approve>"+oSessionInfo.get("can_approve")+"</can_approve>");
					  out.write("<can_settle>"+oSessionInfo.get("can_settle")+"</can_settle>");
					  out.write("<can_premium>"+oSessionInfo.get("can_premium")+"</can_premium>");
					  out.write("<role>"+oSessionInfo.get("role")+"</role>");
					  out.write("</data></response>");
				  }	  		
	  	} else {
	  		LoginCache.expire(sEmail);
	  	}
	}
	if (bCachedCredentials) {
		DAO.log(User.class, "LOGIN", AtrilEvent.Level.INFO, sEmail);
	} else {
		String sName = null;
		User oUsr = null;
		String sUsr;
		Role eRole = null;
		TaxPayer oTxr = null;
		CustomerAccount oAcc = null;
		Employee oEmp = new Employee();
		String sEmp = "";
		AtrilSession oSes = null;
		Log.out.debug("Trying to log in "+sEmail);
		try {
			oSes = DAO.getAdminSession("login");
			oUsr = new User(oSes, User.forEmail(sEmail));
			if (!oUsr.isActive()) throw new SecurityException("User account is deactivated");
			Log.out.debug("User "+oUsr.id()+" found for e-mail "+sEmail);
			Log.out.debug("Loading customer account "+oUsr.getCustomerAccountId());
		  oAcc = new CustomerAccount(oSes,oUsr);
			Log.out.debug("Getting default tax payers for account");
		  oTxr = oAcc.taxpayers(oSes).byDefault(oSes,oUsr);
			Log.out.debug("Selected tax payer was "+oTxr.getBusinessName());
			Log.out.debug("Getting user role");
		  eRole = oUsr.getRole(oAcc);
			Log.out.debug("User role is "+eRole);
			if (oEmp.exists(oSes, "employee_uuid", oUsr.getNickName())!=null) {
				Log.out.debug("Employee UUID is "+oUsr.getNickName());
				sEmp = oUsr.getNickName();
			} else {
				Log.out.debug("No employee found for user "+oUsr.getNickName());			
			}
		} catch (SecurityException sec) {
		  Log.out.error("login.jsp "+sec.getMessage(), sec);
			if (sFrmt.equalsIgnoreCase("session"))
				out.write("KO`deactivated");		
			else
			  out.write("<errors count=\"1\"><error>deactivated</error></errors>");
		} catch (PersistenceManagerException pme) {
			Log.out.error("login.jsp "+pme.getMessage(), pme);
			if (sFrmt.equalsIgnoreCase("session"))
				out.write("KO`errorindatabaseaccess");		
			else if (sFrmt.equalsIgnoreCase("xml"))
				out.write("<errors count=\"1\"><error>errorindatabaseaccess</error></errors>");
		} catch (IllegalStateException ise) {
			Log.out.error("login.jsp "+ise.getMessage(), ise);
			if (sFrmt.equalsIgnoreCase("session"))
			  out.write("KO`databasenotconnected");
			else if (sFrmt.equalsIgnoreCase("xml"))
				out.write("<errors count=\"1\"><error>databasenotconnected</error></errors>");
		} catch (ElementNotFoundException enf) {
			Log.out.error("ElementNotFoundException "+enf.getMessage());
			DAO.log(User.class, "LOGIN", AtrilEvent.Level.ERROR, sEmail+" not found");
			if (sFrmt.equalsIgnoreCase("session"))
			  out.write("KO`usernotfound");
			else if (sFrmt.equalsIgnoreCase("xml"))
				out.write("<errors count=\"1\"><error>usernotfound</error></errors>");
		} finally {
			if (oSes!=null) {
				if (oSes.isConnected()) oSes.disconnect();
				if (oSes.isOpen()) oSes.close();
				oSes = null;
			}
		}
	  if (null==oUsr) {
			if (sFrmt.equalsIgnoreCase("xml"))
			  out.write("<data></data></response>");
	  } else {
		  try {
			  Log.out.debug("DAO.getSession(login, "+oUsr.getAdministeredUser().getName()+","+sPassw+")");
			  oSes = DAO.getSession("login", oUsr.getAdministeredUser().getName(), sPassw);
			  if (sFrmt.equalsIgnoreCase("session")) {
			    request.getSession().setAttribute("nickname", oUsr.getNickName());
			    request.getSession().setAttribute("password", sPassw);
				  request.getSession().setAttribute("fullname", oUsr.getFirstName()+" "+oUsr.getLastName());
				  request.getSession().setAttribute("businessname", oTxr.getBusinessName());
				  request.getSession().setAttribute("user_uuid", oUsr.getString("user_uuid"));
				  request.getSession().setAttribute("user_docid", oUsr.id());
				  request.getSession().setAttribute("customer_acount", oAcc.getUuid());
				  request.getSession().setAttribute("customer_account_docid", oAcc.id());
				  request.getSession().setAttribute("taxpayer_docid", oTxr.id());
				  request.getSession().setAttribute("employee_uuid", sEmp);
				  request.getSession().setAttribute("can_approve", oUsr.getString("can_approve"));
				  request.getSession().setAttribute("can_settle", oUsr.getString("can_settle"));
				  request.getSession().setAttribute("can_premium", oUsr.getString("can_premium"));
				  request.getSession().setAttribute("role", eRole);
					out.write("OK`"+oUsr.getNickName()+"`"+oUsr.getFirstName()+" "+oUsr.getLastName()+"`"+oAcc.getBusinessName());
			  } else if (sFrmt.equalsIgnoreCase("xml")) {
				  out.write("<errors count=\"0\"></errors>");
				  out.write("<data>");
				  out.write("<nickname>"+oUsr.getNickName()+"</nickname>");
				  out.write("<password><![CDATA["+sPassw+"]]></password>");
				  out.write("<fullname><![CDATA["+oUsr.getFirstName()+" "+oUsr.getLastName()+"]]></fullname>");
				  out.write("<businessname><![CDATA["+oTxr.getBusinessName()+"]]></businessname>");
				  out.write("<user_uuid>"+oUsr.getString("user_uuid")+"</user_uuid>");
				  out.write("<user_docid>"+oUsr.id()+"</user_docid>");
				  out.write("<customer_acount>"+oAcc.getUuid()+"</customer_acount>");
				  out.write("<customer_account_docid>"+oAcc.id()+"</customer_account_docid>");
				  out.write("<taxpayer_docid>"+oTxr.id()+"</taxpayer_docid>");
				  out.write("<can_approve>"+oUsr.getString("can_approve")+"</can_approve>");
				  out.write("<can_settle>"+oUsr.getString("can_settle")+"</can_settle>");
				  out.write("<can_premium>"+oUsr.getString("can_premium")+"</can_premium>");
				  out.write("<employee_uuid>"+sEmp+"</employee_uuid>");
				  out.write("<role>"+eRole+"</role>");
				  out.write("</data></response>");
			  }

			  DAO.log(oSes, oUsr.getDocument(), User.class, "LOGIN", AtrilEvent.Level.INFO, sEmail);

				oSessionInfo = new HashMap<String,Object>();
				oSessionInfo.put("nickname", oUsr.getNickName());
				oSessionInfo.put("firstname", oUsr.getFirstName());
				oSessionInfo.put("lastname", oUsr.getLastName());
				oSessionInfo.put("password", sPassw);
				oSessionInfo.put("fullname", oUsr.getFirstName()+" "+oUsr.getLastName());
				oSessionInfo.put("businessname", oTxr.getBusinessName());
				oSessionInfo.put("user_uuid", oUsr.getString("user_uuid"));
				oSessionInfo.put("user_docid", oUsr.id());
				oSessionInfo.put("customer_acount", oAcc.getUuid());
				oSessionInfo.put("customer_account_docid", oAcc.id());
				oSessionInfo.put("taxpayer_docid", oTxr.id());
				oSessionInfo.put("employee_uuid", sEmp);
				oSessionInfo.put("can_approve", oUsr.getString("can_approve"));
				oSessionInfo.put("can_settle", oUsr.getString("can_settle"));
				oSessionInfo.put("can_premium", oUsr.getString("can_premium"));
				oSessionInfo.put("role", eRole);

				try {
			    LoginCache.put(sEmail, oSessionInfo);
				} catch (IllegalStateException ignore) { }
			
		  } catch (IllegalStateException ise) {
				Log.out.warn("login.jsp IllegalStateException "+ise.getMessage(), ise);
				if (sFrmt.equalsIgnoreCase("session"))
				  out.write("KO`databasenotconnected");
				else if (sFrmt.equalsIgnoreCase("xml"))
					out.write("<errors count=\"1\"><error>databasenotconnected</error></errors><data></data></response>");
			} catch (AuthenticationException enf) {
				Log.out.warn("login.jsp AuthenticationException "+enf.getMessage(), enf);
				DAO.log(User.class, "LOGIN", AtrilEvent.Level.WARNING, "password mismatch");
				if (sFrmt.equalsIgnoreCase("session"))
				  out.write("KO`passwordmismatch");
				else if (sFrmt.equalsIgnoreCase("xml"))
					out.write("<errors count=\"1\"><error>passwordmismatch</error></errors><data></data></response>");
			} catch (SecuritySystemException sse) {
				Log.out.warn("login.jsp SecuritySystemException "+sse.getMessage(), sse);
				DAO.log(User.class, "LOGIN", AtrilEvent.Level.ERROR, "system security exception");
				if (sFrmt.equalsIgnoreCase("session"))
				  out.write("KO`systemsecurity");
				else if (sFrmt.equalsIgnoreCase("xml"))
					out.write("<errors count=\"1\"><error>systemsecurity</error></errors><data></data></response>");
			} catch (NullPointerException sse) {
				Log.out.error("login.jsp NullPointerException "+sse.getMessage(), sse);
				DAO.log(User.class, "LOGIN", AtrilEvent.Level.ERROR, "null pointer exception");
				if (sFrmt.equalsIgnoreCase("session"))
				  out.write("KO`nullpointer");
				else if (sFrmt.equalsIgnoreCase("xml"))
					out.write("<errors count=\"1\"><error>nullpointer</error></errors><data></data></response>");
			} finally {
				if (oSes!=null) {
					if (oSes.isConnected()) oSes.disconnect();
					if (oSes.isOpen()) oSes.close();
			  }
	    }
	  }		
	}
}
%>