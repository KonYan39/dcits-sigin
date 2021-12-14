package com.dcits.dx.card;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;

@SpringBootTest
class SignApplicationTests {

	@Autowired
	private Scheduler scheduler;
//	private
	@Test
	void contextLoads() {
	}
	@Test
	void quartz(){
		/*JobDetail job = JobBuilder.newJob().withIdentity("输出hello","default" ).build();
		Trigger trigger = TriggerBuilder.newTrigger().startAt().build();*/
	}

	public static void main(String[] args) throws JsonProcessingException {
		/*java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,Calendar.DAY_OF_MONTH+10);
		System.out.println(calendar.getTime());
		Random random = new Random();
		for (int i = 0; i <50 ; i++) {
			System.out.println(random.nextInt(30));
		}*/
//		System.out.println(ResourceUtil.readUtf8Str("users.json"));
		/*ObjectMapper objectMapper = new ObjectMapper();
		objectMapper*/

//		ObjectMapper objectMapper = new ObjectMapper();
//		UserInfo userInfo = objectMapper.readValue(" {\n" +
//				"    \"openId\": \"ovfnh5G6wcX0dDzowQZC7T7YeZ-I\",\n" +
//				"    \"userName\": \"丁鑫\",\n" +
//				"    \"firstCardHour\": 8,\n" +
//				"    \"firstCardMinute\": 0,\n" +
//				"    \"lastCardHour\": 18,\n" +
//				"    \"lastCardMinute\": 0,\n" +
//				"    \"nextCardTime\": \"\",\n" +
//				"    \"isCurrentFirstCard\": true\n" +
//				"  }", UserInfo.class);
//		System.out.println(userInfo);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,18);
		calendar.set(Calendar.MINUTE,18);
		calendar.set(Calendar.SECOND,50);
		System.out.println(DateUtil.formatDateTime(calendar.getTime()));
	}


}
