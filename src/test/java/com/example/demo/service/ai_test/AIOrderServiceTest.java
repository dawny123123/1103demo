package com.example.demo.service.ai_test;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
 * OrderService类的单元测试 - 遵循AI生成测试用例文档和单元测试编码规范
 */
@DisplayName("OrderService单元测试")
class AIOrderServiceTest {

    @Mock
    private OrderDAO orderDAO;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用构造函数注入方式创建OrderService实例
        orderService = new OrderService(orderDAO);
    }

    /**
     * 测试创建有效订单 - 正常流程
     */
    @Test
    @DisplayName("TC001: 测试创建有效订单")
    void testCreateOrder_ValidOrder_ReturnsTrue() {
        // Arrange
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("318.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "创建有效订单应返回true");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试创建数量<=0的订单，应该抛出异常
     */
    @Test
    @DisplayName("TC002: 测试创建数量<=0的订单，应该抛出异常")
    void testCreateOrder_QuantityLessThanOrEqualToZero_ThrowsException() {
        // Arrange
        Order order = new Order("12345", "user123", "product456", 0, new BigDecimal("0.00"));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order),
            "创建数量<=0的订单应抛出IllegalArgumentException"
        );
        
        // 验证异常信息
        assertEquals("购买数量必须大于0", exception.getMessage(), "异常消息应为'购买数量必须大于0'");
        
        // 验证createOrder方法从未被调用
        verify(orderDAO, never()).createOrder(order);
    }

    /**
     * 测试创建金额<=0的订单，应该抛出异常
     */
    @Test
    @DisplayName("TC003: 测试创建金额<=0的订单，应该抛出异常")
    void testCreateOrder_TotalAmountLessThanOrEqualToZero_ThrowsException() {
        // Arrange
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("0.00"));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order),
            "创建金额<=0的订单应抛出IllegalArgumentException"
        );
        
        // 验证异常信息
        assertEquals("订单金额必须大于0", exception.getMessage(), "异常消息应为'订单金额必须大于0'");
        
        // 验证createOrder方法从未被调用
        verify(orderDAO, never()).createOrder(order);
    }

    /**
     * 测试获取订单 - 订单存在
     */
    @Test
    @DisplayName("TC004: 测试获取存在的订单")
    void testGetOrder_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        String orderId = "12345";
        Order expectedOrder = new Order(orderId, "user123", "product456", 2, new BigDecimal("318.00"));

        when(orderDAO.getOrder(orderId)).thenReturn(expectedOrder);

        // Act
        Order actualOrder = orderService.getOrder(orderId);

        // Assert
        assertNotNull(actualOrder, "获取存在的订单应返回非null对象");
        assertEquals(orderId, actualOrder.getOrderId(), "订单ID应匹配");
        assertEquals(new BigDecimal("318.00"), actualOrder.getTotalAmount(), "订单金额应匹配");
        verify(orderDAO, times(1)).getOrder(orderId);
    }

    /**
     * 测试更新订单 - 正常流程
     */
    @Test
    @DisplayName("TC005: 测试更新订单")
    void testUpdateOrder_ValidOrder_ReturnsTrue() {
        // Arrange
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("318.00"));
        order.setStatus(2); // 设置为已发货状态
        
        // 配置mock行为
        when(orderDAO.updateOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.updateOrder(order);
        
        // Assert
        assertTrue(result, "更新有效订单应返回true");
        verify(orderDAO, times(1)).updateOrder(order);
    }

    /**
     * 测试更新已完成订单应返回false
     */
    @Test
    @DisplayName("TC006: 测试更新已完成订单应返回false")
    void testUpdateOrder_CompletedOrderCannotBeModified_ReturnsFalse() {
        // Arrange
        Order order = new Order("12345", "user123", "product456", 2, new BigDecimal("318.00"));
        order.setStatus(3); // 设置为已完成状态
        
        Order existingOrder = new Order("12345", "user123", "product456", 2, new BigDecimal("318.00"));
        existingOrder.setStatus(3); // 数据库中也是已完成状态
        
        // 配置mock行为
        when(orderDAO.getOrder("12345")).thenReturn(existingOrder);
        
        // Act
        boolean result = orderService.updateOrder(order);
        
        // Assert
        assertFalse(result, "更新已完成订单应返回false");
        verify(orderDAO, times(1)).getOrder("12345");
    }

    /**
     * 测试删除订单 - 正常流程
     */
    @Test
    @DisplayName("TC007: 测试删除订单")
    void testDeleteOrder_ValidOrder_ReturnsTrue() {
        // Arrange
        String orderId = "12345";
        Order order = new Order(orderId, "user123", "product456", 2, new BigDecimal("318.00"));
        order.setStatus(0); // 待支付状态
        
        // 配置mock行为
        when(orderDAO.getOrder(orderId)).thenReturn(order);
        when(orderDAO.deleteOrder(orderId)).thenReturn(true);
        
        // Act
        boolean result = orderService.deleteOrder(orderId);
        
        // Assert
        assertTrue(result, "删除待支付订单应返回true");
        verify(orderDAO, times(1)).getOrder(orderId);
        verify(orderDAO, times(1)).deleteOrder(orderId);
    }

    /**
     * 测试删除已支付订单应返回false
     */
    @Test
    @DisplayName("TC008: 测试删除已支付订单应返回false")
    void testDeleteOrder_PaidOrderCannotBeDeleted_ReturnsFalse() {
        // Arrange
        String orderId = "12345";
        Order order = new Order(orderId, "user123", "product456", 2, new BigDecimal("318.00"));
        order.setStatus(1); // 已支付状态
        
        // 配置mock行为
        when(orderDAO.getOrder(orderId)).thenReturn(order);
        
        // Act
        boolean result = orderService.deleteOrder(orderId);
        
        // Assert
        assertFalse(result, "删除已支付订单应返回false");
        verify(orderDAO, times(1)).getOrder(orderId);
        verify(orderDAO, never()).deleteOrder(orderId);
    }

    /**
     * 测试按用户ID查询订单列表 - 正常查询
     */
    @Test
    @DisplayName("TC009: 测试按用户ID查询订单列表 - 正常查询")
    void testGetOrdersByUserId_ValidUserId_ReturnsOrderList() {
        // Arrange
        String userId = "user001";
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0);
        
        Order order1 = new Order("order001", userId, "prod001", 1, new BigDecimal("159.00"), 0, null, time1, null, null);
        Order order2 = new Order("order002", userId, "prod002", 2, new BigDecimal("318.00"), 1, null, time2, null, null);
        List<Order> expectedOrders = Arrays.asList(order2, order1); // 按时间降序
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(userId)).thenReturn(expectedOrders);
        
        // Act
        List<Order> result = orderService.getOrdersByUserId(userId);
        
        // Assert
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "应该返回2个订单");
        assertEquals("order002", result.get(0).getOrderId(), "第一个订单应该是order002");
        assertEquals(new BigDecimal("318.00"), result.get(0).getTotalAmount(), "第一个订单的金额应该是318.00");
        assertEquals("order001", result.get(1).getOrderId(), "第二个订单应该是order001");
        assertEquals(new BigDecimal("159.00"), result.get(1).getTotalAmount(), "第二个订单的金额应该是159.00");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(userId);
    }

    /**
     * 测试按用户ID查询订单列表 - userId为null
     */
    @Test
    @DisplayName("TC010: 测试按用户ID查询订单列表 - userId为null")
    void testGetOrdersByUserId_NullUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId(null),
            "userId为null时应抛出IllegalArgumentException"
        );
        
        // 验证异常信息
        assertEquals("用户ID不能为空", exception.getMessage(), "异常消息应为'用户ID不能为空'");
        
        // 验证DAO方法从未被调用
        verify(orderDAO, never()).getOrdersByUserId(any());
    }

    /**
     * 测试按用户ID查询订单列表 - userId为空字符串
     */
    @Test
    @DisplayName("TC011: 测试按用户ID查询订单列表 - userId为空字符串")
    void testGetOrdersByUserId_EmptyUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId(""),
            "userId为空字符串时应抛出IllegalArgumentException"
        );
        assertEquals("用户ID不能为空", exception1.getMessage(), "异常消息应为'用户ID不能为空'");
        
        // 验证DAO方法从未被调用
        verify(orderDAO, never()).getOrdersByUserId(any());
    }

    /**
     * 测试按用户ID查询订单列表 - userId为空白字符串
     */
    @Test
    @DisplayName("TC012: 测试按用户ID查询订单列表 - userId为空白字符串")
    void testGetOrdersByUserId_WhitespaceUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId("   "),
            "userId为空白字符串时应抛出IllegalArgumentException"
        );
        assertEquals("用户ID不能为空", exception.getMessage(), "异常消息应为'用户ID不能为空'");
        
        // 验证DAO方法从未被调用
        verify(orderDAO, never()).getOrdersByUserId(any());
    }

    /**
     * 测试按用户ID查询订单列表 - 用户无订单
     */
    @Test
    @DisplayName("TC013: 测试按用户ID查询订单列表 - 用户无订单")
    void testGetOrdersByUserId_NoOrders_ReturnsEmptyList() {
        // Arrange
        String userId = "user999";
        List<Order> emptyList = new ArrayList<>();
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(userId)).thenReturn(emptyList);
        
        // Act
        List<Order> result = orderService.getOrdersByUserId(userId);
        
        // Assert
        assertNotNull(result, "结果不应该为null");
        assertTrue(result.isEmpty(), "应该返回空列表");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(userId);
    }

    /**
     * 测试获取所有订单列表
     */
    @Test
    @DisplayName("TC014: 测试获取所有订单列表")
    void testGetAllOrders_ReturnsAllOrders() {
        // Arrange
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0);
        
        Order order1 = new Order("order001", "user001", "prod001", 1, new BigDecimal("159.00"), 0, null, time1, null, null);
        Order order2 = new Order("order002", "user002", "prod002", 2, new BigDecimal("318.00"), 1, null, time2, null, null);
        List<Order> expectedOrders = Arrays.asList(order2, order1); // 按时间降序
        
        // 配置mock行为
        when(orderDAO.getAllOrders()).thenReturn(expectedOrders);
        
        // Act
        List<Order> result = orderService.getAllOrders();
        
        // Assert
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "应该返回2个订单");
        assertEquals("order002", result.get(0).getOrderId(), "第一个订单应该是order002");
        assertEquals(new BigDecimal("318.00"), result.get(0).getTotalAmount(), "第一个订单的金额应该是318.00");
        assertEquals("order001", result.get(1).getOrderId(), "第二个订单应该是order001");
        assertEquals(new BigDecimal("159.00"), result.get(1).getTotalAmount(), "第二个订单的金额应该是159.00");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getAllOrders();
    }

    /**
     * 测试总金额自动计算功能 - 数量为1
     */
    @Test
    @DisplayName("TC015: 测试总金额自动计算功能 - 数量为1")
    void testTotalAmountCalculation_QuantityOne() {
        // Arrange
        Order order = new Order("order001", "user001", "prod001", 1, new BigDecimal("159.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("159.00"), order.getTotalAmount(), "数量为1时，总金额应为159.00");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试总金额自动计算功能 - 数量为3
     */
    @Test
    @DisplayName("TC016: 测试总金额自动计算功能 - 数量为3")
    void testTotalAmountCalculation_QuantityThree() {
        // Arrange
        Order order = new Order("order002", "user001", "prod001", 3, new BigDecimal("477.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("477.00"), order.getTotalAmount(), "数量为3时，总金额应为477.00");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试总金额自动计算功能 - 数量为5
     */
    @Test
    @DisplayName("TC017: 测试总金额自动计算功能 - 数量为5")
    void testTotalAmountCalculation_QuantityFive() {
        // Arrange
        Order order = new Order("order003", "user001", "prod001", 5, new BigDecimal("795.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("795.00"), order.getTotalAmount(), "数量为5时，总金额应为795.00");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试总金额自动计算功能 - 数量为10
     */
    @Test
    @DisplayName("TC018: 测试总金额自动计算功能 - 数量为10")
    void testTotalAmountCalculation_QuantityTen() {
        // Arrange
        Order order = new Order("order004", "user001", "prod001", 10, new BigDecimal("1590.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("1590.00"), order.getTotalAmount(), "数量为10时，总金额应为1590.00");
        verify(orderDAO, times(1)).createOrder(order);
    }
}