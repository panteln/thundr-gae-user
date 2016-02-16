/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://3wks.github.io/thundr/
 * Copyright (C) 2015 3wks, <thundr@3wks.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.threewks.thundr.user.gae;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.threewks.thundr.user.AccountRepository;
import com.threewks.thundr.user.AccountService;
import com.threewks.thundr.user.Roles;
import com.threewks.thundr.user.UserRepository;

public class AccountServiceImpl<A extends AccountGae, U extends UserGae> implements AccountService<A, U> {
	private AccountRepository<A, U> accountRepository;
	private UserRepository<U> userRepository;

	public AccountServiceImpl(AccountRepository<A, U> accountRepository, UserRepository<U> userRepository) {
		super();
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}

	@Override
	public A getAccount(UUID uuid) {
		return accountRepository.get(uuid);
	}

	@Override
	public A put(A account) {
		return accountRepository.put(account);
	}

	@Override
	public A delete(A account) {
		return accountRepository.delete(account);
	}

	@Override
	public List<U> listUsers(A account) {
		return userRepository.listUsers(account.getUsernames());
	}

	@Override
	public void addUser(A account, U user, Roles roles) {
		user.setRoles(account, roles);
		account.addUser(user);
		putBoth(account, user);
	}

	@Override
	public void removeUser(A account, U user) {
		user.removeAccount(account);
		account.removeUser(user);
		putBoth(account, user);
	}

	@Override
	public Map<U, Roles> getUserRoles(A account) {
		List<String> usernames = account.getUsernames();
		List<U> users = userRepository.listUsers(usernames);
		Map<U, Roles> result = new LinkedHashMap<>();
		for(U user : users){
			result.put(user, user.getRoles(account));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<A, Roles> getAccountRoles(U user) {
		return (Map<A, Roles>) user.getAllRoles();
	}

	@Override
	public Roles getUserAccountRoles(A account, U user) {
		return user.getRoles(account);
	}

	@Override
	public List<A> listAccounts() {
		return accountRepository.list();
	}

	protected void putBoth(A account, U user) {
		accountRepository.put(account);
		userRepository.put(user);
	}
}
