package com.example.demo;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 主程序入口
 */
public class App {
    public static void main(String[] args) {
        // 初始化数据库连接
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.initTable();
        System.out.println("✅ 数据库表初始化完成");
        
        // 创建订单服务
        OrderService orderService = new OrderService();
        
        // 创建新订单
        Order order = new Order(
            "O001", 
            "U100", 
            "P200", 
            2, 
            new BigDecimal("99.99")
        );
        boolean created = orderService.createOrder(order);
        System.out.println(created ? "✅ 订单创建成功" : "❌ 订单创建失败");
        
        // 保存到数据库
        orderDAO.saveToDatabase();
        System.out.println("✅ 数据已持久化到SQLite");
        
        // 查询订单
        Order retrievedOrder = orderService.getOrder("O001");
        System.out.println("🔍 查询结果: " + (retrievedOrder != null ? retrievedOrder.toString() : "未找到"));
        
        // 更新订单状态
        if (retrievedOrder != null) {
            retrievedOrder.setStatus(1); // 设置为已支付
            retrievedOrder.setPayTime(LocalDateTime.now());
            boolean updated = orderService.updateOrder(retrievedOrder);
            System.out.println(updated ? "✅ 订单更新成功" : "❌ 订单更新失败");
            
            // 保存更新
            orderDAO.saveToDatabase();
            System.out.println("✅ 数据更新已持久化");
            
            // 删除订单
            boolean deleted = orderService.deleteOrder("O001");
            System.out.println(deleted ? "✅ 订单删除成功" : "❌ 订单删除失败");
            
            // 保存删除
            orderDAO.saveToDatabase();
            System.out.println("✅ 删除操作已持久化");
        }
    }
}