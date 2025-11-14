import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/influences';

const influenceService = {
  // 健康检查
  healthCheck: () => {
    return axios.get(`${API_BASE_URL}/health`);
  },

  // 创建影响力记录
  createInfluence: (influenceData) => {
    return axios.post(API_BASE_URL, influenceData);
  },

  // 获取所有影响力记录
  getAllInfluences: () => {
    return axios.get(API_BASE_URL);
  },

  // 根据ID获取单个影响力记录
  getInfluenceById: (id) => {
    return axios.get(`${API_BASE_URL}/${id}`);
  },

  // 根据类型获取影响力记录列表
  getInfluencesByType: (type) => {
    return axios.get(`${API_BASE_URL}/type/${type}`);
  },

  // 更新影响力记录
  updateInfluence: (id, influenceData) => {
    return axios.put(`${API_BASE_URL}/${id}`, influenceData);
  },

  // 删除影响力记录
  deleteInfluence: (id) => {
    return axios.delete(`${API_BASE_URL}/${id}`);
  }
};

export default influenceService;
