package com.zesped.model;

import java.nio.channels.NotYetConnectedException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
	
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.knowgate.acl.ACL;
import com.knowgate.misc.Gadgets;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.AttributeMultiValue;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsDocumentModificationException;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.admin.AdministeredGroup;
import es.ipsa.atril.sec.admin.AdminGroupRights;
import es.ipsa.atril.sec.admin.AdministeredUser;
import es.ipsa.atril.sec.admin.AuthorizationAdminManager;
import es.ipsa.atril.sec.admin.exceptions.AuthorizationManagerAdminException;
import es.ipsa.atril.sec.admin.exceptions.DuplicatedElementException;
import es.ipsa.atril.sec.admin.exceptions.OldPasswordWrongOnPasswordChangeException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;
import es.ipsa.atril.sec.exceptions.RecentlyUsedPasswordException;
import es.ipsa.atril.sec.user.Group;
import es.ipsa.atril.sec.user.AuthorizationManager;
import es.ipsa.atril.sec.user.RightsFactory;

public class User extends BaseModelObject implements TypeConverter<User> {

	private static final long serialVersionUID = 1L;
	
	private String s1stName;
	private String s2ndName;
	private String sEmail;
	private String sPassword;	
	private String sNickName;
	private String sFormer1stName;
	private String sFormer2ndName;
	private String sFormerPassword;
	private AdministeredUser oUsr;
	private HashSet<CaptureService> oServs;
	private HashSet<TaxPayer> oAllwed;
	private HashSet<TaxPayer> oDenied;

	private static class CheckEmailSyntax implements CustomConstraint {
		public boolean check (AtrilSession oSes, DocumentIndexer oIdx, BaseModelObject oObj) {
			if (oObj.isNull("email"))
				return false;
			else
				return Gadgets.checkEMail(oObj.getString("email"));
		}
	}

	private static final Attr[] aAttrs = new Attr[] {
    	new Attr("user_id",DataType.STRING,false,true),
    	new Attr("user_uuid",DataType.STRING,true,true),
    	new Attr("customer_acount",DataType.STRING,false,false,new ForeignKey(CustomerAccount.class,"account_id")),
    	new Attr("email",DataType.STRING,true,true,null, new CheckEmailSyntax()),
    	new Attr("allowed_services",DataType.STRING,false,false),
    	new Attr("password",DataType.STRING,false,true),
    	new Attr("active",DataType.STRING,false,true),
    	new Attr("can_approve",DataType.STRING,false,false),
    	new Attr("can_settle",DataType.STRING,false,false),
    	new Attr("can_premium",DataType.STRING,false,false),
    	new Attr("default_capture_type",DataType.STRING,false,false),
    	new Attr("default_scanner",DataType.STRING,false,false),
    	new Attr("default_service_flavor",DataType.STRING,false,false,new ForeignKey(CaptureServiceFlavor.class,"id"))
	};

	public User() {
		super("User");
		oUsr = null;
		oServs = new HashSet<CaptureService>();
		oAllwed = new HashSet<TaxPayer>();
		oDenied = new HashSet<TaxPayer>();
	}

	public User(AtrilSession oSes, String sDocId) throws ElementNotFoundException, NotEnoughRightsException , DmsException {
		super("User");
		oServs = new HashSet<CaptureService>();
		oAllwed = new HashSet<TaxPayer>();
		oDenied = new HashSet<TaxPayer>();
		load(oSes, sDocId);
	}

	public Attr[] attributes() {
	  return aAttrs;	
	}
	
	public AdministeredUser getAdministeredUser() {
		return oUsr;
	}

	public Map<AdministeredGroup,AdminGroupRights> getGroups() {
		return oUsr.getGroupsWithRights();
	}

	public Set<TaxPayer> getAllowedTaxPayers() {
		return oAllwed;
	}

	public Set<TaxPayer> getDeniedTaxPayers() {
		return oDenied;
	}

