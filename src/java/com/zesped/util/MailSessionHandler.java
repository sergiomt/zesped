
package com.zesped.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Iterator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URL;
import java.nio.charset.Charset;
import java.security.Security;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.NoSuchProviderException;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.URLName;
import javax.mail.Session;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.Folder;


import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;
import javax.mail.internet.InternetAddress;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.StringSubstitution;
import org.apache.oro.text.regex.Util;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.sun.mail.smtp.SMTPMessage;
import com.sun.net.ssl.internal.ssl.Provider;

import com.knowgate.dfs.ByteArrayDataSource;
import com.knowgate.dfs.FileSystem;
import com.knowgate.misc.Gadgets;
import com.knowgate.misc.Hosts;
import com.knowgate.dataxslt.FastStreamReplacer;

/**
 * <p>A wrapper around javax.mail.Store and javax.mail.Transport</p>
 * @author Sergio Montoro Ten
 * @version 8.0
 */
public class MailSessionHandler {

  protected String sInAccountName;
  protected String sInAuthStr;
  protected String sOutAccountName;
  protected String sOutAuthStr;
  protected String sInHostName;
  protected String sOutHostName;
  protected String sMBoxDir;
  protected Properties oProps;
  protected URLName oURLSession;
  protected Session oMailSession;
  protected Session oSmtpSession;
  protected Store oMailStore;
  protected Transport oMailTransport;
  protected boolean bIsStoreConnected;
  protected boolean bIsTransportConnected;
  protected boolean bIncomingSSL;
  protected boolean bOutgoingSSL;

  // ---------------------------------------------------------------------------

  /**
   * Default constructor
 * @throws IOException 
   */
  public MailSessionHandler() throws IOException {
	Properties oMailProperties = new Properties();
	oMailProperties.load(getClass().getResourceAsStream("mail.properties"));
    loadProperties(oMailProperties);
  }

  /**
   * Create session using given Properties
   * @param oMailProperties Properties<br>
   * <table><tr><th>Property</th><th>Description></th><th>Default value</th></tr>
   *        <tr><td>mail.user</td><td>Store and transport user</td><td></td></tr>
   *        <tr><td>mail.password</td><td></td>Store and transport password<td></td></tr>
   *        <tr><td>mail.store.protocol</td><td></td><td>pop3</td></tr>
   *        <tr><td>mail.transport.protocol</td><td></td><td>smtp</td></tr>
   *        <tr><td>mail.<i>storeprotocol</i>.host</td><td>For example: pop.mailserver.com</td><td></td></tr>
   *        <tr><td>mail.<i>storeprotocol</i>.socketFactory.class</td><td>Only if using SSL set this value to javax.net.ssl.SSLSocketFactory</td><td></td></tr>
   *        <tr><td>mail.<i>storeprotocol</i>.socketFactory.port</td><td>Only if using SSL</td><td></td></tr>
   *        <tr><td>mail.<i>transportprotocol</i>.host</td><td>For example: smtp.mailserver.com</td><td></td></tr>
   *        <tr><td>mail.<i>transportprotocol</i>.socketFactory.class</td><td>Only if using SSL set this value to javax.net.ssl.SSLSocketFactory</td><td></td></tr>
   *        <tr><td>mail.<i>transportprotocol</i>.socketFactory.port</td><td>Only if using SSL</td><td></td></tr>
   *        <tr><td>proxySet</td><td>Use proxy</td><td>false</td></tr>
   *        <tr><td>socksProxyHost</td><td>Proxy IP address</td><td></td></tr>
   *        <tr><td>socksProxyPort</td><td>Proxy Port</td><td></td></tr>
   * </table>
   * @throws NullPointerException if oMailProperties is null
   * @since 3.1
   */
  public MailSessionHandler(Properties oMailProperties)
    throws NullPointerException {
	  loadProperties(oMailProperties);
  }

  // ---------------------------------------------------------------------------

