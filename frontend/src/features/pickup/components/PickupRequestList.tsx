import type { PickupRequestResponse } from '@/types';
import { PickupRequestCard } from './PickupRequestCard';

interface PickupRequestListProps {
    requests: PickupRequestResponse[];
    isLoading?: boolean;
    renderActions?: (request: PickupRequestResponse) => React.ReactNode;
}

export function PickupRequestList({ requests, isLoading, renderActions }: PickupRequestListProps) {
    if (isLoading) {
        return (
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {[...Array(6)].map((_, i) => (
                    <div key={i} className="h-64 rounded-lg bg-muted animate-pulse" />
                ))}
            </div>
        );
    }

    if (!requests || requests.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center py-12 text-center">
                <div className="rounded-full bg-muted p-6 mb-4">
                    <svg
                        className="h-12 w-12 text-muted-foreground"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
                        />
                    </svg>
                </div>
                <h3 className="text-lg font-semibold mb-1">No pickup requests</h3>
                <p className="text-sm text-muted-foreground">
                    You haven't created any pickup requests yet.
                </p>
            </div>
        );
    }

    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {requests.map((request) => (
                <PickupRequestCard
                    key={request.id}
                    request={request}
                    actions={renderActions?.(request)}
                />
            ))}
        </div>
    );
}
