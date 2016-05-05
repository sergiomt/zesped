package com.zesped.model;

import java.util.ArrayList;
import java.util.Collection;

public class QueryResultSet<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	private boolean bEOF = true;
	private boolean bBOF = true;
	
	public QueryResultSet() { }

	public QueryResultSet(Collection<T> c) {
		super(c);
	}

	public QueryResultSet(int i) {
		super(i);
	}
	
	public boolean eof() {
		return bEOF;
	}

	public void eof(boolean e) {
		bEOF = e;
	}

	public boolean bof() {
		return bBOF;
	}

	public void bof(boolean b) {
		bBOF = b;
	}
	
}

