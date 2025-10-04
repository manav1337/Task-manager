import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Add JWT token to requests
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authAPI = {
  login: (credentials) => API.post('/auth/login', credentials),
  register: (userData) => API.post('/auth/register', userData),
};

export const taskAPI = {
  getTasks: () => API.get('/tasks'),
  createTask: (task) => API.post('/tasks', task),
  updateTask: (id, task) => API.put(`/tasks/${id}`, task),
  deleteTask: (id) => API.delete(`/tasks/${id}`),
};

// ADD ADMIN API ENDPOINTS HERE
export const adminAPI = {
  // User management
  getAllUsers: () => API.get('/admin/users'),
  getUserById: (id) => API.get(`/admin/users/${id}`),
  updateUser: (id, userData) => API.put(`/admin/users/${id}`, userData),
  deleteUser: (id) => API.delete(`/admin/users/${id}`),
  
  // Task management (admin perspective)
  getAllTasks: () => API.get('/admin/tasks'),
  getTaskById: (id) => API.get(`/admin/tasks/${id}`),
  updateTask: (id, taskData) => API.put(`/admin/tasks/${id}`, taskData),
  deleteTask: (id) => API.delete(`/admin/tasks/${id}`),
  
  // Statistics
  getDashboardStats: () => API.get('/admin/stats'),
};

export default API;