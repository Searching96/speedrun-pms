import { Suspense, lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { DashboardLayout } from '@/components/layout/DashboardLayout';
import { ProtectedRoute } from '@/components/ProtectedRoute';

// Lazy load pages for performance (Code Splitting)
const LoginPage = lazy(() => import('@/pages/LoginPage').then(module => ({ default: module.LoginPage })));
const HomePage = lazy(() => import('@/pages/HomePage').then(module => ({ default: module.HomePage })));

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

function LoadingSpinner() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
    </div>
  );
}

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />

        {/* Protected Dashboard Routes */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<HomePage />} />
          {/* Add more protected routes here */}
          <Route path="orders" element={<div>Orders Page</div>} />
          <Route path="pickups" element={<div>Pickups Page</div>} />
          <Route path="tracking" element={<div>Tracking Page</div>} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
}

export default App;
