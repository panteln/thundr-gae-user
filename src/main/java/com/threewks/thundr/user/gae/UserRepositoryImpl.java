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

import com.atomicleopard.expressive.Cast;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserServiceException;
import com.threewks.thundr.user.authentication.Authentication;
import com.threewks.thundr.user.gae.authentication.ObjectifyAuthentication;

public class UserRepositoryImpl<U extends User> implements UserRepository<U> {

	@Override
	public void putAuthentication(U user, Authentication authentication) {
		ObjectifyAuthentication baseAuthentication = baseAuthentication(authentication);
		baseAuthentication.setUser(user);
		update(user);
		ofy().save().entities(baseAuthentication).now();
	}

	@Override
	public void removeAuthentication(Authentication authentication) {
		ofy().delete().entities(authentication);
	}

	/*
	 * @Override
	 * public Collection<Authentication> getAuthentications(U user) {
	 * }
	 * 
	 * @Override
	 * public void removeAuthentications(U user) {
	 * }
	 */

	@Override
	public void update(U user) {
		ofy().save().entity(user).now();
	}

	@SuppressWarnings("unchecked")
	@Override
	public U get(Authentication authentication) {
		ObjectifyAuthentication baseAuthentication = baseAuthentication(authentication);
		return ((U) baseAuthentication.getUser(ofy()));
	}

	@Override
	public Authentication getAuthentication(Authentication authentication) {
		return baseAuthentication(authentication).getMatchingAuthentication(ofy(), authentication);
	}

	private ObjectifyAuthentication baseAuthentication(Authentication authentication) {
		ObjectifyAuthentication baseAuthentication = Cast.as(authentication, ObjectifyAuthentication.class);
		if (baseAuthentication == null) {
			throw new UserServiceException("Unable to work with authentication %s, it must be a %s to be stored/found in the datastore", authentication, ObjectifyAuthentication.class.getSimpleName());
		}
		return baseAuthentication;
	}
}