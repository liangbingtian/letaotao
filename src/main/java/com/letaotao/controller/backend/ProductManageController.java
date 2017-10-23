package com.letaotao.controller.backend;

import com.letaotao.common.Const;
import com.letaotao.common.ResponseCode;
import com.letaotao.common.ServerResponse;
import com.letaotao.pojo.Product;
import com.letaotao.pojo.User;
import com.letaotao.service.IProductService;
import com.letaotao.service.IUserService;
import com.letaotao.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "product_save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse product_save(HttpSession session, Product product){

        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登陆管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);

        }else {
            return ServerResponse.createByErrorMessage("无权限登陆，需要管理员权限");
        }


    }
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> set_sale_status(HttpSession session, Integer productId, Integer status){

        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登陆，需要管理员登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
                   return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("无权限登陆，需要管理员权限");
        }

    }
    @RequestMapping(value = "get_detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> get_detail(HttpSession session, Integer productId){

        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登陆，需要管理员登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
               return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("无权限登陆，需要管理员权限");
        }

    }

    @RequestMapping(value = "get_list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> get_list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){

        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("管理员未登录，需要管理员强制登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){

        }else{
            return ServerResponse.createByErrorMessage("无权限登陆，需要管理员权限");
        }



    }


}
