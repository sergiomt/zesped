package com.zesped.test;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;

import com.zesped.DAO;
import com.zesped.idl.ModelManager;
import com.zesped.model.BillNote;
import com.zesped.model.CaptureServiceFlavor;
import com.zesped.model.CustomerAccount;
import com.zesped.model.Invoice;
import com.zesped.model.TaxPayer;
import com.zesped.model.Ticket;
import com.zesped.model.User;

import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.admin.exceptions.AuthorizationManagerAdminException;
import es.ipsa.atril.sec.admin.exceptions.DuplicatedElementException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class SmokeTests {

	private static DAO oDao;
	
	public static final String TEST_USER_EMAIL = "defaultuser@junittesting.com";
	public static final String TEST_BUSINESSNAME = "Default Test Company Inc.";
	public static String USRID, ACCID;

	//-----------------------------------------------------------
	
	@BeforeClass public static void init() throws Exception {
		ModelManager oMan = new ModelManager();		
		oDao = new DAO();
		oDao.init(oMan.getConnectionProperties());
		AtrilSession oSes = DAO.getAdminSession("SmokeTest");
		oSes.autoCommit(true);		
		try {
		  USRID = User.forEmail(TEST_USER_EMAIL);
		} catch (ElementNotFoundException notfound) {
		  User oUsr = new User();
		  oUsr.setEmail(TEST_USER_EMAIL);
		  oUsr.setFirstName("Default Test User First Name");
		  oUsr.setLastName("Default Test User Last Name");
		  oUsr.setPassword("12345678");
		  oUsr.create(oSes);
		  USRID = oUsr.id();
		}
		try {
		  ACCID = CustomerAccount.forBusinessName(TEST_BUSINESSNAME);
		} catch (ElementNotFoundException notfound) {		
		  ACCID = CustomerAccount.create(oSes, new User(oSes, USRID), TEST_BUSINESSNAME).id();		
		}
		
		CustomerAccount oAcc = new CustomerAccount(oSes.getDms(), ACCID);
		if (oAcc.taxpayers(oSes).list(oSes).size()==0)
			TaxPayer.create(oSes, oAcc, TEST_BUSINESSNAME+" tax payer", "John Smith", "johnj@junittesting.com", null);

		oSes.disconnect();
		oSes.close();
    }

	//-----------------------------------------------------------
	
	@AfterClass public static void destroy() {
		oDao.destroy();
	}

	//-----------------------------------------------------------
	
	protected AtrilSession openTestSession() {
		AtrilSession oSes = DAO.getAdminSession("SmokeTest");
		oSes.autoCommit(true);
		return oSes;
	}

	//-----------------------------------------------------------

	protected void closeTestSession(AtrilSession oSes) {
		oSes.disconnect();
		oSes.close();
	}

	//-----------------------------------------------------------
	
	@Test public void createUser()
		throws IllegalArgumentException,AuthorizationManagerAdminException,DuplicatedElementException,ElementNotFoundException,NotEnoughRightsException {
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User();
	    oUsr.setEmail(generateRandomId(6, null,Character.LOWERCASE_LETTER)+"@"+generateRandomId(6, null,Character.LOWERCASE_LETTER)+".com");
	    oUsr.setFirstName("Test First Name "+generateRandomId(6, null,Character.UPPERCASE_LETTER));
	    oUsr.setLastName("Test Last Name "+generateRandomId(6, null,Character.UPPERCASE_LETTER));
	    oUsr.setPassword("12345678");
	    oUsr.create(oSes);
	    closeTestSession(oSes);
	}

	//-----------------------------------------------------------
	
	@Test public void createCustomerAccount()
		throws IllegalArgumentException,AuthorizationManagerAdminException,DuplicatedElementException,ElementNotFoundException,NotEnoughRightsException {
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User(oSes, USRID);
	    CustomerAccount.create(oSes, oUsr, "Test Company "+generateRandomId(6, null,Character.UPPERCASE_LETTER));
	    closeTestSession(oSes);
	}

	//-----------------------------------------------------------
	
	@Test public void createUserCustomerAccountAndTaxPayer()
		throws Exception {
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User();
	    oUsr.setEmail(generateRandomId(6, null,Character.LOWERCASE_LETTER)+"@"+generateRandomId(6, null,Character.LOWERCASE_LETTER)+".com");
	    oUsr.setFirstName("Test First Name "+generateRandomId(6, null,Character.UPPERCASE_LETTER));
	    oUsr.setLastName("Test Last Name "+generateRandomId(6, null,Character.UPPERCASE_LETTER));
	    oUsr.setPassword("12345678");
	    oUsr.create(oSes);
	    oSes.commit();
	    String sBizName = "Test Company "+generateRandomId(6, null,Character.UPPERCASE_LETTER);
	    CustomerAccount oCac = CustomerAccount.create(oSes, oUsr, sBizName);
		oSes.commit();
		TaxPayer.create(oSes, oCac, sBizName, "", oUsr.getFirstName()+" "+oUsr.getLastName(), oUsr.getEmail(), true, null);
		oSes.commit();
	    closeTestSession(oSes);
	}
	
	//-----------------------------------------------------------
	
	@Test public void createInvoice()
		throws IllegalArgumentException,AuthorizationManagerAdminException,DuplicatedElementException,ElementNotFoundException,NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User(oSes, USRID);
	    CustomerAccount oAcc = new CustomerAccount(oSes.getDms(), ACCID);
	    Iterator<TaxPayer> oItr = oAcc.taxpayers(oSes).list(oSes).iterator();
		TaxPayer oTpr = oItr.next();
		oTpr.invoices(oSes).create(oSes, oUsr.getNickName(), CaptureServiceFlavor.BASIC, oTpr.id(), null, null);
	    closeTestSession(oSes);
	}

	//-----------------------------------------------------------
	
	@Test public void uploadInvoice()
		throws DmsException, NotYetConnectedException, ClassCastException, IllegalStateException, NullPointerException, RuntimeException, IOException, InstantiationException, IllegalAccessException {
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User(oSes, USRID);
	    CustomerAccount oAcc = new CustomerAccount(oSes.getDms(), ACCID);
	    Iterator<TaxPayer> oItr = oAcc.taxpayers(oSes).list(oSes).iterator();
		TaxPayer oTpr = oItr.next();
		Invoice oInv = oTpr.invoices(oSes).create(oSes, oUsr.getNickName(), CaptureServiceFlavor.BASIC, oTpr.id(), null, null);
		oInv.createPage(oSes, getClass().getResourceAsStream("BilleteIberia.jpg"), 1, "BilleteIberia");
		closeTestSession(oSes);
	}
	
	//-----------------------------------------------------------
	
	@Test public void processInvoice()
		throws DmsException, NotYetConnectedException, ClassCastException, IllegalStateException, NullPointerException, RuntimeException, IOException, InstantiationException, IllegalAccessException {
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User(oSes, USRID);
	    CustomerAccount oAcc = new CustomerAccount(oSes.getDms(), ACCID);
	    Iterator<TaxPayer> oItr = oAcc.taxpayers(oSes).list(oSes).iterator();
		TaxPayer oTpr = oItr.next();
		Invoice oInv = oTpr.invoices(oSes).create(oSes, oUsr.getNickName(), CaptureServiceFlavor.BASIC, oTpr.id(), null, null);
		oInv.process(oSes, new User(oSes, User.forEmail(TEST_USER_EMAIL)).getNickName());
		closeTestSession(oSes);
		Assert.assertEquals(true, oInv.isProcessed());
	}

	//-----------------------------------------------------------
	
	@Test public void createTicket()
		throws IllegalArgumentException,AuthorizationManagerAdminException,DuplicatedElementException,ElementNotFoundException,NotEnoughRightsException, DmsException, InstantiationException, IllegalAccessException {
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User(oSes, USRID);
	    CustomerAccount oAcc = new CustomerAccount(oSes.getDms(), ACCID);
	    Iterator<TaxPayer> oItr = oAcc.taxpayers(oSes).list(oSes).iterator();
		TaxPayer oTpr = oItr.next();
		String sEmployeeUuid = "";
		BillNote oBln = oTpr.billnotes(oSes).create(oSes, oUsr.getNickName(), CaptureServiceFlavor.BASIC, oTpr.id(), "Test Concept", sEmployeeUuid);
		oBln.createTicket(oSes);
	    closeTestSession(oSes);
	}

	//-----------------------------------------------------------
	
	@Test public void uploadTicket()
		throws DmsException, NotYetConnectedException, ClassCastException, IllegalStateException, NullPointerException, RuntimeException, IOException, InstantiationException, IllegalAccessException {
	    String sUsrId = User.forEmail(TEST_USER_EMAIL);
	    String sAccId = CustomerAccount.forBusinessName(TEST_BUSINESSNAME);
	    AtrilSession oSes = openTestSession();
	    User oUsr = new User(oSes, sUsrId);
	    CustomerAccount oAcc = new CustomerAccount(oSes.getDms(), sAccId);
	    Iterator<TaxPayer> oItr = oAcc.taxpayers(oSes).list(oSes).iterator();
		TaxPayer oTpr = oItr.next();
		String sEmployeeUuid = "";
		BillNote oBln = oTpr.billnotes(oSes).create(oSes, oUsr.getNickName(), CaptureServiceFlavor.BASIC, oTpr.id(), "Test Concept", sEmployeeUuid);
		Ticket oTck = oBln.createTicket(oSes);
		oTck.createNote(oSes, getClass().getResourceAsStream("OutbackSteakhouse.jpg"), 1, "OutbackSteakhouse.jpg");
		closeTestSession(oSes);
	}
	
	//-----------------------------------------------------------

	  /**
	   * Generate a random identifier of a given length
	   * @param iLength int Length of identifier to be generated /between 1 and 4096 characters)
	   * @param sCharset String Character set to be used for generating the identifier
	   * @param byCategory byte Character category, must be one of Character.UNASSIGNED, Character.UPPERCASE_LETTER or Character.LOWERCASE_LETTER
	   * If sCharset is <b>null</b> then it is "abcdefghjkmnpqrstuvwxyz23456789" by default
	   * @return Identifier of given length composed using the designated character set
	   * created using the machine IP address, current system date, a randon number
	   * and a sequence.
	   */
	public static String generateRandomId(int iLength, String sCharset, byte byCategory )
	  	throws StringIndexOutOfBoundsException {
	    
	    if (iLength<=0) 
	      throw new StringIndexOutOfBoundsException("Gadgets.generateRandomId() identifier length must be greater than zero");

	    if (iLength>4096) 
	      throw new StringIndexOutOfBoundsException("Gadgets.generateRandomId() identifier length must be less than or equal to 4096");

	    if (sCharset!=null) {
	      if (sCharset.length()==0) throw new StringIndexOutOfBoundsException("Gadgets.generateRandomId() character set length must be greater than zero");
	    } else {
	      sCharset = "abcdefghjkmnpqrstuvwxyz23456789";
	    }
	    
		if (byCategory!=Character.UNASSIGNED && byCategory!=Character.UPPERCASE_LETTER && byCategory!=Character.LOWERCASE_LETTER)
		  throw new IllegalArgumentException("Gadgets.generateRandomId() Character category must be one of {UNASSIGNED, UPPERCASE_LETTER, LOWERCASE_LETTER}");

		int iCsLen = sCharset.length();
	    StringBuffer oId = new StringBuffer(iLength);
	    Random oRnd = new Random(new Date().getTime());
	    for (int i=0; i<iLength; i++){
		  char c = sCharset.charAt(oRnd.nextInt(iCsLen));
		  if (byCategory==Character.UPPERCASE_LETTER)
		  	c = Character.toUpperCase(c);
		  else if (byCategory==Character.LOWERCASE_LETTER)
		  	c = Character.toLowerCase(c);
		  oId.append(c);
		} // next
		return oId.toString();
	} // generateRandomId

	// ------------------------------------------------------------------------
	
	public static void main(String[] args) throws Exception {
		SmokeTests oTst = new SmokeTests ();
		SmokeTests.init();
		oTst.createUserCustomerAccountAndTaxPayer();
		SmokeTests.destroy();
	}	
}
