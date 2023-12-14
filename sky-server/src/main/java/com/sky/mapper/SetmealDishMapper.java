package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐和菜品关系
     * @param setmealDishes
     * */
    void insertBanch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐Id查询菜品
     * @param setmealId
     * */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);
    /**
     * 根据套餐Id批量删除
     * @param setmealIds
     * */
    void deleteBySetmealIds(List<Long> setmealIds);
}
