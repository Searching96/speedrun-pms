import { useQuery } from '@tanstack/react-query';
import type { UseQueryOptions, UseQueryResult } from '@tanstack/react-query';

type QueryFn<T> = () => Promise<T>;

interface UseApiQueryOptions<T> extends Omit<UseQueryOptions<T, Error>, 'queryKey' | 'queryFn'> {
    queryKey: string[];
    queryFn: QueryFn<T>;
}

/**
 * Wrapper around useQuery with standardized error handling
 * and consistent options across the application
 */
export function useApiQuery<T>({
    queryKey,
    queryFn,
    ...options
}: UseApiQueryOptions<T>): UseQueryResult<T, Error> {
    return useQuery<T, Error>({
        queryKey,
        queryFn,
        retry: 1,
        staleTime: 1000 * 60, // 1 minute
        ...options,
    });
}
