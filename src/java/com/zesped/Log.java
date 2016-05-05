package com.zesped;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

public class Log {
	  public static Logger out = Logger.getLogger(DAO.class);

	  public static String stackTrace( Throwable aThrowable ) {
		  String sRetVal = "";
		  try {
			  final Writer result = new StringWriter();
			  final PrintWriter printWriter = new PrintWriter( result );
			  aThrowable.printStackTrace( printWriter );
			  sRetVal = result.toString();
			  printWriter.close();
			  result.close();
		  } catch (IOException e) { }
		  return sRetVal;
	  }	  
}
