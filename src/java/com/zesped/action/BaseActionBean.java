package com.zesped.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.NotYetConnectedException;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.SimpleError;

import com.knowgate.jdc.JDCConnection;
import com.knowgate.storage.Table;
import com.knowgate.storage.StorageException;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.SessionCache;
import com.zesped.model.BaseModelObject;
import com.zesped.model.CaptureService;

import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.sec.authentication.AtrilSession;

public abstract class BaseActionBean implements ActionBean {

	private ActionBeanContext oCtx;
	private JDCConnection oCon;
	private AtrilSession oSes;
	private boolean bIsCachedSession;
	private boolean bIsAdminSession;
	private Document oDoc;
	private boolean bJdb;
	private boolean bAtl;
	private String sTbl;
	ResourceBundle oRsb;
	
	private static HashMap<String,HashMap<String,Method>> oGetMethodsMap = new HashMap<String,HashMap<String,Method>>();
	private static HashMap<String,HashMap<String,Method>> oSetMethodsMap = new HashMap<String,HashMap<String,Method>>();

	public BaseActionBean() {
		oCon = null;
		oSes = null;
		oDoc = null;
		oRsb = null;
		bJdb = false;
		bAtl = true;
		bIsCachedSession = false;
		bIsAdminSession = false;
	}

	@Override
	public ActionBeanContext getContext() {
		return oCtx;
	}

	@Override
	public void setContext(ActionBeanContext oAbc) {
		oCtx=oAbc;
	}

	public String getResource(String sKey) {
		if (null==oRsb)
			oRsb = ResourceBundle.getBundle("StripesResources", getContext().getRequest().getLocale());
		return oRsb.getString(sKey);
	}
	
	public abstract CaptureService getCaptureService();

	protected void preload(AtrilSession oSes, BaseModelObject oObj, String sKey) {
		if (sKey!=null) {
		  if (sKey.length()>0) {
   		    try {
				oObj.load(oSes, sKey);
			} catch (Exception xcpt) {
	    		  Log.out.error(xcpt.getMessage(), xcpt);
	    	} finally {
	    		  close();
	    	}
		  } 
		} // fi
		bindRequest(oObj);		
	}

