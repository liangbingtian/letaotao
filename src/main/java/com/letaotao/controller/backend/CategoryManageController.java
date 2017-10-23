package com.letaotao.controller.backend;

import com.letaotao.common.Const;
import com.letaotao.common.ResponseCode;
import com.letaotao.common.ServerResponse;
import com.letaotao.pojo.User;
import com.letaotao.service.ICategoryService;
import com.letaotao.service.IUserService;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {


    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> add_category(HttpSession session, String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){

           User user =(User)session.getAttribute(Const.CURRENT_USER);
           if (user == null){
               return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，需要强制登陆");
           }
           //接下来校验登陆用户的权限
           if (iUserService.checkAdminRole(user).isSuccess()){
                  return iCategoryService.addCategory(categoryName,parentId);
           }else {
               return ServerResponse.createByErrorMessage("无权限登陆，需要管理员权限");
           }
    }
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse set_category_name(String categoryName,Integer categoryId,HttpSession session){

        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，需要强制登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
                 return iCategoryService.setCategoryName(categoryName,categoryId);
        }else {
              return ServerResponse.createByErrorMessage("无权限登陆,需要管理员权限");
        }
    }
    @RequestMapping(value = "get_children_parallel_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse get_children_parallel_category(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

           User user =(User)session.getAttribute(Const.CURRENT_USER);
           if (user == null){
               return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登陆");
           }
           if (iUserService.checkAdminRole(user).isSuccess()){
                return iCategoryService.getChildrenParallelCategory(categoryId);
           }else {
               return ServerResponse.createByErrorMessage("无权限登陆，需要管理员权限");
           }

    }
    @RequestMapping(value = "get_category_and_deep_children_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse get_category_and_deep_children_category(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

           User user=(User)session.getAttribute(Const.CURRENT_USER);
           if (user == null){
               return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，需要强制登陆");
           }
           if (iUserService.checkAdminRole(user).isSuccess()){
                 return iCategoryService.selectCategoryAndChildrenById(categoryId);
           }else {
               return ServerResponse.createByErrorMessage("无权限登陆，需要管理员权限");
           }



    }

}

