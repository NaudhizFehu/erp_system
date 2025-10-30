# DatePicker 드롭다운 기능 구현 계획

## 문제 분석

`captionLayout="dropdown-buttons"` props는 전달했지만, `react-day-picker` v8의 드롭다운 기능이 작동하려면 Calendar 컴포넌트에 추가 CSS 클래스 정의가 필요합니다.

**현재 상태**:
- `EmployeeForm.tsx`에서 `captionLayout="dropdown-buttons"`, `fromYear`, `toYear` props 전달함
- `calendar.tsx` 컴포넌트에 드롭다운 관련 CSS 클래스가 누락됨
- 결과: 여전히 화살표로만 월 이동 가능

## 해결 방안

`frontend/src/components/ui/calendar.tsx`에 드롭다운 버튼 관련 CSS 클래스를 추가합니다.

### 수정 파일: `frontend/src/components/ui/calendar.tsx`

**기존 코드** (라인 19-51):
```tsx
classNames={{
  months: "flex flex-col sm:flex-row space-y-4 sm:space-x-4 sm:space-y-0",
  month: "space-y-4",
  caption: "flex justify-center pt-1 relative items-center",
  caption_label: "text-sm font-medium",
  nav: "space-x-1 flex items-center",
  // ... 기존 스타일들
}}
```

**수정 코드**:
```tsx
classNames={{
  months: "flex flex-col sm:flex-row space-y-4 sm:space-x-4 sm:space-y-0",
  month: "space-y-4",
  caption: "flex justify-center pt-1 relative items-center",
  caption_label: "text-sm font-medium",
  caption_dropdowns: "flex justify-center gap-1",
  dropdown_month: "relative inline-flex items-center rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2",
  dropdown_year: "relative inline-flex items-center rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2",
  dropdown_icon: "ml-1 h-4 w-4 opacity-50",
  nav: "space-x-1 flex items-center",
  // ... 기존 스타일들 유지
}}
```

### 추가할 클래스 설명

1. **`caption_dropdowns`**: 드롭다운 컨테이너
   - `flex justify-center gap-1`: 년도/월 드롭다운을 가운데 정렬하고 간격 추가

2. **`dropdown_month`**: 월 선택 드롭다운
   - 표준 Shadcn UI Select 스타일 적용
   - border, padding, focus ring 등

3. **`dropdown_year`**: 년도 선택 드롭다운  
   - 월 드롭다운과 동일한 스타일

4. **`dropdown_icon`**: 드롭다운 화살표 아이콘
   - 투명도 50%로 시각적 일관성 유지

## 전체 수정 코드

```tsx
classNames={{
  months: "flex flex-col sm:flex-row space-y-4 sm:space-x-4 sm:space-y-0",
  month: "space-y-4",
  caption: "flex justify-center pt-1 relative items-center",
  caption_label: "text-sm font-medium",
  caption_dropdowns: "flex justify-center gap-1",
  dropdown_month: "relative inline-flex items-center rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2",
  dropdown_year: "relative inline-flex items-center rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2",
  dropdown_icon: "ml-1 h-4 w-4 opacity-50",
  nav: "space-x-1 flex items-center",
  nav_button: cn(
    buttonVariants({ variant: "outline" }),
    "h-7 w-7 bg-transparent p-0 opacity-50 hover:opacity-100"
  ),
  nav_button_previous: "absolute left-1",
  nav_button_next: "absolute right-1",
  table: "w-full border-collapse space-y-1",
  head_row: "flex",
  head_cell:
    "text-muted-foreground rounded-md w-9 font-normal text-[0.8rem]",
  row: "flex w-full mt-2",
  cell: "h-9 w-9 text-center text-sm p-0 relative [&:has([aria-selected].day-range-end)]:rounded-r-md [&:has([aria-selected].day-outside)]:bg-accent/50 [&:has([aria-selected])]:bg-accent first:[&:has([aria-selected])]:rounded-l-md last:[&:has([aria-selected])]:rounded-r-md focus-within:relative focus-within:z-20",
  day: cn(
    buttonVariants({ variant: "ghost" }),
    "h-9 w-9 p-0 font-normal aria-selected:opacity-100"
  ),
  day_range_end: "day-range-end",
  day_selected:
    "bg-primary text-primary-foreground hover:bg-primary hover:text-primary-foreground focus:bg-primary focus:text-primary-foreground",
  day_today: "bg-accent text-accent-foreground",
  day_outside:
    "day-outside text-muted-foreground opacity-50 aria-selected:bg-accent/50 aria-selected:text-muted-foreground aria-selected:opacity-30",
  day_disabled: "text-muted-foreground opacity-50",
  day_range_middle:
    "aria-selected:bg-accent aria-selected:text-accent-foreground",
  day_hidden: "invisible",
  ...classNames,
}}
```

## 검증 방법

1. 프론트엔드 재시작
2. 직원 등록 팝업 열기
3. **생년월일** 필드 클릭
4. Calendar 상단에 **년도와 월 드롭다운**이 표시되는지 확인
5. 년도 드롭다운 클릭 → 1950년부터 현재까지 리스트 확인
6. 월 드롭다운 클릭 → 1월부터 12월까지 리스트 확인
7. **입사일** 필드에서도 동일하게 확인 (1990년~현재)
8. 날짜 선택하여 정상 동작 확인

## 예상 결과

- Calendar 상단에 [년도 ▼] [월 ▼] 드롭다운이 표시됨
- 클릭하면 선택 가능한 년도/월 목록이 펼쳐짐
- 빠르게 원하는 년도/월로 이동 가능
- 기존 화살표 버튼도 그대로 작동
