import { api } from '@/lib/axios';
import type {
    CreateOrderRequest,
    OrderResponse,
    PageResponse,
    PaginationParams,
} from '@/types';

export const ordersApi = {
    /**
     * Get orders for current customer or office (paginated)
     */
    getMyOrders: async (params?: PaginationParams): Promise<PageResponse<OrderResponse>> => {
        return api.get('/api/orders', { params });
    },

    /**
     * Create a new shipping order
     */
    createOrder: async (data: CreateOrderRequest): Promise<OrderResponse> => {
        return api.post('/api/orders', data);
    },

    /**
     * Cancel a pending order
     */
    cancelOrder: async (orderId: string): Promise<OrderResponse> => {
        return api.put(`/api/orders/${orderId}/cancel`);
    },

    /**
     * Get order by tracking number (public)
     */
    getOrderByTrackingNumber: async (trackingNumber: string): Promise<OrderResponse> => {
        return api.get(`/api/orders/${trackingNumber}`);
    },
};
