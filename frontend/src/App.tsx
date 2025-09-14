import { Routes, Route } from 'react-router-dom'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { AuthProvider } from '@/contexts/AuthContext'
import { ProtectedRoute } from '@/components/auth/ProtectedRoute'
import Layout from '@/components/layout/Layout'
import { LoginPage } from '@/pages/auth/LoginPage'
import Dashboard from '@/pages/Dashboard'
import { EmployeeManagement } from '@/pages/hr/EmployeeManagement'
import EmployeeDetail from '@/pages/hr/EmployeeDetail'
import DepartmentDetail from '@/pages/hr/DepartmentDetail'
import InventoryDashboardPage from '@/pages/inventory/InventoryDashboardPage'
import ProductManagementPage from '@/pages/inventory/ProductManagementPage'
import ProductDetail from '@/pages/inventory/ProductDetail'
import CustomerList from '@/pages/sales/CustomerList'
import CustomerDetail from '@/pages/sales/CustomerDetail'
import OrderDetail from '@/pages/sales/OrderDetail'
import AccountList from '@/pages/accounting/AccountList'
import CompanyList from '@/pages/company/CompanyList'
import CompanyDetail from '@/pages/company/CompanyDetail'

/**
 * 메인 애플리케이션 컴포넌트
 * 라우팅 및 전체 레이아웃을 관리합니다
 */
function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* 공개 라우트 */}
        <Route path="/login" element={<LoginPage />} />
        
        {/* 보호된 라우트 */}
        <Route path="/*" element={
          <ProtectedRoute>
            <Layout>
              <Routes>
                {/* 대시보드 */}
                <Route path="/" element={<Dashboard />} />
                
                {/* 인사관리 */}
                <Route path="/hr/employees" element={<EmployeeManagement />} />
                <Route path="/hr/employees/:id" element={<EmployeeDetail />} />
                <Route path="/hr/departments/:id" element={<DepartmentDetail />} />
                
                {/* 재고관리 */}
                <Route path="/inventory" element={<InventoryDashboardPage companyId={1} />} />
                <Route path="/inventory/dashboard" element={<InventoryDashboardPage companyId={1} />} />
                <Route path="/inventory/products" element={<ProductManagementPage companyId={1} />} />
                <Route path="/inventory/products/:id" element={<ProductDetail />} />
                
                {/* 영업관리 */}
                <Route path="/sales/customers" element={<CustomerList />} />
                <Route path="/sales/customers/:id" element={<CustomerDetail />} />
                <Route path="/sales/orders/:id" element={<OrderDetail />} />
                
                {/* 회계관리 */}
                <Route path="/accounting/accounts" element={<AccountList />} />
                
                {/* 회사관리 */}
                <Route path="/companies" element={<CompanyList />} />
                <Route path="/companies/:id" element={<CompanyDetail />} />
              </Routes>
            </Layout>
          </ProtectedRoute>
        } />
      </Routes>
      
      {/* React Query 개발자 도구 */}
      <ReactQueryDevtools initialIsOpen={false} />
    </AuthProvider>
  )
}

export default App

