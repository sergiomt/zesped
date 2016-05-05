package com.zesped.model;

import es.ipsa.atril.doc.user.DataType;

public class DeviceInformationType extends BaseModelObject {

	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("brand",DataType.STRING,false,false,null),
    	new Attr("model",DataType.STRING,false,false,null),
    	new Attr("serialnumber",DataType.STRING,false,false,null)
    };

	public DeviceInformationType() {
		super("DeviceInformationType");
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

}
