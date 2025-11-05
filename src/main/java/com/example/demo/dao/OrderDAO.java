package com.example.demo.dao;

import com.example.demo.entity.Order;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    /**
     * 初始化数据库表结构
     */
    public void initTable() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS order0713(" +
                "orderId TEXT PRIMARY KEY, " +
                "userId TEXT, " +
                "productId TEXT, " +
                "quantity INTEGER, " +
                "totalAmount TEXT, " +
                "status INTEGER, " +
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
            "orderId, userId, productId, quantity, " +
            "totalAmount, status, createTime, payTime, updateTime) " +
            "VALUES(?,?,?,?,?,?,?,?,?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Order order : orderMap.values()) {
                pstmt.setString(1, order.getOrderId());
                pstmt.setString(2, order.getUserId());
                pstmt.setString(3, order.getProductId());
                pstmt.setInt(4, order.getQuantity());
                pstmt.setString(5, order.getTotalAmount().toString());
                pstmt.setInt(6, order.getStatus());
                pstmt.setString(7, order.getCreateTime() != null ? order.getCreateTime().toString() : null);
                pstmt.setString(8, order.getPayTime() != null ? order.getPayTime().toString() : null);
                pstmt.setString(9, order.getUpdateTime() != null ? order.getUpdateTime().toString() : null);
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
        String sql = "SELECT * FROM order0713";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getString("orderId"),
                    rs.getString("userId"),
                    rs.getString("productId"),
                    rs.getInt("quantity"),
                    new BigDecimal(rs.getString("totalAmount")),
                    rs.getInt("status"),
                    rs.getObject("createTime", LocalDateTime.class),
                    rs.getObject("payTime", LocalDateTime.class),
                    rs.getObject("updateTime", LocalDateTime.class)
                );
                orderMap.put(order.getOrderId(), order);
            }
        } catch (SQLException e) {
            System.out.println("数据加载失败: " + e.getMessage());
        }
    }
    
    // ... existing CRUD methods ...
    
    /**
     * 创建订单
     * @param order 待创建的订单对象
     * @return 创建成功返回true，订单已存在返回false
     */
    public boolean createOrder(Order order) {
        if (orderMap.containsKey(order.getOrderId())) {
            return false; // 订单已存在
        }
        orderMap.put(order.getOrderId(), order);
        return true;
    }

    /**
     * 获取订单
     * @param orderId 订单ID
     * @return 返回订单对象，不存在返回null
     */
    public Order getOrder(String orderId) {
        return orderMap.get(orderId);
    }

    /**
     * 更新订单
     * @param order 待更新的订单对象
     * @return 更新成功返回true，订单不存在返回false
     */
    public boolean updateOrder(Order order) {
        if (!orderMap.containsKey(order.getOrderId())) {
            return false; // 订单不存在
        }
        orderMap.put(order.getOrderId(), order);
        return true;
    }

    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 删除成功返回true，订单不存在返回false
     */
    public boolean deleteOrder(String orderId) {
        if (!orderMap.containsKey(orderId)) {
            return false; // 订单不存在
        }
        orderMap.remove(orderId);
        return true;
    }

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 返回该用户的所有订单列表，按创建时间降序排列；如果userId为null或空字符串，返回空列表
     */
    public List<Order> getOrdersByUserId(String userId) {
        // 参数校验：userId为null或空字符串时，返回空列表
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 使用流式操作过滤、排序并收集结果
        return orderMap.values().stream()
            .filter(order -> userId.equals(order.getUserId()))
            .sorted(Comparator.comparing(
                Order::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .collect(Collectors.toList());
    }
}