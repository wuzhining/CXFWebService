package com.mes.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mes.dao.WPrintBarcodeDao;
import com.mes.pojo.BarcodeErrorMsg;
import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;

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

		try {
			barcodeList = wPrintBarcodeDao.selectBarcodeByMac(mac);

			if (barcodeList.size() == 0) {
				System.out.println("mac地址：" + mac + " 不存在！");
				return "mac地址：" + mac + " 不存在！";
			}

			if (!worksta.equals(barcodeList.get(0).getCd())) {// 当上传的工站编号与数据库的工站编号不一致时返回当前工站信息
				stepList = wPrintBarcodeDao.selectBarcodeStep(barcodeList.get(0).getCd());

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

			// 查找工站下一站的信息
			stepList = wPrintBarcodeDao.selectBarcodeNextStep(mac, worksta);

			if (stepList.size() == 0) {
				System.out.println("查找不到工位:" + worksta + "下一站的信息！");
				return "查找不到工位:" + worksta + "下一站的信息！";
			}

			String step = stepList.get(0).getStepNo().toString();
			String message = "";
			String sql = "";
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
			barcodeList = wPrintBarcodeDao.selectBarcodeBySql(sql, mac, worksta);

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

		try {
			barcodeList = wPrintBarcodeDao.selectBarcodeByMac(mac);

			if (barcodeList.size() == 0) {
				System.out.println("mac地址：" + mac + " 不存在！");
				return "mac地址：" + mac + " 不存在！";
			}

			if (!worksta.equals(barcodeList.get(0).getCd())) {// 当上传的工站编号与数据库的工站编号不一致时返回当前工站信息
				stepList = wPrintBarcodeDao.selectBarcodeStep(barcodeList.get(0).getCd());

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
			stepList = wPrintBarcodeDao.selectBarcodeNextStep(mac, worksta);

			if (stepList.size() == 0) {
				System.out.println("查找不到工位:" + worksta + "下一站的信息！");
				return "查找不到工位:" + worksta + "下一站的信息！";
			}

			String nextWorkStation = stepList.get(0).getCd();
			String stepCode = stepList.get(0).getStepNo().toString();
			String sql = "";

			if (flag == 1) {// 成功
				// 更新状态，添加过站信息
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
				wPrintBarcodeDao.updateBarcodeStatus(sql, flag, nextWorkStation, mac, worksta);

			} else {// 失败
				String message = "";

				switch (stepCode) {
				case "1":// 烧录
					sql = "UPDATE w_print_barcode set flag=? WHERE cd=? AND mac=? AND flag is null";
					message = "写号不良";
					break;
				case "2":// bob测试
					sql = "UPDATE w_print_barcode set bob_result=? WHERE cd=? AND mac=? AND bob_result is null";
					message = "bob测试不良";
					break;
				case "3":// 打流测试
					sql = "UPDATE w_print_barcode set wander_result=? WHERE cd=? AND mac=? AND wander_result is null";
					message = "打流测试不良";
					break;
				case "4":// 查号测试
					sql = "UPDATE w_print_barcode set check_result=? WHERE cd=? AND mac=? AND check_result is null";
					message = "查号不良";
					break;
				}
				wPrintBarcodeDao.updateBarcodeStatus(sql, flag, worksta, mac, null);

				// 添加不良品信息
				wPrintBarcodeDao.saveBadInput(barcodeList.get(0).getPcbsn(), barcodeList.get(0).getPnsn(), "组装段不良",
						message);

				// 添加不良描述
				if (errorMsg != null) {
					List<BarcodeErrorMsg> errorMsgList = wPrintBarcodeDao
							.selectBarcodeErrorMsg(barcodeList.get(0).getPcbsn(), mac, errorMsg, worksta);

					if (errorMsgList.size() == 0) {
						wPrintBarcodeDao.saveBarcodeErrorMsg(barcodeList.get(0).getPcbsn(), mac, errorMsg, worksta);
					}
				}
			}

			// 更新其他信息
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

			// 测试成功更新为下一站的编号，测试失败则编号不变
			String station = flag == 1 ? nextWorkStation : worksta;
			wPrintBarcodeDao.updateOtherMessage(sql, flag, station, mac, worksta);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("更新数据时发生错误！");
			return "FAULT";
		}
		return "SUCCESS";
	}
}