import { format } from 'date-fns';
import { Package, MapPin, Clock, CheckCircle2, XCircle, PlayCircle } from 'lucide-react';
import type { DeliveryTaskResponse } from '@/types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { useStartTask, useCompleteTask, useFailTask } from '../hooks/useTaskMutations';
import { useState } from 'react';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogFooter,
} from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

interface TaskCardProps {
    task: DeliveryTaskResponse;
}

export function TaskCard({ task }: TaskCardProps) {
    const [completeDialogOpen, setCompleteDialogOpen] = useState(false);
    const [failDialogOpen, setFailDialogOpen] = useState(false);
    const [notes, setNotes] = useState('');
    const [photoProofUrl, setPhotoProofUrl] = useState('');
    const [failureReason, setFailureReason] = useState('');

    const startTask = useStartTask();
    const completeTask = useCompleteTask();
    const failTask = useFailTask();

    const getStatusBadge = (status: string) => {
        const statusMap: Record<string, { variant: 'default' | 'secondary' | 'destructive' | 'outline'; label: string }> = {
            ASSIGNED: { variant: 'secondary', label: 'Assigned' },
            IN_PROGRESS: { variant: 'default', label: 'In Progress' },
            COMPLETED: { variant: 'outline', label: 'Completed' },
            FAILED: { variant: 'destructive', label: 'Failed' },
        };
        const config = statusMap[status] || { variant: 'outline' as const, label: status };
        return <Badge variant={config.variant}>{config.label}</Badge>;
    };

    const getTaskTypeIcon = (type: string) => {
        return type === 'PICKUP' ? (
            <Package className="h-5 w-5 text-blue-600" />
        ) : (
            <Package className="h-5 w-5 text-green-600" />
        );
    };

    const handleStart = () => {
        startTask.mutate(task.id);
    };

    const handleComplete = () => {
        completeTask.mutate(
            { taskId: task.id, data: { notes, photoProofUrl } },
            {
                onSuccess: () => {
                    setCompleteDialogOpen(false);
                    setNotes('');
                    setPhotoProofUrl('');
                },
            }
        );
    };

    const handleFail = () => {
        failTask.mutate(
            { taskId: task.id, data: { reason: failureReason } },
            {
                onSuccess: () => {
                    setFailDialogOpen(false);
                    setFailureReason('');
                },
            }
        );
    };

    const canStart = task.status === 'ASSIGNED';
    const canComplete = task.status === 'IN_PROGRESS';
    const canFail = task.status === 'IN_PROGRESS';

    return (
        <>
            <Card>
                <CardHeader>
                    <div className="flex items-start justify-between">
                        <div className="flex items-center gap-2">
                            {getTaskTypeIcon(task.taskType)}
                            <CardTitle className="text-lg">
                                {task.taskType === 'PICKUP' ? 'Pickup' : 'Delivery'} Task
                            </CardTitle>
                        </div>
                        {getStatusBadge(task.status)}
                    </div>
                </CardHeader>
                <CardContent className="space-y-4">
                    <div>
                        <p className="text-sm font-medium text-muted-foreground">Tracking Number</p>
                        <p className="font-mono font-semibold">{task.orderTrackingNumber}</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <p className="text-sm font-medium text-muted-foreground mb-1">Contact</p>
                            <p className="font-semibold">{task.contactName}</p>
                            <p className="text-sm">{task.contactPhone}</p>
                        </div>
                        <div>
                            <p className="text-sm font-medium text-muted-foreground mb-1">Address</p>
                            <div className="flex items-start gap-1">
                                <MapPin className="h-4 w-4 mt-0.5 text-muted-foreground flex-shrink-0" />
                                <p className="text-sm">{task.address}</p>
                            </div>
                        </div>
                    </div>

                    {task.assignedAt && (
                        <div className="flex items-center gap-1 text-sm text-muted-foreground">
                            <Clock className="h-4 w-4" />
                            Assigned: {format(new Date(task.assignedAt), 'MMM dd, HH:mm')}
                        </div>
                    )}

                    {task.notes && (
                        <div>
                            <p className="text-sm font-medium text-muted-foreground">Notes</p>
                            <p className="text-sm">{task.notes}</p>
                        </div>
                    )}

                    <div className="flex gap-2 pt-2">
                        {canStart && (
                            <Button onClick={handleStart} disabled={startTask.isPending} className="flex-1">
                                <PlayCircle className="mr-2 h-4 w-4" />
                                Start Task
                            </Button>
                        )}
                        {canComplete && (
                            <Button onClick={() => setCompleteDialogOpen(true)} className="flex-1">
                                <CheckCircle2 className="mr-2 h-4 w-4" />
                                Complete
                            </Button>
                        )}
                        {canFail && (
                            <Button
                                onClick={() => setFailDialogOpen(true)}
                                variant="destructive"
                                className="flex-1"
                            >
                                <XCircle className="mr-2 h-4 w-4" />
                                Mark Failed
                            </Button>
                        )}
                    </div>
                </CardContent>
            </Card>

            {/* Complete Dialog */}
            <Dialog open={completeDialogOpen} onOpenChange={setCompleteDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Complete Task</DialogTitle>
                        <DialogDescription>
                            Confirm task completion for {task.orderTrackingNumber}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div>
                            <Label htmlFor="notes">Notes (Optional)</Label>
                            <Textarea
                                id="notes"
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                placeholder="Any additional notes..."
                                rows={3}
                            />
                        </div>
                        <div>
                            <Label htmlFor="photoProof">Photo Proof URL (Optional)</Label>
                            <Input
                                id="photoProof"
                                value={photoProofUrl}
                                onChange={(e) => setPhotoProofUrl(e.target.value)}
                                placeholder="https://..."
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setCompleteDialogOpen(false)}>
                            Cancel
                        </Button>
                        <Button onClick={handleComplete} disabled={completeTask.isPending}>
                            Confirm Complete
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Fail Dialog */}
            <Dialog open={failDialogOpen} onOpenChange={setFailDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Mark Task as Failed</DialogTitle>
                        <DialogDescription>
                            Please provide a reason for failure
                        </DialogDescription>
                    </DialogHeader>
                    <div>
                        <Label htmlFor="reason">Failure Reason *</Label>
                        <Textarea
                            id="reason"
                            value={failureReason}
                            onChange={(e) => setFailureReason(e.target.value)}
                            placeholder="e.g., Customer not available, Wrong address..."
                            rows={3}
                            required
                        />
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setFailDialogOpen(false)}>
                            Cancel
                        </Button>
                        <Button
                            variant="destructive"
                            onClick={handleFail}
                            disabled={!failureReason || failTask.isPending}
                        >
                            Confirm Failed
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </>
    );
}
