package com.zesped.action;

import javax.servlet.http.HttpServletRequest;

import com.knowgate.storage.StorageException;
import com.zesped.Log;
import com.zesped.model.BaseModelObject;
import com.zesped.model.BillNote;
import com.zesped.model.Cache;
import com.zesped.model.Client;
import com.zesped.model.Invoice;
import com.zesped.model.TaxPayer;
import com.zesped.model.Ticket;

import es.ipsa.atril.doc.user.exceptions.DmsException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;

public class DeleteDocuments extends BaseAjaxBean {

	@SuppressWarnings("static-access")
	@DefaultHandler
	public Resolution delete() {
		int d = 0;
		String[] aDocs = null;
		HttpServletRequest oReq = getContext().getRequest();
		final String sClss = oReq.getParameter("type");
		final String sDocs = oReq.getParameter("docs");
		Log.out.debug("Begin DeleteDocuments.delete("+sDocs+")");
		if (sDocs!=null) {
			if (sDocs.length()>0) {
				aDocs = sDocs.split(",");
				try {
					if (sClss.equals("Client")) {
						connect();
						for (d=0; d<aDocs.length; d++) {
							try {
								Client.delete(getSession(),aDocs[d]);
								addDataLine("docid", aDocs[d]);
								commit();
							} catch (Exception xcpt) {
								rollback();
								addError("document", new SimpleError (xcpt.getClass().getName()+" "+xcpt.getMessage()));
								Log.out.error("DeleteDocuments.delete("+aDocs[d]+")  "+xcpt.getClass().getName()+" "+xcpt.getMessage());
								Log.out.error(Log.stackTrace(xcpt), xcpt);
							}
						}
						disconnect();
						Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"accsuppliers");
						Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"clients");
					} else if (sClss.equals("TaxPayer")) {
						connect();
						for (d=0; d<aDocs.length; d++) {
							try {
								TaxPayer.delete(getSession(),aDocs[d]);
								addDataLine("docid", aDocs[d]);
								commit();
							} catch (Exception xcpt) {
								rollback();
								addError("document", new SimpleError (xcpt.getClass().getName()+" "+xcpt.getMessage()));
								Log.out.error("DeleteDocuments.delete("+aDocs[d]+")  "+xcpt.getClass().getName()+" "+xcpt.getMessage());
								Log.out.error(Log.stackTrace(xcpt), xcpt);
							}
						}
						disconnect();
						Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"acctaxpayers");
						Cache.deleteEntry(getSessionAttribute("customer_account_docid")+"taxpayers");
					} else if (sClss.equals("Invoice")) {
						connect();
						for (d=0; d<aDocs.length; d++) {
							try {
								Invoice.delete(getSession(),aDocs[d]);
								addDataLine("docid", aDocs[d]);
								commit();
							} catch (Exception xcpt) {
								rollback();
								addError("document", new SimpleError (xcpt.getClass().getName()+" "+xcpt.getMessage()));
								Log.out.error("DeleteDocuments.delete("+aDocs[d]+")  "+xcpt.getClass().getName()+" "+xcpt.getMessage());
								Log.out.error(Log.stackTrace(xcpt), xcpt);
							}
						}
						disconnect();
					} else if (sClss.equals("Ticket")) {
						connect();
						for (d=0; d<aDocs.length; d++) {
							try {
								Ticket.delete(getSession(),aDocs[d]);
								addDataLine("docid", aDocs[d]);
								commit();
							} catch (Exception xcpt) {
								rollback();
								addError("document", new SimpleError (xcpt.getClass().getName()+" "+xcpt.getMessage()));
								Log.out.error("DeleteDocuments.delete("+aDocs[d]+")  "+xcpt.getClass().getName()+" "+xcpt.getMessage());
								Log.out.error(Log.stackTrace(xcpt), xcpt);
							}
						}
						disconnect();
					} else if (sClss.equals("BillNote")) {
						connect();
						for (d=0; d<aDocs.length; d++) {
							try {
								BillNote.delete(getSession(),aDocs[d]);
								addDataLine("docid", aDocs[d]);
								commit();
							} catch (Exception xcpt) {
								rollback();
								addError("document", new SimpleError (xcpt.getClass().getName()+" "+xcpt.getMessage()));
								Log.out.error("DeleteDocuments.delete("+aDocs[d]+")  "+xcpt.getClass().getName()+" "+xcpt.getMessage());
								Log.out.error(Log.stackTrace(xcpt), xcpt);
							}
						}
						disconnect();
					} else {
						Class oClss = Class.forName("com.zesped.model."+sClss);
						BaseModelObject oObj = (BaseModelObject) oClss.newInstance();
						connect();
						for (d=0; d<aDocs.length; d++) {
							try {
								oObj.delete(getSession(),aDocs[d]);
								addDataLine("docid", aDocs[d]);
								commit();
							} catch (Exception xcpt) {
								rollback();
								addError("document", new SimpleError (xcpt.getClass().getName()+" "+xcpt.getMessage()));
								Log.out.error("DeleteDocuments.delete("+aDocs[d]+")  "+xcpt.getClass().getName()+" "+xcpt.getMessage());
								Log.out.error(Log.stackTrace(xcpt), xcpt);
							}
						}
						disconnect();
					}
				} catch (ClassCastException cce) {
					addError("type", new SimpleError ("Class cast exception "+sClss));
					Log.out.error("DeleteDocuments.delete() ClassCastException "+cce.getMessage()+" document "+aDocs[d]);
				} catch (ClassNotFoundException cnfe) {
					addError("type", new SimpleError ("Class not found "+sClss));
					Log.out.error("DeleteDocuments.delete() ClassNotFoundException "+cnfe.getMessage()+" document "+aDocs[d]);
				} catch (InstantiationException inse) {
					addError("type", new SimpleError ("Instantiation exception "+sClss));
					Log.out.error("DeleteDocuments.delete() InstantiationException "+inse.getMessage()+" document "+aDocs[d]);
				} catch (IllegalAccessException ilae) {
					addError("type", new SimpleError ("Illegal access exception "+sClss));
					Log.out.error("DeleteDocuments.delete() IllegalAccessException "+ilae.getMessage()+" document "+aDocs[d]);
				} catch (IllegalStateException ilst) {
					addError("type", new SimpleError ("Illegal state exception "+sClss));
					Log.out.error("DeleteDocuments.delete() IllegalStateException "+ilst.getMessage()+" document "+aDocs[d]);
				} catch (StorageException stox) {
					addError("document", new SimpleError ("Storage exception"));
					Log.out.error("DeleteDocuments.delete() StorageException "+stox.getMessage()+" document "+aDocs[d]);
				} catch (DmsException dmse) {
					addError("document", new SimpleError ("DMS exception"));
					Log.out.error("DeleteDocuments.delete() DmsException "+dmse.getMessage()+" document "+aDocs[d]);
				} catch (Exception xcpt) {
					addError("document", new SimpleError (xcpt.getClass().getName()));
					Log.out.error("DeleteDocuments.delete()"+xcpt.getClass().getName()+" "+xcpt.getMessage()+" document "+aDocs[d]);
				} finally {
					close();					
				}
			}
		}
		Log.out.debug("End DeleteDocuments.delete("+sDocs+")");
		return AjaxResponseResolution();
	}
	
}
