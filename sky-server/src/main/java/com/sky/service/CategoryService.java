package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    /**
     * 新增分类
     * @param categoryDTO
     * */
    void save(CategoryDTO categoryDTO);
    /**
     * 修改分类
     * @param categoryDTO
     * */
    void update(CategoryDTO categoryDTO);
    /**
     * 启用禁用分类
     * @Param status
     * @Param id
     * */
    void startOrStop(Integer status, Integer id);
    /**
     * 根据Id删除分类
     * @param id
     * */
    void deleteById(Long id);
    /**
     * 根据类型查询
     * @param type
     * */
    List<Category> list(Integer type);
}
