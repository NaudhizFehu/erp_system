package com.erp.accounting.service;

import com.erp.accounting.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 계정과목 서비스
 */
public interface AccountService {

    Page<AccountDto> getAccounts(Pageable pageable);

    AccountDto getAccount(Long id);

    AccountDto createAccount(AccountCreateDto dto);

    AccountDto updateAccount(Long id, AccountUpdateDto dto);

    void deleteAccount(Long id);

    List<AccountTreeNodeDto> getAccountTree(Long companyId);

    boolean isAccountCodeExists(String accountCode, Long companyId, Long excludeId);
}