  private void loadProperties(Properties oMailProperties)
		    throws NullPointerException {
			oProps = oMailProperties;
		    sOutAccountName = sInAccountName = oProps.getProperty("mail.user");
		    sOutAuthStr = sInAuthStr = oProps.getProperty("mail.password");
		    bIsStoreConnected = bIsTransportConnected = false;
			oMailTransport = null;
			oMailStore = null;
			sMBoxDir = null;
			oMailSession = null;
			String sStoreProtocol = oProps.getProperty("mail.store.protocol", "pop3");
		    sInHostName = oProps.getProperty("mail."+sStoreProtocol+".host");
			bIncomingSSL = oProps.getProperty("mail."+sStoreProtocol+".socketFactory.class", "").equals("javax.net.ssl.SSLSocketFactory");
		    if (sInAuthStr!=null) {
		        oProps.put("mail."+sStoreProtocol+".auth", "true");
		      }
		    if (bIncomingSSL) {
		      oProps.setProperty("mail."+sStoreProtocol+".socketFactory.port", oMailProperties.getProperty("mail."+sStoreProtocol+".port"));
		      oProps.setProperty("mail."+sStoreProtocol+".socketFactory.fallback", "false");	
		    }
			String sTransportProtocol = oProps.getProperty("mail.transport.protocol", "smtp");
		    sOutHostName = oProps.getProperty("mail."+sTransportProtocol+".host");
			bOutgoingSSL = oProps.getProperty("mail."+sTransportProtocol+".socketFactory.class", "").equals("javax.net.ssl.SSLSocketFactory");
		    if (sOutAuthStr!=null) {
		      oProps.put("mail."+sTransportProtocol+".auth", "true");
		    }
		    if (bOutgoingSSL) {
		        oProps.setProperty("mail."+sTransportProtocol+".socketFactory.port", oMailProperties.getProperty("mail."+sTransportProtocol+".port"));
		        oProps.setProperty("mail."+sTransportProtocol+".socketFactory.fallback", "false");	
		        oProps.put("mail."+sTransportProtocol+".starttls.enable","true");
		    }
		    if (bIncomingSSL || bOutgoingSSL) {
		      Security.addProvider(new Provider());
		    }
		  }
  
  // ---------------------------------------------------------------------------

  /**
   * <p>Get column incoming_account of k_user_mail</p>
   * @return String account name or <b>null</b> if this instance has not been
   * initialized from a MailAccount object
   */
  public String getAccountName() {
    return sInAccountName;
  }

  // ---------------------------------------------------------------------------

  /**
   * Set incoming mail account name
   */
  public void setAccountName(String aAccName) {
    sInAccountName=aAccName;
  }

  // ---------------------------------------------------------------------------

  /**
   * <p>Get column incoming_password of k_user_mail</p>
   * @return String password or <b>null</b> if this instance has not been
   * initialized from a MailAccount object
   */
  public String getAuthStr() {
    return sInAuthStr;
  }

  // ---------------------------------------------------------------------------

  /**
   * Set incoming mail password
   */
  public void setAuthStr(String aAutStr) {
    sInAuthStr=aAutStr;
  }

  // ---------------------------------------------------------------------------

  /**
   * <p>Get column incoming_server of k_user_mail</p>
   * @return String
   */
  public String getHostName() {
    return sInHostName;
  }

  // ---------------------------------------------------------------------------

  /**
   * Set incoming mail host name or IP address
   */
  public void setHostName(String sName) {
    sInHostName=sName;
  }

  // ---------------------------------------------------------------------------

  public String getMBoxDirectory() {
    return sMBoxDir;
  }

  // ---------------------------------------------------------------------------

  public void setMBoxDirectory(String sDir) {
    sMBoxDir=sDir;
  }

  // ---------------------------------------------------------------------------

  public Properties getProperties() {
    return oProps;
  }

  // ---------------------------------------------------------------------------

  public void setProperties(Properties oPropties) {
    oProps=oPropties;
  }

  // ---------------------------------------------------------------------------

  /**
   * <p>Get incoming mail server Session</p>
   * This method calls JavaMail Session.getInstance() method if neccesary,
   * using properties currently set at this instance and SilentAuthenticator as
   * Authenticator subclass
   * @return javax.mail.Session
   * @throws IllegalStateException
   * @throws NullPointerException
   */
  public Session getSession() throws IllegalStateException {
    if (null==oMailSession) {
      if (null==oProps) {
        throw new IllegalStateException("SessionHandler properties not set");
      }
      if (null==sInAccountName) {
        throw new NullPointerException("SessionHandler account name not set");
      }
      SilentAuthenticator oAuth = new SilentAuthenticator(sInAccountName, sInAuthStr);
      oMailSession = Session.getInstance(oProps, oAuth);
    }
    return oMailSession;
  } // getSession

