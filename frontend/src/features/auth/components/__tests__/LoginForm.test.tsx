import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { LoginForm } from '../LoginForm';
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

// Mock useAuth
const mockLogin = vi.fn();
vi.mock('@/features/auth', () => ({
    useAuth: () => ({
        login: mockLogin,
        isAuthenticated: false,
    }),
}));

describe('LoginForm', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    const renderComponent = () => {
        render(
            <BrowserRouter>
                <LoginForm />
            </BrowserRouter>
        );
    };

    it('renders login form correctly', () => {
        renderComponent();
        expect(screen.getByLabelText(/phone number/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    });

    it('shows validation error for invalid phone number', async () => {
        renderComponent();

        fireEvent.change(screen.getByLabelText(/phone number/i), { target: { value: '123' } });
        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(screen.getByText(/Phone number must be at least 10 digits/i)).toBeInTheDocument();
        });
    });

    it('handles successful login', async () => {
        // Mock API responses - axios interceptor returns response.data directly
        vi.mocked(authApi.login).mockResolvedValue({
            data: { token: 'fake-token' }
        } as any);

        vi.mocked(authApi.fetchMe).mockResolvedValue({
            data: { id: '1', username: '0901234567', role: 'CUSTOMER' }
        } as any);

        renderComponent();

        fireEvent.change(screen.getByLabelText(/phone number/i), { target: { value: '0901234567' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'password123' } });
        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(authApi.login).toHaveBeenCalledWith({
                username: '0901234567',
                password: 'password123'
            });
            expect(localStorage.getItem('accessToken')).toBe('fake-token');
            expect(toast.success).toHaveBeenCalledWith('Login successful');
            expect(navigateMock).toHaveBeenCalledWith('/');
        });
    });

    it('handles login failure', async () => {
        // Mock API error
        const error = new Error('Invalid credentials');
        vi.mocked(authApi.login).mockRejectedValue(error);

        renderComponent();

        fireEvent.change(screen.getByLabelText(/phone number/i), { target: { value: '0901234567' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'wrongpassword' } });
        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(authApi.login).toHaveBeenCalled();
            expect(toast.error).toHaveBeenCalledWith('Invalid credentials');
        });
    });
});
