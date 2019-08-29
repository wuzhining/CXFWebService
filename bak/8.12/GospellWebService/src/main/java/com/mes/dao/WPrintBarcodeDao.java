package com.mes.dao;

import java.util.List;

import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;

public interface WPrintBarcodeDao {
	public List<WPrintBarcode> selectBarcodeByMac(String mac);

	public List<TempWPrintBarcode> selectBarcodeStep(String groupCode);

	public List<TempWPrintBarcode> selectBarcodeNextStep(String mac, String groupCode);
	
	public List<WPrintBarcode> selectBarcodeBySql(String sql,String mac,String worksta);

}
