package com.mes.pojo;

import java.math.BigDecimal;

public class TempWPrintBarcode {
	private String cd;
	private BigDecimal stepNo;
	private String macCode;
	private String proModel;

	public String getMacCode() {
		return macCode;
	}

	public void setMacCode(String macCode) {
		this.macCode = macCode;
	}

	public String getProModel() {
		return proModel;
	}

	public void setProModel(String proModel) {
		this.proModel = proModel;
	}

	public String getCd() {
		return cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public BigDecimal getStepNo() {
		return stepNo;
	}

	public void setStepNo(BigDecimal stepNo) {
		this.stepNo = stepNo;
	}

}
