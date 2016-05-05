package com.zesped.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.NotYetConnectedException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.sql.PreparedStatement;

import com.knowgate.dfs.FileSystem;
import com.knowgate.jdc.JDCConnection;
import com.knowgate.misc.Gadgets;
import com.knowgate.misc.NameValuePair;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.NodeList;
import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.AttributeMultiValue;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.DocumentType;
import es.ipsa.atril.doc.user.exceptions.AttributeTypeHasValuesException;
import es.ipsa.atril.doc.user.exceptions.DmsDocumentModificationException;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.doc.volumes.Volume;
import es.ipsa.atril.doc.volumes.VolumeManager;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.documentindexer.exceptions.DocumentIndexerException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;
import es.ipsa.atril.util.date.DateConversions;


@SuppressWarnings("serial")
public abstract class BaseModelObject extends HashMap<String,Object> {

	private String sId;
	private String sType;
	private transient Document oDoc;

	public BaseModelObject(String sDocType) {
		sType = sDocType;
		sId = null;
	}

	public BaseModelObject(Document oAtrilDocument) {
		setDocument(oAtrilDocument);
	}

	public BaseModelObject(Dms oDms, String sDocId)
		throws ElementNotFoundException,NumberFormatException,NullPointerException,IllegalArgumentException {
		Document oDoc = null;
		if (null==sDocId)
			throw new NullPointerException("Document Id cannot be null");
		if (sDocId.length()==0)
			throw new IllegalArgumentException("Document Id cannot be empty");
		try {
			Long.parseLong(sDocId);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException("Document Id must be a long value");
		}
		try {
		  oDoc = oDms.getDocument(sDocId);
		  if (oDoc==null) throw new ElementNotFoundException("Cannot find document "+sDocId);
		} catch (ElementNotFoundException enf) {
			throw new ElementNotFoundException("Element not found "+sDocId);
		}
		if (oDoc!=null) setDocument(oDoc);
	}
	
	public abstract Attr[] attributes();
		
	public boolean isItem() {
		return false;
	};
	
	public String id() throws NullPointerException,IllegalStateException {
		if (oDoc==null)
			if (getId()!=null)
				return getId();
			else
				throw new IllegalStateException(getTypeName()+" document not set before calling id() method");
		else
			return oDoc.id();
	}

	public String getId() {
		return sId;
	}
	
	public void setId(String id)
		throws NullPointerException,IllegalStateException,IllegalArgumentException,NumberFormatException {
		if (id==null)
			throw new NullPointerException(getTypeName()+" Id cannot be null");
		if (id.length()==0)
			throw new IllegalArgumentException(getTypeName()+" Id cannot be empty");
		if (oDoc!=null)
			if (!id.equals(oDoc.id()) && oDoc.id()!=null)
				throw new IllegalStateException(getTypeName()+" cannot change Id to "+id+" for already loaded instance "+oDoc.id());
		try {
			Long.parseLong(id);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException("Document Id must be a long value");
		}
		sId = id;
	}

	public boolean isNull(String sKey) {
		if (containsKey(sKey))
			return get(sKey)==null;
		else
			return true;
	}

	public String getTypeName() {
		return sType;
	}

	public void setTypeName(String sTypeName) {
		sType = sTypeName;
	}
	
	public boolean isArray(String sKey) {
		if (containsKey(sKey)) {
			Object oObj = get(sKey);
			if (oObj==null)
				return false;
			else
				return oObj.getClass().isArray();			
		} else {
			return false;
		}
	}

	public String getString(String sKey) {
		return (String) get(sKey);
	}

	public String[] getStrings(String sKey) {
		if (isNull(sKey))
			return null;
		else if (isArray(sKey))
			return (String[]) get(sKey);
		else
			return new String[]{getString(sKey)};
	}

	public BigDecimal[] getBigDecimals(String sKey) {
		if (isNull(sKey))
			return null;
		else if (isArray(sKey))
			return (BigDecimal[]) get(sKey);
		else
			return new BigDecimal[]{getBigDecimal(sKey)};
	}
	
	public String getStringNull(String sKey) {
		if (isNull(sKey))
			return "";
		else
			return (String) get(sKey); 
	}

	public String getStringNull(String sKey, String sDefaultValue) {
		if (isNull(sKey))
			return sDefaultValue;
		else
			return (String) get(sKey); 
	}
	
	public BigDecimal getBigDecimal(String sKey) {
		return (BigDecimal) get(sKey);
	}

	public Long getLong(String sKey) {
		return (Long) get(sKey);
	}

