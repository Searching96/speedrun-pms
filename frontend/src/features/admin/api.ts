import { api } from '@/lib/axios';

export const adminApi = {
    // Ward Manager Employee Management
    createWardManagerByWardManager: async (data: any) => {
        return api.post('/api/ward-manager/employees/ward-manager', data);
    },

    createStaffByWardManager: async (data: any) => {
        return api.post('/api/ward-manager/employees/staff', data);
    },

    createShipper: async (data: any) => {
        return api.post('/api/ward-manager/employees/shipper', data);
    },

    getEmployeesByWardManager: async () => {
        return api.get('/api/ward-manager/employees');
    },

    getEmployeeByWardManager: async (employeeId: string) => {
        return api.get(`/api/ward-manager/employees/${employeeId}`);
    },

    updateEmployeeByWardManager: async (employeeId: string, data: any) => {
        return api.put(`/api/ward-manager/employees/${employeeId}`, data);
    },

    deleteEmployeeByWardManager: async (employeeId: string) => {
        return api.delete(`/api/ward-manager/employees/${employeeId}`);
    },

    // Province Admin Employee Management
    createWardManager: async (data: any) => {
        return api.post('/api/province-admin/employees/ward-manager', data);
    },

    createStaff: async (data: any) => {
        return api.post('/api/province-admin/employees/staff', data);
    },

    createProvinceAdmin: async (data: any) => {
        return api.post('/api/province-admin/employees/province-admin', data);
    },

    // HUB Admin Management
    createHubAdmin: async (data: any) => {
        return api.post('/api/hub-admins/register', data);
    },

    // System Admin Management
    createSystemAdmin: async (data: any) => {
        return api.post('/api/dashboard/register-admin', data);
    },

    // Ward Office Management
    getWardOfficePairs: async () => {
        return api.get('/api/province-admin/ward-offices');
    },

    createWardOfficePair: async (data: any) => {
        return api.post('/api/province-admin/ward-offices', data);
    },

    assignWardsToOffice: async (data: any) => {
        return api.post('/api/province-admin/ward-offices/assign-wards', data);
    },

    getWardOfficePair: async (officePairId: string) => {
        return api.get(`/api/province-admin/ward-offices/${officePairId}`);
    },

    getWardAssignmentStatus: async (provinceCode?: string) => {
        return api.get('/api/province-admin/wards/assignment-status', {
            params: provinceCode ? { provinceCode } : {}
        });
    },

    // Global Stats
    getSystemStats: async () => {
        return api.get('/api/dashboard/stats');
    },
};
