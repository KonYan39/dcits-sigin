package com.dcits.dx.card;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dcits.dx.card.config.CardScheduSet;
import com.dcits.dx.card.dao.UserInfoDao;
import com.dcits.dx.card.pojo.CardInfoPojoNVo;
import com.dcits.dx.card.service.CardI;
import com.dcits.dx.card.utils.MyDateUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * @author: dx
 * @date: 2021/7/26
 * @description:
 */
@RestController
public class EchoController {
	public static JSONObject userInfos = new JSONObject();
	public static String lastRotInfo;

	@Autowired
	private CardInfoPojoNVo cardInfoPojoNVo;

	@Autowired
	private CardI cardI;
	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private CardScheduSet cardScheduSet;
	@Autowired
	private Scheduler scheduler;
	@GetMapping("/getConfigs")
	public CardInfoPojoNVo getConfigs(){
		return cardInfoPojoNVo;
	}

	/**
	 * 不可删除此方法
	 * @return
	 * @see com.dcits.dx.card.jobs.CardJob#holdRunning()
	 */
	@GetMapping("/getCurrentServerTime")
	public JSONObject getCurrentServerTime(){
		JSONObject result = new JSONObject();
		result.put("time", DateUtil.formatTime(new Date()));
		result.put("date", DateUtil.formatDateTime(new Date()));
		result.put("sq", Calendar.getInstance().getTimeZone());
		return result;
	}

	@GetMapping("/lastRotInfo")
	public String getLastRotInfo(){
		return this.lastRotInfo;
	}

	@GetMapping("/getCardedInfos")
	public String testGetCardedInfos(){
		cardI.getCardedInfos("27588","","");
		return "success";
	}

	@GetMapping("/getAllJobs")
	public JSONArray getAllJobsDetails() throws SchedulerException {
		JSONArray result = new JSONArray();
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyGroup());
		for (TriggerKey triggerKey : triggerKeys) {
			JSONObject item = new JSONObject();
			Trigger trigger = scheduler.getTrigger(triggerKey);
			item.put("nextFireTime", MyDateUtil.toZoneDateTime(trigger.getNextFireTime(),"Asia/Shanghai"));
			item.put("jobDetailName",trigger.getJobKey());
			result.add(item);
		}
		return result;
	}

	/**
	 * 打卡所有
	 * @return
	 */
	@GetMapping("cardAll")
	public String cardI(){
		cardInfoPojoNVo.getUserInfoList().forEach(item->cardI.executeCard(item));
		return "success";
	}

	/***
	 *
	 * 刷新数据 会重新计算打卡时间
	 *
	 */
	@GetMapping("refresh")
	public String refresh() throws SchedulerException {
		//清除之前所有job
		scheduler.clear();
		cardScheduSet.refreshOrInit(false);
		return "success";
	}
}

