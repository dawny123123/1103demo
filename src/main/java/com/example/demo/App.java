package com.example.demo;

import com.example.demo.dao.OrderDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot主启动类
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public OrderDAO orderDAO() {
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.initTable();
        orderDAO.loadFromDatabase();  // 加载数据库中的数据到内存
        return orderDAO;
    }
}