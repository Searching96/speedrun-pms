// =============================================================================
// API Response Wrappers
// =============================================================================

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    errorCode?: string;
    timestamp: string;
}

export interface PageResponse<T> {
    content: T[];
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
}

export interface PaginationParams {
    page?: number;
    size?: number;
    sortBy?: string;
    order?: 'asc' | 'desc';
}

// =============================================================================
// Authentication
// =============================================================================

export interface LoginRequest {
    username: string;
    password: string;
}

export interface CustomerRegisterRequest {
    fullName: string;
    username: string;
    password: string;
    email: string;
    address: string;
}

export interface AuthResponse {
    token: string;
    role: string;
    id: string;
    fullName: string;
}

// =============================================================================
// User
// =============================================================================

export interface User {
    id: string;
    username: string;
    role: string;
    fullName?: string;
    email?: string;
    [key: string]: unknown;
}

// =============================================================================
// Orders
// =============================================================================

export interface CreateOrderRequest {
    senderName: string;
    senderPhone: string;
    senderAddress: string;
    senderWardCode: string;
    receiverName: string;
    receiverPhone: string;
    receiverAddress: string;
    receiverWardCode: string;
    weightKg: number;
    lengthCm?: number;
    widthCm?: number;
    heightCm?: number;
    description?: string;
    shippingFee: number;
    codAmount?: number;
}

export interface OrderResponse {
    id: string;
    trackingNumber: string;
    status: string;
    senderName: string;
    senderPhone: string;
    senderAddress: string;
    senderWardCode: string;
    receiverName: string;
    receiverPhone: string;
    receiverAddress: string;
    receiverWardCode: string;
    weightKg: number;
    description?: string;
    shippingFee: number;
    codAmount?: number;
    createdAt: string;
    updatedAt: string;
}

// =============================================================================
// Pickup Requests
// =============================================================================

export type TimeSlot = 'MORNING' | 'AFTERNOON' | 'EVENING';

export interface CreatePickupRequest {
    orderId: string;
    pickupAddress: string;
    pickupWardCode: string;
    pickupContactName: string;
    pickupContactPhone: string;
    preferredDate: string;
    preferredTimeSlot?: TimeSlot;
}

export interface AssignShipperRequest {
    shipperId: string;
}

export interface PickupRequestResponse {
    id: string;
    orderId: string;
    orderTrackingNumber: string;
    pickupAddress: string;
    pickupWardCode: string;
    pickupContactName: string;
    pickupContactPhone: string;
    preferredDate: string;
    preferredTimeSlot?: TimeSlot;
    status: string;
    assignedShipperId?: string;
    assignedShipperName?: string;
    createdAt: string;
    assignedAt?: string;
    completedAt?: string;
}

// =============================================================================
// Tracking
// =============================================================================

export interface CreateEventRequest {
    orderId: string;
    status: string;
    description?: string;
    locationName?: string;
}

export interface TrackingEventResponse {
    id: string;
    status: string;
    description?: string;
    locationName?: string;
    officeId?: string;
    eventTime: string;
}

export interface TrackingResponse {
    order: OrderResponse;
    events: TrackingEventResponse[];
}

// =============================================================================
// Ratings
// =============================================================================

export interface CreateRatingRequest {
    orderId: string;
    overallRating: number;
    deliverySpeedRating?: number;
    shipperAttitudeRating?: number;
    comment?: string;
}

export interface RatingResponse {
    id: string;
    orderId: string;
    overallRating: number;
    deliverySpeedRating?: number;
    shipperAttitudeRating?: number;
    comment?: string;
    createdAt: string;
}

// =============================================================================
// Administrative Units
// =============================================================================

export interface ProvinceResponse {
    code: string;
    name: string;
    administrativeRegionName?: string;
}

export interface WardResponse {
    code: string;
    name: string;
    provinceName?: string;
}

// =============================================================================
// Shipper Tasks
// =============================================================================

export type TaskType = 'PICKUP' | 'DELIVERY';

export interface DeliveryTaskResponse {
    id: string;
    orderId: string;
    orderTrackingNumber: string;
    shipperId?: string;
    shipperName?: string;
    taskType: TaskType;
    status: string;
    address: string;
    wardCode: string;
    contactName: string;
    contactPhone: string;
    assignedAt?: string;
    startedAt?: string;
    completedAt?: string;
    notes?: string;
    photoProofUrl?: string;
}

export interface FailTaskRequest {
    reason: string;
}

export interface CompleteTaskRequest {
    notes?: string;
    photoProofUrl?: string;
}

// =============================================================================
// Employee Management
// =============================================================================

export interface EmployeeResponse {
    employeeId: string;
    fullName: string;
    phoneNumber: string;
    email: string;
    role: string;
    officeName?: string;
}

export interface CreateEmployeeBaseRequest {
    fullName: string;
    phoneNumber: string;
    password: string;
    email: string;
}

export interface CreateWardManagerRequest extends CreateEmployeeBaseRequest {
    officeId: string;
}

export interface CreateStaffRequest extends CreateEmployeeBaseRequest {
    officeId: string;
}

export interface CreateShipperRequest extends CreateEmployeeBaseRequest { }

// =============================================================================
// Ward Office Management
// =============================================================================

export interface OfficeInfo {
    officeId: string;
    officeName: string;
    officeEmail?: string;
    officePhoneNumber?: string;
    officeAddress?: string;
    officeType: string;
    parentOfficeId?: string;
    parentOfficeName?: string;
}

export interface WardInfo {
    wardCode: string;
    wardName: string;
}

export interface WardOfficePairResponse {
    officePairId: string;
    warehouse: OfficeInfo;
    postOffice: OfficeInfo;
    provinceCode: string;
    provinceName: string;
    regionName?: string;
    assignedWards: WardInfo[];
    createdAt: string;
}

export interface WardAssignmentInfo {
    wardCode: string;
    wardName: string;
    isAssigned: boolean;
    assignedWarehouseId?: string;
    assignedPostOfficeId?: string;
}
