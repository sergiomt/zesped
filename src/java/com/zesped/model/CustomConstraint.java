package com.zesped.model;

import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.sec.authentication.AtrilSession;

public interface CustomConstraint {

	public boolean check (AtrilSession oSes, DocumentIndexer oIdx, BaseModelObject oObj);

}
