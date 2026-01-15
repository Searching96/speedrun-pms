import {
    LayoutDashboard,
    Package,
    Truck,
    Users,
    MapPin,
    Settings,
    FileText,
    Star,
} from 'lucide-react';

export interface NavItem {
    title: string;
    href: string;
    icon: any;
    roles?: string[]; // If omitted, visible to all authenticated users
}

export const NAV_ITEMS: NavItem[] = [
    {
        title: 'Dashboard',
        href: '/',
        icon: LayoutDashboard,
    },
    {
        title: 'Orders',
        href: '/orders',
        icon: Package,
        roles: ['CUSTOMER', 'PO_STAFF', 'PO_Provinces_ADMIN'], // Example roles
    },
    {
        title: 'Pickup Requests',
        href: '/pickups',
        icon: Truck,
        roles: ['CUSTOMER', 'PO_STAFF'],
    },
    {
        title: 'Tracking',
        href: '/tracking',
        icon: MapPin,
    },
    {
        title: 'Ratings',
        href: '/ratings',
        icon: Star,
    },
    {
        title: 'Staff Management',
        href: '/staff',
        icon: Users,
        roles: ['PO_WARD_MANAGER', 'WH_WARD_MANAGER', 'PO_PROVINCE_ADMIN', 'WH_PROVINCE_ADMIN', 'SYSTEM_ADMIN'],
    },
    {
        title: 'Administrative Units',
        href: '/admin/units',
        icon: FileText,
        roles: ['SYSTEM_ADMIN', 'PO_PROVINCE_ADMIN'],
    },
    {
        title: 'Delivery Tasks',
        href: '/shipper/tasks',
        icon: Truck,
        roles: ['SHIPPER'],
    },
    {
        title: 'Settings',
        href: '/settings',
        icon: Settings,
    },
];
