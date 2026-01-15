import { api } from '@/lib/axios';
import type {
    ProvinceResponse,
    WardResponse,
    ApiResponse,
    PageResponse,
    PaginationParams,
} from '@/types';

export const administrativeApi = {
    /**
     * Get all provinces
     */
    getAllProvinces: async (): Promise<ApiResponse<ProvinceResponse[]>> => {
        return api.get('/api/administrative/provinces');
    },

    /**
     * Get all provinces (paginated)
     */
    getAllProvincesPaginated: async (
        params?: PaginationParams
    ): Promise<ApiResponse<PageResponse<ProvinceResponse>>> => {
        return api.get('/api/administrative/provinces/paginated', { params });
    },

    /**
     * Get provinces by region
     */
    getProvincesByRegion: async (regionId: number): Promise<ApiResponse<ProvinceResponse[]>> => {
        return api.get(`/api/administrative/regions/${regionId}/provinces`);
    },

    /**
     * Get wards by province code
     */
    getWardsByProvince: async (provinceCode: string): Promise<ApiResponse<WardResponse[]>> => {
        return api.get(`/api/administrative/provinces/${provinceCode}/wards`);
    },

    /**
     * Get wards by province (paginated)
     */
    getWardsByProvincePaginated: async (
        provinceCode: string,
        params?: PaginationParams
    ): Promise<ApiResponse<PageResponse<WardResponse>>> => {
        return api.get(`/api/administrative/provinces/${provinceCode}/wards/paginated`, { params });
    },
};
