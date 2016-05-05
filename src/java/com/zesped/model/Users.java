package com.zesped.model;

import java.util.ArrayList;

import com.zesped.Log;
import com.zesped.model.BaseModelObject;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Users extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	public Users() {
		super("Users");
	}

	@Override
	public Attr[] attributes() {
		return null;
	}

	public static Users top(AtrilSession oSes) throws ElementNotFoundException {
		  Log.out.debug("Begin com.zesped.model.Users.top()");
		  Zesped z = Zesped.top(oSes);
		  Users u = new Users();
		  for (Document d : z.getDocument().children()) {
			  if (d.type().name().equals(u.getTypeName())) {
				  u.setDocument(oSes.getDms().getDocument(d.id()));
				  break;
			  }
		  } // next
		  if (u.getDocument()==null) throw new ElementNotFoundException(u.getTypeName()+" document not found");
		  Log.out.debug("End com.zesped.model.Users.top() : " + u);
		  return u;
    }	

	public ArrayList<User> list(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		ArrayList<User> aUsrs = new ArrayList<User>();
		for (Document d : getDocument().children())
			if (d.type().name().equals("User"))
				aUsrs.add(new User(oSes, d.id()));
		return aUsrs;
	}
	
}
