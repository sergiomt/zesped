<%@ page import="java.text.SimpleDateFormat,java.util.Date,java.util.HashMap,com.knowgate.misc.NameValuePair,com.zesped.model.BaseCompanyObject,com.zesped.model.BillNote,com.zesped.model.Ticket,com.zesped.model.QueryResultSet,org.apache.poi.hssf.usermodel.HSSFWorkbook,org.apache.poi.hssf.usermodel.HSSFSheet,org.apache.poi.hssf.usermodel.HSSFRow,org.apache.poi.hssf.usermodel.HSSFCell,org.apache.poi.hssf.usermodel.HSSFCellStyle,org.apache.poi.hssf.usermodel.HSSFFont,org.apache.poi.hssf.usermodel.HSSFDataFormat,org.apache.poi.hssf.usermodel.HSSFPrintSetup" language="java" session="false" contentType="application/vnd.ms-excel" %><%

    SimpleDateFormat oDtFtm = new SimpleDateFormat("yyyyMMdd");

    response.setHeader("Content-Disposition","attachment; filename=\"zesped-notasdegasto-"+oDtFtm.format(new Date())+".xls\"");

    com.zesped.action.QueryTickets oAbn = (com.zesped.action.QueryTickets) request.getAttribute("actionBean");

    HSSFWorkbook oWrkb = new HSSFWorkbook();
    HSSFSheet oSheet = oWrkb.createSheet();
    oWrkb.setSheetName(0, "Tickets");
    oSheet.getPrintSetup().setLandscape(true);
    HSSFRow oRow;
    HSSFCell oCell;
    HSSFFont oBold = oWrkb.createFont();
    oBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    HSSFCellStyle oHeader = oWrkb.createCellStyle();
    oHeader.setFont(oBold);
    oHeader.setBorderBottom(HSSFCellStyle.BORDER_THICK);
    HSSFCellStyle oNextProj = oWrkb.createCellStyle();
    oNextProj.setBorderTop(HSSFCellStyle.BORDER_THIN);
    HSSFCellStyle oIntFmt = oWrkb.createCellStyle();
    oIntFmt.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    oIntFmt.setDataFormat((short)1);
    HSSFCellStyle oPctFmt = oWrkb.createCellStyle();
    oPctFmt.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    oPctFmt.setDataFormat((short)9);
    HSSFCellStyle oDateFmt = oWrkb.createCellStyle();
    oDateFmt.setDataFormat((short)15);

	  QueryResultSet<Ticket> oTcks = oAbn.getResultSet();
	  HashMap<String,BaseCompanyObject> oComps = oAbn.getCompanyMap();
	  HashMap<String,BillNote> oNotes = oAbn.getBillNoteMap();
	  
	  final int nTcks = oTcks.size();
    
	  final NameValuePair[] aCols = new NameValuePair[] {
			  new NameValuePair("Num Ticket","12"),
			  new NameValuePair("Fecha","12"),
				new NameValuePair("Año","6"),
				new NameValuePair("Mes","6"),
				new NameValuePair("Nota de Gasto","40"),
				new NameValuePair("Descripcion","40"),
				new NameValuePair("Emisor","20"),
				new NameValuePair("CIF EMisor","12"),
				new NameValuePair("Medio de Pago","16"),
				new NameValuePair("Receptor","20"),
				new NameValuePair("CIF Receptor","12"),
				new NameValuePair("Base","12"),
				new NameValuePair("IVA","12"),
				new NameValuePair("Total","12"),
				new NameValuePair("Moneda","8"),
				new NameValuePair("Cuenta","20"),
				new NameValuePair("Descripcion de Cuenta","60")
		};
	  
	  final short nCols = (short) aCols.length;
	  
	  oRow = oSheet.createRow(0);
	  for (short h=0; h<nCols; h++) {
	  	oCell = oRow.createCell(h);
		  oCell.setCellValue(aCols[h].getName());
		  oCell.setCellStyle(oHeader);
		  oSheet.setColumnWidth(h, 256*Integer.parseInt(aCols[h].getValue()));
		} // next
		  
	  for (int i=0; i<nTcks; i++) {
		  Ticket oTck = oTcks.get(i);
		  oRow = oSheet.createRow(i+1);
		  int r = 0;
		  oCell = oRow.createCell(r++);
		  oCell.setCellValue(oTck.getStringNull("ticket_number",""));	
		  oCell = oRow.createCell(r++);
		  oCell.setCellStyle(oDateFmt);
		  if (!oTck.isNull("ticket_date"))
		  	oCell.setCellValue(oTck.getTicketDate());
		  oCell = oRow.createCell(r++);
		  if (!oTck.isNull("year"))
			  	oCell.setCellValue(oTck.getYear());
		  oCell = oRow.createCell(r++);
		  if (!oTck.isNull("month"))
			  	oCell.setCellValue(oTck.getMonth());

		  oCell = oRow.createCell(r++);
		  if (oNotes.containsKey(oTck.parentId()))
			  oCell.setCellValue(oNotes.get(oTck.parentId()).getStringNull("concept",""));
		  oCell = oRow.createCell(r++);
			oCell.setCellValue(oTck.getStringNull("concept",""));
	
			oCell = oRow.createCell(r++);
			if (!oTck.isNull("biller_taxpayer")) {
				String sSupplier = oTck.getString("biller_taxpayer");
				if (oComps.containsKey(sSupplier)) {
					BaseCompanyObject oSupplier = oComps.get(sSupplier);
					oCell.setCellValue(oSupplier.getBusinessName());
					oCell = oRow.createCell(r++);
					oCell.setCellValue(oSupplier.getTaxId());
				} else {
					oCell = oRow.createCell(r++);					
				}
			} else {
				oCell = oRow.createCell(r++);				
			}
			oCell = oRow.createCell(r++);	
			oCell.setCellValue(oTck.getStringNull("payment_mean",""));	
			oCell = oRow.createCell(r++);
			if (!oTck.isNull("taxpayer")) {
				String sCustomer = oTck.getString("taxpayer");
				if (oComps.containsKey(sCustomer)) {
					BaseCompanyObject oCustomer = oComps.get(sCustomer);
					oCell.setCellValue(oCustomer.getBusinessName());
					oCell = oRow.createCell(r++);
					oCell.setCellValue(oCustomer.getTaxId());
				} else {
					oCell = oRow.createCell(r++);					
				}
			} else {
				oCell = oRow.createCell(r++);				
			}
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oTck.getStringNull("base_amount",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oTck.getStringNull("vat",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oTck.getStringNull("final_amount",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oTck.getStringNull("currency",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oTck.getStringNull("accounting_code",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oTck.getStringNull("accounting_desc",""));
	  }

	  oWrkb.write(response.getOutputStream());
	    
	  if (true) return;		
%>