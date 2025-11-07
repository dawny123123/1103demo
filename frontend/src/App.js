import React, { useState, useEffect } from 'react';
import './App.css';
import orderService from './services/orderService';

function App() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState({ text: '', type: '' });
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(false);
  const [searchUserId, setSearchUserId] = useState('');
  
  // 表单状态
  const [formData, setFormData] = useState({
    orderId: '',
    userId: '',
    productId: '',
    quantity: 1,
    totalAmount: 0,
    status: 0,
  });

  // 检查后端连接状态
  useEffect(() => {
    checkConnection();
  }, []);

  const checkConnection = async () => {
    try {
      await orderService.healthCheck();
      setIsConnected(true);
      showMessage('后端服务连接成功', 'success');
    } catch (error) {
      setIsConnected(false);
      showMessage('无法连接到后端服务,请确保后端已启动', 'error');
    }
  };

  const showMessage = (text, type) => {
    setMessage({ text, type });
    setTimeout(() => setMessage({ text: '', type: '' }), 3000);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'quantity' || name === 'status' ? parseInt(value) : value,
    });
  };

  const handleCreateOrder = async (e) => {
    e.preventDefault();
    try {
      const response = await orderService.createOrder(formData);
      if (response.data.success) {
        showMessage('订单创建成功!', 'success');
        setFormData({
          orderId: '',
          userId: '',
          productId: '',
          quantity: 1,
          totalAmount: 0,
          status: 0,
        });
        // 如果当前在查看某个用户的订单,刷新列表
        if (searchUserId) {
          handleSearchByUserId();
        }
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '创建订单失败', 'error');
    }
  };

  const handleSearchByUserId = async () => {
    if (!searchUserId.trim()) {
      showMessage('请输入用户ID', 'error');
      return;
    }
    
    setLoading(true);
    try {
      const response = await orderService.getOrdersByUserId(searchUserId);
      if (response.data.success) {
        setOrders(response.data.data);
        showMessage(`找到 ${response.data.count} 个订单`, 'success');
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '查询失败', 'error');
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (orderId, newStatus) => {
    try {
      const order = orders.find(o => o.orderId === orderId);
      const updatedOrder = {
        ...order,
        status: newStatus,
        payTime: newStatus === 1 ? new Date().toISOString() : order.payTime,
      };
      
      const response = await orderService.updateOrder(orderId, updatedOrder);
      if (response.data.success) {
        showMessage('订单状态更新成功!', 'success');
        handleSearchByUserId();
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '更新失败', 'error');
    }
  };

  const handleDeleteOrder = async (orderId) => {
    if (!window.confirm('确定要删除这个订单吗?')) {
      return;
    }
    
    try {
      const response = await orderService.deleteOrder(orderId);
      if (response.data.success) {
        showMessage('订单删除成功!', 'success');
        handleSearchByUserId();
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '删除失败', 'error');
    }
  };

  const getStatusText = (status) => {
    const statusMap = {
      0: '待支付',
      1: '已支付',
      2: '已发货',
      3: '已完成',
      4: '已取消',
    };
    return statusMap[status] || '未知';
  };

  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return '-';
    const date = new Date(dateTimeString);
    return date.toLocaleString('zh-CN');
  };

  return (
    <div className="App">
      <div className="header">
        <h1>📦 订单管理系统</h1>
        <div className={`connection-status ${isConnected ? 'connected' : 'disconnected'}`}>
          {isConnected ? '✅ 后端已连接' : '❌ 后端未连接'}
        </div>
      </div>

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      <div className="order-form">
        <h2>创建新订单</h2>
        <form onSubmit={handleCreateOrder}>
          <div className="form-row">
            <div className="form-group">
              <label>订单ID *</label>
              <input
                type="text"
                name="orderId"
                value={formData.orderId}
                onChange={handleInputChange}
                required
                placeholder="例: ORD001"
              />
            </div>
            <div className="form-group">
              <label>用户ID *</label>
              <input
                type="text"
                name="userId"
                value={formData.userId}
                onChange={handleInputChange}
                required
                placeholder="例: USER001"
              />
            </div>
            <div className="form-group">
              <label>商品ID *</label>
              <input
                type="text"
                name="productId"
                value={formData.productId}
                onChange={handleInputChange}
                required
                placeholder="例: PROD001"
              />
            </div>
          </div>
          
          <div className="form-row">
            <div className="form-group">
              <label>数量 *</label>
              <input
                type="number"
                name="quantity"
                value={formData.quantity}
                onChange={handleInputChange}
                required
                min="1"
              />
            </div>
            <div className="form-group">
              <label>总金额 *</label>
              <input
                type="number"
                name="totalAmount"
                value={formData.totalAmount}
                onChange={handleInputChange}
                required
                min="0"
                step="0.01"
              />
            </div>
            <div className="form-group">
              <label>状态</label>
              <select
                name="status"
                value={formData.status}
                onChange={handleInputChange}
              >
                <option value="0">待支付</option>
                <option value="1">已支付</option>
                <option value="2">已发货</option>
                <option value="3">已完成</option>
                <option value="4">已取消</option>
              </select>
            </div>
          </div>
          
          <div className="button-group">
            <button type="submit" className="btn btn-primary">
              创建订单
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setFormData({
                orderId: '',
                userId: '',
                productId: '',
                quantity: 1,
                totalAmount: 0,
                status: 0,
              })}
            >
              重置
            </button>
          </div>
        </form>
      </div>

      <div className="orders-section">
        <h2>订单查询</h2>
        <div className="search-bar">
          <input
            type="text"
            placeholder="输入用户ID查询订单..."
            value={searchUserId}
            onChange={(e) => setSearchUserId(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearchByUserId()}
          />
          <button className="btn btn-secondary" onClick={handleSearchByUserId}>
            查询
          </button>
        </div>

        {loading ? (
          <div className="loading">加载中...</div>
        ) : orders.length > 0 ? (
          <div className="orders-table">
            <table>
              <thead>
                <tr>
                  <th>订单ID</th>
                  <th>用户ID</th>
                  <th>商品ID</th>
                  <th>数量</th>
                  <th>总金额</th>
                  <th>状态</th>
                  <th>创建时间</th>
                  <th>支付时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.orderId}>
                    <td>{order.orderId}</td>
                    <td>{order.userId}</td>
                    <td>{order.productId}</td>
                    <td>{order.quantity}</td>
                    <td>¥{order.totalAmount}</td>
                    <td>
                      <span className={`status-badge status-${order.status}`}>
                        {getStatusText(order.status)}
                      </span>
                    </td>
                    <td>{formatDateTime(order.createTime)}</td>
                    <td>{formatDateTime(order.payTime)}</td>
                    <td>
                      <div className="action-buttons">
                        {order.status === 0 && (
                          <button
                            className="btn btn-primary"
                            onClick={() => handleUpdateStatus(order.orderId, 1)}
                          >
                            支付
                          </button>
                        )}
                        {order.status === 1 && (
                          <button
                            className="btn btn-warning"
                            onClick={() => handleUpdateStatus(order.orderId, 2)}
                          >
                            发货
                          </button>
                        )}
                        {order.status === 2 && (
                          <button
                            className="btn btn-primary"
                            onClick={() => handleUpdateStatus(order.orderId, 3)}
                          >
                            完成
                          </button>
                        )}
                        {order.status === 0 && (
                          <button
                            className="btn btn-danger"
                            onClick={() => handleDeleteOrder(order.orderId)}
                          >
                            删除
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : searchUserId ? (
          <div className="no-orders">暂无订单数据</div>
        ) : (
          <div className="no-orders">请输入用户ID查询订单</div>
        )}
      </div>
    </div>
  );
}

export default App;
