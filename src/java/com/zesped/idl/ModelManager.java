package com.zesped.idl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletException;

import com.knowgate.dfs.FileSystem;
import com.knowgate.jdc.JDCConnection;
import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.model.*;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.internationalization.InternationalizationManager;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.DocumentType;
import es.ipsa.atril.doc.volumes.Volume;
import es.ipsa.atril.doc.volumes.VolumeManager;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.user.AuthorizationManager;
import es.ipsa.atril.exceptions.ElementNotFoundException;

/**
 * <p>zesped Data Model Manager</p>
 * <p>This class is used for creating the underlying data model for zesped</p>
 * @version 1.9
 */

public class ModelManager {

  private static final String VERSION = "2.2";

  private static final int BULK_PROCEDURES = 1;
  private static final int BULK_STATEMENTS = 2;
  private static final int BULK_BATCH = 3;
  private static final int BULK_PLSQL = 4;
  private static final int FILE_STATEMENTS = 5;

  private boolean bStopOnError;
  private int iErrors;
  private StringBuffer oStrLog;
  private String sEncoding;
  private String sDbms;
  private int iDbms;
  
  private Properties oAtrilProperties;
  
  // ---------------------------------------------------------------------------

  public ModelManager() throws UnsupportedOperationException,IOException,SQLException, ServletException {

    oStrLog = null;
    bStopOnError = false;
    sEncoding = "UTF-8";
    oAtrilProperties = new Properties();
	oAtrilProperties.load(getClass().getResourceAsStream("connection.properties"));
    
	DAO oDao = new DAO();
	oDao.init(oAtrilProperties);	
    final String sDbmsProductName = DAO.getDatabaseProductName();    
    oDao.destroy();
    
    if (sDbmsProductName.equals("MySQL")) {
    	sDbms = "mysql";
    	iDbms = JDCConnection.DBMS_MYSQL;
    } else {
    	throw new UnsupportedOperationException("Unsupported database management system "+sDbmsProductName);
    }
  }

  // ---------------------------------------------------------------------------

  public void activateLog(boolean bActivate) {
    oStrLog = (bActivate ? new StringBuffer() : null);      
  }

  // ---------------------------------------------------------------------------

  public String getEncoding () {
    return sEncoding;
  }

  // ---------------------------------------------------------------------------
  
  public void setEncoding (String sCharset) {
    sEncoding = sCharset;
  }

  // ---------------------------------------------------------------------------
  
  /**
   * <p>Set whether or not create() and drop() methods should stop on error</p>
   * @param bStop <b>true</b>=stop on error, <b>false</b>=don not stop
   */
  public void stopOnError(boolean bStop) {
    bStopOnError = bStop;
  }

  // ---------------------------------------------------------------------------

  /**
   * <p>Get whether or not create() and drop() methods will stop on error</p>
   * @return bStop <b>true</b>=stop on error, <b>false</b>=don not stop
   */
  public boolean stopOnError() {
    return bStopOnError;
  }

  // ---------------------------------------------------------------------------

  /**
   * Clear internal operation log
   */
  public void clear() {
    if (null!=oStrLog) oStrLog.setLength(0);
  }

  // ---------------------------------------------------------------------------

  /**
   * Print internal operation log to a String
   */
  public String report() {
    String sRep;

    if (null!=oStrLog)
      sRep = oStrLog.toString();
    else
      sRep = "";

    return sRep;
  } // report

  // ---------------------------------------------------------------------------

  private boolean isDoubleQuote(StringBuffer oBuffer, int iLen, int iPos) {
    if (iPos>=iLen-2)
      return false;
    else
      return (oBuffer.charAt(++iPos)==(char)39);    
  } // isDoubleQuote

  // ---------------------------------------------------------------------------

  private boolean switchQuoteActiveStatus (StringBuffer oBuffer, int iLen, char cAt, int iPos, boolean bActive) {
    boolean bRetVal;
    // If a single quote sign ' is found then switch ON or OFF the value of bActive
    if (cAt==39) {
      if (isDoubleQuote(oBuffer, iLen, iPos))
      	bRetVal = bActive;
      else
      	bRetVal = !bActive;
    } else {
      bRetVal = bActive;
    }// fi (cAt==')
    return bRetVal;
  } // switchQuoteActiveStatus

  // ---------------------------------------------------------------------------
  
  private String[] split(StringBuffer oBuffer, char cDelimiter, String sGo) {

    // Fast String spliter routine specially tuned for SQL sentence batches

    final int iLen = oBuffer.length();
    int iGo;

    if (null!=sGo)
      iGo = sGo.length();
    else
      iGo = 0;

    char cAt;

    // Initially bActive is set to true
    // bActive signals that the current status is sensitive
    // to statement delimiters.
    // When a single quote is found, bActive is set to false
    // and then found delimiters are ignored until another
    // matching closing quote is reached.
    boolean bActive = true;
    int iStatementsCount = 0;
    int iMark = 0, iTail = 0, iIndex = 0;

    // Scan de input buffer
    for (int c=0; c<iLen; c++) {
      cAt = oBuffer.charAt(c);
      
      if (iGo>0 && JDCConnection.DBMS_POSTGRESQL==iDbms) {
        bActive = switchQuoteActiveStatus(oBuffer, iLen, cAt, c, bActive);
        if (c<iLen-1) if ((cAt==(char)39) && (oBuffer.charAt(c+1)==(char)39)) c+=2;
      }

      // If the statement delimiter is found outside a quoted text then count a new line
      if (cAt==cDelimiter && bActive) {
        if (null==sGo) {
          iStatementsCount++;
        } else if (c>=iGo) {
          if (oBuffer.substring(c-iGo,c).equalsIgnoreCase(sGo)) {
            iStatementsCount++;
          }
        }
      } // fi (cAt==cDelimiter && bActive)
      // Skip any blank or non-printable characters after the end-of-statement marker
      for (iMark=c+1; iMark<iLen; iMark++)
        if (oBuffer.charAt(iMark)>32) break;
    } // next (c)

    String aArray[] = new String[iStatementsCount];
    iMark  = iTail = iIndex = 0;
    bActive = true;
    for (int c=0; c<iLen; c++) {
      cAt = oBuffer.charAt(c);

      if (iGo>0 && JDCConnection.DBMS_POSTGRESQL==iDbms) {
        bActive = switchQuoteActiveStatus(oBuffer, iLen, cAt, c, bActive);
        if (c<iLen-1) if ((cAt==(char)39) && (oBuffer.charAt(c+1)==(char)39)) c+=2;
      }

      // If reached and end-of-statement marker outside a quoted text
      // and either there is no "GO" marker
      // or the "GO" marker is just prior to the delimiter
      if ((cAt==cDelimiter && bActive) &&
    	  (null==sGo || (c>=iGo && oBuffer.substring(c-iGo,c).equalsIgnoreCase(sGo)))) {

    	// Scan backwards from the end-of-statement
        for ( iTail=c-1; iTail>0; iTail--) {
          // If there is no "GO" then just skip blank spaces between the end-of-statement marker
          // and the last printable character of the statement
          if (oBuffer.charAt(iTail)>32 && null==sGo)
            break;
          else
        	// Just step back the length of the "GO" marker and break
        	if (null!=sGo) {
              iTail -= iGo;
              break;
            }
        } // next

        try {
          // Assign the statement to an array line
          aArray[iIndex] = oBuffer.substring(iMark,iTail+1);
          iIndex++;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
          String sXcptInfo = aioobe.getMessage()+" c="+String.valueOf(c)+" at="+cAt+" active="+String.valueOf(bActive)+" aArray.length="+String.valueOf(iStatementsCount)+" oBuffer.length="+String.valueOf(oBuffer.length())+" iIndex="+String.valueOf(iIndex)+" iMark="+String.valueOf(iMark)+" iTail="+String.valueOf(iTail);
          if (iIndex>0) sXcptInfo += " next to " + aArray[iIndex-1];
          throw new ArrayIndexOutOfBoundsException(sXcptInfo);
        }

        // Skip any blank or non-printable characters after the end-of-statement marker
        for (iMark=c+1; iMark<iLen; iMark++)
          if (oBuffer.charAt(iMark)>32) break;

      } // fi (found delimiter)
    } // next (c)

    if (iIndex<iStatementsCount-1 && iMark<iLen-1)
      aArray[iIndex] = oBuffer.substring(iMark);

    return aArray;
  } // split

