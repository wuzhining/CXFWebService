package com.mes.webservice;

import javax.jws.WebService;

@WebService
public interface MesWebService {

	public String querySn(String worksta, String mac);

	public String sendResult(String worksta, String mac, int flag, String errorMsg, String reserve1, String reserve2,
			String reserve3, String reserve4, String reserve5, String reserve6, String reserve7, String reserve8,
			String reserve9, String reserve10, String reserve11, String reserve12, String reserve13, String reserve14,
			String reserve15, String reserve16, String reserve17, String reserve18, String reserve19, String reserve20);

}
