package com.letaotao.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.letaotao.common.ServerResponse;
import com.letaotao.dao.CategoryMapper;
import com.letaotao.pojo.Category;
import com.letaotao.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImp implements ICategoryService{

       private Logger logger = LoggerFactory.getLogger(CategoryServiceImp.class);

       @Autowired
       private CategoryMapper categoryMapper;

       public ServerResponse<String> addCategory(String categoryName, Integer parentId){

           if (parentId == null|| StringUtils.isBlank(categoryName)){

               return ServerResponse.createByErrorMessage("添加品类参数错误");
           }
           Category category = new Category();
           category.setName(categoryName);
           category.setParentId(parentId);
           category.setStatus(true);
           int resultCount = categoryMapper.insert(category);
           if (resultCount>0){
               return ServerResponse.createByErrorMessage("添加品类成功");
           }
           return ServerResponse.createByErrorMessage("添加品类失败");

       }

       public ServerResponse setCategoryName(String categoryName, Integer categoryId){

              if (StringUtils.isBlank(categoryName)||categoryId == null){
                  return ServerResponse.createByErrorMessage("更新品类名称的参数错误");
              }
              Category category = new Category();
              category.setId(categoryId);
              category.setName(categoryName);
              int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
              if (resultCount>0){
                  return ServerResponse.createBySuccessMessage("更新品类名称成功");
              }
              return ServerResponse.createByErrorMessage("更新品类名称失败");
       }

       public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){

           List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
           if (CollectionUtils.isEmpty(categoryList)){
               logger.info("未找到当前分类的子分类");
           }
           return ServerResponse.createBySuccess(categoryList);
       }

    /**
     * 根据categoryId找到当前节点，并通过当前节点递归的找到其子节点，并找到其子节点的子节点
     * @param categoryId
     * @return
     */
       public ServerResponse selectCategoryAndChildrenById(Integer categoryId){

           Set<Category> categorySet = Sets.newHashSet();
           findChildrenCategory(categorySet,categoryId);
           List<Integer> categoryList = Lists.newArrayList();
           if (categoryId!=null){
               for(Category category:categorySet){
                   categoryList.add(category.getId());
               }
           }
           return ServerResponse.createBySuccess(categoryList);
       }

       private Set<Category> findChildrenCategory (Set<Category> categorySet,Integer categoryId){

           Category category = categoryMapper.selectByPrimaryKey(categoryId);
           if (category!=null){
               categorySet.add(category);
           }
           List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
           for(Category categoryItem:categoryList){
               findChildrenCategory(categorySet,categoryItem.getId());
           }
           return categorySet;
       }
}