	protected void bindRequest(Object oTarget) {
		// Log.out.debug("Begin bindRequest()");
		HttpServletRequest oReq = getContext().getRequest();
		HashMap<String,String> mParams = new HashMap<String,String>();
		Iterator<String> oIter = oReq.getParameterMap().keySet().iterator();
		// Log.out.debug("reading "+String.valueOf(oReq.getParameterMap().size())+" parameters");
		while (oIter.hasNext()) {
			final String sParamName = oIter.next();
			String sCanonicalParamName = sParamName.toLowerCase();
			int iDot = sCanonicalParamName.indexOf('.');
			if (iDot>=0) sCanonicalParamName = sCanonicalParamName.substring(++iDot);
			Object obj = oReq.getParameterMap().get(sParamName);
			mParams.put(sCanonicalParamName, ((String[]) obj)[0]);
			// Log.out.debug("got parameter "+sCanonicalParamName+" with value "+(((String[]) obj)[0]));
		} // wend
		final Method[] aMethods = oTarget.getClass().getMethods();
		for (Method oMthd : aMethods) {
			String sMethodName = oMthd.getName();
			if (sMethodName.startsWith("set") && sMethodName.length()>3) {
			  Class[] aTypes = oMthd.getParameterTypes();
			  if (aTypes!=null) {
				  if (aTypes.length==1) {
					  String sCanonicalMethodName = sMethodName.substring(3).toLowerCase();
					  if (mParams.containsKey(sCanonicalMethodName)) {
						  if (aTypes[0].equals(String.class)) {
							  String sParam = mParams.get(sCanonicalMethodName);
							  boolean bVoid = (null==sParam);
							  if (!bVoid) bVoid = (sParam.length()==0);
							  try {
								if (!sCanonicalMethodName.equals("id") || !bVoid) {
									// Log.out.debug("invoking "+sMethodName+" with value "+sParam);
									oMthd.invoke(oTarget, sParam);									
								}
							  } catch (IllegalStateException ilsx) {
								Log.out.warn("BaseActionBean.bindRequest "+sMethodName+" IllegalStateException "+ilsx.getMessage());
							  } catch (Exception xcpt) {
								Log.out.error("BaseActionBean.bindRequest "+sMethodName+" "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
							}
						  } // fi
					  }
				  } // fi
			  } // fi
			} // fi
		} // next
		// Log.out.debug("End bindRequest()");
	}

	private static HashMap<String,Method> getPropertiesGetters(HashMap<String,HashMap<String,Method>> oMethodsMap, Object oObj) {
		HashMap<String,Method> oMethods;
		final String sClassName = oObj.getClass().getName();
		if (oMethodsMap.containsKey(sClassName)) {
			oMethods = oGetMethodsMap.get(sClassName);
		} else {
			oMethods = new HashMap<String,Method>();
			Method[] aMethods = oObj.getClass().getMethods();
			for (Method oMthd : aMethods) {
				String sMethodName = oMthd.getName();
				if (sMethodName.startsWith("get") && sMethodName.length()>3) {
					Class oType = oMthd.getReturnType();
					if (oType!=null) {
						oMethods.put(sMethodName.substring(3)+"("+oType.getClass().getName()+")", oMthd);
					} // fi
				} // fi
			} // next
		}
		return oMethods;
	}
	
	private static HashMap<String,Method> getPropertiesSetters(HashMap<String,HashMap<String,Method>> oMethodsMap, Object oObj) {
		HashMap<String,Method> oMethods;
		final String sClassName = oObj.getClass().getName();
		if (oMethodsMap.containsKey(sClassName)) {
			oMethods = oGetMethodsMap.get(sClassName);
		} else {
			oMethods = new HashMap<String,Method>();
			Method[] aMethods = oObj.getClass().getMethods();
			for (Method oMthd : aMethods) {
				String sMethodName = oMthd.getName();
				if (sMethodName.startsWith("set") && sMethodName.length()>3) {
					Class[] aTypes = oMthd.getParameterTypes();
					if (aTypes!=null) {
						if (aTypes.length==1) {
							oMethods.put(sMethodName.substring(3)+"("+aTypes[0].getClass().getName()+")", oMthd);
					    }
					} // fi
				} // fi
			} // next
		}
		return oMethods;
	}

	protected static void bindObject(Object oSource, Object oTarget) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException {
		Log.out.debug("Begin bindObject("+oSource+","+oTarget+")");
		HashMap<String,Method> oGetMethods = getPropertiesGetters(oGetMethodsMap, oSource);
		HashMap<String,Method> oSetMethods = getPropertiesSetters(oSetMethodsMap, oTarget);
		Iterator<String> oSetIter = oSetMethods.keySet().iterator();
		while (oSetIter.hasNext()) {
			String sMethodSignature = oSetIter.next();
			if (oGetMethods.containsKey(sMethodSignature)) {
				Method oSet = oSetMethods.get(sMethodSignature);
				Method oGet = oGetMethods.get(sMethodSignature);
				Log.out.debug("Invoking "+oTarget.getClass().getName()+"."+oSet.getName()+"("+oSource.getClass().getName()+"."+oGet.getName()+"())");
				oSet.invoke(oTarget, oGet.invoke(oSource));
			} // fi
		} // wend
		Log.out.debug("End bindObject()");
	}
	
	protected void saveRequest(BaseModelObject oObj)
		throws NotYetConnectedException, DmsException, ClassCastException, IllegalStateException, NullPointerException, RuntimeException, StorageException {
		bindRequest(oObj);
		oObj.save(getSession());		
	}
	
	public String getParam(String sParamName) {		
		HttpServletRequest oReq = getContext().getRequest();
		String sRetVal = oReq.getParameter(sParamName);
		if (sRetVal==null)
			sRetVal = (String) oReq.getAttribute(sParamName);
		return sRetVal;
	}

	public String getParam(String sParamName, String sDefaultValue) {
		String sRetVal = getParam(sParamName);
		if (sRetVal==null)
			sRetVal = sDefaultValue;
		return sRetVal;
	}
	
	public void setParam(String sParamName, String sParamValue) {
		FlashScope oFscope = FlashScope.getCurrent(getContext().getRequest(), true);
		oFscope.put(sParamName, sParamValue);		
	}

	public CaptureService[] getCaptureServices() {
		return CaptureService.values();
	}

	public void setSessionAttribute(String sKey, String sValue) {
		getContext().getRequest().getSession().setAttribute(sKey, sValue);
	}

	public String getSessionAttribute(String sKey) {
		return (String) getContext().getRequest().getSession().getAttribute(sKey);
	}

	public void removeSessionAttribute(String sKey) {
		getContext().getRequest().getSession().removeAttribute(sKey);
	}
	
	public void useJDBC(boolean bJDBCUsage) {
		bJdb = bJDBCUsage;
	}

	public boolean useAtril() {
		return bAtl;
	}

	public void useAtril(boolean bAtrilUsage) {
		bAtl = bAtrilUsage;
	}

	public boolean useJDBC() {
		return bJdb;
	}
		
	public Table getTable() throws StorageException {
		if (null==oCon) throw new StorageException("Not connected to the database through JDBC");
		return oCon;
	}

	public AtrilSession getSession() throws StorageException {
		return oSes;
	}

	public void setSession(AtrilSession oAtrilSession) throws StorageException {
		oSes = oAtrilSession;
		bIsCachedSession = false;
	}
	
	public Document getDocument() throws StorageException {
		return oDoc;
	}

	public void setDocument(Document oAtrilDocument) {
		oDoc = oAtrilDocument;
	}
	
	public void connect() throws StorageException {
		if (oCon!=null || oSes!=null) throw new StorageException("Already connected");
		sTbl = getClass().getName();
		if (useJDBC()) {
			try {
				oCon = DAO.getConnection(sTbl);
				oCon.setAutoCommit(false);
			} catch (SQLException sqle) {
				throw new StorageException(sqle.getMessage(), sqle);
			}
		}
		if (useAtril()) {
			oSes = SessionCache.getAdminSession(sTbl);
			if (null==oSes) {
				oSes = DAO.getAdminSession(sTbl);				
				Log.out.info("Admin session cache miss");
			} else {
				if (!oSes.isConnected()) oSes.connect();
				Log.out.info("Admin session cache hit");
			}
		}
		bIsCachedSession = false;
		bIsAdminSession = true;
	}

	public void connect(String sNickName, String sPassword) throws StorageException {
		if (oCon!=null || oSes!=null) throw new StorageException("Already connected");
		sTbl = getClass().getName();
		if (useJDBC()) {
			try {
				oCon = DAO.getConnection(sTbl);
				oCon.setAutoCommit(false);
			} catch (SQLException sqle) {
				throw new StorageException(sqle.getMessage(), sqle);
			}
		}
		if (useAtril()) {
			oSes = SessionCache.getSession(getContext().getRequest().getSession().getId());
			if (null!=oSes) {
				if (oSes.isConnected()) {
					Log.out.info("Session cache miss due to previous connected session");
					oSes = DAO.getSession(sTbl,sNickName,sPassword);
					bIsCachedSession = false;				
				} else {
					Log.out.info("Session cache hit");
					oSes.connect();
					bIsCachedSession = true;				
				}				
			} else {
				Log.out.info("Session cache miss due to no previous session");
				oSes = DAO.getSession(sTbl,sNickName,sPassword);
				SessionCache.putSession(getContext().getRequest().getSession().getId(), oSes);
				bIsCachedSession = true;								
			}
			bIsAdminSession = false;
		}
	}
	
	public void connect(String sTableName) throws StorageException {
		if (oCon!=null) throw new StorageException("Already connected");
		sTbl = sTableName==null ? getClass().getName() : sTableName;
		try {
			oCon = DAO.getConnection(sTbl);
			oCon.setAutoCommit(false);
		} catch (SQLException sqle) {
			throw new StorageException(sqle.getMessage(), sqle);
		}
		if (useAtril()) {
			oSes = SessionCache.getAdminSession(sTbl);
			if (null==oSes) {
				oSes = DAO.getAdminSession(sTbl);				
				Log.out.info("Admin session cache miss");
			} else {
				if (!oSes.isConnected()) oSes.connect();
				Log.out.info("Admin session cache hit");
			}
		}
		bIsCachedSession = false;
		bIsAdminSession = true;
	}

	public void disconnect() throws StorageException {
		if (oCon!=null) {
			try {
				if (!oCon.isClosed()) {
					oCon.commit();
					oCon.close(sTbl);
					oCon = null;
				}
			} catch (SQLException sqle) {
				throw new StorageException(sqle.getMessage(), sqle);
			}
		}
		if (oSes!=null) {
			  if (oSes.isConnected()) {
				  if (!oSes.autoCommit()) oSes.commit();
				  oSes.disconnect();
			  }
			  if (bIsAdminSession) {
				  if (oSes.isOpen() && !SessionCache.putAdminSession(oSes))
					  oSes.close();
			  } else {
				  if (oSes.isOpen() && !bIsCachedSession) oSes.close();
			  }
			  oSes = null;
		}
	}

	public void close() {
		if (oCon!=null) {
			try {
				if (!oCon.isClosed()) {
					oCon.rollback();
					oCon.close(getClass().getName());
					oCon = null;
				}
			} catch (SQLException ignore) { }
		}
		if (oSes!=null) {
			  if (oSes.isConnected()) {
				  if (!oSes.autoCommit()) oSes.rollback();
				  oSes.disconnect();
			  }
			  if (bIsAdminSession) {
				  if (oSes.isOpen() && !SessionCache.putAdminSession(oSes))
					  oSes.close();
			  } else {
				  if (oSes.isOpen() && !bIsCachedSession) oSes.close();
			  }
			  oSes = null;			
		}
	}

	public void commit() throws StorageException {
		if (oCon!=null) {
			try {
				if (!oCon.isClosed()) {
					oCon.commit();
				}
			} catch (SQLException sqle) {
				throw new StorageException(sqle.getMessage(), sqle);
			}
		}
		if (oSes!=null) {
			  if (oSes.isConnected()) {
				  if (!oSes.autoCommit()) oSes.commit();
			  }
		}
	}

	public void rollback() throws StorageException {
		if (oCon!=null) {
			try {
				if (!oCon.isClosed()) {
					oCon.rollback();
				}
			} catch (SQLException sqle) {
				throw new StorageException(sqle.getMessage(), sqle);
			}
		}
		if (oSes!=null) {
			  if (oSes.isConnected()) {
				  if (!oSes.autoCommit()) oSes.rollback();
			  }
		}
	}
	
    @SuppressWarnings("unchecked")
    public String getLastUrl() {
        HttpServletRequest req = getContext().getRequest();
        StringBuilder sb = new StringBuilder();

        // Start with the URI and the path
        String uri = (String)
            req.getAttribute("javax.servlet.forward.request_uri");
        String path = (String)
            req.getAttribute("javax.servlet.forward.path_info");
        if (uri == null) {
            uri = req.getRequestURI(); 
            path = req.getPathInfo(); 
        }
        sb.append(uri);
        if (path != null) { sb.append(path); }

        // Now the request parameters
        sb.append('?');
        Map<String,String[]> map =
            new HashMap<String,String[]>(req.getParameterMap());

        // Remove previous locale parameter, if present.
        // map.remove(MyLocalePicker.LOCALE);

        // Append the parameters to the URL
        for (String key : map.keySet()) {
            String[] values = map.get(key);
            for (String value : values) {
                sb.append(key).append('=').append(value).append('&');
            }
        }
        // Remove the last '&'
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
	
	public void log(Class oCls, String sCategory, String sDetails) {
		if (oDoc==null)
			DAO.log(oSes, getClass(), sCategory, AtrilEvent.Level.INFO, sDetails);
		else
			DAO.log(oSes, oDoc, getClass(), sCategory, AtrilEvent.Level.INFO, sDetails);			
	}
	
	public String errorsToXML(Collection<SimpleError> errors) {
		StringBuffer buffer = new StringBuffer();
		if (null==errors) {
	      buffer.append("<errors count=\"0\" />");			
		} else {
		  buffer.append("<errors count=\""+String.valueOf(errors.size())+"\"");
		  for (SimpleError e : errors) {
		    buffer.append("<error field=\""+e.getFieldName()+"\">"+e.getMessage(getContext().getLocale())+"</error>");
		  }
		  buffer.append("</errors>");
		}
		return buffer.toString();
	}
	
}