	public boolean isAllowedAt(String sTaxPayerId) {
		Iterator<TaxPayer> oIter;
		if (oDenied.size()>0) {
			oIter = oDenied.iterator();
			while (oIter.hasNext()) {
				if (oIter.next().id().equals(sTaxPayerId))
					return false;
			}
		}
		if (oAllwed.size()>0) {
			oIter = oAllwed.iterator();
			while (oIter.hasNext()) {
				if (oIter.next().id().equals(sTaxPayerId))
					return true;
			}
			return false;
		}
		return true;
	}

	public boolean isOperator(AtrilSession oSes) {
		AuthorizationManager oAum = oSes.getAuthorizationManager();
		Group oGrp = Zesped.getOperatorsGroup(oAum);
		long lId = getAdministeredUser().getId();
		for (es.ipsa.atril.sec.user.User u : oGrp.getMembers())
			if (u.getId()==lId)
				return true;
		return false;
	}

	private void setAdministeredUser(AdministeredUser oAdusr) {
		oUsr = oAdusr;
	}

	public String getCustomerAccountId() {
		return getStringNull("customer_acount","");
	}

	public static String forEmail(String sEmailAddr) throws NullPointerException,ElementNotFoundException {
		if (sEmailAddr==null) throw new NullPointerException("Email address may not be null");
		AtrilSession oSes = DAO.getAdminSession("User.forEmail");		
		Dms oDms = oSes.getDms();
		SortableList<Document> oLst = oDms.query("User$email='"+sEmailAddr.toLowerCase().trim()+"'");
		if (oLst.isEmpty()) {
			oSes.disconnect();
			oSes.close();
			throw new ElementNotFoundException("No user found with email "+sEmailAddr.toLowerCase().trim());
		} else {
			String sUsrId = oLst.get(0).id();			
			oSes.disconnect();
			oSes.close();
			return sUsrId;			
		}
	}

	public static String forUuid(String sUuid) throws NullPointerException,ElementNotFoundException {
		if (sUuid==null) throw new NullPointerException("UUID may not be null");
		AtrilSession oSes = DAO.getAdminSession("User.forUuid");		
		Dms oDms = oSes.getDms();
		SortableList<Document> oLst = oDms.query("User$user_uuid='"+sUuid+"'");
		if (oLst.isEmpty()) {
			oSes.disconnect();
			oSes.close();
			throw new ElementNotFoundException("No user found with UUID "+sUuid);
		} else {
			String sUsrId = oLst.get(0).id();			
			oSes.disconnect();
			oSes.close();
			return sUsrId;			
		}
	}

