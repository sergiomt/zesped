package com.zesped.model;

import com.knowgate.misc.Gadgets;
import com.zesped.DAO;

import es.ipsa.atril.SortableList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.documentindexer.DocumentIndexer;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

@SuppressWarnings("serial")
public abstract class BaseCompanyObject extends BaseModelObject {

	public BaseCompanyObject(String sType) {
		super(sType);
	}
	
	public BaseCompanyObject(Document c) {
		super(c);
	}

	public BaseCompanyObject(Dms oDms, String sDocId) throws ElementNotFoundException {
		super(oDms, sDocId);
	}
	
	protected static String sanitizeBusinessName(String businessName) {
		return  Gadgets.removeChars(businessName, "\"*?").trim().toUpperCase();
	}

	protected static class CheckCompanyEmailSyntax implements CustomConstraint {
		public boolean check (AtrilSession oSes, DocumentIndexer oIdx, BaseModelObject oObj) {
			if (oObj.isNull("email"))
				return true;
			else if (oObj.getString("email").length()==0)
				return true;
			else
				return Gadgets.checkEMail(oObj.getString("email"));
		}
	}
	
	public static CustomConstraint checkEmailSyntaxConstraint() {
		return new CheckCompanyEmailSyntax();
	}
		
	public String getBusinessName() {
		return getStringNull("business_name");
	}

	public void setBusinessName(String businessName) {
		if (null==businessName)
			remove("business_name");
		else
			put("business_name", sanitizeBusinessName(businessName));
	}

	public String getTaxId() {
		return getStringNull("tax_id");
	}

	public void setTaxId(String taxId) {
		if (null==taxId)
			remove("tax_id");
		else
			put("tax_id", taxId.trim());
	}

	public String getAddress1() {
		if (isNull("address1"))
			return null;
		else
			return getString("address1");
	}

	public void setAddress1(String address1) {
		if (null==address1)
			remove("address1");
		else
			put("address1", address1.trim());
	}

	public String getAddress2() {
		if (isNull("address2"))
			return null;
		else
			return getString("address2");
	}

	public void setAddress2(String address2) {
		if (null==address2)
			remove("address2");
		else
			put("address2", address2.trim());
	}

	public String getCity() {
		if (isNull("city"))
			return null;
		else
			return getString("city");
	}

	public void setCity(String city) {
		if (null==city)
			remove("city");
		else
			put("city", city.trim());
	}
	
	public String getZipCode() {
		if (isNull("zipcode"))
			return null;
		else
			return getString("zipcode");
	}

	public void setZipCode(String zipcode) {
		if (null==zipcode)
			remove("zipcode");
		else
			put("zipcode", zipcode.trim());
	}

	public String getState() {
		if (isNull("state"))
			return null;
		else
			return getString("state");
	}

	public void setState(String state) {
		if (null==state)
			remove("state");
		else
			put("state", state.trim().toUpperCase());
	}

	public String getCountry() {
		if (isNull("country"))
			return null;
		else
			return getString("country");
	}

	public void setCountry(String country) {
		if (null==country)
			remove("country");
		else
			put("country", country.trim());
	}

	public String getTelephone() {
		if (isNull("telephone"))
			return null;
		else
			return getString("telephone");
	}

	public void setTelephone(String telephone) {
		if (null==telephone)
			remove("telephone");
		else
			put("telephone", telephone.trim());
	}

	public String getEmail() {
		if (isNull("email"))
			return null;
		else
			return getString("email");
	}

	public void setEmail(String email) {
		if (null==email)
			remove("email");
		else
			put("email", email.trim().toLowerCase());
	}

	public String getContactPerson() {
		if (isNull("contact_person"))
			return null;
		else
			return getString("contact_person");
	}

	public void setContactPerson(String contact_person) {
		if (null==contact_person)
			remove("contact_person");
		else
			put("contact_person", contact_person.trim());
	}

	public boolean getRequiresActivation() {
		if (isNull("active"))
			return false;
		else
			return getString("active").equals("-1");
	}

	public void setRequiresActivation(boolean a) {
		put ("active", a ? "-1" : "1");
	}
	
	public boolean getActive() {
		if (isNull("active"))
			return true;
		else
			return getString("active").equals("1");
	}

	public void setActive(boolean a) {
		put ("active", a ? "1" : "0");
	}

	protected static String forBusinessName(String sBusinessName, String sTypeName)  throws ElementNotFoundException, NullPointerException, IllegalArgumentException {
		if (null==sBusinessName) throw new NullPointerException("Business Name cannot be null");
		AtrilSession oSes = DAO.getAdminSession("forBusinessName");
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		String sSanitizedBusinessName = sanitizeBusinessName(sBusinessName);
		if (sSanitizedBusinessName.length()==0) {
			oSes.disconnect();
			oSes.close();
			throw new IllegalArgumentException("Business Name cannot be empty");
		}
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query("business_name:\"" + sSanitizedBusinessName + "\" AND " + DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + sTypeName);
		if (oLst.isEmpty()) {
			oSes.disconnect();
			oSes.close();
			throw new ElementNotFoundException(sBusinessName+" not found");
		} else {
			String sId = oLst.get(0).id();
			oSes.disconnect();
			oSes.close();
			return sId;
		}
	}

	protected static String forTaxId(String sTaxId, String sTypeName)  throws ElementNotFoundException, NullPointerException, IllegalArgumentException {
		if (null==sTaxId) throw new NullPointerException("Tax Id cannot be null");
		AtrilSession oSes = DAO.getAdminSession("forTaxId");
		DocumentIndexer oIdx = oSes.getDocumentIndexer();
		if (sTaxId.length()==0) {
			oSes.disconnect();
			oSes.close();
			throw new IllegalArgumentException("Tax Id cannot be empty");
		}
		SortableList<Document> oLst = (SortableList<Document>) oIdx.query("tax_id:\"" + sTaxId + "\" AND " + DocumentIndexer.AdditionalDocumentFields.DOCUMENT_TYPE_NAME.value() + ":" + sTypeName);
		if (oLst.isEmpty()) {
			oSes.disconnect();
			oSes.close();
			throw new ElementNotFoundException(sTaxId+" not found");
		} else {
			String sId = oLst.get(0).id();
			oSes.disconnect();
			oSes.close();
			return sId;			
		}
	}

	
}
