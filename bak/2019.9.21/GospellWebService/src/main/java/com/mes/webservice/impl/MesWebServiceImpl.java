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

	@Override
	public String sendResult(String worksta, String mac, int flag, String errorMsg, String reserve1, String reserve2,
			String reserve3, String reserve4, String reserve5, String reserve6, String reserve7, String reserve8,
			String reserve9, String reserve10, String reserve11, String reserve12, String reserve13, String reserve14,
			String reserve15, String reserve16, String reserve17, String reserve18, String reserve19,
			String reserve20) {

		return wPrintBarcodeService.updateResult(worksta, mac, flag, errorMsg, reserve1, reserve2, reserve3, reserve4,
				reserve5, reserve6, reserve7, reserve8, reserve9, reserve10, reserve11, reserve12, reserve13, reserve14,
				reserve15, reserve16, reserve17, reserve18, reserve19, reserve20);
	}

}
