/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
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

import static com.atomicleopard.expressive.Expressive.list;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.atomicleopard.expressive.EList;
import com.atomicleopard.expressive.Expressive;
import com.googlecode.objectify.ObjectifyFactory;
import com.threewks.thundr.search.google.SearchRequest;
import com.threewks.thundr.search.google.SearchService;
import com.threewks.thundr.user.BaseUserService;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserTokenRepository;
import com.threewks.thundr.user.gae.User.Fields;

public class UserServiceImpl extends BaseUserService<User> implements UserService {
	private SearchService searchService;

	public UserServiceImpl(SearchService searchService, UserTokenRepository<User> tokenRepository, UserRepository<User> userRepository) {
		super(tokenRepository, userRepository);
		this.searchService = searchService;
	}

	@Override
	public User get(String username) {
		return ofy().load().type(User.class).filter(Fields.Username, username).first().now();
	}

	@Override
	public User put(User user) {
		ofy().save().entity(user).now();
		searchService.index(user, user.getUsername(), user.getFieldsToIndex()).complete();
		return user;
	}

	@Override
	public boolean delete(String username) {
		User user = get(username);
		if (user != null) {
			ofy().delete().entity(user).now();
			searchService.remove(User.class, list(username));
			
		}
		return user != null;
	}

	/*
	 * @Override
	 * public User getExistingUser(Class<? extends AuthenticationStrategy> type, String value) {
	 * AuthenticationStrategy authenticationStrategy = authenticationStrategies.get(type);
	 * if (authenticationStrategy == null) {
	 * throw new UserServiceException("Unable to find user - the %s %s is not registered", AuthenticationStrategy.class.getSimpleName(), type.getSimpleName());
	 * }
	 * String lookupKey = authenticationStrategy.getLookupKey();
	 * User user = ofy().load().type(User.class).filter(lookupKey, value).first().now();
	 * return user;
	 * }
	 */

	@Override
	public List<User> search(String email, int limit) {
		SearchRequest<User> query = searchService.search(User.class);

		if (StringUtils.isNotBlank(email)) {
			query.field(User.Fields.Email).is(email);
		}
		query.order(User.Fields.Email).ascending();
		query.limit(limit);

		EList<String> ids = query.search().getSearchResultIds();
		return load(ids);
	}

	private List<User> load(EList<String> ids) {
		Map<String, User> results = ofy().load().type(User.class).ids(ids);
		return Expressive.Transformers.transformAllUsing(Expressive.Transformers.usingLookup(results)).from(ids);
	}

	public static void registerObjectifyClasses(ObjectifyFactory objectifyFactory) {
		objectifyFactory.register(User.class);
		objectifyFactory.register(UserToken.class);
	}
}
