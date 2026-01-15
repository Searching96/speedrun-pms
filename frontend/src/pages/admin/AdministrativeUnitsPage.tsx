import { useState } from 'react';
import { MapPin } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useQuery } from '@tanstack/react-query';
import { adminUnitsApi } from '@/features/admin/adminUnitsApi';

export default function AdministrativeUnitsPage() {
    const [selectedProvince, setSelectedProvince] = useState<string | null>(null);

    const { data: provinces, isLoading: provincesLoading } = useQuery({
        queryKey: ['provinces'],
        queryFn: () => adminUnitsApi.getAllProvinces(),
    });

    const { data: wards, isLoading: wardsLoading } = useQuery({
        queryKey: ['wards', selectedProvince],
        queryFn: () => adminUnitsApi.getWardsByProvince(selectedProvince!),
        enabled: !!selectedProvince,
    });

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Administrative Units</h1>
                <p className="text-muted-foreground">
                    Browse provinces and wards in the system
                </p>
            </div>

            <Tabs defaultValue="provinces">
                <TabsList>
                    <TabsTrigger value="provinces">Provinces</TabsTrigger>
                    <TabsTrigger value="wards">Wards</TabsTrigger>
                </TabsList>

                <TabsContent value="provinces" className="mt-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>All Provinces</CardTitle>
                        </CardHeader>
                        <CardContent>
                            {provincesLoading ? (
                                <div className="text-center py-12 text-muted-foreground">
                                    <MapPin className="h-12 w-12 mx-auto mb-4 animate-pulse" />
                                    <p>Loading provinces...</p>
                                </div>
                            ) : (
                                <div className="grid gap-2 md:grid-cols-2 lg:grid-cols-3">
                                    {provinces?.data?.map((province: any) => (
                                        <div
                                            key={province.code}
                                            onClick={() => setSelectedProvince(province.code)}
                                            className="p-4 border rounded-lg hover:bg-accent cursor-pointer transition-colors"
                                        >
                                            <p className="font-semibold">{province.name}</p>
                                            <p className="text-sm text-muted-foreground">
                                                {province.administrativeRegionName}
                                            </p>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="wards" className="mt-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>
                                {selectedProvince ? `Wards in Selected Province` : 'Select a Province'}
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            {!selectedProvince ? (
                                <div className="text-center py-12 text-muted-foreground">
                                    <MapPin className="h-16 w-16 mx-auto mb-4 opacity-20" />
                                    <p>Select a province from the Provinces tab to view its wards</p>
                                </div>
                            ) : wardsLoading ? (
                                <div className="text-center py-12 text-muted-foreground">
                                    <MapPin className="h-12 w-12 mx-auto mb-4 animate-pulse" />
                                    <p>Loading wards...</p>
                                </div>
                            ) : (
                                <div className="grid gap-2 md:grid-cols-2 lg:grid-cols-4">
                                    {wards?.data?.map((ward: any) => (
                                        <div
                                            key={ward.code}
                                            className="p-3 border rounded-lg"
                                        >
                                            <p className="font-medium text-sm">{ward.name}</p>
                                            <p className="text-xs text-muted-foreground">
                                                Code: {ward.code}
                                            </p>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>
        </div>
    );
}
