import { useState, useEffect, type ReactNode } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { createEmployeeSchema, updateEmployeeSchema, type CreateEmployeeFormData, type UpdateEmployeeFormData } from '../schemas';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Plus } from 'lucide-react';
import type { Employee } from '../types';
import { useCreateStaff, useCreateWardManager, useCreateShipper, useUpdateEmployee } from '../hooks/useEmployees';

interface EmployeeFormDialogProps {
    role?: 'ward-manager' | 'staff' | 'shipper';
    title?: string;
    employee?: Employee;
    trigger?: ReactNode;
}

export function EmployeeFormDialog({ role, title, employee, trigger }: EmployeeFormDialogProps) {
    const [open, setOpen] = useState(false);
    const isEdit = !!employee;

    const createStaff = useCreateStaff();
    const createWardManager = useCreateWardManager();
    const createShipper = useCreateShipper();
    const updateEmployee = useUpdateEmployee();

    const loading = createStaff.isPending || createWardManager.isPending || createShipper.isPending || updateEmployee.isPending;

    const form = useForm<CreateEmployeeFormData | UpdateEmployeeFormData>({
        resolver: zodResolver(isEdit ? updateEmployeeSchema : createEmployeeSchema),
        defaultValues: {
            fullName: '',
            phoneNumber: '',
            password: '',
            email: '',
            officeId: '',
        },
    });

    useEffect(() => {
        if (employee && open) {
            form.reset({
                fullName: employee.fullName,
                phoneNumber: employee.phoneNumber,
                email: employee.email,
                password: '',
                officeId: '',
            });
        }
    }, [employee, open, form]);

    const onSubmit = async (data: any) => {
        try {
            if (isEdit) {
                await updateEmployee.mutateAsync({
                    employeeId: employee!.employeeId,
                    data: {
                        fullName: data.fullName,
                        phoneNumber: data.phoneNumber,
                        email: data.email,
                        password: data.password || undefined
                    }
                });
            } else {
                if (role === 'ward-manager') {
                    await createWardManager.mutateAsync(data);
                } else if (role === 'staff') {
                    await createStaff.mutateAsync(data);
                } else {
                    await createShipper.mutateAsync(data);
                }
            }
            setOpen(false);
            if (!isEdit) form.reset();
        } catch (error) {
            // Error handling is handled in hooks
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                {trigger || (
                    <Button>
                        <Plus className="mr-2 h-4 w-4" />
                        Add {title}
                    </Button>
                )}
            </DialogTrigger>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle>{isEdit ? `Edit Employee: ${employee.fullName}` : `Create New ${title}`}</DialogTitle>
                </DialogHeader>

                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="fullName"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Full Name *</FormLabel>
                                    <FormControl>
                                        <Input placeholder="John Doe" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="phoneNumber"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Phone Number * (10 digits)</FormLabel>
                                    <FormControl>
                                        <Input placeholder="0901234567" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="email"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Email *</FormLabel>
                                    <FormControl>
                                        <Input type="email" placeholder="john@example.com" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="password"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>
                                        Password {isEdit ? '(Leave blank to keep current)' : '* (min 6 characters)'}
                                    </FormLabel>
                                    <FormControl>
                                        <Input type="password" placeholder="••••••" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <div className="flex justify-end gap-2 pt-4">
                            <Button type="button" variant="outline" onClick={() => setOpen(false)}>
                                Cancel
                            </Button>
                            <Button type="submit" disabled={loading}>
                                {loading ? (isEdit ? 'Updating...' : 'Creating...') : (isEdit ? 'Update' : `Create ${title}`)}
                            </Button>
                        </div>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
