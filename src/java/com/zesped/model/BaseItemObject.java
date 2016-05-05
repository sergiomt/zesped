package com.zesped.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import com.knowgate.dfs.StreamPipe;
import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.Item;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public abstract class BaseItemObject extends BaseModelObject {

	public BaseItemObject(String sType) {
		super(sType);
	}
	
	public BaseItemObject(Document c) {
		super(c);
	}

	public BaseItemObject(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}
	
	@Override
	public boolean isItem() {
		return true;
	};

	public Item item() {
		return getDocument().item();
	}

	public String mimeType() {
		return getDocument().item().mimeType();
	}

	public void mimeType(String sMimeType) {
		getDocument().item().mimeType(sMimeType);
	}

	public int number () {
		return new BigDecimal(get("page").toString()).intValue();
	}

	public void insertContentFromFile(AtrilSession oSes, File oFile)
		throws ElementNotFoundException, IOException {
		String sItemName = Gadgets.removeChars(oFile.getName(), "\"\\/?*:|<>;&");
		Item item = getDocument().item();
		item.setName(sItemName);
		item.insertContentFrom(oFile);
		Log.out.debug(getClass().getName()+".insertContentFromFile("+oFile.getName()+")");
		DAO.log(oSes, getDocument(), getClass(), "INSERT ITEM", AtrilEvent.Level.INFO, sItemName);
	}
		
	public void insertContentFromInputStream(AtrilSession oSes, InputStream instrm, String sItemName)
		throws ElementNotFoundException, IOException {
		Item item = getDocument().item();
		sItemName = Gadgets.removeChars(sItemName, "\"\\/?*:|<>;&");
		item.setName(sItemName);
		OutputStream outstrm = item.getOutputStream();
		StreamPipe pipe = new StreamPipe();
		pipe.between(instrm, outstrm);
		outstrm.close();
		Log.out.debug(getClass().getName()+".insertContentFromInputStream("+sItemName+")");
		DAO.log(oSes, getDocument(), getClass(), "INSERT ITEM", AtrilEvent.Level.INFO, sItemName);
	}
	
	public static final String TYPE_JPEG = "image/jpeg";
	public static final String TYPE_PDF = "application/pdf";
}