  // ---------------------------------------------------------------------------

  private StringBuffer getSQLBuffer(String sResourcePath, int iBatchType)
    throws FileNotFoundException, IOException,SQLException,InterruptedException {

      int iReaded, iSkip;
      final int iBufferSize = 16384;
      char Buffer[] = new char[iBufferSize];
      InputStream oInStrm;
      InputStreamReader oStrm;
      StringBuffer oBuffer = new StringBuffer();

      iErrors = 0;

      if (FILE_STATEMENTS == iBatchType) {
        if (null!=oStrLog) oStrLog.append("Open file " + sResourcePath + " as " + sEncoding + "\n");
        oInStrm = new FileInputStream(sResourcePath);
      }
      else {
        if (null!=oStrLog) oStrLog.append("Get resource " + sResourcePath + " as " + sEncoding + "\n");
        oInStrm = getClass().getResourceAsStream(sResourcePath);
      }

      if (null == oInStrm) {
        iErrors = 1;
        if (null!=oStrLog) oStrLog.append("FileNotFoundException "+sResourcePath);
        throw new FileNotFoundException("executeBulk() " + sResourcePath);
      } // fi

      oStrm = new InputStreamReader(oInStrm, sEncoding);

      try {
        while (true) {
          iReaded = oStrm.read(Buffer,0,iBufferSize);

          if (-1==iReaded) break;

          // Skip FF FE character mark for Unidode files
          iSkip = ((int)Buffer[0]==65279 || (int)Buffer[0]==65534 ? 1 : 0);

          oBuffer.append(Buffer, iSkip, iReaded-iSkip);

        }
        oStrm.close();
        oInStrm.close();
      }
      catch (IOException ioe) {
        iErrors = 1;
        if (null!=oStrLog) oStrLog.append("IOException "+ioe.getMessage());
        throw new IOException(ioe.getMessage());
      }

      return oBuffer;
  }

  // ---------------------------------------------------------------------------

  private int executeBulk(StringBuffer oBuffer, String sResourcePath, int iBatchType)
    throws FileNotFoundException, IOException, SQLException,InterruptedException {

    int iStatements;
    CallableStatement oCall = null;
    Statement oStmt = null;
    String sSQL = null;
    String aStatements[];

    iErrors = 0;

    if (sResourcePath.endsWith(".ddl") || sResourcePath.endsWith(".DDL")) {
      aStatements = split(oBuffer, ';', "GO");
    }
    else {
      aStatements = split(oBuffer, ';', null);
    }

    iStatements = aStatements.length;

    JDCConnection oConn = DAO.getConnection(getClass().getName()+".executeSQLBulk");
    
      switch (iBatchType) {
        case BULK_PROCEDURES:
          for (int s = 0; s < iStatements; s++) {
            sSQL = aStatements[s];
            if (sSQL.length() > 0) {
              if (null!=oStrLog) oStrLog.append(sSQL + "\n");
              try {
                oCall = oConn.prepareCall(sSQL);
                oCall.execute();
                oCall.close();
                oCall = null;
              }
              catch (SQLException sqle) {
                iErrors++;
                if (null!=oStrLog) oStrLog.append("SQLException: " + sqle.getMessage() + "\n");
                try { if (null!=oCall) oCall.close(); } catch (SQLException ignore) { }
                if (bStopOnError) throw new java.lang.InterruptedException();
              }
            } // fi (sSQL)
          } // next
          break;

        case BULK_STATEMENTS:
        case FILE_STATEMENTS:
        case BULK_BATCH:

          oStmt = oConn.createStatement();
          for (int s = 0; s < iStatements; s++) {

            try {
              sSQL = aStatements[s];
            }
            catch (NullPointerException npe) {
              if (null!=oStrLog) oStrLog.append (" NullPointerException: at " + sResourcePath + " statement " + String.valueOf(s) + "\n");
              sSQL = "";
            }

            if (sSQL.length() > 0) {
              if (null!=oStrLog) oStrLog.append(sSQL + "\n\\\n");
              try {
            	if (!sSQL.startsWith("--")) {
                  oStmt.executeUpdate(sSQL);
                }
              }
              catch (SQLException sqle) {
                iErrors++;
                if (null!=oStrLog) oStrLog.append ("SQLException: " + sqle.getMessage() + "\n");

                if (bStopOnError) {
                  try { if (null!=oStmt) oStmt.close(); } catch (SQLException ignore) { }
                  throw new java.lang.InterruptedException();
                }
              }
            } // fi (sSQL)
          } // next
          oStmt.close();
          oStmt = null;
          break;

        case BULK_PLSQL:
          oStmt = oConn.createStatement();
          for (int s = 0; s < iStatements; s++) {
            sSQL = aStatements[s];
            if (sSQL.length() > 0) {
              if (null!=oStrLog) oStrLog.append(sSQL + "\n\\\n");
              try {
                oStmt.execute(sSQL);
              }
              catch (SQLException sqle) {
                iErrors++;
                if (null!=oStrLog) oStrLog.append("SQLException: " + sqle.getMessage() + "\n");

                if (bStopOnError) {
                  try { if (null!=oStmt) oStmt.close(); } catch (SQLException ignore) { }
                  throw new java.lang.InterruptedException();
                }
              }
            } // fi (sSQL)
          } // next
          oStmt.close();
          oStmt = null;
          break;
      } // end switch()

    if (!oConn.getAutoCommit()) oConn.commit();
    oConn.close(getClass().getName()+".executeSQLBulk");
      
    return iErrors;
  } // executeBulk

