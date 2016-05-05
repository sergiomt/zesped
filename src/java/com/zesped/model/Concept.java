package com.zesped.model;

import java.util.Date;

import com.knowgate.misc.Gadgets;

public class Concept {

	public Concept (String s, Date d) {
		setName(s);
		setCreationDate(d);
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name =  Gadgets.removeChars(n, "\n\"\\/?*:|<>;").replace('\t', ' ');
	}

	private Date creationdate;

	public Date getCreationDate() {
		return creationdate;
	}

	public void setCreationDate(Date d) {
		creationdate = d;
	}
	
}