package com.example.demo.service;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单服务类（演示业务逻辑与数据访问的分离）
 */
@Service
public class OrderService {
    // 注入数据访问层
    private final OrderDAO orderDAO;

    @Autowired
    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    /**
     * 创建新订单
     * @param order 待创建的订单对象
     * @return 创建成功返回true，订单已存在返回false
     */
    public boolean createOrder(Order order) {
        // 可以添加业务校验逻辑
        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("购买数量必须大于0");
        }
        if (order.getTotalAmount().compareTo(order.getTotalAmount().ZERO) <= 0) {
            throw new IllegalArgumentException("订单金额必须大于0");
        }

        return getOrderDAO().createOrder(order);
    }

    /**
     * 获取订单信息
     * @param orderId 订单ID
     * @return 返回订单对象，不存在返回null
     */
    public Order getOrder(String orderId) {
        return getOrderDAO().getOrder(orderId);
    }

    /**
     * 获取OrderDAO实例，用于测试mock
     * @return OrderDAO实例
     */
    protected OrderDAO getOrderDAO() {
        return orderDAO;
    }

    /**
     * 测试github推送
     * 更新订单信息
     * @param order 待更新的订单对象
     * @return 更新成功返回true，订单不存在返回false
     */
    public boolean updateOrder(Order order) {
        // 可以添加更新规则
        if (order.getStatus() != null && order.getStatus() == 3) {
            // 已完成订单不能修改
            Order existing = getOrderDAO().getOrder(order.getOrderId());
            if (existing != null && existing.getStatus() == 3) {
                return false;
            }
        }

        return getOrderDAO().updateOrder(order);
    }

    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 删除成功返回true，订单不存在返回false
     */
    public boolean deleteOrder(String orderId) {
        // 可以添加删除规则
        Order order = getOrderDAO().getOrder(orderId);
        if (order != null && order.getStatus() == 1) {
            // 已支付订单不能删除
            return false;
        }

        return getOrderDAO().deleteOrder(orderId);
    }

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 返回该用户的所有订单列表，按创建时间降序排列
     * @throws IllegalArgumentException 当userId为null或空字符串时抛出
     */
    public List<Order> getOrdersByUserId(String userId) {
        // 参数校验
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        return getOrderDAO().getOrdersByUserId(userId);
    }
}