  // ---------------------------------------------------------------------------

  /**
   * <p>Get outgoing mail server Session</p>
   * This method calls JavaMail Session.getInstance() method if neccesary,
   * using properties currently set at this instance and SilentAuthenticator as
   * Authenticator subclass
   * @return javax.mail.Session
   * @throws IllegalStateException
   * @throws NullPointerException
   */
  public Session getSmtpSession() throws IllegalStateException {
    if (null==oSmtpSession) {
      if (null==oProps) {
        throw new IllegalStateException("SessionHandler.getSmtpSession() properties not set");
      }
      if (null==sOutAccountName) {
        sOutAccountName = "";
      }
      if (sOutAccountName.trim().length()==0) {
        oSmtpSession = Session.getInstance(oProps);
      } else {
        SilentAuthenticator oAuth = new SilentAuthenticator(sOutAccountName, sOutAuthStr);
        oSmtpSession = Session.getInstance(oProps, oAuth);
      } // fi
    }
    return oSmtpSession;
  } // getSmtpSession

  // ---------------------------------------------------------------------------

  /**
   * <p>Get Store</p>
   * This method calls Session.getStore() and Store.connect() if neccesary.
   * @return javax.mail.Store
   * @throws NoSuchProviderException
   * @throws MessagingException
   */
  public Store getStore() throws NoSuchProviderException, MessagingException {
    if (null==oMailStore) {
      if (null==sInHostName) {
        throw new NullPointerException("SessionHandler host name not set");
      }
      oMailStore = getSession().getStore();
      getStore().connect(sInHostName, sInAccountName, sInAuthStr);
      bIsStoreConnected = true;
    }
    return oMailStore;
  } // getStore()

  // ---------------------------------------------------------------------------

  /**
   * <p>Get Transport</p>
   * This method calls Session.getTransport() and Transport.connect() if neccesary
   * @return javax.mail.Transport
   * @throws NoSuchProviderException
   * @throws MessagingException
   */
  public Transport getTransport()
    throws NoSuchProviderException,MessagingException {
    if (null==oMailTransport) {
      oMailTransport = getSmtpSession().getTransport();
      oMailTransport.connect();
    }
    return oMailTransport;
  } // getTransport

  // ---------------------------------------------------------------------------

  /**
   * Get folder from current mail store
   * @return javax.mail.Folder
   * @throws NoSuchProviderException
   * @throws MessagingException
   */
  public Folder getFolder(String sFolderName)
      throws NoSuchProviderException,MessagingException {
    getStore();
    if (null==oMailStore)
      return null;
    else {
      return oMailStore.getFolder(sFolderName);
    }
  } // getFolder

  // ---------------------------------------------------------------------------

  public URLName getURL() {
    if (null==oURLSession) {
      oURLSession = new URLName("jdbc://", sInHostName, -1, sMBoxDir, sInAccountName, sInAuthStr);
    }
    return oURLSession;
  }

  // ---------------------------------------------------------------------------

  public boolean isStoreConnected() {
    return bIsStoreConnected;
  }

  // ---------------------------------------------------------------------------

  public boolean isTransportConnected() {
    return bIsTransportConnected;
  }

  // ---------------------------------------------------------------------------

  public void sendMessage(Message oMsg)
    throws NoSuchProviderException,SendFailedException,ParseException,
           MessagingException,NullPointerException,IllegalStateException {
    
    oMsg.setSentDate(new java.util.Date());
    
    Transport.send(oMsg);
    
  } // sendMessage

  // ---------------------------------------------------------------------------

  public void sendMessage(Message oMsg, Address[] aAddrs)
    throws NoSuchProviderException,SendFailedException,ParseException,
           MessagingException,NullPointerException {
    oMsg.setSentDate(new java.util.Date());
    Transport.send(oMsg,aAddrs);
  } // sendMessage

