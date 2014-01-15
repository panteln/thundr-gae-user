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

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.threewks.thundr.user.UserTokenRepository;

public class UserTokenRepositoryImpl<U extends User> implements UserTokenRepository<U> {

	@Override
	public String createToken(U user) {
		final UserToken token = new UserToken(user);
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				ofy().save().entity(token).now();
			}
		});
		return token.getKey().getString();
	}

	@Override
	public void expireTokens(U user) {
		List<UserToken> tokens = ofy().load().type(UserToken.class).ancestor(user).list();
		ofy().delete().entities(tokens).now();
	}

	@SuppressWarnings("unchecked")
	@Override
	public U getUserForToken(String token) {
		Key<UserToken> key = Key.create(token);
		UserToken userToken = ofy().load().key(key).now();
		return (U) (userToken == null ? null : userToken.getUser());
	}

}
