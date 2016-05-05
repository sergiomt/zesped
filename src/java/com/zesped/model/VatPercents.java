package com.zesped.model;

import java.math.BigDecimal;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.zesped.DAO;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

@SuppressWarnings("serial")
public class VatPercents extends BaseModelObject {

	private static ArrayList<VatPercent> oAllPercents = null;
	private static ArrayList<VatPercent> oActivePercents = null;

	public VatPercents() {
		super("VatPercents");
	}

	@Override
	public Attr[] attributes() {
		return null;
	}

	private class VatComparator implements Comparator<VatPercent> {
		public int compare(VatPercent p1, VatPercent p2) {
			return p1.getValue().compareTo(p2.getValue());
		}
	}

	private static VatComparator oVatCmp = new VatPercents().new VatComparator();
	
	public static Collection<VatPercent> listAll() {
		if (oAllPercents==null) {
			AtrilSession oSes = DAO.getAdminSession("VatPercents");
			Dms oDms = oSes.getDms();
			Document p = VatPercents.top(oSes).getDocument();
			oAllPercents = new ArrayList<VatPercent>();
			for (Document d : p.children()) {
				oAllPercents.add(new VatPercent(oDms.getDocument(d.id())));
			}
			oSes.disconnect();
			oSes.close();
			Collections.sort(oAllPercents, oVatCmp);
		}
		return oAllPercents;
	}

	public static Collection<VatPercent> listActive() {
		if (oActivePercents==null) {
			AtrilSession oSes = DAO.getAdminSession("VatPercents");
			Dms oDms = oSes.getDms();
			Document p = VatPercents.top(oSes).getDocument();
			oActivePercents = new ArrayList<VatPercent>();
			for (Document d : p.children()) {
				VatPercent v = new VatPercent(oDms.getDocument(d.id()));
				if (v.isActive()) oActivePercents.add(v);
			}
			oSes.disconnect();
			oSes.close();
			Collections.sort(oActivePercents, oVatCmp);
		}
		return oActivePercents;
	}

	public static VatPercent create(AtrilSession oSes, BigDecimal oPct, String sDescription)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, NotYetConnectedException, DmsException {
		VatPercent v = new VatPercent();
		v.newDocument(oSes, VatPercents.top(oSes).getDocument());
		v.setValue(oPct);
		v.setDescription(sDescription);
		v.activate();
		v.save(oSes);
		return v;
	}

	public static VatPercents top(AtrilSession oSes) throws ElementNotFoundException, NotEnoughRightsException {
		  Document z = Zesped.top(oSes).getDocument();
		  VatPercents v = new VatPercents();
		  for (Document d : z.children()) {
			  if (d.type().name().equals(v.getTypeName())) {
				  v.setDocument(oSes.getDms().getDocument(d.id()));
				  break;
			  }
		  } // next
		  if (v.getDocument()==null) throw new ElementNotFoundException(v.getTypeName()+" document not found");
		  return v;      
		}	
}
