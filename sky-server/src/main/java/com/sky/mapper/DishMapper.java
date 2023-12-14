package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    /**
     * 根据id查询菜品数量
     * @param categoryId
     * @return
     */
    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);
    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 根据Id查询菜品
     * @param id
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);
    /**
     * 根据Id删除菜品
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);
    /**
     * 根据Id批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);
    /**
     * 起售或停售菜品
     * @param status
     * @param id
     */
    @Update("update dish set status = #{status} where id = #{id}")
    void startOrStop(Integer status, Long id);
    /**
     * 根据Id查询菜品
     * @param id
     */
    @Select("select d.*,c.name as categoryName from dish d left outer join category c on d.category_id = c.id " +
            "where d.id=#{id}")
    DishVO selectById(Long id);
    /**
     * 根据分类Id查询菜品
     * @param categoryId
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectByCategoryId(Long categoryId);
    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
    /**
     * 根据菜品Id查看status情况
     * @param ids
     * */
    Integer isContainStop(List<Long> ids);
}
