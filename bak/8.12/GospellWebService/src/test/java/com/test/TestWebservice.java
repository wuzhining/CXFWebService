package com.test;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class TestWebservice {
	public static void main(String[] args) throws Exception {
		JaxWsDynamicClientFactory factory=JaxWsDynamicClientFactory.newInstance();
		Client client=factory.createClient("http://127.0.0.1:8081/GospellWebService/services/MesWebService?wsdl");
		QName name=new QName("http://webservice.mes.com/","querySn");
		String xmlStr="ASSY02";
		Object[] objs;
		try {
			objs=client.invoke(name, xmlStr,"0AAAA0000005");
			System.out.println(objs[0].toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
