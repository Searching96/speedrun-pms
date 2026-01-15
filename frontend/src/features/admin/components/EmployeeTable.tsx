import { Edit, Trash2, Mail, Phone, Shield } from 'lucide-react';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { EmployeeFormDialog } from './EmployeeFormDialog';
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import type { Employee } from '../types';
import { useDeleteEmployee } from '../hooks/useEmployees';

interface EmployeeTableProps {
    employees: Employee[];
}

export function EmployeeTable({ employees }: EmployeeTableProps) {
    const deleteEmployee = useDeleteEmployee();

    const getRoleBadge = (role: string) => {
        switch (role) {
            case 'WH_WARD_MANAGER':
            case 'PO_WARD_MANAGER':
                return <Badge variant="default">Ward Manager</Badge>;
            case 'WH_STAFF':
            case 'PO_STAFF':
                return <Badge variant="secondary">Staff</Badge>;
            case 'SHIPPER':
                return <Badge variant="outline">Shipper</Badge>;
            default:
                return <Badge variant="outline">{role}</Badge>;
        }
    };

    if (!employees || employees.length === 0) {
        return (
            <div className="text-center py-12 text-muted-foreground border rounded-lg bg-muted/20">
                <p>No employees found in your office.</p>
            </div>
        );
    }

    return (
        <div className="rounded-md border">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Full Name</TableHead>
                        <TableHead>Contact</TableHead>
                        <TableHead>Role</TableHead>
                        <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {employees.map((employee) => (
                        <TableRow key={employee.employeeId}>
                            <TableCell className="font-medium">
                                {employee.fullName}
                            </TableCell>
                            <TableCell>
                                <div className="flex flex-col gap-1 text-sm text-muted-foreground">
                                    <div className="flex items-center gap-1">
                                        <Phone className="h-3 w-3" />
                                        {employee.phoneNumber}
                                    </div>
                                    <div className="flex items-center gap-1">
                                        <Mail className="h-3 w-3" />
                                        {employee.email}
                                    </div>
                                </div>
                            </TableCell>
                            <TableCell>
                                <div className="flex flex-col gap-1">
                                    {getRoleBadge(employee.role)}
                                    <div className="flex items-center gap-1 text-xs text-muted-foreground">
                                        <Shield className="h-3 w-3" />
                                        {employee.officeName}
                                    </div>
                                </div>
                            </TableCell>
                            <TableCell className="text-right">
                                <div className="flex justify-end gap-2">
                                    <EmployeeFormDialog
                                        employee={employee}
                                        trigger={
                                            <Button variant="ghost" size="icon">
                                                <Edit className="h-4 w-4" />
                                            </Button>
                                        }
                                    />

                                    <AlertDialog>
                                        <AlertDialogTrigger asChild>
                                            <Button variant="ghost" size="icon" className="text-destructive hover:text-destructive hover:bg-destructive/10">
                                                <Trash2 className="h-4 w-4" />
                                            </Button>
                                        </AlertDialogTrigger>
                                        <AlertDialogContent>
                                            <AlertDialogHeader>
                                                <AlertDialogTitle>Delete Employee</AlertDialogTitle>
                                                <AlertDialogDescription>
                                                    Are you sure you want to delete {employee.fullName}? This will deactivate their account and soft-delete their employee record. This action cannot be undone.
                                                </AlertDialogDescription>
                                            </AlertDialogHeader>
                                            <AlertDialogFooter>
                                                <AlertDialogCancel>Cancel</AlertDialogCancel>
                                                <AlertDialogAction
                                                    onClick={() => deleteEmployee.mutate(employee.employeeId)}
                                                    className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                                                    disabled={deleteEmployee.isPending}
                                                >
                                                    {deleteEmployee.isPending ? 'Deleting...' : 'Delete'}
                                                </AlertDialogAction>
                                            </AlertDialogFooter>
                                        </AlertDialogContent>
                                    </AlertDialog>
                                </div>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
}
