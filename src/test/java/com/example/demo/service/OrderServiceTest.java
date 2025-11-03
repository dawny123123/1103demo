package com.example.demo.service;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OrderService类的单元测试
 */
class OrderServiceTest {

    private OrderService orderService;
    private OrderDAO mockOrderDAO;

    /**
     * 测试用例执行前的初始化工作
     */
    @BeforeEach
    void setUp() {
        // 创建mock对象
        mockOrderDAO = Mockito.mock(OrderDAO.class);
        
        // 创建OrderService实例并注入mock的OrderDAO
        orderService = new OrderService() {
            @Override
            protected OrderDAO getOrderDAO() {
                return mockOrderDAO;
            }
        };
    }

    /**
     * 测试用例执行后的清理工作
     */
    @AfterEach
    void tearDown() {
        orderService = null;
        mockOrderDAO = null;
    }

    /**
     * TC001: 有效订单且orderDAO返回true
     * 预期结果：返回true
     */
    @Test
    void testCreateOrder_ValidOrder_ReturnsTrue() {
        // 准备测试数据
        Order order = createValidOrder();
        
        // 配置mock行为
        Mockito.when(mockOrderDAO.createOrder(order)).thenReturn(true);
        
        // 执行测试
        boolean result = orderService.createOrder(order);
        
        // 验证结果
        assertTrue(result);
        
        // 验证createOrder方法是否被正确调用一次
        Mockito.verify(mockOrderDAO, Mockito.times(1)).createOrder(order);
    }

    /**
     * TC002: 有效订单但orderDAO返回false
     * 预期结果：返回false
     */
    @Test
    void testCreateOrder_ValidOrderButDAOReturnsFalse_ReturnsFalse() {
        // 准备测试数据
        Order order = createValidOrder();
        
        // 配置mock行为
        Mockito.when(mockOrderDAO.createOrder(order)).thenReturn(false);
        
        // 执行测试
        boolean result = orderService.createOrder(order);
        
        // 验证结果
        assertFalse(result);
        
        // 验证createOrder方法是否被正确调用一次
        Mockito.verify(mockOrderDAO, Mockito.times(1)).createOrder(order);
    }

    /**
     * TC003: 数量<=0的订单
     * 预期结果：抛出IllegalArgumentException
     */
    @Test
    void testCreateOrder_QuantityLessThanOrEqualToZero_ThrowsException() {
        // 准备测试数据 - 数量为0
        Order order = createValidOrder();
        order.setQuantity(0);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order)
        );
        
        // 验证异常信息
        assertEquals("购买数量必须大于0", exception.getMessage());
        
        // 验证createOrder方法从未被调用
        Mockito.verify(mockOrderDAO, Mockito.never()).createOrder(order);
    }

    /**
     * TC004: 金额<=0的订单
     * 预期结果：抛出IllegalArgumentException
     */
    @Test
    void testCreateOrder_AmountLessThanOrEqualToZero_ThrowsException() {
        // 准备测试数据 - 金额为0
        Order order = createValidOrder();
        order.setTotalAmount(BigDecimal.ZERO);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder(order)
        );
        
        // 验证异常信息
        assertEquals("订单金额必须大于0", exception.getMessage());
        
        // 验证createOrder方法从未被调用
        Mockito.verify(mockOrderDAO, Mockito.never()).createOrder(order);
    }
    
    /**
     * TC005: 更新已完成订单为已完成状态，数据库中订单也是已完成状态
     * 预期结果：返回false，不调用DAO的updateOrder方法
     */
    @Test
    void testUpdateOrder_CompletedOrderToCompletedStatus_ReturnsFalse() {
        // 准备测试数据
        Order order = createValidOrder();
        order.setStatus(3); // 设置为已完成状态
        
        Order existingOrder = createValidOrder();
        existingOrder.setStatus(3); // 数据库中的订单也是已完成状态
        
        // 配置mock行为
        Mockito.when(mockOrderDAO.getOrder(order.getOrderId())).thenReturn(existingOrder);
        
        // 执行测试
        boolean result = orderService.updateOrder(order);
        
        // 验证结果
        assertFalse(result);
        
        // 验证getOrder方法被调用一次，但updateOrder方法从未被调用
        Mockito.verify(mockOrderDAO, Mockito.times(1)).getOrder(order.getOrderId());
        Mockito.verify(mockOrderDAO, Mockito.never()).updateOrder(order);
    }
    
    /**
     * TC006: 更新已完成订单为已完成状态，但数据库中订单不是已完成状态
     * 预期结果：调用DAO的updateOrder方法并返回其结果
     */
    @Test
    void testUpdateOrder_CompletedOrderToCompletedStatus_ExistingOrderNotCompleted_ReturnsDAOUpdateResult() {
        // 准备测试数据
        Order order = createValidOrder();
        order.setStatus(3); // 设置为已完成状态
        
        Order existingOrder = createValidOrder();
        existingOrder.setStatus(2); // 数据库中的订单是已发货状态
        
        // 配置mock行为
        Mockito.when(mockOrderDAO.getOrder(order.getOrderId())).thenReturn(existingOrder);
        Mockito.when(mockOrderDAO.updateOrder(order)).thenReturn(true);
        
        // 执行测试
        boolean result = orderService.updateOrder(order);
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        Mockito.verify(mockOrderDAO, Mockito.times(1)).getOrder(order.getOrderId());
        Mockito.verify(mockOrderDAO, Mockito.times(1)).updateOrder(order);
    }
    
    /**
     * TC007: 更新订单状态非已完成状态
     * 预期结果：调用DAO的updateOrder方法并返回其结果
     */
    @Test
    void testUpdateOrder_OrderWithNonCompletedStatus_ReturnsDAOUpdateResult() {
        // 准备测试数据
        Order order = createValidOrder();
        order.setStatus(2); // 设置为已发货状态
        
        // 配置mock行为
        Mockito.when(mockOrderDAO.updateOrder(order)).thenReturn(true);
        
        // 执行测试
        boolean result = orderService.updateOrder(order);
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        Mockito.verify(mockOrderDAO, Mockito.never()).getOrder(order.getOrderId()); // 不应该调用getOrder方法
        Mockito.verify(mockOrderDAO, Mockito.times(1)).updateOrder(order);
    }
    
    /**
     * TC008: 更新订单，但DAO返回false
     * 预期结果：返回false
     */
    @Test
    void testUpdateOrder_DAOReturnsFalse_ReturnsFalse() {
        // 准备测试数据
        Order order = createValidOrder();
        order.setStatus(1); // 设置为已支付状态
        
        // 配置mock行为
        Mockito.when(mockOrderDAO.updateOrder(order)).thenReturn(false);
        
        // 执行测试
        boolean result = orderService.updateOrder(order);
        
        // 验证结果
        assertFalse(result);
        
        // 验证方法调用
        Mockito.verify(mockOrderDAO, Mockito.never()).getOrder(order.getOrderId()); // 不应该调用getOrder方法
        Mockito.verify(mockOrderDAO, Mockito.times(1)).updateOrder(order);
    }

    /**
     * 创建一个有效订单
     * @return 有效订单对象
     */
    private Order createValidOrder() {
        String orderId = UUID.randomUUID().toString();
        String userId = "user123";
        String productId = "product456";
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("99.99");
        LocalDateTime now = LocalDateTime.now();
        
        return new Order(orderId, userId, productId, quantity, totalAmount);
    }
}