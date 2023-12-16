package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     * */
    void save(SetmealDTO setmealDTO);
    /**
     * 根据Id查询套餐
     * @param id
     * */
    SetmealVO selectById(Long id);
    /**
     * 批量删除套餐
     * @param ids
     * */
    void deleteBanches(List<Long> ids);
    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    /**
     * 修改套餐
     * @param setmealDTO
     * */
    void update(SetmealDTO setmealDTO);
    /**
     * 起售或禁售套餐
     * @param id
     * @param status
     * */
    void startOrStop(Integer status, Long id);
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);
    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
