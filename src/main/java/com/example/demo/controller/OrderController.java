package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import com.example.demo.dao.OrderDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理REST API控制器
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final OrderDAO orderDAO;

    @Autowired
    public OrderController(OrderService orderService, OrderDAO orderDAO) {
        this.orderService = orderService;
        this.orderDAO = orderDAO;
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = orderService.createOrder(order);
            if (success) {
                orderDAO.saveToDatabase();
                response.put("success", true);
                response.put("message", "订单创建成功");
                response.put("data", order);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("message", "订单已存在");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 获取所有订单列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Order> orders = orderService.getAllOrders();
            response.put("success", true);
            response.put("data", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        Order order = orderService.getOrder(orderId);
        if (order != null) {
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "订单不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 根据用户ID获取订单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            response.put("success", true);
            response.put("data", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 更新订单
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> updateOrder(
            @PathVariable String orderId,
            @RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        try {
            order.setOrderId(orderId);
            boolean success = orderService.updateOrder(order);
            if (success) {
                orderDAO.saveToDatabase();
                response.put("success", true);
                response.put("message", "订单更新成功");
                response.put("data", order);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "订单不存在或无法更新");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = orderService.deleteOrder(orderId);
        if (success) {
            orderDAO.saveToDatabase();
            response.put("success", true);
            response.put("message", "订单删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "订单不存在或无法删除(已支付订单不能删除)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "订单服务运行正常");
        return ResponseEntity.ok(response);
    }
}
