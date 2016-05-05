<%@ page import="java.text.SimpleDateFormat,java.util.Date,java.util.HashMap,com.knowgate.misc.NameValuePair,com.zesped.model.BaseCompanyObject,com.zesped.model.Invoice,com.zesped.model.QueryResultSet,org.apache.poi.hssf.usermodel.HSSFWorkbook,org.apache.poi.hssf.usermodel.HSSFSheet,org.apache.poi.hssf.usermodel.HSSFRow,org.apache.poi.hssf.usermodel.HSSFCell,org.apache.poi.hssf.usermodel.HSSFCellStyle,org.apache.poi.hssf.usermodel.HSSFFont,org.apache.poi.hssf.usermodel.HSSFDataFormat,org.apache.poi.hssf.usermodel.HSSFPrintSetup" language="java" session="false" contentType="application/vnd.ms-excel" %><%

    SimpleDateFormat oDtFtm = new SimpleDateFormat("yyyyMMdd");

    response.setHeader("Content-Disposition","attachment; filename=\"zesped-facturas-"+oDtFtm.format(new Date())+".xls\"");

    com.zesped.action.QueryInvoices oAbn = (com.zesped.action.QueryInvoices) request.getAttribute("actionBean");

    HSSFWorkbook oWrkb = new HSSFWorkbook();
    HSSFSheet oSheet = oWrkb.createSheet();
    oWrkb.setSheetName(0, "Facturas");
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

	  QueryResultSet<Invoice> oInvs = oAbn.getResultSet();
	  HashMap<String,BaseCompanyObject> oComps = oAbn.getCompanyMap();
	  
	  final int nInvs = oInvs.size();
    
	  final NameValuePair[] aCols = new NameValuePair[] {
			  new NameValuePair("Num Fra","12"),
			  new NameValuePair("Fecha","12"),
				new NameValuePair("Año","4"),
				new NameValuePair("Trimestre","4"),
				new NameValuePair("Mes","6"),
				new NameValuePair("Concepto","40"),
				new NameValuePair("Emisor","20"),
				new NameValuePair("CIF EMisor","12"),
				new NameValuePair("Medio de Pago","16"),
				new NameValuePair("Vencimiento","12"),
				new NameValuePair("C.C.","16"),
				new NameValuePair("Receptor","20"),
				new NameValuePair("CIF Receptor","12"),
				new NameValuePair("Base","12"),
				new NameValuePair("IVA","12"),
				new NameValuePair("Total","12"),
				new NameValuePair("Moneda","8")
		};
	  
	  final short nCols = (short) aCols.length;
	  
	  oRow = oSheet.createRow(0);
	  for (short h=0; h<nCols; h++) {
	  	oCell = oRow.createCell(h);
		  oCell.setCellValue(aCols[h].getName());
		  oCell.setCellStyle(oHeader);
		  oSheet.setColumnWidth(h, 256*Integer.parseInt(aCols[h].getValue()));
		} // next
		  
	  for (int i=0; i<nInvs; i++) {
		  Invoice oInv = oInvs.get(i);
		  oRow = oSheet.createRow(i+1);
		  int r = 0;
		  oCell = oRow.createCell(r++);
		  oCell.setCellValue(oInv.getStringNull("invoice_number",""));	
		  oCell = oRow.createCell(r++);
		  oCell.setCellStyle(oDateFmt);
		  if (!oInv.isNull("invoice_date"))
		  	oCell.setCellValue(oInv.getInvoiceDate());
		  oCell = oRow.createCell(r++);
		  if (!oInv.isNull("year"))
			  	oCell.setCellValue(oInv.getYear());
		  oCell = oRow.createCell(r++);
		  if (!oInv.isNull("quarter"))
			  	oCell.setCellValue(oInv.getQuarter());
		  oCell = oRow.createCell(r++);
		  if (!oInv.isNull("month"))
			  	oCell.setCellValue(oInv.getMonth());
		  oCell = oRow.createCell(r++);
			oCell.setCellValue(oInv.getStringNull("concept",""));
			oCell = oRow.createCell(r++);
			if (!oInv.isNull("biller_taxpayer")) {
				String sSupplier = oInv.getString("biller_taxpayer");
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
			oCell.setCellValue(oInv.getStringNull("payment_mean",""));	
			oCell = oRow.createCell(r++);
			oCell.setCellStyle(oDateFmt);
			if (!oInv.isNull("invoice_date"))
				oCell.setCellValue(oInv.getDueDate());
			oCell = oRow.createCell(r++);	
			oCell.setCellValue(oInv.getStringNull("bank_account",""));	
			oCell = oRow.createCell(r++);
			if (!oInv.isNull("recipient_taxpayer")) {
				String sCustomer = oInv.getString("recipient_taxpayer");
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
			oCell.setCellValue(oInv.getStringNull("base_amount",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oInv.getStringNull("vat",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oInv.getStringNull("final_amount",""));
			oCell = oRow.createCell(r++);
			oCell.setCellValue(oInv.getStringNull("currency",""));
	  }

	  oWrkb.write(response.getOutputStream());
	    
	  if (true) return;		
%>