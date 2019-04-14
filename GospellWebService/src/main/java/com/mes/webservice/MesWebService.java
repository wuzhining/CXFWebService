package com.mes.webservice;

import javax.jws.WebService;

@WebService
public interface MesWebService {

	public String querySn(String worksta, String mac);

//	public String queryBob(String worksta, String mac);
//
//	public String queryCheck(String worksta, String mac);
//
//	public String queryWander(String worksta, String mac);
	
	public String sendResult(String worksta, String mac, int flag);
//
//	public String sendBobResult(String worksta, String mac, int bobResult);
//
//	public String sendCheckResult(String worksta, String mac, int checkResult);
//	
//	public String sendWanderResult(String worksta, String mac, int wanderResult);
	
}
