import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { RegisterForm } from '../RegisterForm';
import { authApi } from '../../api/auth.api';
import { BrowserRouter } from 'react-router-dom';
import { toast } from 'sonner';

// Mock dependencies
vi.mock('../../api/auth.api');
vi.mock('sonner', () => ({
    toast: {
        success: vi.fn(),
        error: vi.fn(),
    },
}));

// Mock useNavigate
const navigateMock = vi.fn();
vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
        ...actual,
        useNavigate: () => navigateMock,
    };
});

describe('RegisterForm', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    const renderComponent = () => {
        render(
            <BrowserRouter>
                <RegisterForm />
            </BrowserRouter>
        );
    };

    it('renders register form correctly', () => {
        renderComponent();
        expect(screen.getByLabelText(/full name/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/phone \(username\)/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/^password/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/confirm password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /register/i })).toBeInTheDocument();
    });

    it('shows validation error for password mismatch', async () => {
        renderComponent();

        fireEvent.change(screen.getByLabelText(/^password/i), { target: { value: 'password123' } });
        fireEvent.change(screen.getByLabelText(/confirm password/i), { target: { value: 'nomatch' } });
        fireEvent.click(screen.getByRole('button', { name: /register/i }));

        await waitFor(() => {
            expect(screen.getByText(/passwords don't match/i)).toBeInTheDocument();
        });
    });

    it('handles successful registration', async () => {
        // Mock API response
        vi.mocked(authApi.register).mockResolvedValue({
            success: true,
            data: undefined,
            message: 'Success',
            errorCode: '',
            timestamp: ''
        });

        renderComponent();

        fireEvent.change(screen.getByLabelText(/full name/i), { target: { value: 'Test User' } });
        fireEvent.change(screen.getByLabelText(/phone \(username\)/i), { target: { value: '0901234567' } });
        fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@example.com' } });
        fireEvent.change(screen.getByLabelText(/address/i), { target: { value: '123 Test St' } });
        fireEvent.change(screen.getByLabelText(/^password/i), { target: { value: 'password123' } });
        fireEvent.change(screen.getByLabelText(/confirm password/i), { target: { value: 'password123' } });

        fireEvent.click(screen.getByRole('button', { name: /register/i }));

        await waitFor(() => {
            expect(authApi.register).toHaveBeenCalledWith({
                fullName: 'Test User',
                username: '0901234567',
                email: 'test@example.com',
                address: '123 Test St',
                password: 'password123'
                // confirmPassword should NOT be sent to API
            });
            expect(toast.success).toHaveBeenCalledWith(expect.stringContaining('Registration successful'));
            expect(navigateMock).toHaveBeenCalledWith('/login');
        });
    });

    it('handles registration failure', async () => {
        // Mock API error
        const error = new Error('User already exists');
        vi.mocked(authApi.register).mockRejectedValue(error);

        renderComponent();

        fireEvent.change(screen.getByLabelText(/full name/i), { target: { value: 'Test User' } });
        fireEvent.change(screen.getByLabelText(/phone \(username\)/i), { target: { value: '0901234567' } });
        fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@example.com' } });
        fireEvent.change(screen.getByLabelText(/address/i), { target: { value: '123 Test St' } });
        fireEvent.change(screen.getByLabelText(/^password/i), { target: { value: 'password123' } });
        fireEvent.change(screen.getByLabelText(/confirm password/i), { target: { value: 'password123' } });

        fireEvent.click(screen.getByRole('button', { name: /register/i }));

        await waitFor(() => {
            expect(authApi.register).toHaveBeenCalled();
            expect(toast.error).toHaveBeenCalledWith('User already exists');
        });
    });
});
