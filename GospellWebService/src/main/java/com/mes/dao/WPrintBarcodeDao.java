package com.mes.dao;

import java.util.List;

import com.mes.pojo.BarcodeErrorMsg;
import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;

public interface WPrintBarcodeDao {
	public List<WPrintBarcode> selectBarcodeByMac(String mac);

	public List<TempWPrintBarcode> selectBarcodeStep(String groupCode);

	public List<TempWPrintBarcode> selectBarcodeNextStep(String mac, String groupCode);

	public List<WPrintBarcode> selectBarcodeBySql(String sql, String mac, String worksta);

	public void updateBarcodeStatus(String sql, int flag, String worksta, String mac, String originWorksta);

	public void saveBadInput(String pcbsn, String pnsn, String badType, String badNm);

	public List<BarcodeErrorMsg> selectBarcodeErrorMsg(String pcbsn, String mac, String errorMsg, String worksta);

	public void saveBarcodeErrorMsg(String pcbsn, String mac, String errorMsg, String worksta);

	public void updateOtherMessage(String sql, int flag, String worksta, String mac, String originWorsta);
}
