package com.zesped.model;

import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.zesped.model.BaseModelObject;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public class Cities extends BaseModelObject {

	private static HashMap<String, Collection<City>> oCache = new HashMap<String, Collection<City>>();
	public Cities() {
		super("Cities");
	}

	public Cities(Document d) {
		super(d);
	}

	public Cities(AtrilSession oSes, Document oParent) {
		super("Cities");
		newDocument(oSes, oParent);
	}

	@Override
	public Attr[] attributes() {
		return null;
	}

	public State getState(AtrilSession oSes) {
		return new State(oSes.getDms(), getDocument().parents().get(0).id());
	}

	public City create(AtrilSession oSes, String sName, String sCityId, String sZipcode)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, NotYetConnectedException, DmsException {
		City oCity = new City(oSes, getDocument());
		oCity.put("state_code", getState(oSes).getCode());
		oCity.put("name", sName);
		oCity.put("city_id", sCityId);
		oCity.put("zipcode", sZipcode);
		oCity.save(oSes);
		return oCity;
	}
	
	public Collection<City> list(AtrilSession oSes) {
		Dms oDms = oSes.getDms();
		State oStte = getState(oSes);
		if (oCache.containsKey(oStte.getCode())) {
			return oCache.get(oStte.getCode());
		} else {
			ArrayList<City> aCities = new ArrayList<City>();
			for (Document d : getDocument().children()) {
				aCities.add(new City(oDms.getDocument(d.id())));
			}
			Collections.sort(aCities, oCtyCmp);
			oCache.put(oStte.getCode(), aCities);
			return aCities;			
		}
	}

	public void clearCache(String sStateId) {
		if (null==sStateId)
			oCache.clear();
		else
			oCache.remove(sStateId);
	}

	private class CityComparator implements Comparator<City> {
		public int compare(City s1, City s2) {
			return s1.getName().compareTo(s2.getName());
		}
	}

	private static CityComparator oCtyCmp = new Cities().new CityComparator();	
}
