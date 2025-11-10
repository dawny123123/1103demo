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
 * OrderDAO类的单元测试
 */
class OrderDAOTest {
    
    private OrderDAO orderDAO;
    
    @BeforeEach
    void setUp() {
        orderDAO = new OrderDAO();
    }
    
    @Test
    @DisplayName("创建订单 - 正常创建应返回true")
    void testCreateOrder_NormalCreation_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        order.setDescription("测试订单描述");
        
        // 执行测试
        boolean result = orderDAO.createOrder(order);
        
        // 验证结果
        assertTrue(result, "创建订单应该成功");
    }
    
    @Test
    @DisplayName("创建订单 - 重复创建应返回false")
    void testCreateOrder_DuplicateCreation_ReturnsFalse() {
        // 准备测试数据
        Order order1 = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        Order order2 = new Order("12345", "user123", "product789", 3, new BigDecimal("199.99"));
        
        // 先创建一个订单
        boolean result1 = orderDAO.createOrder(order1);
        
        // 尝试创建相同ID的订单
        boolean result2 = orderDAO.createOrder(order2);
        
        // 验证结果
        assertTrue(result1, "第一次创建应该成功");
        assertFalse(result2, "重复创建应该失败");
    }
    
    @Test
    @DisplayName("获取订单 - 存在的订单应返回正确对象")
    void testGetOrder_ExistingOrder_ReturnsCorrectOrder() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        order.setDescription("测试订单描述");
        orderDAO.createOrder(order);
        
        // 执行测试
        Order result = orderDAO.getOrder("12345");
        
        // 验证结果
        assertNotNull(result, "应该返回非null对象");
        assertEquals("12345", result.getOrderId(), "订单ID应该匹配");
        assertEquals("user123", result.getUserId(), "用户ID应该匹配");
        assertEquals("product456", result.getProductId(), "商品ID应该匹配");
        assertEquals(2, result.getQuantity(), "数量应该匹配");
        assertEquals(new BigDecimal("99.99"), result.getTotalAmount(), "总金额应该匹配");
        assertEquals("测试订单描述", result.getDescription(), "描述应该匹配");
    }
    
    @Test
    @DisplayName("获取订单 - 不存在的订单应返回null")
    void testGetOrder_NonExistingOrder_ReturnsNull() {
        // 执行测试
        Order result = orderDAO.getOrder("nonexistent");
        
        // 验证结果
        assertNull(result, "不存在的订单应该返回null");
    }
    
    @Test
    @DisplayName("更新订单 - 存在的订单应返回true")
    void testUpdateOrder_ExistingOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        orderDAO.createOrder(order);
        
        // 修改订单信息
        order.setQuantity(5);
        order.setTotalAmount(new BigDecimal("199.99"));
        order.setDescription("更新后的描述");
        
        // 执行测试
        boolean result = orderDAO.updateOrder(order);
        
        // 验证结果
        assertTrue(result, "更新应该成功");
        
        // 验证更新后的数据
        Order updatedOrder = orderDAO.getOrder("12345");
        assertEquals(5, updatedOrder.getQuantity(), "数量应该已更新");
        assertEquals(new BigDecimal("199.99"), updatedOrder.getTotalAmount(), "总金额应该已更新");
        assertEquals("更新后的描述", updatedOrder.getDescription(), "描述应该已更新");
    }
    
    @Test
    @DisplayName("更新订单 - 不存在的订单应返回false")
    void testUpdateOrder_NonExistingOrder_ReturnsFalse() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        
        // 执行测试
        boolean result = orderDAO.updateOrder(order);
        
        // 验证结果
        assertFalse(result, "更新不存在的订单应该失败");
    }
    
    @Test
    @DisplayName("删除订单 - 存在的订单应返回true")
    void testDeleteOrder_ExistingOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        orderDAO.createOrder(order);
        
        // 执行测试
        boolean result = orderDAO.deleteOrder("12345");
        
        // 验证结果
        assertTrue(result, "删除应该成功");
        assertNull(orderDAO.getOrder("12345"), "订单应该已被删除");
    }
    
    @Test
    @DisplayName("删除订单 - 不存在的订单应返回false")
    void testDeleteOrder_NonExistingOrder_ReturnsFalse() {
        // 执行测试
        boolean result = orderDAO.deleteOrder("nonexistent");
        
        // 验证结果
        assertFalse(result, "删除不存在的订单应该失败");
    }
    
    @Test
    @DisplayName("按用户ID查询订单 - 按创建时间降序排列")
    void testGetOrdersByUserId_SortedByCreateTimeDesc() {
        // 准备测试数据
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 10, 10, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 15, 10, 0);
        
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"), 0, "订单1描述", time1, null, null);
        Order order2 = new Order("order002", "user001", "prod002", 2, new BigDecimal("200.00"), 1, "订单2描述", time2, null, null);
        Order order3 = new Order("order003", "user001", "prod003", 3, new BigDecimal("300.00"), 0, "订单3描述", time3, null, null);
        
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        orderDAO.createOrder(order3);
        
        // 执行查询
        List<Order> result = orderDAO.getOrdersByUserId("user001");
        
        // 验证结果按时间降序排列
        assertEquals(3, result.size(), "应该有3个订单");
        assertEquals("order003", result.get(0).getOrderId(), "2024-01-15的订单应该排在第一位");
        assertEquals("order002", result.get(1).getOrderId(), "2024-01-10的订单应该排在第二位");
        assertEquals("order001", result.get(2).getOrderId(), "2024-01-01的订单应该排在第三位");
    }
    
    @Test
    @DisplayName("createTime为null的订单 - null值应排在末尾")
    void testGetOrdersByUserId_WithNullCreateTime() {
        // 准备测试数据，确保时间有明显差异
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0); // 比time1晚1天
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 3, 10, 0); // 比time2晚1天
        
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("100.00"), 0, "订单1描述", time1, null, null);
        Order order2 = new Order("order002", "user001", "prod002", 2, new BigDecimal("200.00"), 1, "订单2描述", time3, null, null);
        Order order3 = new Order("order003", "user001", "prod003", 3, new BigDecimal("300.00"), 0, "订单3描述", time2, null, null);
        
        // 先将订单添加到DAO中
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        orderDAO.createOrder(order3);
        
        // 然后显式设置order2的createTime为null（绕过createOrder中的自动设置）
        order2.setCreateTime(null);
        
        // 执行查询
        List<Order> result = orderDAO.getOrdersByUserId("user001");
        
        // 验证结果
        assertEquals(3, result.size(), "应该有3个订单");
        // 由于order2的createTime被设置为null，它会被排在最后
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