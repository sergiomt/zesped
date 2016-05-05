package com.zesped.model;


import es.ipsa.atril.exceptions.ElementNotFoundException;

import es.ipsa.atril.sec.admin.exceptions.AuthorizationManagerAdminException;
import es.ipsa.atril.sec.admin.exceptions.DuplicatedElementException;
import es.ipsa.atril.sec.authentication.AtrilSession;
import es.ipsa.atril.sec.exceptions.NotEnoughRightsException;

public class Operator extends User {

	private static final long serialVersionUID = 1L;

	public static Operator create(AtrilSession oSes, String sFirstName, String sLastName, String sEmailAddr, String sPassword)
		throws IllegalArgumentException,AuthorizationManagerAdminException,DuplicatedElementException,ElementNotFoundException,NotEnoughRightsException {
		Operator oOpr = new Operator();
		oOpr.setFirstName(sFirstName);
		oOpr.setLastName(sLastName);
		oOpr.setEmail(sEmailAddr);
		oOpr.setPassword(sPassword);
		oOpr.create(oSes);
		return oOpr;
	}

	/*
	@Override
	public void create(AtrilSession oSes)
		throws IllegalArgumentException,AuthorizationManagerAdminException,DuplicatedElementException,ElementNotFoundException,NotEnoughRightsException {
		super.create(oSes);
		AuthorizationAdminManager oAam = oSes.getAuthorizationAdminManager();
		AdminGroupRights oAll = AdminRightsFactory.getGroupRightsAllGrant();
		AdministeredUser oAsr = getAdministeredUser();
		AdministeredGroup oAdm = Zesped.getOperatorsAdministeredGroup(oAam);
		oAdm.addMember(oAsr);
		oAam.setRights(oAsr, oAdm, oAll);
		DAO.log(oSes, getDocument(), getClass(), "CREATE OPERATOR", AtrilEvent.Level.INFO, String.valueOf(getAdministeredUser().getId())+";"+getEmail());
	}
	*/
}
