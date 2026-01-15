import {
    LayoutDashboard,
    Package,
    Truck,
    Users,
    MapPin,
} from 'lucide-react';

import { ROLES } from '@/features/auth/roles';

export interface NavItem {
    title: string;
    href: string;
    icon: any;
    roles?: string[]; // If omitted, visible to all authenticated users
}

export const NAV_ITEMS: NavItem[] = [
    // Common / Customer
    {
        title: 'Home',
        href: '/',
        icon: LayoutDashboard,
        roles: ROLES.CUSTOMER,
    },
    {
        title: 'My Orders',
        href: '/orders',
        icon: Package,
        roles: ROLES.CUSTOMER,
    },
    {
        title: 'Tracking',
        href: '/tracking',
        icon: MapPin,
        // Visible to everyone, or just customers? Assuming everyone for now or explicit roles:
        roles: [...ROLES.CUSTOMER, ...ROLES.STAFF_GROUP, ...ROLES.ADMIN_GROUP],
    },

    // Admin
    {
        title: 'Admin Dashboard',
        href: '/admin',
        icon: LayoutDashboard,
        roles: [...ROLES.ADMIN_GROUP, ...ROLES.MANAGER_GROUP],
    },
    {
        title: 'Manage Staff',
        href: '/admin/staff',
        icon: Users,
        roles: [...ROLES.ADMIN_GROUP, ...ROLES.MANAGER_GROUP],
    },

    // Staff
    {
        title: 'Staff Dashboard',
        href: '/staff',
        icon: LayoutDashboard,
        roles: ROLES.STAFF_GROUP,
    },
    {
        title: 'Manage Orders',
        href: '/staff/orders',
        icon: Package,
        roles: ROLES.STAFF_GROUP,
    },

    // Shipper
    {
        title: 'My Tasks',
        href: '/shipper',
        icon: Truck,
        roles: ROLES.SHIPPER,
    },
];
