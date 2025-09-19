import { ReactNode } from 'react'
import { Sidebar } from './Sidebar'
import { Header } from './Header'

interface LayoutProps {
  children: ReactNode
}

/**
 * 메인 레이아웃 컴포넌트
 * 사이드바, 헤더, 메인 콘텐츠 영역을 포함합니다
 */
function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen bg-background">
      {/* 사이드바 */}
      <Sidebar />
      
      {/* 메인 콘텐츠 영역 */}
      <div className="pl-64">
        {/* 헤더 */}
        <Header />
        
        {/* 페이지 콘텐츠 */}
        <main className="p-6">
          {children}
        </main>
      </div>
    </div>
  )
}

export { Layout }





