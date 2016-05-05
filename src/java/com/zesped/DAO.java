package com.zesped;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.knowgate.dataobjs.DBBind;
import com.knowgate.dataobjs.DBCommand;

import com.knowgate.jdc.JDCConnection;

import es.ipsa.atril.NodeList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.volumes.Volume;
import es.ipsa.atril.doc.volumes.VolumeManager;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.authentication.AuthenticationManagerFactory;
import es.ipsa.atril.sec.exceptions.AuthenticationException;
import es.ipsa.atril.sec.exceptions.SecuritySystemException;
import es.ipsa.atril.util.persistence.exceptions.PersistenceManagerException;

public class DAO extends HttpServlet  {

  public static final String EVENT_STORE = "STORE";

  private static final long serialVersionUID = 1l;
  private static DBBind oRdbms = null;
  private static Properties oAtrilProperties = null;
  private static Properties oRdbmsProperties = null;  
  private static boolean bInitOK = false;
  private static String sInitError = "";
  public static boolean checkConstraints = true;

  // --------------------------------------------------------------------------
  
  public final static Properties atrilProperties() throws ServletException {
    return oAtrilProperties;
  }

  // --------------------------------------------------------------------------

  public void init(Properties oProps) throws ServletException {
    Log.out.info("Begin com.zesped.DAO.init()");
	oAtrilProperties = oProps;
    oRdbmsProperties = new Properties();
    oRdbmsProperties.put("driver", oAtrilProperties.getProperty("atril.connectionType.jdbc.driver"));
    oRdbmsProperties.put("dburl" , oAtrilProperties.getProperty("atril.connectionType.jdbc.url"));
    Log.out.info("Instantiating DBBind");
    oRdbms = new DBBind(oRdbmsProperties);    
	bInitOK = true;
    Log.out.info("End com.zesped.DAO.init()");
  }
  
  // --------------------------------------------------------------------------
  
  /**
   * <p>Inicialización del servlet</p>
   * Carga los parametros de /WEB-INF/web.xml y verifica que se puede conectar a las bases de datos
   * @throws ServletException
   */
  public void init() throws ServletException {
	
	Log.out.info("Begin com.zesped.DAO.init()");

	bInitOK = false;

    ServletConfig oCfg = getServletConfig();
    
    // ********************************************************************************************
    // Cargar las propiedades para conectarse a Atril desde /WEB-INF/web.xml a un objeto Properties

	oAtrilProperties = new Properties();
    
	oAtrilProperties.put("atril.platform", getInitParameter(oCfg,"atril.platform", "Atril6"));
	oAtrilProperties.put("atril.name", getInitParameter(oCfg,"atril.name", "Zesped DMS"));
	oAtrilProperties.put("atril.dialect", getInitParameter(oCfg,"atril.dialect", "MySQL"));
	oAtrilProperties.put("atril.connectionType", getInitParameter(oCfg,"atril.connectionType", "JDBC"));
	oAtrilProperties.put("atril.connectionType.jdbc.driver", getInitParameter(oCfg,"atril.connectionType.jdbc.driver", "com.mysql.jdbc.Driver"));
	oAtrilProperties.put("atril.connectionType.jdbc.url",getInitParameter(oCfg,"atril.connectionType.jdbc.url", ""));
	oAtrilProperties.put("atril.authentication",getInitParameter(oCfg,"atril.authentication", "Database"));
	oAtrilProperties.put("atril.authentication.cryptography.provider",getInitParameter(oCfg,"atril.authentication.cryptography.provider", "BC"));
	oAtrilProperties.put("atril.authentication.cryptography.algorithm",getInitParameter(oCfg,"atril.authentication.cryptography.algorithm", "SHA-1"));
	oAtrilProperties.put("atril.indexerType",getInitParameter(oCfg,"atril.indexerType", "Lucene"));
	oAtrilProperties.put("atril.indexerType.indexDirectoryPath",getInitParameter(oCfg,"atril.indexerType.indexDirectoryPath", ""));
	oAtrilProperties.put("atril.user", getInitParameter(oCfg,"atril.user", "admin"));
	oAtrilProperties.put("atril.password", getInitParameter(oCfg,"atril.password", "admin"));
	oAtrilProperties.put("atril.volumeManager.mountBase", getInitParameter(oCfg, "atril.volumeManager.mountBase", ""));

	if (oAtrilProperties.getProperty("atril.indexerType").equalsIgnoreCase("Lucene")) {
      if (oAtrilProperties.getProperty("atril.indexerType.indexDirectoryPath").length()==0) {
    	sInitError = "com.zesped.DAO.init() Se requiere el parametro atril.indexerType.indexDirectoryPath en /WEB-INF/web.xml <init-param>";
  	    Log.out.fatal(sInitError);
        throw new ServletException(sInitError);
      } else if (!new File(oAtrilProperties.getProperty("atril.indexerType.indexDirectoryPath")).exists()) {
    	  sInitError = "com.zesped.DAO.init()  No existe el subdirectorio "+oAtrilProperties.getProperty("atril.indexerType.indexDirectoryPath");
    	  Log.out.fatal(sInitError);
          throw new ServletException(sInitError);    	  
      }
    }

    if (oAtrilProperties.getProperty("atril.connectionType.jdbc.url").length()==0) {
	  oAtrilProperties = null;
	  sInitError = "com.zesped.DAO.init() Se requiere el parametro atril.connectionType.jdbc.url en /WEB-INF/web.xml <init-param>"; 
	  Log.out.fatal(sInitError);
      throw new ServletException(sInitError);
    } else if (oAtrilProperties.getProperty("atril.connectionType").equalsIgnoreCase("JDBC")) {
      // Probar a cargar el driver de BB.DD. que esté usando Atril
      try {
		Class.forName(oAtrilProperties.getProperty("atril.connectionType.jdbc.driver"));
	  } catch (ClassNotFoundException cnfe) {
		oAtrilProperties = null;
		sInitError = "com.zesped.DAO.init() El cargador no pudo encontrar la clase "+oAtrilProperties.getProperty("atril.connectionType.jdbc.driver");
		Log.out.fatal(sInitError);
	    throw new ServletException(sInitError);
	  }
      // Probar a conectarse a la BB.DD. de Atril
      try {
		Connection oConn = DriverManager.getConnection(oAtrilProperties.getProperty("atril.connectionType.jdbc.url"));
		oConn.close();
		Log.out.info("Conexion a la BB.DD. de Atril completada con exito");
      } catch (SQLException sqle) {
    	oAtrilProperties = null;
    	sInitError = "com.zesped.DAO.init() SQLException "+sqle.getLocalizedMessage();
	    Log.out.fatal(sInitError, sqle);
  	    throw new ServletException("com.zesped.DAO.init() SQLException "+sqle.getLocalizedMessage(), sqle);
	  }
	  
    } // fi

    // ********************************************************************************************
    // Cargar las propiedades para conectarse a Atril desde /WEB-INF/web.xml a un objeto Properties
    
    oRdbmsProperties = new Properties();
    oRdbmsProperties.put("driver", getInitParameter(oCfg,"atril.connectionType.jdbc.driver", "com.mysql.jdbc.Driver"));
    oRdbmsProperties.put("dburl" , getInitParameter(oCfg,"atril.connectionType.jdbc.url", ""));

    Log.out.info("Instantiating DBBind");

    oRdbms = new DBBind(oRdbmsProperties);    

	bInitOK = true;

	Log.out.info("End com.zesped.DAO.init()");
  } // init

