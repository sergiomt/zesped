package com.zesped.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.knowgate.jdc.JDCConnection;
import com.zesped.DAO;
import com.zesped.Log;
import com.zesped.model.CaptureService;
import com.zesped.model.Invoice;
import com.zesped.model.Invoices;
import com.zesped.model.TaxPayer;

import es.ipsa.atril.NodeList;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.exceptions.DmsException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class DepositToZespedBridge extends Thread {

	private CaptureService oSrv;
	private long lDepositId, lBillableId;
	private String sUid, sFlavor, sTaxPayerId, sBiller, sRecipient;
	
	public DepositToZespedBridge(CaptureService oCapSrv, long lDepositId, long lBillableId) {
		this.lDepositId = lDepositId;
		this.lBillableId= lBillableId;
		oSrv = oCapSrv;
	}

	public DepositToZespedBridge(CaptureService oCapSrv, long lDepositId, String sUid, String sFlavor, String sTaxPayerId, String sBiller, String sRecipient) {
		this.lDepositId = lDepositId;
		this.sUid = sUid;
		this.sFlavor = sFlavor;
		this.sTaxPayerId = sTaxPayerId;
		this.sBiller = sBiller;
		this.sRecipient = sRecipient;
		oSrv = oCapSrv;
	}
	
	public void run() {
		Log.out.debug("Begin AttachDeposit.run()");		
		AtrilSession oSes = null;
		try {
			oSes = DAO.getAdminSession("DepositToZespedBridge");
			oSes.autoCommit(false);
			switch (oSrv) {
				case BILLNOTES:
					addSidesToTicket(oSes);
					break;
				case INVOICES:
					addDocumentsToInvoices(oSes);
					break;
			}
			oSes.commit();
			oSes.disconnect();
			oSes.close();
			oSes = null;
		} catch (Exception xcpt) {
			Log.out.error("DepositToZespedBridge.run() "+xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
		} finally {
			if (oSes!=null) {
				if (oSes.isConnected()) oSes.disconnect();
				if (oSes.isOpen()) oSes.close();
			}
		}
		Log.out.debug("End AttachDeposit.run()");		
	}

	public void addSidesToTicket(AtrilSession oSes)
		throws DmsException {
		
		Log.out.debug("Begin DepositToZespedBridge.addSidesToTicket("+String.valueOf(lDepositId)+","+String.valueOf(lBillableId)+")");
		ArrayList<Document> aSides = new ArrayList<Document>();
		JDCConnection oCon = null;
		PreparedStatement oStm = null;
		try {
			oCon = DAO.getConnection("DepositToZespedBridge");
			oCon.setAutoCommit(false);
			oStm = oCon.prepareStatement("UPDATE Documento SET fkIdDocPadre=? WHERE IdDoc=?");
			Dms oDms = oSes.getDms();
			Document oDepo = oDms.getDocument(String.valueOf(lDepositId));
			Document oDdoc = oDepo.children().get(0);
			Document oBble = oDms.getDocument(String.valueOf(lBillableId));	

			Log.out.debug("getting sides for document "+oDdoc.id());
						
			for (Document oSide : oDdoc.children()) {
				oStm.setLong(1, Long.parseLong(oBble.id()));
				oStm.setLong(2, Long.parseLong(oSide.id()));
				Log.out.debug("UPDATE documento SET fkIdDocPadre="+oBble.id()+" WHERE IdDoc="+oSide.id());
				oStm.executeUpdate();
				aSides.add(oSide);
				ThumbnailCreator.createThumbnailFor(oBble.id(), oSide.id());
			}
			oStm.close();
			oStm=null;
			oCon.commit();
			oCon.close();
			oCon=null;

		} catch (SQLException sqle) {
			Log.out.debug("SQLException "+sqle.getMessage());
			throw new DmsException(sqle.getMessage(), sqle);
		} finally {
			try {
				if (oCon!=null) {
					if (!oCon.isClosed()) {
						if (oStm!=null) oStm.close();
						oCon.rollback();
						oCon.close("DepositToZespedBridge");
					}
				}
			} catch (SQLException ignore) { Log.out.debug("SQLException "+ignore.getMessage()); }
		}
		
		Log.out.debug("End DepositToZespedBridge.addSidesTicket() : "+String.valueOf(aSides.size()));

	}	

	public ArrayList<Document> addDocumentsToInvoices(AtrilSession oSes)
			throws DmsException {
			
			Log.out.debug("Begin DepositToZespedBridge.addSidesToInvoices("+String.valueOf(lDepositId)+")");
			ArrayList<Document> aSides = new ArrayList<Document>();
			JDCConnection oCon = null;
			PreparedStatement oStm = null;
			try {
				oCon = DAO.getConnection("DepositToZespedBridge");
				oCon.setAutoCommit(false);
				oStm = oCon.prepareStatement("UPDATE Documento SET fkIdDocPadre=? WHERE IdDoc=?");
				Dms oDms = oSes.getDms();
				TaxPayer txpy = new TaxPayer(oDms, sTaxPayerId);
				Invoices invs = txpy.invoices(oSes);
				Document oDepo = oDms.getDocument(String.valueOf(lDepositId));
				for (Document oDdoc : oDepo.children()) {
					Log.out.debug("getting sides for document "+oDdoc.id());
					NodeList<Document> oChlds = oDdoc.children();
					Invoice[] aInvs = invs.create(oSes, sUid, sFlavor, sTaxPayerId, sBiller, sRecipient, oChlds.size());
					int i = 0;
					for (Document oSide : oChlds) {
						Invoice oInvc = aInvs[i++];
						oStm.setLong(1, Long.parseLong(oInvc.id()));
						oStm.setLong(2, Long.parseLong(oSide.id()));
						Log.out.debug("UPDATE documento SET fkIdDocPadre="+oInvc.id()+" WHERE IdDoc="+oSide.id());
						oStm.executeUpdate();
						aSides.add(oSide);
						ThumbnailCreator.createThumbnailFor(oInvc.id(), oSide.id());
					}
					oCon.commit();
				}
				oStm.close();
				oStm=null;
				oCon.close();
				oCon=null;
				
			} catch (SQLException sqle) {
				Log.out.debug("SQLException "+sqle.getMessage());
				throw new DmsException(sqle.getMessage(), sqle);
			} finally {
				try {
					if (oCon!=null) {
						if (!oCon.isClosed()) {
							if (oStm!=null) oStm.close();
							oCon.rollback();
							oCon.close("DepositToZespedBridge");
						}
					}
				} catch (SQLException ignore) { Log.out.debug("SQLException "+ignore.getMessage()); }
			}
			
			Log.out.debug("End DepositToZespedBridge.addSidesToInvoices() : "+String.valueOf(aSides.size()));

			return aSides;
		}	
	
}
