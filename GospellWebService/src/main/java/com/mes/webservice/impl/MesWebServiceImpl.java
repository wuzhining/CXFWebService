package com.mes.webservice.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mes.service.WPrintBarcodeService;
import com.mes.service.WPrintBarcodeServiceImpl;
import com.mes.webservice.MesWebService;

@Component
public class MesWebServiceImpl implements MesWebService {
	@Resource
	private WPrintBarcodeService wPrintBarcodeService;

	public String querySn(String worksta, String mac) {
		return wPrintBarcodeService.checkData(worksta, mac);
	}

	public String sendResult(String worksta, String mac, int flag, String errorMsg) {
		return wPrintBarcodeService.updateResult(worksta, mac, flag, errorMsg);
	}

}
