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
     * 测试创建有效订单 - 灵码专属版
     */
    @Test
    void testCreateOrder_ValidOrder_ReturnsTrue() {
        // 准备测试数据 - 灵码专属版: 2个LIC * 159 = 318
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
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
     * 测试创建已购LIC数<=0的订单，应该抛出异常
     */
    @Test
    void testCreateOrder_PurchasedLicCountLessThanOrEqualToZero_ThrowsException() {
        // 准备测试数据 - 已购LIC数为0
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 0, new BigDecimal("0.00"));
        order.setDescription("测试订单描述");
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order)
        );
        
        // 验证异常信息
        assertEquals("已购LIC数必须大于0", exception.getMessage());
        
        // 验证createOrder方法从未被调用
        verify(orderDAO, never()).createOrder(order);
    }

    /**
     * 测试获取订单
     */
    @Test
    void testGetOrder_WhenOrderExists_ShouldReturnOrder() {
        // Given
        String cid = "12345";
        Order expectedOrder = new Order(cid, "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
        expectedOrder.setDescription("测试订单描述");

        when(orderDAO.getOrder(cid)).thenReturn(expectedOrder);

        // When
        Order actualOrder = orderService.getOrder(cid);

        // Then
        assertNotNull(actualOrder);
        assertEquals(cid, actualOrder.getCid());
        assertEquals(new BigDecimal("318.00"), actualOrder.getTotalAmount());
        assertEquals("测试订单描述", actualOrder.getDescription());
        verify(orderDAO, times(1)).getOrder(cid);
    }

    /**
     * 测试更新订单
     */
    @Test
    void testUpdateOrder_ValidOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = new Order("12345", "客户A", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("318.00"));
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
     * 测试按客户名称查询订单列表 - 正常查询
     */
    @Test
    @DisplayName("正常查询 - 应返回客户的订单列表")
    void testGetOrdersByUserId_ValidUserId_ReturnsOrderList() {
        // 准备测试数据
        String customerName = "客户A";
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 10, 0);
        
        Order order1 = new Order("order001", customerName, "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"), 0, "订单1描述", time1, null, null);
        Order order2 = new Order("order002", customerName, "LINGMA_EXCLUSIVE", 20, 2, new BigDecimal("318.00"), 1, "订单2描述", time2, null, null);
        List<Order> expectedOrders = Arrays.asList(order2, order1); // 按时间降序
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(customerName)).thenReturn(expectedOrders);
        
        // 执行测试
        List<Order> result = orderService.getOrdersByUserId(customerName);
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertEquals(2, result.size(), "应该返回2个订单");
        assertEquals("order002", result.get(0).getCid(), "第一个订单应该是order002");
        assertEquals(new BigDecimal("318.00"), result.get(0).getTotalAmount(), "第一个订单的金额应该是318.00");
        assertEquals("订单2描述", result.get(0).getDescription(), "第一个订单的描述应该是'订单2描述'");
        assertEquals("order001", result.get(1).getCid(), "第二个订单应该是order001");
        assertEquals(new BigDecimal("159.00"), result.get(1).getTotalAmount(), "第二个订单的金额应该是159.00");
        assertEquals("订单1描述", result.get(1).getDescription(), "第二个订单的描述应该是'订单1描述'");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(customerName);
    }

    /**
     * 测试按客户名称查询订单列表 - customerName为null
     */
    @Test
    @DisplayName("customerName为null - 应抛出IllegalArgumentException")
    void testGetOrdersByUserId_NullUserId_ThrowsException() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId(null)
        );
        
        // 验证异常信息
        assertEquals("客户名称不能为空", exception.getMessage());
        
        // 验证DAO方法从未被调用
        verify(orderDAO, never()).getOrdersByUserId(any());
    }

    /**
     * 测试按客户名称查询订单列表 - customerName为空字符串
     */
    @Test
    @DisplayName("customerName为空字符串 - 应抛出IllegalArgumentException")
    void testGetOrdersByUserId_EmptyUserId_ThrowsException() {
        // 测试空字符串
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId("")
        );
        assertEquals("客户名称不能为空", exception1.getMessage());
        
        // 测试只包含空格的字符串
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrdersByUserId("   ")
        );
        assertEquals("客户名称不能为空", exception2.getMessage());
        
        // 验证DAO方法从未被调用
        verify(orderDAO, never()).getOrdersByUserId(any());
    }

    /**
     * 测试按客户名称查询订单列表 - 客户无订单
     */
    @Test
    @DisplayName("客户无订单 - 应返回空列表")
    void testGetOrdersByUserId_NoOrders_ReturnsEmptyList() {
        // 准备测试数据
        String customerName = "客户Z";
        List<Order> emptyList = new ArrayList<>();
        
        // 配置mock行为
        when(orderDAO.getOrdersByUserId(customerName)).thenReturn(emptyList);
        
        // 执行测试
        List<Order> result = orderService.getOrdersByUserId(customerName);
        
        // 验证结果
        assertNotNull(result, "结果不应该为null");
        assertTrue(result.isEmpty(), "应该返回空列表");
        
        // 验证方法调用
        verify(orderDAO, times(1)).getOrdersByUserId(customerName);
    }

    /**
     * 测试产品版本验证 - 灵码企业版
     */
    @Test
    @DisplayName("产品版本验证 - 灵码企业版")
    void testCreateOrder_LingmaEnterprise_Success() {
        // 准备测试数据 - 灵码企业版: 5个LIC * 79 = 395
        Order order = new Order("order001", "客户A", "LINGMA_ENTERPRISE", 50, 5, new BigDecimal("395.00"));
        order.setDescription("灵码企业版测试订单");
        
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // 执行测试
        boolean result = orderService.createOrder(order);
        
        // 验证结果
        assertTrue(result);
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试产品版本验证 - Qoder
     */
    @Test
    @DisplayName("产品版本验证 - Qoder")
    void testCreateOrder_Qoder_Success() {
        // 准备测试数据 - Qoder: 3个LIC * 140 = 420
        Order order = new Order("order002", "客户B", "QODER", 30, 3, new BigDecimal("420.00"));
        order.setDescription("Qoder测试订单");
        
        when(orderDAO.createOrder(order)).thenReturn(true);
        
        // 执行测试
        boolean result = orderService.createOrder(order);
        
        // 验证结果
        assertTrue(result);
        verify(orderDAO, times(1)).createOrder(order);
    }

    /**
     * 测试产品版本验证 - 非法产品版本
     */
    @Test
    @DisplayName("产品版本验证 - 非法产品版本应抛出异常")
    void testCreateOrder_InvalidProductVersion_ThrowsException() {
        // 准备测试数据 - 非法产品版本
        Order order = new Order("order003", "客户C", "INVALID_PRODUCT", 10, 1, new BigDecimal("100.00"));
        order.setDescription("非法产品版本测试");
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order)
        );
        
        // 验证异常信息
        assertTrue(exception.getMessage().contains("不支持的产品版本"));
        verify(orderDAO, never()).createOrder(order);
    }

    /**
     * 测试总金额计算 - 灵码专属版
     */
    @Test
    @DisplayName("总金额计算 - 灵码专属版: 1个LIC * 159 = 159")
    void testTotalAmountCalculation_LingmaExclusive_OneLic() {
        // 准备测试数据
        Order order = new Order("order004", "客户D", "LINGMA_EXCLUSIVE", 10, 1, new BigDecimal("159.00"));
        order.setDescription("测试订单");
        
        when(orderDAO.createOrder(order)).thenReturn(true);
        boolean result = orderService.createOrder(order);
        
        // 验证总金额是否正确
        assertTrue(result);
        assertEquals(new BigDecimal("159.00"), order.getTotalAmount());
    }

    /**
     * 测试总金额计算 - 灵码企业版
     */
    @Test
    @DisplayName("总金额计算 - 灵码企业版: 10个LIC * 79 = 790")
    void testTotalAmountCalculation_LingmaEnterprise_TenLics() {
        // 准备测试数据
        Order order = new Order("order005", "客户E", "LINGMA_ENTERPRISE", 100, 10, new BigDecimal("790.00"));
        order.setDescription("测试订单");
        
        when(orderDAO.createOrder(order)).thenReturn(true);
        boolean result = orderService.createOrder(order);
        
        // 验证总金额是否正确
        assertTrue(result);
        assertEquals(new BigDecimal("790.00"), order.getTotalAmount());
    }

    /**
     * 测试总金额计算 - Qoder
     */
    @Test
    @DisplayName("总金额计算 - Qoder: 7个LIC * 140 = 980")
    void testTotalAmountCalculation_Qoder_SevenLics() {
        // 准备测试数据
        Order order = new Order("order006", "客户F", "QODER", 70, 7, new BigDecimal("980.00"));
        order.setDescription("测试订单");
        
        when(orderDAO.createOrder(order)).thenReturn(true);
        boolean result = orderService.createOrder(order);
        
        // 验证总金额是否正确
        assertTrue(result);
        assertEquals(new BigDecimal("980.00"), order.getTotalAmount());
    }

    /**
     * 测试总金额校验 - 金额错误应抛出异常
     */
    @Test
    @DisplayName("总金额校验 - 金额计算错误应抛出异常")
    void testCreateOrder_WrongTotalAmount_ThrowsException() {
        // 准备测试数据 - 总金额错误(应为318.00，实际为300.00)
        Order order = new Order("order007", "客户G", "LINGMA_EXCLUSIVE", 10, 2, new BigDecimal("300.00"));
        order.setDescription("金额错误测试");
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order)
        );
        
        // 验证异常信息
        assertTrue(exception.getMessage().contains("总金额计算错误"));
        verify(orderDAO, never()).createOrder(order);
    }

    /**
     * 测试研发规模校验 - 研发规模<=0应抛出异常
     */
    @Test
    @DisplayName("研发规模校验 - 研发规模<=0应抛出异常")
    void testCreateOrder_InvalidDevScale_ThrowsException() {
        // 准备测试数据 - 研发规模为0
        Order order = new Order("order008", "客户H", "QODER", 0, 5, new BigDecimal("700.00"));
        order.setDescription("研发规模错误测试");
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order)
        );
        
        // 验证异常信息
        assertEquals("研发规模必须大于0", exception.getMessage());
        verify(orderDAO, never()).createOrder(order);
    }
}