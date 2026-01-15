import { api } from '@/lib/axios';
import type {
    LoginRequest,
    CustomerRegisterRequest,
    ApiResponse,
    AuthResponse,
} from '@/types';

export const authApi = {
    /**
     * Login with username (phone) and password
     */
    login: async (credentials: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
        return api.post('/api/auth/login', credentials);
    },

    /**
     * Register a new customer account
     */
    register: async (data: CustomerRegisterRequest): Promise<ApiResponse<void>> => {
        return api.post('/api/auth/register', data);
    },

    /**
     * Get current logged-in user info
     */
    fetchMe: async (): Promise<ApiResponse<unknown>> => {
        return api.get('/api/users/me');
    },
};
