import type { OrderResponse } from '@/types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

interface OrderSummaryProps {
    order: OrderResponse;
}

export function OrderSummary({ order }: OrderSummaryProps) {
    const getStatusColor = (status: string) => {
        if (status.includes('DELIVERED')) return 'bg-green-100 text-green-800';
        if (status.includes('CANCELLED')) return 'bg-red-100 text-red-800';
        if (status.includes('TRANSIT') || status.includes('SORTING')) return 'bg-blue-100 text-blue-800';
        return 'bg-yellow-100 text-yellow-800';
    };

    return (
        <Card>
            <CardHeader>
                <div className="flex items-center justify-between">
                    <CardTitle>Order Details</CardTitle>
                    <Badge className={getStatusColor(order.status)}>
                        {order.status.replace(/_/g, ' ')}
                    </Badge>
                </div>
            </CardHeader>
            <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <p className="text-sm font-medium text-muted-foreground">Tracking Number</p>
                        <p className="font-mono font-semibold">{order.trackingNumber}</p>
                    </div>
                    <div>
                        <p className="text-sm font-medium text-muted-foreground">Weight</p>
                        <p>{order.weightKg} kg</p>
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <p className="text-sm font-medium text-muted-foreground mb-1">From</p>
                        <p className="font-semibold">{order.senderName}</p>
                        <p className="text-sm">{order.senderPhone}</p>
                        <p className="text-sm text-muted-foreground">{order.senderAddress}</p>
                    </div>
                    <div>
                        <p className="text-sm font-medium text-muted-foreground mb-1">To</p>
                        <p className="font-semibold">{order.receiverName}</p>
                        <p className="text-sm">{order.receiverPhone}</p>
                        <p className="text-sm text-muted-foreground">{order.receiverAddress}</p>
                    </div>
                </div>

                {order.description && (
                    <div>
                        <p className="text-sm font-medium text-muted-foreground">Description</p>
                        <p className="text-sm">{order.description}</p>
                    </div>
                )}
            </CardContent>
        </Card>
    );
}
