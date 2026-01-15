import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import { StarRating } from './StarRating';
import { ratingSchema, type RatingFormValues } from '../schema';
import { ratingsApi } from '../api';

interface RatingModalProps {
    open: boolean;
    onClose: () => void;
    orderId: string;
    trackingNumber: string;
}

export function RatingModal({ open, onClose, orderId, trackingNumber }: RatingModalProps) {
    const [loading, setLoading] = useState(false);

    const form = useForm<RatingFormValues>({
        resolver: zodResolver(ratingSchema),
        defaultValues: {
            orderId,
            overallRating: 0,
            deliverySpeedRating: 0,
            shipperAttitudeRating: 0,
            comment: '',
        },
    });

    const handleSubmit = async (values: RatingFormValues) => {
        setLoading(true);
        try {
            await ratingsApi.createRating(values);
            toast.success('Thank you for your feedback!');
            onClose();
            form.reset();
        } catch (error: any) {
            toast.error(error.message || 'Failed to submit rating');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[500px]">
                <DialogHeader>
                    <DialogTitle>Rate Your Experience</DialogTitle>
                    <DialogDescription>
                        How was your delivery experience for order {trackingNumber}?
                    </DialogDescription>
                </DialogHeader>

                <Form {...form}>
                    <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
                        <FormField
                            control={form.control}
                            name="overallRating"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Overall Rating *</FormLabel>
                                    <FormControl>
                                        <StarRating
                                            value={field.value}
                                            onChange={field.onChange}
                                            size="lg"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="deliverySpeedRating"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Delivery Speed</FormLabel>
                                    <FormControl>
                                        <StarRating
                                            value={field.value || 0}
                                            onChange={field.onChange}
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="shipperAttitudeRating"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Shipper Attitude</FormLabel>
                                    <FormControl>
                                        <StarRating
                                            value={field.value || 0}
                                            onChange={field.onChange}
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="comment"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Comments (Optional)</FormLabel>
                                    <FormControl>
                                        <Textarea
                                            placeholder="Share your experience..."
                                            className="resize-none"
                                            rows={4}
                                            {...field}
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <div className="flex justify-end gap-2">
                            <Button type="button" variant="outline" onClick={onClose} disabled={loading}>
                                Cancel
                            </Button>
                            <Button type="submit" disabled={loading}>
                                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                Submit Rating
                            </Button>
                        </div>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
