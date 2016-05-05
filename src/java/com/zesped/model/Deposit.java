package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;

public class Deposit extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("commitdate",DataType.DATE,false,false,null),
    	new Attr("deposit_id",DataType.STRING,false,false,null),
    	new Attr("endorsement",DataType.STRING,false,false,null),
    	new Attr("number_of_cheques",DataType.NUMBER,false,false,null),
    	new Attr("status",DataType.STRING,false,false,null),
    	new Attr("total_amount",DataType.NUMBER,false,false,null),
    	new Attr("user",DataType.STRING,false,false,null),
    };

	public Deposit() {
		super("Deposit");
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

}
