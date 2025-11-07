package com.example.demo.service;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OrderService类的单元测试
 */
class OrderServiceTest {

    // 创建一个自定义的OrderService，用于注入mock的OrderDAO
    private OrderService orderService;
    
    @Mock
    private OrderDAO orderDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 创建OrderService实例并注入mock的OrderDAO
        orderService = new OrderService(orderDAO);
    }

    /**
     * 测试创建有效订单
     */
    @Test
    void testCreateOrder_ValidOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // 执行测试
        boolean result = orderService.createOrder(order);
        
        // 验证结果
        assertTrue(result);
        
        // 验证createOrder方法是否被正确调用一次
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试创建数量<=0的订单，应该抛出异常
     */
    @Test
    void testCreateOrder_QuantityLessThanOrEqualToZero_ThrowsException() {
        // 准备测试数据 - 数量为0
        Order order = new Order("12345", "user123", "product456", 0, new BigDecimal("99.99"));
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order)
        );
        
        // 验证异常信息
        assertEquals("购买数量必须大于0", exception.getMessage());
        
        // 验证createOrder方法从未被调用
        verify(orderDAO, never()).createOrder(order);
    }

    /**
     * 测试获取订单
     */
    @Test
    void testGetOrder_WhenOrderExists_ShouldReturnOrder() {
        // Given
        String orderId = "12345";
        Order expectedOrder = new Order(orderId, "user123", "product456", 2, new BigDecimal("99.99"));

        when(orderDAO.getOrder(orderId)).thenReturn(expectedOrder);

        // When
        Order actualOrder = orderService.getOrder(orderId);

        // Then
        assertNotNull(actualOrder);
        assertEquals(orderId, actualOrder.getOrderId());
        assertEquals(new BigDecimal("99.99"), actualOrder.getTotalAmount());
        verify(orderDAO, times(1)).getOrder(orderId);
    }

    /**
     * 测试更新订单
     */
    @Test
    void testUpdateOrder_ValidOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("99.99"));
        order.setStatus(2); // 设置为已发货状态
        
        // 配置mock行为
        when(orderDAO.updateOrder(order)).thenReturn(true);
        
        // 执行测试
        boolean result = orderService.updateOrder(order);
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(orderDAO, times(1)).updateOrder(order);
    }

    /**
     * 测试按用户ID查询订单列表 - 正常查询
     */
    @Test
    @DisplayName("正常查询 - 应返回用户的订单列表")
    void testGetOrdersByUserId_ValidUserId_ReturnsOrderList() {
        // 准备测试数据
        String userId = "user001";
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0);
        
        Order order1 = new Order("order001", userId, "prod001", 1, new BigDecimal("100.00"), 0, time1, null, null);
        Order order2 = new Order("order002", userId, "prod002", 2, new BigDecimal("200.00"), 1, time2, null, null);
        List<Order> expectedOrders = Arrays.asList(order2, order1); // 按时间降序
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(userId)).thenReturn(expectedOrders);
        
        // 执行测试
        List<Order> result = orderService.getOrdersByUserId(userId);
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "应该返回2个订单");
        assertEquals("order002", result.get(0).getOrderId(), "第一个订单应该是order002");
        assertEquals("order001", result.get(1).getOrderId(), "第二个订单应该是order001");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(userId);
    }

    /**
     * 测试按用户ID查询订单列表 - userId为null
     */
    @Test
    @DisplayName("userId为null - 应抛出IllegalArgumentException")
    void testGetOrdersByUserId_NullUserId_ThrowsException() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId(null)
        );
        
        // 验证异常信息
        assertEquals("用户ID不能为空", exception.getMessage());
        
        // 验证DAO方法从未被调用
        verify(orderDAO, never()).getOrdersByUserId(any());
    }

    /**
     * 测试按用户ID查询订单列表 - userId为空字符串
     */
    @Test
    @DisplayName("userId为空字符串 - 应抛出IllegalArgumentException")
    void testGetOrdersByUserId_EmptyUserId_ThrowsException() {
        // 测试空字符串
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId("")
        );
        assertEquals("用户ID不能为空", exception1.getMessage());
        
        // 测试只包含空格的字符串
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId("   ")
        );
        assertEquals("用户ID不能为空", exception2.getMessage());
        
        // 验证DAO方法从未被调用
        verify(orderDAO, never()).getOrdersByUserId(any());
    }

    /**
     * 测试按用户ID查询订单列表 - 用户无订单
     */
    @Test
    @DisplayName("用户无订单 - 应返回空列表")
    void testGetOrdersByUserId_NoOrders_ReturnsEmptyList() {
        // 准备测试数据
        String userId = "user999";
        List<Order> emptyList = new ArrayList<>();
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(userId)).thenReturn(emptyList);
        
        // 执行测试
        List<Order> result = orderService.getOrdersByUserId(userId);
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertTrue(result.isEmpty(), "应该返回空列表");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(userId);
    }
}