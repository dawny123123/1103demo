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
        // Arrange - 灵码专属版: 2个LIC * 159 = 318元
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "创建有效订单应返回true");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试创建已购LIC数<=0的订单，应该抛出异常
     */
    @Test
    @DisplayName("TC002: 测试创建已购LIC数<=0的订单，应该抛出异常")
    void testCreateOrder_QuantityLessThanOrEqualToZero_ThrowsException() {
        // Arrange
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 0, new BigDecimal("0.00"));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order),
            "创建已购LIC数<=0的订单应抛出IllegalArgumentException"
        );
        
        // 验证异常信息
        assertEquals("已购LIC数必须大于0", exception.getMessage(), "异常消息应为'已购LIC数必须大于0'");
        
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
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("0.00"));
        
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
        String cid = "12345";
        Order expectedOrder = new Order(cid, "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));

        when(orderDAO.getOrder(cid)).thenReturn(expectedOrder);

        // Act
        Order actualOrder = orderService.getOrder(cid);

        // Assert
        assertNotNull(actualOrder, "获取存在的订单应返回非null对象");
        assertEquals(cid, actualOrder.getCid(), "CID应匹配");
        assertEquals(new BigDecimal("318.00"), actualOrder.getTotalAmount(), "订单金额应匹配");
        verify(orderDAO, times(1)).getOrder(cid);
    }

    /**
     * 测试更新订单 - 正常流程
     */
    @Test
    @DisplayName("TC005: 测试更新订单")
    void testUpdateOrder_ValidOrder_ReturnsTrue() {
        // Arrange
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
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
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
        order.setStatus(3); // 设置为已完成状态
        
        Order existingOrder = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
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
        String cid = "12345";
        Order order = new Order(cid, "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
        order.setStatus(0); // 待支付状态
        
        // 配置mock行为
        when(orderDAO.getOrder(cid)).thenReturn(order);
        when(orderDAO.deleteOrder(cid)).thenReturn(true);
        
        // Act
        boolean result = orderService.deleteOrder(cid);
        
        // Assert
        assertTrue(result, "删除待支付订单应返回true");
        verify(orderDAO, times(1)).getOrder(cid);
        verify(orderDAO, times(1)).deleteOrder(cid);
    }

    /**
     * 测试删除已支付订单应返回false
     */
    @Test
    @DisplayName("TC008: 测试删除已支付订单应返回false")
    void testDeleteOrder_PaidOrderCannotBeDeleted_ReturnsFalse() {
        // Arrange
        String cid = "12345";
        Order order = new Order(cid, "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
        order.setStatus(1); // 已支付状态
        
        // 配置mock行为
        when(orderDAO.getOrder(cid)).thenReturn(order);
        
        // Act
        boolean result = orderService.deleteOrder(cid);
        
        // Assert
        assertFalse(result, "删除已支付订单应返回false");
        verify(orderDAO, times(1)).getOrder(cid);
        verify(orderDAO, never()).deleteOrder(cid);
    }

    /**
     * 测试按用户ID查询订单列表 - 正常查询
     */
    @Test
    @DisplayName("TC009: 测试按用户ID查询订单列表 - 正常查询")
    void testGetOrdersByUserId_ValidUserId_ReturnsOrderList() {
        // Arrange
        String customerName = "客户A";
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0);
        
        Order order1 = new Order("order001", customerName, "QODER", 10, 1, new BigDecimal("140.00"), 0, null, time1, null, null);
        Order order2 = new Order("order002", customerName, "LINGMA_EXCLUSIVE", 20, 2, new BigDecimal("318.00"), 1, null, time2, null, null);
        List<Order> expectedOrders = Arrays.asList(order2, order1); // 按时间降序
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(customerName)).thenReturn(expectedOrders);
        
        // Act
        List<Order> result = orderService.getOrdersByUserId(customerName);
        
        // Assert
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "应该返回2个订单");
        assertEquals("order002", result.get(0).getCid(), "第一个订单应该是order002");
        assertEquals(new BigDecimal("318.00"), result.get(0).getTotalAmount(), "第一个订单的金额应该是318.00");
        assertEquals("order001", result.get(1).getCid(), "第二个订单应该是order001");
        assertEquals(new BigDecimal("140.00"), result.get(1).getTotalAmount(), "第二个订单的金额应该是140.00");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(customerName);
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
        assertEquals("客户名称不能为空", exception.getMessage(), "异常消息应为'客户名称不能为空'");
        
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
        assertEquals("客户名称不能为空", exception1.getMessage(), "异常消息应为'客户名称不能为空'");
        
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
        assertEquals("客户名称不能为空", exception.getMessage(), "异常消息应为'客户名称不能为空'");
        
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
        String customerName = "客户Z";
        List<Order> emptyList = new ArrayList<>();
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(customerName)).thenReturn(emptyList);
        
        // Act
        List<Order> result = orderService.getOrdersByUserId(customerName);
        
        // Assert
        assertNotNull(result, "结果不应该为null");
        assertTrue(result.isEmpty(), "应该返回空列表");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(customerName);
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
        
        Order order1 = new Order("order001", "客户A", "QODER", 10, 1, new BigDecimal("140.00"), 0, null, time1, null, null);
        Order order2 = new Order("order002", "客户B", "LINGMA_EXCLUSIVE", 20, 2, new BigDecimal("318.00"), 1, null, time2, null, null);
        List<Order> expectedOrders = Arrays.asList(order2, order1); // 按时间降序
        
        // 配置mock行为
        when(orderDAO.getAllOrders()).thenReturn(expectedOrders);
        
        // Act
        List<Order> result = orderService.getAllOrders();
        
        // Assert
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "应该返回2个订单");
        assertEquals("order002", result.get(0).getCid(), "第一个订单应该是order002");
        assertEquals(new BigDecimal("318.00"), result.get(0).getTotalAmount(), "第一个订单的金额应该是318.00");
        assertEquals("order001", result.get(1).getCid(), "第二个订单应该是order001");
        assertEquals(new BigDecimal("140.00"), result.get(1).getTotalAmount(), "第二个订单的金额应该是140.00");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getAllOrders();
    }

    /**
     * 测试总金额自动计算功能 - 灵码专属版1个LIC
     */
    @Test
    @DisplayName("TC015: 测试总金额自动计算功能 - 灵码专属版1个LIC")
    void testTotalAmountCalculation_QuantityOne() {
        // Arrange - 灵码专属版: 1个LIC * 159 = 159
        Order order = new Order("order001", "客户A", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("159.00"), order.getTotalAmount(), "灵码专属版 1个LIC，总金额应为159.00");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试总金额自动计算功能 - 灵码专属版3个LIC
     */
    @Test
    @DisplayName("TC016: 测试总金额自动计算功能 - 灵码专属版3个LIC")
    void testTotalAmountCalculation_QuantityThree() {
        // Arrange - 灵码专属版: 3个LIC * 159 = 477
        Order order = new Order("order002", "客户A", "LINGMA_EXCLUSIVE", 30, 3, new BigDecimal("477.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("477.00"), order.getTotalAmount(), "灵码专属版 3个LIC，总金额应为477.00");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试总金额自动计算功能 - 灵码企业版5个LIC
     */
    @Test
    @DisplayName("TC017: 测试总金额自动计算功能 - 灵码企业版5个LIC")
    void testTotalAmountCalculation_QuantityFive() {
        // Arrange - 灵码企业版: 5个LIC * 79 = 395
        Order order = new Order("order003", "客户A", "LINGMA_ENTERPRISE", 50, 5, new BigDecimal("395.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("395.00"), order.getTotalAmount(), "灵码企业版 5个LIC，总金额应为395.00");
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试总金额自动计算功能 - Qoder 10个LIC
     */
    @Test
    @DisplayName("TC018: 测试总金额自动计算功能 - Qoder 10个LIC")
    void testTotalAmountCalculation_QuantityTen() {
        // Arrange - Qoder: 10个LIC * 140 = 1400
        Order order = new Order("order004", "客户A", "QODER", 100, 10, new BigDecimal("1400.00"));
        
        // 配置mock行为
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // Act
        boolean result = orderService.createOrder(order);
        
        // Assert
        assertTrue(result, "订单创建应成功");
        assertEquals(new BigDecimal("1400.00"), order.getTotalAmount(), "Qoder 10个LIC，总金额应为1400.00");
        verify(orderDAO, times(1)).createOrder(order);
    }
}