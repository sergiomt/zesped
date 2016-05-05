package com.zesped.action;

import java.util.ArrayList;
import java.util.Date;

import com.knowgate.misc.Calendar;
import com.knowgate.misc.NameValuePair;

public abstract class BaseDatableBean extends BaseActionBean {

	private ArrayList<NameValuePair> days = new ArrayList<NameValuePair>();
	private ArrayList<NameValuePair> months = new ArrayList<NameValuePair>();
	private ArrayList<NameValuePair> years = new ArrayList<NameValuePair>();
	private ArrayList<NameValuePair> yearslater = new ArrayList<NameValuePair>(); // SICUBO 201212 Cinco años siguientes

	public BaseDatableBean() {
		final int thisyear = new Date().getYear()+1900;
		for (int d=1; d<=31; d++)
			days.add(new NameValuePair(String.valueOf(d),  String.valueOf(d)));			
		for (int y=5; y>=0; y--)
			years.add(new NameValuePair(String.valueOf(thisyear-y), String.valueOf(thisyear-y)));			
		years.add(new NameValuePair(String.valueOf(thisyear+1), String.valueOf(thisyear+1)));
		for (int m=1; m<=12; m++)
			months.add(new NameValuePair(Calendar.MonthName(m-1, "es").substring(0, 3), String.valueOf(m)));
		for (int yt=0;yt<5;yt++) //SICUBO 201212 Cinco años siguientes
			yearslater.add(new NameValuePair(String.valueOf(thisyear+yt), String.valueOf(thisyear+yt)));
	}	

	public ArrayList<NameValuePair> getDays() {
		return days;
	}

	public ArrayList<NameValuePair> getMonths() {
		return months;
	}

	public ArrayList<NameValuePair> getYears() {
		return years;
	}

	//SICUBO 201212 {
	/**
	 *	Obtiene una lista de los 5 proximos años.
	 * @return Lista con los 5 proximos años.
	 */
	public ArrayList<NameValuePair> getYearslater() {
		return yearslater;
	}
	//SICUBO 201212 }	
}