  // ---------------------------------------------------------------------------

  public void sendMessage (Message oMsg,
                           Address[] aAdrFrom, Address[] aAdrReply,
                           Address[] aAdrTo, Address[] aAdrCc, Address[] aAdrBcc)
    throws NoSuchProviderException,SendFailedException,ParseException,
           MessagingException,NullPointerException {           	
    oMsg.addFrom(aAdrFrom);
    if (null==aAdrReply)
      oMsg.setReplyTo(aAdrReply);
    else
      oMsg.setReplyTo(aAdrFrom);
    if (aAdrTo!=null) oMsg.addRecipients(javax.mail.Message.RecipientType.TO, aAdrTo);
    if (aAdrCc!=null) oMsg.addRecipients(javax.mail.Message.RecipientType.CC, aAdrCc);
    if (aAdrBcc!=null) oMsg.addRecipients(javax.mail.Message.RecipientType.BCC, aAdrBcc);
    oMsg.setSentDate(new java.util.Date());
    Transport.send(oMsg);
  } // sendMessage

  // ---------------------------------------------------------------------------

  public void close()
    throws MessagingException {
    if (null!=oMailStore) {
      if (isStoreConnected()) {
        oMailStore.close();
      }
      oMailStore = null;
    }
    if (null!=oMailTransport) {
      if (isTransportConnected()) {
        oMailTransport.close();
      }
      oMailTransport=null;
    }
    oMailSession=null;
    oSmtpSession=null;
  } // close

  // ---------------------------------------------------------------------------
  
