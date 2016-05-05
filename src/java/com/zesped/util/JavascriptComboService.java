package com.zesped.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zesped.Log;

public class JavascriptComboService extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static HashMap<String,StringBuffer> cachedFiles = new HashMap<String,StringBuffer>();
	private boolean cachingEnabled = true;

	@Override
	public void init() throws ServletException {
		if (	(System.getProperty("js.combo.service.caching.enabled") != null &&
				 System.getProperty("js.combo.service.caching.enabled").equalsIgnoreCase("FALSE"))
			||
				(System.getProperty("js.combo.service.caching.enabled") != null &&
				 System.getProperty("js.combo.service.caching.enabled").equalsIgnoreCase("NO"))
			|| 
				(this.getServletConfig().getInitParameter("js.combo.service.caching.enabled") != null &&
				 this.getServletConfig().getInitParameter("js.combo.service.caching.enabled").equalsIgnoreCase("FALSE"))
			|| 
				(this.getServletConfig().getInitParameter("js.combo.service.caching.enabled") != null &&
				 this.getServletConfig().getInitParameter("js.combo.service.caching.enabled").equalsIgnoreCase("NO"))
			) {
				Log.out.info("file caching disabled");
				this.cachingEnabled = false;
		} else {
			Log.out.info("file caching enabled");
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String queryString = request.getQueryString();
		
		if (queryString == null || queryString.length() == 0) {
			Log.out.warn("request made without query string");
			return;
		}
		
		String[] fileRefs;
		if (queryString.contains("&")) {
			fileRefs = queryString.split("&");
		} else {
			fileRefs = new String[1];
			fileRefs[0] = queryString;
		}
		PrintWriter writer = response.getWriter();

		if (fileRefs.length > 0) {
			if (fileRefs[0].endsWith(".css")) {
				response.setContentType("text/css");
			} else if (fileRefs[0].endsWith(".js")) {
				response.setContentType("text/javascript");
			} else {
				Log.out.warn("unable to determine content type within request " + fileRefs[0]);
				return;
			}
		}
		
		int fileCount = 0;
		
		for ( String fileRef : fileRefs ) {
			fileCount++;
			if (fileRef.endsWith(".css") || fileRef.endsWith(".js")) {
				if (cachedFiles.containsKey(this.getServletContext().getRealPath(fileRef))) {
					writer.print(cachedFiles.get(this.getServletContext().getRealPath(fileRef)).toString());
				} else {					
					StringBuffer fileData = new StringBuffer();
					FileReader fileReader = null;
					try {
						fileReader = new FileReader(this.getServletContext().getRealPath(fileRef));
					} catch (FileNotFoundException fnfe) {
						Log.out.warn("file " + this.getServletContext().getRealPath(fileRef) + " was not found");
						continue;
					}
			        BufferedReader reader = new BufferedReader(fileReader);
			        char[] buf = new char[1024];
			        int numRead=0;
			        while((numRead=reader.read(buf)) != -1){
			            String readData = String.valueOf(buf, 0, numRead);
			            fileData.append(readData);
			            buf = new char[1024];
			        }
			        reader.close();

			        writer.print(fileData.toString());
					
					if (this.cachingEnabled){
						cachedFiles.put(this.getServletContext().getRealPath(fileRef), fileData);
					}
				}
			}
		}
		
		if (Log.out.isDebugEnabled()) {
			if (fileRefs[0].endsWith(".css")) {
				Log.out.debug("CSS response complete.  " + fileCount + " files combined.");
			} else if (fileRefs[0].endsWith(".js")) {
				Log.out.debug("JS response complete.  " + fileCount + " files combined.");
			}
		}
		
	}
	
}
