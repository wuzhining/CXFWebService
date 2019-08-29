package com.mes.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mes.pojo.BarcodeErrorMsg;
import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;

@Repository
public class WPrintBarcodeDaoImpl implements WPrintBarcodeDao {
	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Override
	public List<WPrintBarcode> selectBarcodeByMac(String mac) {
		String sql = "SELECT cd,mac,pcbsn FROM w_print_barcode WHERE mac=?";
		RowMapper<WPrintBarcode> rowMapper = new BeanPropertyRowMapper<WPrintBarcode>(WPrintBarcode.class);
		List<WPrintBarcode> barcodeList = jdbcTemplate.query(sql, rowMapper, mac);
		return barcodeList;
	}

	@Override
	public List<TempWPrintBarcode> selectBarcodeStep(String groupCode) {
		String sql = "SELECT t1.group_next as cd,t1.step_no as step_no FROM iplant1.c_route_control_t t1 WHERE t1.group_code = ?";
		RowMapper<TempWPrintBarcode> rowMapper = new BeanPropertyRowMapper<TempWPrintBarcode>(TempWPrintBarcode.class);
		List<TempWPrintBarcode> barcodeList = jdbcTemplate.query(sql, rowMapper, groupCode);
		return barcodeList;
	}

	@Override
	public List<TempWPrintBarcode> selectBarcodeNextStep(String mac, String groupCode) {
		String sql = "SELECT t1.group_next as cd,t1.step_no as step_no FROM iplant1.c_route_control_t t1  WHERE t1.route_code=("
				+ "SELECT route_code FROM mes1.r_mes_mo_t WHERE mo_cd =("
				+ "SELECT mo_no FROM mes1.r_mes_mo_mac_t WHERE mac_code = ?)) AND t1.group_code = ?";
		RowMapper<TempWPrintBarcode> rowMapper = new BeanPropertyRowMapper<TempWPrintBarcode>(TempWPrintBarcode.class);
		List<TempWPrintBarcode> barcodeList = jdbcTemplate.query(sql, rowMapper, mac, groupCode);
		return barcodeList;
	}

	@Override
	public List<WPrintBarcode> selectBarcodeBySql(String sql, String mac, String worksta) {
		RowMapper<WPrintBarcode> rowMapper = new BeanPropertyRowMapper<WPrintBarcode>(WPrintBarcode.class);
		List<WPrintBarcode> barcodeList = jdbcTemplate.query(sql, rowMapper, mac, worksta);
		return barcodeList;
	}

	@Override
	public void updateBarcodeStatus(String sql, int flag, String worksta, String mac, String originWorksta) {
		try {
			if (flag == 1) {
				jdbcTemplate.update(sql, flag, worksta, mac, originWorksta);
			} else {
				jdbcTemplate.update(sql, flag, worksta, mac);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveBadInput(String pcbsn, String pnsn, String badType, String badNm) {
		String sql = "INSERT INTO mes1.r_mes_asse_bad_input_t (pcb_sn,pon_sn,bad_type,bad_nm,seq_id,crt_dt) "
				+ "VALUES(?,?,?,?,bad_seq.nextval,sysdate)";
		try {
			jdbcTemplate.update(sql, pcbsn, pnsn, badType, badNm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<BarcodeErrorMsg> selectBarcodeErrorMsg(String pcbsn, String mac, String errorMsg, String worksta) {
		String sql = "SELECT seq_id,pcb_sn,mac,error_msg,crt_dt,rout_cd FROM r_barcode_error_msg_t WHERE pcb_sn=? AND mac=? AND error_msg=?"
				+ "AND rout_cd=?";
		RowMapper<BarcodeErrorMsg> rowMapper = new BeanPropertyRowMapper<BarcodeErrorMsg>(BarcodeErrorMsg.class);
		List<BarcodeErrorMsg> barcodeList = jdbcTemplate.query(sql, rowMapper, pcbsn, mac, errorMsg, worksta);
		return barcodeList;
	}

	@Override
	public void saveBarcodeErrorMsg(String pcbsn, String mac, String errorMsg, String worksta) {
		String sql = "INSERT INTO mes1.r_barcode_error_msg_t(seq_id,pcb_sn,mac,error_msg,crt_dt,rout_cd)VALUES(error_msg_seq.nextval,?,?,?,sysdate,?)";
		try {
			jdbcTemplate.update(sql, pcbsn, mac, errorMsg, worksta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateOtherMessage(String sql, int flag, String worksta, String mac, String originWorsta) {
		try {
			jdbcTemplate.update(sql, flag, worksta, mac, originWorsta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
