package com.mes.service;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mes.dao.WPrintBarcodeDao;
import com.mes.pojo.BarcodeErrorMsg;
import com.mes.pojo.JsonResult;
import com.mes.pojo.ResultMessage;
import com.mes.pojo.TempWPrintBarcode;
import com.mes.pojo.WPrintBarcode;

@Service
public class WPrintBarcodeServiceImpl implements WPrintBarcodeService {

	@Autowired
	public WPrintBarcodeDao wPrintBarcodeDao;
	private static final Logger log = LoggerFactory.getLogger(WPrintBarcodeServiceImpl.class);

	/**
	 * 验证mac的过站信息
	 */
	@Override
	public String checkData(String worksta, String mac) {
		JsonResult jsonResult = new JsonResult();
		ObjectMapper mapper = new ObjectMapper();

		List<WPrintBarcode> barcodeList;
		List<TempWPrintBarcode> stepList;

		try {
			barcodeList = wPrintBarcodeDao.selectBarcodeByMac(mac);

			if (barcodeList.size() == 0) {
				log.error("mac地址：" + mac + " 不存在！");
				jsonResult.setResult("NG");
				jsonResult.setMsg("mac地址：" + mac + " 不存在！");
				return mapper.writeValueAsString(jsonResult);
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
					log.error("mac地址：" + mac + " 目前处于" + step + "工位");
					jsonResult.setResult("NG");
					jsonResult.setMsg("mac地址：" + mac + " 目前处于" + step + "工位");
					return mapper.writeValueAsString(jsonResult);
				}
			}

			// 查找工站下一站的信息
			stepList = wPrintBarcodeDao.selectBarcodeNextStep(mac, worksta);

			if (stepList.size() == 0) {
				log.error("查找不到工位:" + worksta + "下一站的信息！");
				jsonResult.setResult("NG");
				jsonResult.setMsg("查找不到工位:" + worksta + "下一站的信息！");
				return mapper.writeValueAsString(jsonResult);
			}

			String step = stepList.get(0).getStepNo().toString();
			String message = "";
			String sql = "";
			switch (step) {
			case "1":// 烧录、写号
				sql = "SELECT pcbsn,pnsn,mac,gpon_mac FROM w_print_barcode WHERE (flag is null OR flag='0')AND mac=? AND cd=?";
				message = "mac绑定";
				break;

			case "2":// bob测试
				sql = "SELECT pcbsn,pnsn,mac,gpon_mac FROM w_print_barcode WHERE (bob_result is null OR bob_result='0') AND "
						+ "flag='1' AND mac=? AND cd=?";
				message = "写号";
				break;

			case "3":// 打流测试
				sql = "SELECT pcbsn,pnsn,mac,gpon_mac FROM w_print_barcode WHERE (wander_result is null OR wander_result='0')"
						+ "AND bob_result='1' AND mac=? AND cd=?";
				message = "bob测试";
				break;

			case "4":// 查号测试
				sql = "SELECT pcbsn,pnsn,mac,gpon_mac FROM w_print_barcode WHERE (check_result is null OR check_result='0')"
						+ "AND wander_result='1' AND mac=? AND cd=?";
				message = "打流测试";
				break;
			}
			barcodeList = wPrintBarcodeDao.selectBarcodeBySql(sql, mac, worksta);

			if (barcodeList.size() == 0) {
				log.error("mac：" + mac + "未通过" + message);
				jsonResult.setResult("NG");
				jsonResult.setMsg("mac：" + mac + "未通过" + message);
				return mapper.writeValueAsString(jsonResult);
			}

			if (step.equals("3") || step.equals("2")) {
				jsonResult.setResult("OK");
				jsonResult.setMsg("验证成功");
				return mapper.writeValueAsString(jsonResult);
			} else {

				ResultMessage result = new ResultMessage();
				result.setPcbsn(barcodeList.get(0).getPcbsn());
				result.setPnsn(barcodeList.get(0).getPnsn());
				result.setMac(barcodeList.get(0).getMac());

				if (barcodeList.get(0).getGponMac() != null || !barcodeList.get(0).getGponMac().equals("")) {
					result.setMac2(barcodeList.get(0).getGponMac());
				}

				List<TempWPrintBarcode> modelList = wPrintBarcodeDao.selectMacModel(mac);
				if (modelList.size() > 0) {
					result.setModel(modelList.get(0).getProModel());
				}
				// 预留字段

				jsonResult.setResult("OK");
				jsonResult.setMsg(result);
				return mapper.writeValueAsString(jsonResult);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("访问数据库时发生错误！");

			jsonResult.setResult("FAULT");
			jsonResult.setMsg("访问数据库时发生错误！");
			String result = null;
			try {
				result = mapper.writeValueAsString(jsonResult);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return result;
		}
	}

	/**
	 * 更新测试结果
	 * 
	 * @param worksta  工序编号
	 * @param mac      mac地址
	 * @param flag     测试结果 flag=1测试成功，flag=0测试失败
	 * @param errorMsg 失败原因（不良描述）
	 * @param          reserve1---20 预留字段
	 * @return
	 */
	@Override
	public String updateResult(String worksta, String mac, int flag, String errorMsg, String reserve1, String reserve2,
			String reserve3, String reserve4, String reserve5, String reserve6, String reserve7, String reserve8,
			String reserve9, String reserve10, String reserve11, String reserve12, String reserve13, String reserve14,
			String reserve15, String reserve16, String reserve17, String reserve18, String reserve19,
			String reserve20) {
		JsonResult jsonResult = new JsonResult();
		ObjectMapper mapper = new ObjectMapper();

		List<WPrintBarcode> barcodeList;
		List<TempWPrintBarcode> stepList;

		try {
			barcodeList = wPrintBarcodeDao.selectBarcodeByMac(mac);
			log.info("查找mac信息...");

			if (barcodeList.size() == 0) {
				log.error("mac地址：" + mac + " 不存在！");
				jsonResult.setResult("NG");
				jsonResult.setMsg("mac地址：" + mac + " 不存在！");
				return mapper.writeValueAsString(jsonResult);
			}

			if (!worksta.equals(barcodeList.get(0).getCd())) {// 当上传的工站编号与数据库的工站编号不一致时返回当前工站信息
				stepList = wPrintBarcodeDao.selectBarcodeStep(barcodeList.get(0).getCd());
				log.info("查找工站信息...");

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
					log.error("mac地址：" + mac + " 目前处于" + step + "工位");
					jsonResult.setResult("NG");
					jsonResult.setMsg("mac地址：" + mac + " 目前处于" + step + "工位");
					return mapper.writeValueAsString(jsonResult);
				}
			}

			// 查找工位信息
			stepList = wPrintBarcodeDao.selectBarcodeNextStep(mac, worksta);
			log.info("查找工位信息...");

			if (stepList.size() == 0) {
				log.error("查找不到工位:" + worksta + "下一站的信息！");
				jsonResult.setResult("NG");
				jsonResult.setMsg("查找不到工位:" + worksta + "下一站的信息！");
				return mapper.writeValueAsString(jsonResult);
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
				log.info("保存测试成功信息...");
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
				log.info("保存测试失败信息...");
				wPrintBarcodeDao.updateBarcodeStatus(sql, flag, worksta, mac, null);

				// 添加不良品信息
				log.info("添加不良品信息...");
				wPrintBarcodeDao.saveBadInput(barcodeList.get(0).getPcbsn(), barcodeList.get(0).getPnsn(), "组装段不良",
						message);

				// 添加不良描述
				if (errorMsg != null) {
					log.info("添加不良描述...");
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
			log.info("更新其他信息...");
			String station = flag == 1 ? nextWorkStation : worksta;
			wPrintBarcodeDao.updateOtherMessage(sql, flag, station, mac, worksta);
		} catch (Exception e) {
			
			e.printStackTrace();
			log.error("访问数据库时发生错误！");
			jsonResult.setResult("FAULT");
			jsonResult.setMsg("访问数据库时发生错误！");
			String result = null;
			try {
				result = mapper.writeValueAsString(jsonResult);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return result;
		}
		
		log.info("保存成功！");
		jsonResult.setResult("OK");
		jsonResult.setMsg("保存成功！");
		String result = null;
		try {
			result = mapper.writeValueAsString(jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}