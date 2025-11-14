package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类 - 软件许可证订单
 */
public class Order {
    // 客户合同ID (Customer ID)
    private String cid;
    
    // 客户名称
    private String customerName;
    
    // 产品版本（枚举：QODER/LINGMA_ENTERPRISE/LINGMA_EXCLUSIVE）
    private String productVersion;
    
    // 研发规模
    private Integer devScale;
    
    // 已购LIC数（用于计算总金额）
    private Integer purchasedLicCount;
    
    // 订单总金额
    private BigDecimal totalAmount;
    
    // 订单状态: 0-售前, 1-下单, 2-扩容, 3-流失
    private Integer status;
    
    // 订单描述
    private String description;
    
    // 创建时间
    private LocalDateTime createTime;
    
    // 支付时间
    private LocalDateTime payTime;
    
    // 更新时间
    private LocalDateTime updateTime;

    // 无参构造函数（用于Jackson反序列化）
    public Order() {
        this.status = 0;  // 默认售前
        this.createTime = LocalDateTime.now();
    }

    // 全参数构造函数
    public Order(String cid, String customerName, String productVersion, Integer devScale, 
                 Integer purchasedLicCount, BigDecimal totalAmount, Integer status, String description, 
                 LocalDateTime createTime, LocalDateTime payTime, LocalDateTime updateTime) {
        this.cid = cid;
        this.customerName = customerName;
        this.productVersion = productVersion;
        this.devScale = devScale;
        this.purchasedLicCount = purchasedLicCount;
        this.totalAmount = totalAmount;
        this.status = status != null ? status : 0;  // 默认售前
        this.description = description;
        this.createTime = createTime != null ? createTime : LocalDateTime.now();
        this.payTime = payTime;
        this.updateTime = updateTime;
    }

    // 基础字段构造函数（自动设置创建时间和默认状态）
    public Order(String cid, String customerName, String productVersion, Integer devScale, 
                 Integer purchasedLicCount, BigDecimal totalAmount) {
        this(cid, customerName, productVersion, devScale, purchasedLicCount, totalAmount, 0, null, null, null, null);
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public Integer getDevScale() {
        return devScale;
    }

    public void setDevScale(Integer devScale) {
        this.devScale = devScale;
    }

    public Integer getPurchasedLicCount() {
        return purchasedLicCount;
    }

    public void setPurchasedLicCount(Integer purchasedLicCount) {
        this.purchasedLicCount = purchasedLicCount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                "cid='" + cid + '\'' +
                ", customerName='" + customerName + '\'' +
                ", productVersion='" + productVersion + '\'' +
                ", devScale=" + devScale +
                ", purchasedLicCount=" + purchasedLicCount +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", payTime=" + payTime +
                ", updateTime=" + updateTime +
                '}';
    }
}