	public Double getDouble(String sKey) {
		return (Double) get(sKey);
	}

	public Date getDate(String sKey) {
		return (Date) get(sKey);
	}

	public String getCaption() {
		return getDocument().getCaption();
	}

	public Document getDocument() {
		return oDoc;
	}

	protected void newDocument(AtrilSession oSes, Document oParent) {
		Dms oDms = oSes.getDms();
		DocumentType oDoct = oDms.getDocumentType(getTypeName());
		oDoc = oDms.newDocument(oDoct, oParent);
		setTypeName(oDoc.type().name());
		sId = oDoc.id();
		clear();
	} // newDocument
	
	protected void setDocument(Document oDocument) {
		oDoc = oDocument;
		setTypeName(oDoc.type().name());
		sId = oDoc.id();
		clear();
		final Attr[] aAttrs = attributes();
		if (aAttrs!=null) {
			final int nAttrs = aAttrs.length;
			for (int a=0; a<nAttrs; a++) {
				AttributeMultiValue oAttr = oDoc.attribute(aAttrs[a].name);
				try {
					if (oAttr!=null) {
						if (oAttr.count()==1) {
						  switch (oAttr.dataType()) {
							case STRING:
								put(aAttrs[a].name, oAttr.toString());
								break;
							case NUMBER:
								put(aAttrs[a].name, oAttr.toBigDecimal());
								break;
							case DATE:
								put(aAttrs[a].name, oAttr.toDate());
								break;
							case DATE_TIME:
								long lValue;
								try {
									lValue = oAttr.toLong();
									Log.out.info("Got long value "+String.valueOf(lValue));
									if (lValue!=-1l)
										put(aAttrs[a].name, DateConversions.getDateFromIPSATime(lValue));
								} catch (NumberFormatException nfe) {
									String sValue;
									try { sValue = oAttr.toString(); } catch (Exception ignore) { sValue = ""; }
									if (sValue.length()>0)
									  put(aAttrs[a].name, Attr.toDate(sValue));
								}
								break;
							default:
								Log.out.error("Unrecognized data type "+oAttr.dataType().name());
						  }
						} else if (oAttr.count()>1) {
							switch (oAttr.dataType()) {
								case STRING:
									String[] aStrs = new String[oAttr.count()];
									for (int c=0; c<oAttr.count(); c++) aStrs[c] = oAttr.get(c).toString();
									put(aAttrs[a].name, aStrs);
									break;
								case NUMBER:
									BigDecimal[] aDecs = new BigDecimal[oAttr.count()];
									for (int c=0; c<oAttr.count(); c++) aDecs[c] = oAttr.get(c).toBigDecimal();
									put(aAttrs[a].name, aDecs);
									break;
								case DATE:
									Date[] aDats = new Date[oAttr.count()];
									for (int c=0; c<oAttr.count(); c++) aDats[c] = oAttr.get(c).toDate();
									put(aAttrs[a].name, aDats);
									break;
								case DATE_TIME:
									Date[] aDttm = new Date[oAttr.count()];
									for (int c=0; c<oAttr.count(); c++) {
										long lValue;
										try {
											lValue = oAttr.toLong();
											if (lValue!=-1l)				
												aDttm[c] = DateConversions.getDateFromIPSATime(lValue);
											else
												aDttm[c] = null;
										} catch (NumberFormatException nfe) {
											String sValue;
											try {
												sValue = oAttr.toString();
											} catch (Exception ignore) { sValue = ""; }										
											if (sValue.length()>0)				
												aDttm[c] = Attr.toDate(sValue);
											else
												aDttm[c] = null;
										}										
									}
									put(aAttrs[a].name, aDttm);
									break;
								default:
									Log.out.error("Unrecognized data type "+oAttr.dataType().name());
							  }							
						}
					} // fi
				} catch (Exception resumenext) {
					String sAttrValue = "";
					try { sAttrValue = oAttr.toString(); } catch (Exception ignore) { }
					System.out.println(resumenext.getClass().getName()+" "+(oAttr==null ? "" : sAttrValue)+" "+aAttrs[a].name+" "+resumenext.getMessage());
					Log.out.error(resumenext.getClass().getName()+" "+(oAttr==null ? "" : sAttrValue)+" "+aAttrs[a].name+" "+resumenext.getMessage());
				}
			} // next
		} // fi
	} // setDocument

