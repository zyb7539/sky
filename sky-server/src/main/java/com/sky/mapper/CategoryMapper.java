package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    /**
     * 增加分类
     * @param category
     * */
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            " values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);
    /**
     * 修改分类
     * @param category
     * */
    void update(Category category);

    /**
     * 根据Id删除分类
     * @param id
     * */
    @Delete("delete from category where id = #{id}")
    void deteleById(Long id);
    /**
     * 更加类型查询
     * @param type
     * */
    List<Category> list(Integer type);
}
