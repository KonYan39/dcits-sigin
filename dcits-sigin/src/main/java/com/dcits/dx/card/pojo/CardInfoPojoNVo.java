package com.dcits.dx.card.pojo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: dx
 * @date: 2021/9/29
 * @description:
 */
@Component
@Data
public class CardInfoPojoNVo {
	private CardInfoPojoN cardInfoPojoN;
	private String[] dingtalkWebHooks;
	private List<UserInfo> userInfoList;
	private List<String> workdays;
	private List<String> holidays;
	@Value("${spring.profiles.active}")
	private String active;
	private boolean isException;
}
