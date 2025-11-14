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
    @GetMapping("/{cid}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String cid) {
        Map<String, Object> response = new HashMap<>();
        Order order = orderService.getOrder(cid);
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
     * 根据客户名称获取订单列表
     */
    @GetMapping("/user/{customerName}")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(@PathVariable String customerName) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Order> orders = orderService.getOrdersByUserId(customerName);
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
    @PutMapping("/{cid}")
    public ResponseEntity<Map<String, Object>> updateOrder(
            @PathVariable String cid,
            @RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        try {
            order.setCid(cid);
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
     * @param cid 订单CID
     * @param reason 删除原因（传入必填）
     */
    @DeleteMapping("/{cid}")
    public ResponseEntity<Map<String, Object>> deleteOrder(
            @PathVariable String cid,
            @RequestParam(value = "reason", required = true) String reason) {
        Map<String, Object> response = new HashMap<>();
        
        // 校验删除原因
        if (reason == null || reason.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "删除失败：必须提供删除原因");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // 记录删除原因（在删除前更新订单描述）
        Order order = orderService.getOrder(cid);
        if (order != null) {
            String deleteLog = String.format("[%s] 订单删除：%s",
                    java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    reason);
            String newDescription = (order.getDescription() != null ? order.getDescription() + "\n" : "") + deleteLog;
            order.setDescription(newDescription);
            orderService.updateOrder(order);
        }
        
        boolean success = orderService.deleteOrder(cid);
        if (success) {
            orderDAO.saveToDatabase();
            response.put("success", true);
            response.put("message", "订单删除成功，删除原因: " + reason);
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