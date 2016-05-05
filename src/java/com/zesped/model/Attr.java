package com.zesped.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.ipsa.atril.doc.user.DataType;

public class Attr {

	public String name;
	public boolean unique;
	public boolean required;
	public ForeignKey fk;
	public CustomConstraint cc;
	public DataType dataType;

	private static final SimpleDateFormat oDtFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS:s");
	
	public Attr(String sName, DataType oDataType) {
		name = sName;
		dataType = oDataType;
		unique = false;
		required = false;
		fk = null;
		cc = null;
	}
	
	public Attr(String sName, DataType oDataType, boolean bUnique, boolean bRequired) {
		name = sName;
		dataType = oDataType;
		unique = bUnique;
		required = bRequired;
		fk = null;
		cc = null;
	}

	public Attr(String sName, DataType oDataType, boolean bUnique, boolean bRequired, ForeignKey oForeignKey) {
		name = sName;
		dataType = oDataType;
		unique = bUnique;
		required = bRequired;
		fk = oForeignKey;
		cc = null;
	}

	public Attr(String sName, DataType oDataType, boolean bUnique, boolean bRequired, ForeignKey oForeignKey, CustomConstraint oCustomConstraint) {
		name = sName;
		dataType = oDataType;
		unique = bUnique;
		required = bRequired;
		fk = oForeignKey;
		cc = oCustomConstraint;
	}
	
	public static Date toDate(String sTimestamp) throws ParseException {
		return oDtFmt.parse(sTimestamp.replace('T', ' '));
	}
	
}