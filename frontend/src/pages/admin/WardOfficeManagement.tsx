import { useState } from 'react';
import { Building2 } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useWardOfficePairs } from '@/features/admin/hooks/useWardOffices';
import { WardOfficeTable } from '@/features/admin/components/WardOfficeTable';
import { WardOfficeFormDialog } from '@/features/admin/components/WardOfficeFormDialog';
import { WardAssignmentDialog } from '@/features/admin/components/WardAssignmentDialog';
import type { WardOfficePair } from '@/features/admin/types';

export default function WardOfficeManagement() {
    const [selectedOfficePair, setSelectedOfficePair] = useState<WardOfficePair | null>(null);
    const [assignDialogOpen, setAssignDialogOpen] = useState(false);

    const { data: officePairs, isLoading, error } = useWardOfficePairs();

    const handleAssignWards = (officePair: WardOfficePair) => {
        setSelectedOfficePair(officePair);
        setAssignDialogOpen(true);
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Ward Office Management</h1>
                    <p className="text-muted-foreground">
                        Manage ward office pairs (warehouse + post office)
                    </p>
                </div>
                <WardOfficeFormDialog />
            </div>

            {error && (
                <Alert variant="destructive">
                    <AlertDescription>
                        {error instanceof Error ? error.message : 'Failed to load ward office pairs'}
                    </AlertDescription>
                </Alert>
            )}

            <Card>
                <CardHeader>
                    <CardTitle>Ward Office Pairs</CardTitle>
                </CardHeader>
                <CardContent>
                    {isLoading ? (
                        <div className="text-center py-12 text-muted-foreground">
                            <Building2 className="h-12 w-12 mx-auto mb-4 animate-pulse" />
                            <p>Loading ward office pairs...</p>
                        </div>
                    ) : (
                        <WardOfficeTable
                            officePairs={officePairs || []}
                            onAssignWards={handleAssignWards}
                        />
                    )}
                </CardContent>
            </Card>

            <WardAssignmentDialog
                officePair={selectedOfficePair}
                open={assignDialogOpen}
                onOpenChange={setAssignDialogOpen}
            />
        </div>
    );
}
