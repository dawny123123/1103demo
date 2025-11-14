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
        String cid = "12345";
        String customerName = "客户A";
        String productVersion = "LINGMA_EXCLUSIVE";
        Integer devScale = 10;
        Integer purchasedLicCount = 2;
        BigDecimal totalAmount = new BigDecimal("318.00");
        
        Order order = new Order(cid, customerName, productVersion, devScale, purchasedLicCount, totalAmount);
        
        assertEquals(cid, order.getCid());
        assertEquals(customerName, order.getCustomerName());
        assertEquals(productVersion, order.getProductVersion());
        assertEquals(devScale, order.getDevScale());
        assertEquals(purchasedLicCount, order.getPurchasedLicCount());
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
        String cid = "12345";
        String customerName = "客户A";
        String productVersion = "LINGMA_EXCLUSIVE";
        Integer devScale = 10;
        Integer purchasedLicCount = 2;
        BigDecimal totalAmount = new BigDecimal("318.00");
        Integer status = 1;
        String description = "测试订单描述";
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime payTime = LocalDateTime.now().plusHours(1);
        LocalDateTime updateTime = LocalDateTime.now().plusHours(2);
        
        Order order = new Order(cid, customerName, productVersion, devScale, purchasedLicCount, totalAmount, status, description, createTime, payTime, updateTime);
        
        assertEquals(cid, order.getCid());
        assertEquals(customerName, order.getCustomerName());
        assertEquals(productVersion, order.getProductVersion());
        assertEquals(devScale, order.getDevScale());
        assertEquals(purchasedLicCount, order.getPurchasedLicCount());
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
        
        String cid = "12345";
        String customerName = "客户A";
        String productVersion = "LINGMA_EXCLUSIVE";
        Integer devScale = 10;
        Integer purchasedLicCount = 2;
        BigDecimal totalAmount = new BigDecimal("318.00");
        Integer status = 1;
        String description = "测试订单描述";
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime payTime = LocalDateTime.now().plusHours(1);
        LocalDateTime updateTime = LocalDateTime.now().plusHours(2);
        
        order.setCid(cid);
        order.setCustomerName(customerName);
        order.setProductVersion(productVersion);
        order.setDevScale(devScale);
        order.setPurchasedLicCount(purchasedLicCount);
        order.setTotalAmount(totalAmount);
        order.setStatus(status);
        order.setDescription(description);
        order.setCreateTime(createTime);
        order.setPayTime(payTime);
        order.setUpdateTime(updateTime);
        
        assertEquals(cid, order.getCid());
        assertEquals(customerName, order.getCustomerName());
        assertEquals(productVersion, order.getProductVersion());
        assertEquals(devScale, order.getDevScale());
        assertEquals(purchasedLicCount, order.getPurchasedLicCount());
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
        String cid = "12345";
        String customerName = "客户A";
        String productVersion = "LINGMA_EXCLUSIVE";
        Integer devScale = 10;
        Integer purchasedLicCount = 2;
        BigDecimal totalAmount = new BigDecimal("318.00");
        Integer status = 1;
        String description = "测试订单描述";
        
        Order order = new Order(cid, customerName, productVersion, devScale, purchasedLicCount, totalAmount);
        order.setStatus(status);
        order.setDescription(description);
        
        String orderString = order.toString();
        assertTrue(orderString.contains("cid='" + cid + "'"));
        assertTrue(orderString.contains("customerName='" + customerName + "'"));
        assertTrue(orderString.contains("productVersion='" + productVersion + "'"));
        assertTrue(orderString.contains("devScale=" + devScale));
        assertTrue(orderString.contains("purchasedLicCount=" + purchasedLicCount));
        assertTrue(orderString.contains("totalAmount=" + totalAmount));
        assertTrue(orderString.contains("status=" + status));
        assertTrue(orderString.contains("description='" + description + "'"));
    }
}