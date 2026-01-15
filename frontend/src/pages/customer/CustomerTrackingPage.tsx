import { useState } from 'react';
import { Package } from 'lucide-react';
import { TrackingForm } from '@/features/tracking/components/TrackingForm';
import { TrackingTimeline } from '@/features/tracking/components/TrackingTimeline';
import { OrderSummary } from '@/features/tracking/components/OrderSummary';
import { trackingApi } from '@/features/tracking/api';
import { orderApi } from '@/features/orders/api';
import type { TrackingResponse, OrderResponse } from '@/types';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { useQuery } from '@tanstack/react-query';

export default function CustomerTrackingPage() {
    const [selectedTracking, setSelectedTracking] = useState<TrackingResponse | null>(null);
    const [searchLoading, setSearchLoading] = useState(false);
    const [searchError, setSearchError] = useState<string | null>(null);

    // Fetch user's orders
    const { data: ordersData, isLoading } = useQuery({
        queryKey: ['orders', 'my'],
        queryFn: () => orderApi.getMyOrders(0, 50),
    });

    const orders = ordersData?.content || [];

    const handleTrackOrder = async (trackingNumber: string) => {
        setSearchLoading(true);
        setSearchError(null);

        try {
            const data = await trackingApi.getTracking(trackingNumber);
            setSelectedTracking(data);
        } catch (err: any) {
            setSearchError(err.message || 'Failed to fetch tracking information');
            setSelectedTracking(null);
        } finally {
            setSearchLoading(false);
        }
    };

    const handleOrderClick = (order: OrderResponse) => {
        handleTrackOrder(order.trackingNumber);
    };

    const getStatusColor = (status: string) => {
        if (status.includes('DELIVERED')) return 'bg-green-100 text-green-800';
        if (status.includes('CANCELLED')) return 'bg-red-100 text-red-800';
        if (status.includes('TRANSIT') || status.includes('SORTING')) return 'bg-blue-100 text-blue-800';
        return 'bg-yellow-100 text-yellow-800';
    };

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Track Your Packages</h1>
                <p className="text-muted-foreground">
                    View tracking information for all your orders
                </p>
            </div>

            {/* Search Form */}
            <Card>
                <CardHeader>
                    <CardTitle>Search by Tracking Number</CardTitle>
                </CardHeader>
                <CardContent>
                    <TrackingForm onSubmit={handleTrackOrder} loading={searchLoading} />
                </CardContent>
            </Card>

            {searchError && (
                <Alert variant="destructive">
                    <AlertDescription>{searchError}</AlertDescription>
                </Alert>
            )}

            {/* Selected Tracking Details */}
            {selectedTracking && (
                <div className="space-y-6">
                    <OrderSummary order={selectedTracking.order} />
                    <TrackingTimeline events={selectedTracking.events} />
                </div>
            )}

            {/* My Orders List */}
            {!selectedTracking && (
                <Card>
                    <CardHeader>
                        <CardTitle>My Orders</CardTitle>
                    </CardHeader>
                    <CardContent>
                        {isLoading ? (
                            <div className="text-center py-8 text-muted-foreground">
                                <Package className="h-12 w-12 mx-auto mb-4 animate-pulse" />
                                <p>Loading your orders...</p>
                            </div>
                        ) : orders.length === 0 ? (
                            <div className="text-center py-12 text-muted-foreground">
                                <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                                <p>No orders found</p>
                            </div>
                        ) : (
                            <div className="space-y-3">
                                {orders.map((order: OrderResponse) => (
                                    <div
                                        key={order.id}
                                        onClick={() => handleOrderClick(order)}
                                        className="flex items-center justify-between p-4 border rounded-lg hover:bg-accent cursor-pointer transition-colors"
                                    >
                                        <div className="flex-1">
                                            <div className="flex items-center gap-2 mb-1">
                                                <p className="font-mono font-semibold">{order.trackingNumber}</p>
                                                <Badge className={getStatusColor(order.status)}>
                                                    {order.status.replace(/_/g, ' ')}
                                                </Badge>
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
            )}
        </div>
    );
}
