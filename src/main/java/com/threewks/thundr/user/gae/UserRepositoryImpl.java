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

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.atomicleopard.expressive.Cast;
import com.googlecode.objectify.ObjectifyFactory;
import com.threewks.thundr.gae.objectify.repository.Repository;
import com.threewks.thundr.gae.objectify.repository.StringRepository;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserServiceException;
import com.threewks.thundr.user.authentication.Authentication;
import com.threewks.thundr.user.gae.authentication.AuthenticationContextGae;
import com.threewks.thundr.user.gae.authentication.OAuthAuthentication;
import com.threewks.thundr.user.gae.authentication.ObjectifyAuthentication;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;

public class UserRepositoryImpl<U extends UserGae> extends StringRepository<U> implements UserRepository<U>, Repository<U, String> {

	public UserRepositoryImpl(Class<U> entityType, SearchConfig searchConfig) {
		super(entityType, searchConfig);
	}

	@SuppressWarnings("unchecked")
	@Override
	public U putAuthentication(U user, Authentication authentication) {
		ObjectifyAuthentication<?> objectifyAuthentication = objectifyAuthentication(authentication);
		objectifyAuthentication.setUser(user);
		update(user);
		ofy().save().entities(objectifyAuthentication).now();
		return user;
	}

	@Override
	public void removeAuthentication(Authentication authentication) {
		ofy().delete().entities(authentication).now();
	}

	@Override
	public void update(U user) {
		ofy().save().entity(user).now();
	}

	@Override
	public List<U> list(List<String> usernames) {
		return super.load(usernames);
	}

	@SuppressWarnings("unchecked")
	@Override
	public U get(Authentication authentication) {
		ObjectifyAuthentication<?> objectifyAuthentication = objectifyAuthentication(authentication);
		return ((U) objectifyAuthentication.getUser(ofy()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Authentication getAuthentication(Authentication authentication) {
		ObjectifyAuthentication objectifyAuthentication = objectifyAuthentication(authentication);
		return objectifyAuthentication.getMatchingAuthentication(ofy(), objectifyAuthentication);
	}

	private ObjectifyAuthentication<?> objectifyAuthentication(Authentication authentication) {
		ObjectifyAuthentication<?> objectifyAuthentication = Cast.as(authentication, ObjectifyAuthentication.class);
		if (objectifyAuthentication == null) {
			throw new UserServiceException("Unable to work with authentication %s, it must be a %s to be stored/found in the datastore", authentication, ObjectifyAuthentication.class.getSimpleName());
		}
		return objectifyAuthentication;
	}

	public static void registerObjectifyClasses(ObjectifyFactory objectifyFactory) {
		objectifyFactory.register(UserGae.class);
		objectifyFactory.register(AuthenticationContextGae.class);
		objectifyFactory.register(SessionId.class);
		objectifyFactory.register(SessionGae.class);
		objectifyFactory.register(PasswordAuthentication.class);
		objectifyFactory.register(OAuthAuthentication.class);
	}
}
