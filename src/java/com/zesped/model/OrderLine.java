package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class OrderLine extends BaseModelObject {

	private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("line_num",DataType.NUMBER,true,true,null),
    	new Attr("product_id",DataType.STRING,true,true,new ForeignKey(Product.class, "product_id")),
    	new Attr("product_name",DataType.STRING,true,true,null),
    	new Attr("base_price",DataType.STRING,true,false,null),
    	new Attr("taxes",DataType.STRING,true,false,null),
    	new Attr("taxespct",DataType.STRING,true,false,null),
    	new Attr("subtotal_price",DataType.STRING,true,false,null),
    	new Attr("currency",DataType.STRING,true,false,null)
	};
    
	public OrderLine() {
		super("OrderLine");
	}

	public OrderLine(AtrilSession oSes, Order oOrd) {
		super(oSes.getDms().newDocument(oSes.getDms().getDocumentType("OrderLine"), oOrd.getDocument()));
	}

	public OrderLine(Document oDoc) {
		super(oDoc);
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}	
}
