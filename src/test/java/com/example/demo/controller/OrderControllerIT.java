package com.example.demo.controller;

import com.example.demo.entity.Order;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * OrderController的集成测试类
 * 使用RestAssured测试REST API端点
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    /**
     * 测试创建订单接口 - 正常流程
     */
    @Test
    void testCreateOrder_ValidOrder_ReturnsSuccess() {
        // 创建测试订单对象 - 灵码专属版: 2个LIC * 159 = 318
        Order order = new Order();
        order.setCid("IT-" + System.currentTimeMillis());
        order.setCustomerName("客户IT-" + System.currentTimeMillis());
        order.setProductVersion("LINGMA_EXCLUSIVE");
        order.setDevScale(20);
        order.setPurchasedLicCount(2);
        order.setTotalAmount(new BigDecimal("318.00"));
        order.setDescription("集成测试订单");

        given()
            .contentType(ContentType.JSON)
            .body(order)
        .when()
            .post("/api/orders")
        .then()
            .statusCode(201)
            .body("success", equalTo(true))
            .body("message", equalTo("订单创建成功"))
            .body("data.cid", equalTo(order.getCid()))
            .body("data.customerName", equalTo(order.getCustomerName()))
            .body("data.productVersion", equalTo(order.getProductVersion()));
    }

    /**
     * 测试创建订单接口 - 已购LIC数为0
     */
    @Test
    void testCreateOrder_ZeroQuantity_ReturnsBadRequest() {
        // 创建测试订单对象
        Order order = new Order();
        order.setCid("IT-002");
        order.setCustomerName("客户IT-002");
        order.setProductVersion("LINGMA_EXCLUSIVE");
        order.setDevScale(10);
        order.setPurchasedLicCount(0); // 已购LIC数为0
        order.setTotalAmount(new BigDecimal("0.00"));
        order.setDescription("集成测试订单");

        given()
            .contentType(ContentType.JSON)
            .body(order)
        .when()
            .post("/api/orders")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("message", containsString("已购LIC数必须大于0"));
    }

    /**
     * 测试创建订单接口 - 金额为负数
     */
    @Test
    void testCreateOrder_NegativeAmount_ReturnsBadRequest() {
        // 创建测试订单对象
        Order order = new Order();
        order.setCid("IT-003");
        order.setCustomerName("客户IT-003");
        order.setProductVersion("LINGMA_EXCLUSIVE");
        order.setDevScale(20);
        order.setPurchasedLicCount(2);
        order.setTotalAmount(new BigDecimal("-100.00")); // 负数金额
        order.setDescription("集成测试订单");

        given()
            .contentType(ContentType.JSON)
            .body(order)
        .when()
            .post("/api/orders")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("message", containsString("订单金额必须大于0"));
    }

    /**
     * 测试获取所有订单接口
     */
    @Test
    void testGetAllOrders_ReturnsOrderList() {
        given()
        .when()
            .get("/api/orders")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data", notNullValue());
    }

    /**
     * 测试获取订单详情接口
     */
    @Test
    void testGetOrder_ValidOrderId_ReturnsOrder() {
        String cid = "IT-" + System.currentTimeMillis();
        String customerName = "客户IT-" + System.currentTimeMillis();
        String productVersion = "LINGMA_EXCLUSIVE";
        
        // 先创建一个订单 - 灵码专属版: 1个LIC * 159 = 159
        Order order = new Order();
        order.setCid(cid);
        order.setCustomerName(customerName);
        order.setProductVersion(productVersion);
        order.setDevScale(10);
        order.setPurchasedLicCount(1);
        order.setTotalAmount(new BigDecimal("159.00"));
        order.setDescription("集成测试订单");

        // 创建订单
        given()
            .contentType(ContentType.JSON)
            .body(order)
        .when()
            .post("/api/orders")
        .then()
            .statusCode(201);

        // 获取订单详情
        given()
        .when()
            .get("/api/orders/" + cid)
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.cid", equalTo(cid))
            .body("data.customerName", equalTo(customerName));
    }

    /**
     * 测试健康检查接口
     */
    @Test
    void testHealthCheck_ReturnsOK() {
        given()
        .when()
            .get("/api/orders/health")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("message", equalTo("订单服务运行正常"));
    }
}