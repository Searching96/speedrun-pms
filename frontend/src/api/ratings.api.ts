import { api } from '@/lib/axios';
import type { CreateRatingRequest, RatingResponse } from '@/types';

export const ratingsApi = {
    /**
     * Submit a rating for a delivered order
     */
    createRating: async (data: CreateRatingRequest): Promise<RatingResponse> => {
        return api.post('/api/ratings', data);
    },

    /**
     * Get rating by order ID
     */
    getRatingByOrder: async (orderId: string): Promise<RatingResponse> => {
        return api.get(`/api/ratings/order/${orderId}`);
    },
};
