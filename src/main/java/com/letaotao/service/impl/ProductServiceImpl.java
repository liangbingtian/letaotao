package com.letaotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.letaotao.common.ResponseCode;
import com.letaotao.common.ServerResponse;
import com.letaotao.dao.CategoryMapper;
import com.letaotao.dao.ProductMapper;
import com.letaotao.pojo.Category;
import com.letaotao.pojo.Product;
import com.letaotao.service.IProductService;
import com.letaotao.util.DateTimeUtil;
import com.letaotao.util.PropertiesUtil;
import com.letaotao.vo.ProductDetailVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService{


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse saveOrUpdateProduct(Product product){

           if (product!=null){
               if (StringUtils.isNotBlank(product.getSubImages())){
                   String[] subimages = product.getSubImages().split(",");
                   if (subimages.length>0){
                       product.setMainImage(subimages[0]);
                   }
               }
               if (product.getId()!=null){
                   int resultCount = productMapper.updateByPrimaryKey(product);
                   if (resultCount>0){
                       return ServerResponse.createBySuccessMessage("更新产品成功");
                   }
                   return ServerResponse.createByErrorMessage("更新产品失败");
               }else {
                   int resultCount = productMapper.insert(product);
                   if (resultCount>0){
                       return ServerResponse.createBySuccessMessage("新增产品成功");
                   }
                   return ServerResponse.createByErrorMessage("新增产品失败");
               }
           }
        return ServerResponse.createByErrorMessage("新增或更新产品的参数不正确");
    }


    public ServerResponse<String> setSaleStatus(Integer productId, Integer status){

        if (productId == null||status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("更新销售状态成功");
        }
        return ServerResponse.createByErrorMessage("更新销售状态失败");
    }


    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){

        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者已经被删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    //通过assemble方法将product对象组装成为productDetailVo对象。
    private ProductDetailVo assembleProductDetailVo(Product product){

            ProductDetailVo productDetailVo = new ProductDetailVo();
            productDetailVo.setId(product.getId());
            productDetailVo.setCategoryId(product.getCategoryId());
            productDetailVo.setSubtitle(product.getSubtitle());
            productDetailVo.setMainImage(product.getMainImage());
            productDetailVo.setSubImages(product.getSubImages());
            productDetailVo.setDetail(product.getDetail());
            productDetailVo.setName(product.getName());
            productDetailVo.setPrice(product.getPrice());
            productDetailVo.setStatus(product.getStatus());
            productDetailVo.setStock(product.getStock());
            //接下来获得ProductDetailVo中四个属性

        productDetailVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ServerResponse getProductList(int pageNum, int pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectProductList();



    }


}
