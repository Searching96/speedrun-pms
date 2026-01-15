import { useState, useMemo } from 'react';
import { useAssignWards, useWardAssignmentStatus } from '../hooks/useWardOffices';
import type { WardOfficePair, WardAssignmentInfo } from '../types';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Checkbox } from '@/components/ui/checkbox';
import { Badge } from '@/components/ui/badge';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { MapPin, Search } from 'lucide-react';

interface WardAssignmentDialogProps {
    officePair: WardOfficePair | null;
    open: boolean;
    onOpenChange: (open: boolean) => void;
}

export function WardAssignmentDialog({ officePair, open, onOpenChange }: WardAssignmentDialogProps) {
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedWards, setSelectedWards] = useState<string[]>([]);

    const { data: wardAssignments, isLoading } = useWardAssignmentStatus(officePair?.provinceCode);
    const assignMutation = useAssignWards();

    // Filter unassigned wards
    const availableWards = useMemo(() => {
        if (!wardAssignments) return [];
        return wardAssignments.filter((ward: WardAssignmentInfo) => !ward.isAssigned);
    }, [wardAssignments]);

    // Filter by search term
    const filteredWards = useMemo(() => {
        if (!searchTerm) return availableWards;
        const term = searchTerm.toLowerCase();
        return availableWards.filter((ward: WardAssignmentInfo) =>
            ward.wardName.toLowerCase().includes(term) ||
            ward.wardCode.includes(term)
        );
    }, [availableWards, searchTerm]);

    const handleToggleWard = (wardCode: string) => {
        setSelectedWards(prev =>
            prev.includes(wardCode)
                ? prev.filter(code => code !== wardCode)
                : [...prev, wardCode]
        );
    };

    const handleSelectAll = () => {
        if (selectedWards.length === filteredWards.length) {
            setSelectedWards([]);
        } else {
            setSelectedWards(filteredWards.map((w: WardAssignmentInfo) => w.wardCode));
        }
    };

    const handleSubmit = async () => {
        if (!officePair || selectedWards.length === 0) return;

        try {
            await assignMutation.mutateAsync({
                officePairId: officePair.officePairId,
                wardCodes: selectedWards,
            });
            setSelectedWards([]);
            setSearchTerm('');
            onOpenChange(false);
        } catch (error) {
            // Error handled by mutation
        }
    };

    const handleClose = () => {
        setSelectedWards([]);
        setSearchTerm('');
        onOpenChange(false);
    };

    if (!officePair) return null;

    return (
        <Dialog open={open} onOpenChange={handleClose}>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-hidden flex flex-col">
                <DialogHeader>
                    <DialogTitle>Assign Wards to Office Pair</DialogTitle>
                    <p className="text-sm text-muted-foreground">
                        {officePair.warehouse.officeName} & {officePair.postOffice.officeName}
                    </p>
                </DialogHeader>

                <div className="space-y-4 flex-1 overflow-hidden flex flex-col">
                    {/* Currently Assigned Wards */}
                    {officePair.assignedWards && officePair.assignedWards.length > 0 && (
                        <Alert>
                            <MapPin className="h-4 w-4" />
                            <AlertDescription>
                                <p className="font-semibold mb-2">Currently Assigned ({officePair.assignedWards.length}):</p>
                                <div className="flex flex-wrap gap-1">
                                    {officePair.assignedWards.map((ward) => (
                                        <Badge key={ward.wardCode} variant="secondary" className="text-xs">
                                            {ward.wardName}
                                        </Badge>
                                    ))}
                                </div>
                            </AlertDescription>
                        </Alert>
                    )}

                    {/* Search */}
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder="Search wards..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="pl-9"
                        />
                    </div>

                    {/* Select All */}
                    {filteredWards.length > 0 && (
                        <div className="flex items-center gap-2 pb-2 border-b">
                            <Checkbox
                                checked={selectedWards.length === filteredWards.length && filteredWards.length > 0}
                                onCheckedChange={handleSelectAll}
                                id="select-all"
                            />
                            <label htmlFor="select-all" className="text-sm font-medium cursor-pointer">
                                Select All ({filteredWards.length} available)
                            </label>
                        </div>
                    )}

                    {/* Ward List */}
                    <div className="flex-1 overflow-y-auto space-y-2">
                        {isLoading ? (
                            <div className="text-center py-8 text-muted-foreground">
                                <MapPin className="h-12 w-12 mx-auto mb-4 animate-pulse" />
                                <p>Loading wards...</p>
                            </div>
                        ) : filteredWards.length === 0 ? (
                            <div className="text-center py-8 text-muted-foreground">
                                <MapPin className="h-12 w-12 mx-auto mb-4 opacity-20" />
                                <p>
                                    {searchTerm
                                        ? 'No wards match your search'
                                        : 'No unassigned wards available'}
                                </p>
                            </div>
                        ) : (
                            filteredWards.map((ward: WardAssignmentInfo) => (
                                <div
                                    key={ward.wardCode}
                                    className="flex items-center gap-3 p-3 border rounded-lg hover:bg-accent cursor-pointer"
                                    onClick={() => handleToggleWard(ward.wardCode)}
                                >
                                    <Checkbox
                                        checked={selectedWards.includes(ward.wardCode)}
                                        onCheckedChange={() => handleToggleWard(ward.wardCode)}
                                    />
                                    <div className="flex-1">
                                        <p className="font-medium">{ward.wardName}</p>
                                        <p className="text-sm text-muted-foreground">Code: {ward.wardCode}</p>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>

                    {/* Selected Count */}
                    {selectedWards.length > 0 && (
                        <div className="text-sm text-muted-foreground">
                            {selectedWards.length} ward{selectedWards.length !== 1 ? 's' : ''} selected
                        </div>
                    )}
                </div>

                <div className="flex justify-end gap-2 pt-4 border-t">
                    <Button variant="outline" onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button
                        onClick={handleSubmit}
                        disabled={selectedWards.length === 0 || assignMutation.isPending}
                    >
                        {assignMutation.isPending ? 'Assigning...' : `Assign ${selectedWards.length} Ward${selectedWards.length !== 1 ? 's' : ''}`}
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    );
}
