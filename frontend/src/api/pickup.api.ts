import { api } from '@/lib/axios';
import type {
    CreatePickupRequest,
    AssignShipperRequest,
    PickupRequestResponse,
    PageResponse,
    PaginationParams,
} from '@/types';

export const pickupApi = {
    /**
     * Create a pickup request for an order
     */
    createPickupRequest: async (data: CreatePickupRequest): Promise<PickupRequestResponse> => {
        return api.post('/api/pickup-requests', data);
    },

    /**
     * Get my pickup requests (paginated)
     */
    getMyRequests: async (params?: PaginationParams): Promise<PageResponse<PickupRequestResponse>> => {
        return api.get('/api/pickup-requests/my-requests', { params });
    },

    /**
     * Get pending pickup requests by ward code
     */
    getPendingRequests: async (wardCode: string): Promise<PickupRequestResponse[]> => {
        return api.get('/api/pickup-requests/pending', { params: { wardCode } });
    },

    /**
     * Assign a shipper to a pickup request
     */
    assignShipper: async (requestId: string, data: AssignShipperRequest): Promise<PickupRequestResponse> => {
        return api.put(`/api/pickup-requests/${requestId}/assign`, data);
    },
};
