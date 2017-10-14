package com.letaotao.controller.portal;

import com.letaotao.common.Const;
import com.letaotao.common.ResponseCode;
import com.letaotao.common.ServerResponse;
import com.letaotao.pojo.User;
import com.letaotao.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    /**
     * 用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
     @Autowired
     private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){

        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;

    }
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){

        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){

           return iUserService.register(user);

    }
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){

        return iUserService.checkValid(str,type);

    }
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_user_info(HttpSession session){

           User user = (User)session.getAttribute(Const.CURRENT_USER);
           if (user!=null){
               return ServerResponse.createBySuccess(user);
           }
           return ServerResponse.createByErrorMessage("用户未登陆，无法获取用户当前信息");

    }
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forget_get_question(String username){

           return iUserService.selectQuestion(username);

    }
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forget_check_answer(String username, String question, String answer){

           return iUserService.checkAnswer(username, question, answer);
    }
    @RequestMapping(value = "forget_set_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forget_reset_password(String username,String passwordNew,String forgetToken) {

        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> reset_password(String passwordOld,String passwordNew,HttpSession session){
           User user =(User)session.getAttribute(Const.CURRENT_USER);
           if (user == null){
               return ServerResponse.createByErrorMessage("用户未登陆，需要登陆");
           }
           return iUserService.resetPassword(passwordOld,passwordNew,user);
    }
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session, User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登陆，需要登陆");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse serverResponse = iUserService.updateInformation(user);
        if (serverResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
        }
        return serverResponse;
    }
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登陆需要强制登陆，状态码类型为10");
        }
        return iUserService.getInformation(user.getId());
    }
}
