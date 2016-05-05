<%@ page import="es.ipsa.atril.sec.authentication.AtrilSession,com.zesped.Log,com.zesped.DAO,com.zesped.model.Countries,com.zesped.model.Country,com.zesped.model.State" language="java" session="true" contentType="text/plain;charset=UTF-8" %><%

  request.setCharacterEncoding("UTF-8");

  final String sCountryISOCode = request.getParameter("c");

  if (null==sCountryISOCode) return;
  if (sCountryISOCode.length()!=2) return;

  AtrilSession oSes = null;
  try {
	    oSes = DAO.getAdminSession("states");
	    Country oCntr = Countries.top(oSes).getCountry(oSes, sCountryISOCode);
	    for (State s : oCntr.states(oSes).list(oSes)) {
	      out.write(s.getCode()+"`"+s.getName()+"\n");	
	    }
		} catch (Exception xcpt) {
		  Log.out.error("states.jsp "+xcpt.getClass()+" "+xcpt.getMessage(), xcpt);
	} finally {
	  if (oSes!=null) {
		  if (oSes.isConnected()) oSes.disconnect();
	    if (oSes.isOpen())  oSes.close();
    }
  }
%>