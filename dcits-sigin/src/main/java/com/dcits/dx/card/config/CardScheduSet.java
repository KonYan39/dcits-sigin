package com.dcits.dx.card.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dcits.dx.card.EchoController;
import com.dcits.dx.card.dao.CardInfoDao;
import com.dcits.dx.card.dao.UserInfoDao;
import com.dcits.dx.card.jobs.CardJob;
import com.dcits.dx.card.pojo.CardInfoPojoN;
import com.dcits.dx.card.pojo.CardInfoPojoNVo;
import com.dcits.dx.card.pojo.UserInfo;
import com.dcits.dx.card.service.CardI;
import com.dcits.dx.card.utils.MyDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.*;

/**
 * @author: dx
 * @date: 2021/7/28
 * @description:打卡调度设置
 */
@Component
@Slf4j
public class CardScheduSet {
	@Autowired
	private Scheduler scheduler;


	@Autowired
	private Random random;

	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private CardI<JSONObject> cardI;

	@Autowired
	private CardInfoPojoNVo cardInfoPojoNVo;

	@Autowired
	private CardInfoDao cardInfoDao;

	public void setCardSchedu(UserInfo userInfo) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.CHINESE);
		String times;
		//当天是否第一次打卡
		if (userInfo.getCurrentDayCardCounts() == 1) {
			//已第一次打卡 任务调度下午
			calendar.set(Calendar.HOUR_OF_DAY, userInfo.getLastCardHour());
			calendar.set(Calendar.MINUTE, RandomUtil.randomInt(userInfo.getLastCardMinute(), userInfo.getLastCardMinute() + userInfo.getAfterCardMinutes()));
			times = "一次打卡";
		} else {
			//不是第一次打卡 任务调度第二天上午
			//当前小时大于打卡的小时才会设置第二天打卡
			if (calendar.get(Calendar.HOUR_OF_DAY) > userInfo.getFirstCardHour())
				calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
			calendar.set(Calendar.HOUR_OF_DAY, userInfo.getFirstCardHour());
			calendar.set(Calendar.MINUTE, RandomUtil.randomInt(userInfo.getFirstCardMinute(), userInfo.getFirstCardMinute() + userInfo.getAfterCardMinutes()));
			times = "二次打卡";
		}
		//秒数也随机生成
		calendar.set(Calendar.SECOND, random.nextInt(60));
		Date nextCardTime = calendar.getTime();
		// 双休日不打卡 除非工作日 法定假日不打卡
		String nextCardDate = DateUtil.format(calendar.getTime(), "MM-dd");
		String nextCartTimeStr = MyDateUtil.toZoneDateTime(nextCardTime, "Asia/Shanghai");
		log.warn("{}:下次job时间: [{}] 用户:[{}],dayOfWeek:[{}],nextJobDate:[{}]", times, nextCartTimeStr, userInfo.getUserName(), DateUtil.dayOfWeek(nextCardTime), nextCardDate);
		userInfo.setNextCardTime(nextCartTimeStr);
		//理论上调度结束之后 job就会消失 下方name和group可以重复使用 但是此处会在job中重新调用这方法 然后此时job还没消失 下方调用就会报错 故加uuid字段
		JobDetail job = JobBuilder.newJob(CardJob.class).withIdentity("card-" + RandomUtil.randomStringUpper(5), userInfo.getUserName()).build();
		//传递参数给job
		job.getJobDataMap().put("openId", userInfo.getOpenId());
		//开启任务调度
		Trigger trigger = TriggerBuilder.newTrigger().startAt(nextCardTime).build();
		try {
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}


	public void refreshOrInit(boolean isInit) {
		//初始化参数
		CardInfoPojoN cardInfo = cardInfoDao.getCardInfos();
		cardInfoPojoNVo.setException(false);
		cardInfoPojoNVo.setCardInfoPojoN(cardInfoDao.getCardInfos());
		cardInfoPojoNVo.setDingtalkWebHooks(cardInfo.getDingTalkWebHooks().split(","));
		cardInfoPojoNVo.setHolidays(Arrays.asList(cardInfo.getHolidays().split(",")));
		cardInfoPojoNVo.setWorkdays(Arrays.asList(cardInfo.getWorkdays().split(",")));
		cardInfoPojoNVo.setUserInfoList(userInfoDao.getUserInfos());
		cardInfoPojoNVo.getUserInfoList().forEach(userInfo -> {
			EchoController.userInfos.put(userInfo.getOpenId(), userInfo);
			//程序启动通知打卡机器人
			JSONObject userInfoJson = cardI.constructCardInfo(userInfo.getOpenId());
			if(!userInfoJson.getBoolean("success"))
				if(!userInfo.getUserName().contains("error openId may error")){
					userInfo.setUserName(userInfo.getUserName() + "--constructCardInfo error openId may error");
				}
			JSONObject messageWrapper = new JSONObject();
			JSONObject cardedInfos = cardI.getCardedInfos(userInfoJson.getString("userId"), userInfoJson.getString("itcode"), userInfo.getOpenId());
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.CHINESE);
			//服务启动时间在打卡在末次打卡时间之前 则不管是否打过卡都需要修改当天打卡次数为1
			if (calendar.get(Calendar.HOUR_OF_DAY) <= userInfo.getLastCardHour()) {
				userInfo.setCurrentDayCardCounts(1);
			}
			// 末次打卡和第二天初次打卡之间启动服务设置打卡次数为0
			if (calendar.get(Calendar.HOUR_OF_DAY) > userInfo.getLastCardHour() || calendar.get(Calendar.HOUR_OF_DAY) <= userInfo.getFirstCardHour()) {
				userInfo.setCurrentDayCardCounts(0);
			}
			messageWrapper.put("cardInfo", cardedInfos);
			messageWrapper.put("success", true);
			//构建任务调度
			this.setCardSchedu(userInfo);
			cardI.sendMessageToBot(messageWrapper, userInfo, isInit?"~~~系统初次启动~~~":"~~~刷新参数~~~");
			//初次启动强制打卡
			if (userInfo.getIsFirstForce() == 1) {
				cardI.executeCard(userInfo);
			}
			UpdateWrapper updateWrapper = new UpdateWrapper();
			updateWrapper.eq("open_id", userInfo.getOpenId());
			userInfoDao.update(userInfo, updateWrapper);
		});
	}
}