	public void create(AtrilSession oSes)
		throws IllegalArgumentException,AuthorizationManagerAdminException,
		 	   DuplicatedElementException,ElementNotFoundException,
		 	   NotEnoughRightsException,NullPointerException {

		Log.out.debug("Begin com.zesped.model.User.create()");
		
		if (null==getPassword()) throw new NullPointerException("User.create() password cannot be null");
		if (null==getEmail()) throw new NullPointerException("User.create() email cannot be null");
		if (getEmail().length()==0) throw new NullPointerException("User.create() email cannot be empty");
		
		AuthorizationManager oAum = oSes.getAuthorizationManager();
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		AdministeredGroup oGrp = oAam.getGroup("Zesped Users");
		
		if (null==sNickName) sNickName = Gadgets.generateUUID();

		Log.out.debug("AuthorizationAdminManager.createUser("+sNickName+","+getPassword()+","+getFirstName()+","+getLastName()+","+oGrp.getName()+")");
		
		setAdministeredUser(oAam.createUser(sNickName, getPassword(), getFirstName(), getLastName(), oGrp));

		String sUsrs = Users.top(oSes).getDocument().id();
		
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		SortableList<Document> oLst = (SortableList<Document>) oSes.getDms().query("User$email='"+getEmail()+"'");
		if (!oLst.isEmpty()) {
			Log.out.error("E-mail address "+getEmail()+" already belongs to another user");
			throw new IllegalArgumentException("E-mail address "+getEmail()+" already belongs to another user");
		}
		
		Dms oDms = oSes.getDms();
		Document oDoc = oDms.newDocument(oDms.getDocumentType("User"), oDms.getDocument(sUsrs));
		oDoc.save("");
		setDocument(oDoc);
		
		AttributeMultiValue oUid = oDoc.attribute("user_id");
		oUid.set(oDoc.id());
		AttributeMultiValue oUuid = oDoc.attribute("user_uuid");
		oUuid.set(sNickName);
		AttributeMultiValue oAttr = oDoc.attribute("email");
		oAttr.set(getEmail());
		AttributeMultiValue oActv = oDoc.attribute("active");
		oActv.set(getStringNull("active","1"));
		AttributeMultiValue oSrvs = oDoc.attribute("allowed_services");
		oSrvs.set(String.valueOf(CaptureService.INVOICES.toInt())+","+String.valueOf(CaptureService.BILLNOTES.toInt()));
		AttributeMultiValue oPassw = oDoc.attribute("password");
		oPassw.set(ACL.encript(getPassword(), ACL.PWD_DTIP_RC4_64));
		AttributeMultiValue oApr = oDoc.attribute("can_approve");
		oApr.set("1");
		AttributeMultiValue oStl = oDoc.attribute("can_settle");
		oStl.set("1");
		AttributeMultiValue oPre = oDoc.attribute("can_premium");
		oPre.set("1");
		oDoc.save("");
		oIdx.indexDocument(oDoc);

		put("user_id", oDoc.id());
		put("user_uuid", sNickName);
		put("can_approve", "1");
		put("can_settle", "1");
		put("can_premium", "1");
		put("active", getStringNull("active","1"));
		put("allowed_services", String.valueOf(CaptureService.INVOICES.toInt())+","+String.valueOf(CaptureService.BILLNOTES.toInt()));

		es.ipsa.atril.sec.user.User oUsr = oAum.getUser(getNickName());
		
		oAum.setDocumentRights(oUsr, oDoc, RightsFactory.getDocumentRightsAllGrant());

		try {
			oAum.setDocumentRights(oUsr, Deposits.top(oSes).getDocument(), RightsFactory.getDocumentRightsAllGrant());
		} catch (ElementNotFoundException ignore) { }
		
		DAO.log(oSes, oDoc, User.class, "CREATE USER", AtrilEvent.Level.INFO, String.valueOf(getAdministeredUser().getId())+";"+getEmail());

		Log.out.debug("End com.zesped.model.User.create() : "+oDoc.id());
	}
	
	public static User create(AtrilSession oSes, String sFirstName, String sLastName, String sEmailAddr, String sPassword, boolean bActive)
		throws IllegalArgumentException,AuthorizationManagerAdminException,DuplicatedElementException,ElementNotFoundException,NotEnoughRightsException {	
		User oUser = new User();
		oUser.put("active", bActive ? "1" : "0");
		oUser.setFirstName(sFirstName);
		oUser.setLastName(sLastName);
		oUser.setEmail(sEmailAddr);
		oUser.setPassword(sPassword);
		oUser.create(oSes);
		return oUser;
	}

	public void setPassword(String sPassw) {
		sPassword = sPassw;
	}

	public String getPassword() {
		return sPassword;
	}
	
	public void setFirstName(String sFirstName) {
		s1stName = sFirstName;
	}

	public String getFirstName() {
		return s1stName;
	}
	
	public String getLastName() {
		return s2ndName;
	}

	public String getFullName() {
		return s1stName+" "+s2ndName;
	}
	
	public void setLastName(String sLastName) {
		s2ndName = sLastName;
	}

	public void setEmail(String sEmailAddress) {
		if (sEmailAddress!=null) {
			sEmail = sEmailAddress.toLowerCase().trim();
			put("email", sEmail);
		} else {
			remove("email");
		}
	}

	public String getEmail() {
		return sEmail;
	}

	public void setNickName(String sNick) {
		sNickName = sNick;
	}

