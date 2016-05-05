package com.zesped.model;

import java.math.BigDecimal;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.List;

import com.knowgate.misc.Gadgets;
import com.zesped.Log;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.documentindexer.exceptions.DocumentIndexerException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class Employee extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("employee_uuid",DataType.STRING,true,true,null),
    	new Attr("employee_name",DataType.STRING,false,true,null),
    	new Attr("employee_id",DataType.STRING,false,false,null),
    	new Attr("tax_id",DataType.STRING,false,false),
    	new Attr("active",DataType.STRING,false,false)
    };

	public Employee() {
		super("Employee");
	}

	public Employee(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}

	public Employee(AtrilSession oSes, Employees oParent) {
		super("Employee");
		newDocument(oSes, oParent.getDocument());
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String getUuid() {
		return getString("employee_uuid");
	}

	public void setUuid(String uuid) throws NullPointerException {
		if (null==uuid)
			throw new NullPointerException("employee_uuid cannot be null");
		else if (uuid.trim().length()==0) 
			throw new NullPointerException("employee_uuid cannot be empty");
		else
			put("employee_uuid", uuid.trim());
	}

	public String getEmployeeId() {
		if (isNull("employee_id"))
			return null;
		else
			return getString("employee_id");
	}

	public void setEmployeeId(String eid) throws NullPointerException {
		if (null==eid)
			remove("employee_id");
		else
			put("employee_id", eid.trim());
	}
	
	public String getName() {
		return getString("employee_name");
	}

	public void setName(String name) throws NullPointerException {
		if (null==name)
			throw new NullPointerException("employee_name cannot be null");
		else if (name.trim().length()==0) 
			throw new NullPointerException("employee_name cannot be empty");
		else
			put("employee_name", name.trim());
	}

	public TaxPayer getTaxPayer(AtrilSession oSes) {
		return new Employees(oSes.getDms().getDocument(parentId())).getTaxPayer(oSes);
	}
	
	public String getTaxId() {
		if (isNull("tax_id"))
			return null;
		else
			return getString("tax_id");
	}

	public void setTaxId(String tid) throws NullPointerException {
		if (null==tid)
			remove("tax_id");
		else
			put("tax_id", tid.trim());
	}
	
	public boolean getActive() {
		if (isNull("active"))
			return true;
		else
			return getString("active").equals("1");
	}

	public void setActive(boolean a) throws NullPointerException {
		put("active", a ? "1" : "0");
	}
		
	public ArrayList<BillNote> billNotes(AtrilSession oSes) {
		ArrayList<BillNote> aLst = new ArrayList<BillNote>();
		Dms oDms = oSes.getDms();
		List<Document> oLbn = oDms.query("BillNote$employee_uuid='"+getUuid()+"'");
		if (!oLbn.isEmpty()) {
		    for (Document b : oLbn) {
		    	aLst.add(new BillNote(oDms, b.id()));
			}
		}	
		return aLst;
	}
	
	public QueryResultSet<Ticket> queryTickets(AtrilSession oSes, Boolean bProcessed, Boolean bHasMistakes, Boolean bOnlyOpen, BigDecimal[] aAmountRange, Integer[] aMonthsRange, Integer[] aYearsRange, String sBiller, String sSearchStr, int iMaxRows, int iOffset)
		throws DocumentIndexerException {
		QueryResultSet<Ticket> aLst = new QueryResultSet<Ticket>();
		Dms oDms = oSes.getDms();
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		if (aAmountRange==null && aMonthsRange==null && aYearsRange==null)
			oIdx.setMaximumNumberOfDocumentReturned(iOffset+iMaxRows);
		String sQry = DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "Ticket AND employee_uuid:\""+getUuid()+"\" AND taxpayer:\""+getTaxPayer(oSes).id()+"\"";
		if (bProcessed!=null)
			  sQry += " AND is_processed:\""+(bProcessed.booleanValue() ? "1" : "0")+"\"";
		if (bHasMistakes!=null)
			  sQry += " AND has_mistakes:\""+(bHasMistakes.booleanValue() ? "1" : "0")+"\"";
		if (bOnlyOpen!=null)
			  sQry += " AND is_open:\""+(bOnlyOpen.booleanValue() ? "1" : "0")+"\"";
		if (sBiller!=null)
			if (sBiller.length()>0)
				sQry += " AND biller_taxpayer:\""+sBiller+"\"";
		if (sSearchStr!=null) {
			if (sSearchStr.length()>0) {
				String sSanitizedSearchStr = Gadgets.removeChars(sSearchStr, "\"\n");
				sQry += " AND (ticket_number:\""+sSanitizedSearchStr +"\" OR concept:\""+sSanitizedSearchStr +"\" OR comments:\""+sSanitizedSearchStr +"\")";
			}
		}
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query(sQry);
		if (!oLst.isEmpty()) {
			int o = 0, n = 0;
			for (Document d : oLst) {
				if (iOffset<=o++) {
					try {
						Ticket oTck = new Ticket(oDms, d.id());
						if (oTck.isWithinRanges(aAmountRange, aMonthsRange, aYearsRange)) {
							aLst.add(oTck);
							if (++n>=iMaxRows) break;								
						}
					} catch (ElementNotFoundException enfe) {
						Log.out.error("Employee.queryTickets() ElementNotFoundException "+enfe.getMessage());	
					}
				}				
			} // next
		} // fi
		aLst.bof(iOffset==0);
		aLst.eof(aLst.size()<iMaxRows);
		return aLst;
	}

	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, NotYetConnectedException, DmsException {
		
		if (isNull("employee_uuid")) put("employee_uuid", Gadgets.generateUUID());
		if (isNull("active")) put("active", "1");
		
		super.save(oSes);
	}	
}
