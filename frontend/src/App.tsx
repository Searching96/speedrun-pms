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
const PickupRequestsPage = lazy(() => import('@/pages/customer/PickupRequestsPage').then(module => ({ default: module.PickupRequestsPage })));
const CustomerTrackingPage = lazy(() => import('@/pages/customer/CustomerTrackingPage'));
const PickupManagementPage = lazy(() => import('@/pages/staff/PickupManagementPage').then(module => ({ default: module.PickupManagementPage })));
const TrackingPage = lazy(() => import('@/pages/TrackingPage'));
const TasksPage = lazy(() => import('@/pages/shipper/TasksPage'));
const ReceiveOrderPage = lazy(() => import('@/pages/staff/ReceiveOrderPage'));
const AdminDashboard = lazy(() => import('@/pages/admin/AdminDashboard'));
const EmployeeManagement = lazy(() => import('@/pages/admin/EmployeeManagement'));
const WardOfficeManagement = lazy(() => import('@/pages/admin/WardOfficeManagement'));
const ReportsPage = lazy(() => import('@/pages/admin/ReportsPage'));
const AdministrativeUnitsPage = lazy(() => import('@/pages/admin/AdministrativeUnitsPage'));
const StaffOrdersPage = lazy(() => import('@/pages/staff/StaffOrdersPage'));
const OrderDetailsPage = lazy(() => import('@/pages/orders/OrderDetailsPage'));

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
        <Route path="/track" element={<TrackingPage />} />
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
          <Route path="pickups" element={<PickupRequestsPage />} />
          <Route path="tracking" element={<CustomerTrackingPage />} />
          <Route path="orders/:trackingNumber" element={<OrderDetailsPage />} />
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
          <Route index element={<AdminDashboard />} />
          <Route path="employees" element={<EmployeeManagement />} />
          <Route path="ward-offices" element={<WardOfficeManagement />} />
          <Route path="reports" element={<ReportsPage />} />
          <Route path="units" element={<AdministrativeUnitsPage />} />
          <Route path="orders/:trackingNumber" element={<OrderDetailsPage />} />
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
          <Route path="orders" element={<StaffOrdersPage />} />
          <Route path="orders/receive" element={<ReceiveOrderPage />} />
          <Route path="pickups" element={<PickupManagementPage />} />
          <Route path="orders/:trackingNumber" element={<OrderDetailsPage />} />
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
          <Route index element={<TasksPage />} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
}

export default App;