  private SMTPMessage composeMessage(String sSubject, String sEncoding,
		                             String sTextBody, String sHtmlBody,
		                             String sId, String [] aAttachmentsPath,
		                             String sBasePath)
  throws IOException,MessagingException,IllegalArgumentException,SecurityException {

  PatternCompiler oCompiler = new Perl5Compiler();
  PatternMatcher oMatcher = new Perl5Matcher();
  
  String sContentType = (sHtmlBody==null ? "plain" : "html");
  
  if (sEncoding==null) sEncoding = "ASCII";
  String sCharEnc = Charset.forName(sEncoding).name();
  	
  SMTPMessage oSentMessage = new SMTPMessage(getSmtpSession());

  MimeBodyPart oMsgPlainText = new MimeBodyPart();
  MimeMultipart oSentMsgParts = new MimeMultipart("mixed");

  if (sContentType.equalsIgnoreCase("html")) {

    MimeMultipart oHtmlRelated  = new MimeMultipart("related");
    MimeMultipart oTextHtmlAlt  = new MimeMultipart("alternative");

    // ************************************************************************
    // Replace image CIDs

    HashMap oDocumentImages = new HashMap(23);

    StringSubstitution oSrcSubs = new StringSubstitution();

    Parser oPrsr = Parser.createParser(sHtmlBody, sEncoding);

    String sCid, sSrc;

    try {

      if (sTextBody==null) {
          // ****************************
          // Extract plain text from HTML

          StringBean oStrBn = new StringBean();

          try {
            oPrsr.visitAllNodesWith (oStrBn);
          } catch (ParserException pe) {
          throw new MessagingException(pe.getMessage(), pe);
          }

          sTextBody = oStrBn.getStrings();

          oStrBn = null;
      } // fi (sTextBody==null)

      // *******************************
      // Set plain text alternative part

      oMsgPlainText.setDisposition("inline");
      oMsgPlainText.setText(sTextBody, sCharEnc, "plain");
      // oMsgPlainText.setContent(sTextBody, "text/plain; charset="+sCharEnc);
      oTextHtmlAlt.addBodyPart(oMsgPlainText);

      // *****************************************
      // Iterate images from HTML and replace CIDs

      NodeList oCollectionList = new NodeList();
      TagNameFilter oImgFilter = new TagNameFilter ("IMG");
      for (NodeIterator e = oPrsr.elements(); e.hasMoreNodes();)
        e.nextNode().collectInto(oCollectionList, oImgFilter);

      final int nImgs = oCollectionList.size();

      for (int i=0; i<nImgs; i++) {

        sSrc = ((ImageTag) oCollectionList.elementAt(i)).extractImageLocn();

        // Keep a reference to every related image name so that the same image is not included twice in the message
        if (!oDocumentImages.containsKey(sSrc)) {

          // Find last slash from image url
          int iSlash = sSrc.lastIndexOf('/');

          // Take image name
          if (iSlash>=0) {
            while (sSrc.charAt(iSlash)=='/') { if (++iSlash==sSrc.length()) break; }
            sCid = sSrc.substring(iSlash);
          }
          else {
            sCid = sSrc;
          }

          oDocumentImages.put(sSrc, sCid);
        } // fi (!oDocumentImages.containsKey(sSrc))

        try {
          Pattern oPattern = oCompiler.compile(sSrc, Perl5Compiler.SINGLELINE_MASK);
          oSrcSubs.setSubstitution("cid:"+oDocumentImages.get(sSrc));
          sHtmlBody = Util.substitute(oMatcher, oPattern, oSrcSubs, sHtmlBody);
        } catch (MalformedPatternException neverthrown) { }

      } // next
    }
    catch (ParserException pe) {
    }
    // End replace image CIDs
    // ************************************************************************

    // ************************************************************************
    // Add HTML related images

    if (oDocumentImages.isEmpty()) {
        // Set HTML part
        MimeBodyPart oMsgHtml = new MimeBodyPart();
        oMsgHtml.setDisposition("inline");
        oMsgHtml.setText(sHtmlBody, sCharEnc, "html");
        // oMsgHtml.setContent(sHtmlBody, "text/html; charset="+sCharEnc);
        oTextHtmlAlt.addBodyPart(oMsgHtml);
    } else {

      // Set HTML text related part

      MimeBodyPart oMsgHtmlText = new MimeBodyPart();
      oMsgHtmlText.setDisposition("inline");
      oMsgHtmlText.setText(sHtmlBody, sCharEnc, "html");
      // oMsgHtmlText.setContent(sHtmlBody, "text/html; charset="+sCharEnc);
      oHtmlRelated.addBodyPart(oMsgHtmlText);

      // Set HTML text related inline images

      Iterator oImgs = oDocumentImages.keySet().iterator();

      while (oImgs.hasNext()) {
        BodyPart oImgBodyPart = new MimeBodyPart();

        sSrc = (String) oImgs.next();
        sCid = (String) oDocumentImages.get(sSrc);

        if (sSrc.startsWith("www."))
          sSrc = "http://" + sSrc;

        if (sSrc.startsWith("http://") || sSrc.startsWith("https://")) {
          oImgBodyPart.setDataHandler(new DataHandler(new URL(Hosts.resolve(sSrc))));
        }
        else {
          oImgBodyPart.setDataHandler(new DataHandler(new FileDataSource((sBasePath==null ? "" : sBasePath)+sSrc)));
        }

        oImgBodyPart.setDisposition("inline");
        oImgBodyPart.setHeader("Content-ID", sCid);
        oImgBodyPart.setFileName(sCid);

        // Add image to multi-part
        oHtmlRelated.addBodyPart(oImgBodyPart);
      } // wend

      // Set html text alternative part (html text + inline images)
      MimeBodyPart oTextHtmlRelated = new MimeBodyPart();
      oTextHtmlRelated.setContent(oHtmlRelated);
      oTextHtmlAlt.addBodyPart(oTextHtmlRelated);
    }

    // ************************************************************************
    // Create message to be sent and add main text body to it

    if (aAttachmentsPath==null) {
      oSentMessage.setContent(oTextHtmlAlt);
    } else {
      MimeBodyPart oMixedPart = new MimeBodyPart();
      oMixedPart.setContent(oTextHtmlAlt);
      oSentMsgParts.addBodyPart(oMixedPart);
    }

  } else { // (sContentType=="plain")

    // *************************************************
    // If this is a plain text message just add the text

    if (aAttachmentsPath==null) {
      oSentMessage.setText(sTextBody, sCharEnc);
    } else {
      oMsgPlainText.setDisposition("inline");
      oMsgPlainText.setText(sTextBody, sCharEnc, "plain");
      //oMsgPlainText.setContent(sTextBody, "text/plain; charset="+sCharEnc);
      oSentMsgParts.addBodyPart(oMsgPlainText);
    }
  }
  // fi (sContentType=="html")

  // ************************************************************************
  // Add attachments to message to be sent

  if (aAttachmentsPath!=null) {
    final int nAttachments = aAttachmentsPath.length;

    FileSystem oFS = new FileSystem();
    for (int p=0; p<nAttachments; p++) {
      String sFilePath = aAttachmentsPath[p];
      if (sBasePath!=null) {
        if (!sFilePath.startsWith(sBasePath))
          sFilePath = sBasePath + sFilePath;
      }
      File oFile = new File(sFilePath);
      
      MimeBodyPart oAttachment = new MimeBodyPart();
      oAttachment.setDisposition("attachment");
      oAttachment.setFileName(oFile.getName());
      oAttachment.setHeader("Content-Transfer-Encoding", "base64");

      ByteArrayDataSource oDataSrc;
      try {
        oDataSrc = new ByteArrayDataSource(oFS.readfilebin(sFilePath), "application/octet-stream");
      } catch (com.enterprisedt.net.ftp.FTPException ftpe) {
    	throw new IOException(ftpe.getMessage());
      }
      oAttachment.setDataHandler(new DataHandler(oDataSrc));
      oSentMsgParts.addBodyPart(oAttachment);
    } // next
    oSentMessage.setContent(oSentMsgParts);
  } // fi (iDraftParts>0)

  if (null!=sSubject) oSentMessage.setSubject(sSubject);

  if (sId!=null)
    if (sId.trim().length()>0)
      oSentMessage.setContentID(sId);

  return oSentMessage;
  } // composeMessage
     
