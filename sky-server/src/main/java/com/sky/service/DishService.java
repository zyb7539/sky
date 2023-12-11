package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     * */
    public void saveWithFlavor(DishDTO dishDTO);
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 菜品的批量删除
     * @param ids
     * */
    void deleteBatch(List<Long> ids);
    /**
     * 起售或停售菜品
     * @param status
     * @param id
     * */
    void startOrStop(Integer status, Long id);
    /**
     * 根据Id查询菜品
     * @param id
     * */
    DishVO selectById(Long id);
    /**
     * 根据分类Id查询菜品
     * @param categoryId
     * */
    List<Dish> selectByCategoryId(Long categoryId);
    /**
     * 修改菜品
     * @param dishDTO
     * */
    void update(DishDTO dishDTO);
}
