import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { trackingApi } from '@/features/tracking/api';
import { TrackingTimeline } from '@/features/tracking/components/TrackingTimeline';
import { OrderSummary } from '@/features/tracking/components/OrderSummary';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Package } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router-dom';

export function OrderDetailsPage() {
    const { trackingNumber } = useParams<{ trackingNumber: string }>();
    const navigate = useNavigate();

    const { data: tracking, isLoading, error } = useQuery({
        queryKey: ['tracking', trackingNumber],
        queryFn: () => trackingApi.getTracking(trackingNumber!),
        enabled: !!trackingNumber,
    });

    if (isLoading) return <div className="flex justify-center py-12"><LoadingSpinner /></div>;

    return (
        <div className="container mx-auto max-w-4xl py-6">
            <div className="mb-6 flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <Package className="h-6 w-6" />
                    <h1 className="text-2xl font-bold">Order Details: {trackingNumber}</h1>
                </div>
                <Button variant="outline" onClick={() => navigate(-1)}>Back</Button>
            </div>

            {error ? (
                <Alert variant="destructive">
                    <AlertDescription>Failed to fetch order details. Please verify the tracking number.</AlertDescription>
                </Alert>
            ) : tracking ? (
                <div className="space-y-6">
                    <OrderSummary order={tracking.order} />
                    <div className="bg-card rounded-xl border p-6 shadow-sm">
                        <h3 className="text-lg font-semibold mb-6">Tracking History</h3>
                        <TrackingTimeline events={tracking.events} />
                    </div>
                </div>
            ) : null}
        </div>
    );
}

export default OrderDetailsPage;
