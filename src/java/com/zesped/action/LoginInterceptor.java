package com.zesped.action;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.TaxPayer;
import com.zesped.model.User;

import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

@Intercepts(LifecycleStage.ActionBeanResolution)
public class LoginInterceptor implements Interceptor {

	private static HashSet<String> oActive = new HashSet<String>();
	private static HashSet<String> oCredentials = new HashSet<String>();

	@SuppressWarnings("unchecked" )
	private static final List<Class<? extends BaseActionBean>> ALLOW =
	Arrays.asList(
			ActivateUser.class,
			SessionPing.class,
			SignUpForm.class,
			SignUpConfirmation.class,
			RecoverPassword.class,
			ContactForm.class,
			TpvNotificacion.class
	);

	@SuppressWarnings("unchecked" )
	private static final List<Class<? extends BaseEditBean>> CHKTXP =
	Arrays.asList(
			CaptureInvoice.class,
			CaptureBillNote.class
	);
	
	private boolean isAjaxBean(Class oCls) {
		if (oCls==null)
			return false;
		else if (oCls.getName().equals("com.zesped.action.BaseAjaxBean"))
			return true;
		else
			return isAjaxBean(oCls.getSuperclass());
	}

	private Resolution resolve(Class oCls, String sErrCode, String sLastURL) {
		Resolution oRes;
		if (isAjaxBean(oCls)) {
			oRes = new RedirectResolution("/ajaxerror.jsp?e="+sErrCode);			
		} else {
			oRes = new RedirectResolution("/enter.jsp?e="+sErrCode);
			if (sLastURL!=null)
				((RedirectResolution) oRes).addParameter("lastUrl" , sLastURL);		
		}
		return oRes;
	}

	public Resolution intercept(ExecutionContext execContext) throws Exception {
		Resolution oRes = execContext.proceed();
		BaseActionBean oBab = (BaseActionBean) execContext.getActionBean();
		Class<? extends ActionBean> oCls = oBab.getClass();
		if (ALLOW.contains(oCls)) {
			return oRes;
		} else {
			final String sNickName = oBab.getSessionAttribute("nickname");
			final String sPassword = oBab.getSessionAttribute("password");
			final String sTaxPayer = oBab.getSessionAttribute("taxpayer_docid");
			AtrilSession oSes = null;
			if (sNickName==null || sPassword==null || sTaxPayer==null) {
				oRes = new RedirectResolution("/enter.jsp?e=expiredsession");
				((RedirectResolution) oRes).addParameter("lastUrl" , oBab.getLastUrl());				
			} else {
				if (!oCredentials.contains(sNickName+sPassword)) {
					try {
						oSes = DAO.getSession("LoginInterceptor", sNickName, sPassword);
						try {
							User oUsr = new User(oSes, User.forUuid(sNickName));
							if (!oUsr.isActive()) {
								oRes = resolve(oCls, "deactivated", oBab.getLastUrl());
							} else {
								if (CHKTXP.contains(oCls) && !oActive.contains(sTaxPayer)) {
									TaxPayer oTxp = new TaxPayer(oSes.getDms(), sTaxPayer);
									if (oTxp.getRequiresActivation()) {
										oRes = new RedirectResolution(ActivateTaxPayer.class);
									} else {
										oActive.add(sTaxPayer);
									}
								}
								oCredentials.add(sNickName+sPassword);
							}
						} catch (ElementNotFoundException enfe) {
							Log.out.error("ElementNotFoundException "+enfe.getMessage(), enfe);
							oRes = resolve(oCls, "userdatanotfound", oBab.getLastUrl());
						}
					} catch (Exception xcpt) {
						Log.out.error(xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
						oRes = resolve(oCls, "systemsecurity", oBab.getLastUrl());
					} finally {
						if (null!=oSes) {
							if (oSes.isConnected()) oSes.disconnect();
							if (oSes.isOpen()) oSes.close();
						} // fi
					}					
				} else if (oBab.getSessionAttribute("businessname").length()==32) {
					if (CHKTXP.contains(oCls)) {
						oSes = DAO.getSession("LoginInterceptor", sNickName, sPassword);
						TaxPayer oTxp = new TaxPayer(oSes.getDms(), sTaxPayer);
						oBab.setSessionAttribute("businessname", oTxp.getBusinessName());
						if (oTxp.getRequiresActivation()) {
							oRes = new RedirectResolution(ActivateTaxPayer.class);
						} else if (!oActive.contains(sTaxPayer)) {
							oActive.add(sTaxPayer);
						}
						oSes.disconnect();
						oSes.close();						
					}
				}
			} // fi
		}
		return oRes;
	}
	
	public static void expire(String sNickName, String sPassword) {
		oCredentials.remove(sNickName+sPassword);
	}
}
