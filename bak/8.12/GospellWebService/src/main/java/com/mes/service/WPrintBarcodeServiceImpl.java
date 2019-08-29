package com.mes.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mes.dao.WPrintBarcodeDao;
import com.mes.pojo.BarcodeErrorMsg;
import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;
import com.mes.utils.DBHelper;

@Service
public class WPrintBarcodeServiceImpl implements WPrintBarcodeService {
	
	@Autowired
	public WPrintBarcodeDao wPrintBarcodeDao;

	/**
	 * 验证mac的过站信息
	 */
	@Override
	public String checkData(String worksta, String mac) {
		List<WPrintBarcode> barcodeList;
		List<TempWPrintBarcode> stepList;
		List<Object> list = new ArrayList<>();

		try {
			list.add(mac);
			DBHelper db = new DBHelper();
			String originCd = "";

			String sql = "SELECT cd,mac FROM w_print_barcode WHERE mac=?";
			barcodeList = db.executeQuery(sql, list, WPrintBarcode.class);
			barcodeList=wPrintBarcodeDao.selectBarcodeByMac(mac);
			if (barcodeList.size() == 0) {
				System.out.println("mac地址：" + mac + " 不存在！");
				return "mac地址：" + mac + " 不存在！";
			}

			originCd = barcodeList.get(0).getCd();

			if (!worksta.equals(originCd)) {// 当上传的工站编号与数据库的工站编号不一致时返回当前工站信息
				sql = "SELECT t1.group_next as cd,t1.step_no as step_no FROM iplant1.c_route_control_t t1 WHERE t1.group_code = ?";
				list.clear();
				list.add(originCd);
				stepList = db.executeQuery(sql, list, TempWPrintBarcode.class);
				if (stepList.size() > 0) {
					String step = "";
					switch (stepList.get(0).getStepNo().toString()) {
					case "1":
						step = "写号";
						break;
					case "2":
						step = "bob测试";
						break;
					case "3":
						step = "打流测试";
						break;
					case "4":
						step = "查号测试";
						break;

					default:
						break;
					}
					System.out.println("mac地址：" + mac + " 目前处于" + step + "工位");
					return "mac地址：" + mac + " 目前处于" + step + "工位";
				}
			}

			// 查找工站下一站的信息
			list.clear();
			list.add(mac);
			list.add(worksta);

			sql = "SELECT t1.group_next as cd,t1.step_no as step_no FROM iplant1.c_route_control_t t1  WHERE t1.route_code=("
					+ "SELECT route_code FROM mes1.r_mes_mo_t WHERE mo_cd =("
					+ "SELECT mo_no FROM mes1.r_mes_mo_mac_t WHERE mac_code = ?)) AND t1.group_code = ?";
			stepList = db.executeQuery(sql, list, TempWPrintBarcode.class);

			if (stepList.size() == 0) {
				System.out.println("查找不到工位:" + worksta + "下一站的信息！");
				return "查找不到工位:" + worksta + "下一站的信息！";
			}

			String step = stepList.get(0).getStepNo().toString();
			String message = "";
			switch (step) {
			case "1":// 烧录、写号
				sql = "SELECT pcbsn,pnsn,mac FROM w_print_barcode WHERE (flag is null OR flag='0')AND mac=? AND cd=?";
				message = "mac绑定";
				break;
			case "2":// bob测试
				sql = "SELECT pcbsn,pnsn,mac FROM w_print_barcode WHERE (bob_result is null OR bob_result='0') AND "
						+ "flag='1' AND mac=? AND cd=?";
				message = "写号";
				break;
			case "3":// 打流测试
				sql = "SELECT pcbsn,pnsn,mac FROM w_print_barcode WHERE (wander_result is null OR wander_result='0')"
						+ "AND bob_result='1' AND mac=? AND cd=?";
				message = "bob测试";
				break;
			case "4":// 查号测试
				sql = "SELECT pcbsn,pnsn,mac FROM w_print_barcode WHERE (check_result is null OR check_result='0')"
						+ "AND wander_result='1' AND mac=? AND cd=?";
				message = "打流测试";
				break;
			}
			barcodeList = db.executeQuery(sql, list, WPrintBarcode.class);

			if (barcodeList.size() == 0) {
				return "mac：" + mac + "未通过" + message;
			}

			if (step.equals("3")) {
				return "SUCCESS";
			} else {
				JSONObject jobj = new JSONObject();
				jobj.put("pcbsn", barcodeList.get(0).getPcbsn());
				jobj.put("pnsn", barcodeList.get(0).getPnsn());
				jobj.put("mac", barcodeList.get(0).getMac());

				return jobj.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("访问数据库时发生错误！");
			return "FAULT";
		}
	}

	/**
	 * 更新测试结果
	 * 
	 * @param worksta
	 * @param mac
	 * @param flag
	 * @return
	 */
	@Override
	public String updateResult(String worksta, String mac, int flag, String errorMsg) {
		List<WPrintBarcode> barcodeList;
		List<TempWPrintBarcode> stepList;
		List<Object> list = new ArrayList<>();
		DBHelper db = new DBHelper();

		try {
			list.add(mac);
			String sql = "SELECT cd,mac,pcbsn,pnsn FROM w_print_barcode WHERE mac=?";
			barcodeList = db.executeQuery(sql, list, WPrintBarcode.class);

			if (barcodeList.size() == 0) {
				System.out.println("mac地址：" + mac + " 不存在！");
				return "mac地址：" + mac + " 不存在！";
			}

			String originCd = barcodeList.get(0).getCd();

			if (!worksta.equals(originCd)) { // 测试软件上传的工站与数据库记录的工站不一致
				sql = "SELECT t1.group_next as cd,t1.step_no as step_no FROM iplant1.c_route_control_t t1 WHERE t1.group_code = ?";
				list.clear();
				list.add(originCd);
				stepList = db.executeQuery(sql, list, TempWPrintBarcode.class);

				if (stepList.size() > 0) {
					String step = "";
					switch (stepList.get(0).getStepNo().toString()) {
					case "1":
						step = "写号";
						break;
					case "2":
						step = "bob测试";
						break;
					case "3":
						step = "打流测试";
						break;
					case "4":
						step = "查号测试";
						break;
					}
					System.out.println("mac地址：" + mac + " 目前处于" + step + "工位");
					return "mac地址：" + mac + " 目前处于" + step + "工位";
				}
			}

			// 查找工位信息
			list.clear();
			list.add(mac);
			list.add(worksta);

			sql = "SELECT t1.group_next as cd,t1.STEP_NO as step_no FROM IPLANT1.C_ROUTE_CONTROL_T T1  WHERE t1.route_code=("
					+ "SELECT route_code FROM mes1.r_mes_mo_t WHERE mo_cd =("
					+ "SELECT mo_no FROM mes1.r_mes_mo_mac_t WHERE mac_code = ?)) AND t1.group_code = ?";
			stepList = db.executeQuery(sql, list, TempWPrintBarcode.class);

			if (stepList.size() == 0) {
				// 查找不到工位下一站的信息，
				System.out.println("查找不到工位:" + worksta + "下一站的信息！");
				return "查找不到工位:" + worksta + "下一站的信息！";
			}

			String nextWorkStation = stepList.get(0).getCd();

			String stepCode = stepList.get(0).getStepNo().toString();

			if (flag == 1) {// 成功
				// 更新状态，添加过站信息
				list.clear();
				list.add(flag);
				list.add(nextWorkStation);
				list.add(mac);
				list.add(worksta);

				switch (stepCode) {
				case "1":// 烧录
					sql = "UPDATE w_print_barcode set flag=?,cd=? WHERE mac=? AND cd=?";
					break;
				case "2":// bob测试
					sql = "UPDATE w_print_barcode set bob_result=?,cd=? WHERE mac=? AND cd=?";
					break;
				case "3":// 打流测试
					sql = "UPDATE w_print_barcode set wander_result=?,cd=? WHERE mac=? AND cd=?";
					break;
				case "4":// 查号测试
					sql = "UPDATE w_print_barcode set check_result=?,cd=? WHERE mac=? AND cd=?";
					break;
				}
				db.excuteUpdate(sql, list);

			} else {// 失败

				list.clear();
				list.add(flag);
				list.add(mac);
				list.add(worksta);

				String message = "";

				switch (stepCode) {
				case "1":// 烧录
					sql = "UPDATE w_print_barcode set flag=? WHERE mac=? AND cd=? AND flag is null";
					message = "写号不良";
					break;
				case "2":// bob测试
					sql = "UPDATE w_print_barcode set bob_result=? WHERE mac=? AND cd=? AND bob_result is null";
					message = "bob测试不良";
					break;
				case "3":// 打流测试
					sql = "UPDATE w_print_barcode set wander_result=? WHERE mac=? AND cd=? AND wander_result is null";
					message = "打流测试不良";
					break;
				case "4":// 查号测试
					sql = "UPDATE w_print_barcode set check_result=? WHERE mac=? AND cd=? AND check_result is null";
					message = "查号不良";
					break;
				}
				db.excuteUpdate(sql, list);

				// 添加不良品信息
				list.clear();
				list.add(barcodeList.get(0).getPcbsn());
				list.add(barcodeList.get(0).getPnsn());
				list.add("组装段不良");
				list.add(message);

				sql = "INSERT INTO mes1.r_mes_asse_bad_input_t (pcb_sn,pon_sn,bad_type,bad_nm,seq_id,crt_dt) "
						+ "VALUES(?,?,?,?,bad_seq.nextval,sysdate)";
				db.excuteUpdate(sql, list);

				// 添加不良描述
				if (errorMsg != null) {
					list.clear();
					list.add(barcodeList.get(0).getPcbsn());
					list.add(mac);
					list.add(errorMsg);
					list.add(worksta);

					List<BarcodeErrorMsg> errorMsgList = new ArrayList<>();
					sql = "SELECT seq_id,pcb_sn,mac,error_msg,crt_dt,rout_cd FROM r_barcode_error_msg_t WHERE pcb_sn=? AND mac=? AND error_msg=?"
							+ "AND rout_cd=?";
					errorMsgList = db.executeQuery(sql, list, BarcodeErrorMsg.class);

					if (errorMsgList.size() == 0) {
						sql = "INSERT INTO mes1.r_barcode_error_msg_t(seq_id,pcb_sn,mac,error_msg,crt_dt,rout_cd)VALUES(error_msg_seq.nextval,?,?,?,sysdate,?)";

						db.excuteUpdate(sql, list);
					}
				}
			}

			// 更新其他信息
			list.clear();
			list.add(flag);
			// 测试成功更新为下一站的编号，测试失败则编号不变
			list.add(flag == 1 ? nextWorkStation : worksta);
			list.add(mac);
			list.add(worksta);

			switch (stepCode) {
			case "1":// 烧录
				sql = "UPDATE mes1.r_pon_pcb_mac_en_t SET burn_results =?,rout_cd=?,upt_dt=sysdate "
						+ "WHERE mac=? AND rout_cd=?";
				break;
			case "2":// bob测试
				sql = "UPDATE mes1.r_pon_pcb_mac_en_t SET bob_results =?,rout_cd=?,upt_dt=sysdate "
						+ "WHERE mac=? AND rout_cd=?";
				break;
			case "3":// 打流测试
				sql = "UPDATE mes1.r_pon_pcb_mac_en_t SET wander_results =?,rout_cd=?,upt_dt=sysdate "
						+ "WHERE mac=? AND rout_cd=?";
				break;
			case "4":// 查号测试
				sql = "UPDATE mes1.r_pon_pcb_mac_en_t SET check_results =?,rout_cd=?,upt_dt=sysdate "
						+ "WHERE mac=? AND rout_cd=?";
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