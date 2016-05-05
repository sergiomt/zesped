package com.zesped.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.AttributeMultiValue;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.admin.AdminGroupRights;
import es.ipsa.atril.sec.admin.AdminRightsFactory;
import es.ipsa.atril.sec.admin.AdministeredGroup;
import es.ipsa.atril.sec.admin.AdministeredUser;
import es.ipsa.atril.sec.admin.AuthorizationAdminManager;
import es.ipsa.atril.sec.admin.exceptions.AuthorizationManagerAdminException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.AuthorizationException;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;
import es.ipsa.atril.sec.user.AuthorizationManager;
import es.ipsa.atril.sec.user.Group;

public class CustomerAccount extends BaseCompanyObject {
	
	private static final long serialVersionUID = 1L;

	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("account_id",DataType.STRING,true,true),
    	new Attr("creation_date",DataType.DATE_TIME,false,true),
    	new Attr("active",DataType.STRING,false,true,null),
    	new Attr("customer_type",DataType.STRING,false,false),
    	new Attr("business_name",DataType.STRING,true,true),
    	new Attr("tax_id",DataType.STRING,true,false),
    	new Attr("address1",DataType.STRING,false,false),
    	new Attr("address2",DataType.STRING,false,false),
    	new Attr("city",DataType.STRING,false,false),
    	new Attr("zipcode",DataType.STRING,false,false),
    	new Attr("state",DataType.STRING,false,false),
    	new Attr("country",DataType.STRING,false,false),
    	new Attr("contact_person",DataType.STRING,false,false),
    	new Attr("telephone",DataType.STRING,false,false),
    	new Attr("email",DataType.STRING,false,false),
    	new Attr("comments",DataType.STRING,false,false),
    	// new Attr("credits_used",DataType.NUMBER,false,false),
    	// new Attr("credits_left",DataType.NUMBER,false,false),
    	// SICUBO 201212 {
    	new Attr("name_billing",DataType.STRING,false,false),
    	new Attr("cif_billing",DataType.STRING,false,false),
    	new Attr("phone_billing",DataType.STRING,false,false),
    	new Attr("mail_billing",DataType.STRING,false,false),
    	new Attr("address_billing",DataType.STRING,false,false),
    	new Attr("city_billing",DataType.STRING,false,false),
    	new Attr("state_billing",DataType.STRING,false,false),
    	new Attr("country_billing",DataType.STRING,false,false),
    	new Attr("postcode_billing",DataType.STRING,false,false)
        // SICUBO 201212 }    	
    };

	private CustomerAccountCredits oAccCredits;
	
	public CustomerAccount() {
		super("CustomerAccount");
		oAccCredits = new CustomerAccountCredits();
	}

	public CustomerAccount(Document oDoc) {
		super(oDoc);		
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("CustomerAccountCredits"))  {
				oAccCredits = new CustomerAccountCredits(d);
				break;
			}
		}
	}

	public CustomerAccount(Dms oDms, String sDocId) throws ElementNotFoundException {
		super(oDms, sDocId);
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("CustomerAccountCredits"))  {
				oAccCredits = new CustomerAccountCredits(oDms, d.id());
				break;
			}
		}
	}

	public CustomerAccount(AtrilSession oSes, User oUsr) throws NullPointerException,ElementNotFoundException {
		super("CustomerAccount");
		if (null==oUsr) throw new NullPointerException("User cannot be null");
		Dms oDms = oSes.getDms();		
		List<Document> oLst =  oDms.query("CustomerAccount$account_id='" + oUsr.getCustomerAccountId() + "'");
		if (oLst.isEmpty())
			throw new ElementNotFoundException("CustomerAccount for user not found");
		else
			load(oSes, oLst.get(0).id());
	}

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	private void loadCreditCounters(AtrilSession oSes) {
		String sId = null;
		for (Document d : getDocument().children()) {
			if (d.type().name().equals("CustomerAccountCredits"))  {
				sId = d.id();
				break;
			}
		}
		if (null==sId) {
			Dms oDms = oSes.getDms();
			Document oCredits = oDms.newDocument(oDms.getDocumentType("CustomerAccountCredits"), getDocument());
			oCredits.attribute("account_id").set(getString("account_id"));
			oCredits.attribute("credits_used").set(BigDecimal.ZERO);
			oCredits.attribute("credits_left").set(BigDecimal.ZERO);
			oCredits.save("");
			oAccCredits = new CustomerAccountCredits(oCredits);
			oSes.commit();
		} else {
			oAccCredits = new CustomerAccountCredits(oSes.getDms(), sId);			
		}		
	}

	@Override
	public void load(AtrilSession oSes, String sDocId) throws ElementNotFoundException, NotEnoughRightsException , DmsException {
		super.load(oSes, sDocId);
		loadCreditCounters(oSes);
	}
	
	public String getUuid() {
		return getString("account_id");
	}

	public void setUuid(String uuid) throws NullPointerException {
		if (null==uuid)
			throw new NullPointerException("account_id cannot be null");
		else
			put("account_id", uuid);
	}

	public BigDecimal getCreditsUsed() throws NumberFormatException {
		return oAccCredits.getCreditsUsed();
	}

	public BigDecimal getCreditsLeft() throws NumberFormatException {
		return oAccCredits.getCreditsLeft();
	}

	public void burnCredits(AtrilSession oSes, BigDecimal credits)
		throws IllegalArgumentException {
		oAccCredits.burnCredits(credits);
		oAccCredits.save(oSes);
	}

	public void restoreCredits(AtrilSession oSes, BigDecimal credits)
		throws IllegalArgumentException {
		oAccCredits.restoreCredits(credits);
		oAccCredits.save(oSes);
	}

	public TaxPayers taxpayers(AtrilSession oSes) throws ElementNotFoundException {
		TaxPayers t = new TaxPayers();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals(t.getTypeName())) {
			  t.setDocument(oSes.getDms().getDocument(d.id()));
			  break;
			}
		} // next
		if (t.getDocument()==null) throw new ElementNotFoundException(t.getTypeName()+" document not found");
		return t;
    }	
	
	public static String forBusinessName(String sBusinessName) throws ElementNotFoundException, NullPointerException, IllegalArgumentException {
		return forBusinessName(sBusinessName, "CustomerAccount");
	}

	public static String forTaxId(String sTaxId)  throws ElementNotFoundException, NullPointerException, IllegalArgumentException {
		return forTaxId(sTaxId, "CustomerAccount");
	}

	public static CustomerAccount forUuid(AtrilSession oSes, String sUuid)  throws ElementNotFoundException, NullPointerException, IllegalArgumentException {
		Dms oDms = oSes.getDms();
		SortableList<Document> oLst = oDms.query("CustomerAccount$account_id='"+sUuid+"'");
		if (oLst.isEmpty()) throw new ElementNotFoundException("Could not find any CustomerAccount with unique id "+sUuid);
		return new CustomerAccount(oDms.getDocument(oLst.get(0).id()));
	}
	
	public static List<CustomerAccount> search(AtrilSession oSes, String sNameOrId) {
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		oIdx.setMaximumNumberOfDocumentReturned(50);
		String sSanitizedNameOrId = Gadgets.removeChars(sNameOrId, "\"*?").trim();
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query("(business_name:\"" + sSanitizedNameOrId + "\" OR tax_id:\""+sNameOrId+"\") AND " + DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "CustomerAccount");
		if (oLst.isEmpty()) {
			int i1stWord = sSanitizedNameOrId.indexOf(' ');
			if (i1stWord>0) sSanitizedNameOrId = sSanitizedNameOrId.substring(0, i1stWord);
			oLst = (SortableList<Document>) oIdx.query("(business_name:" + sNameOrId + "* AND " + DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "CustomerAccount", "business_name");
			if (oLst.isEmpty()) {
				oLst = (SortableList<Document>) oIdx.query("(business_name:" + sNameOrId + "~ AND " + DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "CustomerAccount", "business_name");
			}
		}
		ArrayList aLst = new ArrayList();
		for (Document d : oLst) {
			CustomerAccount oAcc = new CustomerAccount();
			oAcc.setDocument(d);
			aLst.add(oAcc);
		}
		return aLst;
	}

	public Clients clients(AtrilSession oSes) {
		Clients c = new Clients();
		for (Document d : getDocument().children()) {
			if (d.type().name().equals(c.getTypeName())) {
				c.setDocument(oSes.getDms().getDocument(d.id()));
				return c;
			}  
		} // next
		return null;
	}

	public Orders orders(AtrilSession oSes) throws IllegalStateException {
		if (getDocument()==null) throw new IllegalStateException("CustomerAccount document must be set before calling orders() method");
		Orders o = new Orders();
		for (Document d : getDocument().children()) {
		  if (d.type().name().equals(o.getTypeName())) {
			o.setDocument(oSes.getDms().getDocument(d.id()));
			break;
		  }
		} // next
		if (o.getDocument()==null) throw new ElementNotFoundException(o.getTypeName()+" document not found");
		return o;      
	}

	public Order createOrder(AtrilSession oSes) {
		Order o = new Order(oSes, orders(oSes));		
		o.put("customer_acount", getString("account_id"));
		return o;
	}

    public Group getAdminsGroup(AuthorizationManager oAum) throws ElementNotFoundException {
    	return oAum.getGroup(getString("business_name")+" administrators");
    }

    public Group getUsersGroup(AuthorizationManager oAum) throws ElementNotFoundException {
    	return oAum.getGroup(getString("business_name")+" standard users");
    }

    public Group getGuestsGroup(AuthorizationManager oAum) throws ElementNotFoundException {
    	return oAum.getGroup(getString("business_name")+" guests");
    }

    public ArrayList<User> getAdmins(AtrilSession oSes) throws ElementNotFoundException {
    	Log.out.debug("CustomerAccount.getAdmins()");
    	AuthorizationManager oAum = oSes.getAuthorizationManager();
    	UsersGroup oUgrp = new UsersGroup(oAum, getAdminsGroup(oAum));
    	return oUgrp.getMembers(oSes);
    }

    public ArrayList<User> getUsers(AtrilSession oSes) throws ElementNotFoundException {
    	Log.out.debug("CustomerAccount.getUsers()");
    	AuthorizationManager oAum = oSes.getAuthorizationManager();
    	UsersGroup oUgrp = new UsersGroup(oAum, getUsersGroup(oAum));
    	return oUgrp.getMembers(oSes);
    }

    public ArrayList<User> getGuests(AtrilSession oSes) throws ElementNotFoundException {
    	Log.out.debug("CustomerAccount.getGuests()");
    	AuthorizationManager oAum = oSes.getAuthorizationManager();
    	UsersGroup oUgrp = new UsersGroup(oAum, getGuestsGroup(oAum));
    	return oUgrp.getMembers(oSes);
    }
    
	public void grantGuest(AtrilSession oSes, User oUsr)
		throws AuthorizationManagerAdminException, ElementNotFoundException, NotEnoughRightsException {
		Log.out.debug("Begin CustomerAccount.grantUser("+oUsr.getEmail()+")");
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		AdminGroupRights oRgh = AdminRightsFactory.getGroupRightsAllDeny();
		oRgh.setReadGroup(true);
		oRgh.setReadUsers(true);
		oRgh.setLinkResources(true);
		oRgh.setUnlinkResources(true);
		String sBusinessName = getString("business_name");
		AdministeredUser oAsr = oUsr.getAdministeredUser();
		AdministeredGroup oGts = oAam.getGroup(sBusinessName+" guests");
		if (!oGts.getMembers().contains(oAsr)) {
			oGts.addMember(oAsr);		
			oAam.setRights(oAsr, oGts, oRgh);			
		}
		AdministeredGroup oStd = oAam.getGroup(sBusinessName+" standard users");
		if (oStd.getMembers().contains(oAsr))
			oStd.removeMember(oAsr);
		AdministeredGroup oAdm = oAam.getGroup(sBusinessName+" administrators");
		if (oAdm.getMembers().contains(oAsr))
			oAdm.removeMember(oAsr);
		AdministeredGroup oZus = oAam.getGroup("Zesped Users");
		if (!oZus.getMembers().contains(oUsr.getAdministeredUser())) {
			oZus.addMember(oUsr.getAdministeredUser());
			oAam.setRights(oAsr, oZus, oRgh);
		}
		Log.out.debug("End CustomerAccount.grantGuest("+oUsr.getEmail()+")");
	}
    
	public void grantUser(AtrilSession oSes, User oUsr)
		throws AuthorizationManagerAdminException, ElementNotFoundException, NotEnoughRightsException {
		Log.out.debug("Begin CustomerAccount.grantUser("+oUsr.getEmail()+")");
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		AdminGroupRights oRgh = AdminRightsFactory.getGroupRightsAllDeny();
		oRgh.setReadGroup(true);
		oRgh.setReadUsers(true);
		oRgh.setLinkResources(true);
		oRgh.setUnlinkResources(true);
		String sBusinessName = getString("business_name");
		AdministeredUser oAsr = oUsr.getAdministeredUser();
		AdministeredGroup oStd = oAam.getGroup(sBusinessName+" standard users");
		if (!oStd.getMembers().contains(oAsr)) {
			oStd.addMember(oAsr);
			oAam.setRights(oAsr, oStd, oRgh);			
		}
		AdministeredGroup oGts = oAam.getGroup(sBusinessName+" guests");
		if (oGts.getMembers().contains(oAsr))
			oGts.removeMember(oAsr);
		AdministeredGroup oAdm = oAam.getGroup(sBusinessName+" administrators");
		if (oAdm.getMembers().contains(oAsr))
			oAdm.removeMember(oAsr);
		AdministeredGroup oZus = oAam.getGroup("Zesped Users");
		if (!oZus.getMembers().contains(oUsr.getAdministeredUser())) {
			oZus.addMember(oUsr.getAdministeredUser());
			oAam.setRights(oAsr, oZus, oRgh);		
		}
		Log.out.debug("End CustomerAccount.grantUser("+oUsr.getEmail()+")");
	}

	public void grantAdmin(AtrilSession oSes, User oUsr)
		throws AuthorizationManagerAdminException, ElementNotFoundException, NotEnoughRightsException {
		Log.out.debug("Begin CustomerAccount.grantAdmin("+oUsr.getEmail()+")");
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		AdminGroupRights oAll = AdminRightsFactory.getGroupRightsAllGrant();
		String sBusinessName = getString("business_name");
		AdministeredUser oAsr = oUsr.getAdministeredUser();
		AdministeredGroup oAdm = oAam.getGroup(sBusinessName+" administrators");
		if (!oAdm.getMembers().contains(oAsr)) {
			oAdm.addMember(oAsr);
			oAam.setRights(oAsr, oAdm, oAll);
		}
		AdministeredGroup oStd = oAam.getGroup(sBusinessName+" standard users");
		if (oStd.getMembers().contains(oAsr))
			oStd.removeMember(oAsr);
		oAam.setRights(oAsr, oStd, oAll);
		AdministeredGroup oGts = oAam.getGroup(sBusinessName+" guests");
		if (oGts.getMembers().contains(oAsr))
			oGts.removeMember(oAsr);
		oAam.setRights(oAsr, oGts, oAll);
		AdministeredGroup oZus = oAam.getGroup("Zesped Users");
		if (!oZus.getMembers().contains(oUsr.getAdministeredUser())) {
			oZus.addMember(oUsr.getAdministeredUser());
			oAam.setRights(oAsr, oZus, oAll);			
		}
		Log.out.debug("End CustomerAccount.grantAdmin("+oUsr.getEmail()+")");
	}

	public void denyGuest(AtrilSession oSes, User oUsr)
		throws AuthorizationManagerAdminException, ElementNotFoundException, NotEnoughRightsException {
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		String sBusinessName = getString("business_name");
		AdministeredUser oAsr = oUsr.getAdministeredUser();
		AdministeredGroup oGts = oAam.getGroup(sBusinessName+" guests");
		if (oGts.getMembers().contains(oAsr))
			oGts.removeMember(oAsr);
	}

	public void denyUser(AtrilSession oSes, User oUsr)
		throws AuthorizationManagerAdminException, ElementNotFoundException, NotEnoughRightsException {
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		String sBusinessName = getString("business_name");
		AdministeredUser oAsr = oUsr.getAdministeredUser();
		AdministeredGroup oStd = oAam.getGroup(sBusinessName+" users");
		if (oStd.getMembers().contains(oAsr))
			oStd.removeMember(oAsr);
	}

	public void denyAdmin(AtrilSession oSes, User oUsr)
		throws AuthorizationManagerAdminException, ElementNotFoundException, NotEnoughRightsException {
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		String sBusinessName = getString("business_name");
		AdministeredUser oAsr = oUsr.getAdministeredUser();
		AdministeredGroup oAdm = oAam.getGroup(sBusinessName+" admins");
		if (oAdm.getMembers().contains(oAsr))
			oAdm.removeMember(oAsr);
	}
	
	public void grant(AtrilSession oSes, Document... aDocs)
		throws AuthorizationException,ElementNotFoundException {
		AuthorizationManager oAum = oSes.getAuthorizationManager();
		Group oGuestsGrp = getGuestsGroup(oAum);
		Group oUsersGrp = getUsersGroup(oAum);
		Group oOpersGrp = Zesped.getOperatorsGroup(oAum);
		Group oAdminsGrp = getAdminsGroup(oAum);
		UsersGroup oGgrp = new UsersGroup(oAum, oGuestsGrp);
		UsersGroup oUgrp = new UsersGroup(oAum, oUsersGrp);
		UsersGroup oOgrp = new UsersGroup(oAum, oOpersGrp);
		UsersGroup oAgrp = new UsersGroup(oAum, oAdminsGrp);		
		for (Document oDoc : aDocs) {
			Log.out.debug("Granting group permissions for document "+oDoc.id());
			oGgrp.grantReadOnly(oDoc);
			oUgrp.grantAll(oDoc);
			oOgrp.grantAll(oDoc);
			oAgrp.grantAll(oDoc);
		}
	}

	public void grant(AtrilSession oSes, Collection<? extends BaseModelObject> aObjs)
		throws AuthorizationException,ElementNotFoundException {
		AuthorizationManager oAum = oSes.getAuthorizationManager();		
		Group oGuestsGrp = getGuestsGroup(oAum);
		Group oUsersGrp = getUsersGroup(oAum);
		Group oOpersGrp = Zesped.getOperatorsGroup(oAum);
		Group oAdminsGrp = getAdminsGroup(oAum);
		UsersGroup oGgrp = new UsersGroup(oAum, oGuestsGrp);
		UsersGroup oUgrp = new UsersGroup(oAum, oUsersGrp);
		UsersGroup oOgrp = new UsersGroup(oAum, oOpersGrp);
		UsersGroup oAgrp = new UsersGroup(oAum, oAdminsGrp);		
		for (BaseModelObject oObj : aObjs) {
			Document oDoc = oObj.getDocument();
			oGgrp.grantReadOnly(oDoc);
			oUgrp.grantAll(oDoc);
			oOgrp.grantAll(oDoc);
			oAgrp.grantAll(oDoc);
		}
	}
	
	public void grant(BaseModelObject... aObjs)
		throws AuthorizationException,ElementNotFoundException {
		AtrilSession oSes = DAO.getAdminSession("CustomerAccount.grant");
		AuthorizationManager oAum = oSes.getAuthorizationManager();		
		Group oGuestsGrp = getGuestsGroup(oAum);
		Group oUsersGrp = getUsersGroup(oAum);
		Group oOpersGrp = Zesped.getOperatorsGroup(oAum);
		Group oAdminsGrp = getAdminsGroup(oAum);
		UsersGroup oGgrp = new UsersGroup(oAum, oGuestsGrp);
		UsersGroup oUgrp = new UsersGroup(oAum, oUsersGrp);
		UsersGroup oOgrp = new UsersGroup(oAum, oOpersGrp);
		UsersGroup oAgrp = new UsersGroup(oAum, oAdminsGrp);		
		for (BaseModelObject oObj : aObjs) {
			Document oDoc = oObj.getDocument();
			oGgrp.grantReadOnly(oDoc);
			oUgrp.grantAll(oDoc);
			oOgrp.grantAll(oDoc);
			oAgrp.grantAll(oDoc);
		}
		oSes.commit();
		if (oSes.isConnected()) oSes.disconnect();
		if (oSes.isOpen()) oSes.close();
	}

	public void createGroups(AtrilSession oSes)  throws NullPointerException, IllegalArgumentException {
		Log.out.debug("Begin CustomerAccount.createGroups()");

		long lStart, lEnd;

		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		AdministeredGroup oGrp = oAam.getGroup("Zesped Users");
		
		AdministeredGroup oCom=null, oStd=null, oAdm=null, oGts=null;
		String sBusinessName = getString("business_name");

		try {

		  oCom = oAam.getGroup(sBusinessName);
		  oGts = oAam.getGroup(sBusinessName+" guests");
		  oStd = oAam.getGroup(sBusinessName+" standard users");
		  oAdm = oAam.getGroup(sBusinessName+" administrators");

		} catch (ElementNotFoundException enf) {

		  // AdminGroupRights oAll = AdminRightsFactory.getGroupRightsAllGrant();
		  // lStart = new Date().getTime();
		  // AdministeredUser oAsr = oAam.getUser("admin");
		  // Log.out.debug("PROFILING: AuthorizationAdminManager.getUser('admin') "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");

		  if (null==oCom) {
			  Log.out.debug("AuthorizationAdminManager.createGroup("+sBusinessName+")");
			  oCom = oAam.createGroup(sBusinessName, "", false, null, oGrp);
			  lStart = new Date().getTime();
			  // oCom.addMember(oAsr);
			  // Log.out.debug("PROFILING: AdministeredGroup.addMember(AdministeredUser) "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
			  // lStart = lEnd;
			  // oAam.setRights(oAsr, oCom, oAll);
			  // Log.out.debug("PROFILING: AuthorizationAdminManager.setRights(...) "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		  }
		  if (null==oGts) {
			  Log.out.debug("AuthorizationAdminManager.createGroup("+sBusinessName+" guests)");
			  oGts = oAam.createGroup(sBusinessName+" guests", sBusinessName+" guests", false, null, oCom);
			  // oGts.addMember(oAsr);
			  // oAam.setRights(oAsr, oGts, oAll);
		  }
		  if (null==oStd) {
			  Log.out.debug("AuthorizationAdminManager.createGroup("+sBusinessName+" standard users)");
			  oStd = oAam.createGroup(sBusinessName+" standard users", sBusinessName+" standard users", false, null, oCom);
			  // oStd.addMember(oAsr);
			  // oAam.setRights(oAsr, oStd, oAll);
		  }
		  if (null==oAdm) {
			  Log.out.debug("AuthorizationAdminManager.createGroup("+sBusinessName+" administrators)");
			  oAdm = oAam.createGroup(sBusinessName+" administrators", sBusinessName+" administrators", false, null, oCom);			
			  // oAdm.addMember(oAsr);
			  // oAam.setRights(oAsr, oAdm, oAll);
		  }
		}	
		Log.out.debug("End CustomerAccount.createGroups()");
	}
		
	public static CustomerAccount create(AtrilSession oSes, User oUsr, String sBusinessName)  throws NullPointerException, IllegalArgumentException {

		Log.out.debug("Begin CustomerAccount.create(AtrilSession, "+oUsr.getEmail()+", "+sBusinessName+")");
		
		if (sBusinessName==null) throw new NullPointerException("Business Name cannot be null");
		if (sBusinessName.length()==0) throw new NullPointerException("Business Name cannot be empty");

		oUsr = new User(oSes, oUsr.id());

		CustomerAccount oNew = new CustomerAccount();

		Date dtNow = new Date();
		
		DocumentIndexer oIdx = oSes.getDocumentIndexer();

		long lEnd, lStart = new Date().getTime();
		
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query("business_name:\"" + sBusinessName + "\" AND " + DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + "CustomerAccount");
		if (!oLst.isEmpty()) throw new IllegalArgumentException(sBusinessName+" already exists");

		Log.out.debug("PROFILING: Search Business Name "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		Dms oDms = oSes.getDms();
		oNew.setDocument (oDms.newDocument(oDms.getDocumentType(oNew.getTypeName()), Zesped.top(oSes).getDocument()));
		oNew.put("account_id",Gadgets.generateUUID());
		oNew.put("active","1");
		oNew.put("business_name",sBusinessName);
		oNew.put("creation_date", dtNow);
		oNew.put("contact_person", oUsr.getFirstName()+" "+oUsr.getLastName());
		oNew.save(oSes);

		Log.out.debug("PROFILING: Save CustomerAccount "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		Log.out.debug("CustomerAccount.createGroups(AtrilSession)");

		oNew.createGroups(oSes);

		Log.out.debug("PROFILING: Create permissions groups "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		oNew.grantAdmin(oSes, oUsr);

		Log.out.debug("PROFILING: Grant admin permissions "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		AttributeMultiValue oAttr = oUsr.getDocument().attribute("customer_acount");
		oAttr.set(oNew.getUuid());
		oUsr.getDocument().save("");
		oIdx.indexDocument(oUsr.getDocument());

		Document oOrders = oDms.newDocument(oDms.getDocumentType("Orders"), oNew.getDocument());
		oOrders.save("");
		oIdx.indexDocument(oOrders);
		
		Document oClients = oDms.newDocument(oDms.getDocumentType("Clients"), oNew.getDocument());
		oClients.save("");
		oIdx.indexDocument(oClients);

		Document oPayers = oDms.newDocument(oDms.getDocumentType("TaxPayers"), oNew.getDocument());
		oPayers.save("");
		oIdx.indexDocument(oPayers);

		Document oCredits = oDms.newDocument(oDms.getDocumentType("CustomerAccountCredits"), oNew.getDocument());
		oCredits.attribute("account_id").set(oNew.getString("account_id"));
		oCredits.attribute("credits_used").set(0l);
		oCredits.attribute("credits_left").set(0l);
		oCredits.save("");
		oIdx.indexDocument(oCredits);
		
		Log.out.debug("PROFILING: Create CustomerAccount child documents "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		oNew.grant(oSes, oNew.getDocument(), oOrders, oClients, oPayers);

		Log.out.debug("PROFILING: Grant access to children "+String.valueOf((lEnd=new Date().getTime())-lStart)+" ms");
		lStart = lEnd;
		
		DAO.log(oSes, oNew.getDocument(), CustomerAccount.class, "CREATE CUSTOMERACCOUNT", AtrilEvent.Level.INFO, oNew.getDocument().id()+";"+sBusinessName);

		Log.out.debug("End CustomerAccount.create() : "+oNew.getDocument().id());
		
		return oNew;
	}
}
