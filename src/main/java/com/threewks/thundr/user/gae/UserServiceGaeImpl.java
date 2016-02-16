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

import org.apache.commons.lang3.StringUtils;

import com.threewks.thundr.gae.objectify.repository.Repository;
import com.threewks.thundr.search.Is;
import com.threewks.thundr.search.Search;
import com.threewks.thundr.session.SessionService;
import com.threewks.thundr.user.User;
import com.threewks.thundr.user.UserServiceImpl;

public class UserServiceGaeImpl<U extends UserGae, S extends SessionGae> extends UserServiceImpl<U, S> implements UserServiceGae<U, S> {
	protected UserRepositoryGae<U> userRepositoryGae;

	public UserServiceGaeImpl(UserRepositoryGae<U> userRepository, SessionService<S> sessionService) {
		super(userRepository, sessionService);
		this.userRepositoryGae = userRepository;
	}

	@Override
	public U get(String username) {
		return userRepositoryGae.get(username);
	}

	@Override
	public U put(U user) {
		return ((Repository<U, String>)userRepositoryGae).put(user);
	}

	@Override
	public boolean delete(String username) {
		User user = get(username);
		if (user != null) {
			userRepositoryGae.deleteByKey(username);
		}
		return user != null;
	}

	@Override
	public List<U> search(String email, int limit) {
		Search<U, String> query = buildSearch(email, limit);
		return query.run().getResults();
	}

	Search<U, String> buildSearch(String email, int limit) {
		Search<U, String> query = userRepositoryGae.search();

		if (StringUtils.isNotBlank(email)) {
			query = query.field(UserGae.Fields.Email, Is.Is, email);
		}

		query = query.order(UserGae.Fields.Email, true);
		query = query.limit(limit);
		return query;
	}
}