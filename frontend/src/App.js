import React, { useState, useEffect } from 'react';
import './App.css';
import orderService from './services/orderService';
import influenceService from './services/influenceService';

function App() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState({ text: '', type: '' });
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(false);
  const [searchCustomerName, setSearchCustomerName] = useState('');
  
  // 影响力模块状态
  const [influences, setInfluences] = useState([]);
  const [influenceLoading, setInfluenceLoading] = useState(false);
  const [searchInfluenceType, setSearchInfluenceType] = useState('');
  
  // 影响力类型常量定义
  const INFLUENCE_TYPE_SA_TRAINING = 'SA_TRAINING';
  const INFLUENCE_TYPE_LOGO = 'LOGO';
  const INFLUENCE_TYPE_CASE_STUDY = 'CASE_STUDY';
  const INFLUENCE_TYPE_COMPETITOR_ANALYSIS = 'COMPETITOR_ANALYSIS';
  const INFLUENCE_TYPE_DEMO = 'DEMO';
  const INFLUENCE_TYPE_CONFERENCE_SHARING = 'CONFERENCE_SHARING';
  
  // 影响力类型名称映射
  const INFLUENCE_TYPE_NAMES = {
    [INFLUENCE_TYPE_SA_TRAINING]: 'SA培训',
    [INFLUENCE_TYPE_LOGO]: 'logo',
    [INFLUENCE_TYPE_CASE_STUDY]: '案例',
    [INFLUENCE_TYPE_COMPETITOR_ANALYSIS]: '竞品分析',
    [INFLUENCE_TYPE_DEMO]: 'demo',
    [INFLUENCE_TYPE_CONFERENCE_SHARING]: '大会分享'
  };
  
  // 影响力状态常量
  const INFLUENCE_STATUS_PLANNED = 'PLANNED';
  const INFLUENCE_STATUS_IN_PROGRESS = 'IN_PROGRESS';
  const INFLUENCE_STATUS_COMPLETED = 'COMPLETED';
  const INFLUENCE_STATUS_CANCELLED = 'CANCELLED';
  
  // 影响力状态名称映射
  const INFLUENCE_STATUS_NAMES = {
    [INFLUENCE_STATUS_PLANNED]: '计划中',
    [INFLUENCE_STATUS_IN_PROGRESS]: '进行中',
    [INFLUENCE_STATUS_COMPLETED]: '已完成',
    [INFLUENCE_STATUS_CANCELLED]: '已取消'
  };
  
  // 影响力表单状态
  const [influenceFormData, setInfluenceFormData] = useState({
    id: '',
    name: '',
    type: INFLUENCE_TYPE_SA_TRAINING,
    status: INFLUENCE_STATUS_PLANNED,
    eventTime: '',
    link: '',
    remark: '',
    imageUrls: []
  });
  
  // 产品版本常量定义
  const PRODUCT_QODER = 'QODER';
  const PRODUCT_LINGMA_ENTERPRISE = 'LINGMA_ENTERPRISE';
  const PRODUCT_LINGMA_EXCLUSIVE = 'LINGMA_EXCLUSIVE';
  
  // 产品单价定义
  const PRODUCT_PRICES = {
    [PRODUCT_QODER]: 140,
    [PRODUCT_LINGMA_ENTERPRISE]: 79,
    [PRODUCT_LINGMA_EXCLUSIVE]: 159
  };
  
  // 产品名称映射
  const PRODUCT_NAMES = {
    [PRODUCT_QODER]: 'Qoder',
    [PRODUCT_LINGMA_ENTERPRISE]: '灵码企业版',
    [PRODUCT_LINGMA_EXCLUSIVE]: '灵码专属版'
  };
  
  // 表单状态
  const [formData, setFormData] = useState({
    cid: '',
    customerName: '',
    productVersion: PRODUCT_LINGMA_EXCLUSIVE,
    devScale: 1,
    purchasedLicCount: 1,
    totalAmount: 159, // 默认灵码专属版价格
    status: 0,
    description: ''
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
      // 连接成功后自动加载所有订单
      loadAllOrders();
    } catch (error) {
      setIsConnected(false);
      showMessage('无法连接到后端服务,请确保后端已启动', 'error');
    }
  };

  const loadAllOrders = async () => {
    setLoading(true);
    try {
      const response = await orderService.getAllOrders();
      if (response.data.success) {
        setOrders(response.data.data);
        showMessage(`加载到 ${response.data.count} 个订单`, 'success');
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '加载订单失败', 'error');
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  const showMessage = (text, type) => {
    setMessage({ text, type });
    // 如果是警告或较长的消息，显示5秒，否则3秒
    const duration = type === 'warning' || text.length > 50 ? 5000 : 3000;
    setTimeout(() => setMessage({ text: '', type: '' }), duration);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    let newFormData = {
      ...formData,
      [name]: name === 'devScale' || name === 'purchasedLicCount' || name === 'status' ? parseInt(value) : value,
    };
    
    // 如果是产品版本或已购LIC数变化，自动计算总金额
    if (name === 'productVersion' || name === 'purchasedLicCount') {
      const productVersion = name === 'productVersion' ? value : formData.productVersion;
      const purchasedLicCount = name === 'purchasedLicCount' ? parseInt(value) || 0 : formData.purchasedLicCount;
      newFormData.totalAmount = PRODUCT_PRICES[productVersion] * purchasedLicCount;
    }
    
    setFormData(newFormData);
  };

  const handleCreateOrder = async (e) => {
    e.preventDefault();
    try {
      const response = await orderService.createOrder(formData);
      if (response.data.success) {
        showMessage('订单创建成功!', 'success');
        setFormData({
          cid: '',
          customerName: '',
          productVersion: PRODUCT_LINGMA_EXCLUSIVE,
          devScale: 1,
          purchasedLicCount: 1,
          totalAmount: 159,
          status: 0,
          description: ''
        });
        // 创建订单后刷新列表
        if (searchCustomerName) {
          handleSearchByCustomerName();
        } else {
          loadAllOrders();
        }
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '创建订单失败', 'error');
    }
  };

  const handleSearchByCustomerName = async () => {
    if (!searchCustomerName.trim()) {
      showMessage('请输入客户名称', 'error');
      return;
    }
    
    setLoading(true);
    try {
      const response = await orderService.getOrdersByUserId(searchCustomerName);
      if (response.data.success) {
        // 确保数据是数组
        setOrders(response.data.data || []);
        showMessage(`找到 ${response.data.count} 个订单`, 'success');
      } else {
        // 处理API错误
        showMessage(response.data.message || '查询失败', 'error');
        setOrders([]);
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '查询失败', 'error');
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (cid, newStatus) => {
    // 提示输入修改原因（可选，但建议填写）
    const updateReason = window.prompt('请输入修改原因（可选，建议填写）:');
    
    try {
      const order = orders.find(o => o.cid === cid);
      const updatedOrder = {
        ...order,
        status: newStatus,
        payTime: newStatus === 1 ? new Date().toISOString() : order.payTime,
        // 如果提供了修改原因，则追加到描述中
        description: updateReason && updateReason.trim() 
          ? `${order.description || ''}${order.description ? '\n' : ''}[${new Date().toLocaleString('zh-CN')}] 状态修改: ${getStatusText(order.status)} → ${getStatusText(newStatus)}, 原因: ${updateReason}` 
          : order.description
      };
      
      const response = await orderService.updateOrder(cid, updatedOrder);
      if (response.data.success) {
        showMessage(updateReason ? `订单状态更新成功! 修改原因: ${updateReason}` : '订单状态更新成功!', 'success');
        // 根据当前视图刷新列表
        if (searchCustomerName) {
          handleSearchByCustomerName();
        } else {
          loadAllOrders();
        }
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '更新失败', 'error');
    }
  };

  const handleDeleteOrder = async (cid) => {
    // 弹出输入框要求输入删除原因
    const deleteReason = window.prompt('请输入删除原因（必填）:');
    
    // 如果用户取消或未输入删除原因，则终止删除操作
    if (!deleteReason || deleteReason.trim() === '') {
      showMessage('删除操作已取消：必须提供删除原因', 'warning');
      return;
    }
    
    // 再次确认删除
    if (!window.confirm(`确定要删除这个订单吗？\n删除原因: ${deleteReason}`)) {
      return;
    }
    
    try {
      // 将删除原因作为查询参数传递
      const response = await orderService.deleteOrder(cid, deleteReason);
      if (response.data.success) {
        showMessage(`订单删除成功! 删除原因: ${deleteReason}`, 'success');
        // 根据当前视图刷新列表
        if (searchCustomerName) {
          handleSearchByCustomerName();
        } else {
          loadAllOrders();
        }
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '删除失败', 'error');
    }
  };

  const getStatusText = (status) => {
    const statusMap = {
      0: '售前',
      1: '下单',
      2: '扩容',
      3: '流失',
    };
    return statusMap[status] || '未知';
  };

  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return '-';
    const date = new Date(dateTimeString);
    return date.toLocaleString('zh-CN');
  };

  // ========== 影响力模块处理函数 ==========
  
  // 加载所有影响力记录
  const loadAllInfluences = async () => {
    setInfluenceLoading(true);
    try {
      const response = await influenceService.getAllInfluences();
      if (response.data.success) {
        setInfluences(response.data.data);
        showMessage(`加载到 ${response.data.count} 条影响力记录`, 'success');
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '加载影响力记录失败', 'error');
      setInfluences([]);
    } finally {
      setInfluenceLoading(false);
    }
  };

  // 影响力表单输入处理
  const handleInfluenceInputChange = (e) => {
    const { name, value } = e.target;
    setInfluenceFormData({
      ...influenceFormData,
      [name]: value
    });
  };

  // 创建影响力记录
  const handleCreateInfluence = async (e) => {
    e.preventDefault();
    try {
      // 生成唯一ID（只在用户未填写时自动生成）
      const finalId = influenceFormData.id && influenceFormData.id.trim() 
        ? influenceFormData.id.trim()
        : `INF_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`;
      
      // 将日期格式转换为后端需要的LocalDateTime格式
      // datetime-local 输入框格式："2025-11-14T14:30"，需要添加秒":00"
      const dataToSubmit = {
        ...influenceFormData,
        id: finalId,
        eventTime: influenceFormData.eventTime ? `${influenceFormData.eventTime}:00` : null
      };
      
      // 调试输出：查看提交的数据
      console.log('提交的数据：', dataToSubmit);
      
      const response = await influenceService.createInfluence(dataToSubmit);
      if (response.data.success) {
        showMessage('影响力记录创建成功!', 'success');
        setInfluenceFormData({
          id: '',
          name: '',
          type: INFLUENCE_TYPE_SA_TRAINING,
          status: INFLUENCE_STATUS_PLANNED,
          eventTime: '',
          link: '',
          remark: '',
          imageUrls: []
        });
        // 创建后刷新列表
        if (searchInfluenceType) {
          handleSearchByInfluenceType();
        } else {
          loadAllInfluences();
        }
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '创建影响力记录失败', 'error');
    }
  };

  // 按类型查询影响力记录
  const handleSearchByInfluenceType = async () => {
    if (!searchInfluenceType.trim()) {
      showMessage('请选择活动类型', 'error');
      return;
    }
    
    setInfluenceLoading(true);
    try {
      const response = await influenceService.getInfluencesByType(searchInfluenceType);
      if (response.data.success) {
        setInfluences(response.data.data || []);
        showMessage(`找到 ${response.data.count} 条影响力记录`, 'success');
      } else {
        showMessage(response.data.message || '查询失败', 'error');
        setInfluences([]);
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '查询失败', 'error');
      setInfluences([]);
    } finally {
      setInfluenceLoading(false);
    }
  };

  // 更新影响力状态
  const handleUpdateInfluenceStatus = async (id, newStatus) => {
    try {
      const influence = influences.find(i => i.id === id);
      const updatedInfluence = {
        ...influence,
        status: newStatus
      };
      
      const response = await influenceService.updateInfluence(id, updatedInfluence);
      if (response.data.success) {
        showMessage('影响力记录状态更新成功!', 'success');
        if (searchInfluenceType) {
          handleSearchByInfluenceType();
        } else {
          loadAllInfluences();
        }
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '更新失败', 'error');
    }
  };

  // 删除影响力记录
  const handleDeleteInfluence = async (id) => {
    if (!window.confirm('确定要删除这条影响力记录吗？')) {
      return;
    }
    
    try {
      const response = await influenceService.deleteInfluence(id);
      if (response.data.success) {
        showMessage('影响力记录删除成功!', 'success');
        if (searchInfluenceType) {
          handleSearchByInfluenceType();
        } else {
          loadAllInfluences();
        }
      }
    } catch (error) {
      showMessage(error.response?.data?.message || '删除失败', 'error');
    }
  };

  return (
    <div className="App">
      <div className="header">
        <h1>📦 软件许可证订单管理系统</h1>
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
              <label>CID *</label>
              <input
                type="text"
                name="cid"
                value={formData.cid}
                onChange={handleInputChange}
                required
                placeholder="例: CID001"
              />
            </div>
            <div className="form-group">
              <label>客户名称 *</label>
              <input
                type="text"
                name="customerName"
                value={formData.customerName}
                onChange={handleInputChange}
                required
                placeholder="例: XX科技公司"
              />
            </div>
            <div className="form-group">
              <label>产品版本 *</label>
              <select
                name="productVersion"
                value={formData.productVersion}
                onChange={handleInputChange}
                required
              >
                <option value={PRODUCT_QODER}>Qoder</option>
                <option value={PRODUCT_LINGMA_ENTERPRISE}>灵码企业版</option>
                <option value={PRODUCT_LINGMA_EXCLUSIVE}>灵码专属版</option>
              </select>
            </div>
          </div>
          
          <div className="form-row">
            <div className="form-group">
              <label>研发规模 *</label>
              <input
                type="number"
                name="devScale"
                value={formData.devScale}
                onChange={handleInputChange}
                required
                min="1"
              />
            </div>
            <div className="form-group">
              <label>已购LIC数 *</label>
              <input
                type="number"
                name="purchasedLicCount"
                value={formData.purchasedLicCount}
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
                readOnly // 设置为只读，因为金额是自动计算的
              />
              <div className="form-hint">金额根据产品版本和已购LIC数自动计算</div>
            </div>
            <div className="form-group">
              <label>状态</label>
              <select
                name="status"
                value={formData.status}
                onChange={handleInputChange}
              >
                <option value="0">售前</option>
                <option value="1">下单</option>
                <option value="2">扩容</option>
                <option value="3">流失</option>
              </select>
            </div>
          </div>
          
          {/* 添加描述字段输入 */}
          <div className="form-row">
            <div className="form-group full-width">
              <label>订单描述</label>
              <input
                type="text"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="请输入订单描述..."
              />
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
                cid: '',
                customerName: '',
                productVersion: PRODUCT_LINGMA_EXCLUSIVE,
                devScale: 1,
                purchasedLicCount: 1,
                totalAmount: 159,
                status: 0,
                description: ''
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
            placeholder="输入客户名称查询订单..."
            value={searchCustomerName}
            onChange={(e) => setSearchCustomerName(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearchByCustomerName()}
          />
          <button className="btn btn-secondary" onClick={handleSearchByCustomerName}>
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
                  <th>CID</th>
                  <th>客户名称</th>
                  <th>产品版本</th>
                  <th>研发规模</th>
                  <th>已购LIC数</th>
                  <th>总金额</th>
                  <th>状态</th>
                  <th>描述</th>
                  <th>创建时间</th>
                  <th>支付时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.cid}>
                    <td>{order.cid}</td>
                    <td>{order.customerName}</td>
                    <td>{PRODUCT_NAMES[order.productVersion] || order.productVersion}</td>
                    <td>{order.devScale}</td>
                    <td>{order.purchasedLicCount}</td>
                    <td>¥{order.totalAmount}</td>
                    <td>
                      <span className={`status-badge status-${order.status}`}>
                        {getStatusText(order.status)}
                      </span>
                    </td>
                    <td>
                      <div className="description-cell" title={order.description}>
                        {order.description ? (
                          <pre className="description-text">{order.description}</pre>
                        ) : '-'}
                      </div>
                    </td>
                    <td>{formatDateTime(order.createTime)}</td>
                    <td>{formatDateTime(order.payTime)}</td>
                    <td>
                      <div className="action-buttons">
                        {order.status === 0 && (
                          <button
                            className="btn btn-primary"
                            onClick={() => handleUpdateStatus(order.cid, 1)}
                          >
                            下单
                          </button>
                        )}
                        {order.status === 1 && (
                          <button
                            className="btn btn-warning"
                            onClick={() => handleUpdateStatus(order.cid, 2)}
                          >
                            扩容
                          </button>
                        )}
                        {order.status === 2 && (
                          <button
                            className="btn btn-primary"
                            onClick={() => handleUpdateStatus(order.cid, 3)}
                          >
                            流失
                          </button>
                        )}
                        {order.status === 0 && (
                          <button
                            className="btn btn-danger"
                            onClick={() => handleDeleteOrder(order.cid)}
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
        ) : searchCustomerName ? (
          <div className="no-orders">暂无订单数据</div>
        ) : (
          <div className="no-orders">暂无订单数据</div>
        )}
      </div>

      {/* 影响力管理区块 */}
      <div className="influence-section">
        <h2>🌟 影响力管理</h2>
        
        {/* 创建影响力记录表单 */}
        <div className="influence-form">
          <h3>创建影响力记录</h3>
          <form onSubmit={handleCreateInfluence}>
            <div className="form-row">
              <div className="form-group">
                <label>ID（可选，留空自动生成）</label>
                <input
                  type="text"
                  name="id"
                  value={influenceFormData.id}
                  onChange={handleInfluenceInputChange}
                  placeholder="留空将自动生成ID"
                />
              </div>
              <div className="form-group">
                <label>名称 *</label>
                <input
                  type="text"
                  name="name"
                  value={influenceFormData.name}
                  onChange={handleInfluenceInputChange}
                  required
                  maxLength="200"
                  placeholder="例: Qoder产品SA培训"
                />
              </div>
              <div className="form-group">
                <label>类型 *</label>
                <select
                  name="type"
                  value={influenceFormData.type}
                  onChange={handleInfluenceInputChange}
                  required
                >
                  <option value={INFLUENCE_TYPE_SA_TRAINING}>SA培训</option>
                  <option value={INFLUENCE_TYPE_LOGO}>logo</option>
                  <option value={INFLUENCE_TYPE_CASE_STUDY}>案例</option>
                  <option value={INFLUENCE_TYPE_COMPETITOR_ANALYSIS}>竞品分析</option>
                  <option value={INFLUENCE_TYPE_DEMO}>demo</option>
                  <option value={INFLUENCE_TYPE_CONFERENCE_SHARING}>大会分享</option>
                </select>
              </div>
            </div>
            
            <div className="form-row">
              <div className="form-group">
                <label>状态 *</label>
                <select
                  name="status"
                  value={influenceFormData.status}
                  onChange={handleInfluenceInputChange}
                  required
                >
                  <option value={INFLUENCE_STATUS_PLANNED}>计划中</option>
                  <option value={INFLUENCE_STATUS_IN_PROGRESS}>进行中</option>
                  <option value={INFLUENCE_STATUS_COMPLETED}>已完成</option>
                  <option value={INFLUENCE_STATUS_CANCELLED}>已取消</option>
                </select>
              </div>
              <div className="form-group">
                <label>活动时间 *</label>
                <input
                  type="datetime-local"
                  name="eventTime"
                  value={influenceFormData.eventTime}
                  onChange={handleInfluenceInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>链接</label>
                <input
                  type="url"
                  name="link"
                  value={influenceFormData.link}
                  onChange={handleInfluenceInputChange}
                  placeholder="https://example.com"
                />
              </div>
            </div>
            
            <div className="form-row">
              <div className="form-group full-width">
                <label>备注</label>
                <textarea
                  name="remark"
                  value={influenceFormData.remark}
                  onChange={handleInfluenceInputChange}
                  maxLength="2000"
                  rows="3"
                  placeholder="请输入备注信息..."
                />
              </div>
            </div>
            
            <div className="button-group">
              <button type="submit" className="btn btn-primary">
                创建影响力记录
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setInfluenceFormData({
                  id: '',
                  name: '',
                  type: INFLUENCE_TYPE_SA_TRAINING,
                  status: INFLUENCE_STATUS_PLANNED,
                  eventTime: '',
                  link: '',
                  remark: '',
                  imageUrls: []
                })}
              >
                重置
              </button>
            </div>
          </form>
        </div>

        {/* 影响力记录查询和列表 */}
        <div className="influences-list-section">
          <h3>影响力记录查询</h3>
          <div className="search-bar">
            <select
              value={searchInfluenceType}
              onChange={(e) => setSearchInfluenceType(e.target.value)}
            >
              <option value="">所有类型</option>
              <option value={INFLUENCE_TYPE_SA_TRAINING}>SA培训</option>
              <option value={INFLUENCE_TYPE_LOGO}>logo</option>
              <option value={INFLUENCE_TYPE_CASE_STUDY}>案例</option>
              <option value={INFLUENCE_TYPE_COMPETITOR_ANALYSIS}>竞品分析</option>
              <option value={INFLUENCE_TYPE_DEMO}>demo</option>
              <option value={INFLUENCE_TYPE_CONFERENCE_SHARING}>大会分享</option>
            </select>
            <button className="btn btn-secondary" onClick={handleSearchByInfluenceType}>
              查询
            </button>
            <button className="btn btn-secondary" onClick={loadAllInfluences}>
              加载全部
            </button>
          </div>

          {influenceLoading ? (
            <div className="loading">加载中...</div>
          ) : influences.length > 0 ? (
            <div className="influences-table">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>名称</th>
                    <th>类型</th>
                    <th>状态</th>
                    <th>活动时间</th>
                    <th>链接</th>
                    <th>备注</th>
                    <th>创建时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  {influences.map((influence) => (
                    <tr key={influence.id}>
                      <td>{influence.id}</td>
                      <td>{influence.name}</td>
                      <td>{INFLUENCE_TYPE_NAMES[influence.type] || influence.type}</td>
                      <td>
                        <span className={`influence-status influence-status-${influence.status.toLowerCase()}`}>
                          {INFLUENCE_STATUS_NAMES[influence.status] || influence.status}
                        </span>
                      </td>
                      <td>{formatDateTime(influence.eventTime)}</td>
                      <td>
                        {influence.link ? (
                          <a href={influence.link} target="_blank" rel="noopener noreferrer">
                            查看
                          </a>
                        ) : '-'}
                      </td>
                      <td>
                        <div className="remark-cell" title={influence.remark}>
                          {influence.remark ? influence.remark.substring(0, 50) + (influence.remark.length > 50 ? '...' : '') : '-'}
                        </div>
                      </td>
                      <td>{formatDateTime(influence.createTime)}</td>
                      <td>
                        <div className="action-buttons">
                          {influence.status === INFLUENCE_STATUS_PLANNED && (
                            <button
                              className="btn btn-primary btn-sm"
                              onClick={() => handleUpdateInfluenceStatus(influence.id, INFLUENCE_STATUS_IN_PROGRESS)}
                            >
                              开始
                            </button>
                          )}
                          {influence.status === INFLUENCE_STATUS_IN_PROGRESS && (
                            <button
                              className="btn btn-success btn-sm"
                              onClick={() => handleUpdateInfluenceStatus(influence.id, INFLUENCE_STATUS_COMPLETED)}
                            >
                              完成
                            </button>
                          )}
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() => handleDeleteInfluence(influence.id)}
                          >
                            删除
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="no-data">暂无影响力记录</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;