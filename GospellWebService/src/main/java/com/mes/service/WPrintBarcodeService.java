package com.mes.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;
import com.mes.utils.DBHelper;

@Service
public class WPrintBarcodeService {

	/**
	 * 查询sn码
	 * 
	 * @return
	 */
	public String querySn(String worksta, String mac) {
		return this.checkData(worksta, mac);
	}

	/**
	 * 验证mac的过站信息
	 */
	@SuppressWarnings("unchecked")
	public String checkData(String worksta, String mac) {
		List<WPrintBarcode> barcodeList;
		List<TempWPrintBarcode> stepList;
		List<Object> list = new ArrayList<>();
		try {
			list.add(mac);
			list.add(worksta);
			DBHelper db = new DBHelper();
			String sql = "";
			sql = "select t1.group_next as cd,t1.STEP_NO as step_no from IPLANT1.C_ROUTE_CONTROL_T T1  where t1.route_code=("
					+ "select route_code from mes1.r_mes_mo_t where mo_cd =("
					+ "select mo_no from MES1.R_MES_MO_MAC_T where MAC_CODE = ?)) and t1.group_code = ?";
			stepList = (List<TempWPrintBarcode>) db.executeQuery(sql, list, new TempWPrintBarcode().getClass());

			if (stepList.size() == 0) {
				// 查找不到工位下一站的信息，
				System.out.println( "查找不到工位:" + worksta + "下一站的信息！");
				return "NODATA";
			}

			// String nextWorkStation = stepList.get(0).getCd();
			String step = stepList.get(0).getStepNo().toString();
			switch (step) {
			case "1":// 烧录
				sql = "select pcbsn,pnsn,mac from w_print_barcode where (flag is null or flag='0')and mac=? and cd=?";
				break;
			case "2":// bob测试
				sql = "select pcbsn,pnsn,mac from w_print_barcode where (bob_result is null or bob_result='0') and "
						+ "flag='1' and mac=? and cd=?";
				break;
			case "3":// 打流测试
				sql = "select pcbsn,pnsn,mac from w_print_barcode where (wander_result is null or wander_result='0')"
						+ "and bob_result='1' and mac=? and cd=?";
				break;
			case "4":// 查号测试
				sql = "select pcbsn,pnsn,mac from w_print_barcode where (check_result is null or check_result='0')"
						+ "and wander_result='1' and mac=? and cd=?";
				break;
			}
			// sql = "select pcbsn,pnsn,mac from w_print_barcode where (flag is
			// null or flag='0')and mac=? and cd=?";
			barcodeList = (List<WPrintBarcode>) db.executeQuery(sql, list, new WPrintBarcode().getClass());

			if (barcodeList.size() == 0) {
				return "NODATA";
			}

			if (step.equals("1")) {
				JSONObject jobj = new JSONObject();
				jobj.put("pcbsn", barcodeList.get(0).getPcbsn());
				jobj.put("pnsn", barcodeList.get(0).getPnsn());
				jobj.put("mac", barcodeList.get(0).getMac());

				return jobj.toString();
			} else {
				return "SUCCESS";
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("访问数据库时发生错误！");
			return "FAULT";
		}
	}

	/**
	 * 产品测试结果
	 * 
	 * @param mac
	 * @param flag
	 */
	public String sendResult(String worksta, String mac, int flag) {
		return this.updateResult(worksta, mac, flag);
	}

	/**
	 * 更新测试结果
	 * 
	 * @param worksta
	 * @param mac
	 * @param flag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String updateResult(String worksta, String mac, int flag) {
		List<Object> list = new ArrayList<Object>();

		try {

			DBHelper db = new DBHelper();
			String sql;
			List<WPrintBarcode> cdList;
			List<TempWPrintBarcode> stepList;

			// 查找工位信息
			list.add(mac);
			list.add(worksta);

			sql = "select t1.group_next as cd,t1.STEP_NO as step_no from IPLANT1.C_ROUTE_CONTROL_T T1  where t1.route_code=("
					+ "select route_code from mes1.r_mes_mo_t where mo_cd =("
					+ "select mo_no from MES1.R_MES_MO_MAC_T where MAC_CODE = ?)) and t1.group_code = ?";
			stepList = (List<TempWPrintBarcode>) db.executeQuery(sql, list, new TempWPrintBarcode().getClass());

			if (stepList.size() == 0) {
				// 查找不到工位下一站的信息，
				System.out.println("查找不到工位:" + worksta + "下一站的信息！");
				return "NODATA";
			}

			String nextWorkStation = stepList.get(0).getCd();

			String method = stepList.get(0).getStepNo().toString();
			sql = "select mac,cd,flag from w_print_barcode where mac=? and cd=?";
			cdList = (List<WPrintBarcode>) db.executeQuery(sql, list, new WPrintBarcode().getClass());
			if (cdList.size() == 0) {
				System.out.println("工位：" + worksta + "中查找不到mac:" + mac + "的数据信息！");
				return "NODATA";
			}

			if (flag == 1) {// 成功
				// 更新状态，添加过站信息
				list.clear();
				list.add(flag);
				list.add(nextWorkStation);
				list.add(mac);
				list.add(worksta);

				switch (method) {
				case "1":// 烧录
					sql = "update w_print_barcode set flag=?,cd=? where mac=? and cd=?";
					break;
				case "2":// bob测试
					sql = "update w_print_barcode set bob_result=?,cd=? where mac=? and cd=?";
					break;
				case "3":// 打流测试
					sql = "update w_print_barcode set wander_result=?,cd=? where mac=? and cd=?";
					break;
				case "4":// 查号测试
					sql = "update w_print_barcode set check_result=?,cd=? where mac=? and cd=?";
					break;
				}
				db.excuteUpdate(sql, list);
			} else {// 失败

				list.clear();
				list.add(flag);
				list.add(mac);
				list.add(worksta);

				switch (method) {
				case "1":// 烧录
					sql = "update w_print_barcode set flag=? where mac=? and cd=? and flag is null";
					break;
				case "2":// bob测试
					sql = "update w_print_barcode set bob_result=? where mac=? and cd=? and bob_result is null";
					break;
				case "3":// 打流测试
					sql = "update w_print_barcode set wander_result=? where mac=? and cd=? and wander_result is null";
					break;
				case "4":// 查号测试
					sql = "update w_print_barcode set check_result=? where mac=? and cd=? and check_result is null";
					break;
				}
				db.excuteUpdate(sql, list);
			}
			// 更新其他信息
			list.clear();
			list.add(flag);
			// 测试成功更新为下一站的编号，测试失败则编号不变
			list.add(flag == 1 ? nextWorkStation : worksta);
			list.add(mac);
			list.add(worksta);

			switch (method) {
			case "1":// 烧录
				sql = "UPDATE MES1.R_PON_PCB_MAC_EN_T SET BURN_RESULTS =?,ROUT_CD=?,UPT_DT=sysdate "
						+ "WHERE MAC=? and rout_cd=?";
				break;
			case "2":// bob测试
				sql = "UPDATE MES1.R_PON_PCB_MAC_EN_T SET BOB_RESULTS =?,ROUT_CD=?,UPT_DT=sysdate "
						+ "WHERE MAC=? and rout_cd=?";
				break;
			case "3":// 打流测试
				sql = "UPDATE MES1.R_PON_PCB_MAC_EN_T SET WANDER_RESULTS =?,ROUT_CD=?,UPT_DT=sysdate "
						+ "WHERE MAC=? and rout_cd=?";
				break;
			case "4":// 查号测试
				sql = "UPDATE MES1.R_PON_PCB_MAC_EN_T SET CHECK_RESULTS =?,ROUT_CD=?,UPT_DT=sysdate "
						+ "WHERE MAC=? and rout_cd=?";
				break;
			}
			db.excuteUpdate(sql, list);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("更新数据时发生错误！");
			return "FAULT";
		}
		return "SUCCESS";
	}
}