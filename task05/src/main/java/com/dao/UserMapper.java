package com.dao;

import com.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User selectById(Long id);

    User selectByName(String name);

    int insert(User record);

    User selectByCondition(@Param("name") String name, @Param("password") String password);
}
