package com.zesped.model;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class ExistsDocument implements CustomConstraint {
	public boolean check (AtrilSession oSes, DocumentIndexer oIdx, BaseModelObject oObj) {
		boolean bFound;
		if (oObj.getStringNull("related_document","").length()>0) {
			try {
				Dms oDms = oSes.getDms();
				oDms.getDocument(oObj.getString("related_document"));
				bFound=true;
			} catch (ElementNotFoundException enfe) {
				bFound=false;
			}
		} else {
			bFound=true;
		}
		return bFound;
	}
	
	public static ExistsDocument INSTANCE = new ExistsDocument();
}
