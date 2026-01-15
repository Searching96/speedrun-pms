import { api } from '@/lib/axios';
import type { Province, Ward } from './types';

export const locationApi = {
    getProvinces: async () => {
        // ApiResponseListProvinceResponse -> data: Province[]
        // Based on OpenAPI: /api/administrative/provinces -> 200 -> content -> ApiResponseListProvinceResponse
        const response = await api.get<any, { data: Province[] }>('/api/administrative/provinces');
        return response.data;
    },

    getWardsByProvince: async (provinceCode: string) => {
        // ApiResponseListWardResponse -> data: Ward[]
        const response = await api.get<any, { data: Ward[] }>(`/api/administrative/provinces/${provinceCode}/wards`);
        return response.data;
    }
};
