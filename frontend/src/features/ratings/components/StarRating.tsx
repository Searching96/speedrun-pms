import { Star } from 'lucide-react';
import { cn } from '@/lib/utils';

interface StarRatingProps {
    value: number;
    onChange?: (value: number) => void;
    readonly?: boolean;
    size?: 'sm' | 'md' | 'lg';
}

export function StarRating({ value, onChange, readonly = false, size = 'md' }: StarRatingProps) {
    const sizeClasses = {
        sm: 'h-4 w-4',
        md: 'h-6 w-6',
        lg: 'h-8 w-8',
    };

    return (
        <div className="flex gap-1">
            {[1, 2, 3, 4, 5].map((star) => (
                <button
                    key={star}
                    type="button"
                    onClick={() => !readonly && onChange?.(star)}
                    disabled={readonly}
                    className={cn(
                        'transition-colors',
                        !readonly && 'hover:scale-110 cursor-pointer',
                        readonly && 'cursor-default'
                    )}
                >
                    <Star
                        className={cn(
                            sizeClasses[size],
                            star <= value
                                ? 'fill-yellow-400 text-yellow-400'
                                : 'fill-none text-gray-300'
                        )}
                    />
                </button>
            ))}
        </div>
    );
}
