/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://3wks.github.io/thundr/
 * Copyright (C) 2014 3wks, <thundr@3wks.com.au>
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
import com.threewks.thundr.user.BaseUserService;
import com.threewks.thundr.user.UserTokenRepository;

public class UserServiceImpl extends BaseUserService<User> implements UserService {
	private UserRepositoryImpl<User> userRepositoryImpl;

	public UserServiceImpl(UserTokenRepository<User> tokenRepository, UserRepositoryImpl<User> userRepository) {
		super(tokenRepository, userRepository);
		this.userRepositoryImpl = userRepository;
	}

	@Override
	public User get(String username) {
		return userRepositoryImpl.load(username);
	}

	@Override
	public User put(User user) {
		return userRepositoryImpl.save(user).complete();
	}

	@Override
	public boolean delete(String username) {
		User user = get(username);
		if (user != null) {
			userRepositoryImpl.deleteByKey(username).complete();
		}
		return user != null;
	}

	@Override
	public List<User> search(String email, int limit) {
		Search<User, String> query = buildSearch(email, limit);
		return query.run().getResults();
	}

	Search<User, String> buildSearch(String email, int limit) {
		Search<User, String> query = userRepositoryImpl.search();

		if (StringUtils.isNotBlank(email)) {
			query = query.field(User.Fields.Email, Is.Is, email);
		}

		query = query.order(User.Fields.Email, true);
		query = query.limit(limit);
		return query;
	}
}
