package com.zesped.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.knowgate.dfs.StreamPipe;
import com.zesped.DAO;
import com.zesped.Log;

import es.ipsa.atril.doc.user.AttributeMultiValue;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.doc.user.Item;
import es.ipsa.atril.eventLogger.AtrilEvent;
import es.ipsa.atril.sec.authentication.AtrilSession;


public class ThumbnailCreator extends Thread {

	private String sParent, sItem;
	
    protected ThumbnailCreator(String sParentId, String sItemId) {
    	sParent = sParentId;
    	sItem = sItemId;
    }

    public void run() {
    	final float MaxWidth = 512f;
    	final float MaxHeight = 640f;
    	AtrilSession oSes = null;
    	StreamPipe oPipe;
    	try {
    		oSes = DAO.getAdminSession("ThumbnailCreator");
    		oSes.autoCommit(true);
    		Dms oDms = oSes.getDms();
    		Document oDoc = oDms.getDocument(sParent);
    		Item oItm = oDms.getDocument(sItem).item();
    		String sFileName = oItm.name().toLowerCase();
    		oPipe = new StreamPipe();
    		ByteArrayOutputStream oByOut = new ByteArrayOutputStream();
    		if (sFileName.endsWith(".pdf")) {
    		  Log.out.debug("Item.getInputStreamTranscodedToMime(image/jpeg)");
    		  oPipe.between(oItm.getInputStreamTranscodedToMime("image/jpeg"), oByOut);
    		} else {
      		  Log.out.debug("Item.getInputStream()");
      		  oPipe.between(oItm.getInputStream(), oByOut);    			
    		}
    		byte[] aBytes = oByOut.toByteArray();
    		oByOut.close();
    		oByOut = null;
    		Log.out.debug("new Picture()");
    		Picture oPic = new Picture();
    		Log.out.debug("Before Picture.dimensions()");
    		int[] aWidthHeight = oPic.dimensions(aBytes, "jpeg");
    		Log.out.debug("After Picture.dimensions()");
    		if (null==aWidthHeight)
    			throw new NullPointerException("Unable to get dimensions for image "+oItm.name());
    		Log.out.debug("Image width="+String.valueOf(aWidthHeight[0])+" height="+String.valueOf(aWidthHeight[1]));
    		int iWidth, iHeight;
    		if (aWidthHeight[0]<=MaxWidth && aWidthHeight[0]<=MaxHeight) {
    			iWidth = aWidthHeight[0];
    			iHeight = aWidthHeight[1];
    		} else {
    			float fWidthRatio = ((float) aWidthHeight[0]) / MaxWidth;
    			float fHeightRatio = ((float) aWidthHeight[1]) / MaxHeight;
    			if (fWidthRatio>fHeightRatio) {
    				iWidth = (int) MaxWidth;
    				iHeight = (int) (MaxWidth*aWidthHeight[1])/aWidthHeight[0];
    			} else {
    				iWidth = (int) (aWidthHeight[0]*MaxHeight)/aWidthHeight[1];
    				iHeight = (int) MaxHeight;
    			}
    		}
    		Log.out.debug("Resampled width="+String.valueOf(iWidth)+" height="+String.valueOf(iHeight));
    		String sCodec = null;
    		if (sFileName.endsWith(".jpg") || sFileName.endsWith(".jpeg") || sFileName.endsWith(".pdf")) 
    			sCodec = "jpeg";
    		else if (sFileName.endsWith(".gif"))
    			sCodec = "gif";
    		else if (sFileName.endsWith(".tif") || sFileName.endsWith(".tiff"))
    			sCodec = "tiff";
    		else {
    			Log.out.error("ThumbnailCreator.run()  Could not find suitable codec for file "+oItm.name());
    			throw new InstantiationException("Could not find suitable codec for file "+oItm.name());
    		}
    		Log.out.debug("Picture.createThumbBitmap("+sCodec+","+String.valueOf(iWidth)+","+String.valueOf(iHeight)+",80)");
    		byte[] byThumb = oPic.createThumbBitmap(aBytes, sCodec, iWidth, iHeight, 80);
    		
    		Document oThl = oDms.newDocument(oDms.getDocumentType(oDoc.type().name()+"Thumbnail"), oDoc);
    		AttributeMultiValue oAtr = oThl.attribute("width");
			oAtr.set((long) iWidth);
			oAtr = oThl.attribute("height");
			oAtr.set((long) iHeight);
			oThl.save("");
    		Item oThi = oThl.item();
    		String sItemName = "th"+oDoc.id()+".jpg";
    		oThi.setName(sItemName);
    		oThi.mimeType("image/jpeg");
    		OutputStream oOutStrm = oThi.getOutputStream();
    		oPipe = new StreamPipe();
    		oPipe.between(new ByteArrayInputStream(byThumb), oOutStrm);
    		oThl.save("");
    		oOutStrm.close();
    		Log.out.debug("Thumbnail creation done");
    		DAO.log(oSes, oThl, Class.forName("com.zesped.model."+oDoc.type().name()+"Thumbnail"), "INSERT THUMBNAIL", AtrilEvent.Level.INFO, sItemName);
    		oSes.disconnect();
    		oSes.close();
    		oSes=null;
    	} catch (Exception e) {
    		Log.out.error("ThumbnailCreator.run() "+e.getClass().getName()+" "+e.getMessage(), e);
    	} finally {
    		if (oSes!=null) {
    			if (oSes.isConnected()) oSes.disconnect();
    			if (oSes.isOpen()) oSes.close();
    		}
    	}
		Log.out.debug("End ThumbnailCreator.run()");
    }
    
    public static void createThumbnailFor(String sParentId, String sItemId) {
    	ThumbnailCreator oThc = new ThumbnailCreator(sParentId, sItemId);
    	oThc.run();
    }
} 