  // ---------------------------------------------------------------------------

  private int executeBulk(String sResourcePath, int iBatchType)
    throws FileNotFoundException, IOException, SQLException,InterruptedException {
    StringBuffer oBuffer = getSQLBuffer(sResourcePath, iBatchType);
    return executeBulk(oBuffer, sResourcePath, iBatchType);
  }

  // ---------------------------------------------------------------------------

  /**
   * <p>Create a functional module</p>
   * @param sModuleName Name of module to create. Currently only "atril" is supported
   * @return <b>true</b> if module was successfully created, <b>false</b> if errors
   * occured during module creation. Even if error occur module may still be partially
   * created at database after calling create()
   * @throws IllegalStateException If not connected to database
   * @throws FileNotFoundException If any of the internal files for module is not found
   * @throws SQLException
   * @throws IOException
   */
  public boolean createModule(String sModuleName)
    throws IllegalStateException, SQLException, FileNotFoundException, IOException {

    boolean bRetVal = true;

    try {
        executeBulk("tables/"+sDbms+"/"+sModuleName+".sql", BULK_STATEMENTS);
    } catch (InterruptedException ie) {
    if (null!=oStrLog) oStrLog.append("STOP ON ERROR SET TO ON: SCRIPT INTERRUPTED\n");
    bRetVal = false;
  }

  return bRetVal;
 } // createModule

  // ---------------------------------------------------------------------------

  /**
   * <p>Drop a functional module</p>
   * @param sModuleName Name of module to drop
   * @return <b>true</b> if module was successfully dropped, <b>false</b> if errors occurred during dropping module.
   * Even if error occur module may still be partially dropped at database after calling drop()
   * @throws IllegalStateException
   * @throws SQLException
   * @throws FileNotFoundException
   * @throws IOException
   */
  public boolean dropModule(String sModuleName)
    throws IllegalStateException, SQLException, FileNotFoundException,IOException {

    boolean bRetVal = true;

    try {

        executeBulk("drop/" + sDbms + "/"+sModuleName+".sql", BULK_STATEMENTS);
        executeBulk("drop/"+sModuleName+".sql", BULK_STATEMENTS);

    } catch (InterruptedException ie) {
      if (null!=oStrLog) oStrLog.append("STOP ON ERROR SET TO ON: SCRIPT INTERRUPTED\n");
      bRetVal = false;
    }

  return bRetVal;
  } // dropModule

  // ---------------------------------------------------------------------------

  /**
   * <p>Create all modules</p>
   * The created modules will be (in order): atril
   * @throws FileNotFoundException If any of the internal files for modules are not found
   * @throws IllegalStateException
   * @throws SQLException
   * @throws IOException
   */
  public boolean createAllModules()
    throws IllegalStateException, SQLException, FileNotFoundException, IOException {

    if (!createModule ("atril") && bStopOnError) return false;

    return true;
  } // createAll

  // ---------------------------------------------------------------------------

  /**
   * <p>Drop all modules</p>
   * The created modules will be (in order)
   * @throws IllegalStateException
   * @throws SQLException
   * @throws FileNotFoundException
   * @throws IOException
   */
  public boolean dropAllModules()
    throws IllegalStateException, SQLException, FileNotFoundException, IOException {

    if (!dropModule ("atril") && bStopOnError) return false;

    return true;
  } // createAllModules

  // ---------------------------------------------------------------------------

  private void addTypeAttributes(AtrilSession oSess, DocumentType oDoct, Attr[] aAttrs) {
    for (int a=0; a<aAttrs.length; a++) {
      try {
    	oDoct.attribute(aAttrs[a].name);
      } catch (ElementNotFoundException enf) {
    	oDoct.newAttributeType(aAttrs[a].name, aAttrs[a].dataType);
      }
    }
	oDoct.save();
  }

  // ---------------------------------------------------------------------------

  private void createTypeHierarchy(Dms oDms, DocumentType oParentType, String[] aTypesHierarchy) {
	  DocumentType oChildType;
	  for (int t=0; t<aTypesHierarchy.length; t++) {
	    try {
	      oChildType = oDms.getDocumentType(aTypesHierarchy[t]);
	    } catch (ElementNotFoundException enf) {
	      oChildType = oDms.newDocumentType(aTypesHierarchy[t], oParentType);
	      try {
			Class oCls = Class.forName("com.zesped.model."+aTypesHierarchy[t]);
	        BaseModelObject oObj = (BaseModelObject) oCls.newInstance();
		    oChildType.setItem(oObj.isItem());
		    if (oObj.isItem()) oChildType.expectedSize(102400);
	      } catch (ClassNotFoundException e) {
		    oChildType.setItem(false);
	      } catch (InstantiationException e) {
		    oChildType.setItem(false);
		  } catch (IllegalAccessException e) {
		    oChildType.setItem(false);
		  }
	      oChildType.save();
	    }
	    oParentType = oChildType;
	  }
  }

  // ---------------------------------------------------------------------------

  private Document createDocumentOfType(DocumentIndexer oIdx, Dms oDms, String sTypeName, Document oParentDoc) {
	  Document oDoc;
	  SortableList<Document> oLst = oDms.query(sTypeName);
	  if (oLst.isEmpty()) {
		oDoc = oDms.newDocument(oDms.getDocumentType(sTypeName), oParentDoc);
		oDoc.save("");
		oIdx.indexDocument(oDoc);
	  } else {
		oDoc = oLst.get(0);
	  }
	  return oDoc;
  }
  
  // ----------------------------------------------------------

