import * as React from "react"
import { DayPicker } from "react-day-picker"
import { cn } from "@/lib/utils"
import { buttonVariants } from "@/components/ui/button"

export type CalendarProps = React.ComponentProps<typeof DayPicker>

// 커스텀 Caption Props 타입
interface CustomCaptionProps {
  displayMonth: Date
  onMonthChange?: (month: Date) => void
  fromYear?: number
  toYear?: number
}

// 커스텀 Caption 컴포넌트 - 화살표 없이 드롭다운만 표시
function CustomCaption({ displayMonth, onMonthChange, fromYear = 1950, toYear = new Date().getFullYear() }: CustomCaptionProps) {
  const currentYear = displayMonth.getFullYear()
  const currentMonth = displayMonth.getMonth()

  const years = []
  for (let year = toYear; year >= fromYear; year--) {
    years.push(year)
  }

  // 월 배열 (1-12)
  const months = Array.from({ length: 12 }, (_, i) => i)

  const handleYearChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newDate = new Date(displayMonth)
    newDate.setFullYear(Number(e.target.value))
    onMonthChange?.(newDate)
  }

  const handleMonthChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newDate = new Date(displayMonth)
    newDate.setMonth(Number(e.target.value))
    onMonthChange?.(newDate)
  }

  return (
    <div className="flex justify-center gap-2 mb-4">
      {/* 년도 드롭다운 */}
      <select
        value={currentYear}
        onChange={handleYearChange}
        className="inline-flex items-center justify-center rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background transition-colors hover:bg-accent hover:text-accent-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
      >
        {years.map((year) => (
          <option key={year} value={year}>
            {year}
          </option>
        ))}
      </select>

      {/* 월 드롭다운 */}
      <select
        value={currentMonth}
        onChange={handleMonthChange}
        className="inline-flex items-center justify-center rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background transition-colors hover:bg-accent hover:text-accent-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
      >
        {months.map((month) => (
          <option key={month} value={month}>
            {month + 1}
          </option>
        ))}
      </select>
    </div>
  )
}

function Calendar({
  className,
  classNames,
  showOutsideDays = true,
  fromYear,
  toYear,
  ...props
}: CalendarProps) {
  // 내부 상태로 현재 표시 중인 월 관리
  const [month, setMonth] = React.useState<Date>(props.selected as Date || new Date())

  // selected가 변경되면 month도 업데이트
  React.useEffect(() => {
    if (props.selected) {
      setMonth(props.selected as Date)
    }
  }, [props.selected])

  return (
    <DayPicker
      showOutsideDays={showOutsideDays}
      className={cn("p-3", className)}
      month={month}
      onMonthChange={setMonth}
      classNames={{
        months: "flex flex-col sm:flex-row space-y-4 sm:space-x-4 sm:space-y-0",
        month: "space-y-4",
        caption: "flex justify-center pt-1 relative items-center",
        caption_label: "hidden", // 기본 라벨 숨김
        nav: "hidden", // 화살표 네비게이션 완전 숨김
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
      components={{
        Caption: (captionProps) => (
          <CustomCaption
            {...captionProps}
            fromYear={fromYear}
            toYear={toYear}
            onMonthChange={setMonth}
          />
        ),
      }}
      {...props}
    />
  )
}
Calendar.displayName = "Calendar"

export { Calendar }



