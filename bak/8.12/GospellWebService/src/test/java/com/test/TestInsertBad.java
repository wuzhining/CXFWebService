package com.test;

import java.util.ArrayList;
import java.util.List;

//import org.junit.Test;

import com.mes.pojo.BarcodeErrorMsg;
import com.mes.service.WPrintBarcodeServiceImpl;
import com.mes.utils.DBHelper;

public class TestInsertBad {
//	@Test
	public void test() throws Exception {
		WPrintBarcodeServiceImpl wPrintBarcodeService=new WPrintBarcodeServiceImpl();
//		wPrintBarcodeService.updateResult("ASSY02", "BBBBBBBBC284", 0);
		DBHelper db=new DBHelper();
		
		List<Object> list=new ArrayList<>();
		
		List<BarcodeErrorMsg> errorMsgList=new ArrayList<>();
		
		list.add("BBBBBBBBC284");
		list.add("BBBBBBBBC999");
		list.add("justTest");
		list.add("ASSY02");
		String sql="INSERT INTO MES1.r_barcode_error_msg_t(seq_id,pcb_sn,mac,error_msg,crt_dt,rout_cd)values(ERROR_MSG_SEQ.NEXTVAL,?,?,?,sysdate,?)";
//		db.excuteUpdate(sql, list);
		
		list.clear();
		sql="select * from R_BARCODE_ERROR_MSG_T";
		errorMsgList=(List<BarcodeErrorMsg>) db.executeQuery(sql, list, BarcodeErrorMsg.class);
		System.out.println(errorMsgList.size());
		
		list.clear();
		list.add("BBBBBBBBC284");
		list.add("BBBBBBBBC999");
		list.add("justTest");
		list.add("ASSY02");
		
		sql="SELECT seq_id,pcb_sn,mac,error_msg,crt_dt,rout_cd FROM r_barcode_error_msg_t WHERE pcb_sn=? AND mac=? AND error_msg=?"
				+ "AND rout_cd=?";
		errorMsgList=db.executeQuery(sql, list, BarcodeErrorMsg.class);
		
		if (errorMsgList.size()==0) {
			sql="INSERT INTO mes1.r_barcode_error_msg_t(seq_id,pcb_sn,mac,error_msg,crt_dt,rout_cd)VALUES(error_msg_seq.nextval,?,?,?,sysdate,?)";
			
			db.excuteUpdate(sql, list);
		}
		
	}
	
}
