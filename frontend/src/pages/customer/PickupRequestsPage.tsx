import { useState } from 'react';
import { useMyPickupRequests } from '@/features/pickup/hooks/useMyPickupRequests';
import { CreatePickupForm } from '@/features/pickup/components/CreatePickupForm';
import { PickupRequestList } from '@/features/pickup/components/PickupRequestList';
import { Button } from '@/components/ui/button';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog';
import { Plus } from 'lucide-react';

export function PickupRequestsPage() {
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [page, setPage] = useState(0);
    const pageSize = 12;

    const { data, isLoading } = useMyPickupRequests({ page, size: pageSize });

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Pickup Requests</h1>
                    <p className="text-muted-foreground">
                        Manage your package pickup requests
                    </p>
                </div>
                <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
                    <DialogTrigger asChild>
                        <Button>
                            <Plus className="mr-2 h-4 w-4" />
                            New Pickup Request
                        </Button>
                    </DialogTrigger>
                    <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                        <DialogHeader>
                            <DialogTitle>Create Pickup Request</DialogTitle>
                        </DialogHeader>
                        <CreatePickupForm
                            onSuccess={() => setIsCreateDialogOpen(false)}
                        />
                    </DialogContent>
                </Dialog>
            </div>

            {/* Pickup Requests List */}
            <PickupRequestList
                requests={data?.content || []}
                isLoading={isLoading}
            />

            {/* Pagination */}
            {data && data.totalPages > 1 && (
                <div className="flex items-center justify-center gap-2">
                    <Button
                        variant="outline"
                        onClick={() => setPage((p) => Math.max(0, p - 1))}
                        disabled={!data.hasPrevious}
                    >
                        Previous
                    </Button>
                    <span className="text-sm text-muted-foreground">
                        Page {data.pageNumber + 1} of {data.totalPages}
                    </span>
                    <Button
                        variant="outline"
                        onClick={() => setPage((p) => p + 1)}
                        disabled={!data.hasNext}
                    >
                        Next
                    </Button>
                </div>
            )}
        </div>
    );
}
