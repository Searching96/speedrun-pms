import { api } from '@/lib/axios';
import type { TrackingResponse } from '@/types';

export const trackingApi = {
    /**
     * Get tracking information by tracking number (public endpoint)
     */
    getTracking: async (trackingNumber: string): Promise<TrackingResponse> => {
        return api.get(`/api/tracking/${trackingNumber}`);
    },
};
