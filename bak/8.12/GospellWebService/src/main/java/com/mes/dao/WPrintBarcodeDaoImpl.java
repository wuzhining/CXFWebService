package com.mes.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;

@Repository
public class WPrintBarcodeDaoImpl implements WPrintBarcodeDao {
	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Override
	public List<WPrintBarcode> selectBarcodeByMac(String mac) {
		String sql = "SELECT cd,mac FROM w_print_barcode WHERE mac=?";
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
		List<WPrintBarcode> barcodeList = jdbcTemplate.query(sql, rowMapper, mac,worksta);
		return barcodeList;
	}

}
