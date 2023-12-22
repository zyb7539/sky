package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    // @Autowired
    // private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常，地址簿为空，购物车为空
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null || list.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PAID);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));   //订单号
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());
        StringBuilder sb = new StringBuilder();
        sb.append(addressBook.getProvinceName()).append(" ")
                        .append(addressBook.getCityName()).append(" ")
                        .append(addressBook.getDistrictName()).append(" ")
                        .append(addressBook.getDetail());
        orders.setAddress(sb.toString());

        orderMapper.insert(orders);
        //向订单明细表插入n条数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());

            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);
        //清空购物车数据
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
        //返回VO结果
        OrderSubmitVO build = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return build;

    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        // Long userId = BaseContext.getCurrentId();
        // User user = userMapper.getById(userId);

        // //调用微信支付接口，生成预支付交易单
        // JSONObject jsonObject = weChatPayUtil.pay(
        //         ordersPaymentDTO.getOrderNumber(), //商户订单号
        //         new BigDecimal(0.01), //支付金额，单位 元
        //         "苍穹外卖订单", //商品描述
        //         user.getOpenid() //微信用户的openid
        // );
        //
        // if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
        //     throw new OrderBusinessException("该订单已支付");
        // }
        //
        // OrderPaymentVO vo = new OrderPaymentVO();
        // vo.setPackageStr(jsonObject.getString("package"))
        // return vo;

        //无微信支付实现

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过websocket向客户端浏览器推送消息
        Map map = new HashMap();
        map.put("type",1);  //1来单提醒，2客户催单
        map.put("orderId",orders.getId());
        map.put("content","订单号：" + ordersPaymentDTO.getOrderNumber());
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
        return  null;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult pageQuery(int page, int pageSize, Integer status) {
        PageHelper.startPage(page,pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        Page<Orders> pageList = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();
        if(pageList != null && pageList.size() > 0){
            for (Orders orders : pageList) {
                //查看订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(pageList.getTotal(), list);
    }

    @Override
    public OrderVO details(Long id) {
        OrderVO orderVO = new OrderVO();
        //查询对象orders
        Orders orders = orderMapper.getById(id);

        List<OrderDetail> list = new ArrayList<>();
        if(orders != null){
            list= orderDetailMapper.getByOrderId(id);
        }
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(list);

        return orderVO;
    }

    @Override
    public void cancel(Long id) {
        //修改订单状态,取消时间，取消原因
        Orders orders = orderMapper.getById(id);
        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (orders.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders1 = new Orders();
        //如果是待接单情况下取消，退款
        if(orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            orders1.setPayStatus(Orders.REFUND);
        }
        orders1.setId(id);
        orders1.setStatus(Orders.CANCELLED);
        orders1.setCancelReason("用户取消");
        orders1.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders1);
    }

    @Override
    public void repetition(Long id) {
        //将订单中的菜品再加入到购物车中

        //得到订单细节
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> list = new ArrayList<>();
        //加入购物车
        if(orderDetails !=null && orderDetails.size() >0){
            for (OrderDetail orderDetail : orderDetails) {
                ShoppingCart shoppingCart = new ShoppingCart();
                BeanUtils.copyProperties(orderDetail,shoppingCart);
                shoppingCart.setUserId(BaseContext.getCurrentId());
                shoppingCart.setCreateTime(LocalDateTime.now());
                list.add(shoppingCart);
            }
        }
        shoppingCartMapper.insertBatch(list);

    }

    @Override
    public void reminder(Long id) {

        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //通过websocket向客户端浏览器推送消息
        Map map = new HashMap();
        map.put("type",2);  //1来单提醒，2客户催单
        map.put("orderId",orders.getId());
        map.put("content","订单号：" +orders.getNumber());
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    @Override
    public PageResult adminPageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        //开启分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        Page<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);
        if(orders == null || orders.size() ==0){
           return null;
        }
        List<OrderVO> list = new ArrayList<>();
        for (Orders order : orders) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order,orderVO);
            StringBuilder orderDishes = new StringBuilder();
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(order.getId());
            for (OrderDetail orderDetail : orderDetails) {
                orderDishes.append(orderDetail.getName()).append(" * ").append(orderDetail.getNumber()).append(";");
            }

            orderVO.setOrderDishes(orderDishes.toString());
            list.add(orderVO);
        }

        return new PageResult(orders.getTotal(),list);
    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        //待接单数量
        Integer num = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        //待派送数量
        Integer num1 = orderMapper.countByStatus(Orders.CONFIRMED);
        //派送中数量
        Integer num2 = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);
        orderStatisticsVO.setToBeConfirmed(num);
        orderStatisticsVO.setConfirmed(num1);
        orderStatisticsVO.setDeliveryInProgress(num2);
        return orderStatisticsVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        if(orders == null){
            throw  new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Orders build = Orders.builder()
                .id(orders.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(build);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // - 商家拒单其实就是将订单状态修改为“已取消”
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());
        if(orders ==null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // - 只有订单处于“待接单”状态时可以执行拒单操作
        //- 商家拒单时需要指定拒单原因
        Orders orders1 = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        //- 商家拒单时，如果用户已经完成了支付，需要为用户退款
        if(orders.getPayStatus().equals(Orders.PAID)){
            orders1.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders1);
    }

    @Override
    public void cancelAdmin(OrdersCancelDTO ordersCancelDTO) {
        // - 取消订单其实就是将订单状态修改为“已取消”
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        // - 商家取消订单时需要指定取消原因
        Orders build = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        // - 商家取消订单时，如果用户已经完成了支付，需要为用户退款
        if(orders.getPayStatus().equals(Orders.PAID)){
            build.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(build);
    }

    @Override
    public void delivery(Long id) {
        // 业务规则：
        // - 派送订单其实就是将订单状态修改为“派送中”
        // - 只有状态为“待派送”的订单可以执行派送订单操作

        Orders orders = orderMapper.getById(id);
        if(orders == null || !orders.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders build = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(build);
    }

    @Override
    public void complete(Long id) {
        // 业务规则：
        // - - 完成订单其实就是将订单状态修改为“已完成”
        // - - 只有状态为“派送中”的订单可以执行订单完成操作

        Orders orders = orderMapper.getById(id);
        if(orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders build = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(build);
    }

}
