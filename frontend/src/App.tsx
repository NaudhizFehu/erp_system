import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { Routes, Route } from 'react-router-dom'

import { ProtectedRoute } from '@/components/auth/ProtectedRoute'
import { RoleProtectedRoute } from '@/components/auth/RoleProtectedRoute'
import { Layout } from '@/components/layout/Layout'
import { AuthProvider } from '@/contexts/AuthContext'
import { NotificationProvider } from '@/contexts/NotificationContext'
import AccountDetail from '@/pages/accounting/AccountDetail'
import { AccountList } from '@/pages/accounting/AccountList'
import FinancialReportDetailPage from '@/pages/accounting/FinancialReportDetailPage'
import FinancialReportListPage from '@/pages/accounting/FinancialReportListPage'
import FinancialStatementPage from '@/pages/accounting/FinancialStatementPage'
import TransactionDetail from '@/pages/accounting/TransactionDetail'
import TransactionList from '@/pages/accounting/TransactionList'
import UserManagementPage from '@/pages/admin/UserManagementPage'
import { LoginPage } from '@/pages/auth/LoginPage'
import { CompanyDetail } from '@/pages/company/CompanyDetail'
import { CompanyList } from '@/pages/company/CompanyList'
import { Dashboard } from '@/pages/Dashboard'
import { DepartmentDetail } from '@/pages/hr/DepartmentDetail'
import { EmployeeDetail } from '@/pages/hr/EmployeeDetail'
import { EmployeeManagement } from '@/pages/hr/EmployeeManagement'
import { MyEmployeeProfile } from '@/pages/hr/MyEmployeeProfile'
import { InventoryDashboardPage } from '@/pages/inventory/InventoryDashboardPage'
import { ProductDetail } from '@/pages/inventory/ProductDetail'
import { ProductManagementPage } from '@/pages/inventory/ProductManagementPage'
import { NotificationListPage } from '@/pages/notifications/NotificationListPage'
import { NotificationTestPage } from '@/pages/notifications/NotificationTestPage'
import { UserProfilePage } from '@/pages/profile/UserProfilePage'
import { CustomerDetail } from '@/pages/sales/CustomerDetail'
import { CustomerList } from '@/pages/sales/CustomerList'
import { OrderDetail } from '@/pages/sales/OrderDetail'

/**
 * 메인 애플리케이션 컴포넌트
 * 라우팅 및 전체 레이아웃을 관리합니다
 */
function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <Routes>
          {/* 공개 라우트 */}
          <Route path="/login" element={<LoginPage />} />

          {/* 보호된 라우트 */}
          <Route
            path="/*"
            element={
              <ProtectedRoute>
                <Layout>
                  <Routes>
                    {/* 대시보드 */}
                    <Route path="/" element={<Dashboard />} />

                    {/* 인사관리 */}
                    {/* 모든 권한: 본인 정보 조회 */}
                    <Route
                      path="/hr/employees/me"
                      element={<MyEmployeeProfile />}
                    />
                    {/* SUPER_ADMIN, ADMIN, MANAGER: 직원 관리 */}
                    <Route
                      path="/hr/employees"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={['SUPER_ADMIN', 'ADMIN', 'MANAGER']}
                        >
                          <EmployeeManagement />
                        </RoleProtectedRoute>
                      }
                    />
                    <Route
                      path="/hr/employees/:id"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={['SUPER_ADMIN', 'ADMIN', 'MANAGER']}
                        >
                          <EmployeeDetail />
                        </RoleProtectedRoute>
                      }
                    />
                    <Route
                      path="/hr/departments/:id"
                      element={<DepartmentDetail />}
                    />

                    {/* 재고관리 */}
                    <Route
                      path="/inventory"
                      element={<InventoryDashboardPage companyId={1} />}
                    />
                    <Route
                      path="/inventory/dashboard"
                      element={<InventoryDashboardPage companyId={1} />}
                    />
                    <Route
                      path="/inventory/products"
                      element={<ProductManagementPage companyId={1} />}
                    />
                    <Route
                      path="/inventory/products/:id"
                      element={<ProductDetail />}
                    />

                    {/* 영업관리 */}
                    <Route path="/sales/customers" element={<CustomerList />} />
                    <Route
                      path="/sales/customers/:id"
                      element={<CustomerDetail />}
                    />
                    <Route path="/sales/orders/:id" element={<OrderDetail />} />

                    {/* 회계관리 */}
                    <Route
                      path="/accounting/accounts"
                      element={<AccountList />}
                    />
                    <Route
                      path="/accounting/accounts/:id"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={['SUPER_ADMIN', 'ADMIN']}
                        >
                          <AccountDetail />
                        </RoleProtectedRoute>
                      }
                    />
                    <Route
                      path="/accounting/transactions"
                      element={<TransactionList />}
                    />
                    <Route
                      path="/accounting/transactions/:id"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={['SUPER_ADMIN', 'ADMIN', 'MANAGER']}
                        >
                          <TransactionDetail />
                        </RoleProtectedRoute>
                      }
                    />
                    <Route
                      path="/accounting/financial-statements"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={[
                            'SUPER_ADMIN',
                            'ADMIN',
                            'MANAGER',
                            'USER',
                          ]}
                        >
                          <FinancialStatementPage />
                        </RoleProtectedRoute>
                      }
                    />
                    <Route
                      path="/accounting/reports"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={['SUPER_ADMIN', 'ADMIN', 'MANAGER']}
                        >
                          <FinancialReportListPage />
                        </RoleProtectedRoute>
                      }
                    />
                    <Route
                      path="/accounting/reports/:id"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={['SUPER_ADMIN', 'ADMIN', 'MANAGER']}
                        >
                          <FinancialReportDetailPage />
                        </RoleProtectedRoute>
                      }
                    />

                    {/* 회사관리 */}
                    <Route path="/companies" element={<CompanyList />} />
                    <Route path="/companies/:id" element={<CompanyDetail />} />

                    {/* 알림관리 */}
                    <Route
                      path="/notifications"
                      element={<NotificationListPage />}
                    />
                    <Route
                      path="/notifications/test"
                      element={<NotificationTestPage />}
                    />

                    {/* 사용자 프로필 */}
                    <Route path="/profile" element={<UserProfilePage />} />

                    {/* 관리자 기능 */}
                    <Route
                      path="/admin/users"
                      element={
                        <RoleProtectedRoute
                          requiredRoles={['SUPER_ADMIN', 'ADMIN']}
                        >
                          <UserManagementPage />
                        </RoleProtectedRoute>
                      }
                    />
                  </Routes>
                </Layout>
              </ProtectedRoute>
            }
          />
        </Routes>

        {/* React Query 개발자 도구 */}
        <ReactQueryDevtools initialIsOpen={false} />
      </NotificationProvider>
    </AuthProvider>
  )
}

export default App
