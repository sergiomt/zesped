package com.zesped.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class CaptureType extends BaseModelObject {

private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("description",DataType.STRING,false,false,null),
    	new Attr("name",DataType.STRING,false,false,null),
    	new Attr("MimeType",DataType.STRING,false,false,null),
    	new Attr("MultiPageItem",DataType.NUMBER,false,false,null),
    	new Attr("MultiSheetDocument",DataType.NUMBER,false,false,null),
    	new Attr("Sign",DataType.NUMBER,false,false,null),
    	new Attr("SignInClient",DataType.NUMBER,false,false,null),
    	new Attr("SignInServer",DataType.NUMBER,false,false,null),
    	new Attr("IsPDFA",DataType.NUMBER,false,false,null),
    	new Attr("XMPMetadataStructure",DataType.STRING,false,false,null),
    	new Attr("bitsDepth",DataType.NUMBER,false,false,null)
    };

	public CaptureType() {
		super("CaptureType");
	}

	public CaptureType(Document d) {
		super("CaptureType");
		setDocument(d);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public String description() {
		return getString("description");
	}

	public String mimeType() {
		return getString("MimeType");
	}
	
	public String name() {
		return getString("name");
	}

	public boolean sign() {
		if (isNull("Sign"))
			return false;
		else
			return getLong("Sign")==1l;
	}

	public boolean signInClient() {
		if (isNull("SignInClient"))
			return false;
		else
			return getLong("SignInClient")==1l;
	}
	
	public boolean signInServer() {
		if (isNull("SignInServer"))
			return false;
		else
			return getLong("SignInServer")==1l;
	}

	public boolean multiPageItem() {
		if (isNull("MultiPageItem"))
			return false;
		else
			return getLong("MultiPageItem")==1l;		
	}

	public void multiPageItem(boolean mpi) {
		put("MultiPageItem", new Long(mpi ? 1l : 0l));		
	}
	
	public boolean multiSheetDocument() {
		if (isNull("MultiSheetDocument"))
			return false;
		else
			return getLong("MultiSheetDocument")==1l;		
	}

	public void multiSheetDocument(boolean msd) {
		put("MultiSheetDocument", new Long(msd ? 1l : 0l));		
	}
	
	public Fields fields(AtrilSession oSes) {
		Dms oDms = oSes.getDms();
		for (Document d : getDocument().children())
			if (d.type().name().equals("Fields"))
				return new Fields(oDms.getDocument(d.id()));
		throw new ElementNotFoundException("Fields not found");
	}

	public Collection<Scanner> scanners (AtrilSession oSes)
		throws NotEnoughRightsException, DmsException {
		Dms oDms = oSes.getDms();
		ArrayList<Scanner> aScanners = new ArrayList<Scanner>();
		for (Document d : getDocument().children())
			if (d.type().name().equals("Scanner"))
				aScanners.add(new Scanner(oDms.getDocument(d.id())));
		return aScanners;
	}

	public Scanner seek (AtrilSession oSes, String sScannerName)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException  {
		Dms oDms = oSes.getDms();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("Scanner")) {
				Scanner s = new Scanner(oDms.getDocument(d.id()));
				if (s.name().equals(sScannerName))
					return s;
			}
		}
		throw new ElementNotFoundException("Scanner "+sScannerName+" not found");
	}
	
	public Scanner addScanner(AtrilSession oSes, String sName, InputStream oConfigProperties)
		throws ElementNotFoundException, IOException {
		Scanner oScr = new Scanner();
		Dms oDms = oSes.getDms();
		Document oDoc = oDms.newDocument(oDms.getDocumentType(oScr.getTypeName()), getDocument());
		oDoc.attribute("name").set(sName);
		oDoc.save("");
		oScr.setDocument(oDoc);
		oScr.insertContentFromInputStream(oSes, oConfigProperties, sName+".properties");
		oScr.save(oSes);
		return oScr;
	}
}