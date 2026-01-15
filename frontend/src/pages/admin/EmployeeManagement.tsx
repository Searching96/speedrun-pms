import { useEmployees } from '@/features/admin/hooks/useEmployees';
import { EmployeeFormDialog } from '@/features/admin/components/EmployeeFormDialog';
import { EmployeeTable } from '@/features/admin/components/EmployeeTable';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';

export default function EmployeeManagement() {
    const { data: employees, isLoading, error } = useEmployees();

    return (
        <div className="space-y-6">
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Employee Management</h1>
                    <p className="text-muted-foreground">
                        Create, update and manage employees in your jurisdiction
                    </p>
                </div>
                <div className="flex flex-wrap gap-2">
                    <EmployeeFormDialog role="ward-manager" title="Ward Manager" />
                    <EmployeeFormDialog role="staff" title="Staff" />
                    <EmployeeFormDialog role="shipper" title="Shipper" />
                </div>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Employees</CardTitle>
                </CardHeader>
                <CardContent>
                    {isLoading ? (
                        <div className="flex justify-center py-12">
                            <LoadingSpinner />
                        </div>
                    ) : error ? (
                        <div className="text-center py-12 text-destructive">
                            <p>Failed to load employees. Please try again later.</p>
                        </div>
                    ) : (
                        <EmployeeTable employees={employees || []} />
                    )}
                </CardContent>
            </Card>
        </div>
    );
}
