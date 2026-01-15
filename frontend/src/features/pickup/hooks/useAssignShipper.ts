import { useMutation, useQueryClient } from '@tanstack/react-query';
import { pickupApi } from '@/api/pickup.api';
import type { AssignShipperRequest } from '@/types';
import { toast } from 'sonner';

export function useAssignShipper() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ requestId, data }: { requestId: string; data: AssignShipperRequest }) =>
            pickupApi.assignShipper(requestId, data),
        onSuccess: () => {
            toast.success('Shipper assigned successfully!');
            // Invalidate all pickup queries to refresh lists
            queryClient.invalidateQueries({ queryKey: ['pickupRequests'] });
        },
        onError: (error: Error) => {
            toast.error(error.message || 'Failed to assign shipper');
        },
    });
}
