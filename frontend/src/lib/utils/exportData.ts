/**
 * 데이터 내보내기 유틸리티
 * Excel, CSV 형식으로 데이터를 내보내는 기능 제공
 */

/**
 * CSV 형식으로 데이터 내보내기
 */
export function exportToCSV<T extends Record<string, any>>(
  data: T[],
  filename: string,
  headers: { key: keyof T; label: string }[]
) {
  // CSV 헤더 생성
  const csvHeaders = headers.map(h => h.label).join(',')

  // CSV 행 생성
  const csvRows = data.map(row => {
    return headers
      .map(h => {
        const value = row[h.key]
        // 값에 쉼표나 줄바꿈이 있으면 따옴표로 감싸기
        if (value === null || value === undefined || value === '') {
          return ''
        }
        const stringValue = String(value)
        if (
          stringValue.includes(',') ||
          stringValue.includes('\n') ||
          stringValue.includes('"')
        ) {
          return `"${stringValue.replace(/"/g, '""')}"`
        }
        return stringValue
      })
      .join(',')
  })

  // UTF-8 BOM 추가 (Excel 한글 인코딩)
  const BOM = '\uFEFF'
  const csvContent = BOM + csvHeaders + '\n' + csvRows.join('\n')

  // Blob 생성 및 다운로드
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)

  link.setAttribute('href', url)
  link.setAttribute('download', `${filename}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/**
 * Excel 형식으로 데이터 내보내기 (CSV 기반)
 * 실제 Excel 파일 생성을 위해서는 xlsx 라이브러리 필요
 * 현재는 CSV를 .xls 확장자로 저장 (Excel에서 호환)
 */
export function exportToExcel<T extends Record<string, any>>(
  data: T[],
  filename: string,
  headers: { key: keyof T; label: string }[]
) {
  // CSV 헤더 생성
  const csvHeaders = headers.map(h => h.label).join('\t')

  // CSV 행 생성 (탭으로 구분)
  const csvRows = data.map(row => {
    return headers
      .map(h => {
        const value = row[h.key]
        if (value === null || value === undefined || value === '') {
          return ''
        }
        return String(value)
      })
      .join('\t')
  })

  // UTF-8 BOM 추가
  const BOM = '\uFEFF'
  const csvContent = BOM + csvHeaders + '\n' + csvRows.join('\n')

  // Excel 호환 형식으로 다운로드
  const blob = new Blob([csvContent], {
    type: 'application/vnd.ms-excel;charset=utf-8;',
  })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)

  link.setAttribute('href', url)
  link.setAttribute('download', `${filename}.xls`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/**
 * 현재 날짜시간으로 파일명 생성
 */
export function generateFilename(prefix: string): string {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')

  return `${prefix}_${year}${month}${day}_${hours}${minutes}${seconds}`
}
