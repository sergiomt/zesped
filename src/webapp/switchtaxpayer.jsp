<%@ page language="java" session="true" %><%

final String id = request.getParameter("id");
final String name = request.getParameter("name");

if (id!=null && name!=null) {
	if (id.length()>0 && name.length()>0) {
    session.setAttribute("taxpayer_docid", id);
    session.setAttribute("businessname", name);
    session.removeAttribute("incoming_deposits");
	}
}

%>