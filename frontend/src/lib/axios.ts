import axios from 'axios';

// Create a configured axios instance
export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || '/',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // If using cookies, otherwise false is fine but good for future
});

// Request Interceptor: Attach Token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response Interceptor: Handle Errors (401, etc.)
api.interceptors.response.use(
    (response) => response.data,
    async (error: any) => {
        const originalRequest = error.config;

        // Handle 401 Unauthorized (Token Expired)
        // Skip for login/register endpoints to allow specific error messages to show
        // Handle 401 Unauthorized (Token Expired)
        // Skip for login/register endpoints to allow specific error messages to show
        if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/login')) {
            // No refresh token available in backend yet.
            // Production behavior: Clear session and redirect to login to force re-auth.
            localStorage.removeItem('accessToken');
            localStorage.removeItem('user');

            // Redirect to login
            if (window.location.pathname !== '/login') {
                window.location.href = '/login?expired=true';
            }

            return Promise.reject(new Error("Session expired. Please login again."));
        }

        // Standardize error message
        const message = error.response?.data?.message || 'Something went wrong';
        return Promise.reject(new Error(message));
    }
);