	public String getNickName() {
		return sNickName;
	}

	public boolean isActive() {
		if (isNull("active"))
			return true;
		else
			return getString("active").equals("1");
	}

	public void activate() {
		put ("active", "1");
	}

	public void deactivate() {
		put ("active", "0");
	}

	public Set<CaptureService> allowedServices() {
		String sServs = getStringNull("allowed_services");
		if (sServs.length()==0) {
			oServs.add(CaptureService.INVOICES);
			oServs.add(CaptureService.BILLNOTES);
		} else {
			String[] aServs = sServs.split(",");
			if (aServs.equals("1"))
				oServs.add(CaptureService.INVOICES);
			else if (aServs.equals("2"))
				oServs.add(CaptureService.BILLNOTES);
		}
		return oServs;
	}

	public void allowService(CaptureService oServ) {
		if (!oServs.contains(oServ)) oServs.add(oServ);
	}

	public void denyService(CaptureService oServ) {
		if (oServs.contains(oServ)) oServs.remove(oServ);
	}

	private void addTaxPayerToSet(AtrilSession oSes, String sTaxPayerId, HashSet<TaxPayer> oSet, String sType) throws IllegalArgumentException,ElementNotFoundException {
		TaxPayer oTxPr = null;
		Iterator<TaxPayer> oIter = oSet.iterator();
		while (oIter.hasNext()) {
			oTxPr = oIter.next();
			if (oTxPr.id().equals(sTaxPayerId)) return;
		}
		oTxPr = null;
		Dms oDms = oSes.getDms();
		boolean bSubFolderFound = false;
		for (Document c : getDocument().children())
			if (bSubFolderFound = c.type().name().equals(sType+"s")) break;
		if (!bSubFolderFound)
			oDms.newDocument(oDms.getDocumentType(sType+"s"), getDocument()).save("");
		for (Document c : getDocument().children()) {
			if (c.type().name().equals(sType+"s")) {
				oTxPr = new TaxPayer(oDms, sTaxPayerId);
				BaseCompanyObject oObj;
				if (sType.equals("AllowedTaxPayer"))
					oObj = new AllowedTaxPayer();
				else if (sType.equals("DeniedTaxPayer"))
					oObj = new DeniedTaxPayer();
				else
					throw new IllegalArgumentException("Unrecognized type "+sType);				
				oObj.newDocument(oSes, oDms.getDocument(c.id()));
				oObj.put("taxpayer", sTaxPayerId);
				oObj.put("creation_date", new Date());
				oObj.put("customer_acount", oTxPr.getString("customer_acount"));
				oObj.put("business_name", oTxPr.getString("business_name"));
				oObj.put("tax_id", oTxPr.getStringNull("tax_id",""));
				oObj.save(oSes);
			}
		}
		if (oTxPr!=null) oSet.add(oTxPr);
	}

	private void removeTaxPayerFromSet(AtrilSession oSes, String sTaxPayerId, HashSet<TaxPayer> oSet, String sType)
		throws ElementNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException,
		ClassCastException, IllegalStateException, NullPointerException {
		TaxPayer oTxp = null;
		Iterator<TaxPayer> oIter = oSet.iterator();
		while (oIter.hasNext()) {
			TaxPayer oPry = oIter.next();
			if (oPry.id().equals(sTaxPayerId)) {
				oTxp = oPry;
				break;
			}
		}
		if (oTxp!=null) {
			oSet.remove(oTxp);
			BaseCompanyObject oObj;
			if (sType.equals("AllowedTaxPayer"))
				oObj = new AllowedTaxPayer();
			else if (sType.equals("DeniedTaxPayer"))
				oObj = new DeniedTaxPayer();
			else
				throw new IllegalArgumentException("Unrecognized type "+sType);				
			Document t = oObj.exists(oSes, "taxpayer", sTaxPayerId);
			if (t!=null) {
				BaseCompanyObject.delete(oSes, t.id());
			}
		}
	}

