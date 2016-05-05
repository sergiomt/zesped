package com.zesped.model;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.AttributeMultiValue;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

@SuppressWarnings("serial")
public class BillNotes extends BaseCustomerAccountFolder {

	public BillNotes() {
		super("BillNotes");
	}

	public BillNotes(Document d) {
		super(d);
	}
	
	public BillNotes(Dms oDms, TaxPayer oTaxp) {
		super("BillNotes");
		for (Document d : oTaxp.getDocument().children()) {
			if (d.type().name().equals(getTypeName())) {
				  setDocument(oDms.getDocument(d.id()));
				  break;
			}  
		} // next
	}

	@Override
	public Attr[] attributes() {
		return null;
	}

	public ArrayList<BillNote> list(AtrilSession oSes) throws ElementNotFoundException, NotEnoughRightsException, DmsException {
		ArrayList<BillNote> aNotes = new ArrayList<BillNote>();
		Dms oDms = oSes.getDms();
		for (Document d : getDocument().children())
			aNotes.add(new BillNote(oDms.getDocument(d.id())));
		return aNotes;
	}

	public BillNote seek(AtrilSession oSes, String sConcept) throws ElementNotFoundException, NotEnoughRightsException, DmsException {
		if (null==sConcept) return null;
		if (sConcept.length()==0) return null;
		final String sSanitizedConcept = Gadgets.removeChars(sConcept,"\"\n'");
		for (BillNote b : list(oSes))
			if (!b.isNull("concept"))
				if (sSanitizedConcept.equalsIgnoreCase(b.getConcept()))
					return b;
		return null;
	}
	
	public BillNote create(AtrilSession oSes, String sUserUuid, String sServiceType, String sTaxPayerId, String sConcept, String sEmployeeUuid)
		throws ElementNotFoundException,DmsException,NullPointerException {
		Log.out.debug("Begin BillNotes.create(AtrilSession, "+sUserUuid+","+sServiceType+","+sTaxPayerId+","+sConcept+","+sEmployeeUuid+")");
		if (null==sConcept) throw new NullPointerException("BillNote.create() concept may not be bull");
		if (null==sEmployeeUuid) throw new NullPointerException("BillNote.create() employee may not be bull");
		final String sSanitizedConcept = Gadgets.removeChars(sConcept,"\"\n'");
		if (sConcept.length()==0) throw new NullPointerException("BillNote.create() concept may not be empty");
		if (sEmployeeUuid.length()==0) throw new NullPointerException("BillNote.create() employee may not be empty");
		Employee emee = new Employee();
		Document dmee = emee.exists(oSes, "employee_uuid", sEmployeeUuid);
		if (null==dmee)
			throw new ElementNotFoundException("BillNotes.create()  Employee "+sEmployeeUuid+" not found");
		emee.setDocument(dmee);
		Log.out.debug("got employee "+sEmployeeUuid);
		BillNote bill = new BillNote(oSes, this);
		bill.put("service_type", sServiceType);
		bill.put("concept", sSanitizedConcept);
		if (sTaxPayerId!=null) if (sTaxPayerId.length()>0) bill.put("taxpayer", sTaxPayerId);
		bill.setEmployee(emee);
		bill.save(oSes);
		DAO.log(oSes, bill.getDocument(), Invoice.class, "CREATE BILLNOTE", AtrilEvent.Level.INFO, "");
		oSes.commit();
		customerAccount(oSes.getDms()).grant(bill);
		oSes.commit();
		Log.out.debug("End BillNotes.create() : "+bill);
		return bill;
	}

	private class ConceptDateComparator implements Comparator<Concept> {
		public int compare(Concept c1, Concept c2) {
			return c2.getCreationDate().compareTo(c1.getCreationDate());
		}
	}

	private static ConceptDateComparator oCdtCmp = new BillNotes().new ConceptDateComparator();
	
	private Collection<Concept> concepts(AtrilSession oSes, boolean bOnlyOpen, String sEmployeeUuid) {
		String sTaxPayerId = getDocument().parents().get(0).id();
		Dms oDms = oSes.getDms();
		SortableList<Document> oLst = (SortableList<Document>) oDms.query("BillNote & (($taxpayer='" + sTaxPayerId + "')"+(bOnlyOpen ? " & ($is_open='1')" : "")+")");
		TreeSet<String> oSet = new TreeSet<String>();
		ArrayList<Concept> oCps = new ArrayList<Concept>();
		for (Document d : oLst) {
			Document b = oDms.getDocument(d.id());
			AttributeMultiValue a = b.attribute("concept");
			AttributeMultiValue t = b.attribute("creation_date");
			AttributeMultiValue e = b.attribute("employee_uuid");
			if (a!=null) {
				Concept c;
				try {
					Log.out.debug("concept "+a.toString());
					Log.out.debug("given employee UUID "+sEmployeeUuid);
					Log.out.debug("employee_uuid is empty "+e.isEmpty());
					if (!e.isEmpty()) Log.out.debug("employee_uuid is "+e.toString());
					
					c = new Concept(a.toString(), Attr.toDate(t.toString()));
					if (sEmployeeUuid==null) {
						if (!oSet.contains(c.getName())) {
							oCps.add(c);
							oSet.add(c.getName());
						}
					} else if (e.isEmpty()) {
						if (!oSet.contains(c.getName())) {
							oCps.add(c);
							oSet.add(c.getName());
						}	
					} else if (sEmployeeUuid.equals(e.toString())) {
						if (!oSet.contains(c.getName())) {
							oCps.add(c);
							oSet.add(c.getName());
						}						
					}
				} catch (ParseException pe) {
					Log.out.error("BillNotes.concepts() Error parsing date "+t.toString()+" of concept "+a.toString());
				}
			}
		}
		Collections.sort(oCps, oCdtCmp);
		return oCps;		
	}

	public Collection<Concept> allConcepts(AtrilSession oSes) {
		return concepts(oSes, false, null);
	}

	public Collection<Concept> openConcepts(AtrilSession oSes) {
		return concepts(oSes, true, null);
	}

	public Collection<Concept> allConceptsForEmployee(AtrilSession oSes, String sEmployeeUuid) {
		return concepts(oSes, false, sEmployeeUuid);
	}

	public Collection<Concept> openConceptsForEmployee(AtrilSession oSes, String sEmployeeUuid) {
		return concepts(oSes, true, sEmployeeUuid);
	}
	
	public BillNote forConcept(AtrilSession oSes, String sConcept, String sEmployeeUuid) {
		String sTaxPayerId = getDocument().parents().get(0).id();
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		final String sQry = "taxpayer:\""+sTaxPayerId+"\" AND employee_uuid:\""+sEmployeeUuid+"\" AND concept:\"" + Gadgets.removeChars(sConcept,"\"\n'")+ "\" AND " + DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "BillNote";
		Log.out.debug("DocumentIndexer.query("+sQry+")");
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query(sQry);
		if (oLst.isEmpty())
			return create(oSes, "", "basic", sTaxPayerId, sConcept, sEmployeeUuid);
		else
			return new BillNote(oSes.getDms().getDocument(oLst.get(0).id()));
	}
	
}