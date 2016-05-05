package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;

public class Document extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("amount",DataType.NUMBER,false,false,null),
    	new Attr("cheque_account",DataType.STRING,false,false,null),
    	new Attr("cheque_number",DataType.STRING,false,false,null),
    	new Attr("codeline",DataType.STRING,false,false,null),
    	new Attr("endorsement",DataType.STRING,false,false,null),
    	new Attr("incident_cause",DataType.STRING,false,false,null),
    	new Attr("issue",DataType.STRING,false,false,null),
    	new Attr("observations",DataType.STRING,false,false,null),
    	new Attr("status_bo",DataType.STRING,false,false,null),
    	new Attr("transit_route",DataType.STRING,false,false,null)
    };

	public Document() {
		super("Document");
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

}
