package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;

public class Endorsement extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("endorsement_id",DataType.STRING,false,false,null),
    	new Attr("endorsement_mask",DataType.STRING,false,false,null),
    	new Attr("endorsement_text",DataType.STRING,false,false,null),
    };

	public Endorsement() {
		super("Endorsement");
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

}
