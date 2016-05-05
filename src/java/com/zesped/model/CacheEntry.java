package com.zesped.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Date;
import java.util.HashMap;

import com.knowgate.jdc.JDCConnection;

public class CacheEntry {

	private String sCid;
	private Date dtMod;
	private Object oVal;
	private boolean bFound;
	
	public CacheEntry() {
		sCid = null;
		dtMod = null;
		oVal = null;
	}

	public CacheEntry(JDCConnection oCon, String sKey) throws ClassNotFoundException, SQLException, IOException {
		sCid = sKey;
		read (oCon);
	}

	public boolean found() {
		return bFound;
	}
	
	public Date getDateModified() {
		return dtMod;
	}

	public String getKey() {
		return sCid;
	}

	public void setKey(String sKey) {
		sCid = sKey;
	}
	
	public String getString() {
		return (String) oVal;
	}
	
	public HashMap<String,Object> getMap() {
		return (HashMap<String,Object>) oVal;
	}

	public Object getValue() {
		return oVal;
	}
	
	public void setValue(Object oObj) {
		oVal = oObj;
	}

	public boolean read (JDCConnection oCon)
		throws SQLException, IOException, ClassNotFoundException, InvalidClassException {
		PreparedStatement oStm = oCon.prepareStatement("SELECT dts,val FROM Cache WHERE cid=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		oStm.setString(1, sCid);
		ResultSet oRst = oStm.executeQuery();
		bFound = oRst.next();
		if (bFound) {
			dtMod = new Date(oRst.getTimestamp(1).getTime());
		    ObjectInputStream oOis = new ObjectInputStream(oRst.getBinaryStream(2));
		    oVal = oOis.readObject();
		    oOis.close();
		}
		oRst.close();
		oStm.close();
		return bFound;
	}

	public void delete (JDCConnection oCon)
		throws SQLException {
		PreparedStatement oDlt = oCon.prepareStatement("DELETE FROM Cache WHERE cid=?");
		oDlt.setString(1, sCid);
		oDlt.executeUpdate();
		oDlt.close();
	}
	
	public void store (JDCConnection oCon)
		throws SQLException, IOException {

		delete(oCon);

		ByteArrayOutputStream oBos = new ByteArrayOutputStream();
		ObjectOutputStream oOos = new ObjectOutputStream(oBos);
		oOos.writeObject(oVal);
		ByteArrayInputStream oBis = new ByteArrayInputStream(oBos.toByteArray());
		
		PreparedStatement oIns = oCon.prepareStatement("INSERT INTO Cache (cid,dts,val) VALUES (?,?,?)");
		oIns.setString(1, sCid);
		oIns.setTimestamp(2, new Timestamp(new Date().getTime()));
		oIns.setBinaryStream(3, oBis);
		oIns.executeUpdate();
		oIns.close();
		
		oBis.close();
		oOos.close();
		oBos.close();
	}
}
