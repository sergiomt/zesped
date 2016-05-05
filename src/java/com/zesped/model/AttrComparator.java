package com.zesped.model;

import java.util.Comparator;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Document;

public class AttrComparator implements Comparator<BaseModelObject> {

	private String sAtr;
	private boolean bAsc;
	
	public AttrComparator(String sAttrName, boolean bAscending) {
		sAtr = sAttrName;
		bAsc = bAscending;
	}

	@Override
	public int compare(BaseModelObject oObj1, BaseModelObject oObj2) {
		Document oDoc1 = oObj1.getDocument();
		Document oDoc2 = oObj2.getDocument();
		DataType oTyp = oDoc1.attribute(sAtr).type().getDataType();
		Comparable oAtr1=null, oAtr2=null;
		switch (oTyp) {
			case STRING:
				if (oDoc1.attribute(sAtr).isEmpty())
					oAtr1 = null;
				else
					oAtr1 = oDoc1.attribute(sAtr).toString();
				if (oDoc2.attribute(sAtr).isEmpty())
					oAtr2 = null;
				else
					oAtr2 = oDoc2.attribute(sAtr).toString();
				break;
			case DATE:
			case DATE_TIME:
				if (oDoc1.attribute(sAtr).isEmpty())
					oAtr1 = null;
				else
					oAtr1 = oObj1.getDate(sAtr);
				if (oDoc2.attribute(sAtr).isEmpty())
					oAtr2 = null;
				else
					oAtr2 = oObj2.getDate(sAtr);
				break;
			case NUMBER:
				if (oDoc1.attribute(sAtr).isEmpty())
					oAtr1 = null;
				else
					oAtr1 = oDoc1.attribute(sAtr).toBigDecimal();
				if (oDoc2.attribute(sAtr).isEmpty())
					oAtr2 = null;
				else
					oAtr2 = oDoc2.attribute(sAtr).toBigDecimal();
				break;
		}
		if (oAtr1==null && oAtr2==null)
			return 0;
		else if (oAtr1==null)
			return 1;
		else if (oAtr2==null)
			return -1;
		else
			return oAtr1.compareTo(oAtr2) * (bAsc ? 1 : -1);
	}

}
