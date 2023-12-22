package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api("管理端订单相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;
    /**
     * 管理端订单查询
     * @param ordersPageQueryDTO
     * */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.adminPageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 各个状态的订单数量统计
     * */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }
    /**
     * 查询订单详细信息
     * @param id
     * */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详细信息")
    public Result<OrderVO> details(@PathVariable Long id){
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }
    /**
     * 接单
     * @param ordersConfirmDTO
     * */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }
    /**
     * 拒单
     * @param ordersRejectionDTO
     * */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    /**
     * 取消订单
     * @param ordersCancelDTO
     * */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancelAdmin(ordersCancelDTO);
        return  Result.success();
    }
    /**
     * 派送
     * @param id
     * */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return  Result.success();
    }
    /**
     * 完成派送
     * @param id
     * */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成派送")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
}
