package com.zesped.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Orders extends BaseCustomerAccountFolder {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] { };

	public Orders() {
		super("Orders");
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public int count() {
		int c = 0;
		for (Document d : getDocument().children())
			if (d.type().name().equals("Order")) c++;
		return c;
	}
	
	private class OrderDateComparator implements Comparator<Order> {
		public int compare(Order c1, Order c2) {
			return c2.getOrderDate().compareTo(c1.getOrderDate());
		}
	}
	
	private static OrderDateComparator oOrdCmp = new Orders().new OrderDateComparator();
	
	public ArrayList<Order> list(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		ArrayList<Order> aOrders = new ArrayList<Order>();
		for (Document d : getDocument().children()) {
			Order oOrd = new Order();
			oOrd.load(oSes, d.id());
			aOrders.add(oOrd);
		}
		Collections.sort(aOrders, oOrdCmp);
		return aOrders;
	}
}
