package com.example.demo.dao;

import java.sql.*;

/**
 * SQLite数据库操作工具类
 * 数据库连接管理
 */
public class DBUtil {
    // 数据库连接URL（test.db位于项目根目录）
    private static final String URL = "jdbc:sqlite:test.db";
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}