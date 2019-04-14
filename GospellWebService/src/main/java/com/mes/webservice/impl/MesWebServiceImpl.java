package com.mes.webservice.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mes.service.WPrintBarcodeService;
import com.mes.webservice.MesWebService;
@Component
public class MesWebServiceImpl implements MesWebService {
	@Resource
	private WPrintBarcodeService service;
	
	public String querySn(String worksta, String mac) {
		return service.querySn(worksta,mac);
	}

	public String sendResult(String worksta, String mac, int flag) {
		return service.sendResult(worksta,mac, flag);
	}

}
