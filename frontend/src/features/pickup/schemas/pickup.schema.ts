import { z } from 'zod';
import type { TimeSlot } from '@/types';

// Time slot enum for validation
export const timeSlotEnum = z.enum(['MORNING', 'AFTERNOON', 'EVENING']);

// Schema for creating a pickup request
export const createPickupSchema = z.object({
    orderId: z.string().min(1, 'Order is required'),
    pickupAddress: z.string().min(5, 'Pickup address must be at least 5 characters'),
    pickupWardCode: z.string().min(1, 'Ward is required'),
    pickupContactName: z.string().min(2, 'Contact name must be at least 2 characters'),
    pickupContactPhone: z
        .string()
        .min(10, 'Phone number must be at least 10 digits')
        .regex(/^[0-9]+$/, 'Phone number must contain only digits'),
    preferredDate: z.string().refine(
        (date) => {
            const selectedDate = new Date(date);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            return selectedDate >= today;
        },
        { message: 'Preferred date must be today or in the future' }
    ),
    preferredTimeSlot: timeSlotEnum.optional(),
});

export type CreatePickupSchema = z.infer<typeof createPickupSchema>;

// Helper to convert TimeSlot to display text
export const timeSlotLabels: Record<TimeSlot, string> = {
    MORNING: 'Morning (8:00 - 12:00)',
    AFTERNOON: 'Afternoon (13:00 - 17:00)',
    EVENING: 'Evening (17:00 - 20:00)',
};
