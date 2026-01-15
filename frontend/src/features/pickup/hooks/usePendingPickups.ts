import { useQuery } from '@tanstack/react-query';
import { pickupApi } from '@/api/pickup.api';

export function usePendingPickups(wardCode: string) {
    return useQuery({
        queryKey: ['pickupRequests', 'pending', wardCode],
        queryFn: () => pickupApi.getPendingRequests(wardCode),
        enabled: !!wardCode, // Only run query if wardCode is provided
        refetchInterval: 30000, // Auto-refresh every 30 seconds
        staleTime: 10000,
    });
}
