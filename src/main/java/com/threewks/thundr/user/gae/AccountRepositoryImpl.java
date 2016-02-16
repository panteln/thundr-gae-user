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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.gae.objectify.repository.Repository;
import com.threewks.thundr.gae.objectify.repository.UuidRepository;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.user.AccountRepository;
import com.threewks.thundr.user.OrganisationAccountRepository;
import com.threewks.thundr.user.Roles;

public class AccountRepositoryImpl<A extends AccountGae, U extends UserGae, O extends OrganisationGae> implements AccountRepository<A, U>, OrganisationAccountRepository<A, O> {

	private UuidRepository<A> delegateRepository;
	private UserRepositoryGae<U> userRepository;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AccountRepositoryImpl(UserRepositoryGae<U> userRepository, SearchConfig searchConfig) {
		this((Class) AccountGae.class, userRepository, searchConfig);
	}

	public AccountRepositoryImpl(Class<A> entityType, UserRepositoryGae<U> userRepository, SearchConfig searchConfig) {
		this.delegateRepository = new UuidRepository<>(entityType, searchConfig);
		this.userRepository = userRepository;
	}

	@Override
	public A put(A account) {
		delegateRepository.put(account);
		return account;
	}

	/**
	 * This is not a transactional operation, if you require this to be transactional
	 * you need to remove the users in batches yourself, then delete the empty account
	 * - this probably should happen with sequential task queue jobs.
	 */
	@Override
	public A delete(A account) {
		List<String> usernames = account.getUsernames();
		List<U> users = userRepository.get(usernames);
		for (U user : users) {
			user.removeAccount(account);
		}
		// Order of these is important
		delegateRepository.delete(account);
		userRepository.put(users);
		return account;
	}

	@Override
	public Roles getRoles(A account, U user) {
		return user.getRoles(account);
	}

	@Override
	public Roles putRoles(A account, U user, Roles roles) {
		user.setRoles(account, roles);
		boolean updateAccount = !account.hasUser(user);
		account.addUser(user);
		put(user);
		if (updateAccount) {
			delegateRepository.put(account);
		}
		return roles;
	}

	@Override
	public A addUserToAccount(U user, A account) {
		account.addUser(user);
		user.addAccount(account);
		put(user);
		delegateRepository.put(account);
		return account;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<A> getAccounts(U user) {
		// TODO - Enhancement: Given the potential for writes across entity groups to fail,
		// we could validate the returned accounts have the username in their set of users
		// to ensure a consistent view.
		Collection<AccountGae> accounts = user.getAccounts();
		return (List<A>) Expressive.list(accounts).removeItems((AccountGae) null);
	}

	@Override
	public List<U> getUsers(A account) {
		List<String> usernames = account.getUsernames();
		return userRepository.get(usernames);
	}

	@Override
	public Map<U, Roles> getUserRoles(A account) {
		List<U> users = getUsers(account);
		Map<U, Roles> results = new LinkedHashMap<>();
		for (U user : users) {
			results.put(user, user.getRoles(account));
		}
		return results;
	}

	@Override
	public A get(UUID accountId) {
		return delegateRepository.get(accountId);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void removeUsersFromAccount(A account, List<U> users) {
		account.removeUsers((List) users);
		for (U user : users) {
			user.removeAccount(account);
		}
		// Order here is important
		delegateRepository.put(account);
		userRepository.put(users);
	}

	@Override
	public void removeUserFromAccounts(U user, List<A> accounts) {
		for (A account : accounts) {
			account.removeUser(user);
			user.removeAccount(account);
		}
		// Order is important
		delegateRepository.put(accounts);
		put(user);

	}

	@Override
	public List<A> list() {
		return delegateRepository.list(200);
	}

	@Override
	public List<A> list(O organisation) {
		return delegateRepository.getByField("organisation", organisation);
	}

	@Override
	public O getOrganisation(A account) {
		return (O) account.getOrganisation();
	}

	protected void put(U user) {
		((Repository<U, String>) userRepository).put(user);
	}
}
