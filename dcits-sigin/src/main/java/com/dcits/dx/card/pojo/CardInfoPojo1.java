package com.dcits.dx.card.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: dx
 * @date: 2021/7/26
 * @description:
 */
@Data
@Component
public class CardInfoPojo1 {
    @JsonIgnore
	private String telegramApiUrl;
    @JsonIgnore
    private String getCardInfoByOpenId;
    @JsonIgnore
    private String cardUrl;
    @JsonIgnore
    private String getCardedInfosByUserId;
    @JsonIgnore
    private String[] dingtalkWebHooks;
    private List<UserInfo> userInfoList;
    private List<String> workdays;
    private List<String> holidays;
    @Value("${spring.profiles.active}")
    private String active;
    private boolean isException;
}