  // --------------------------------------------------------------------------

  public void destroy() {
	bInitOK = false;
	Log.out.info("com.zesped.DAO.destroy()");
	if (null!=oRdbms) { oRdbms.close(); oRdbms=null; }
	Log.out.info("com.zesped.DAO.destroy()");
  } // destroy


  // --------------------------------------------------------------------------

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
  } // doGet

  // --------------------------------------------------------------------------

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    doGet(request, response);
  }
  
  // --------------------------------------------------------------------------
  
  private static String getInitParameter(ServletConfig oCfg, String sParamName, String sDefaultValue) {
    String sParamValue = oCfg.getInitParameter(sParamName);
    if (null==sParamValue)
      sParamValue = sDefaultValue;
    else if (sParamValue.length()==0)
      sParamValue = sDefaultValue;    
    Log.out.debug(sParamName+"="+sParamValue);
    return sParamValue;
  }  

  // --------------------------------------------------------------------------
  
  public String getResourceAsString (String sResourcePath, String sEncoding)
    throws FileNotFoundException,IOException {

	StringBuffer oSource = new StringBuffer(6000);
	char[] Buffer = new char[4000];
	InputStreamReader oReader = null;
	int iReaded, iSkip;
	InputStream oIoStrm = getClass().getResourceAsStream(sResourcePath);
	if (null==oIoStrm) throw new FileNotFoundException(getClass().getName().replace('.','/')+sResourcePath);
	try {
	  oReader = new InputStreamReader(oIoStrm, sEncoding);
	} catch (UnsupportedEncodingException neverthrown) { }
	while (true) {
	  iReaded = oReader.read(Buffer, 0, 4000);
	  if (-1==iReaded) break;
	  // Skip FF FE character mark for Unicode files
	  iSkip = ((int)Buffer[0]==65279 || (int)Buffer[0]==65534 ? 1 : 0);
	  oSource.append(Buffer, iSkip, iReaded-iSkip);
	} // wend
    oReader.close();
    oIoStrm.close();
	return oSource.toString();
  } // getResourceAsString

  // --------------------------------------------------------------------------
  
  public static JDCConnection getConnection(final String sCaller) throws SQLException {
	  if (!bInitOK) throw new IllegalStateException("DAO was not properly initialized");
	  return oRdbms.getConnection(sCaller);
  }

  // --------------------------------------------------------------------------
  
  public static String getDatabaseProductName() throws SQLException {
	  if (!bInitOK) throw new IllegalStateException("DAO was not properly initialized");
	  return oRdbms.getDatabaseProductName();	  
  }

  // --------------------------------------------------------------------------
  
  public static String getRepositoryVersion() throws SQLException {
	  if (!bInitOK) throw new IllegalStateException("DAO was not properly initialized");
	  JDCConnection oCon = oRdbms.getConnection("DAO.getRepositoryVersion");
	  String sVersion;
	  if (DBBind.exists(oCon, "VersionRepositorio", "U")) {
		  sVersion = DBCommand.queryStr(oCon, "SELECT IdVersion FROM VersionRepositorio");
	  } else {
		  sVersion = null;
	  }
	  oCon.close("DAO.getRepositoryVersion");
	  return sVersion;
  }
  
  // --------------------------------------------------------------------------

  public static void log(AtrilSession oSes, Class oCls, String sCategory, AtrilEvent.Level eLevel, String sDetails) {
	  oSes.getEventLogger().registerEvent(new AtrilEvent.Builder(oCls.getName(), sCategory, eLevel).setDetails(sDetails).build());
  }

  // --------------------------------------------------------------------------

  public static void log(Class oCls, String sCategory, AtrilEvent.Level eLevel, String sDetails) throws AuthenticationException, SecuritySystemException,PersistenceManagerException {
	  AtrilSession oSes = getAdminSession("log");
	  oSes.getEventLogger().registerEvent(new AtrilEvent.Builder(oCls.getName(), sCategory, eLevel).setDetails(sDetails).build());
	  oSes.commit();
	  oSes.disconnect();
	  oSes.close();
  }
  
  // --------------------------------------------------------------------------

  public static void log(AtrilSession oSes, Document oDoc, Class oCls, String sCategory, AtrilEvent.Level eLevel, String sDetails) {
	  oSes.getEventLogger().registerEvent(new AtrilEvent.Builder(oCls.getName(), sCategory, eLevel).setDetails(sDetails).setDocument(oDoc.id()).isTransactionalEvent(true).build());
  }
  
  // --------------------------------------------------------------------------
  
  public static AtrilSession getSession(String sContext, String sUsr, String sPwd)
	  throws AuthenticationException, SecuritySystemException, IllegalStateException,PersistenceManagerException {
	  if (!bInitOK) throw new IllegalStateException("DAO was not properly initialized");
      AtrilSession oSess = AuthenticationManagerFactory.getAuthenticationManager(oAtrilProperties).authenticateUser(sUsr, sPwd);
	  oSess.open(sContext);
      oSess.connect();
	  oSess.autoCommit(false);
      return oSess;
  }

  // --------------------------------------------------------------------------
  
  public static AtrilSession getAdminSession(String sContext) throws AuthenticationException, SecuritySystemException,PersistenceManagerException {
	  if (!bInitOK) throw new IllegalStateException("DAO was not properly initialized");
      return getSession(sContext, oAtrilProperties.getProperty("atril.user"), oAtrilProperties.getProperty("atril.password"));
  }

  // --------------------------------------------------------------------------
  
  public static String getVolumesMountBase() {
	  return oAtrilProperties.getProperty("atril.volumeManager.mountBase", "");
  }

  // --------------------------------------------------------------------------
  
  public static Volume defaultVolume(VolumeManager oVolm) {
	  List<Volume> aVols = oVolm.getVolumeList();
	  Volume oVol; 
	  if (aVols.isEmpty()) {
		  oVol = null;
		  Log.out.debug("No volumes found at Atril repository");
	  } else {
		  int iId = 0;
		  do {
			  oVol = aVols.get(iId++);		  
		  } while ((!oVol.isMounted() || !oVol.isUsable()) && iId<aVols.size());
		  if (!oVol.isMounted() || !oVol.isUsable()) {
			  oVol = null;
			  Log.out.debug("No mounted and usable volume was found");
		  } else {
			  Log.out.debug("Using volume "+oVol.name());			  
		  }
	  }
	  return oVol;
  }
  
  // --------------------------------------------------------------------------
  
  public static void reindexDocument(AtrilSession oSes, Document oDoc) throws AuthenticationException, SecuritySystemException, SQLException {
	  if (!bInitOK) throw new IllegalStateException("DAO was not properly initialized");
	  DocumentIndexer oIdx = oSes.getDocumentIndexer();
	  oIdx.indexDocument(oDoc);
	  NodeList<Document> oChilds = oDoc.children();
	  for (Document c : oChilds) {
		  reindexDocument(oSes, oSes.getDms().getDocument(c.id()));
	  }
  }
  
  // --------------------------------------------------------------------------
  
  public static void reindexAll() throws AuthenticationException, SecuritySystemException, SQLException {
	  if (!bInitOK) throw new IllegalStateException("DAO was not properly initialized");
	  
	  AtrilSession oSess = getSession("reindexAll", oAtrilProperties.getProperty("atril.user"), oAtrilProperties.getProperty("atril.password"));
	  Dms oDms = oSess.getDms();
	  Document oRoot = oDms.getRootDocument();
	  NodeList<Document> oChilds = oRoot.children();
	  for (Document c : oChilds) {
		  if (c.type().name().equals("Zesped")) {
			  reindexDocument(oSess, c);
			  break;
		  }
	  }
	  oSess.commit();
	  oSess.disconnect();
	  oSess.close();	  
  }
  
}