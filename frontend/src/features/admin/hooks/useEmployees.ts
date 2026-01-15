import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../api';
import type { Employee, UpdateEmployeeRequest } from '../types';
import { toast } from 'sonner';

// Get all employees in the ward manager's office
export const useEmployees = () => {
    return useQuery({
        queryKey: ['employees'],
        queryFn: async () => {
            const response = await adminApi.getEmployeesByWardManager();
            return response.data.data as Employee[];
        },
    });
};

// Get a specific employee
export const useEmployee = (employeeId: string) => {
    return useQuery({
        queryKey: ['employees', employeeId],
        queryFn: async () => {
            const response = await adminApi.getEmployeeByWardManager(employeeId);
            return response.data.data as Employee;
        },
        enabled: !!employeeId,
    });
};

// Create a new staff member
export const useCreateStaff = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: any) => adminApi.createStaffByWardManager(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['employees'] });
            toast.success('Staff created successfully');
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || 'Failed to create staff');
        },
    });
};

// Create a new ward manager
export const useCreateWardManager = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: any) => adminApi.createWardManagerByWardManager(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['employees'] });
            toast.success('Ward Manager created successfully');
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || 'Failed to create ward manager');
        },
    });
};

// Create a new shipper
export const useCreateShipper = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: any) => adminApi.createShipper(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['employees'] });
            toast.success('Shipper created successfully');
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || 'Failed to create shipper');
        },
    });
};

// Update an employee
export const useUpdateEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ employeeId, data }: { employeeId: string; data: UpdateEmployeeRequest }) =>
            adminApi.updateEmployeeByWardManager(employeeId, data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['employees'] });
            queryClient.invalidateQueries({ queryKey: ['employees', variables.employeeId] });
            toast.success('Employee updated successfully');
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || 'Failed to update employee');
        },
    });
};

// Delete an employee
export const useDeleteEmployee = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (employeeId: string) => adminApi.deleteEmployeeByWardManager(employeeId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['employees'] });
            toast.success('Employee deleted successfully');
        },
        onError: (error: any) => {
            toast.error(error.response?.data?.message || 'Failed to delete employee');
        },
    });
};
