import { api } from '@/lib/axios';
import type { CreateOrderRequest, Order, PageOrderResponse, OrderFilters } from './types';

const BASE_URL = '/api/orders';

export const orderApi = {
    getMyOrders: async (filters: OrderFilters = {}) => {
        const params = {
            page: filters.page || 0,
            size: filters.size || 10,
            sortBy: filters.sortBy || 'createdAt',
            order: filters.order || 'desc',
        };
        // Interceptor returns response.data (the body)
        // We assume the body matches PageOrderResponse directly based on OpenAPI
        return api.get<any, PageOrderResponse>(BASE_URL, { params });
    },

    createOrder: async (data: CreateOrderRequest) => {
        return api.post<any, Order>(BASE_URL, data);
    },

    cancelOrder: async (id: string) => {
        return api.put<any, Order>(`${BASE_URL}/${id}/cancel`);
    },

    getOrderByTrackingNumber: async (trackingNumber: string) => {
        return api.get<any, Order>(`${BASE_URL}/${trackingNumber}`);
    }
};
