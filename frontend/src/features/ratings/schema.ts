import { z } from 'zod';

export const ratingSchema = z.object({
    orderId: z.string().min(1, 'Order ID is required'),
    overallRating: z.number().min(1, 'Please provide a rating').max(5),
    deliverySpeedRating: z.number().min(1).max(5).optional(),
    shipperAttitudeRating: z.number().min(1).max(5).optional(),
    comment: z.string().max(500, 'Comment must be less than 500 characters').optional(),
});

export type RatingFormValues = z.infer<typeof ratingSchema>;
