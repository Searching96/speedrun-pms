import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { shipperApi } from '../api';
import type { CompleteTaskRequest, FailTaskRequest } from '@/types';

export function useStartTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (taskId: string) => shipperApi.startTask(taskId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['shipper', 'tasks'] });
            toast.success('Task started');
        },
        onError: (error: Error) => {
            toast.error(error.message || 'Failed to start task');
        },
    });
}

export function useCompleteTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ taskId, data }: { taskId: string; data: CompleteTaskRequest }) =>
            shipperApi.completeTask(taskId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['shipper', 'tasks'] });
            toast.success('Task completed successfully');
        },
        onError: (error: Error) => {
            toast.error(error.message || 'Failed to complete task');
        },
    });
}

export function useFailTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ taskId, data }: { taskId: string; data: FailTaskRequest }) =>
            shipperApi.failTask(taskId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['shipper', 'tasks'] });
            toast.success('Task marked as failed');
        },
        onError: (error: Error) => {
            toast.error(error.message || 'Failed to update task');
        },
    });
}
