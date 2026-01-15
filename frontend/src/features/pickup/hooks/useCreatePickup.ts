import { useMutation, useQueryClient } from '@tanstack/react-query';
import { pickupApi } from '@/api/pickup.api';
import type { CreatePickupRequest } from '@/types';
import { toast } from 'sonner';

export function useCreatePickup() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreatePickupRequest) => pickupApi.createPickupRequest(data),
        onSuccess: () => {
            toast.success('Pickup request created successfully!');
            // Invalidate queries to refresh the list
            queryClient.invalidateQueries({ queryKey: ['pickupRequests', 'my'] });
        },
        onError: (error: Error) => {
            toast.error(error.message || 'Failed to create pickup request');
        },
    });
}
