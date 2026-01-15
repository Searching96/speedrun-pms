// Employee types based on OpenAPI EmployeeResponse
export interface Employee {
    employeeId: string; // UUID
    fullName: string;
    phoneNumber: string;
    email: string;
    role: string;
    officeName: string;
}

export interface CreateEmployeeRequest {
    fullName: string;
    phoneNumber: string; // 10 digits
    password: string; // min 6 characters
    email: string;
    officeId?: string; // UUID - required for some roles
}

export interface UpdateEmployeeRequest {
    fullName: string;
    phoneNumber: string; // 10 digits
    email: string;
    password?: string; // Optional
}

// Ward Office types based on OpenAPI WardOfficePairResponse
export interface OfficeInfo {
    officeId: string; // UUID
    officeName: string;
    officeEmail: string;
    officePhoneNumber: string;
    officeAddress: string;
    officeType: string;
    parentOfficeId?: string; // UUID
    parentOfficeName?: string;
}

export interface WardInfo {
    wardCode: string;
    wardName: string;
}

export interface WardOfficePair {
    officePairId: string; // UUID
    warehouse: OfficeInfo;
    postOffice: OfficeInfo;
    provinceCode: string;
    provinceName: string;
    regionName: string;
    assignedWards: WardInfo[];
    createdAt: string; // ISO date-time
}

export interface CreateWardOfficeRequest {
    warehouseName: string;
    warehouseEmail: string;
    warehousePhoneNumber: string; // 10-11 digits
    warehouseAddress: string;
    postOfficeName: string;
    postOfficeEmail: string;
    postOfficePhoneNumber: string; // 10-11 digits
    postOfficeAddress: string;
    provinceCode?: string; // Required for SYSTEM_ADMIN, optional for province admins
}

export interface AssignWardsRequest {
    officePairId: string; // UUID
    wardCodes: string[]; // min 1 item
}

export interface WardAssignmentInfo {
    wardCode: string;
    wardName: string;
    isAssigned: boolean;
    assignedWarehouseId?: string; // UUID
    assignedPostOfficeId?: string; // UUID
}

// Dashboard Stats types
export interface DashboardStats {
    totalOrders: number;
    totalCustomers: number;
    totalOffices: number;
    totalRevenue: number;
    ordersByStatus: Record<string, number>;
    recentGrowth: Record<string, number>;
}
