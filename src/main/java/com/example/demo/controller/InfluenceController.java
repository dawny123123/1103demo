package com.example.demo.controller;

import com.example.demo.entity.Influence;
import com.example.demo.service.InfluenceService;
import com.example.demo.dao.InfluenceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 影响力管理REST API控制器
 */
@RestController
@RequestMapping("/api/influences")
@CrossOrigin(origins = "*")
public class InfluenceController {

    private final InfluenceService influenceService;
    private final InfluenceDAO influenceDAO;

    @Autowired
    public InfluenceController(InfluenceService influenceService, InfluenceDAO influenceDAO) {
        this.influenceService = influenceService;
        this.influenceDAO = influenceDAO;
    }

    /**
     * 创建影响力记录
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInfluence(@RequestBody Influence influence) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = influenceService.createInfluence(influence);
            if (success) {
                influenceDAO.saveToDatabase();
                response.put("success", true);
                response.put("message", "影响力记录创建成功");
                response.put("data", influence);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("message", "影响力记录ID已存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 查询所有影响力记录
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInfluences() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Influence> influences = influenceService.getAllInfluences();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("count", influences.size());
            response.put("data", influences);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID查询单个影响力记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInfluence(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Influence influence = influenceService.getInfluence(id);
            if (influence != null) {
                response.put("success", true);
                response.put("message", "查询成功");
                response.put("data", influence);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "影响力记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据类型查询影响力记录列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getInfluencesByType(@PathVariable String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Influence> influences = influenceService.getInfluencesByType(type);
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("count", influences.size());
            response.put("data", influences);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新影响力记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateInfluence(
            @PathVariable String id,
            @RequestBody Influence influence) {
        Map<String, Object> response = new HashMap<>();
        try {
            influence.setId(id);
            boolean success = influenceService.updateInfluence(influence);
            if (success) {
                influenceDAO.saveToDatabase();
                response.put("success", true);
                response.put("message", "更新成功");
                response.put("data", influence);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "影响力记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除影响力记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteInfluence(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = influenceService.deleteInfluence(id);
            if (success) {
                influenceDAO.saveToDatabase();
                response.put("success", true);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "影响力记录不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "影响力服务运行正常");
        return ResponseEntity.ok(response);
    }
}
