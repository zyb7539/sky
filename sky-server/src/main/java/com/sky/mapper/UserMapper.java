package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查用户
     * @param openid
     * */
    @Select("select * from user where openid = #{openid}")
    User getByOPenId(String openid);

    /**
     * 新增用户
     * */
    void insert(User user);
}
