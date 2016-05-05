package com.zesped.model;

public enum CaptureService {

	NONE (0),
	INVOICES (1),
	BILLNOTES (2);
	
	private final int iInternalId;

	CaptureService (int idService) {
	  iInternalId = idService;
	}
	
	public int toInt() {
		return iInternalId;
	}
}
