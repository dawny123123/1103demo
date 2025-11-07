package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
public class Order {
    // 订单唯一标识
    private String orderId;
    
    // 用户ID
    private String userId;
    
    // 商品ID
    private String productId;
    
    // 购买数量
    private Integer quantity;
    
    // 订单总金额
    private BigDecimal totalAmount;
    
    // 订单状态: 0-待支付, 1-已支付, 2-已发货, 3-已完成, 4-已取消
    private Integer status;
    
    // 创建时间
    private LocalDateTime createTime;
    
    // 支付时间
    private LocalDateTime payTime;
    
    // 更新时间
    private LocalDateTime updateTime;

    // 无参构造函数（用于Jackson反序列化）
    public Order() {
        this.status = 0;  // 默认待支付
        this.createTime = LocalDateTime.now();
    }

    // 全参数构造函数
    public Order(String orderId, String userId, String productId, Integer quantity, 
                 BigDecimal totalAmount, Integer status, LocalDateTime createTime, 
                 LocalDateTime payTime, LocalDateTime updateTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = status != null ? status : 0;  // 默认待支付
        this.createTime = createTime != null ? createTime : LocalDateTime.now();
        this.payTime = payTime;
        this.updateTime = updateTime;
    }

    // 基础字段构造函数（自动设置创建时间和默认状态）
    public Order(String orderId, String userId, String productId, Integer quantity, BigDecimal totalAmount) {
        this(orderId, userId, productId, quantity, totalAmount, 0, null, null, null);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", createTime=" + createTime +
                ", payTime=" + payTime +
                ", updateTime=" + updateTime +
                '}';
    }
}