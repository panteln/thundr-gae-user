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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.threewks.thundr.user.AccountService;
import com.threewks.thundr.user.OrganisationAccountRepository;
import com.threewks.thundr.user.OrganisationAccountService;
import com.threewks.thundr.user.OrganisationService;
import com.threewks.thundr.user.Roles;

public class OrganisationAccountServiceImpl<O extends OrganisationGae, A extends AccountGae, U extends UserGae> implements OrganisationAccountService<O, A, U> {
	protected AccountService<A, U> accountService;
	protected OrganisationService<O, U> organisationService;
	protected OrganisationAccountRepository<A, O> organisationAccountRepository;

	public OrganisationAccountServiceImpl(
			AccountService<A, U> accountService, 
			OrganisationService<O, U> organisationService, 
			OrganisationAccountRepository<A, O> organisationAccountRepository) {
		super();
		this.accountService = accountService;
		this.organisationService = organisationService;
		this.organisationAccountRepository = organisationAccountRepository;
	}

	@Override
	public A getAccount(UUID uuid) {
		return accountService.getAccount(uuid);
	}

	@Override
	public A put(A account) {
		return accountService.put(account);
	}

	@Override
	public A delete(A account) {
		return accountService.delete(account);
	}

	@Override
	public List<A> listAccounts() {
		return accountService.listAccounts();
	}

	@Override
	public List<U> listUsers(A account) {
		return accountService.listUsers(account);
	}

	@Override
	public void addUser(A account, U users, Roles roles) {
		accountService.addUser(account, users, roles);
	}

	@Override
	public void removeUser(A account, U users) {
		accountService.removeUser(account, users);
	}

	@Override
	public Map<U, Roles> getUserRoles(A account) {
		return accountService.getUserRoles(account);
	}

	@Override
	public Map<A, Roles> getAccountRoles(U user) {
		return accountService.getAccountRoles(user);
	}

	@Override
	public Roles getUserAccountRoles(A account, U user) {
		return accountService.getUserAccountRoles(account, user);
	}

	@Override
	public O getOrganisation(UUID uuid) {
		return organisationService.getOrganisation(uuid);
	}

	@Override
	public O put(O organisation) {
		return organisationService.put(organisation);
	}

	@Override
	public O delete(O organisation) {
		return organisationService.delete(organisation);
	}

	@Override
	public List<O> listOrganisations() {
		return organisationService.listOrganisations();
	}

	@Override
	public List<U> listUsers(O organisation) {
		return organisationService.listUsers(organisation);
	}

	@Override
	public void addUser(O organisation, U user) {
		organisationService.addUser(organisation, user);
	}

	@Override
	public void removeUser(O organisation, U user) {
		organisationService.removeUser(organisation, user);
	}

	@Override
	public O getOrganisation(U user) {
		return organisationService.getOrganisation(user);
	}

	@Override
	public O getOrganisation(A account) {
		return organisationAccountRepository.getOrganisation(account);
	}

	@Override
	public List<A> listAccounts(O organisation) {
		return organisationAccountRepository.list(organisation);
	}

	@Override
	public void addAccount(O organisation, A account) {
		account.setOrganisation(organisation);
		accountService.put(account);
	}

	@Override
	public void removeAccount(O organisation, A account) {
		account.setOrganisation(null);
		accountService.put(account);
	}

}
