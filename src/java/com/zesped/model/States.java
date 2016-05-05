package com.zesped.model;

import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public class States extends BaseModelObject {

	private static HashMap<String, Collection<State>> oCache = new HashMap<String, Collection<State>>();
	
	public States() {
		super("States");
	}

	public States(Document d) {
		super(d);
	}

	public States(AtrilSession oSes, Document oParent) {
		super("States");
		newDocument(oSes, oParent);
	}

	@Override
	public Attr[] attributes() {
		return null;
	}

	public Country getCountry(AtrilSession oSes) {
		return new Country(oSes.getDms(), getDocument().parents().get(0).id());
	}

	public State getState(AtrilSession oSes, String sCode) {
		Country oCntr = getCountry(oSes);
		if (oCache.containsKey(oCntr.getIsoCode())) {
			for (State s : oCache.get(oCntr.getIsoCode()))
				if (s.getCode().equals(sCode)) return s;
			return null;
		} else {
		  SortableList<Document> oLst = (SortableList<Document>) oSes.getDms().query("State & (($country_id='" + oCntr.getIsoCode() +"') & ($code='" + sCode + "'))");
		  if (oLst.isEmpty())
			return null;
		  else {
			return new State(oSes.getDms().getDocument(oLst.get(0).id()));
		  }
	   }
	}

	public State create(AtrilSession oSes, String sCode, String sName)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, NotYetConnectedException, DmsException {
		State oStte = new State(oSes, getDocument());
		oStte.put("country_id", getCountry(oSes).getIsoCode());
		oStte.put("code", sCode);
		oStte.put("name", sName);
		oStte.save(oSes);
		Cities oCtts = new Cities(oSes, oStte.getDocument());
		oCtts.save(oSes);
		return oStte;
	}
	
	public Collection<State> list(AtrilSession oSes) {
		Dms oDms = oSes.getDms();
		Country oCntr = getCountry(oSes);
		if (oCache.containsKey(oCntr.getIsoCode())) {
			return oCache.get(oCntr.getIsoCode());
		} else {
			ArrayList<State> aStates = new ArrayList<State>();
			for (Document d : getDocument().children()) {
				aStates.add(new State(oDms.getDocument(d.id())));
			}
			Collections.sort(aStates, oSttCmp);
			oCache.put(oCntr.getIsoCode(), aStates);
			return aStates;			
		}
	}

	public void clearCache(String sCountryId) {
		if (null==sCountryId)
			oCache.clear();
		else
			oCache.remove(sCountryId);
	}
	
	private class StateComparator implements Comparator<State> {
		public int compare(State s1, State s2) {
			return s1.getName().compareTo(s2.getName());
		}
	}

	private static StateComparator oSttCmp = new States().new StateComparator();
	
}
