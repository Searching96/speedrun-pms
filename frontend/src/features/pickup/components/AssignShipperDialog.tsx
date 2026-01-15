import { useState } from 'react';
import { useAssignShipper } from '../hooks/useAssignShipper';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

interface AssignShipperDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    requestId: string;
    orderTrackingNumber: string;
}

export function AssignShipperDialog({
    open,
    onOpenChange,
    requestId,
    orderTrackingNumber,
}: AssignShipperDialogProps) {
    const [shipperId, setShipperId] = useState('');
    const { mutate: assignShipper, isPending } = useAssignShipper();

    const handleAssign = () => {
        if (!shipperId.trim()) return;

        assignShipper(
            { requestId, data: { shipperId } },
            {
                onSuccess: () => {
                    onOpenChange(false);
                    setShipperId('');
                },
            }
        );
    };

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Assign Shipper</DialogTitle>
                    <DialogDescription>
                        Assign a shipper to pickup request for order {orderTrackingNumber}
                    </DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                    <div className="space-y-2">
                        <Label htmlFor="shipperId">Shipper ID</Label>
                        <Input
                            id="shipperId"
                            placeholder="Enter shipper ID"
                            value={shipperId}
                            onChange={(e) => setShipperId(e.target.value)}
                        />
                        <p className="text-sm text-muted-foreground">
                            Enter the ID of the shipper you want to assign to this pickup request.
                        </p>
                    </div>
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={() => onOpenChange(false)} disabled={isPending}>
                        Cancel
                    </Button>
                    <Button onClick={handleAssign} disabled={isPending || !shipperId.trim()}>
                        {isPending ? 'Assigning...' : 'Assign Shipper'}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
