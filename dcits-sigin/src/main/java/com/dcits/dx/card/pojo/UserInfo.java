package com.dcits.dx.card.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: dx
 * @date: 2021/7/28
 * @description:
 */
@NoArgsConstructor
@Data
@TableName("tbl_userinfo")
public class UserInfo {
	@JsonIgnore
	@TableId
	private String openId;
	private String userName;
	private int firstCardHour;
	private int firstCardMinute;
	private int lastCardHour;
	private int lastCardMinute;
	private String nextCardTime;
	private int afterCardMinutes;
	@JsonIgnore
	private String atPhoneNum;
	private int currentDayCardCounts;
	//初次启动是否强制打卡
	private int isFirstForce;

	private int isFullCard;

	private String address;

	private String yxbz;

}
