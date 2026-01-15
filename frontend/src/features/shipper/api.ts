import { api } from '@/lib/axios';
import type { DeliveryTaskResponse, CompleteTaskRequest, FailTaskRequest } from '@/types';

export const shipperApi = {
    /**
     * Get all tasks assigned to current shipper
     */
    getTasks: async (): Promise<DeliveryTaskResponse[]> => {
        return api.get('/api/shipper/tasks');
    },

    /**
     * Start a task
     */
    startTask: async (taskId: string): Promise<DeliveryTaskResponse> => {
        return api.put(`/api/shipper/tasks/${taskId}/start`);
    },

    /**
     * Complete a task
     */
    completeTask: async (taskId: string, data: CompleteTaskRequest): Promise<DeliveryTaskResponse> => {
        return api.put(`/api/shipper/tasks/${taskId}/complete`, data);
    },

    /**
     * Fail a task
     */
    failTask: async (taskId: string, data: FailTaskRequest): Promise<DeliveryTaskResponse> => {
        return api.put(`/api/shipper/tasks/${taskId}/fail`, data);
    },
};
