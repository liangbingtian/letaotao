package com.letaotao.service;

import com.letaotao.common.ServerResponse;
import com.letaotao.pojo.Product;
import com.letaotao.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
}
