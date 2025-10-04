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

export default API;