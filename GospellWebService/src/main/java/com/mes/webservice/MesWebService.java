package com.mes.webservice;

import javax.jws.WebService;

@WebService
public interface MesWebService {

	public String querySn(String worksta, String mac);

	public String sendResult(String worksta, String mac, int flag, String errorMsg);

}
