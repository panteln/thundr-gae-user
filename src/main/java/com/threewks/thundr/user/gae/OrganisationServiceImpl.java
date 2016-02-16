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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.threewks.thundr.gae.objectify.repository.AsyncResult;
import com.threewks.thundr.gae.objectify.repository.UuidRepository;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.user.OrganisationService;

public class OrganisationServiceImpl<O extends OrganisationGae, U extends UserGae> implements OrganisationService<O, U> {
	private UuidRepository<O> organisationRepository;
	private UserRepositoryGae<U> userRepositoryGae;

	public OrganisationServiceImpl(UserRepositoryGae<U> userRepositoryGae, SearchConfig searchConfig) {
		this(userRepositoryGae, (Class<O>) OrganisationGae.class, searchConfig);
	}

	public OrganisationServiceImpl(UserRepositoryGae<U> userRepositoryGae, Class<O> type, SearchConfig searchConfig) {
		this.organisationRepository = new UuidRepository<>(type, searchConfig);
		this.userRepositoryGae = userRepositoryGae;
	}

	@Override
	public O getOrganisation(UUID uuid) {
		return organisationRepository.get(uuid);
	}

	@Override
	public O put(O organisation) {
		return organisationRepository.put(organisation);
	}

	@Override
	public O delete(O organisation) {
		return organisationRepository.put(organisation);
	}

	@Override
	public List<O> listOrganisations() {
		return organisationRepository.list(200);
	}

	@Override
	public List<U> listUsers(O organisation) {
		Set<String> users = organisation.getUsernames();
		return userRepositoryGae.listUsers(users);
	}

	@Override
	public void addUser(O organisation, U user) {
		user.setOrganisation(organisation);
		organisation.addUsernames(Collections.singleton(user.getUsername()));
		putBoth(organisation, user);
	}

	@Override
	public void removeUser(O organisation, U user) {
		user.setOrganisation(null);
		organisation.removeUsernames(Collections.singleton(user.getUsername()));
		putBoth(organisation, user);
	}

	@Override
	public O getOrganisation(U user) {
		return (O) user.getOrganistion();
	}

	protected void putBoth(O organisation, U user) {
		AsyncResult<U> async1 = userRepositoryGae.putAsync(user);
		AsyncResult<O> async2 = organisationRepository.putAsync(organisation);
		async1.complete();
		async2.complete();
	}

}
