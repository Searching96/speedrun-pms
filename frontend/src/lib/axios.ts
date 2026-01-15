import axios from 'axios';

// Create a configured axios instance
export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
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
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                // TODO: Replace with actual endpoint to refresh token
                // const { data } = await axios.post('/api/auth/refresh');
                // const newToken = data.accessToken;

                // For now, simulating a failed refresh to force logout since we don't have the endpoint yet
                throw new Error("Refresh token endpoint not implemented");

                // localStorage.setItem('accessToken', newToken);
                // api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
                // return api(originalRequest);
            } catch (refreshError) {
                // If refresh fails, logout
                localStorage.removeItem('accessToken');
                localStorage.removeItem('user');
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }

        // Standardize error message
        const message = error.response?.data?.message || 'Something went wrong';
        return Promise.reject(new Error(message));
    }
);
