import { useMutation } from '@tanstack/react-query';
import type { UseMutationOptions, UseMutationResult } from '@tanstack/react-query';

type MutationFn<TData, TVariables> = (variables: TVariables) => Promise<TData>;

interface UseApiMutationOptions<TData, TVariables>
    extends Omit<UseMutationOptions<TData, Error, TVariables>, 'mutationFn'> {
    mutationFn: MutationFn<TData, TVariables>;
    successMessage?: string;
    errorMessage?: string;
}

/**
 * Wrapper around useMutation with standardized error handling
 * and optional console notifications (replace with toast later)
 */
export function useApiMutation<TData = unknown, TVariables = void>({
    mutationFn,
    successMessage,
    errorMessage,
    onSuccess,
    onError,
    ...options
}: UseApiMutationOptions<TData, TVariables>): UseMutationResult<TData, Error, TVariables> {
    return useMutation<TData, Error, TVariables>({
        mutationFn,
        onSuccess: (...args) => {
            if (successMessage) {
                // TODO: Replace with toast notification when UI library is added
                console.log('✅', successMessage);
            }
            // Forward all arguments to user's callback
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            (onSuccess as any)?.(...args);
        },
        onError: (...args) => {
            const error = args[0];
            const message = errorMessage || error.message || 'Something went wrong';
            // TODO: Replace with toast notification when UI library is added
            console.error('❌', message);
            // Forward all arguments to user's callback
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            (onError as any)?.(...args);
        },
        ...options,
    });
}