	public String parentId() throws IllegalStateException, ElementNotFoundException {
	  if (oDoc==null) throw new IllegalStateException(getTypeName()+" document not set before calling parentId() method");
	  NodeList<Document> oParents = oDoc.parents();
	  if (oParents.isEmpty()) throw new ElementNotFoundException("Parent not found");
	  return oParents.get(0).id();
	}

	public void load(AtrilSession oSes, String sDocId) throws ElementNotFoundException, NotEnoughRightsException , DmsException {
		Dms oDms = oSes.getDms();
		Document d = oDms.getDocument(sDocId);
		if (null==d) throw new ElementNotFoundException("Document "+sDocId+" not found");
		setDocument(d);
	}

	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException,
		       AttributeTypeHasValuesException, NotYetConnectedException, DmsException, DmsDocumentModificationException {
		if (oDoc==null) throw new IllegalStateException(getTypeName()+" document not set before calling save() method");

		if (DAO.checkConstraints) {
			AtrilSession oAdm = DAO.getAdminSession("checkConstraints");
			Dms oDms = oAdm.getDms();
			DocumentIndexer oIdx = oAdm.getDocumentIndexer();
			final Attr[] aAttrs = attributes();
			final int nAttrs = (aAttrs==null ? 0 : aAttrs.length);
			try {
				for (int a=0; a<nAttrs; a++) {
					if (aAttrs[a].required && isNull(aAttrs[a].name)) throw new NullPointerException(getTypeName()+" attribute "+aAttrs[a].name+" is required");
					if (aAttrs[a].unique && !isNull(aAttrs[a].name)) {
						List<Document> oLst;
						if (aAttrs[a].dataType.equals(DataType.STRING))
						  if (getString(aAttrs[a].name).indexOf("'")<0)
						    oLst = oDms.query(getTypeName()+"$"+aAttrs[a].name+"='"+escape(getString(aAttrs[a].name))+"'");
						  else
							oLst = null;
						else if (aAttrs[a].dataType.equals(DataType.NUMBER))
					      oLst = oDms.query(getTypeName()+"$"+aAttrs[a].name+"="+get(aAttrs[a].name));
						else 
						  oLst = null;
						if (null!=oLst) {
							if (!oLst.isEmpty()) {
								String p = parentId();						
								for (Document d : oLst) {
									String q = d.parents().get(0).id();
									if (p.equals(q) && !d.id().equals(getId()) &&
										d.attribute(aAttrs[a].name).toString().equals(get(aAttrs[a].name))) {
										Log.out.debug(getTypeName()+" attribute "+aAttrs[a].name+" unique constraint violation for value "+get(aAttrs[a].name)+" parent document id. is "+p+" previous document id. is "+d.id()+" current document id. is "+getId());
								    	throw new DmsException(getTypeName()+" attribute "+aAttrs[a].name+" unique constraint violation for value "+get(aAttrs[a].name));
								    } // fi							
								}
							} // fi							
						}
					} // fi (unique)
					if (aAttrs[a].fk!=null && !isNull(aAttrs[a].name)) {
						try {
							BaseModelObject oObj = aAttrs[a].fk.doctype.newInstance();
							if (null==oObj.exists(oAdm, aAttrs[a].fk.attrib, get(aAttrs[a].name).toString())) {
						    	throw new DmsException(getTypeName()+" attribute "+aAttrs[a].name+" foreign key violation "+get(aAttrs[a].name).toString()+" referencing "+oObj.getTypeName()+"."+aAttrs[a].fk.attrib);
							}
						} catch (InstantiationException e) {
							Log.out.error("BaseModelObject.save() InstantiationException "+e.getMessage());
						} catch (IllegalAccessException e) {
							Log.out.error("BaseModelObject.save() IllegalAccessException "+e.getMessage());
						}
					} // fi (foreign key)
					if (aAttrs[a].cc!=null) {
						if (!aAttrs[a].cc.check(oAdm, oIdx, this))
							throw new DmsException(getTypeName()+" attribute "+aAttrs[a].name+" constraint violation "+aAttrs[a].cc.getClass().getName());
					}
				} // next
			} finally {
				if (oAdm!=null) {
					if (oAdm.isConnected()) oAdm.disconnect();
					if (oAdm.isOpen()) oAdm.close();					
				}				
			}
		} // fi
		AttributeMultiValue oAttr;
		Iterator<String> oKeys = keySet().iterator();
		while (oKeys.hasNext()) {
			String sKey = oKeys.next();
			Object oVal = get(sKey);
			if (oVal!=null) {
				if (oVal instanceof String) {
					oAttr = oDoc.attribute(sKey);
					oAttr.set((String) oVal);
				} else if (oVal instanceof Date) {
					oAttr = oDoc.attribute(sKey);
					if (oAttr.type().getDataType().equals(DataType.DATE))
						oAttr.set((Date) (oVal));
					else if (oAttr.type().getDataType().equals(DataType.DATE_TIME))
						oAttr.set((long) DateConversions.getIPSATimeFromDate((Date) (oVal)));
					else
						throw new ClassCastException("Cannot bind Java "+oVal.getClass().getName()+" into "+sKey+" "+oAttr.type().getDataType().name());
				} else if (oVal instanceof BigDecimal) {
					oAttr = oDoc.attribute(sKey);
					BigDecimal oDec = (BigDecimal) oVal;
					try {
					  BigInteger oBig = oDec.toBigIntegerExact();
					  oAttr.set(oBig.longValue());				        
				    } catch (ArithmeticException ex) {
					  oAttr.set(oDec);
				    }
				} else if (oVal instanceof Integer) {
					oAttr = oDoc.attribute(sKey);
					oAttr.set((long) ((Integer) oVal).intValue());
				} else if (oVal instanceof Long) {
					oAttr = oDoc.attribute(sKey);
					oAttr.set(((Long) oVal).longValue());
				} else if (oVal instanceof Float) {
					oAttr = oDoc.attribute(sKey);
					oAttr.set((double) ((Float) oVal).floatValue());
				} else if (oVal instanceof Double) {
					oAttr = oDoc.attribute(sKey);
					oAttr.set(((Double) oVal).doubleValue());
				} else if (oVal instanceof String[]) {
					String[] aStrs = (String[]) oVal;
					oAttr = oDoc.attribute(sKey);
					if (!oAttr.type().isMultivaluable()) throw new AttributeTypeHasValuesException("Cannot set multiple values for attribute "+sKey);
					int nVals = oAttr.count();
					for (int c=0; c<aStrs.length; c++) {
						if (c<nVals)
							oAttr.get(c).set(aStrs[c]);
						else
							oAttr.add().set(aStrs[c]);						
					} // next
				} else if (oVal instanceof BigDecimal[]) {
					oAttr = oDoc.attribute(sKey);
					if (!oAttr.type().isMultivaluable()) throw new AttributeTypeHasValuesException("Cannot set multiple values for attribute "+sKey);
					int nVals = oAttr.count();
					BigDecimal[] aDecs = (BigDecimal[]) oVal;
					for (int c=0; c<aDecs.length; c++) {
						try {
							BigInteger oBig = aDecs[c].toBigIntegerExact();
							if (c<nVals)
								oAttr.get(c).set(oBig.longValue());
							else
								oAttr.add().set(oBig.longValue());
						} catch (ArithmeticException ex) {
							if (c<nVals)
								oAttr.get(c).set(aDecs[c]);
							else
								oAttr.add().set(aDecs[c]);
						}
					} // next
				} else {
					throw new ClassCastException("Cannot bind Java "+oVal.getClass().getName()+" into "+sKey);
				}
			}
		}
		oDoc.save(new Date().toString());
		oSes.getDocumentIndexer().indexDocument(oDoc);
		sId = oDoc.id();
	}

