package com.dcits.dx.card.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: dx
 * @date: 2021/7/29
 * @description:
 */
public class MyDateUtil {
	public static String toZoneDateTime(Date date, String zoneId){
		Instant instant = date.toInstant();
		//系统默认的时区
		ZoneId desZoneId = ZoneId.of(zoneId);
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, desZoneId);
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss "));
	}
}
