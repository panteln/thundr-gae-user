package com.threewks.thundr.user.gae;

import com.threewks.thundr.user.AccountRepository;
import com.threewks.thundr.user.BaseAccountService;

public class AccountServiceImpl extends BaseAccountService<Account, User> {
	public AccountServiceImpl(AccountRepository<Account, User> accountRepository) {
		super(accountRepository);
	}
}
