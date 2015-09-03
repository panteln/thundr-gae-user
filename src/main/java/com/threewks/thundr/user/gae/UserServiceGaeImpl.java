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

import com.threewks.thundr.search.Is;
import com.threewks.thundr.search.Search;
import com.threewks.thundr.session.SessionService;
import com.threewks.thundr.user.UserServiceImpl;
import com.threewks.thundr.user.User;

public class UserServiceGaeImpl extends UserServiceImpl<UserGae> implements UserServiceGae {
	protected UserRepositoryGae<UserGae> userRepositoryGae;

	public UserServiceGaeImpl(UserRepositoryGae<UserGae> userRepository, SessionService sessionService) {
		super(userRepository, sessionService);
		this.userRepositoryGae = userRepository;
	}

	@Override
	public UserGae get(String username) {
		return userRepositoryGae.load(username);
	}

	@Override
	public UserGae put(UserGae user) {
		return userRepositoryGae.save(user).complete();
	}

	@Override
	public boolean delete(String username) {
		User user = get(username);
		if (user != null) {
			userRepositoryGae.deleteByKey(username).complete();
		}
		return user != null;
	}

	@Override
	public List<UserGae> search(String email, int limit) {
		Search<UserGae, String> query = buildSearch(email, limit);
		return query.run().getResults();
	}

	Search<UserGae, String> buildSearch(String email, int limit) {
		Search<UserGae, String> query = userRepositoryGae.search();

		if (StringUtils.isNotBlank(email)) {
			query = query.field(UserGae.Fields.Email, Is.Is, email);
		}

		query = query.order(UserGae.Fields.Email, true);
		query = query.limit(limit);
		return query;
	}
}
