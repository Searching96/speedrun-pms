import axios from 'axios';

// Augment Axios to return T instead of AxiosResponse<T> since we use a response interceptor
declare module 'axios' {
    export interface AxiosInstance {
        request<T = any, R = T>(config: import('axios').AxiosRequestConfig): Promise<R>;
        get<T = any, R = T>(url: string, config?: import('axios').AxiosRequestConfig): Promise<R>;
        delete<T = any, R = T>(url: string, config?: import('axios').AxiosRequestConfig): Promise<R>;
        head<T = any, R = T>(url: string, config?: import('axios').AxiosRequestConfig): Promise<R>;
        options<T = any, R = T>(url: string, config?: import('axios').AxiosRequestConfig): Promise<R>;
        post<T = any, R = T>(url: string, data?: any, config?: import('axios').AxiosRequestConfig): Promise<R>;
        put<T = any, R = T>(url: string, data?: any, config?: import('axios').AxiosRequestConfig): Promise<R>;
        patch<T = any, R = T>(url: string, data?: any, config?: import('axios').AxiosRequestConfig): Promise<R>;
    }
}


// Create a configured axios instance
export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || '/',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // If using cookies, otherwise false is fine but good for future
});

// Request Interceptor: No longer need to attach Bearer token manually as we use HTTP-only cookies
api.interceptors.request.use(
    (config) => config,
    (error) => Promise.reject(error)
);

// Response Interceptor: Handle Errors (401, etc.)
api.interceptors.response.use(
    (response) => response.data,
    async (error: any) => {
        const originalRequest = error.config;

        // Handle 401 Unauthorized (Token Expired)
        if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/login')) {
            // Clear local user data
            localStorage.removeItem('user');

            // Redirect to login if not already there
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
