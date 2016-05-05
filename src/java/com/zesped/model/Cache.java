package com.zesped.model;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.knowgate.jdc.JDCConnection;
import com.zesped.DAO;

public class Cache {

	public Cache() { }

	public static void clear() throws SQLException {
		JDCConnection oCon = DAO.getConnection("Cache");
		Statement oStm = oCon.createStatement();
		oStm.execute("TRUNCATE TABLE Cache");
		oCon.close("Cache");
	}

	public static CacheEntry getEntry(String sKey)
		throws SQLException, ClassNotFoundException, IOException {
		CacheEntry oRetVal;
		JDCConnection oCon = DAO.getConnection("Cache");
		oRetVal = new CacheEntry(oCon, sKey);
		if (!oRetVal.found()) oRetVal = null;
		oCon.close("Cache");
		return oRetVal;
	}

	public static String getEntryString(String sKey)
		throws SQLException, ClassNotFoundException, IOException {
		CacheEntry oEntry = getEntry(sKey);
		if (oEntry!=null)
			return oEntry.getString();
		else
			return null;
	}

	public static HashMap<String,Object> getEntryMap(String sKey)
		throws SQLException, ClassNotFoundException, IOException {
		CacheEntry oEntry = getEntry(sKey);
		if (oEntry!=null)
			return oEntry.getMap();
		else
			return null;
	}

	public static Object getObject(String sKey)
		throws SQLException, ClassNotFoundException, IOException {
		CacheEntry oEntry = getEntry(sKey);
		if (oEntry!=null)
			return oEntry.getValue();
		else
			return null;
	}

	public static void putEntry(String sKey, Object oVal)
		throws SQLException, IOException  {
		CacheEntry oEntry = new CacheEntry();
		JDCConnection oCon = DAO.getConnection("Cache");
		oCon.setAutoCommit(true);
		oEntry.setKey(sKey);
		oEntry.setValue(oVal);
		oEntry.store(oCon);
		oCon.close("Cache");
	}
	
	public static void deleteEntry(String sKey) throws SQLException {
		CacheEntry oEntry = new CacheEntry();
		JDCConnection oCon = DAO.getConnection("Cache");
		oCon.setAutoCommit(true);
		oEntry.setKey(sKey);
		oEntry.delete(oCon);
		oCon.close("Cache");
	}
}
