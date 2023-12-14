package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setmeal
     * @return
     */
    @AutoFill(OperationType.INSERT)
    void save(Setmeal setmeal);
    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);
    /**
     * 批量删除套餐
     * @param ids
     * */
    void deleteBanches(List<Long> ids);
    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * */
    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    /**
     * 修改套餐
     * @param setmeal
     * */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
    /**
     * 起售或停售套餐
     * @param status
     * @param id
     * */
    @Update("update setmeal set status = #{status} where id = #{id};")
    void startOrStop(Integer status, Long id);
}