  /**
   * <p>Get an embedded resource file as a String</p>
   * @param sResourcePath Relative path at JAR file from com/knowgate/hipergate/datamodel/ModelManager
   * @param sEncoding Character encoding for resource if it is a text file.<br>
   * If sEncoding is <b>null</b> then UTF-8 is assumed.
   * @return Readed file
   * @throws FileNotFoundException
   * @throws IOException
   */
  public String getResourceAsString (String sResourcePath, String sEncoding)
      throws FileNotFoundException, IOException {

    StringBuffer oXMLSource = new StringBuffer(12000);
    char[] Buffer = new char[4000];
    InputStreamReader oReader = null;
    int iReaded, iSkip;

    if (null==sEncoding) sEncoding = "UTF-8";

    InputStream oIoStrm = this.getClass().getResourceAsStream(sResourcePath);

	if (null==oIoStrm) throw new FileNotFoundException("Resource "+sResourcePath+" not found for class "+this.getClass().getName());

    oReader = new InputStreamReader(oIoStrm, sEncoding);
	
    while (true) {
      iReaded = oReader.read(Buffer, 0, 4000);

      if (-1==iReaded) break;

      // Skip FF FE character mark for Unidode files
      iSkip = ((int)Buffer[0]==65279 || (int)Buffer[0]==65534 ? 1 : 0);

      oXMLSource.append(Buffer, iSkip, iReaded-iSkip);
    } // wend

    oReader.close();
	oIoStrm.close();

    return oXMLSource.toString();

  } // getResourceAsString
  

  // ---------------------------------------------------------------------------

