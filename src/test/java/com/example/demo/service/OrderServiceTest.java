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

    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试创建有效订单
     */
    @Test
    void testCreateOrder_ValidOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("318.00"));
        order.setDescription("测试订单描述");
        
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
        Order order = new Order("12345", "user123", "product456", 0, new BigDecimal("0.00"));
        order.setDescription("测试订单描述");
        
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
        Order expectedOrder = new Order(orderId, "user123", "product456", 2, new BigDecimal("318.00"));
        expectedOrder.setDescription("测试订单描述");

        when(orderDAO.getOrder(orderId)).thenReturn(expectedOrder);

        // When
        Order actualOrder = orderService.getOrder(orderId);

        // Then
        assertNotNull(actualOrder);
        assertEquals(orderId, actualOrder.getOrderId());
        assertEquals(new BigDecimal("318.00"), actualOrder.getTotalAmount());
        assertEquals("测试订单描述", actualOrder.getDescription());
        verify(orderDAO, times(1)).getOrder(orderId);
    }

    /**
     * 测试更新订单
     */
    @Test
    void testUpdateOrder_ValidOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("318.00"));
        order.setStatus(2); // 设置为已发货状态
        order.setDescription("测试订单描述");
        
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
        
        Order order1 = new Order("order001", userId, "prod001", 1, new BigDecimal("159.00"), 0, "订单1描述", time1, null, null);
        Order order2 = new Order("order002", userId, "prod002", 2, new BigDecimal("318.00"), 1, "订单2描述", time2, null, null);
        List<Order> expectedOrders = Arrays.asList(order2, order1); // 按时间降序
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(userId)).thenReturn(expectedOrders);
        
        // 执行测试
        List<Order> result = orderService.getOrdersByUserId(userId);
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "应该返回2个订单");
        assertEquals("order002", result.get(0).getOrderId(), "第一个订单应该是order002");
        assertEquals(new BigDecimal("318.00"), result.get(0).getTotalAmount(), "第一个订单的金额应该是318.00");
        assertEquals("订单2描述", result.get(0).getDescription(), "第一个订单的描述应该是'订单2描述'");
        assertEquals("order001", result.get(1).getOrderId(), "第二个订单应该是order001");
        assertEquals(new BigDecimal("159.00"), result.get(1).getTotalAmount(), "第二个订单的金额应该是159.00");
        assertEquals("订单1描述", result.get(1).getDescription(), "第二个订单的描述应该是'订单1描述'");
        
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

    /**
     * 测试总金额自动计算功能 - 数量为1
     */
    @Test
    @DisplayName("总金额自动计算 - 数量为1")
    void testTotalAmountCalculation_QuantityOne() {
        // 准备测试数据
        Order order = new Order("order001", "user001", "prod001", 1, new BigDecimal("159.00"));
        order.setDescription("测试订单");
        
        // 验证总金额是否正确计算
        assertEquals(new BigDecimal("159.00"), order.getTotalAmount(), "数量为1时，总金额应为159.00");
    }

    /**
     * 测试总金额自动计算功能 - 数量为3
     */
    @Test
    @DisplayName("总金额自动计算 - 数量为3")
    void testTotalAmountCalculation_QuantityThree() {
        // 准备测试数据
        Order order = new Order("order002", "user001", "prod001", 3, new BigDecimal("477.00"));
        order.setDescription("测试订单");
        
        // 验证总金额是否正确计算
        assertEquals(new BigDecimal("477.00"), order.getTotalAmount(), "数量为3时，总金额应为477.00");
    }

    /**
     * 测试总金额自动计算功能 - 数量为5
     */
    @Test
    @DisplayName("总金额自动计算 - 数量为5")
    void testTotalAmountCalculation_QuantityFive() {
        // 准备测试数据
        Order order = new Order("order003", "user001", "prod001", 5, new BigDecimal("795.00"));
        order.setDescription("测试订单");
        
        // 验证总金额是否正确计算
        assertEquals(new BigDecimal("795.00"), order.getTotalAmount(), "数量为5时，总金额应为795.00");
    }

    /**
     * 测试总金额自动计算功能 - 数量为10
     */
    @Test
    @DisplayName("总金额自动计算 - 数量为10")
    void testTotalAmountCalculation_QuantityTen() {
        // 准备测试数据
        Order order = new Order("order004", "user001", "prod001", 10, new BigDecimal("1590.00"));
        order.setDescription("测试订单");
        
        // 验证总金额是否正确计算
        assertEquals(new BigDecimal("1590.00"), order.getTotalAmount(), "数量为10时，总金额应为1590.00");
    }
}