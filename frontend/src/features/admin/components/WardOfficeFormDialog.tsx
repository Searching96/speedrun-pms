import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { createWardOfficeSchema, type CreateWardOfficeFormData } from '../schemas';
import { useCreateWardOfficePair } from '../hooks/useWardOffices';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Plus } from 'lucide-react';

export function WardOfficeFormDialog() {
    const [open, setOpen] = useState(false);
    const createMutation = useCreateWardOfficePair();

    const form = useForm<CreateWardOfficeFormData>({
        resolver: zodResolver(createWardOfficeSchema),
        defaultValues: {
            warehouseName: '',
            warehouseEmail: '',
            warehousePhoneNumber: '',
            warehouseAddress: '',
            postOfficeName: '',
            postOfficeEmail: '',
            postOfficePhoneNumber: '',
            postOfficeAddress: '',
            provinceCode: '',
        },
    });

    const onSubmit = async (data: CreateWardOfficeFormData) => {
        try {
            await createMutation.mutateAsync(data);
            form.reset();
            setOpen(false);
        } catch (error) {
            // Error handled by mutation
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>
                    <Plus className="mr-2 h-4 w-4" />
                    Create Office Pair
                </Button>
            </DialogTrigger>
            <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Create Ward Office Pair</DialogTitle>
                </DialogHeader>

                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                        {/* Warehouse Section */}
                        <div className="space-y-4">
                            <h3 className="font-semibold text-lg">Warehouse Information</h3>

                            <FormField
                                control={form.control}
                                name="warehouseName"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Warehouse Name *</FormLabel>
                                        <FormControl>
                                            <Input placeholder="e.g., Kho Phường Bến Nghé" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />

                            <div className="grid md:grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="warehouseEmail"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Email *</FormLabel>
                                            <FormControl>
                                                <Input type="email" placeholder="warehouse@example.com" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="warehousePhoneNumber"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Phone Number *</FormLabel>
                                            <FormControl>
                                                <Input placeholder="0281234567" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>

                            <FormField
                                control={form.control}
                                name="warehouseAddress"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Address *</FormLabel>
                                        <FormControl>
                                            <Input placeholder="123 Main Street" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>

                        {/* Post Office Section */}
                        <div className="space-y-4 pt-4 border-t">
                            <h3 className="font-semibold text-lg">Post Office Information</h3>

                            <FormField
                                control={form.control}
                                name="postOfficeName"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Post Office Name *</FormLabel>
                                        <FormControl>
                                            <Input placeholder="e.g., Bưu cục Phường Bến Nghé" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />

                            <div className="grid md:grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="postOfficeEmail"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Email *</FormLabel>
                                            <FormControl>
                                                <Input type="email" placeholder="postoffice@example.com" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="postOfficePhoneNumber"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Phone Number *</FormLabel>
                                            <FormControl>
                                                <Input placeholder="0281234568" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>

                            <FormField
                                control={form.control}
                                name="postOfficeAddress"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Address *</FormLabel>
                                        <FormControl>
                                            <Input placeholder="125 Main Street" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>

                        {/* Province Code (Optional for Province Admins) */}
                        <FormField
                            control={form.control}
                            name="provinceCode"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Province Code (Optional)</FormLabel>
                                    <FormControl>
                                        <Input placeholder="79" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <div className="flex justify-end gap-2">
                            <Button
                                type="button"
                                variant="outline"
                                onClick={() => setOpen(false)}
                            >
                                Cancel
                            </Button>
                            <Button type="submit" disabled={createMutation.isPending}>
                                {createMutation.isPending ? 'Creating...' : 'Create Office Pair'}
                            </Button>
                        </div>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
