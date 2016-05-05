<%@ page import="java.util.Collection,es.ipsa.atril.sec.authentication.AtrilSession,com.zesped.Log,com.zesped.DAO,com.zesped.model.Country,com.zesped.model.Countries,com.zesped.model.Cities,com.zesped.model.City" language="java" session="true" contentType="text/plain" %><%

  final String sCountryISOCode = request.getParameter("c");
  final String sStateCode = request.getParameter("s");

  if (null==sCountryISOCode) return;
  if (sCountryISOCode.length()!=2) return;

  AtrilSession oSes = null;
  try {
	    oSes = DAO.getAdminSession("cities");
	    Cities oCts = Countries.top(oSes).getCountry(oSes, sCountryISOCode).states(oSes).getState(oSes, sStateCode).cities(oSes);	    
	    for (City c : oCts.list(oSes)) {
	      out.write(c.getName()+"\n");	
	    }
		} catch (Exception xcpt) {
		  Log.out.error("cities.jsp "+xcpt.getClass()+" "+xcpt.getMessage());
	} finally {
	  if (oSes!=null) {
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
    }
  }
%>