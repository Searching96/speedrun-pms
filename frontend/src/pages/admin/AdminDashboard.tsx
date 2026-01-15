import { Package, TruckIcon, CheckCircle, Users, Building2, Banknote } from 'lucide-react';
import { Link } from 'react-router-dom';
import { StatsCard } from '@/features/admin/components/StatsCard';
import { useQuery } from '@tanstack/react-query';
import { adminApi } from '@/features/admin/api';
import { orderApi } from '@/features/orders/api';
import type { DashboardStats } from '@/features/admin/types';
import type { OrderResponse } from '@/types/api';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';

export default function AdminDashboard() {
    const { data: statsResponse, isLoading } = useQuery({
        queryKey: ['admin', 'stats'],
        queryFn: () => adminApi.getSystemStats(),
    });

    const stats = statsResponse?.data?.data as DashboardStats;

    if (isLoading) {
        return (
            <div className="flex h-[400px] items-center justify-center">
                <LoadingSpinner />
            </div>
        );
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
        }).format(amount);
    };

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Admin Dashboard</h1>
                <p className="text-muted-foreground">
                    Global overview of system operations and statistics
                </p>
            </div>

            {/* Stats Grid */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                <StatsCard
                    title="Total Orders"
                    value={stats?.totalOrders || 0}
                    description="All time orders"
                    icon={Package}
                />
                <StatsCard
                    title="Total Revenue"
                    value={formatCurrency(stats?.totalRevenue || 0)}
                    description="From delivered orders"
                    icon={Banknote}
                />
                <StatsCard
                    title="Customers"
                    value={stats?.totalCustomers || 0}
                    description="Active customer accounts"
                    icon={Users}
                />
                <StatsCard
                    title="Delivered"
                    value={stats?.ordersByStatus?.['DELIVERED'] || 0}
                    description="Successfully delivered"
                    icon={CheckCircle}
                />
                <StatsCard
                    title="In Transit"
                    value={stats?.ordersByStatus?.['IN_TRANSIT'] || 0}
                    description="Currently shipping"
                    icon={TruckIcon}
                />
                <StatsCard
                    title="Offices"
                    value={stats?.totalOffices || 0}
                    description="HUBs and Ward Offices"
                    icon={Building2}
                />
            </div>

            {/* Quick Actions */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                <div className="rounded-lg border p-6">
                    <h3 className="font-semibold mb-2">Employee Management</h3>
                    <p className="text-sm text-muted-foreground mb-4">
                        Create and manage employees in your jurisdiction
                    </p>
                    <Link
                        to="/admin/employees"
                        className="text-sm font-medium text-primary hover:underline"
                    >
                        Manage Employees →
                    </Link>
                </div>

                <div className="rounded-lg border p-6">
                    <h3 className="font-semibold mb-2">Ward Offices</h3>
                    <p className="text-sm text-muted-foreground mb-4">
                        Manage ward office pairs and assignments
                    </p>
                    <Link
                        to="/admin/ward-offices"
                        className="text-sm font-medium text-primary hover:underline"
                    >
                        Manage Offices →
                    </Link>
                </div>

                <div className="rounded-lg border p-6">
                    <h3 className="font-semibold mb-2">Reports</h3>
                    <p className="text-sm text-muted-foreground mb-4">
                        View statistics and generate reports
                    </p>
                    <Link
                        to="/admin/reports"
                        className="text-sm font-medium text-primary hover:underline"
                    >
                        View Reports →
                    </Link>
                </div>
            </div>

            {/* Recent Activity */}
            <div className="rounded-xl border bg-card text-card-foreground shadow">
                <div className="flex flex-col space-y-1.5 p-6">
                    <div className="flex items-center justify-between">
                        <h3 className="font-semibold leading-none tracking-tight">Recent Orders</h3>
                        <Link to="/admin/reports" className="text-sm text-primary hover:underline">
                            View Reports
                        </Link>
                    </div>
                </div>
                <div className="p-6 pt-0">
                    <RecentOrdersList />
                </div>
            </div>
        </div>
    );
}

function RecentOrdersList() {
    // Actually just use orderApi directly
    const { data: recentOrders, isLoading } = useQuery({
        queryKey: ['orders', 'recent'],
        queryFn: () => orderApi.getAllOrders(0, 5),
    });

    const orders = recentOrders?.content || [];

    if (isLoading) return <LoadingSpinner />;

    if (orders.length === 0) {
        return <div className="text-center py-4 text-muted-foreground">No recent orders found.</div>;
    }

    return (
        <div className="space-y-4">
            {orders.map((order: OrderResponse) => (
                <div key={order.id} className="flex items-center justify-between border-b pb-4 last:border-0 last:pb-0">
                    <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{order.trackingNumber}</p>
                        <p className="text-xs text-muted-foreground">
                            {order.senderName} → {order.receiverName}
                        </p>
                    </div>
                    <div className="flex items-center gap-4">
                        <div className="text-right">
                            <p className="text-sm font-medium">{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.shippingFee)}</p>
                            <p className="text-xs text-muted-foreground">{new Date(order.createdAt).toLocaleDateString()}</p>
                        </div>
                        <div className={`px-2 py-1 rounded-full text-[10px] font-bold ${order.status === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                            order.status === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                                'bg-blue-100 text-blue-700'
                            }`}>
                            {order.status}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}
