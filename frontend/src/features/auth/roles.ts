export const Role = {
    // System
    SYSTEM_ADMIN: 'SYSTEM_ADMIN',

    // Hub
    HUB_ADMIN: 'HUB_ADMIN',

    // Warehouse (WH)
    WH_PROVINCE_ADMIN: 'WH_PROVINCE_ADMIN',
    WH_WARD_MANAGER: 'WH_WARD_MANAGER',
    WH_STAFF: 'WH_STAFF',

    // Post Office (PO)
    PO_PROVINCE_ADMIN: 'PO_PROVINCE_ADMIN',
    PO_WARD_MANAGER: 'PO_WARD_MANAGER',
    PO_STAFF: 'PO_STAFF',

    // Others
    SHIPPER: 'SHIPPER',
    CUSTOMER: 'CUSTOMER',
} as const;

export type RoleType = typeof Role[keyof typeof Role];

// Grouped Roles for easier route protection
export const ROLES = {
    ADMIN_GROUP: [
        Role.SYSTEM_ADMIN,
        Role.HUB_ADMIN,
        Role.WH_PROVINCE_ADMIN,
        Role.PO_PROVINCE_ADMIN,
    ],
    MANAGER_GROUP: [
        Role.WH_WARD_MANAGER,
        Role.PO_WARD_MANAGER,
    ],
    STAFF_GROUP: [
        Role.WH_STAFF,
        Role.PO_STAFF,
    ],
    SHIPPER: [Role.SHIPPER],
    CUSTOMER: [Role.CUSTOMER],
};
