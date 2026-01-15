import axios, { type AxiosError } from 'axios';
import { logger } from './logger';

export interface ApiError {
    message: string;
    code?: string;
    details?: unknown;
}

/**
 * Parse error from API response
 */
export function parseApiError(error: unknown): ApiError {
    // Axios error with response
    if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError<{ message?: string; error?: string }>;

        if (axiosError.response) {
            const { data, status } = axiosError.response;

            return {
                message: data?.message || data?.error || getDefaultErrorMessage(status),
                code: `HTTP_${status}`,
                details: data,
            };
        }

        // Network error
        if (axiosError.request) {
            return {
                message: 'Unable to connect to the server. Please check your internet connection.',
                code: 'NETWORK_ERROR',
            };
        }
    }

    // Generic error
    if (error instanceof Error) {
        return {
            message: error.message,
        };
    }

    // Unknown error
    return {
        message: 'An unexpected error occurred',
    };
}

/**
 * Get user-friendly error message based on HTTP status code
 */
function getDefaultErrorMessage(status: number): string {
    switch (status) {
        case 400:
            return 'Invalid request. Please check your input.';
        case 401:
            return 'You are not authenticated. Please log in.';
        case 403:
            return 'You do not have permission to perform this action.';
        case 404:
            return 'The requested resource was not found.';
        case 409:
            return 'A conflict occurred. The resource may already exist.';
        case 422:
            return 'Validation failed. Please check your input.';
        case 429:
            return 'Too many requests. Please try again later.';
        case 500:
            return 'A server error occurred. Please try again later.';
        case 503:
            return 'The service is temporarily unavailable. Please try again later.';
        default:
            return `An error occurred (${status})`;
    }
}

/**
 * Log error to console in development, send to monitoring in production
 */
export function logError(error: unknown, context?: string) {
    const apiError = parseApiError(error);

    // Log using structured logger
    logger.error(`[Error${context ? ` - ${context}` : ''}]: ${apiError.message}`, {
        originalError: error,
        apiError,
        context
    });
}
