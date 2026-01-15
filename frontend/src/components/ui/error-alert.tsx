import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { AlertCircle } from 'lucide-react';

interface ErrorAlertProps {
    title?: string;
    message: string;
    onRetry?: () => void;
}

export function ErrorAlert({ title = 'Error', message, onRetry }: ErrorAlertProps) {
    return (
        <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertTitle>{title}</AlertTitle>
            <AlertDescription className="flex flex-col gap-2">
                <p>{message}</p>
                {onRetry && (
                    <button
                        onClick={onRetry}
                        className="w-fit text-sm underline underline-offset-4 hover:no-underline"
                    >
                        Try again
                    </button>
                )}
            </AlertDescription>
        </Alert>
    );
}
