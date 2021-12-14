package com.dcits.dx.card.jobs;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dcits.dx.card.EchoController;
import com.dcits.dx.card.config.CardScheduSet;
import com.dcits.dx.card.dao.UserInfoDao;
import com.dcits.dx.card.pojo.CardInfoPojoNVo;
import com.dcits.dx.card.pojo.UserInfo;
import com.dcits.dx.card.service.CardI;
import com.dcits.dx.card.service.RobotService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * @author: dx
 * @date: 2021/7/28
 * @description: 自定义的打卡时间
 */
@Component
@Slf4j
public class CardJob implements Job {
//	@Value("${holdRunning}")
	private String holdRunning;
	@Autowired
	private CardScheduSet cardScheduSet;
	@Autowired
	private CardI cardI;


	@Autowired
	private CardInfoPojoNVo cardInfoPojoNVo;

	@Autowired
	private RobotService robotService;

	@Autowired
	private UserInfoDao userInfoDao;
	@Override
	public void execute(JobExecutionContext jobExecutionContext){
		try {
			JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
			UserInfo userInfo = (UserInfo)EchoController.userInfos.get(jobDataMap.getString("openId"));
			if(!cardInfoPojoNVo.isException()) {
				userInfo.setCurrentDayCardCounts(userInfo.getCurrentDayCardCounts() == 1 ? 2 : 1);
				cardScheduSet.setCardSchedu(userInfo);
			}
			//判断当前job是否要执行打卡
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.CHINESE);
			boolean flag=true;
			boolean isWeekDay = DateUtil.dayOfWeek(calendar.getTime()) == 7 || DateUtil.dayOfWeek(calendar.getTime()) == 1;
			String currentDate = DateUtil.format(calendar.getTime(),"MM-dd");
			if(!CollectionUtil.isEmpty(cardInfoPojoNVo.getHolidays().stream().filter(holiday->holiday.equals(currentDate)).collect(Collectors.toList())))
				//假期不打卡
				flag = false;
				//周末
			else if(isWeekDay){
				//工作日打卡
				flag = !CollectionUtil.isEmpty(cardInfoPojoNVo.getWorkdays().stream().filter(workday->workday.equals(currentDate)).collect(Collectors.toList()));
			}


			if(userInfo.getIsFullCard()==1){
				flag=true;
			}
			//先开启任务调度再执行打卡以便发送消息时可以获取到正确的下次打卡时间 判断是否需要打卡 过滤周末
			if(flag && "1".equals(userInfo.getYxbz())){
				cardI.executeCard(userInfo);
				UpdateWrapper updateWrapper = new UpdateWrapper();
				updateWrapper.eq("open_id",userInfo.getOpenId());
				userInfoDao.update(userInfo,updateWrapper);
			}
		}catch (Exception e){
			e.printStackTrace();
			//
			robotService.sendToDingTalk("==========ERROR==========\n打卡异常，异常信息：【{"+e.getMessage()+"}】请及时处理 \n系统已重新执行该job","18861856639");
//			robotService.sendToTelegram("==========ERROR==========\n打卡异常，异常信息：【{"+e.getMessage()+"}】请及时处理 \n系统已重新执行该job");
			JobExecutionException e2 = new JobExecutionException(e);
			// 一分钟后执行
			try {
				Thread.sleep(60*1000L);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			// 这个工作将立即重新开始
			e2.setRefireImmediately(true);
		}

	}

	/**
	 * @author:dx
	 * @date:2021/7/28
	 * @time:15:09
	 * @description:用于维持连接 heroku 平台30min无人访问应用就会处于休眠状态
	 * 此方法20分钟执行一次 调用地址执行方法为调用此项目接口 /
	 */
	/*@Scheduled(cron = "0 0/20 * * * ? ")
	public void holdRunning(){
		try{
			restTemplate.getForObject(holdRunning,String.class);
		}catch (Exception e){
			log.error("holdRunning error");
		}
	}*/

}
