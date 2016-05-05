package com.zesped.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;

import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

@SuppressWarnings("serial")
public class Clients extends BaseCollectionObject<Client> {

	public Clients() {
		super("Clients", Client.class);
	}

	private class ClientNameComparator implements Comparator<Client> {
		public int compare(Client c1, Client c2) {
			return c1.getBusinessName().compareTo(c2.getBusinessName());
		}
	}
	
	private static ClientNameComparator oCcnCmp = new Clients().new ClientNameComparator();
	
	@Override
	public ArrayList<Client> list(AtrilSession oSes) throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		ArrayList<Client> aClients = super.list(oSes);
		Collections.sort(aClients, oCcnCmp);
		return aClients;
	}

	@Override
	public Attr[] attributes() {
		return null;
	}

	public Client seek(AtrilSession oSes, String sClient) throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		if (sClient==null) throw new ElementNotFoundException("Client may not be null");
		if (sClient.length()==0) throw new ElementNotFoundException("Client may not be empty");
		final String sSanitizedBusinessName =  Gadgets.removeChars(sClient, "\"*?").trim().toUpperCase();;
		for (Client c : super.list(oSes)) {
			if (c.getBusinessName().equals(sSanitizedBusinessName) || c.getBusinessName().equalsIgnoreCase(sClient) || c.getId().equals(sClient) || c.getTaxId().equalsIgnoreCase(sClient))
				return c;
		}
		throw new ElementNotFoundException("Client "+sClient+"not found");
	}

	public Client create(AtrilSession oSes, String sUserUuid, String sBusinessName, String sTaxId) {
		Client clnt = new Client(oSes, this);
		clnt.setBusinessName(sBusinessName);
		clnt.setTaxId(sTaxId);
		clnt.setActive(true);
		clnt.save(oSes);		
		DAO.log(oSes, clnt.getDocument(), Client.class, "CREATE CLIENT", AtrilEvent.Level.INFO, "");
		return clnt;		
	}
	
}
