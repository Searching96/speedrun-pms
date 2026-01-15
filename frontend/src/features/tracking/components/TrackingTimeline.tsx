import { format } from 'date-fns';
import { Package, MapPin, CheckCircle2, Clock } from 'lucide-react';
import type { TrackingEventResponse } from '@/types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

interface TrackingTimelineProps {
    events: TrackingEventResponse[];
}

export function TrackingTimeline({ events }: TrackingTimelineProps) {
    if (!events || events.length === 0) {
        return (
            <Card>
                <CardContent className="py-8 text-center text-muted-foreground">
                    No tracking events found
                </CardContent>
            </Card>
        );
    }

    const getStatusIcon = (status: string) => {
        if (status.includes('DELIVERED')) return <CheckCircle2 className="h-5 w-5 text-green-600" />;
        if (status.includes('TRANSIT') || status.includes('SORTING')) return <Package className="h-5 w-5 text-blue-600" />;
        return <Clock className="h-5 w-5 text-gray-600" />;
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>Tracking History</CardTitle>
            </CardHeader>
            <CardContent>
                <div className="relative space-y-4">
                    {/* Timeline line */}
                    <div className="absolute left-[13px] top-2 h-[calc(100%-2rem)] w-0.5 bg-border" />

                    {events.map((event) => (
                        <div key={event.id} className="relative flex gap-4">
                            {/* Icon */}
                            <div className="relative z-10 flex h-7 w-7 items-center justify-center rounded-full bg-background border-2 border-border">
                                {getStatusIcon(event.status)}
                            </div>

                            {/* Content */}
                            <div className="flex-1 pb-4">
                                <div className="flex items-start justify-between">
                                    <div>
                                        <p className="font-semibold">{event.status.replace(/_/g, ' ')}</p>
                                        {event.description && (
                                            <p className="text-sm text-muted-foreground">{event.description}</p>
                                        )}
                                        {event.locationName && (
                                            <div className="mt-1 flex items-center gap-1 text-sm text-muted-foreground">
                                                <MapPin className="h-3 w-3" />
                                                {event.locationName}
                                            </div>
                                        )}
                                    </div>
                                    <time className="text-sm text-muted-foreground whitespace-nowrap">
                                        {format(new Date(event.eventTime), 'MMM dd, HH:mm')}
                                    </time>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </CardContent>
        </Card>
    );
}
