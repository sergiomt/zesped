package com.zesped.action;

import java.util.ArrayList;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;

import com.knowgate.misc.NameValuePair;

import com.zesped.model.CaptureService;


public abstract class BaseAjaxBean extends BaseActionBean {

	private static final String XML="/WEB-INF/jsp/ajaxresponse.jsp" ;
	
	private ArrayList<SimpleError> infos = new ArrayList<SimpleError>();
	private ArrayList<SimpleError> errors = new ArrayList<SimpleError>();	
	private ArrayList<NameValuePair> datavalues = new ArrayList<NameValuePair>();	

	public ArrayList<NameValuePair> getResponseData() {
		return datavalues;
	}
	
	public ArrayList<SimpleError> getInformationMessages() {
		return infos;
	}

	public int getInformationMessagesCount() {
		return infos.size();
	}
	
	public ArrayList<SimpleError> getErrors() {
		return errors;
	}

	public int getErrorsCount() {
		return errors.size();
	}

	public ValidationError addError(String f, SimpleError e, ValidationErrors v) {
		errors.add(e);
		if (v!=null)
			if (f!=null)
				if (f.length()>0)
					v.add(f, e);
				else
					v.addGlobalError(e);
			else
				v.addGlobalError(e);
		return e;
	}
	
	public ValidationError addError(String f, SimpleError e) {
		return addError(f, e, null);
	}

	public ValidationError addError(SimpleError e, ValidationErrors v) {
		return addError(e.getFieldName(), e, null);
	}
	
	public ValidationError addError(SimpleError e) {
		return addError(e.getFieldName(), e, null);
	}

	public ValidationError addError(String f, LocalizableError l, ValidationErrors v) {
		SimpleError e = new SimpleError(l.getMessage(getContext().getLocale()), l.getReplacementParameters());
		if (l.getFieldName()!=null) e.setFieldName(l.getFieldName());
		if (l.getFieldValue()!=null) e.setFieldValue(l.getFieldValue());
		return addError(f, e, v);
	}
	
	public ValidationError addError(LocalizableError l, ValidationErrors v) {
		SimpleError e = new SimpleError(l.getMessage(getContext().getLocale()), l.getReplacementParameters());
		if (l.getFieldName()!=null) e.setFieldName(l.getFieldName());
		if (l.getFieldValue()!=null) e.setFieldValue(l.getFieldValue());
		return addError(e, v);
	}
	
	public ValidationError addError(LocalizableError l) {
		return addError(l, null);
	}
	
	public ValidationError addInformationMessage(SimpleError m) {
		infos.add(m);
		return m;
	}

	public NameValuePair addDataLine(String sCode, String sText) {
		NameValuePair nv = new NameValuePair(sCode,sText);
		datavalues.add(nv);
		return nv;
	}
	
	public Resolution AjaxResponseResolution() {
	    return new ForwardResolution(XML);	
	}

	@Override
	public CaptureService getCaptureService() {
		return null;
	}

}
