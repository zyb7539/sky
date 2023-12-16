package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api("菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    /**
     * 新增菜品
     * */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
    /**
     * 分页查询
     * */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 菜品删除
     * */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品：{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }
    /**
     * 菜品起售或停售
     * */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售或停售")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("起售或停售菜品！");
        dishService.startOrStop(status,id);
        return Result.success();
    }
    /**
     * 根据Id查询菜品
     * */
    @GetMapping("/{id}")
    @ApiOperation("根据Id查询菜品")
    public Result<DishVO> selectById(@PathVariable Long id){
       DishVO dish =  dishService.selectById(id);
       log.info("菜品: {}",dish);
       return Result.success(dish);
    }
    /**
     * 根据分类Id查询菜品
     * */
    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品")
    public Result<List<Dish>> selectByCategoryId(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        log.info("根据分类Id查询菜品:{}",list);
        return Result.success(list);
    }
    /**
     * 修改菜品
     * */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }
}
