package com.example.demo.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Order实体类的单元测试
 */
class OrderTest {

    /**
     * 测试Order的无参构造函数
     */
    @Test
    @DisplayName("测试Order无参构造函数")
    void testDefaultConstructor() {
        Order order = new Order();
        
        assertNotNull(order);
        assertEquals(0, order.getStatus());
        assertNotNull(order.getCreateTime());
    }

    /**
     * 测试Order的基础字段构造函数
     */
    @Test
    @DisplayName("测试Order基础字段构造函数")
    void testBasicConstructor() {
        String orderId = "12345";
        String userId = "user123";
        String productId = "product456";
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("99.99");
        
        Order order = new Order(orderId, userId, productId, quantity, totalAmount);
        
        assertEquals(orderId, order.getOrderId());
        assertEquals(userId, order.getUserId());
        assertEquals(productId, order.getProductId());
        assertEquals(quantity, order.getQuantity());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(0, order.getStatus());
        assertNotNull(order.getCreateTime());
    }

    /**
     * 测试Order的全参数构造函数
     */
    @Test
    @DisplayName("测试Order全参数构造函数")
    void testFullConstructor() {
        String orderId = "12345";
        String userId = "user123";
        String productId = "product456";
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("99.99");
        Integer status = 1;
        String description = "测试订单描述";
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime payTime = LocalDateTime.now().plusHours(1);
        LocalDateTime updateTime = LocalDateTime.now().plusHours(2);
        
        Order order = new Order(orderId, userId, productId, quantity, totalAmount, status, description, createTime, payTime, updateTime);
        
        assertEquals(orderId, order.getOrderId());
        assertEquals(userId, order.getUserId());
        assertEquals(productId, order.getProductId());
        assertEquals(quantity, order.getQuantity());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(status, order.getStatus());
        assertEquals(description, order.getDescription());
        assertEquals(createTime, order.getCreateTime());
        assertEquals(payTime, order.getPayTime());
        assertEquals(updateTime, order.getUpdateTime());
    }

    /**
     * 测试Order的Getter和Setter方法
     */
    @Test
    @DisplayName("测试Order的Getter和Setter方法")
    void testGettersAndSetters() {
        Order order = new Order();
        
        String orderId = "12345";
        String userId = "user123";
        String productId = "product456";
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("99.99");
        Integer status = 1;
        String description = "测试订单描述";
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime payTime = LocalDateTime.now().plusHours(1);
        LocalDateTime updateTime = LocalDateTime.now().plusHours(2);
        
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(totalAmount);
        order.setStatus(status);
        order.setDescription(description);
        order.setCreateTime(createTime);
        order.setPayTime(payTime);
        order.setUpdateTime(updateTime);
        
        assertEquals(orderId, order.getOrderId());
        assertEquals(userId, order.getUserId());
        assertEquals(productId, order.getProductId());
        assertEquals(quantity, order.getQuantity());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(status, order.getStatus());
        assertEquals(description, order.getDescription());
        assertEquals(createTime, order.getCreateTime());
        assertEquals(payTime, order.getPayTime());
        assertEquals(updateTime, order.getUpdateTime());
    }

    /**
     * 测试Order的toString方法
     */
    @Test
    @DisplayName("测试Order的toString方法")
    void testToString() {
        String orderId = "12345";
        String userId = "user123";
        String productId = "product456";
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("99.99");
        Integer status = 1;
        String description = "测试订单描述";
        
        Order order = new Order(orderId, userId, productId, quantity, totalAmount);
        order.setStatus(status);
        order.setDescription(description);
        
        String orderString = order.toString();
        assertTrue(orderString.contains("orderId='" + orderId + "'"));
        assertTrue(orderString.contains("userId='" + userId + "'"));
        assertTrue(orderString.contains("productId='" + productId + "'"));
        assertTrue(orderString.contains("quantity=" + quantity));
        assertTrue(orderString.contains("totalAmount=" + totalAmount));
        assertTrue(orderString.contains("status=" + status));
        assertTrue(orderString.contains("description='" + description + "'"));
    }
}