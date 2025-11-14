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
        // 准备测试数据 - 灵码专属版: 1个LIC * 159 = 159元, 研发规模10人
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
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
        Order order1 = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
        Order order2 = new Order("12345", "客户A", "QODER", 20, 2, new BigDecimal("280.00"));
        
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
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
        order.setDescription("测试订单描述");
        orderDAO.createOrder(order);
        
        // 执行测试
        Order result = orderDAO.getOrder("12345");
        
        // 验证结果
        assertNotNull(result, "应该返回非null对象");
        assertEquals("12345", result.getCid(), "CID应该匹配");
        assertEquals("客户A", result.getCustomerName(), "客户名称应该匹配");
        assertEquals("LINGMA_EXCLUSIVE", result.getProductVersion(), "产品版本应该匹配");
        assertEquals(10, result.getDevScale(), "研发规模应该匹配");
        assertEquals(1, result.getPurchasedLicCount(), "已购LIC数应该匹配");
        assertEquals(new BigDecimal("159.00"), result.getTotalAmount(), "总金额应该匹配");
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
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
        orderDAO.createOrder(order);
        
        // 修改订单信息
        order.setDevScale(20);
        order.setPurchasedLicCount(2);
        order.setTotalAmount(new BigDecimal("318.00"));
        order.setDescription("更新后的描述");
        
        // 执行测试
        boolean result = orderDAO.updateOrder(order);
        
        // 验证结果
        assertTrue(result, "更新应该成功");
        
        // 验证更新后的数据
        Order updatedOrder = orderDAO.getOrder("12345");
        assertEquals(20, updatedOrder.getDevScale(), "研发规模应该已更新");
        assertEquals(2, updatedOrder.getPurchasedLicCount(), "已购LIC数应该已更新");
        assertEquals(new BigDecimal("318.00"), updatedOrder.getTotalAmount(), "总金额应该已更新");
        assertEquals("更新后的描述", updatedOrder.getDescription(), "描述应该已更新");
    }
    
    @Test
    @DisplayName("更新订单 - 不存在的订单应返回false")
    void testUpdateOrder_NonExistingOrder_ReturnsFalse() {
        // 准备测试数据
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
        
        // 执行测试
        boolean result = orderDAO.updateOrder(order);
        
        // 验证结果
        assertFalse(result, "更新不存在的订单应该失败");
    }
    
    @Test
    @DisplayName("删除订单 - 存在的订单应返回true")
    void testDeleteOrder_ExistingOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
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
        
        Order order1 = new Order("order001", "客户A", "QODER", 10, 1, new BigDecimal("140.00"), 0, "订单1描述", time1, null, null);
        Order order2 = new Order("order002", "客户A", "LINGMA_ENTERPRISE", 20, 2, new BigDecimal("158.00"), 1, "订单2描述", time2, null, null);
        Order order3 = new Order("order003", "客户A", "LINGMA_EXCLUSIVE", 30, 2, new BigDecimal("318.00"), 0, "订单3描述", time3, null, null);
        
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        orderDAO.createOrder(order3);
        
        // 执行查询
        List<Order> result = orderDAO.getOrdersByUserId("user001");
        
        // 验证结果按时间降序排列
        assertEquals(3, result.size(), "应该有3个订单");
        assertEquals("order003", result.get(0).getCid(), "2024-01-15的订单应该排在第一位");
        assertEquals("order002", result.get(1).getCid(), "2024-01-10的订单应该排在第二位");
        assertEquals("order001", result.get(2).getCid(), "2024-01-01的订单应该排在第三位");
    }
    
    @Test
    @DisplayName("createTime为null的订单 - null值应排在末尾")
    void testGetOrdersByUserId_WithNullCreateTime() {
        // 准备测试数据，确保时间有明显差异
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0); // 比time1晚1天
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 3, 10, 0); // 比time2晚1天
        
        Order order1 = new Order("order001", "客户A", "QODER", 10, 1, new BigDecimal("140.00"), 0, "订单1描述", time1, null, null);
        Order order2 = new Order("order002", "客户A", "LINGMA_ENTERPRISE", 20, 2, new BigDecimal("158.00"), 1, "订单2描述", time3, null, null);
        Order order3 = new Order("order003", "客户A", "LINGMA_EXCLUSIVE", 30, 2, new BigDecimal("318.00"), 0, "订单3描述", time2, null, null);
        
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
        assertEquals("order003", result.get(0).getCid(), "时间最新的非null订单排第一");
        assertEquals("order001", result.get(1).getCid(), "时间较早的非null订单排第二");
        assertEquals("order002", result.get(2).getCid(), "createTime为null的订单应该排在末尾");
    }
    
    @Test
    @DisplayName("多用户订单隔离性验证 - 查询应只返回指定用户的订单")
    void testGetOrdersByUserId_MultiUserIsolation() {
        // 准备测试数据 - 创建多个客户的订单
        Order order1 = new Order("order001", "客户A", "QODER", 10, 1, new BigDecimal("140.00"));
        Order order2 = new Order("order002", "客户B", "LINGMA_ENTERPRISE", 20, 2, new BigDecimal("158.00"));
        Order order3 = new Order("order003", "客户A", "LINGMA_EXCLUSIVE", 30, 2, new BigDecimal("318.00"));
        Order order4 = new Order("order004", "客户C", "QODER", 40, 3, new BigDecimal("420.00"));
        
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        orderDAO.createOrder(order3);
        orderDAO.createOrder(order4);
        
        // 验证客户A的订单
        List<Order> customerAOrders = orderDAO.getOrdersByUserId("客户A");
        assertEquals(2, customerAOrders.size(), "客户A应该有2个订单");
        assertTrue(customerAOrders.stream().allMatch(o -> "客户A".equals(o.getCustomerName())), 
                  "所有订单应该属于客户A");
        
        // 验证客户B的订单
        List<Order> customerBOrders = orderDAO.getOrdersByUserId("客户B");
        assertEquals(1, customerBOrders.size(), "客户B应该有1个订单");
        assertEquals("客户B", customerBOrders.get(0).getCustomerName(), "订单应该属于客户B");
        
        // 验证客户C的订单
        List<Order> customerCOrders = orderDAO.getOrdersByUserId("客户C");
        assertEquals(1, customerCOrders.size(), "客户C应该有1个订单");
        assertEquals("客户C", customerCOrders.get(0).getCustomerName(), "订单应该属于客户C");
    }
}