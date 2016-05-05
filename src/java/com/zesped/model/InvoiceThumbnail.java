package com.zesped.model;

import java.util.Collection;
import java.util.Locale;

import com.zesped.DAO;
import com.zesped.Log;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;
import es.ipsa.atril.doc.user.DataType;
import es.ipsa.atril.doc.user.Dms;
import es.ipsa.atril.doc.user.Document;
import es.ipsa.atril.exceptions.ElementNotFoundException;
import es.ipsa.atril.sec.authentication.AtrilSession;

public class InvoiceThumbnail extends BaseItemObject implements TypeConverter<InvoiceThumbnail>  {

	private static final long serialVersionUID = 1L;

	public InvoiceThumbnail() {
		super("InvoiceThumbnail");
	}

	public InvoiceThumbnail(Document d) {
		super(d);
	}

	public InvoiceThumbnail(Dms oDms, String sDocId) {
		super(oDms, sDocId);
	}
	
	private static final Attr[] aAttrs = new Attr[]{
    	new Attr("width",DataType.NUMBER,false,true,null),
    	new Attr("height",DataType.NUMBER,false,true,null)
    };	

	@Override
	public Attr[] attributes() {
		return aAttrs;
	}

	@Override
	public void setLocale(Locale arg0) {		
	}

	@Override
	public InvoiceThumbnail convert(String sId, Class<? extends InvoiceThumbnail> invThumbClass, Collection<ValidationError> conversionErrors) {
		Log.out.debug("InvoiceThumbnail convert("+sId+")");
		AtrilSession oSes = null;
		InvoiceThumbnail oThn = null;
		try {
			oSes = DAO.getAdminSession("InvoiceThumbnailTypeConverter");
			oThn = new InvoiceThumbnail(oSes.getDms(), sId);
		} catch (ElementNotFoundException enfe) {
			Log.out.error("Thumbnail "+sId+" not found "+enfe.getMessage(), enfe);
		} catch (Exception xcpt) {
			Log.out.error(xcpt.getClass().getName()+" "+xcpt.getMessage(), xcpt);
		} finally {
			if (oSes!=null) {
				if (oSes.isConnected()) oSes.disconnect();
				if (oSes.isOpen()) oSes.close();
			}
		}
		return oThn;
	}
	
}
