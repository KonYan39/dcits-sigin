package com.dcits.dx.card.service;

import com.dcits.dx.card.pojo.UserInfo;

/**
 * @author: dx
 * @date: 2021/7/26
 * @description:
 */
public interface CardI <T>{
	//构建打卡信息
	 T constructCardInfo(String openId);
	 //执行打卡任务
	 void executeCard(UserInfo userInfo);
	 //获取已打卡信息
	 T getCardedInfos(String id,String itcode,String openId);
	 //发送信息给机器人
	 void sendMessageToBot(T t,UserInfo userInfo,String extraMessage);
}
