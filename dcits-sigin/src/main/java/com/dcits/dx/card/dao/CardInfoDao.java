package com.dcits.dx.card.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dcits.dx.card.pojo.CardInfoPojoN;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: dx
 * @date: 2021/8/5
 * @description:
 */
@Mapper
public interface CardInfoDao extends BaseMapper<CardInfoPojoN> {
	@Select("select * from tbl_cardconf")
	CardInfoPojoN getCardInfos();
}