	public void allowAll(AtrilSession oSes)
		throws ElementNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IllegalStateException {
	    Dms oDms = oSes.getDms();
		for (Document c : getDocument().children())
			if (c.type().equals("AllowedTaxPayers"))
				new AllowedTaxPayers(oDms.getDocument(c.id())).clear(oSes);
			else if (c.type().equals("DeniedTaxPayers"))
				new DeniedTaxPayers(oDms.getDocument(c.id())).clear(oSes);
	}

	public void allowTaxPayer(AtrilSession oSes, String sTaxPayerId)
		throws ElementNotFoundException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, ClassCastException, IllegalStateException, NullPointerException {
		addTaxPayerToSet(oSes, sTaxPayerId, oAllwed, "AllowedTaxPayer");
		removeTaxPayerFromSet(oSes, sTaxPayerId, oDenied, "DeniedTaxPayer");		
	}

	public void denyTaxPayer(AtrilSession oSes, String sTaxPayerId)
		throws ElementNotFoundException, ClassNotFoundException, InstantiationException,
			   IllegalAccessException, ClassCastException, IllegalStateException, NullPointerException {
		addTaxPayerToSet(oSes, sTaxPayerId, oDenied, "DeniedTaxPayer");
		removeTaxPayerFromSet(oSes, sTaxPayerId, oAllwed, "AllowedTaxPayer");		
	}

	public Role getRole(CustomerAccount oAcc) throws NullPointerException {
		if (null==oAcc) throw new NullPointerException("User.getRole() CustomerAccount cannot be null");
		Log.out.debug("Begin User.getRole("+oAcc.getBusinessName()+")");
		AtrilSession oSes = DAO.getAdminSession("getRole");
		Role eRetVal = null;
		try {
		  for (User oUsr : oAcc.getAdmins(oSes))
			  if (id().equals(oUsr.id()))
				  eRetVal = Role.admin;
		  if (null==eRetVal)
			  for (User oUsr : oAcc.getUsers(oSes))
				  if (id().equals(oUsr.id()))
					  eRetVal = Role.user;
		  if (null==eRetVal)
			  for (User oUsr : oAcc.getGuests(oSes))
				  if (id().equals(oUsr.id()))
					  eRetVal = Role.guest;
		} finally {
			if (oSes!=null) {
				if (oSes.isConnected()) oSes.disconnect();
				if (oSes.isOpen()) oSes.close();				
			}			
		}
		Log.out.debug("End User.getRole() : "+eRetVal);
		return eRetVal;
	}

	public void setRole(AtrilSession oSes , CustomerAccount oAcc, Role eRole)
		throws IllegalArgumentException,NullPointerException {

		if (oAcc!=null && eRole.equals(Role.oper))
			throw new IllegalArgumentException("User.setRole() cannot set oper role over a customer account");
		if (oAcc==null && !eRole.equals(Role.oper))
			throw new NullPointerException("User.setRole() customer account cannot be null");

		Log.out.debug("Begin User.setRole("+id()+","+eRole+")");
		
		if (oAcc==null) {
			AuthorizationAdminManager oAum = oSes.getAuthorizationAdminManager();
			AdministeredGroup oGrp = oAum.getGroup("Back-End operators");
			if (!oGrp.getMembers().contains(getAdministeredUser()))
				oGrp.addMember(getAdministeredUser());			
		} else {
			if (!getStringNull("customer_acount","").equals(oAcc.getUuid()) ||
			   (eRole.equals(Role.admin) && (!canApproveInvoices() || !canSettleBillNotes() || !canUsePremiumCaptureServiceFlavor()))) {
				put ("customer_acount",oAcc.getUuid());
				if (eRole.equals(Role.admin)) {
					canApproveInvoices(true);
					canSettleBillNotes(true);
					canUsePremiumCaptureServiceFlavor(true);
				}
				super.save(oSes);
			}
			if (eRole.equals(Role.admin)) {
				oAcc.grantAdmin(oSes, this);
				try { oAcc.denyUser(oSes, this); } catch (ElementNotFoundException ignore) { }
				try { oAcc.denyGuest(oSes, this); } catch (ElementNotFoundException ignore) { }
			}
			if (eRole.equals(Role.user)) {
				oAcc.grantUser(oSes, this);
				try { oAcc.denyAdmin(oSes, this); } catch (ElementNotFoundException ignore) { }
				try { oAcc.denyGuest(oSes, this); } catch (ElementNotFoundException ignore) { }
			}
			if (eRole.equals(Role.guest)) {
				oAcc.grantGuest(oSes, this);
				try { oAcc.denyAdmin(oSes, this); } catch (ElementNotFoundException ignore) { }
				try { oAcc.denyUser(oSes, this); } catch (ElementNotFoundException ignore) { }
			}
			AuthorizationManager oAum = oSes.getAuthorizationManager();
			UsersGroup oAdm = new UsersGroup(oAum, oAcc.getAdminsGroup(oAum));
			oAdm.grantAll(getDocument());			
		}
		Log.out.debug("End User.setRole()");
	}
	
