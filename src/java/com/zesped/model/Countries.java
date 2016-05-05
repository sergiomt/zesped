package com.zesped.model;

import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import com.zesped.DAO;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public class Countries extends BaseModelObject {

	private static final Attr[] aAttrs = new Attr[] { };

	private static ArrayList<Country> aCntrs = null;

	public Countries() {
		super("Countries");
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public static Countries top(AtrilSession oSes) throws ElementNotFoundException {
		Document z = Zesped.top(oSes).getDocument();
		Countries c = new Countries();
		for (Document d : z.children()) {
			if (d.type().name().equals(c.getTypeName())) {
				c.setDocument(oSes.getDms().getDocument(d.id()));
				break;
			}
		} // next
		if (c.getDocument()==null) throw new ElementNotFoundException(c.getTypeName()+" document not found");
		return c;      
	}

	public Country getCountry(AtrilSession oSes, String sISOCode) {
		Country oCntr = new Country();
		if (aCntrs==null) {
			Document oDoc = oCntr.exists(oSes, "id", sISOCode);
			if (oDoc==null) {
				return null;
			} else {
				oCntr.setDocument(oDoc);
				return oCntr;			
			}
		} else {
			for (Country c : aCntrs)
				if (c.getIsoCode().equals(sISOCode)) return c;
			return null;
		}
	}

	public Country create(AtrilSession oSes, String sISOCode, Map<String,String> oNameMap)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, NotYetConnectedException, DmsException {
		Country oCntr = new Country(oSes, this.getDocument());
		oCntr.put("id", sISOCode);
		Iterator<String> oNames = oNameMap.keySet().iterator();
		while (oNames.hasNext()) {
			String sName = oNames.next();
			oCntr.put(sName, oNameMap.get(sName));
		}
		oCntr.save(oSes);
		States oStts = new States(oSes, oCntr.getDocument());
		oStts.save(oSes);
		return oCntr;
	}

	private class CountryNameComparator implements Comparator<Country> {
		public int compare(Country c1, Country c2) {
			return c1.getString("es").compareTo(c2.getString("es"));
		}
	}

	private static CountryNameComparator oCntrCmp = new Countries().new CountryNameComparator();

	public static Collection<Country> list() {
		if (aCntrs==null) {
			AtrilSession oSes = DAO.getAdminSession("Countries");
			Dms oDms = oSes.getDms();
			SortableList<Document> oLst = (SortableList<Document>) oDms.query("Country");
			aCntrs = new ArrayList<Country>();
			for (Document d : oLst) {
				Country c = new Country();
				c.setDocument(oDms.getDocument(d.id()));
				aCntrs.add(c);
			} // next
			oSes.disconnect();
			oSes.close();		
			Collections.sort(aCntrs, oCntrCmp);
		}
		return aCntrs;
	}
	
}
