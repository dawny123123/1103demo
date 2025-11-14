import axios from 'axios';

const API_BASE_URL = 'http://localhost:9090/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const orderService = {
  // 创建订单
  createOrder: (orderData) => {
    return api.post('/orders', orderData);
  },

  // 获取订单详情
  getOrder: (orderId) => {
    return api.get(`/orders/${orderId}`);
  },

  // 获取所有订单列表
  getAllOrders: () => {
    return api.get('/orders');
  },

  // 根据用户ID获取订单列表
  getOrdersByUserId: (userId) => {
    return api.get(`/orders/user/${userId}`);
  },

  // 更新订单
  updateOrder: (orderId, orderData) => {
    return api.put(`/orders/${orderId}`, orderData);
  },

  // 删除订单
  deleteOrder: (orderId, deleteReason) => {
    // 将删除原因作为查询参数传递
    return api.delete(`/orders/${orderId}`, {
      params: {
        reason: deleteReason
      }
    });
  },

  // 健康检查
  healthCheck: () => {
    return api.get('/orders/health');
  },
};

export default orderService;