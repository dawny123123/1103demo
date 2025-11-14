package com.example.demo.dao;

import com.example.demo.entity.Influence;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 影响力数据访问对象 - 实现SQLite数据库操作
 */
@Repository
public class InfluenceDAO {
    // 使用ConcurrentHashMap作为内存缓存，key为影响力记录ID
    private final Map<String, Influence> influenceMap = new ConcurrentHashMap<>();
    
    // 日期时间格式化器
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // Jackson对象映射器，用于JSON序列化
    private final ObjectMapper objectMapper;

    /**
     * 构造函数：初始化数据库表并加载数据
     */
    public InfluenceDAO() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        initTable();
        loadFromDatabase();
    }

    /**
     * 初始化数据库表结构
     */
    public void initTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS influence(" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "event_time TEXT NOT NULL, " +
                "link TEXT, " +
                "remark TEXT, " +
                "image_urls TEXT, " +
                "create_time TEXT NOT NULL, " +
                "update_time TEXT" +
                ");";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Influence表初始化成功");
        } catch (SQLException e) {
            System.err.println("初始化Influence表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 将内存数据保存到数据库
     */
    public void saveToDatabase() {
        String deleteSQL = "DELETE FROM influence";
        String insertSQL = "INSERT OR REPLACE INTO influence " +
                "(id, name, type, status, event_time, link, remark, image_urls, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             Statement deleteStmt = conn.createStatement();
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
            
            // 清空表
            deleteStmt.executeUpdate(deleteSQL);
            
            // 批量插入
            for (Influence influence : influenceMap.values()) {
                insertStmt.setString(1, influence.getId());
                insertStmt.setString(2, influence.getName());
                insertStmt.setString(3, influence.getType());
                insertStmt.setString(4, influence.getStatus());
                insertStmt.setString(5, influence.getEventTime().format(FORMATTER));
                insertStmt.setString(6, influence.getLink());
                insertStmt.setString(7, influence.getRemark());
                
                // 将imageUrls列表序列化为JSON字符串
                String imageUrlsJson = null;
                if (influence.getImageUrls() != null && !influence.getImageUrls().isEmpty()) {
                    try {
                        imageUrlsJson = objectMapper.writeValueAsString(influence.getImageUrls());
                    } catch (Exception e) {
                        System.err.println("序列化imageUrls失败: " + e.getMessage());
                    }
                }
                insertStmt.setString(8, imageUrlsJson);
                
                insertStmt.setString(9, influence.getCreateTime().format(FORMATTER));
                insertStmt.setString(10, influence.getUpdateTime() != null ? 
                        influence.getUpdateTime().format(FORMATTER) : null);
                
                insertStmt.addBatch();
            }
            
            insertStmt.executeBatch();
            System.out.println("成功保存 " + influenceMap.size() + " 条影响力记录到数据库");
            
        } catch (SQLException e) {
            System.err.println("保存影响力记录到数据库失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从数据库加载数据到内存
     */
    public void loadFromDatabase() {
        String selectSQL = "SELECT * FROM influence";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            
            influenceMap.clear();
            int count = 0;
            
            while (rs.next()) {
                // 解析imageUrls JSON字符串
                List<String> imageUrls = new ArrayList<>();
                String imageUrlsJson = rs.getString("image_urls");
                if (imageUrlsJson != null && !imageUrlsJson.isEmpty()) {
                    try {
                        imageUrls = objectMapper.readValue(imageUrlsJson, 
                                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    } catch (Exception e) {
                        System.err.println("反序列化imageUrls失败: " + e.getMessage());
                    }
                }
                
                Influence influence = new Influence(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("status"),
                    LocalDateTime.parse(rs.getString("event_time"), FORMATTER),
                    rs.getString("link"),
                    rs.getString("remark"),
                    imageUrls,
                    LocalDateTime.parse(rs.getString("create_time"), FORMATTER),
                    rs.getString("update_time") != null ? 
                            LocalDateTime.parse(rs.getString("update_time"), FORMATTER) : null
                );
                
                influenceMap.put(influence.getId(), influence);
                count++;
            }
            
            System.out.println("成功从数据库加载 " + count + " 条影响力记录");
            
        } catch (SQLException e) {
            System.err.println("从数据库加载影响力记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 创建影响力记录
     * @param influence 影响力记录对象
     * @return 创建成功返回true，ID已存在返回false
     */
    public boolean createInfluence(Influence influence) {
        if (influenceMap.containsKey(influence.getId())) {
            return false; // ID已存在
        }
        
        // 确保创建时间已设置
        if (influence.getCreateTime() == null) {
            influence.setCreateTime(LocalDateTime.now());
        }
        
        // 确保imageUrls不为null
        if (influence.getImageUrls() == null) {
            influence.setImageUrls(new ArrayList<>());
        }
        
        influenceMap.put(influence.getId(), influence);
        return true;
    }

    /**
     * 获取影响力记录
     * @param id 记录ID
     * @return 返回影响力记录对象，不存在返回null
     */
    public Influence getInfluence(String id) {
        return influenceMap.get(id);
    }

    /**
     * 更新影响力记录
     * @param influence 待更新的影响力记录对象
     * @return 更新成功返回true，记录不存在返回false
     */
    public boolean updateInfluence(Influence influence) {
        if (!influenceMap.containsKey(influence.getId())) {
            return false; // 记录不存在
        }
        
        // 更新更新时间
        influence.setUpdateTime(LocalDateTime.now());
        influenceMap.put(influence.getId(), influence);
        return true;
    }

    /**
     * 删除影响力记录
     * @param id 记录ID
     * @return 删除成功返回true，记录不存在返回false
     */
    public boolean deleteInfluence(String id) {
        if (!influenceMap.containsKey(id)) {
            return false; // 记录不存在
        }
        influenceMap.remove(id);
        return true;
    }

    /**
     * 根据类型查询影响力记录列表
     * @param type 活动类型
     * @return 返回该类型的所有影响力记录列表，按活动时间降序排列
     */
    public List<Influence> getInfluencesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return influenceMap.values().stream()
                .filter(influence -> type.equals(influence.getType()))
                .sorted(Comparator.comparing(Influence::getEventTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取所有影响力记录列表
     * @return 返回所有影响力记录列表，按活动时间降序排列（最新的在前）
     */
    public List<Influence> getAllInfluences() {
        return influenceMap.values().stream()
                .sorted(Comparator.comparing(Influence::getEventTime).reversed())
                .collect(Collectors.toList());
    }
}