  /**
   * <p>Create a default data model ready for use</p>
   * @throws FileNotFoundException If any of the internal files for modules are not found
   * @throws InstantiationException SAX parser is not properly installed
   * @throws IllegalAccessException SAX parser is not properly installed
   * @throws ClassNotFoundException SAX parser is not properly installed
   * @throws IOException
   * @throws SQLException
   */
  public void createDataModel()
    throws FileNotFoundException, IOException, SQLException, 
    InstantiationException, IllegalAccessException, ClassNotFoundException {

    if (DAO.getRepositoryVersion()==null) {
    	createAllModules();
    }

    AtrilSession oSess = DAO.getAdminSession(getClass().getName()+".createDataModel");

    Dms oDms = oSess.getDms();
    InternationalizationManager oInm = oDms.getInternationalizationManager();
    Locale oES = new Locale("es", "ES");
    
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"Configurations","Endorsements","Endorsement"});
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"Deposits","Deposit","Document","Side"});
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"CaptureTypes","CaptureType","Fields","Field"});
    createTypeHierarchy(oDms, oDms.getDocumentType("CaptureType"), new String[]{"Scanner"});
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"DeviceInformationType"});
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"Zesped","CustomerAccount","TaxPayers","TaxPayer","Invoices","Invoice","InvoiceThumbnail"});
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"Zesped","CaptureServiceFlavor"});
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"Zesped","Countries","Country","States","State","Cities","City"});
    createTypeHierarchy(oDms, oDms.getRootType(), new String[]{"Zesped","VatPercents","VatPercent"});
    createTypeHierarchy(oDms, oDms.getDocumentType("Zesped"), new String[]{"Users","User","AllowedTaxPayers","AllowedTaxPayer"});
    createTypeHierarchy(oDms, oDms.getDocumentType("User"), new String[]{"DeniedTaxPayers","DeniedTaxPayer"});
    createTypeHierarchy(oDms, oDms.getDocumentType("Zesped"), new String[]{"Messages","Message"});
    createTypeHierarchy(oDms, oDms.getDocumentType("Zesped"), new String[]{"Products","Product"});
    createTypeHierarchy(oDms, oDms.getDocumentType("Zesped"), new String[]{"AccountingAccountsDefaults","AccountingAccountDefault"});
    createTypeHierarchy(oDms, oDms.getDocumentType("CustomerAccount"), new String[]{"Orders","Order","OrderLine"});
    createTypeHierarchy(oDms, oDms.getDocumentType("CustomerAccount"), new String[]{"Clients","Client"});
    createTypeHierarchy(oDms, oDms.getDocumentType("CustomerAccount"), new String[]{"CustomerAccountCredits"});
    createTypeHierarchy(oDms, oDms.getDocumentType("TaxPayer"), new String[]{"BillNotes","BillNote","Ticket","TicketThumbnail"});
    createTypeHierarchy(oDms, oDms.getDocumentType("TaxPayer"), new String[]{"Employees","Employee"});
    createTypeHierarchy(oDms, oDms.getDocumentType("TaxPayer"), new String[]{"AccountingAccounts","AccountingAccount"});
    createTypeHierarchy(oDms, oDms.getDocumentType("TaxPayer"), new String[]{"IncomingDeposits"});
    
    addTypeAttributes(oSess, oDms.getDocumentType("AccountingAccount"), new AccountingAccount().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("AccountingAccountDefault"), new AccountingAccountDefault().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("AllowedTaxPayer"), new AllowedTaxPayer().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("CaptureServiceFlavor"), new CaptureServiceFlavor().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("CaptureTypes"), new CaptureTypes().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("CaptureType"), new CaptureType().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("BillNote"), new BillNote().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("City"), new City().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Client"), new Client().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Country"), new Country().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("CustomerAccount"), new CustomerAccount().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("CustomerAccountCredits"), new CustomerAccountCredits().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("DeniedTaxPayer"), new DeniedTaxPayer().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Deposit"), new Deposit().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Deposits"), new Deposits().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("DeviceInformationType"), new DeviceInformationType().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Document"), new com.zesped.model.Document().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Employee"), new Employee().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Endorsement"), new Endorsement().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Field"), new Field().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Invoice"), new Invoice().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("InvoiceThumbnail"), new InvoiceThumbnail().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Message"), new Message().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Order"), new Order().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("OrderLine"), new OrderLine().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Product"), new Product().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Scanner"), new Scanner().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Side"), new Side().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("State"), new State().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("TaxPayer"), new TaxPayer().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("Ticket"), new Ticket().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("TicketThumbnail"), new TicketThumbnail().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("User"), new User().attributes());
    addTypeAttributes(oSess, oDms.getDocumentType("VatPercent"), new VatPercent().attributes());
    
    oInm.setCaptionFormat(oDms.getDocumentType("Zesped"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Zesped"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("CaptureServiceFlavor"), Locale.ENGLISH, "{id}");
    oInm.setCaptionFormat(oDms.getDocumentType("CaptureServiceFlavor"), oES, "{id}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("TaxPayers"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("TaxPayers"), oES, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Invoices"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Invoices"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("Country"), Locale.ENGLISH, "{en}");
    oInm.setCaptionFormat(oDms.getDocumentType("Country"), oES, "{es}");

    oInm.setCaptionFormat(oDms.getDocumentType("State"), Locale.ENGLISH, "{name}");
    oInm.setCaptionFormat(oDms.getDocumentType("State"), oES, "{name}");

    oInm.setCaptionFormat(oDms.getDocumentType("City"), Locale.ENGLISH, "{name}");
    oInm.setCaptionFormat(oDms.getDocumentType("City"), oES, "{name}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("Users"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Users"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("VatPercent"), Locale.ENGLISH, "{percentage} {description}");
    oInm.setCaptionFormat(oDms.getDocumentType("VatPercent"), oES, "{percentage} {description}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccountsDefaults"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccountsDefaults"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccountDefault"), Locale.ENGLISH, "{account_code} - {account_desc}");
    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccountDefault"), oES, "{account_code} - {account_desc}");

    oInm.setCaptionFormat(oDms.getDocumentType("Messages"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Messages"), oES, "{#TypeName}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("Products"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Products"), oES, "{#TypeName}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("Orders"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Orders"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("Clients"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Clients"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("BillNotes"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("BillNotes"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("Employees"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("Employees"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("Employee"), Locale.ENGLISH, "{employee_name} ({employee_id})");
    oInm.setCaptionFormat(oDms.getDocumentType("Employee"), oES, "{employee_name} ({employee_id})");
        
    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccounts"), Locale.ENGLISH, "{#TypeName}");
    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccounts"), oES, "{#TypeName}");

    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccount"), Locale.ENGLISH, "{account_code} - {account_desc}");
    oInm.setCaptionFormat(oDms.getDocumentType("AccountingAccount"), oES, "{account_code} - {account_desc}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("CustomerAccount"), Locale.ENGLISH, "{business_name}");
    oInm.setCaptionFormat(oDms.getDocumentType("CustomerAccount"), oES, "{business_name}");

    oInm.setCaptionFormat(oDms.getDocumentType("CustomerAccountCredits"), Locale.ENGLISH, "Used {credits_used} Left {credits_left}");
    oInm.setCaptionFormat(oDms.getDocumentType("CustomerAccountCredits"), oES, "Usados {credits_used} Disponibles {credits_left}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("Order"), Locale.ENGLISH, "{order_id} / {creation_date}");
    oInm.setCaptionFormat(oDms.getDocumentType("Order"), oES, "{order_id} / {creation_date}");

    oInm.setCaptionFormat(oDms.getDocumentType("OrderLine"), Locale.ENGLISH, "{line_num}: {product_name}");
    oInm.setCaptionFormat(oDms.getDocumentType("OrderLine"), oES, "{line_num}: {product_name}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("TaxPayer"), Locale.ENGLISH, "{business_name} ({tax_id})");
    oInm.setCaptionFormat(oDms.getDocumentType("TaxPayer"), oES, "{business_name} ({tax_id})");
    
    oInm.setCaptionFormat(oDms.getDocumentType("Client"), Locale.ENGLISH, "{business_name} ({tax_id})");
    oInm.setCaptionFormat(oDms.getDocumentType("Client"), oES, "{business_name} ({tax_id})");
    
    oInm.setCaptionFormat(oDms.getDocumentType("Invoice"), Locale.ENGLISH, "{invoice_number}");
    oInm.setCaptionFormat(oDms.getDocumentType("Invoice"), oES, "{invoice_number}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("BillNote"), Locale.ENGLISH, "{employee_name} {comments}");
    oInm.setCaptionFormat(oDms.getDocumentType("BillNote"), oES, "{employee_name} {comments}");

    oInm.setCaptionFormat(oDms.getDocumentType("Ticket"), Locale.ENGLISH, "{ticket_date} {comments}");
    oInm.setCaptionFormat(oDms.getDocumentType("Ticket"), oES, "{ticket_date} {comments}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("User"), Locale.ENGLISH, "{email}");
    oInm.setCaptionFormat(oDms.getDocumentType("User"), oES, "{email}");
    
    oInm.setCaptionFormat(oDms.getDocumentType("Message"), Locale.ENGLISH, "{message_subject}");
    oInm.setCaptionFormat(oDms.getDocumentType("Message"), oES, "{message_subject}");

    oInm.setCaptionFormat(oDms.getDocumentType("Product"), Locale.ENGLISH, "{product_name}");
    oInm.setCaptionFormat(oDms.getDocumentType("Product"), oES, "{product_name} ({price} {currency})");

    oInm.setCaptionFormat(oDms.getDocumentType("CaptureType"), Locale.ENGLISH, "{name}");
    oInm.setCaptionFormat(oDms.getDocumentType("CaptureType"), oES, "{name}");

    oInm.setCaptionFormat(oDms.getDocumentType("DeviceInformationType"), Locale.ENGLISH, "{brand} {model}");
    oInm.setCaptionFormat(oDms.getDocumentType("DeviceInformationType"), oES, "{brand} {model}");
    
    DocumentType oDepT = oDms.getDocumentType("Deposit");
    DocumentType oSidT = oDms.getDocumentType("Side");
    DocumentType oInvT = oDms.getDocumentType("Invoice");
    DocumentType oTckT = oDms.getDocumentType("Ticket");
    DocumentType oIncT = oDms.getDocumentType("IncomingDeposits");
    try {
    	oSidT.addParent(oInvT);
        oSidT.save();
    } catch (IllegalArgumentException yaespadre) { }
    try {
        oSidT.addParent(oTckT);
        oSidT.save();
    } catch (IllegalArgumentException yaespadre) { }
    try {
    	oDepT.addParent(oIncT);
    	oDepT.save();
    } catch (IllegalArgumentException yaespadre) { }

    oSess.commit();

    String sMountBase = "/usr/local/apache-tomcat-6.0.36/webapps/zesped/WEB-INF/volumes/"; // DAO.getVolumesMountBase();
    if (sMountBase.length()>0) {
    	boolean bDefaultVolumeExists = false;
    	VolumeManager oVolm = oDms.getVolumeManager();
    	for (Volume v : oVolm.getVolumeList()) {
    		bDefaultVolumeExists = v.name().equals("Default");
    		if (bDefaultVolumeExists) break;
    	}
    	if (!bDefaultVolumeExists) {
    		sMountBase = Gadgets.chomp(sMountBase, File.separator);
        	FileSystem oFs = new FileSystem();
        	try {
				oFs.mkdirs("file://"+sMountBase+"default");
			} catch (Exception e) {
			  throw new FileNotFoundException("Could not create directory "+sMountBase+"default");
			}
        	if (!new File(sMountBase).exists()) throw new FileNotFoundException("Could not create directory "+sMountBase+"default");
        	Volume oVol = oVolm.createVolume("Default", sMountBase, "default", 20*1048576);
        	oVol.setTotalSpace(100*1048576);
        	oVol.setDescription("Default Zesped Volume");
        	oVol.setMounted(true);
        	oVol.setUsable(true);
        	oVol.setMaxDirectories(1000);
        	oVol.setMaxFiles(1000);
        	oVol.save();
        	oSess.commit();    		
    	}
    }
    
    oSess.disconnect();
    oSess.close();
    
  } // createDataModel

  // ---------------------------------------------------------------------------

  public void writeInfrastructureDocuments() {

	    AtrilSession oSess = DAO.getAdminSession(getClass().getName()+".createDataModel");

	    AuthorizationManager oAum = oSess.getAuthorizationManager();
	    
		Zesped.createGroups(oSess);
		
	    oSess.commit();
	    
		Dms oDms = oSess.getDms();
		DocumentIndexer oIdx = oSess.getDocumentIndexer();
		Document oZespedDoc = null;

		UsersGroup oUsrsGrp = new UsersGroup(oAum, Zesped.getUsersGroup(oAum));
		UsersGroup oOpersGrp = new UsersGroup(oAum, Zesped.getOperatorsGroup(oAum));
		
		SortableList<Document> oLst = oDms.query("DeviceInformationType$brand='TWAIN'");
		if (oLst.isEmpty()) {
			Document oDit = oDms.newDocument(oDms.getDocumentType("DeviceInformationType"), oDms.getRootDocument());
			oDit.attribute("model").set("TWAIN");
			oDit.attribute("brand").set("TWAIN");
			oDit.save("");
			oSess.commit();
			oUsrsGrp.grantReadOnly(oDit);
			oOpersGrp.grantReadOnly(oDit);
			oSess.commit();
		}

		oLst = oDms.query("Endorsement$endorsement_id='01'");
		if (oLst.isEmpty()) {
			Configurations oCnf = null;
			try {
				oCnf = Configurations.top(oSess);
			} catch (ElementNotFoundException enfe) {
				Document oDnf = oDms.newDocument(oDms.getDocumentType("Configurations"), oDms.getRootDocument());
				oDnf.save("");
				oSess.commit();
				oCnf = new Configurations(oDnf);
				oUsrsGrp.grantReadOnly(oDnf);
				oOpersGrp.grantReadOnly(oDnf);
			}
			Document oEns = oDms.newDocument(oDms.getDocumentType("Endorsements"), oCnf.getDocument());
			oEns.save("");
			oSess.commit();
			Document oEnd = oDms.newDocument(oDms.getDocumentType("Endorsement"), oEns);
			oEnd.attribute("endorsement_id").set("01");
			oEnd.attribute("endorsement_mask").set("JJJUUUSSS%03d");
			oEnd.attribute("endorsement_text").set("Ipsa [date_dd/MM/yyyy] [Endorsement.endorsement_mask]");
			oEnd.save("");
			oSess.commit();
		}
		
		try {
			oZespedDoc = Zesped.top(oSess).getDocument();
		} catch (ElementNotFoundException enfe) {
			oZespedDoc = createDocumentOfType(oIdx, oDms, "Zesped", oDms.getRootDocument());		
			oOpersGrp.grantAll(oZespedDoc);
		}
		try {
			Users.top(oSess);
		} catch (ElementNotFoundException enfe) {
			createDocumentOfType(oIdx, oDms, "Users", oZespedDoc);
		}
		try {
			Products.top(oSess);
		} catch (ElementNotFoundException enfe) {
			Document oProds = createDocumentOfType(oIdx, oDms, "Products", oZespedDoc);
			oUsrsGrp.grantReadOnly(oProds);
		}
		try {
			Countries.top(oSess);
		} catch (ElementNotFoundException enfe) {
			Document oCountries = createDocumentOfType(oIdx, oDms, "Countries", oZespedDoc);
			oUsrsGrp.grantReadOnly(oCountries);
		}
		try {
			AccountingAccountsDefaults.top(oSess);
		} catch (ElementNotFoundException enfe) {
			Document oAAccsDefs = createDocumentOfType(oIdx, oDms, "AccountingAccountsDefaults", oZespedDoc);
			oUsrsGrp.grantReadOnly(oAAccsDefs);
		}
		try {
			VatPercents.top(oSess);
		} catch (ElementNotFoundException enfe) {
			Document oVatPcts = createDocumentOfType(oIdx, oDms, "VatPercents", oZespedDoc);
			oUsrsGrp.grantReadOnly(oVatPcts);
		}
			
	    oSess.commit();
	    oSess.disconnect();
	    oSess.close();
  }
  
  // ---------------------------------------------------------------------------

  public Properties getConnectionProperties() {
	  return oAtrilProperties;
  }

  // ---------------------------------------------------------------------------
  
  public void writeCountries(AtrilSession oSes) throws IOException {
	  InputStream oIoEs = getClass().getResourceAsStream("data/countries.es.properties");
	  InputStream oIoEn = getClass().getResourceAsStream("data/countries.en.properties");
	  try {
		InputStreamReader oRdrEs = new InputStreamReader(oIoEs, "ISO8859_1");
		InputStreamReader oRdrEn = new InputStreamReader(oIoEn, "ISO8859_1");
		LineNumberReader oLnEs = new LineNumberReader(oRdrEs);
		LineNumberReader oLnEn = new LineNumberReader(oRdrEn);
		String sEs, sEn;
		Countries oCntrs = Countries.top(oSes);
		HashMap<String,String> oNames = new HashMap<String,String>();
		do {
			sEs = oLnEs.readLine();
			sEn = oLnEn.readLine();
			if (sEs!=null && sEn!=null) {
				String[] aEs = sEs.split("=");
				String[] aEn = sEn.split("=");
				if (new Country().exists(oSes, "id", aEs[0])==null) {
					oNames.clear();
					oNames.put("es", aEs[1]);
					oNames.put("en", aEn[1]);
					oCntrs.create(oSes, aEs[0], oNames);
				}
			} // fi
		} while (sEs!=null && sEn!=null);
		oLnEn.close();
		oLnEs.close();
		oRdrEs.close();
		oRdrEn.close();
		oIoEs.close();
		oIoEn.close();
	  } catch (UnsupportedEncodingException neverthrown) {
	  }
  }

  // ---------------------------------------------------------------------------
  
  public void writeStates(AtrilSession oSes, String sCountryCode) throws IOException {
	  InputStream oIoEs = getClass().getResourceAsStream("data/states."+sCountryCode+".properties");
	  try {
		InputStreamReader oRdrEs = new InputStreamReader(oIoEs, "ISO8859_1");
		LineNumberReader oLnEs = new LineNumberReader(oRdrEs);
		String sEs;
		Country oCntr = Countries.top(oSes).getCountry(oSes, sCountryCode);
		States oStts = oCntr.states(oSes);
		do {
			sEs = oLnEs.readLine();
			if (sEs!=null) {
				String[] aEs = sEs.split("=");
				if (new State().exists(oSes, "code", aEs[0])==null) {
					oStts.create(oSes, aEs[0], aEs[1]);
				}
			} // fi
		} while (sEs!=null);
		oLnEs.close();
		oRdrEs.close();
		oIoEs.close();
	  } catch (UnsupportedEncodingException neverthrown) {
	  }
  }

  // ---------------------------------------------------------------------------
  
  public void writeCities(AtrilSession oSes, String sCountryCode) throws IOException {
	  if (new City().exists(oSes, "name", "LECAROZ")!=null) return;
	  HashMap<String,Document> oCitiesCache = new HashMap<String,Document>();
	  InputStream oIoEs = getClass().getResourceAsStream("data/cities."+sCountryCode+".csv");
	  try {
		InputStreamReader oRdrEs = new InputStreamReader(oIoEs, "ISO8859_1");
		LineNumberReader oLnEs = new LineNumberReader(oRdrEs);
		String sEs;
		Country oCntr = Countries.top(oSes).getCountry(oSes, sCountryCode);
		States oStts = oCntr.states(oSes);
		Document oCtts;
		do {
			sEs = oLnEs.readLine();
			if (sEs!=null) {
				String[] aEs = sEs.split("\t");
				if (oCitiesCache.containsKey(aEs[0])) {
					oCtts = oCitiesCache.get(aEs[0]);
				} else {
					oCtts = oStts.getState(oSes, aEs[0]).cities(oSes).getDocument();
					oCitiesCache.put(aEs[0], oCtts);
				}
				City oCity = new City(oSes, oCtts);
				oCity.put("state_code", aEs[0]);
				oCity.put("name", aEs[1]);
				oCity.put("city_id", aEs[2]);
				oCity.put("zipcode", aEs[3]);
				oCity.save(oSes);
			}
		} while (sEs!=null);
		oLnEs.close();
		oRdrEs.close();
		oIoEs.close();
	  } catch (UnsupportedEncodingException neverthrown) {
	  }
  }
  
  // ---------------------------------------------------------------------------
  
  public void writeServiceFlavors(AtrilSession oSes) throws IOException {
		CaptureServiceFlavor oCsfl = new CaptureServiceFlavor();
		if (oCsfl.exists(oSes,"id","INVOICESBASIC")==null)
			new CaptureServiceFlavor(oSes, "INVOICESBASIC", CaptureService.INVOICES, "Captura Basica de Facturas", 2).save(oSes);
		if (oCsfl.exists(oSes,"id","INVOICESPREMIUM")==null)
			new CaptureServiceFlavor(oSes, "INVOICESPREMIUM", CaptureService.INVOICES, "Captura Premium de Facturas", 4).save(oSes);
		if (oCsfl.exists(oSes,"id","BILLNOTESBASIC")==null)
			new CaptureServiceFlavor(oSes, "BILLNOTESBASIC", CaptureService.BILLNOTES, "Captura Basica de Notas de Gasto", 1).save(oSes);
  }

  // ---------------------------------------------------------------------------
  
  public void writeProducts(AtrilSession oSes) throws IOException {
		Product oProd = new Product();
		if (oProd.exists(oSes,"product_id","FREETRIAL2020")==null)	
		  new Product(oSes, "FREETRIAL2020", "Prueba Gratuita 20 Facturas + 20 Notas de Gasto", 60l, "0", "EUR").save(oSes);
		if (oProd.exists(oSes,"product_id","BASICINVOICES100")==null)	
		  new Product(oSes, "BASICINVOICES100", "100 Facturas Basicas", 200l, "50", "EUR").save(oSes);
		if (oProd.exists(oSes,"product_id","PREMIUMINVOICES100")==null)	
		  new Product(oSes, "PREMIUMINVOICES100", "100 Facturas Premium", 200l, "150", "EUR").save(oSes);
		if (oProd.exists(oSes,"product_id","BILLNOTES100")==null)	
		  new Product(oSes, "BILLNOTES100", "100 Notas de Gasto", 200l, "150", "EUR").save(oSes);
  }

  // ---------------------------------------------------------------------------
  
  public void writeVatPercents(AtrilSession oSes) throws IOException {
	  VatPercent oVpct = new VatPercent();
	  if (oVpct.exists(oSes,"percentage","0")==null)
		  VatPercents.create(oSes, BigDecimal.ZERO, "Exento");
	  if (oVpct.exists(oSes,"percentage","0.03")==null)
		  VatPercents.create(oSes, new BigDecimal("0.03"), "IGIC Reducido");
	  if (oVpct.exists(oSes,"percentage","0.04")==null)
		  VatPercents.create(oSes, new BigDecimal("0.04"), "Superreducido");
	  if (oVpct.exists(oSes,"percentage","0.05")==null)
		  VatPercents.create(oSes, new BigDecimal("0.05"), "IGIC General");
	  if (oVpct.exists(oSes,"percentage","0.095")==null)
		  VatPercents.create(oSes, new BigDecimal("0.095"), "IGIC Incrementado");
	  if (oVpct.exists(oSes,"percentage","0.135")==null)
		  VatPercents.create(oSes, new BigDecimal("0.135"), "IGIC Incrementado");
	  if (oVpct.exists(oSes,"percentage","0.1")==null)
		  VatPercents.create(oSes, new BigDecimal("0.1"), "Reducido");
	  if (oVpct.exists(oSes,"percentage","0.21")==null)
		  VatPercents.create(oSes, new BigDecimal("0.21"), "General");
  }

  // ---------------------------------------------------------------------------
  
  public void writeAccountingAccounts(AtrilSession oSes) throws IOException {
		AccountingAccountDefault oAccd = new AccountingAccountDefault();
		if (oAccd.exists(oSes,"account_code","6290004")==null)
			new AccountingAccountDefault(oSes, "6290004", "Desplazamientos").save(oSes);
		if (oAccd.exists(oSes,"account_code","6290006")==null)
			new AccountingAccountDefault(oSes, "6290006", "Hospedaje y manutencion").save(oSes);
		if (oAccd.exists(oSes,"account_code","6290007")==null)
			new AccountingAccountDefault(oSes, "6290007", "Locomocion empleados").save(oSes);
		if (oAccd.exists(oSes,"account_code","6290008")==null)
			new AccountingAccountDefault(oSes, "6290008", "Comida empleados").save(oSes);
		if (oAccd.exists(oSes,"account_code","6270003")==null)
			new AccountingAccountDefault(oSes, "6270003", "Gastos de representacion").save(oSes);
  }

  // ---------------------------------------------------------------------------

  public static void createScanner(AtrilSession oSes, CaptureType oCpt, String sScanMode) throws IOException {
	  try {
		  Scanner oScr = oCpt.seek (oSes, "Twain");
		  oScr.mimeType("text/plain");
		  oScr.insertContentFromInputStream(oSes, ModelManager.class.getResourceAsStream("Twain"+sScanMode+".properties"), "Twain.properties");
		  oScr.save(oSes);
	  } catch (ElementNotFoundException enfe) {
		  oCpt.addScanner(oSes, "Twain", ModelManager.class.getResourceAsStream("Twain"+sScanMode+".properties"));
	  }
  }

  // ---------------------------------------------------------------------------
  
  public static void createCaptureTypes(AtrilSession oSes) throws IOException {
	  final String[] aCaptureModes = new String[]{"UnsignedSinglePage","SignedSinglePage","ServerSignedSinglePage","UnsignedMultiPage","SignedMultiPage","ServerSignedMultiPage"};
	  final String[] aScanModes = new String[]{"FullDuplexGUI","FullDuplexNoGUI","HalfDuplexGUI","HalfDuplexNoGUI"};
	  final int nCaptureModes = aCaptureModes.length;
	  final int nScanModes = aScanModes.length;

	  Dms oDms = oSes.getDms();
	  
	  AuthorizationManager oAum = oSes.getAuthorizationManager();
      UsersGroup oUsrsGrp = new UsersGroup(oAum, Zesped.getUsersGroup(oAum));
	  UsersGroup oOpersGrp = new UsersGroup(oAum, Zesped.getOperatorsGroup(oAum));
	  
	  CaptureType oCpt = new CaptureType();	  
	  CaptureTypes oCts = null;
	  
	  try {
		  oCts = CaptureTypes.top(oSes);
	  } catch(ElementNotFoundException enfe)   {
		  Document dCts = oDms.newDocument(oDms.getDocumentType("CaptureTypes"), oDms.getRootDocument());
		  dCts.save("");
		  oUsrsGrp.grantReadOnly(dCts);
		  oOpersGrp.grantReadOnly(dCts);
		  oSes.commit();
		  VolumeManager oVolm = oDms.getVolumeManager();
		  Volume oVol = DAO.defaultVolume(oVolm);
		  oVol.addDocument(dCts);
		  oVol.save();
		  oSes.commit();
		  oCts = new CaptureTypes(dCts);
	  }
	  
	  Document oDoc;
	  for (int c=0; c<nCaptureModes; c++) {
		  for (int s=0; s<nScanModes; s++) {
			  String sCaptureType = aCaptureModes[c]+aScanModes[s];
			  oDoc = oCpt.exists(oSes, "name", sCaptureType);
			  if (oDoc==null) {
				  oCpt = oCts.create(oSes, BaseItemObject.TYPE_PDF, true,
						  			 sCaptureType.indexOf("MultiPage")>=0, sCaptureType.startsWith("Signed"),
						  			 sCaptureType.startsWith("ServerSigned"),
						             aCaptureModes[c]+aScanModes[s], "");
			  } else {
				  oCpt = new CaptureType(oDoc);
				  oCpt.put("MimeType", BaseItemObject.TYPE_PDF);
				  oCpt.put("Sign", sCaptureType.startsWith("Signed") ? 1 : 0);
				  oCpt.put("SignInClient", sCaptureType.startsWith("Signed") ? 1 : 0);
				  oCpt.put("SignInServer", sCaptureType.startsWith("ServerSigned") ? 1 : 0);
				  oCpt.put("MultiPageItem", 1);
				  oCpt.put("MultiSheetDocument", sCaptureType.indexOf("MultiPage")>0 ? 1 : 0);
				  oCpt.put("bitsDepth", 0);
				  oCpt.save(oSes);
			  }
			  createScanner(oSes, oCpt, aScanModes[s]);
		  }
	  }	  
  }

  // ---------------------------------------------------------------------------

  public static void createDeposits(AtrilSession oSes) throws IOException {
	  Deposits d;
	  try {
		  Deposits.top(oSes);
	  } catch (ElementNotFoundException enfe) {
		  d = new Deposits(oSes);
		  d.put("disabled", 0);
		  d.save(oSes);
		  oSes.commit();
		  AuthorizationManager oAum = oSes.getAuthorizationManager();
	      UsersGroup oUsrsGrp = new UsersGroup(oAum, Zesped.getUsersGroup(oAum));
		  UsersGroup oOpersGrp = new UsersGroup(oAum, Zesped.getOperatorsGroup(oAum));
		  oUsrsGrp.grantAll(d.getDocument());
		  oOpersGrp.grantAll(d.getDocument());
	  }
  }

  // ---------------------------------------------------------------------------

  public static void createCache(JDCConnection oCon) {
	  Statement oStm = null;
	  try {
		  oStm = oCon.createStatement();
		  oStm.execute("CREATE TABLE Cache (cid VARCHAR(50) NOT NULL,dts TIMESTAMP NOT NULL,val MEDIUMBLOB NOT NULL,CONSTRAINT PRIMARY KEY (cid))");
	  } catch (SQLException sqle) { }
	  try {
		  if (oStm!=null) oStm.close();
	  } catch (SQLException sqle) { }
  }
  
  
  // ---------------------------------------------------------------------------
  
  public static void main(String[] args)
    throws Exception {
	
	ModelManager oMan = new ModelManager();
	DAO oDao = null;
	
	oDao = new DAO();    
	oDao.init(oMan.getConnectionProperties());
    oMan.createDataModel();
	oMan.writeInfrastructureDocuments();
	oDao.destroy();

	oDao = new DAO();
    oDao.init(oMan.getConnectionProperties());

    JDCConnection oCon = DAO.getConnection("ModelManager");
    createCache(oCon);
    oCon.close("ModelManager");

	AtrilSession oSes = DAO.getAdminSession("ModelManager");

	createDeposits(oSes);	
	oSes.commit();
	
	createCaptureTypes(oSes);
	oSes.commit();
	
	oMan.writeServiceFlavors(oSes);
	oSes.commit();

	oMan.writeProducts(oSes);
	oSes.commit();
	
	oMan.writeVatPercents(oSes);
	oSes.commit();
	
	oMan.writeAccountingAccounts(oSes);
	oSes.commit();

	oMan.writeCountries(oSes);
	oSes.commit();

	oMan.writeStates(oSes, "es");
	oSes.commit();

	/*
	oMan.writeCities(oSes, "es");
	oSes.commit();
	*/
	
    oSes.disconnect();
    oSes.close();
    oDao.destroy();
	System.out.println("Done!");
  }


} // ModelManager
