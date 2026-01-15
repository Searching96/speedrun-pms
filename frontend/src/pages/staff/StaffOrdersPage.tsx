import { Package } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useQuery } from '@tanstack/react-query';
import { orderApi } from '@/features/orders/api';
import { Badge } from '@/components/ui/badge';

export default function StaffOrdersPage() {
    const { data: ordersData, isLoading } = useQuery({
        queryKey: ['staff', 'orders'],
        queryFn: () => orderApi.getMyOrders(0, 50),
    });

    const orders = ordersData?.content || [];

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Orders Management</h1>
                <p className="text-muted-foreground">
                    View and manage all orders in your office
                </p>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>All Orders</CardTitle>
                </CardHeader>
                <CardContent>
                    {isLoading ? (
                        <div className="text-center py-12 text-muted-foreground">
                            <Package className="h-12 w-12 mx-auto mb-4 animate-pulse" />
                            <p>Loading orders...</p>
                        </div>
                    ) : orders.length === 0 ? (
                        <div className="text-center py-12 text-muted-foreground">
                            <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                            <p>No orders found</p>
                        </div>
                    ) : (
                        <div className="space-y-3">
                            {orders.map((order: any) => (
                                <div
                                    key={order.id}
                                    className="flex items-center justify-between p-4 border rounded-lg"
                                >
                                    <div className="flex-1">
                                        <div className="flex items-center gap-2 mb-1">
                                            <p className="font-mono font-semibold">{order.trackingNumber}</p>
                                            <Badge>{order.status.replace(/_/g, ' ')}</Badge>
                                        </div>
                                        <p className="text-sm text-muted-foreground">
                                            From: {order.senderName} → To: {order.receiverName}
                                        </p>
                                    </div>
                                    <div className="text-right">
                                        <p className="text-sm font-medium">{order.weightKg} kg</p>
                                        <p className="text-xs text-muted-foreground">
                                            {new Date(order.createdAt).toLocaleDateString()}
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </CardContent>
            </Card>
        </div>
    );
}
