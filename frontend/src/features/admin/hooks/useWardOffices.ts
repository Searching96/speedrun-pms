import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../api';
import type { CreateWardOfficeRequest, AssignWardsRequest } from '../types';
import { toast } from 'sonner';

// Get all ward office pairs
export function useWardOfficePairs() {
    return useQuery({
        queryKey: ['admin', 'ward-offices'],
        queryFn: async () => {
            const response = await adminApi.getWardOfficePairs();
            return response.data || [];
        },
    });
}

// Get specific ward office pair
export function useWardOfficePair(officePairId: string | undefined) {
    return useQuery({
        queryKey: ['admin', 'ward-offices', officePairId],
        queryFn: async () => {
            if (!officePairId) throw new Error('Office pair ID is required');
            const response = await adminApi.getWardOfficePair(officePairId);
            return response.data;
        },
        enabled: !!officePairId,
    });
}

// Get ward assignment status
export function useWardAssignmentStatus(provinceCode?: string) {
    return useQuery({
        queryKey: ['admin', 'wards', 'assignment-status', provinceCode],
        queryFn: async () => {
            const response = await adminApi.getWardAssignmentStatus(provinceCode);
            return response.data || [];
        },
    });
}

// Create ward office pair mutation
export function useCreateWardOfficePair() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateWardOfficeRequest) => adminApi.createWardOfficePair(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['admin', 'ward-offices'] });
            toast.success('Ward office pair created successfully');
        },
        onError: (error: any) => {
            toast.error(error.message || 'Failed to create ward office pair');
        },
    });
}

// Assign wards mutation
export function useAssignWards() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: AssignWardsRequest) => adminApi.assignWardsToOffice(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['admin', 'ward-offices'] });
            queryClient.invalidateQueries({ queryKey: ['admin', 'wards', 'assignment-status'] });
            toast.success('Wards assigned successfully');
        },
        onError: (error: any) => {
            toast.error(error.message || 'Failed to assign wards');
        },
    });
}
