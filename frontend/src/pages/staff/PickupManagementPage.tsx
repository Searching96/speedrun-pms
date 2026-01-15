import { useState } from 'react';
import { usePendingPickups } from '@/features/pickup/hooks/usePendingPickups';
import { PickupRequestList } from '@/features/pickup/components/PickupRequestList';
import { AssignShipperDialog } from '@/features/pickup/components/AssignShipperDialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import type { PickupRequestResponse } from '@/types';
import { UserPlus } from 'lucide-react';

export function PickupManagementPage() {
    const [wardCode, setWardCode] = useState('');
    const [searchWardCode, setSearchWardCode] = useState('');
    const [assignDialogOpen, setAssignDialogOpen] = useState(false);
    const [selectedRequest, setSelectedRequest] = useState<PickupRequestResponse | null>(null);

    const { data: requests, isLoading } = usePendingPickups(searchWardCode);

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        setSearchWardCode(wardCode);
    };

    const handleAssignClick = (request: PickupRequestResponse) => {
        setSelectedRequest(request);
        setAssignDialogOpen(true);
    };

    return (
        <div className="space-y-6">
            {/* Header */}
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Pickup Management</h1>
                <p className="text-muted-foreground">
                    Manage pending pickup requests and assign shippers
                </p>
            </div>

            {/* Search Form */}
            <form onSubmit={handleSearch} className="flex gap-4 items-end">
                <div className="flex-1 space-y-2">
                    <Label htmlFor="wardCode">Ward Code</Label>
                    <Input
                        id="wardCode"
                        placeholder="Enter ward code (e.g., 00001)"
                        value={wardCode}
                        onChange={(e) => setWardCode(e.target.value)}
                    />
                </div>
                <Button type="submit">Search</Button>
            </form>

            {/* Results */}
            {searchWardCode && (
                <>
                    <div className="flex items-center justify-between">
                        <p className="text-sm text-muted-foreground">
                            {isLoading
                                ? 'Loading...'
                                : `${requests?.length || 0} pending pickup request(s) in ward ${searchWardCode}`}
                        </p>
                    </div>

                    <PickupRequestList
                        requests={requests || []}
                        isLoading={isLoading}
                        renderActions={(request) =>
                            request.status === 'PENDING' ? (
                                <Button
                                    size="sm"
                                    variant="outline"
                                    onClick={() => handleAssignClick(request)}
                                >
                                    <UserPlus className="mr-2 h-4 w-4" />
                                    Assign
                                </Button>
                            ) : null
                        }
                    />
                </>
            )}

            {!searchWardCode && (
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
                                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                            />
                        </svg>
                    </div>
                    <h3 className="text-lg font-semibold mb-1">Search for pickup requests</h3>
                    <p className="text-sm text-muted-foreground">
                        Enter a ward code to view pending pickup requests
                    </p>
                </div>
            )}

            {/* Assign Shipper Dialog */}
            {selectedRequest && (
                <AssignShipperDialog
                    open={assignDialogOpen}
                    onOpenChange={setAssignDialogOpen}
                    requestId={selectedRequest.id}
                    orderTrackingNumber={selectedRequest.orderTrackingNumber}
                />
            )}
        </div>
    );
}
