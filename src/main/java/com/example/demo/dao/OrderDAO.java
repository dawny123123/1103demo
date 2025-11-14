package com.example.demo.dao;

import com.example.demo.entity.Order;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 订单数据访问对象（SQLite实现）
 */
public class OrderDAO {
    // 模拟数据库存储 - 使用线程安全的Map
    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();
    
    // 时间格式化器
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * 初始化数据库表结构
     */
    public void initTable() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS order0713(" +
                "cid TEXT PRIMARY KEY, " +
                "customerName TEXT, " +
                "productVersion TEXT, " +
                "devScale INTEGER, " +
                "purchasedLicCount INTEGER, " +
                "totalAmount TEXT, " +
                "status INTEGER, " +
                "description TEXT, " +
                "createTime TEXT, " +
                "payTime TEXT, " +
                "updateTime TEXT)";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("数据库初始化失败: " + e.getMessage());
        }
    }
    
    /**
     * 将内存数据保存到数据库
     */
    public void saveToDatabase() {
        String sql = "INSERT OR REPLACE INTO order0713(" +
            "cid, customerName, productVersion, devScale, purchasedLicCount, " +
            "totalAmount, status, description, createTime, payTime, updateTime) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Order order : orderMap.values()) {
                pstmt.setString(1, order.getCid());
                pstmt.setString(2, order.getCustomerName());
                pstmt.setString(3, order.getProductVersion());
                pstmt.setInt(4, order.getDevScale());
                pstmt.setInt(5, order.getPurchasedLicCount());
                pstmt.setString(6, order.getTotalAmount().toString());
                pstmt.setInt(7, order.getStatus());
                pstmt.setString(8, order.getDescription());
                pstmt.setString(9, order.getCreateTime() != null ? order.getCreateTime().toString() : null);
                pstmt.setString(10, order.getPayTime() != null ? order.getPayTime().toString() : null);
                pstmt.setString(11, order.getUpdateTime() != null ? order.getUpdateTime().toString() : null);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println("数据保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 从数据库加载数据到内存
     */
    public void loadFromDatabase() {
        String sql = "SELECT cid, customerName, productVersion, devScale, purchasedLicCount, totalAmount, status, description, createTime, payTime, updateTime FROM order0713";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // 解析时间字段
                LocalDateTime createTime = null;
                LocalDateTime payTime = null;
                LocalDateTime updateTime = null;
                
                String createTimeStr = rs.getString("createTime");
                String payTimeStr = rs.getString("payTime");
                String updateTimeStr = rs.getString("updateTime");
                
                if (createTimeStr != null && !createTimeStr.isEmpty()) {
                    try {
                        createTime = LocalDateTime.parse(createTimeStr, FORMATTER);
                    } catch (Exception e) {
                        // 如果解析失败，尝试其他格式
                        try {
                            createTime = LocalDateTime.parse(createTimeStr);
                        } catch (Exception e2) {
                            System.out.println("创建时间解析失败: " + createTimeStr);
                        }
                    }
                }
                
                if (payTimeStr != null && !payTimeStr.isEmpty()) {
                    try {
                        payTime = LocalDateTime.parse(payTimeStr, FORMATTER);
                    } catch (Exception e) {
                        // 如果解析失败，尝试其他格式
                        try {
                            payTime = LocalDateTime.parse(payTimeStr);
                        } catch (Exception e2) {
                            System.out.println("支付时间解析失败: " + payTimeStr);
                        }
                    }
                }
                
                if (updateTimeStr != null && !updateTimeStr.isEmpty()) {
                    try {
                        updateTime = LocalDateTime.parse(updateTimeStr, FORMATTER);
                    } catch (Exception e) {
                        // 如果解析失败，尝试其他格式
                        try {
                            updateTime = LocalDateTime.parse(updateTimeStr);
                        } catch (Exception e2) {
                            System.out.println("更新时间解析失败: " + updateTimeStr);
                        }
                    }
                }
                
                Order order = new Order(
                    rs.getString("cid"),
                    rs.getString("customerName"),
                    rs.getString("productVersion"),
                    rs.getInt("devScale"),
                    rs.getInt("purchasedLicCount"),
                    new BigDecimal(rs.getString("totalAmount")),
                    rs.getInt("status"),
                    rs.getString("description"),
                    createTime,
                    payTime,
                    updateTime
                );
                orderMap.put(order.getCid(), order);
            }
            System.out.println("从数据库加载了 " + orderMap.size() + " 条订单记录");
        } catch (SQLException e) {
            System.out.println("数据加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建订单
     * @param order 待创建的订单对象
     * @return 创建成功返回true，订单已存在返回false
     */
    public boolean createOrder(Order order) {
        if (orderMap.containsKey(order.getCid())) {
            return false; // 订单已存在
        }
        // 确保创建时间被设置
        if (order.getCreateTime() == null) {
            order.setCreateTime(LocalDateTime.now());
        }
        orderMap.put(order.getCid(), order);
        return true;
    }

    /**
     * 获取订单
     * @param cid 订单ID
     * @return 返回订单对象，不存在返回null
     */
    public Order getOrder(String cid) {
        return orderMap.get(cid);
    }

    /**
     * 更新订单
     * @param order 待更新的订单对象
     * @return 更新成功返回true，订单不存在返回false
     */
    public boolean updateOrder(Order order) {
        if (!orderMap.containsKey(order.getCid())) {
            return false; // 订单不存在
        }
        // 更新更新时间
        order.setUpdateTime(LocalDateTime.now());
        orderMap.put(order.getCid(), order);
        return true;
    }

    /**
     * 删除订单
     * @param cid 订单ID
     * @return 删除成功返回true，订单不存在返回false
     */
    public boolean deleteOrder(String cid) {
        if (!orderMap.containsKey(cid)) {
            return false; // 订单不存在
        }
        orderMap.remove(cid);
        return true;
    }

    /**
     * 根据客户名称查询订单列表
     * @param customerName 客户名称
     * @return 返回该用户的所有订单列表，按创建时间降序排列；如果customerName为null或空字符串，返回空列表
     */
    public List<Order> getOrdersByUserId(String customerName) {
        // 参数校验：customerName为null或空字符串时，返回空列表
        if (customerName == null || customerName.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 使用流式操作过滤、排序并收集结果
        return orderMap.values().stream()
            .filter(order -> customerName.equals(order.getCustomerName()))  // 严格匹配客户名称
            .sorted(Comparator.comparing(
                Order::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .collect(Collectors.toList());
    }

    /**
     * 获取所有订单列表
     * @return 返回所有订单列表，按创建时间降序排列（最新订单在前）
     */
    public List<Order> getAllOrders() {
        // 使用流式操作获取所有订单并按创建时间降序排序
        return orderMap.values().stream()
            .sorted(Comparator.comparing(
                Order::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .collect(Collectors.toList());
    }
}