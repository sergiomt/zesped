<%@ page import="java.text.SimpleDateFormat,java.util.HashMap,com.zesped.model.BaseCompanyObject,com.zesped.model.Invoice,com.zesped.action.QueryInvoices" language="java" session="true" contentType="text/plain" %><%

  QueryInvoices oQry = (com.zesped.action.QueryInvoices) request.getAttribute("actionBean");
  
  if (oQry.getMaxRows()==0) {
	  out.write("````````````");
  } else {
		HashMap<String,BaseCompanyObject> oCmp = oQry.getCompanyMap();
		SimpleDateFormat oFmt = new SimpleDateFormat("yyyy-MM-dd");
  		
		boolean b1st = true;
		for (Invoice i : oQry.getResultSet()) {
			  if (b1st) b1st=false; else out.write("\n");
				out.write(i.id());
				out.write("`");
				if (!i.isNull("creation_date"))
					out.write(oFmt.format(i.getDate("creation_date")));
				out.write("`");
				if (!i.isNull("invoice_date"))
					out.write(oFmt.format(i.getDate("invoice_date")));
				out.write("`");
				if (i.isNull("biller_taxpayer")) {
					out.write("`");
				} else {
				  out.write(i.getString("biller_taxpayer"));
					out.write("`");
				  out.write(oCmp.get(i.getString("biller_taxpayer")).getString("business_name"));
				}
				out.write("`");
				out.write(i.getStringNull("invoice_number"));
				out.write("`");
				if (!i.isNull("final_amount"))
					out.write(i.getString("final_amount"));
				out.write("`");
				if (!i.isNull("currency"))
					out.write(i.getString("currency"));
				out.write("`");
				if (!i.isNull("concept"))
					out.write(i.getString("concept"));
				out.write("`");
				out.write(i.getStringNull("is_processed","0"));
				out.write("`");
				out.write(i.getStringNull("is_approved","0"));
				out.write("`");
				out.write(i.getStringNull("has_mistakes","0"));
				out.write("`");
				out.write(i.id());
		}
  }
%>