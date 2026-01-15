import { z } from 'zod';

export const trackingSchema = z.object({
    trackingNumber: z.string()
        .min(1, 'Tracking number is required')
        .regex(/^[A-Z0-9]+$/, 'Invalid tracking number format'),
});

export type TrackingFormValues = z.infer<typeof trackingSchema>;
