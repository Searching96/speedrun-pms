import { RegisterForm } from '@/features/auth/components/RegisterForm';

export function RegisterPage() {
    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
            <div className="w-full max-w-lg space-y-8">
                <div className="text-center">
                    <h2 className="mt-6 text-3xl font-bold tracking-tight text-gray-900">Create a new account</h2>
                </div>
                <RegisterForm />
            </div>
        </div>
    );
}
