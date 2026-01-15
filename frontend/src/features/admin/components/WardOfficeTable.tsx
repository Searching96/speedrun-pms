import type { WardOfficePair } from '../types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Building2, MapPin } from 'lucide-react';

interface WardOfficeTableProps {
    officePairs: WardOfficePair[];
    onAssignWards: (officePair: WardOfficePair) => void;
}

export function WardOfficeTable({ officePairs, onAssignWards }: WardOfficeTableProps) {
    if (officePairs.length === 0) {
        return (
            <div className="text-center py-12 text-muted-foreground">
                <Building2 className="h-16 w-16 mx-auto mb-4 opacity-20" />
                <p>No ward office pairs found</p>
                <p className="text-sm mt-2">Click "Create Office Pair" to add your first office pair</p>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            {officePairs.map((pair) => (
                <Card key={pair.officePairId}>
                    <CardHeader>
                        <div className="flex items-center justify-between">
                            <div>
                                <CardTitle className="text-lg">{pair.warehouse.officeName}</CardTitle>
                                <p className="text-sm text-muted-foreground mt-1">
                                    {pair.provinceName} • {pair.regionName}
                                </p>
                            </div>
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => onAssignWards(pair)}
                            >
                                <MapPin className="mr-2 h-4 w-4" />
                                Assign Wards
                            </Button>
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="grid md:grid-cols-2 gap-6">
                            {/* Warehouse */}
                            <div className="space-y-2">
                                <div className="flex items-center gap-2">
                                    <Building2 className="h-4 w-4 text-muted-foreground" />
                                    <h4 className="font-semibold text-sm">Warehouse</h4>
                                </div>
                                <div className="pl-6 space-y-1 text-sm">
                                    <p className="font-medium">{pair.warehouse.officeName}</p>
                                    <p className="text-muted-foreground">{pair.warehouse.officeAddress}</p>
                                    <p className="text-muted-foreground">{pair.warehouse.officeEmail}</p>
                                    <p className="text-muted-foreground">{pair.warehouse.officePhoneNumber}</p>
                                </div>
                            </div>

                            {/* Post Office */}
                            <div className="space-y-2">
                                <div className="flex items-center gap-2">
                                    <Building2 className="h-4 w-4 text-muted-foreground" />
                                    <h4 className="font-semibold text-sm">Post Office</h4>
                                </div>
                                <div className="pl-6 space-y-1 text-sm">
                                    <p className="font-medium">{pair.postOffice.officeName}</p>
                                    <p className="text-muted-foreground">{pair.postOffice.officeAddress}</p>
                                    <p className="text-muted-foreground">{pair.postOffice.officeEmail}</p>
                                    <p className="text-muted-foreground">{pair.postOffice.officePhoneNumber}</p>
                                </div>
                            </div>
                        </div>

                        {/* Assigned Wards */}
                        {pair.assignedWards && pair.assignedWards.length > 0 && (
                            <div className="mt-4 pt-4 border-t">
                                <h4 className="font-semibold text-sm mb-2">Assigned Wards ({pair.assignedWards.length})</h4>
                                <div className="flex flex-wrap gap-2">
                                    {pair.assignedWards.map((ward) => (
                                        <Badge key={ward.wardCode} variant="secondary">
                                            {ward.wardName}
                                        </Badge>
                                    ))}
                                </div>
                            </div>
                        )}

                        {(!pair.assignedWards || pair.assignedWards.length === 0) && (
                            <div className="mt-4 pt-4 border-t">
                                <p className="text-sm text-muted-foreground">
                                    No wards assigned yet. Click "Assign Wards" to assign wards to this office pair.
                                </p>
                            </div>
                        )}
                    </CardContent>
                </Card>
            ))}
        </div>
    );
}
