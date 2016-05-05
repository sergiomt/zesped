<%@ page import="java.util.HashMap,java.text.SimpleDateFormat,com.zesped.action.QueryBillNotes,com.knowgate.misc.Gadgets,com.zesped.model.BillNote,com.zesped.model.QueryResultSet" language="java" session="true" contentType="text/plain" %><%

QueryBillNotes oQry = (com.zesped.action.QueryBillNotes) request.getAttribute("actionBean");

    if (oQry.getMaxRows()==0) {
		 	out.write("```````````");
		} else {
		  SimpleDateFormat oFmt = new SimpleDateFormat("yyyy-MM-dd");
	
			boolean b1st = true;
			for (BillNote b : oQry.getResultSet()) {
				  if (b1st) b1st=false; else out.write("\n");
					out.write(b.id());
					out.write("`");
				  out.write(oFmt.format(b.getCreationDate()));
					out.write("`");
					out.write(b.getEmployeeUuid());
					out.write("`");
					out.write(b.getEmployeeId());
					out.write("`");
					out.write(b.getEmployeeName());
					out.write("`");
					out.write(b.getConcept());
					out.write("`");
					if (!b.isNull("base_amount")) out.write(b.getBaseAmount().toString());
					out.write("`");
					if (!b.isNull("vat")) out.write(b.getVat().toString());
					out.write("`");
					if (!b.isNull("final_amount")) out.write(b.getTotalAmount().toString());
					out.write("`");
					if (!b.isNull("comments")) out.write(Gadgets.removeChars(b.getComments(),"\"\n'`"));
					out.write("`");
					out.write(b.isOpen() ? "1" : "0");					
					out.write("`");
					out.write(b.id());
			} // next
		}

%>