package com.example.demo.service;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单服务类（演示业务逻辑与数据访问的分离）
 */
@Service
public class OrderService {
    // 产品版本枚举定义
    public static final String QODER = "QODER";
    public static final String LINGMA_ENTERPRISE = "LINGMA_ENTERPRISE";
    public static final String LINGMA_EXCLUSIVE = "LINGMA_EXCLUSIVE";

    // 产品单价定义
    public static final BigDecimal QODER_PRICE = new BigDecimal("140"); // 20 * 7 = 140
    public static final BigDecimal LINGMA_ENTERPRISE_PRICE = new BigDecimal("79");
    public static final BigDecimal LINGMA_EXCLUSIVE_PRICE = new BigDecimal("159");
    
    // 注入数据访问层
    private final OrderDAO orderDAO;

    @Autowired
    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    /**
     * 创建新订单
     * @param order 待创建的订单对象
     * @return 创建成功返回true，订单已存在返回false
     */
    public boolean createOrder(Order order) {
        // 校验产品版本合法性
        validateProductVersion(order.getProductVersion());
        
        // 校验已购LIC数和研发规模
        if (order.getPurchasedLicCount() == null || order.getPurchasedLicCount() <= 0) {
            throw new IllegalArgumentException("已购LIC数必须大于0");
        }
        
        if (order.getDevScale() == null || order.getDevScale() <= 0) {
            throw new IllegalArgumentException("研发规模必须大于0");
        }

        // 重新计算总金额并校验
        BigDecimal calculatedAmount = calculateTotalAmount(order.getProductVersion(), order.getPurchasedLicCount());
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(calculatedAmount) != 0) {
            throw new IllegalArgumentException("总金额计算错误，应为：" + calculatedAmount);
        }

        return getOrderDAO().createOrder(order);
    }

    /**
     * 校验产品版本是否合法
     * @param productVersion 产品版本
     */
    private void validateProductVersion(String productVersion) {
        if (productVersion == null || 
            (!QODER.equals(productVersion) && 
             !LINGMA_ENTERPRISE.equals(productVersion) && 
             !LINGMA_EXCLUSIVE.equals(productVersion))) {
            throw new IllegalArgumentException("产品版本不合法，仅支持：Qoder、灵码企业版、灵码专属版");
        }
    }

    /**
     * 根据产品版本和已购LIC数计算总金额
     * @param productVersion 产品版本
     * @param purchasedLicCount 已购LIC数
     * @return 总金额
     */
    private BigDecimal calculateTotalAmount(String productVersion, Integer purchasedLicCount) {
        BigDecimal unitPrice;
        switch (productVersion) {
            case QODER:
                unitPrice = QODER_PRICE;
                break;
            case LINGMA_ENTERPRISE:
                unitPrice = LINGMA_ENTERPRISE_PRICE;
                break;
            case LINGMA_EXCLUSIVE:
                unitPrice = LINGMA_EXCLUSIVE_PRICE;
                break;
            default:
                throw new IllegalArgumentException("不支持的产品版本: " + productVersion);
        }
        return unitPrice.multiply(new BigDecimal(purchasedLicCount));
    }

    /**
     * 获取订单信息
     * @param cid 订单ID
     * @return 返回订单对象，不存在返回null
     */
    public Order getOrder(String cid) {
        return getOrderDAO().getOrder(cid);
    }

    /**
     * 获取OrderDAO实例，用于测试mock
     * @return OrderDAO实例
     */
    protected OrderDAO getOrderDAO() {
        return orderDAO;
    }

    /**
     * 测试github推送
     * 更新订单信息
     * @param order 待更新的订单对象
     * @return 更新成功返回true，订单不存在返回false
     */
    public boolean updateOrder(Order order) {
        // 可以添加更新规则
        if (order.getStatus() != null && order.getStatus() == 3) {
            // 已完成订单不能修改
            Order existing = getOrderDAO().getOrder(order.getCid());
            if (existing != null && existing.getStatus() == 3) {
                return false;
            }
        }

        return getOrderDAO().updateOrder(order);
    }

    /**
     * 删除订单
     * @param cid 订单ID
     * @return 删除成功返回true，订单不存在返回false
     */
    public boolean deleteOrder(String cid) {
        // 可以添加删除规则
        Order order = getOrderDAO().getOrder(cid);
        if (order != null && order.getStatus() == 1) {
            // 已支付订单不能删除
            return false;
        }

        return getOrderDAO().deleteOrder(cid);
    }

    /**
     * 根据客户名称查询订单列表
     * @param customerName 客户名称
     * @return 返回该用户的所有订单列表，按创建时间降序排列
     * @throws IllegalArgumentException 当customerName为null或空字符串时抛出
     */
    public List<Order> getOrdersByUserId(String customerName) {
        // 参数校验
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("客户名称不能为空");
        }

        return getOrderDAO().getOrdersByUserId(customerName);
    }

    /**
     * 获取所有订单列表
     * @return 返回所有订单列表，按创建时间降序排列（最新订单在前）
     */
    public List<Order> getAllOrders() {
        return getOrderDAO().getAllOrders();
    }
}