package com.zesped.model;

import java.util.ArrayList;

import com.zesped.Log;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

@SuppressWarnings("serial")
public abstract class BaseCollectionObject<T extends BaseModelObject> extends BaseModelObject {

	private Class oClass;
	
	public BaseCollectionObject(String sType, Class oElements) {
		super(sType);
		oClass = oElements;
	}

	public BaseCollectionObject(Document oDoc, Class oElements) {
		super(oDoc);
		oClass = oElements;
	}
	
	public BaseCollectionObject(String sType, Class oElements, Document oDoc) {
		super(sType);
		setDocument(oDoc);
		oClass = oElements;
	}
	
	public ArrayList<T> list(AtrilSession oSes)
		throws InstantiationException, IllegalStateException, IllegalAccessException,
		ElementNotFoundException, NotEnoughRightsException, DmsException {
		Log.out.debug("Begin BaseCollectionObject.list("+getTypeName()+" - "+id()+" get "+oClass.getName()+")");
		ArrayList<T> aEmps = new ArrayList<T>();
		for (Document d : getDocument().children()) {
			T oObj = (T) oClass.newInstance();
			oObj.load(oSes, d.id());
			aEmps.add(oObj);
		}
		Log.out.debug("End BaseCollectionObject.list("+getTypeName()+" - "+id()+") : "+String.valueOf(aEmps.size()));
		return aEmps;
	}

	public int count() throws InstantiationException, IllegalStateException, IllegalAccessException, ElementNotFoundException, NotEnoughRightsException, DmsException {
		return getDocument().children().size();
	}

}