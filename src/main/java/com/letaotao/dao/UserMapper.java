package com.letaotao.dao;

import com.letaotao.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    int checkEmail(String email);

    User selectLogin(@Param(value = "username") String username, @Param(value = "password") String password);

    int checkAnswer(@Param(value = "username") String username,@Param(value = "question") String question,@Param(value = "answer") String answer);

    int updatePasswordByUsername(@Param(value = "username") String username,@Param(value = "passwordNew") String passwordNew);

    int checkPassword(@Param(value = "password") String password,@Param(value = "userId") Integer userId);

    int checkEmailByUserId(@Param(value = "email") String email,@Param("userId") Integer userId);

    String selectQuestionByUsername(String username);
}