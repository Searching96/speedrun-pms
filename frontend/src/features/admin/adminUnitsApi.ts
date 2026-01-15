import { api } from '@/lib/axios';

export const adminUnitsApi = {
    // Provinces
    getAllProvinces: async () => {
        return api.get('/api/administrative/provinces');
    },

    getProvincesByRegion: async (regionId: number) => {
        return api.get(`/api/administrative/regions/${regionId}/provinces`);
    },

    // Wards
    getWardsByProvince: async (provinceCode: string) => {
        return api.get(`/api/administrative/provinces/${provinceCode}/wards`);
    },

    getWardsByProvincePaginated: async (provinceCode: string, page: number = 0, size: number = 10) => {
        return api.get(`/api/administrative/provinces/${provinceCode}/wards/paginated`, {
            params: { page, size }
        });
    },
};
