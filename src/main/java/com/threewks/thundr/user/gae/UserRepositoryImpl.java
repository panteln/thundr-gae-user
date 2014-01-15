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

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;

import com.atomicleopard.expressive.Cast;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserServiceException;
import com.threewks.thundr.user.authentication.Authentication;
import com.threewks.thundr.user.gae.authentication.BaseAuthentication;

public class UserRepositoryImpl<U extends User> implements UserRepository<U> {

	@Override
	public void putAuthentication(U user, Authentication authentication) {
		user.addAuthentication(authentication);
		update(user);
	}

	@Override
	public void removeAuthentication(Authentication authentication) {
		U user = get(authentication);
		if (user != null) {
			user.removeAuthentication(authentication);
		}
		update(user);
	}

	@Override
	public Collection<Authentication> getAuthentications(U user) {
		return user.getAuthentications();
	}

	@Override
	public void removeAuthentications(U user) {
		user.getAuthentications().clear();
		update(user);
	}

	@Override
	public void update(U user) {
		ofy().save().entity(user).now();
	}

	@SuppressWarnings("unchecked")
	@Override
	public U get(Authentication authentication) {
		BaseAuthentication baseAuthentication = Cast.as(authentication, BaseAuthentication.class);
		if (baseAuthentication == null) {
			throw new UserServiceException("Unable to find a matching authentication for %s, it must be a %s to be found in the datastore", authentication, BaseAuthentication.class.getSimpleName());
		}
		return ((U) baseAuthentication.getUser(ofy()));
	}

	@Override
	public Authentication getAuthentication(Authentication authentication) {
		U user = get(authentication);
		return user.getMatchingAuthentication(authentication);
	}
}
