import { api } from '@/lib/axios';
import type { CreateOrderFormValues } from './schema';
import type { OrderResponse, ApiResponse } from '@/types';

export const orderApi = {
    createOrder: async (data: CreateOrderFormValues): Promise<OrderResponse> => {
        return api.post('/api/orders', data);
    },

    getMyOrders: async (page = 0, size = 10) => {
        return api.get('/api/orders', {
            params: { page, size },
        });
    },

    getOrderByTrackingNumber: async (trackingNumber: string): Promise<OrderResponse> => {
        return api.get(`/api/orders/${trackingNumber}`);
    },

    cancelOrder: async (orderId: string): Promise<OrderResponse> => {
        return api.put(`/api/orders/${orderId}/cancel`);
    },

    calculateShippingFee: async (params: {
        senderWardCode: string;
        receiverWardCode: string;
        weightKg: number;
        lengthCm?: number;
        widthCm?: number;
        heightCm?: number;
    }): Promise<number> => {
        const response = await api.post<ApiResponse<number>>('/api/orders/calculate-fee', params);
        return response.data; // This one needs .data because the backend returns ApiResponse
    },

    getAllOrders: async (page = 0, size = 10) => {
        return api.get('/api/orders/all', {
            params: { page, size }
        });
    },
};
