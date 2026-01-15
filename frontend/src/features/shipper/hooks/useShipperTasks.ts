import { useQuery } from '@tanstack/react-query';
import { shipperApi } from '../api';

export function useShipperTasks() {
    return useQuery({
        queryKey: ['shipper', 'tasks'],
        queryFn: () => shipperApi.getTasks(),
        refetchInterval: 30000, // Auto-refresh every 30 seconds
    });
}
