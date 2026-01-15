import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Package, Search } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { toast } from 'sonner';
import { api } from '@/lib/axios';
import type { OrderResponse } from '@/types';

const searchSchema = z.object({
    trackingNumber: z.string().min(1, 'Tracking number is required'),
});

const updateSchema = z.object({
    status: z.string().min(1, 'Status is required'),
    description: z.string().optional(),
    locationName: z.string().optional(),
});

export default function ReceiveOrderPage() {
    const [order, setOrder] = useState<OrderResponse | null>(null);
    const [loading, setLoading] = useState(false);
    const [updating, setUpdating] = useState(false);

    const searchForm = useForm({
        resolver: zodResolver(searchSchema),
        defaultValues: {
            trackingNumber: '',
        },
    });

    const updateForm = useForm({
        resolver: zodResolver(updateSchema),
        defaultValues: {
            status: '',
            description: '',
            locationName: '',
        },
    });

    const handleSearch = async (values: z.infer<typeof searchSchema>) => {
        setLoading(true);
        try {
            const data = await api.get<OrderResponse>(`/api/orders/${values.trackingNumber}`);
            setOrder(data);
            toast.success('Order found');
        } catch (error: any) {
            toast.error(error.message || 'Order not found');
            setOrder(null);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateStatus = async (values: z.infer<typeof updateSchema>) => {
        if (!order) return;

        setUpdating(true);
        try {
            await api.post('/api/tracking/events', {
                orderId: order.id,
                status: values.status,
                description: values.description,
                locationName: values.locationName,
            });
            toast.success('Order status updated successfully');
            updateForm.reset();
            // Refresh order data
            const refreshed = await api.get<OrderResponse>(`/api/orders/${order.trackingNumber}`);
            setOrder(refreshed);
        } catch (error: any) {
            toast.error(error.message || 'Failed to update status');
        } finally {
            setUpdating(false);
        }
    };

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Receive Order</h1>
                <p className="text-muted-foreground">
                    Scan or enter tracking number to process orders
                </p>
            </div>

            {/* Search Form */}
            <Card>
                <CardHeader>
                    <CardTitle>Find Order</CardTitle>
                </CardHeader>
                <CardContent>
                    <Form {...searchForm}>
                        <form onSubmit={searchForm.handleSubmit(handleSearch)} className="flex gap-2">
                            <FormField
                                control={searchForm.control}
                                name="trackingNumber"
                                render={({ field }) => (
                                    <FormItem className="flex-1">
                                        <FormControl>
                                            <Input
                                                placeholder="Enter tracking number..."
                                                {...field}
                                                onChange={(e) => field.onChange(e.target.value.toUpperCase())}
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <Button type="submit" disabled={loading}>
                                <Search className="mr-2 h-4 w-4" />
                                Search
                            </Button>
                        </form>
                    </Form>
                </CardContent>
            </Card>

            {/* Order Details */}
            {order && (
                <>
                    <Card>
                        <CardHeader>
                            <CardTitle>Order Details</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <p className="text-sm font-medium text-muted-foreground">Tracking Number</p>
                                    <p className="font-mono font-semibold">{order.trackingNumber}</p>
                                </div>
                                <div>
                                    <p className="text-sm font-medium text-muted-foreground">Current Status</p>
                                    <p className="font-semibold">{order.status}</p>
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <p className="text-sm font-medium text-muted-foreground mb-1">From</p>
                                    <p className="font-semibold">{order.senderName}</p>
                                    <p className="text-sm">{order.senderPhone}</p>
                                    <p className="text-sm text-muted-foreground">{order.senderAddress}</p>
                                </div>
                                <div>
                                    <p className="text-sm font-medium text-muted-foreground mb-1">To</p>
                                    <p className="font-semibold">{order.receiverName}</p>
                                    <p className="text-sm">{order.receiverPhone}</p>
                                    <p className="text-sm text-muted-foreground">{order.receiverAddress}</p>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Update Status Form */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Update Status</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <Form {...updateForm}>
                                <form onSubmit={updateForm.handleSubmit(handleUpdateStatus)} className="space-y-4">
                                    <FormField
                                        control={updateForm.control}
                                        name="status"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>New Status *</FormLabel>
                                                <Select onValueChange={field.onChange} value={field.value}>
                                                    <FormControl>
                                                        <SelectTrigger>
                                                            <SelectValue placeholder="Select status" />
                                                        </SelectTrigger>
                                                    </FormControl>
                                                    <SelectContent>
                                                        <SelectItem value="RECEIVED_AT_OFFICE">Received at Office</SelectItem>
                                                        <SelectItem value="SORTING">Sorting</SelectItem>
                                                        <SelectItem value="IN_TRANSIT">In Transit</SelectItem>
                                                        <SelectItem value="OUT_FOR_DELIVERY">Out for Delivery</SelectItem>
                                                        <SelectItem value="DELIVERED">Delivered</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={updateForm.control}
                                        name="locationName"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Location</FormLabel>
                                                <FormControl>
                                                    <Input placeholder="e.g., HCM Distribution Center" {...field} />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={updateForm.control}
                                        name="description"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Description</FormLabel>
                                                <FormControl>
                                                    <Textarea
                                                        placeholder="Additional notes..."
                                                        rows={3}
                                                        {...field}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <Button type="submit" disabled={updating} className="w-full">
                                        {updating ? 'Updating...' : 'Update Status'}
                                    </Button>
                                </form>
                            </Form>
                        </CardContent>
                    </Card>
                </>
            )}

            {!order && !loading && (
                <div className="text-center py-12 text-muted-foreground">
                    <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                    <p>Search for an order to get started</p>
                </div>
            )}
        </div>
    );
}
