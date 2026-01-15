import { render, screen, fireEvent, waitFor } from '@/tests/utils';
import { LoginPage } from './LoginPage';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import * as api from '@/api';
import { toast } from 'sonner';

// Mock API and toast
vi.mock('@/api', () => ({
    authApi: {
        login: vi.fn(),
        fetchMe: vi.fn(),
    },
}));

vi.mock('sonner', () => ({
    toast: {
        success: vi.fn(),
        error: vi.fn(),
    },
}));

// Mock router hooks
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
        ...actual,
        useNavigate: () => mockNavigate,
        useLocation: () => ({ state: null }),
    };
});

// Mock Auth context
const mockLogin = vi.fn();
vi.mock('@/features/auth', async () => {
    const actual = await vi.importActual('@/features/auth');
    return {
        ...actual,
        useAuth: () => ({
            login: mockLogin,
            isAuthenticated: false,
        }),
    };
});

describe('LoginPage', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('renders login form correctly', () => {
        render(<LoginPage />);

        expect(screen.getByRole('heading', { name: /sign in/i })).toBeInTheDocument();
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
    });

    it('shows validation errors when submitting empty form', async () => {
        render(<LoginPage />);

        const submitBtn = screen.getByRole('button', { name: /sign in/i });
        fireEvent.click(submitBtn);

        await waitFor(() => {
            expect(screen.getByText(/username is required/i)).toBeInTheDocument();
            expect(screen.getByText(/password is required/i)).toBeInTheDocument();
        });

        expect(api.authApi.login).not.toHaveBeenCalled();
    });

    it('handles successful login', async () => {
        // Mock successful API responses
        const mockToken = 'mock-token';
        const mockUser = { id: '1', username: 'test', role: 'ADMIN' };

        vi.mocked(api.authApi.login).mockResolvedValue({
            data: { token: mockToken } as any
        } as any);

        vi.mocked(api.authApi.fetchMe).mockResolvedValue({
            data: mockUser
        } as any);

        render(<LoginPage />);

        // Fill credentials
        fireEvent.change(screen.getByLabelText(/username/i), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'password123' } });

        // Submit
        fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

        await waitFor(() => {
            expect(api.authApi.login).toHaveBeenCalledWith({ username: 'testuser', password: 'password123' });
            expect(api.authApi.fetchMe).toHaveBeenCalled();
            expect(mockLogin).toHaveBeenCalledWith(mockToken, mockUser);
            expect(toast.success).toHaveBeenCalledWith('Login successful!');
            expect(mockNavigate).toHaveBeenCalled();
        });
    });

    it('handles login failure', async () => {
        // Mock failed API response
        const errorMsg = 'Invalid credentials';
        vi.mocked(api.authApi.login).mockRejectedValue(new Error(errorMsg));

        render(<LoginPage />);

        // Fill credentials
        fireEvent.change(screen.getByLabelText(/username/i), { target: { value: 'wrong' } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'wrong' } });

        // Submit
        fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

        await waitFor(() => {
            expect(api.authApi.login).toHaveBeenCalled();
            expect(mockLogin).not.toHaveBeenCalled();
            expect(toast.error).toHaveBeenCalledWith(errorMsg);
        });
    });
});
