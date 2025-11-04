package com.example.demo.service;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

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
        orderService = new OrderService() {
            @Override
            protected OrderDAO getOrderDAO() {
                return orderDAO;
            }
        };
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
}