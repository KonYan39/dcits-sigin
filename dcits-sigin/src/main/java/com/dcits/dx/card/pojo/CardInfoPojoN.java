package com.dcits.dx.card.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author: dx
 * @date: 2021/7/26
 * @description:
 */
@Data
public class CardInfoPojoN {
    @JsonIgnore
    private String getCardInfoByOpenIdUrl;
    @JsonIgnore
    private String cardUrl;
    @JsonIgnore
    private String getCardedInfosByUserIdUrl;

    private String dingTalkWebHooks;
    private String workdays;
    private String holidays;
}