	public boolean canApproveInvoices() {
		return getStringNull("can_approve","1").equals("1");
	}

	public void canApproveInvoices(boolean b) {
		put ("can_approve", b ? "1" : "0");
	}
	
	public boolean canSettleBillNotes() {
		return getStringNull("can_settle","1").equals("1");
	}

	public void canSettleBillNotes(boolean b) {
		put ("can_settle", b ? "1" : "0");
	}
	
	public boolean canUsePremiumCaptureServiceFlavor() {
		return getStringNull("can_premium","1").equals("1");
	}

	public void canUsePremiumCaptureServiceFlavor(boolean b) {
		put ("can_premium", b ? "1" : "0");
	}
	
	private void loadTaxPayerPermissions(AtrilSession oSes)
		throws ElementNotFoundException, NotEnoughRightsException , DmsException,
		InstantiationException, IllegalStateException, IllegalAccessException {
		Dms oDms = oSes.getDms();
		for (Document c : getDocument().children()) {
			if (c.type().name().equals("AllowedTaxPayers"))
				for (AllowedTaxPayer oAlTp : new AllowedTaxPayers(oDms.getDocument(c.id())).list(oSes))
					oAllwed.add(new TaxPayer(oDms, oAlTp.getString("taxpayer")));
			else if (c.type().name().equals("DeniedTaxPayers"))
				for (DeniedTaxPayer oDnTp : new DeniedTaxPayers(oDms.getDocument(c.id())).list(oSes))
					oDenied.add(new TaxPayer(oDms, oDnTp.getString("taxpayer")));
		}
	}

	public CaptureServiceFlavor defaultCaptureServiceFlavor() throws ElementNotFoundException {
		return CaptureServiceFlavors.getCaptureServiceFlavor(getStringNull("default_service_flavor","INVOICESBASIC"));
	}

	public CaptureType defaultCaptureType(AtrilSession oSes) throws ElementNotFoundException {
		if (isNull("default_capture_type"))
			return CaptureTypes.seek(oSes, "UnsignedSinglePageHalfDuplexNoGUI");
		else
			return CaptureTypes.seek(oSes, getString("default_capture_type"));
	}

	public void defaultCaptureType(String sCaptureType) throws ElementNotFoundException {
		put ("default_capture_type",sCaptureType);
	}
	
	public Scanner defaultScanner(AtrilSession oSes) throws ElementNotFoundException {
		if (isNull("default_scanner"))
			return defaultCaptureType(oSes).seek(oSes, "Twain");
		else
			return defaultCaptureType(oSes).seek(oSes, getString("default_scanner"));
	}

	public void defaultScanner(String sScanner) throws ElementNotFoundException {
		put ("default_scanner",sScanner);
	}

	public void defaultCaptureOptions(String sCaptureType, String sScanner) throws ElementNotFoundException {
		put ("default_capture_type",sCaptureType);
		put ("default_scanner",sScanner);
	}

