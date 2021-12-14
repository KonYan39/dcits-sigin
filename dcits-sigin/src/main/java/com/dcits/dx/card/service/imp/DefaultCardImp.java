package com.dcits.dx.card.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.dcits.dx.card.EchoController;
import com.dcits.dx.card.pojo.CardInfoPojoNVo;
import com.dcits.dx.card.pojo.UserInfo;
import com.dcits.dx.card.service.CardI;
import com.dcits.dx.card.service.RobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author: dx
 * @date: 2021/7/26
 * @description:
 */
@Component
@Slf4j
public class DefaultCardImp implements CardI<JSONObject> {
	@Autowired
	private CardInfoPojoNVo cardInfoPojoNVo;
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RobotService robotService;


	@Override
	public JSONObject constructCardInfo(String openId) {
		JSONObject userInfo = new JSONObject();
		try {
			JSONObject cardInfo = restTemplate.getForObject(cardInfoPojoNVo.getCardInfoPojoN().getGetCardInfoByOpenIdUrl() + openId, JSONObject.class);
			if (cardInfo.containsKey("success") && cardInfo.getBoolean("success") && cardInfo.containsKey("data")) {
				//construct info
				JSONObject data = cardInfo.getJSONObject("data");
				userInfo.put("userId", data.get("employeeId"));
				userInfo.put("projectId", data.get("projectId"));
				userInfo.put("ruleId", data.get("ID"));
				userInfo.put("addrId", data.getJSONArray("addressList").getJSONObject(0).get("id"));
				userInfo.put("apprUserId", data.get("apprUserId"));
				userInfo.put("deptId", data.get("deptId"));
				userInfo.put("workReportType", data.get("missionType"));
				userInfo.put("longitude", data.getJSONArray("addressList").getJSONObject(0).get("attendanceLon"));
				userInfo.put("latitude", data.getJSONArray("addressList").getJSONObject(0).get("attendanceLat"));
				userInfo.put("address", ((UserInfo)EchoController.userInfos.get(openId)).getAddress());
				userInfo.put("imagePath", "");
				userInfo.put("atcity", "无锡市");
				userInfo.put("pbflag", data.get("pbflag"));
				userInfo.put("beforeup", data.get("BEFOREUP"));
				userInfo.put("itcode", data.get("loginname"));
				userInfo.put("sbuId", data.get("sbuid"));
				userInfo.put("success",true);
			} else {
				userInfo.put("error", " getCardInfo " + openId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			userInfo.put("success", false);
		}
		return userInfo;
	}

	@Override
	public void executeCard(UserInfo userInfo) {
		JSONObject result = new JSONObject();
		JSONObject userInfoJson = this.constructCardInfo(userInfo.getOpenId());
		if (userInfoJson.containsKey("success")&&userInfoJson.getBoolean("success")) {
			userInfoJson.remove("success");
			result = restTemplate.postForEntity(cardInfoPojoNVo.getCardInfoPojoN().getCardUrl(), userInfoJson, JSONObject.class).getBody();
			result.put("cardInfo", this.getCardedInfos(userInfoJson.getString("userId"), userInfoJson.getString("itcode"), userInfo.getOpenId()));
		} else {
			JSONObject cardInfo = new JSONObject();
			cardInfo.put("success", false);
			cardInfo.put("openId", userInfo.getOpenId());
			result.put("cardInfo", cardInfo);
		}
		this.sendMessageToBot(result, userInfo, "~~~任务调度提示~~~~");
	}

	@Override
	public JSONObject getCardedInfos(String id, String itcode, String openId) {
		//线程睡眠3秒 因出现幻读的现象
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JSONObject result = new JSONObject();
		JSONObject cardInfos = restTemplate.getForObject(cardInfoPojoNVo.getCardInfoPojoN().getGetCardedInfosByUserIdUrl() + id, JSONObject.class);
		if (cardInfos.containsKey("success")) {
			if (cardInfos.containsKey("firstCard") && null != cardInfos.get("firstCard")) {
				JSONObject firstCard = new JSONObject();
				firstCard.put("userId", id);
				firstCard.put("itcode", itcode);
				firstCard.put("openId", openId);
				firstCard.put("firstCard-Time", cardInfos.getJSONObject("firstCard").getString("cardTime"));
				firstCard.put("firstCard-address", cardInfos.getJSONObject("firstCard").getString("address"));
				result.put("firstCard", firstCard);
			}
			if (cardInfos.containsKey("lastCard") && null != cardInfos.get("lastCard")) {
				JSONObject lastCard = new JSONObject();
				lastCard.put("userId", id);
				lastCard.put("itcode", itcode);
				lastCard.put("openId", openId);
				lastCard.put("lastCard-Time", cardInfos.getJSONObject("lastCard").getString("cardTime"));
				lastCard.put("lastCard-address", cardInfos.getJSONObject("lastCard").getString("address"));
				result.put("lastCard", lastCard);
			}
		}
		return result;
	}

	public void sendMessageToBot(JSONObject jsonItem, UserInfo userInfo, String extraMessage) {
		log.warn("=================sendMessageToBot===========jsonItem {}:userInfo[{}]", jsonItem, userInfo);
		StringBuilder str = new StringBuilder(extraMessage + "\n**************************\n");
		StringBuilder itemStr = new StringBuilder();
		JSONObject cardInfo = jsonItem.getJSONObject("cardInfo");
		if (jsonItem.containsKey("success") && jsonItem.getBoolean("success") && cardInfo.size() > 0) {
			itemStr.append("------"+userInfo.getUserName()+"------\n*openId[" + cardInfo.getJSONObject("firstCard").getString("openId") +
					"]\n*itcode[" + cardInfo.getJSONObject("firstCard").getString("itcode") + "]\n*userId["
					+ cardInfo.getJSONObject("firstCard").getString("userId") + "]\n*打卡成功\n");
			itemStr.append("*首次打卡时间:" + cardInfo.getJSONObject("firstCard").getString("firstCard-Time") + "\n*打卡地址:" + cardInfo.getJSONObject("firstCard").getString("firstCard-address") + "\n");
			if (cardInfo.containsKey("lastCard") && null != cardInfo.get("lastCard")) {
				itemStr.append("*末次打卡时间:" + cardInfo.getJSONObject("lastCard").getString("lastCard-Time") + "\n*打卡地址:" + cardInfo.getJSONObject("lastCard").getString("lastCard-address") + "\n");
			}
		} else {
			itemStr.append("---暂无打卡信息或openId找不到[" + cardInfo.get("openId") + "]\n");
		}
		itemStr.append("*下次job执行时间:" + userInfo.getNextCardTime() + "\n");
//		itemStr.append("*是否会打卡:" + (userInfo.getIsExecute()==0?"是":"否") + "\n");
		str.append(itemStr.toString() + "**************************\n");
		robotService.sendToDingTalk(str.toString(), userInfo.getAtPhoneNum());
//		robotService.sendToTelegram(str.toString());
	}
}
