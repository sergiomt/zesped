package com.zesped.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;

import com.zesped.Log;
import com.zesped.model.User;

public class SaveCaptureOptions extends BaseAjaxBean {

	@DefaultHandler
	public Resolution save() {
		final boolean bGUI = getParam("gui","0").equals("1");
		final boolean bSign = getParam("sign","0").equals("1");
		final boolean bServerSign = getParam("sign","0").equals("2");
		final boolean bFullDuplex = getParam("fullDuplex","0").equals("1");
		final boolean bMultiPage = getParam("multiPage","0").equals("1");		
		Log.out.debug("Begin SaveCaptureOptions.save(sign="+String.valueOf(bSign)+",fullduplex="+String.valueOf(bFullDuplex)+",multiPage="+String.valueOf(bMultiPage)+",gui="+String.valueOf(bGUI)+")");
		User oUsr = null;
		String sSignType = bSign ? "Signed" : bServerSign ? "ServerSigned" : "Unsigned";
		try {
			connect(getSessionAttribute("nickname"), getSessionAttribute("password"));
			oUsr = new User(getSession(), getSessionAttribute("user_docid"));
			if (bGUI)
				if (bFullDuplex)
					if (bMultiPage)
						oUsr.defaultCaptureOptions(sSignType+"MultiPageFullDuplexGUI", "Twain");
					else
						oUsr.defaultCaptureOptions(sSignType+"SinglePageFullDuplexGUI", "Twain");
				else
					if (bMultiPage)
						oUsr.defaultCaptureOptions(sSignType+"MultiPageHalfDuplexGUI", "Twain");
					else
						oUsr.defaultCaptureOptions(sSignType+"SinglePageHalfDuplexGUI", "Twain");
			else
				if (bFullDuplex)
					if (bMultiPage)
						oUsr.defaultCaptureOptions(sSignType+"MultiPageFullDuplexNoGUI", "Twain");
					else
						oUsr.defaultCaptureOptions(sSignType+"SinglePageFullDuplexNoGUI", "Twain");
				else
					if (bMultiPage)
						oUsr.defaultCaptureOptions(sSignType+"MultiPageHalfDuplexNoGUI", "Twain");
					else
						oUsr.defaultCaptureOptions(sSignType+"SinglePageHalfDuplexNoGUI", "Twain");
			oUsr.save(getSession());
			disconnect();
			Log.out.debug("default_capture_type="+oUsr.getString("default_capture_type"));
			Log.out.debug("default_scanner="+oUsr.getString("default_scanner"));
	    	addDataLine("id",oUsr.id());
	    	addDataLine("id",oUsr.getString("default_capture_type"));
	    	addDataLine("id",oUsr.getString("default_scanner"));
		} catch (Exception xcpt) {
			Log.out.error(xcpt.getMessage(), xcpt);
			addError(new SimpleError(xcpt.getMessage()));
		} finally {
			close();
		}
		if (oUsr!=null) {
			Log.out.debug("default_capture_type="+oUsr.getString("default_capture_type"));
			Log.out.debug("default_scanner="+oUsr.getString("default_scanner"));
		}
		Log.out.debug("End SaveCaptureOptions.save()");
	    return AjaxResponseResolution();
	}
	
}
