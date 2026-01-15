import { Routes, Route } from 'react-router-dom';
import { useAuth } from '@/features/auth';

// Placeholder pages - to be replaced with actual implementations
function HomePage() {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background p-8">
      <div className="w-full max-w-md space-y-6 rounded-lg border bg-card p-8 shadow-sm">
        <h1 className="text-2xl font-bold text-foreground">
          Postal Management System
        </h1>

        {isAuthenticated ? (
          <>
            <p className="text-muted-foreground">
              Welcome, <strong>{user?.fullName || user?.username}</strong>!
            </p>
            <p className="text-sm text-muted-foreground">
              Role: <code className="rounded bg-muted px-1 py-0.5">{user?.role}</code>
            </p>
            <button
              onClick={logout}
              className="w-full rounded-md bg-destructive px-4 py-2 text-destructive-foreground transition-colors hover:bg-destructive/90"
            >
              Logout
            </button>
          </>
        ) : (
          <div className="space-y-4">
            <p className="text-muted-foreground">
              Please login to continue.
            </p>
            <a
              href="/login"
              className="block w-full rounded-md bg-primary px-4 py-2 text-center text-primary-foreground transition-colors hover:bg-primary/90"
            >
              Go to Login
            </a>
          </div>
        )}
      </div>
    </div>
  );
}

function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="w-full max-w-md space-y-6 rounded-lg border bg-card p-8 shadow-sm">
        <h1 className="text-2xl font-bold text-foreground">Login</h1>
        <p className="text-muted-foreground">
          Login page placeholder - implement login form here.
        </p>
        <a
          href="/"
          className="block text-center text-sm text-primary hover:underline"
        >
          Back to Home
        </a>
      </div>
    </div>
  );
}

function UnauthorizedPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background">
      <div className="space-y-4 text-center">
        <h1 className="text-4xl font-bold text-destructive">403</h1>
        <p className="text-lg text-muted-foreground">
          You don't have permission to access this page.
        </p>
        <a
          href="/"
          className="inline-block rounded-md bg-primary px-4 py-2 text-primary-foreground transition-colors hover:bg-primary/90"
        >
          Go Home
        </a>
      </div>
    </div>
  );
}

function NotFoundPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background">
      <div className="space-y-4 text-center">
        <h1 className="text-4xl font-bold text-muted-foreground">404</h1>
        <p className="text-lg text-muted-foreground">Page not found.</p>
        <a
          href="/"
          className="inline-block rounded-md bg-primary px-4 py-2 text-primary-foreground transition-colors hover:bg-primary/90"
        >
          Go Home
        </a>
      </div>
    </div>
  );
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

export default App;