  // ---------------------------------------------------------------------------

  /**
   * <p>Send e-mail message</p>
   * @param sSubject String e-mail Subject
   * @param sFromPersonal String Sender display name
   * @param sFromAddr String Sender e-mail address
   * @param sReplyAddr String Reply-To e-mail address
   * @param aRecipients Array of recipients e-mail addresses
   * @param aRecType Array of types for each recipient {to, cc, bcc}
   * @param sTextBody String Plain Text Message Body
   * @param sHtmlBody String HTML Text Message Body
   * @param sEncoding Character Encoding to be used
   * @param sId String Message Unique Id. Optional, may be null.
   * @param aAttachmentsPath Array of relative paths to files to be attached
   * @param sUserDir Base path for attached files
   * @param oOut PrintStream Output stream for messages verbose
   * @throws NullPointerException
   * @throws IOException
   * @throws MessagingException
   * @throws IllegalArgumentException
   * @throws SecurityException
   */
  public int sendMessage(String sSubject, String sFromPersonal, String sFromAddr, String sReplyAddr,
		                 String[] aRecipients, RecipientType[] aRecType,
		                 String sTextBody, String sHtmlBody, String sEncoding, String sId,
                         String [] aAttachmentsPath, String sUserDir, PrintStream oOut )
    throws NullPointerException,IOException,MessagingException,IllegalArgumentException,SecurityException {

	if (sFromAddr==null) throw new NullPointerException("SessionHandler.sendMessage sender address cannot be null");
	if (aRecipients==null) throw new NullPointerException("SessionHandler.sendMessage repients list cannot be null");

    boolean bHasReplacements = false;
    if (null!=sTextBody) bHasReplacements |= (Gadgets.indexOfIgnoreCase(sTextBody, "{#Message.id}")>=0);
    if (null!=sHtmlBody) bHasReplacements |= (Gadgets.indexOfIgnoreCase(sHtmlBody, "{#Message.id}")>=0);

    final int nRecipients = aRecipients.length;
    
    int nSend = 0;
    if (bHasReplacements) {
    	HashMap oMap = new HashMap(13);
    	oMap.put("Message.id", sId);
    	StringBuffer oTextBody = new StringBuffer(sTextBody);
    	StringBuffer oHtmlBody = new StringBuffer(sHtmlBody);
    	FastStreamReplacer oRpl = new FastStreamReplacer();
        for (int r=0; r<nRecipients; r++) {
          String sRecipientAddr = Gadgets.removeChars(aRecipients[r], " \t\r\n");
          if (sRecipientAddr.length()>0) {
            try {
              String sUniqueId = sId+"."+String.valueOf(r+1);
              oMap.remove("Message.id");
          	  oMap.put("Message.id", sUniqueId);          	  
              SMTPMessage oCurrentMsg = composeMessage(sSubject, sEncoding, oRpl.replace(oTextBody, oMap), oRpl.replace(oHtmlBody, oMap), sUniqueId, aAttachmentsPath, sUserDir);
              oCurrentMsg.setFrom(new InternetAddress(sFromAddr, null==sFromPersonal ? sFromAddr : sFromPersonal));
              if (null!=sReplyAddr) oCurrentMsg.setReplyTo(new Address[]{new InternetAddress(sReplyAddr)});
              oCurrentMsg.setRecipient(aRecType[r], new InternetAddress(sRecipientAddr));      
              sendMessage(oCurrentMsg);
              oOut.println("OK "+sRecipientAddr);
              nSend++;
            } catch (Exception xcpt) {
          	  if (oOut==null) {
          	  } else {
          	    oOut.println("ERROR at SessionHandler.sendMessage() "+aRecipients[r]+" "+xcpt.getClass().getName()+" "+xcpt.getMessage());
              } // fi (oOut)
          	}
          } // fi (sRecipientAddr!="")
        } // next    	
    } else {
        for (int r=0; r<nRecipients; r++) {
          String sRecipientAddr = Gadgets.removeChars(aRecipients[r], " \t\r\n");
          if (sRecipientAddr.length()>0) {
            SMTPMessage oMasterMsg = composeMessage(sSubject, sEncoding, sTextBody, sHtmlBody, null, aAttachmentsPath, sUserDir);
            oMasterMsg.setFrom(new InternetAddress(sFromAddr, null==sFromPersonal ? sFromAddr : sFromPersonal));
            if (null!=sReplyAddr) oMasterMsg.setReplyTo(new Address[]{new InternetAddress(sReplyAddr)});
            try {
              SMTPMessage oCurrentMsg = new SMTPMessage (oMasterMsg);
              oCurrentMsg.setContentID(sId+"."+String.valueOf(r+1));
              oCurrentMsg.setRecipient(aRecType[r], new InternetAddress(sRecipientAddr));      
              sendMessage(oCurrentMsg);
              oOut.println("OK "+sRecipientAddr);
              nSend++;
            }
            catch (Exception xcpt) {
              String sCause = "";
              if (xcpt.getCause()!=null)
                sCause = " cause "+xcpt.getCause().getClass().getName()+" "+xcpt.getCause().getMessage();
          	  if (oOut==null) {
          	  } else {
          	    oOut.println("ERROR "+aRecipients[r]+" "+xcpt.getClass().getName()+" "+xcpt.getMessage()+sCause);
          	  }
            }
          } // fi (sRecipientAddr!="")
        } // next    	
    } // fi

    if (nSend==nRecipients) {
    	oOut.println("Process successfully completed. "+String.valueOf(nSend)+" messages sent");
    } else {
    	oOut.println("Process finished with errors. "+String.valueOf(nSend)+" messages successfully sent, "+String.valueOf(nRecipients-nSend)+" messages failed");
    }
    return nSend;
  } // sendMessage

  // -------------------------------------------------------------------

  public int sendMessage(String sSubject, String sFromPersonal, String sFromAddr, String sReplyAddr,
		                 String[] aRecipients, RecipientType oRecType,
		                 String sTextBody, String sHtmlBody, String sEncoding, String sId,
                         String [] aAttachmentsPath, String sUserDir, PrintStream oOut )
    throws NullPointerException,IOException,MessagingException,IllegalArgumentException,SecurityException {
	if (oRecType==null) oRecType = RecipientType.TO;
	RecipientType[] aRecTypes = new RecipientType[aRecipients.length];
	Arrays.fill(aRecTypes,oRecType);
    return sendMessage(sSubject, sFromPersonal, sFromAddr, sReplyAddr,
		               aRecipients, aRecTypes,
		               sTextBody, sHtmlBody, sEncoding, sId,
                       aAttachmentsPath, sUserDir, oOut);
  }

} // SessionHandler
