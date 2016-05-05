package com.zesped.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Employees extends BaseCollectionObject<Employee> {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] { };

	public Employees() {
		super("Employees", Employee.class);
	}

	public Employees(Document d) {
		super(d, Employee.class);
	}
	
	public Employees(AtrilSession oSes, Document oParent) {
		super("Employees", Employee.class);
		newDocument(oSes, oParent);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public TaxPayer getTaxPayer(AtrilSession oSes) {
		Document oParent = oSes.getDms().getDocument(parentId());
		TaxPayer oTxpr = new TaxPayer(oParent);
		return oTxpr;
	}
	
	private class EmployeeNameComparator implements Comparator<Employee> {
		public int compare(Employee c1, Employee c2) {
			return c1.getName().compareTo(c2.getName());
		}
	}
	
	private static EmployeeNameComparator oEmpCmp = new Employees().new EmployeeNameComparator();
	
	public Employee seek(AtrilSession oSes, String sIdOrUUid) throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		if (sIdOrUUid==null) return null;
		if (sIdOrUUid.length()==0) return null;
		for (Employee e : list(oSes))
			if (sIdOrUUid.equals(e.getId()) || sIdOrUUid.equals(e.getUuid()) || sIdOrUUid.equalsIgnoreCase(e.getTaxId()))
				return e;
		return null;
	}
	
	@Override
	public ArrayList<Employee> list(AtrilSession oSes) throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		ArrayList<Employee> aEmployees = super.list(oSes);
		Collections.sort(aEmployees, oEmpCmp);
		return aEmployees;
	}
	
}