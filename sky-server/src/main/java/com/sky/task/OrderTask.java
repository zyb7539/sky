package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，处理超时订单
 * */
//TODO 修改时间
// @Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理超时订单的方法
     * */
    @Scheduled(cron = "0 * * * * ?")    //每分钟触发一次
    public void processTimeOutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-15);

        //查询超时订单,待付款,订单时间超过15分钟
        List<Orders> list = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT,orderTime);
        if(list != null && list.size() >0){
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("订单超时，自动取消");
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直派送中的订单
     * */
    @Scheduled(cron = "0 0 1 * * ?") //每天凌晨一点
    public void processDeliveryOrder(){
        log.info("定时处理派送中订单：{}", LocalDateTime.now());
        List<Orders> list = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusHours(-1));
        if(list != null && list.size() >0){
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
               orderMapper.update(orders);
            }
        }
    }

}
