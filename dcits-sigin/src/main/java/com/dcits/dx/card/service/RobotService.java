package com.dcits.dx.card.service;

import com.alibaba.fastjson.JSONObject;
import com.dcits.dx.card.pojo.CardInfoPojoNVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author: dx
 * @date: 2021/7/30
 * @description:
 */
@Service
@Slf4j
public class RobotService {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CardInfoPojoNVo cardInfoPojoNVo;
	public void sendToDingTalk(String message,String phoneNum){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.CHINESE);
		//七点之前发消息
		if(calendar.get(Calendar.HOUR_OF_DAY)<23&&calendar.get(Calendar.HOUR_OF_DAY)>6) {
			//dingtalk
			JSONObject dingTalkChatInfo = new JSONObject();
			JSONObject content = new JSONObject();
			content.put("content", message + "环境:" + cardInfoPojoNVo.getActive());
			dingTalkChatInfo.put("msgtype", "text");
			dingTalkChatInfo.put("text", content);
			dingTalkChatInfo.put("at", "{\"atMobiles\":[\"" + phoneNum + "\"]}");
			for (String dingtalkWebHook : cardInfoPojoNVo.getDingtalkWebHooks()) {
				try {
					JSONObject result = restTemplate.postForObject(dingtalkWebHook, dingTalkChatInfo, JSONObject.class);
					log.warn("钉钉：{}", result);
				} catch (Exception e) {
					log.error("钉钉机器人发送失败");
				}
			}
		}
	}
	/*public void sendToTelegram(String message){
		//telegram
		JSONObject chatInfo = new JSONObject();
		chatInfo.put("chat_id", cardInfoPojo.getChat_id());
		chatInfo.put("text", message+"环境:"+cardInfoPojo.getActive());
		try {
			EchoController.lastRotInfo = restTemplate.postForObject(cardInfoPojo.getTelegramApiUrl(), chatInfo, JSONObject.class).toJSONString();
		}catch (Exception e){
			log.error("Telegram 机器人发送失败!");
		}
	}*/
}
