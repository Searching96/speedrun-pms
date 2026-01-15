import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema, type LoginSchema } from '../schemas/auth.schema';
import { authApi } from '../api/auth.api';
import { useState } from 'react';
// We'll replace these with real UI components later, for now using standard HTML or empty placeholders if UI lib not fully ready
// But guidelines say use components/ui. I'll check if they exist. Defaulting to standard HTML with tailwind for now if uncertainty exists, 
// but assuming Shadcn Setup:
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { toast } from 'sonner';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '@/features/auth';
import { ROLES } from '@/features/auth/roles';

export function LoginForm() {
    const navigate = useNavigate();
    const { login } = useAuth(); // Use context to update global state
    const [loading, setLoading] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<LoginSchema>({
        resolver: zodResolver(loginSchema),
    });

    const onSubmit = async (data: LoginSchema) => {
        setLoading(true);
        try {
            // 1. Perform Login
            const loginRes = await authApi.login(data);
            if (loginRes.data) {
                const userData = loginRes.data;

                // 2. Update Global Auth State (Token is handled by cookie automatically)
                login(userData as any);
                toast.success('Login successful');

                // 3. Smart Redirection
                const role = userData.role as any;
                if (ROLES.ADMIN_GROUP.includes(role) || ROLES.MANAGER_GROUP.includes(role)) {
                    navigate('/admin');
                } else if (ROLES.STAFF_GROUP.includes(role)) {
                    navigate('/staff');
                } else if (role === ROLES.SHIPPER[0]) {
                    navigate('/shipper');
                } else {
                    // Default to Customer Dashboard
                    navigate('/');
                }
            }
        } catch (error: any) {
            console.error('Login Error:', error);
            toast.error(error.message || 'Login failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card className="w-full max-w-md mx-auto">
            <CardHeader>
                <CardTitle>Login</CardTitle>
                <CardDescription>Enter your credentials to access your account</CardDescription>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="username">Phone Number</Label>
                        <Input id="username" placeholder="0901234567" {...register('username')} />
                        {errors.username && <p className="text-sm text-red-500">{errors.username.message}</p>}
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="password">Password</Label>
                        <Input id="password" type="password" placeholder="******" {...register('password')} />
                        {errors.password && <p className="text-sm text-red-500">{errors.password.message}</p>}
                    </div>
                    <Button type="submit" className="w-full" disabled={loading}>
                        {loading ? 'Logging in...' : 'Login'}
                    </Button>
                </form>
            </CardContent>
            <CardFooter className="flex justify-center">
                <p className="text-sm text-gray-500">
                    Don't have an account? <Link to="/register" className="text-primary hover:underline">Register</Link>
                </p>
            </CardFooter>
        </Card>
    );
}