	@Override
	public void load(AtrilSession oSes, String sDocId) throws ElementNotFoundException, NotEnoughRightsException , DmsException {

		super.load(oSes, sDocId);

		setEmail(getDocument().attribute("email").toString());
		setNickName(getDocument().attribute("user_uuid").toString());

		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		setAdministeredUser(oAam.getUser(getDocument().attribute("user_uuid").toString()));
		setFirstName(getAdministeredUser().getFirstName());
		setLastName(getAdministeredUser().getLastName());
		if (isNull("password"))
			setPassword(null);
		else
			setPassword(ACL.decript(getString("password"), ACL.PWD_DTIP_RC4_64));
		sFormer1stName = getFirstName();
		sFormer2ndName = getLastName();
		sFormerPassword= getPassword();
		allowedServices();
		try {
			loadTaxPayerPermissions(oSes);
		} catch (InstantiationException e) {
			Log.out.error("User.loadTaxPayerPermissions() InstantiationException "+e.getMessage(), e);
		} catch (IllegalStateException e) {
			Log.out.error("User.loadTaxPayerPermissions() IllegalStateException "+e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Log.out.error("User.loadTaxPayerPermissions() IllegalAccessException "+e.getMessage(), e);
		}
	}
	
	@Override
	public void save(AtrilSession oSes)
		throws ClassCastException, RuntimeException, IllegalStateException, NullPointerException, DmsDocumentModificationException, 
		NotYetConnectedException, DmsException, RecentlyUsedPasswordException, OldPasswordWrongOnPasswordChangeException {
		
		if (getEmail()==null) throw new NullPointerException("User e-mail cannot be null");
		if (exists(oSes,"user_uuid",sNickName)==null) throw new ElementNotFoundException("Cannot find any user with uuid "+sNickName);

		String sAllowedServices = "";
		for (CaptureService s : allowedServices())
			sAllowedServices += (sAllowedServices.length()==0 ? "" : ",") + String.valueOf(s.toInt());
		put ("allowed_services", sAllowedServices);

		if (getPassword()!=null) {
			put("password", ACL.encript(getPassword(), ACL.PWD_DTIP_RC4_64));
		}

		if (!sFormer1stName.equals(getFirstName()) || !sFormer2ndName.equals(getLastName()) || !sFormerPassword.equals(getPassword())) {
			AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
			AdministeredUser oUsr = oAam.getUser(sNickName);
			super.save(oSes);
			if (!sFormer1stName.equals(getFirstName())) oUsr.setFirstName(getFirstName());
			if (!sFormer2ndName.equals(getLastName())) oUsr.setLastName(getLastName());
			if (!sFormerPassword.equals(getPassword())) {
				oUsr.changePassword(sFormerPassword, getPassword());
			}
			oAam.updateUser(oUsr);
		} else {
			super.save(oSes);			
		}
	}

	public void delete(AtrilSession oSes)
		throws DmsException, ElementNotFoundException, ClassNotFoundException,
			   InstantiationException, IllegalAccessException, ClassCastException,
			   IllegalStateException, NullPointerException,SecurityException {
		if (getNickName().equals("admin")) throw new SecurityException("Cannot delete admin user");
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		oAam.removeUser(getAdministeredUser());
		BaseModelObject.delete(oSes, id());
		clear();
	}
		
	@Override
	public User convert(String sDocId, Class<? extends User> userClass,
						   Collection<ValidationError> conversionErrors) {
		try {			
			AtrilSession oSes = DAO.getAdminSession("User");
			User oUsr = new User(oSes, sDocId);
			oSes.disconnect();
			oSes.close();
			oSes = null;
			return oUsr;
		} catch (Exception exc) {
			Log.out.error(exc.getClass().getName()+" User.convert("+sDocId+") "+exc.getMessage());
			conversionErrors.add(new SimpleError(exc.getMessage()));
			return null;
		}
	}
		
	@Override
	public void setLocale(Locale locale) { }	
}
