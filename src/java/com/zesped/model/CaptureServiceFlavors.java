package com.zesped.model;

import java.util.HashMap;

import com.zesped.DAO;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class CaptureServiceFlavors {

	private static HashMap<String, CaptureServiceFlavor> oServFlvrs = null;
	
	public static CaptureServiceFlavor getCaptureServiceFlavor(String sCaptureServiceFlavorId)
		throws ElementNotFoundException,NullPointerException {
		if (sCaptureServiceFlavorId==null) throw new NullPointerException("CaptureServiceFlavor.getCaptureServiceFlavor() CaptureService Flavor Id cannot be null");
		if (sCaptureServiceFlavorId.length()==0) throw new NullPointerException("CaptureServiceFlavor.getCaptureServiceFlavor() CaptureService Flavor Id cannot be empty");
		CaptureServiceFlavor oSrvFlv;
		if (oServFlvrs==null) {
			oServFlvrs = new HashMap<String, CaptureServiceFlavor>();
			AtrilSession oSes = DAO.getAdminSession("CaptureServiceFlavors");
			Dms oDms = oSes.getDms();
			Zesped z = Zesped.top(oSes);
			for (Document d : z.getDocument().children()) {
				if (d.type().name().equals("CaptureServiceFlavor")) {
					oSrvFlv = new CaptureServiceFlavor(oDms.getDocument(d.id()));
					oServFlvrs.put(oSrvFlv.uid(), oSrvFlv);
				}
			}
			oSes.disconnect();
			oSes.close();
		}
		oSrvFlv = oServFlvrs.get(sCaptureServiceFlavorId);
		if (null==oSrvFlv) throw new ElementNotFoundException("No CaptureService Flavor was found with Id. "+sCaptureServiceFlavorId);
		return oSrvFlv;
	}
	
	public static CaptureServiceFlavor getCaptureServiceFlavor(String sCaptureService, String sFlavor)
		throws ElementNotFoundException,NullPointerException {
		if (sCaptureService==null) throw new NullPointerException("CaptureServiceFlavor.getCaptureServiceFlavor() CaptureService cannot be null");
		if (sFlavor==null) throw new NullPointerException("CaptureServiceFlavor.getCaptureServiceFlavor() Flavor cannot be null");
		return getCaptureServiceFlavor((sCaptureService+sFlavor).toUpperCase());
	}
}
