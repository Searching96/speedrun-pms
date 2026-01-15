import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import type { PickupRequestResponse } from '@/types';
import { format } from 'date-fns';
import { timeSlotLabels } from '../schemas/pickup.schema';
import { Calendar, MapPin, Phone, User, Package } from 'lucide-react';

interface PickupRequestCardProps {
    request: PickupRequestResponse;
    actions?: React.ReactNode;
}

export function PickupRequestCard({ request, actions }: PickupRequestCardProps) {
    const getStatusColor = (status: string) => {
        switch (status.toUpperCase()) {
            case 'PENDING':
                return 'bg-yellow-500';
            case 'ASSIGNED':
                return 'bg-blue-500';
            case 'COMPLETED':
                return 'bg-green-500';
            case 'CANCELLED':
                return 'bg-red-500';
            default:
                return 'bg-gray-500';
        }
    };

    return (
        <Card className="hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                    <div className="space-y-1">
                        <CardTitle className="text-lg flex items-center gap-2">
                            <Package className="h-5 w-5 text-muted-foreground" />
                            {request.orderTrackingNumber}
                        </CardTitle>
                        <Badge className={getStatusColor(request.status)}>
                            {request.status}
                        </Badge>
                    </div>
                    {actions}
                </div>
            </CardHeader>
            <CardContent className="space-y-3">
                <div className="flex items-start gap-2 text-sm">
                    <MapPin className="h-4 w-4 text-muted-foreground mt-0.5 flex-shrink-0" />
                    <span className="text-muted-foreground">{request.pickupAddress}</span>
                </div>

                <div className="flex items-center gap-2 text-sm">
                    <User className="h-4 w-4 text-muted-foreground flex-shrink-0" />
                    <span className="text-muted-foreground">{request.pickupContactName}</span>
                </div>

                <div className="flex items-center gap-2 text-sm">
                    <Phone className="h-4 w-4 text-muted-foreground flex-shrink-0" />
                    <span className="text-muted-foreground">{request.pickupContactPhone}</span>
                </div>

                <div className="flex items-center gap-2 text-sm">
                    <Calendar className="h-4 w-4 text-muted-foreground flex-shrink-0" />
                    <span className="text-muted-foreground">
                        {format(new Date(request.preferredDate), 'PPP')}
                        {request.preferredTimeSlot && ` • ${timeSlotLabels[request.preferredTimeSlot]}`}
                    </span>
                </div>

                {request.assignedShipperName && (
                    <div className="pt-2 border-t">
                        <p className="text-sm text-muted-foreground">
                            Assigned to: <span className="font-medium text-foreground">{request.assignedShipperName}</span>
                        </p>
                    </div>
                )}
            </CardContent>
        </Card>
    );
}
