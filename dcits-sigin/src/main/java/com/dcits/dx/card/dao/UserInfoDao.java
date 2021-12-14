package com.dcits.dx.card.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dcits.dx.card.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: dx
 * @date: 2021/8/5
 * @description:
 */
@Mapper
public interface UserInfoDao extends BaseMapper<UserInfo> {
	@Select("select * from tbl_userinfo")
	List<UserInfo> getUserInfos();
}