	public String escape(String s) {
		if (s==null)
			return null;
		else
			return s.replace("'", "\\'").replace("\n", "\\n").replace("\n", "\\n").replace("\r", "\\r");
	}
	
	public Document exists(AtrilSession oSes, String sAttributeName, String sAttributeValue)
	    throws DmsException {
		List<Document> oLst = oSes.getDms().query(getTypeName()+"$"+sAttributeName+"='" + escape(sAttributeValue) + "'");
		if (oLst.isEmpty())
			return null;
		else
			return oLst.get(0);		
	}

	public Document exists(AtrilSession oSes, NameValuePair... aNvp) throws DocumentIndexerException {
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		oIdx.setMaximumNumberOfDocumentReturned(1);
		String sQry = DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + getTypeName();
		for (NameValuePair oNvp : aNvp)
			sQry += " AND "+oNvp.getName()+"=\""+oNvp.getValue()+"\"";
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query(sQry);
		if (oLst.isEmpty())
			return null;
		else
			return oLst.get(0);		
	}
	
	protected void delete(AtrilSession oSes, Dms oDms)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException,
		IllegalStateException, DmsException  {
		Log.out.debug("Begin "+getClass().getName()+".delete("+id()+")");
		Document p = oDms.getDocument(id());
		for (Document c : p.children()) {
			Document d = oDms.getDocument(c.id());
			Class t = Class.forName("com.zesped.model."+d.type().name());
			BaseModelObject o = (BaseModelObject) t.newInstance();
			o.setDocument(d);
			o.delete(oSes,oDms);
		}
		if (attributes()!=null) {
			for (int n=0; n<attributes().length; n++) {
				Attr a = attributes()[n];
				if (a.dataType.equals(DataType.DATE) || a.dataType.equals(DataType.DATE_TIME)) {
					getDocument().attribute(a.name).delete();
				}
			}
		}
		p.delete();
		Log.out.debug("End "+getClass().getName()+".delete("+id()+")");		
	}
	
