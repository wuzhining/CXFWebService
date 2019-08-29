package com.mes.pojo;

import java.io.Serializable;

public class WPrintBarcode implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;

	private String cd;

	private String flag;

	private String mac;

	private String pcbsn;

	private String pnsn;

	private String bobResult;

	private String checkResult;

	public WPrintBarcode() {
	}
	
	public String getBobResult() {
		return bobResult;
	}

	public void setBobResult(String bobResult) {
		this.bobResult = bobResult;
	}

	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCd() {
		return this.cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getMac() {
		return this.mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPcbsn() {
		return this.pcbsn;
	}

	public void setPcbsn(String pcbsn) {
		this.pcbsn = pcbsn;
	}

	public String getPnsn() {
		return this.pnsn;
	}

	public void setPnsn(String pnsn) {
		this.pnsn = pnsn;
	}

}