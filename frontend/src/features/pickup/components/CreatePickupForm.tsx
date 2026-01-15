import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { createPickupSchema, type CreatePickupSchema, timeSlotLabels } from '../schemas/pickup.schema';
import { useCreatePickup } from '../hooks/useCreatePickup';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/components/ui/card';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { Calendar } from 'lucide-react';

interface CreatePickupFormProps {
    orderId?: string;
    onSuccess?: () => void;
}

export function CreatePickupForm({ orderId, onSuccess }: CreatePickupFormProps) {
    const { mutate: createPickup, isPending } = useCreatePickup();

    const {
        register,
        handleSubmit,
        formState: { errors },
        setValue,
        watch,
    } = useForm<CreatePickupSchema>({
        resolver: zodResolver(createPickupSchema),
        defaultValues: {
            orderId: orderId || '',
        },
    });

    const onSubmit = (data: CreatePickupSchema) => {
        createPickup(data, {
            onSuccess: () => {
                onSuccess?.();
            },
        });
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>Create Pickup Request</CardTitle>
                <CardDescription>
                    Request a shipper to pick up your package from your location
                </CardDescription>
            </CardHeader>
            <form onSubmit={handleSubmit(onSubmit)}>
                <CardContent className="space-y-4">
                    {/* Order ID */}
                    <div className="space-y-2">
                        <Label htmlFor="orderId">Order ID *</Label>
                        <Input
                            id="orderId"
                            placeholder="Enter order ID"
                            {...register('orderId')}
                            disabled={!!orderId}
                        />
                        {errors.orderId && (
                            <p className="text-sm text-destructive">{errors.orderId.message}</p>
                        )}
                    </div>

                    {/* Pickup Address */}
                    <div className="space-y-2">
                        <Label htmlFor="pickupAddress">Pickup Address *</Label>
                        <Input
                            id="pickupAddress"
                            placeholder="123 Main Street, District 1"
                            {...register('pickupAddress')}
                        />
                        {errors.pickupAddress && (
                            <p className="text-sm text-destructive">{errors.pickupAddress.message}</p>
                        )}
                    </div>

                    {/* Ward Code */}
                    <div className="space-y-2">
                        <Label htmlFor="pickupWardCode">Ward Code *</Label>
                        <Input
                            id="pickupWardCode"
                            placeholder="e.g., 00001"
                            {...register('pickupWardCode')}
                        />
                        {errors.pickupWardCode && (
                            <p className="text-sm text-destructive">{errors.pickupWardCode.message}</p>
                        )}
                    </div>

                    {/* Contact Name */}
                    <div className="space-y-2">
                        <Label htmlFor="pickupContactName">Contact Name *</Label>
                        <Input
                            id="pickupContactName"
                            placeholder="John Doe"
                            {...register('pickupContactName')}
                        />
                        {errors.pickupContactName && (
                            <p className="text-sm text-destructive">{errors.pickupContactName.message}</p>
                        )}
                    </div>

                    {/* Contact Phone */}
                    <div className="space-y-2">
                        <Label htmlFor="pickupContactPhone">Contact Phone *</Label>
                        <Input
                            id="pickupContactPhone"
                            placeholder="0901234567"
                            {...register('pickupContactPhone')}
                        />
                        {errors.pickupContactPhone && (
                            <p className="text-sm text-destructive">{errors.pickupContactPhone.message}</p>
                        )}
                    </div>

                    {/* Preferred Date */}
                    <div className="space-y-2">
                        <Label htmlFor="preferredDate">Preferred Date *</Label>
                        <div className="relative">
                            <Input
                                id="preferredDate"
                                type="date"
                                {...register('preferredDate')}
                                min={new Date().toISOString().split('T')[0]}
                            />
                            <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground pointer-events-none" />
                        </div>
                        {errors.preferredDate && (
                            <p className="text-sm text-destructive">{errors.preferredDate.message}</p>
                        )}
                    </div>

                    {/* Time Slot */}
                    <div className="space-y-2">
                        <Label htmlFor="preferredTimeSlot">Preferred Time Slot (Optional)</Label>
                        <Select
                            onValueChange={(value) => setValue('preferredTimeSlot', value as any)}
                            defaultValue={watch('preferredTimeSlot')}
                        >
                            <SelectTrigger>
                                <SelectValue placeholder="Select a time slot" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="MORNING">{timeSlotLabels.MORNING}</SelectItem>
                                <SelectItem value="AFTERNOON">{timeSlotLabels.AFTERNOON}</SelectItem>
                                <SelectItem value="EVENING">{timeSlotLabels.EVENING}</SelectItem>
                            </SelectContent>
                        </Select>
                        {errors.preferredTimeSlot && (
                            <p className="text-sm text-destructive">{errors.preferredTimeSlot.message}</p>
                        )}
                    </div>
                </CardContent>
                <CardFooter>
                    <Button type="submit" className="w-full" disabled={isPending}>
                        {isPending ? 'Creating...' : 'Create Pickup Request'}
                    </Button>
                </CardFooter>
            </form>
        </Card>
    );
}
