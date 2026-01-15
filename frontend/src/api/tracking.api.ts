import { api } from '@/lib/axios';
import type {
    CreateEventRequest,
    TrackingEventResponse,
    TrackingResponse,
} from '@/types';

export const trackingApi = {
    /**
     * Track order by tracking number (public)
     */
    trackOrder: async (trackingNumber: string): Promise<TrackingResponse> => {
        return api.get(`/api/tracking/${trackingNumber}`);
    },

    /**
     * Add a tracking event (staff/shipper only)
     */
    addTrackingEvent: async (data: CreateEventRequest): Promise<TrackingEventResponse> => {
        return api.post('/api/tracking/events', data);
    },
};
