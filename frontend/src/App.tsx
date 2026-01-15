import { Suspense, lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { DashboardLayout } from '@/components/layout/DashboardLayout';
import { ProtectedRoute } from '@/components/ProtectedRoute';
import { ROLES } from '@/features/auth/roles';

// Lazy load pages for performance (Code Splitting)
const LoginPage = lazy(() => import('@/pages/auth/LoginPage').then(module => ({ default: module.LoginPage })));
const RegisterPage = lazy(() => import('@/pages/auth/RegisterPage').then(module => ({ default: module.RegisterPage })));
const HomePage = lazy(() => import('@/pages/HomePage').then(module => ({ default: module.HomePage })));
const OrdersPage = lazy(() => import('@/pages/orders/OrdersPage').then(module => ({ default: module.OrdersPage })));


import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { NotFoundPage } from '@/pages/NotFoundPage';
import { UnauthorizedPage } from '@/pages/UnauthorizedPage';

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />

        {/* Customer Routes */}
        <Route
          path="/"
          element={
            <ProtectedRoute allowedRoles={ROLES.CUSTOMER}>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<HomePage />} />
          <Route path="orders" element={<OrdersPage />} />
          <Route path="tracking" element={<div>Tracking Page</div>} />
        </Route>

        {/* Admin/Manager Routes */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute allowedRoles={[...ROLES.ADMIN_GROUP, ...ROLES.MANAGER_GROUP]}>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<div>Admin Dashboard</div>} />
        </Route>

        {/* Staff Routes */}
        <Route
          path="/staff"
          element={
            <ProtectedRoute allowedRoles={ROLES.STAFF_GROUP}>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<div>Staff Dashboard</div>} />
          <Route path="orders" element={<div>Orders Management</div>} />
          <Route path="pickups" element={<div>Pickup Requests</div>} />
        </Route>

        {/* Shipper Routes */}
        <Route
          path="/shipper"
          element={
            <ProtectedRoute allowedRoles={ROLES.SHIPPER}>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<div>My Tasks</div>} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
}

export default App;
