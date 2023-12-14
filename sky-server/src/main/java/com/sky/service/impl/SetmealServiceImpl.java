package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public void save(SetmealDTO setmealDTO) {
        //添加套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.save(setmeal);
        Long setmealId = setmeal.getId();
        //添加套餐包含的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
            }
            setmealDishMapper.insertBanch(setmealDishes);
        }

    }

    @Override
    public SetmealVO selectById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        //先获得套餐信息
        Setmeal setmeal = setmealMapper.selectById(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        Long setmealId = setmealVO.getId();
        //获得对应菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(setmealId);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Transactional
    @Override
    public void deleteBanches(List<Long> ids) {
        //判断套餐是否起售
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE)
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ON_SALE);
        }
        //删除套餐
        setmealMapper.deleteBanches(ids);
        //删除关联的菜品
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page =  setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        //修改套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.update(setmeal);
        Long setmealId = setmeal.getId();
        //修改套餐包含的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
            }
            List<Long> ids = new ArrayList<>();
            ids.add(setmealId);
            setmealDishMapper.deleteBySetmealIds(ids);
            setmealDishMapper.insertBanch(setmealDishes);
        }
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，如果套餐内包含停售的菜品，则不能起售
        if(status == 1){
            //判断id套餐内是否包含有停售的菜品
            //获取关联的菜品Id
            List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
            List<Long> ids = new ArrayList<>();
            if(setmealDishes != null && setmealDishes.size() >0){
                for (SetmealDish setmealDish : setmealDishes) {
                    ids.add(setmealDish.getDishId());
                }
                Integer count = dishMapper.isContainStop(ids);
                if (count > 0){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }else {
                    setmealMapper.startOrStop(status,id);
                }
            }else {
                setmealMapper.startOrStop(status,id);
            }
        }
    }
}
