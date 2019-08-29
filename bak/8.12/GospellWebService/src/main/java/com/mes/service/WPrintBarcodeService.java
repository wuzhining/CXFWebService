package com.mes.service;

public interface WPrintBarcodeService {
	
	public String checkData(String worksta, String mac);

	public String updateResult(String worksta, String mac, int flag, String errorMsg);
}
