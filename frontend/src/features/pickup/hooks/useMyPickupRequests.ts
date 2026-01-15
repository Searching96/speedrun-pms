import { useQuery } from '@tanstack/react-query';
import { pickupApi } from '@/api/pickup.api';
import type { PaginationParams } from '@/types';

export function useMyPickupRequests(params?: PaginationParams) {
    return useQuery({
        queryKey: ['pickupRequests', 'my', params],
        queryFn: () => pickupApi.getMyRequests(params),
        refetchInterval: 30000, // Auto-refresh every 30 seconds
        staleTime: 10000, // Consider data stale after 10 seconds
    });
}
