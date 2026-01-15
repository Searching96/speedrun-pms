import { api } from '@/lib/axios';
import type {
    DeliveryTaskResponse,
    FailTaskRequest,
    CompleteTaskRequest,
} from '@/types';

export const shipperApi = {
    /**
     * Get all tasks assigned to current shipper
     */
    getMyTasks: async (): Promise<DeliveryTaskResponse[]> => {
        return api.get('/api/shipper/tasks');
    },

    /**
     * Start a task (mark as IN_PROGRESS)
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
