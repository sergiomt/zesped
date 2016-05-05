<%@ page import="java.util.HashMap,java.text.SimpleDateFormat,com.zesped.action.QueryTickets,com.zesped.model.BaseCompanyObject,com.zesped.model.BillNote,com.zesped.model.Employee,com.zesped.model.Ticket,com.zesped.model.QueryResultSet" language="java" session="true" contentType="text/plain" %><%

    QueryTickets oQry = (com.zesped.action.QueryTickets) request.getAttribute("actionBean");

    if (oQry.getMaxRows()==0) {
		 	out.write("```````````````");
		} else {
			HashMap<String,BaseCompanyObject> oCmp = oQry.getCompanyMap();
			HashMap<String,Employee> oEmp = oQry.getEmployeeMap();
		  SimpleDateFormat oFmt = new SimpleDateFormat("yyyy-MM-dd");
	
			boolean b1st = true;
			for (Ticket i : oQry.getResultSet()) {
				  if (b1st) b1st=false; else out.write("\n");
					out.write(i.id());
					out.write("`");
					if (!i.isNull("creation_date"))
						out.write(oFmt.format(i.getCreationDate()));
					out.write("`");
					if (i.isNull("biller_taxpayer")) {
						out.write("`");
					} else {					
					  out.write(i.getBillerTaxPayer());
						out.write("`");
					  out.write(oCmp.get(i.getBillerTaxPayer()).getBusinessName());
					}
					out.write("`");
					out.write(i.getStringNull("ticket_number"));
					BillNote oBln = oQry.getBillNoteMap().get(i.parentId());
					out.write("`");
					out.write(oBln.id());
					out.write("`");
					out.write(oBln.getConcept());
					out.write("`");
					out.write(i.getConcept());
					out.write("`");
					out.write(i.getStringNull("base_amount",""));
					out.write("`");
					out.write(i.getStringNull("vat",""));
					out.write("`");
					out.write(i.getStringNull("final_amount",""));
					out.write("`");
					out.write(i.getStringNull("currency",""));
					out.write("`");
					out.write(i.getStringNull("employee_uuid",""));
					out.write("`");
					if (i.getStringNull("employee_uuid","").length()>0)
						  out.write(oEmp.get(i.getString("employee_uuid")).getName());
					out.write("`");
					out.write(i.getStringNull("is_processed","0"));
					out.write("`");
					out.write(i.id());
		  } // next
		}

%>