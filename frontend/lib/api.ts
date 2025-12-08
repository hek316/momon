import axios from 'axios';
import { getDeviceId } from './deviceId';

// Backend API base URL
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

/**
 * Axios instance with automatic X-Device-ID header injection
 */
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request interceptor to add X-Device-ID header to all requests
 */
api.interceptors.request.use(
  (config) => {
    const deviceId = getDeviceId();

    // Add X-Device-ID header if device ID exists
    if (deviceId) {
      config.headers['X-Device-ID'] = deviceId;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor for error handling
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Log error for debugging
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;
