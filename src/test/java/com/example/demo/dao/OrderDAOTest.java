package com.example.demo.dao;

import com.example.demo.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderDAO的单元测试类 - 测试按用户ID查询订单列表功能
 */
@DisplayName("OrderDAO按用户ID查询订单列表测试")
public class OrderDAOTest {
    
    private OrderDAO orderDAO;
    
    @BeforeEach
    void setUp() {
        orderDAO = new OrderDAO();
    }
    
    @Test
    @DisplayName("查询存在订单的用户 - 应返回该用户的所有订单")
    void testGetOrdersByUserId_WithExistingOrders() {
        // 准备测试数据 - 创建user001的多个订单
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 3, 10, 0);
        
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"), 0, time1, null, null);
        Order order2 = new Order("order002", "user001", "prod002", 2, new BigDecimal("200.00"), 1, time2, null, null);
        Order order3 = new Order("order003", "user002", "prod003", 3, new BigDecimal("300.00"), 0, time3, null, null);
        
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        orderDAO.createOrder(order3);
        
        // 执行查询
        List<Order> result = orderDAO.getOrdersByUserId("user001");
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "user001应该有2个订单");
        
        // 验证排序：最新的订单应该在最前面（按createTime降序）
        assertEquals("order002", result.get(0).getOrderId(), "最新订单应该排在第一位");
        assertEquals("order001", result.get(1).getOrderId(), "较早订单应该排在第二位");
    }
    
    @Test
    @DisplayName("查询不存在订单的用户 - 应返回空列表")
    void testGetOrdersByUserId_WithNoOrders() {
        // 准备测试数据
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"));
        orderDAO.createOrder(order1);
        
        // 执行查询 - 查询一个没有订单的用户
        List<Order> result = orderDAO.getOrdersByUserId("user999");
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertTrue(result.isEmpty(), "应该返回空列表");
    }
    
    @Test
    @DisplayName("userId为null - 应返回空列表")
    void testGetOrdersByUserId_WithNullUserId() {
        // 准备测试数据
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"));
        orderDAO.createOrder(order1);
        
        // 执行查询
        List<Order> result = orderDAO.getOrdersByUserId(null);
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertTrue(result.isEmpty(), "userId为null时应返回空列表");
    }
    
    @Test
    @DisplayName("userId为空字符串 - 应返回空列表")
    void testGetOrdersByUserId_WithEmptyUserId() {
        // 准备测试数据
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"));
        orderDAO.createOrder(order1);
        
        // 执行查询 - 测试空字符串
        List<Order> result1 = orderDAO.getOrdersByUserId("");
        assertNotNull(result1, "结果不应该为null");
        assertTrue(result1.isEmpty(), "空字符串应返回空列表");
        
        // 执行查询 - 测试只包含空格的字符串
        List<Order> result2 = orderDAO.getOrdersByUserId("   ");
        assertNotNull(result2, "结果不应该为null");
        assertTrue(result2.isEmpty(), "只包含空格的字符串应返回空列表");
    }
    
    @Test
    @DisplayName("验证排序正确性 - 应按createTime降序排列")
    void testGetOrdersByUserId_OrderingSortedByCreateTimeDesc() {
        // 准备测试数据 - 创建不同创建时间的订单
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 10, 10, 0);
        
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"), 0, time1, null, null);
        Order order2 = new Order("order002", "user001", "prod002", 2, new BigDecimal("200.00"), 1, time2, null, null);
        Order order3 = new Order("order003", "user001", "prod003", 3, new BigDecimal("300.00"), 0, time3, null, null);
        
        // 以乱序添加
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order3);
        orderDAO.createOrder(order2);
        
        // 执行查询
        List<Order> result = orderDAO.getOrdersByUserId("user001");
        
        // 验证结果按时间降序排列
        assertEquals(3, result.size(), "应该有3个订单");
        assertEquals("order002", result.get(0).getOrderId(), "2024-01-15的订单应该排在第一位");
        assertEquals("order003", result.get(1).getOrderId(), "2024-01-10的订单应该排在第二位");
        assertEquals("order001", result.get(2).getOrderId(), "2024-01-01的订单应该排在第三位");
    }
    
    @Test
    @DisplayName("createTime为null的订单 - null值应排在末尾")
    void testGetOrdersByUserId_WithNullCreateTime() {
        // 准备测试数据
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0);
        
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"), 0, time1, null, null);
        Order order2 = new Order("order002", "user001", "prod002", 2, new BigDecimal("200.00"), 1, time2, null, null);
        // 通过setter显式设置createTime为null（因为构造函数会自动设置为LocalDateTime.now()）
        order2.setCreateTime(null);
        Order order3 = new Order("order003", "user001", "prod003", 3, new BigDecimal("300.00"), 0, time2, null, null);
        
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        orderDAO.createOrder(order3);
        
        // 执行查询
        List<Order> result = orderDAO.getOrdersByUserId("user001");
        
        // 验证结果
        assertEquals(3, result.size(), "应该有3个订单");
        assertEquals("order003", result.get(0).getOrderId(), "时间最新的非null订单排第一");
        assertEquals("order001", result.get(1).getOrderId(), "时间较早的非null订单排第二");
        assertEquals("order002", result.get(2).getOrderId(), "createTime为null的订单应该排在末尾");
    }
    
    @Test
    @DisplayName("多用户订单隔离性验证 - 查询应只返回指定用户的订单")
    void testGetOrdersByUserId_MultiUserIsolation() {
        // 准备测试数据 - 创建多个用户的订单
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"));
        Order order2 = new Order("order002", "user002", "prod002", 2, new BigDecimal("200.00"));
        Order order3 = new Order("order003", "user001", "prod003", 3, new BigDecimal("300.00"));
        Order order4 = new Order("order004", "user003", "prod004", 4, new BigDecimal("400.00"));
        
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        orderDAO.createOrder(order3);
        orderDAO.createOrder(order4);
        
        // 验证user001的订单
        List<Order> user001Orders = orderDAO.getOrdersByUserId("user001");
        assertEquals(2, user001Orders.size(), "user001应该有2个订单");
        assertTrue(user001Orders.stream().allMatch(o -> "user001".equals(o.getUserId())), 
                  "所有订单应该属于user001");
        
        // 验证user002的订单
        List<Order> user002Orders = orderDAO.getOrdersByUserId("user002");
        assertEquals(1, user002Orders.size(), "user002应该有1个订单");
        assertEquals("user002", user002Orders.get(0).getUserId(), "订单应该属于user002");
        
        // 验证user003的订单
        List<Order> user003Orders = orderDAO.getOrdersByUserId("user003");
        assertEquals(1, user003Orders.size(), "user003应该有1个订单");
        assertEquals("user003", user003Orders.get(0).getUserId(), "订单应该属于user003");
    }
}
