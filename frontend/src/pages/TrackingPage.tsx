import { useState } from 'react';
import { Package } from 'lucide-react';
import { TrackingForm } from '@/features/tracking/components/TrackingForm';
import { TrackingTimeline } from '@/features/tracking/components/TrackingTimeline';
import { OrderSummary } from '@/features/tracking/components/OrderSummary';
import { trackingApi } from '@/features/tracking/api';
import type { TrackingResponse } from '@/types';
import { Alert, AlertDescription } from '@/components/ui/alert';

export default function TrackingPage() {
    const [tracking, setTracking] = useState<TrackingResponse | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleTrack = async (trackingNumber: string) => {
        setLoading(true);
        setError(null);
        setTracking(null);

        try {
            const data = await trackingApi.getTracking(trackingNumber);
            setTracking(data);
        } catch (err: any) {
            setError(err.message || 'Failed to fetch tracking information');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mx-auto max-w-4xl py-8 px-4">
            <div className="mb-8 text-center">
                <div className="flex items-center justify-center gap-2 mb-2">
                    <Package className="h-8 w-8" />
                    <h1 className="text-3xl font-bold">Track Your Package</h1>
                </div>
                <p className="text-muted-foreground">
                    Enter your tracking number to see the current status and location of your shipment
                </p>
            </div>

            <div className="mb-8">
                <TrackingForm onSubmit={handleTrack} loading={loading} />
            </div>

            {error && (
                <Alert variant="destructive" className="mb-6">
                    <AlertDescription>{error}</AlertDescription>
                </Alert>
            )}

            {tracking && (
                <div className="space-y-6">
                    <OrderSummary order={tracking.order} />
                    <TrackingTimeline events={tracking.events} />
                </div>
            )}

            {!tracking && !error && !loading && (
                <div className="text-center py-12 text-muted-foreground">
                    <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                    <p>Enter a tracking number above to get started</p>
                </div>
            )}
        </div>
    );
}
