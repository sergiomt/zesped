package com.zesped.model;

public class ForeignKey {

	public Class<? extends BaseModelObject> doctype;
	public String attrib;
	
	public ForeignKey (Class<? extends BaseModelObject> oClass, String sAttr) {
		doctype = oClass;
		attrib = sAttr;
	}
}