package com.mes.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BarcodeErrorMsg {
	private BigDecimal seqId;
	private String pcbSn;
	private String mac;
	private String errorMsg;
	private String routCd;
	private Timestamp crtDt;

	public BigDecimal getSeqId() {
		return seqId;
	}

	public void setSeqId(BigDecimal seqId) {
		this.seqId = seqId;
	}

	public String getPcbSn() {
		return pcbSn;
	}

	public void setPcbSn(String pcbSn) {
		this.pcbSn = pcbSn;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getRoutCd() {
		return routCd;
	}

	public void setRoutCd(String routCd) {
		this.routCd = routCd;
	}

	public Timestamp getCrtDt() {
		return crtDt;
	}

	public void setCrtDt(Timestamp crtDt) {
		this.crtDt = crtDt;
	}

}
