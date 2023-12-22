package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 动态条件查询
     * @param shoppingCart
     * */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据Id修改商品数量
     * @param shoppingCart
     * */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 添加新的购物车数据记录
     * @param shoppingCart
     * */
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);


    /**
     * 删除购物车记录
     * @param userId
     * */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 减小购物车记录
     * @param shoppingCart
     * */
    void delete(ShoppingCart shoppingCart);
    /**
     * 批量插入记录
     * @param list
     * */
    void insertBatch(List<ShoppingCart> list);
}
