package com.letaotao.service.impl;

import com.letaotao.common.Const;
import com.letaotao.common.ServerResponse;
import com.letaotao.common.TokenCache;
import com.letaotao.dao.UserMapper;
import com.letaotao.pojo.User;
import com.letaotao.service.IUserService;
import com.letaotao.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUserName(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    public ServerResponse<String> register(User user){

        ServerResponse checkvaild = this.checkValid(user.getUsername(),Const.EMAIL);
        if (!checkvaild.isSuccess())
            return checkvaild;
        checkvaild = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!checkvaild.isSuccess())
            return checkvaild;
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){

        if (StringUtils.isNoneBlank(type)){
            if (type.equals(Const.USER_NAME)){
                int resultCount = userMapper.checkUserName(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (type.equals(Const.EMAIL)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username){

        ServerResponse checkvalid = this.checkValid(username,Const.USER_NAME);
        if (checkvalid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (question!=null){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("忘记密码的问题为空");

    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer){

        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("忘记密码问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){

        if (StringUtils.isBlank(forgetToken)){
             return ServerResponse.createByErrorMessage("参数错误，token需要进行传递");
        }
        ServerResponse checkvalid = this.checkValid(username,Const.USER_NAME);
        if (checkvalid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if (StringUtils.equals(forgetToken,token)){
              String md5password = MD5Util.MD5EncodeUtf8(passwordNew);
              int resultCount = userMapper.updatePasswordByUsername(username,md5password);
              if (resultCount>0){
                  return ServerResponse.createBySuccessMessage("更新密码成功");
              }
              return ServerResponse.createByErrorMessage("更新密码失败");
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取token");
        }
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
           int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
           if (resultCount == 0){
               return ServerResponse.createByErrorMessage("旧密码错误");
           }
           user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
           resultCount = userMapper.updateByPrimaryKeySelective(user);
           if (resultCount>0){
               return ServerResponse.createBySuccessMessage("密码更新成功");
           }
           return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user){
           int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
           if (resultCount>0){
               return ServerResponse.createByErrorMessage("email已存在");
           }
           User updateUser = new User();
           updateUser.setId(user.getId());
           updateUser.setEmail(user.getEmail());
           updateUser.setQuestion(user.getQuestion());
           updateUser.setAnswer(user.getAnswer());
           resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
           if (resultCount>0){
               return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
           }
           return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
