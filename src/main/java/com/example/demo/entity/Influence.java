package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 影响力实体类 - 记录公司影响力活动
 */
public class Influence {
    // 影响力记录唯一标识
    private String id;
    
    // 活动名称
    private String name;
    
    // 活动类型（枚举：SA_TRAINING/LOGO/CASE_STUDY/COMPETITOR_ANALYSIS/DEMO/CONFERENCE_SHARING）
    private String type;
    
    // 当前状态（枚举：PLANNED/IN_PROGRESS/COMPLETED/CANCELLED）
    private String status;
    
    // 活动时间
    private LocalDateTime eventTime;
    
    // 相关链接
    private String link;
    
    // 备注说明
    private String remark;
    
    // 图片URL列表
    private List<String> imageUrls;
    
    // 创建时间
    private LocalDateTime createTime;
    
    // 更新时间
    private LocalDateTime updateTime;

    // 活动类型常量
    public static final String TYPE_SA_TRAINING = "SA_TRAINING";
    public static final String TYPE_LOGO = "LOGO";
    public static final String TYPE_CASE_STUDY = "CASE_STUDY";
    public static final String TYPE_COMPETITOR_ANALYSIS = "COMPETITOR_ANALYSIS";
    public static final String TYPE_DEMO = "DEMO";
    public static final String TYPE_CONFERENCE_SHARING = "CONFERENCE_SHARING";
    
    // 状态常量
    public static final String STATUS_PLANNED = "PLANNED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // 无参构造函数（用于Jackson反序列化）
    public Influence() {
        this.status = STATUS_PLANNED;  // 默认计划中
        this.createTime = LocalDateTime.now();
        this.imageUrls = new ArrayList<>();
    }

    // 全参数构造函数
    public Influence(String id, String name, String type, String status, 
                     LocalDateTime eventTime, String link, String remark,
                     List<String> imageUrls, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status != null ? status : STATUS_PLANNED;
        this.eventTime = eventTime;
        this.link = link;
        this.remark = remark;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.createTime = createTime != null ? createTime : LocalDateTime.now();
        this.updateTime = updateTime;
    }

    // 基础字段构造函数
    public Influence(String id, String name, String type, String status, LocalDateTime eventTime) {
        this(id, name, type, status, eventTime, null, null, null, null, null);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Influence{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", eventTime=" + eventTime +
                ", link='" + link + '\'' +
                ", remark='" + remark + '\'' +
                ", imageUrls=" + imageUrls +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
