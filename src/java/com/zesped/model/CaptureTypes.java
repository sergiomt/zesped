package com.zesped.model;

import java.util.ArrayList;
import java.util.Collection;

import com.zesped.DAO;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;
import es.ipsa.atril.sec.user.AuthorizationManager;
import es.ipsa.atril.sec.user.DocumentRights;
import es.ipsa.atril.sec.user.RightsFactory;

public class CaptureTypes extends BaseModelObject {

private static final long serialVersionUID = 1L;
	
	private static final Attr[] aAttrs = new Attr[] { };

	public CaptureTypes() {
		super("CaptureTypes");
	}

	public CaptureTypes(Document d) {
		super(d);
	}
	
	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	public CaptureType create(AtrilSession oSes, String sMimeType, boolean bMultiPage, boolean bMultiSheet,
							  boolean bSign, boolean bServerSign, String sName, String sDescripcion) {
		CaptureType oCpt = new CaptureType();
		Dms oDms = oSes.getDms();
		Document oDoc = oDms.newDocument(oDms.getDocumentType(oCpt.getTypeName()), getDocument());
		oDoc.attribute("name").set(sName);
		oDoc.attribute("description").set(sDescripcion);
		oDoc.attribute("MimeType").set(sMimeType);
		oDoc.attribute("MultiPageItem").set(bMultiPage ? 1 : 0);
		oDoc.attribute("MultiSheetDocument").set(bMultiSheet ? 1 : 0);
		oDoc.attribute("Sign").set(bSign ? 1 : 0);
		oDoc.attribute("SignInClient").set(bSign ? 1 : 0);
		oDoc.attribute("SignInServer").set(bServerSign ? 1 : 0);
		oDoc.attribute("IsPDFA").set(0);
		oDoc.attribute("XMPMetadataStructure").set("");
		oDoc.attribute("bitsDepth").set(0);
		oDoc.save("");
		oCpt.setDocument(oDoc);
		oDoc = oDms.newDocument(oDms.getDocumentType("Fields"), oCpt.getDocument());
		oDoc.save("");
		return oCpt;
	}
	
	public static CaptureTypes top(AtrilSession oSes) throws ElementNotFoundException {
		Document r = oSes.getDms().getRootDocument();
		CaptureTypes t = new CaptureTypes();
		for (Document d : r.children()) {
			if (d.type().name().equals(t.getTypeName())) {
				t.setDocument(oSes.getDms().getDocument(d.id()));
				break;
			}
		} // next
		if (t.getDocument()==null) throw new ElementNotFoundException(t.getTypeName()+" document not found");
		return t;
	}

	public static Collection<CaptureType> list() {
		ArrayList<CaptureType> aCtps = new ArrayList<CaptureType>();
		AtrilSession oSes = DAO.getAdminSession("CaptureTypes");
		for (Document d : top(oSes).getDocument().children()) {
			if (d.type().name().equals("CaptureType")) {
				CaptureType c = new CaptureType();
				c.setDocument(d);
				aCtps.add(c);										
			}
		}
		oSes.disconnect();
		oSes.close();		
		return aCtps;
	}	

	public static CaptureType seek(AtrilSession oSes, String sName) throws ElementNotFoundException {
		Dms oDms = oSes.getDms();
		SortableList<Document> oLst = oDms.query("CaptureType$name='"+sName+"'");
		if (oLst.isEmpty())
			throw new ElementNotFoundException("CaptureType "+sName+" not found");
		return new CaptureType(oDms.getDocument(oLst.get(0).id()));
	}

	public void grantAll(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
		AuthorizationManager oAum = oSes.getAuthorizationManager();
		Dms oDms = oSes.getDms();
		DocumentRights oGrt = RightsFactory.getDocumentRightsAllGrant();
		ArrayList<User> aUsrs = Users.top(oSes).list(oSes);
		Document oCpts = getDocument();
		for (User u : aUsrs) {
			String sNick = u.getNickName();
			if (!sNick.equals("admin")) {
				es.ipsa.atril.sec.user.User oUsr = oAum.getUser(sNick);
				oAum.setDocumentRights(oUsr, oCpts, oGrt);
				for (Document t : oCpts.children()) {
					oAum.setDocumentRights(oUsr, oDms.getDocument(t.id()), oGrt);
					for (Document f : t.children()) {
						oAum.setDocumentRights(oUsr, oDms.getDocument(f.id()), oGrt);
						for (Document s : f.children()) {
							oAum.setDocumentRights(oUsr, oDms.getDocument(s.id()), oGrt);							
						} // next
					} // next
				} // next
			} // fi
		} // next
	}
	
}
