import { useQuery } from '@tanstack/react-query';
import { adminApi } from '@/features/admin/api';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
    PieChart, Pie, Cell, Legend
} from 'recharts';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import type { DashboardStats } from '@/features/admin/types';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#6b7280'];

export default function ReportsPage() {
    const { data: statsResponse, isLoading } = useQuery({
        queryKey: ['admin', 'stats', 'reports'],
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

    const statusData = Object.entries(stats?.ordersByStatus || {}).map(([name, value]) => ({
        name,
        value,
    }));

    const summaryData = [
        { name: 'Total Orders', value: stats?.totalOrders || 0 },
        { name: 'Customers', value: stats?.totalCustomers || 0 },
        { name: 'Offices', value: stats?.totalOffices || 0 },
    ];

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Reports & Analytics</h1>
                <p className="text-muted-foreground">
                    System-wide data visualization and performance metrics
                </p>
            </div>

            <div className="grid gap-6 md:grid-cols-2">
                {/* Status Distribution */}
                <Card className="col-span-1">
                    <CardHeader>
                        <CardTitle>Order Distribution by Status</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="h-[300px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={statusData}
                                        cx="50%"
                                        cy="50%"
                                        labelLine={false}
                                        label={({ name, percent }) => `${name} ${((percent || 0) * 100).toFixed(0)}%`}
                                        outerRadius={80}
                                        fill="#8884d8"
                                        dataKey="value"
                                    >
                                        {statusData.map((_, index) => (
                                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                        ))}
                                    </Pie>
                                    <Tooltip />
                                    <Legend />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                    </CardContent>
                </Card>

                {/* System Overview Bar Chart */}
                <Card className="col-span-1">
                    <CardHeader>
                        <CardTitle>System Overview</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="h-[300px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={summaryData}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="name" />
                                    <YAxis />
                                    <Tooltip />
                                    <Bar dataKey="value" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
