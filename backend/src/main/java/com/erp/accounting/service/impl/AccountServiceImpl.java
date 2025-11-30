package com.erp.accounting.service.impl;

import com.erp.accounting.dto.*;
import com.erp.accounting.entity.Account;
import com.erp.accounting.repository.AccountRepository;
import com.erp.accounting.service.AccountService;
import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AccountDto> getAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(AccountDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("계정과목을 찾을 수 없습니다"));
        return AccountDto.from(account);
    }

    @Override
    @Transactional
    public AccountDto createAccount(AccountCreateDto dto) {
        if (isAccountCodeExists(dto.accountCode(), dto.companyId(), null)) {
            throw new IllegalArgumentException("이미 존재하는 계정 코드입니다");
        }

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new EntityNotFoundException("회사를 찾을 수 없습니다"));

        Account account = new Account();
        account.setCompany(company);
        account.setAccountCode(dto.accountCode());
        account.setName(dto.accountName());
        account.setNameEn(dto.accountNameEn());
        account.setDescription(dto.description());
        account.setAccountType(dto.accountType());
        account.setAccountCategory(dto.accountCategory());
        account.setDebitCreditType(dto.debitCreditType());
        if (dto.parentAccountId() != null) {
            Account parent = accountRepository.findById(dto.parentAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("상위 계정을 찾을 수 없습니다"));
            account.setParentAccount(parent);
        }
        account.setAccountLevel(dto.accountLevel());
        account.setSortOrder(dto.sortOrder());
        account.setIsActive(dto.isActive());
        account.setTrackBalance(dto.trackBalance());
        account.setOpeningBalance(dto.openingBalance());
        account.setBudgetAmount(dto.budgetAmount());
        account.setTaxCode(dto.taxCode());
        account.setControlField1(dto.controlField1());
        account.setControlField2(dto.controlField2());

        Account saved = accountRepository.save(account);
        return AccountDto.from(saved);
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Long id, AccountUpdateDto dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("계정과목을 찾을 수 없습니다"));

        account.setName(dto.accountName());
        account.setNameEn(dto.accountNameEn());
        account.setDescription(dto.description());
        account.setAccountType(dto.accountType());
        account.setAccountCategory(dto.accountCategory());
        account.setDebitCreditType(dto.debitCreditType());
        if (dto.parentAccountId() != null) {
            Account parent = accountRepository.findById(dto.parentAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("상위 계정을 찾을 수 없습니다"));
            account.setParentAccount(parent);
        } else {
            account.setParentAccount(null);
        }
        account.setAccountLevel(dto.accountLevel());
        account.setSortOrder(dto.sortOrder());
        account.setIsActive(dto.isActive());
        account.setTrackBalance(dto.trackBalance());
        account.setOpeningBalance(dto.openingBalance());
        account.setBudgetAmount(dto.budgetAmount());
        account.setTaxCode(dto.taxCode());
        account.setControlField1(dto.controlField1());
        account.setControlField2(dto.controlField2());

        return AccountDto.from(account);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("계정과목을 찾을 수 없습니다"));
        accountRepository.delete(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountTreeNodeDto> getAccountTree(Long companyId) {
        List<Account> accounts = accountRepository.findByCompanyId(companyId);

        Map<Long, AccountTreeNodeDto> nodeMap = new HashMap<>();
        Map<Long, List<AccountTreeNodeDto>> childrenMap = new HashMap<>();

        for (Account a : accounts) {
            AccountTreeNodeDto node = new AccountTreeNodeDto(
                    a.getId(),
                    a.getAccountCode(),
                    a.getName(),
                    a.getAccountLevel(),
                    a.getIsActive(),
                    new ArrayList<>()
            );
            nodeMap.put(a.getId(), node);
            if (a.getParentAccount() != null) {
                childrenMap.computeIfAbsent(a.getParentAccount().getId(), k -> new ArrayList<>()).add(node);
            }
        }

        // 루트 노드 수집 및 children 연결
        List<AccountTreeNodeDto> roots = new ArrayList<>();
        for (Account a : accounts) {
            AccountTreeNodeDto node = nodeMap.get(a.getId());
            List<AccountTreeNodeDto> children = childrenMap.get(a.getId());
            if (children != null && !children.isEmpty()) {
                node = new AccountTreeNodeDto(
                        node.id(), node.accountCode(), node.accountName(), node.accountLevel(), node.isActive(),
                        children.stream()
                                .sorted(Comparator.comparing(AccountTreeNodeDto::accountCode))
                                .collect(Collectors.toList())
                );
                nodeMap.put(a.getId(), node);
            }
            if (a.getParentAccount() == null) {
                roots.add(node);
            }
        }

        return roots.stream()
                .sorted(Comparator.comparing(AccountTreeNodeDto::accountCode))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountCodeExists(String accountCode, Long companyId, Long excludeId) {
        if (companyId != null) {
            return excludeId == null
                    ? accountRepository.findByCompanyIdAndAccountCode(companyId, accountCode).isPresent()
                    : accountRepository.findByCompanyIdAndAccountCode(companyId, accountCode)
                        .filter(a -> !a.getId().equals(excludeId)).isPresent();
        }
        return accountRepository.findByAccountCode(accountCode).isPresent();
    }
}




