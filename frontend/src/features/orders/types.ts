export const OrderStatus = {
    PENDING: 'PENDING',
    PICKUP_PENDING: 'PICKUP_PENDING',
    PICKUP_ASSIGNED: 'PICKUP_ASSIGNED',
    PICKED_UP: 'PICKED_UP',
    IN_TRANSIT: 'IN_TRANSIT',
    OUT_FOR_DELIVERY: 'OUT_FOR_DELIVERY',
    DELIVERED: 'DELIVERED',
    CANCELLED: 'CANCELLED',
    REFUSED: 'REFUSED',
    RETURNING: 'RETURNING',
    RETURNED: 'RETURNED',
} as const;

export type OrderStatus = typeof OrderStatus[keyof typeof OrderStatus];

export interface Order {
    id: string; // uuid
    trackingNumber: string;
    status: OrderStatus;
    senderName: string;
    senderPhone: string;
    senderAddress: string;
    senderWardCode: string;
    receiverName: string;
    receiverPhone: string;
    receiverAddress: string;
    receiverWardCode: string;
    weightKg: number;
    lengthCm: number;
    widthCm: number;
    heightCm: number;
    description?: string;
    shippingFee: number;
    codAmount: number;
    createdAt: string; // date-time
    updatedAt: string; // date-time
}

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

export interface PageOrderResponse {
    content: Order[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number; // current page (0-indexed)
    first: boolean;
    last: boolean;
    empty: boolean;
}

export interface OrderFilters {
    page?: number;
    size?: number;
    sortBy?: string;
    order?: 'asc' | 'desc';
}