	public static void delete(AtrilSession oSes, String sDocId)
		throws DmsException, ElementNotFoundException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, ClassCastException, IllegalStateException {
		Dms oDms = oSes.getDms();
		Document d = oDms.getDocument(sDocId);
		Log.out.debug("BaseModelObject.delete(AtrilSession, "+sDocId+")");
		if (null==d) throw new ElementNotFoundException("Document "+sDocId+" not found");
		Class t = Class.forName("com.zesped.model."+d.type().name());
		BaseModelObject o = (BaseModelObject) t.newInstance();
		o.setDocument(d);
		o.delete(oSes, oDms);
	}

	public static int deleteVersion(long lDocId, int iVersion) throws DmsException {
		JDCConnection oCon = null;
		int nDeletedAttributes = 0;
		try {
			oCon = DAO.getConnection("BaseModelObject.deleteVersion");
			oCon.setAutoCommit(false);
			PreparedStatement oStm = oCon.prepareStatement("DELETE FROM ValorCadenaTraza where fkIdDoc=? AND Version=?");
			oStm.setLong(1, lDocId);
			oStm.setInt(2, iVersion);
			nDeletedAttributes = oStm.executeUpdate();
			oStm.close();
			oStm = oCon.prepareStatement("DELETE FROM ValorEnteroTraza where fkIdDoc=? AND Version=?");
			oStm.setLong(1, lDocId);
			oStm.setInt(2, iVersion);
			nDeletedAttributes += oStm.executeUpdate();
			oStm.close();
			oCon.commit();
			oCon.close("BaseModelObject.deleteVersion");
		} catch (SQLException sqle) {
			if (oCon!=null) {
				try {
					if (!oCon.isClosed()) oCon.close();
				} catch (SQLException e) { }
			}
			throw new DmsException(sqle.getMessage(), sqle);
		}
		return nDeletedAttributes;
	}

	public Volume getVolume(AtrilSession oSes) throws ElementNotFoundException {
		if (oDoc==null) {
			return null;
		} else {
			VolumeManager oVolm = oSes.getDms().getVolumeManager();
			if (oVolm.hasVolume(oDoc))
				return oVolm.getVolume(oDoc);
			else
				return null;
		}			
	}
	
	public Volume setNewVolume(Dms oDms, String sVolumeName, String sVolumesMountBase, String sVolumeSubpath, String sRepositorySubpath, int iMaxDocSize, long lTotalSpace)
		throws FileNotFoundException, IOException, Exception {
		FileSystem oFs = new FileSystem();
		final String sRepositoryFullPath = Gadgets.chomp(sVolumesMountBase,File.separator)+sVolumeSubpath+File.separator+sRepositorySubpath;
		Log.out.debug("FileSystem.mkdirs(file://"+sRepositoryFullPath+")");
		oFs.mkdirs("file://"+sRepositoryFullPath);
		File oFl = new File(sRepositoryFullPath);
		if (!oFl.exists()) throw new FileNotFoundException("Could not create directory "+sRepositoryFullPath);
		VolumeManager oVolm = oDms.getVolumeManager();
		Log.out.debug("VolumeManager.createVolume("+sVolumeName+","+Gadgets.chomp(sVolumesMountBase,File.separator)+sVolumeSubpath+File.separator+","+sRepositorySubpath+","+String.valueOf(iMaxDocSize)+")");
		Volume oVol = oVolm.createVolume(sVolumeName, Gadgets.chomp(sVolumesMountBase,File.separator)+sVolumeSubpath+File.separator, sRepositorySubpath, iMaxDocSize);
		oVol.setTotalSpace(lTotalSpace);
		oVol.setMaxFiles(65535);
		oVol.setMaxDirectories(65535);
		oVol.addDocument(getDocument());
		oVol.save();
		return oVol;
	}
	
	public final static int  MB = 1048576;
	public final static int  GB = 1073741824;
	public final static long TB = 1099511627776l;
	
}
