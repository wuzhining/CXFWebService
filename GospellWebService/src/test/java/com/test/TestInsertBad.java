package com.test;

import org.junit.Test;

import com.mes.service.WPrintBarcodeService;

public class TestInsertBad {
//	@Test
	public void test() {
		WPrintBarcodeService wPrintBarcodeService=new WPrintBarcodeService();
		wPrintBarcodeService.updateResult("ASSY02", "BBBBBBBBC284", 0);
	}
}
