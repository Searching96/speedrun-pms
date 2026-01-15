import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Search } from 'lucide-react';
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
import { trackingSchema, type TrackingFormValues } from '../schema';

interface TrackingFormProps {
    onSubmit: (trackingNumber: string) => void;
    loading?: boolean;
}

export function TrackingForm({ onSubmit, loading }: TrackingFormProps) {
    const form = useForm<TrackingFormValues>({
        resolver: zodResolver(trackingSchema),
        defaultValues: {
            trackingNumber: '',
        },
    });

    const handleSubmit = (values: TrackingFormValues) => {
        onSubmit(values.trackingNumber.toUpperCase());
    };

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
                <FormField
                    control={form.control}
                    name="trackingNumber"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Tracking Number</FormLabel>
                            <div className="flex gap-2">
                                <FormControl>
                                    <Input
                                        placeholder="Enter tracking number (e.g., TN20250115001)"
                                        {...field}
                                        onChange={(e) => field.onChange(e.target.value.toUpperCase())}
                                        className="flex-1"
                                    />
                                </FormControl>
                                <Button type="submit" disabled={loading}>
                                    <Search className="mr-2 h-4 w-4" />
                                    Track
                                </Button>
                            </div>
                            <FormMessage />
                        </FormItem>
                    )}
                />
            </form>
        </Form>
    );
}
