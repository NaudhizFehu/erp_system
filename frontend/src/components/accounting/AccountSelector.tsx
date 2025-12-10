/**
 * 계정과목 선택 컴포넌트
 * 복식부기 전표 입력 시 사용하는 계정과목 선택 드롭다운
 * leaf account (최하위 계정)만 선택 가능
 */

import { Check, ChevronsUpDown } from 'lucide-react'
import { useMemo, useState } from 'react'

import { Button } from '@/components/ui/button'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
} from '@/components/ui/command'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { useAccountTree } from '@/hooks/useAccounts'
import { cn } from '@/lib/utils'
import { AccountTreeNode } from '@/types/accounting'

interface AccountSelectorProps {
  value: number | null
  onChange: (accountId: number, account: AccountTreeNode) => void
  companyId?: number
  error?: string
  disabled?: boolean
}

/**
 * 계정과목 트리를 평탄화하여 모든 계정을 배열로 변환
 */
function flattenAccountTree(accounts: AccountTreeNode[]): AccountTreeNode[] {
  const result: AccountTreeNode[] = []

  function traverse(account: AccountTreeNode) {
    result.push(account)
    if (account.children && account.children.length > 0) {
      account.children.forEach(traverse)
    }
  }

  accounts.forEach(traverse)
  return result
}

export function AccountSelector({
  value,
  onChange,
  companyId,
  error,
  disabled = false,
}: AccountSelectorProps) {
  const [open, setOpen] = useState(false)

  // 계정과목 트리 조회
  const { data: accountTree, isLoading } = useAccountTree(companyId)

  // leaf account만 필터링 (최하위 계정만 선택 가능)
  const leafAccounts = useMemo(() => {
    if (!accountTree) return []
    const allAccounts = flattenAccountTree(accountTree)
    return allAccounts.filter((acc) => acc.isLeafAccount)
  }, [accountTree])

  // 현재 선택된 계정 찾기
  const selectedAccount = leafAccounts.find((acc) => acc.id === value)

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className={cn(
            'w-full justify-between',
            !value && 'text-muted-foreground',
            error && 'border-red-500'
          )}
          disabled={disabled || isLoading}
        >
          {isLoading ? (
            '로딩 중...'
          ) : selectedAccount ? (
            <span className="flex items-center gap-2">
              <span className="text-xs text-muted-foreground">
                {selectedAccount.accountCode}
              </span>
              <span>{selectedAccount.name || selectedAccount.accountName}</span>
            </span>
          ) : (
            '계정과목 선택'
          )}
          <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[400px] p-0">
        <Command>
          <CommandInput placeholder="계정과목 검색..." />
          <CommandEmpty>계정과목을 찾을 수 없습니다.</CommandEmpty>
          <CommandGroup className="max-h-[300px] overflow-auto">
            {leafAccounts.map((account) => (
              <CommandItem
                key={account.id}
                value={`${account.accountCode} ${account.name || account.accountName}`}
                onSelect={() => {
                  onChange(account.id, account)
                  setOpen(false)
                }}
              >
                <Check
                  className={cn(
                    'mr-2 h-4 w-4',
                    value === account.id ? 'opacity-100' : 'opacity-0'
                  )}
                />
                <div className="flex flex-col">
                  <div className="flex items-center gap-2">
                    <span className="text-xs text-muted-foreground">
                      {account.accountCode}
                    </span>
                    <span>{account.name || account.accountName}</span>
                  </div>
                  {account.description && (
                    <span className="text-xs text-muted-foreground">
                      {account.description}
                    </span>
                  )}
                </div>
              </CommandItem>
            ))}
          </CommandGroup>
        </Command>
      </PopoverContent>
    </Popover>
  )
}
