package com.letaotao.controller.backend;

import com.letaotao.common.Const;
import com.letaotao.common.ServerResponse;
import com.letaotao.pojo.User;
import com.letaotao.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

       @Autowired
        private IUserService iUserService;
       @RequestMapping(value = "manager_login.do",method = RequestMethod.POST)
       @ResponseBody
       public ServerResponse<User> manager_login(String username, String password, HttpSession session){

             ServerResponse<User> serverResponse = iUserService.login(username, password);
             if (serverResponse.isSuccess()){
                 User user = serverResponse.getData();
                 if (user.getRole() == Const.Role.ROLE_ADMIN){
                     session.setAttribute(Const.CURRENT_USER,user);
                     return serverResponse;
                 }else {
                     return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
                 }
             }
             return serverResponse;
       }
}
