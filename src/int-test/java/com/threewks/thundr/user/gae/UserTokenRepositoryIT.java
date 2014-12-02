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
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;

public class UserTokenRepositoryIT {
	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(User.class, UserToken.class);

	private UserTokenRepositoryImpl<User> userTokenRepository;

	@Before
	public void before() {
		userTokenRepository = new UserTokenRepositoryImpl<User>();
	}

	@Test
	public void shouldCreateTokenForUser() {
		User user = new User("username");
		ofy().save().entity(user).now();

		String token = userTokenRepository.createToken(user);
		assertThat(token, is(notNullValue()));

		User matchedUser = userTokenRepository.getUserForToken(token);
		assertThat(matchedUser, is(user));
	}

	@Test
	public void shouldReturnNullIfGivenTokenDoesntMatchAnything() {
		assertThat(userTokenRepository.getUserForToken(null), is(nullValue()));
		assertThat(userTokenRepository.getUserForToken("whatever"), is(nullValue()));
	}

	@Test
	public void shouldExpireToken() {
		User user = new User("username");
		ofy().save().entity(user).now();

		String token = userTokenRepository.createToken(user);
		assertThat(userTokenRepository.getUserForToken(token), is(user));

		userTokenRepository.expireToken(user, token);
		assertThat(userTokenRepository.getUserForToken(token), is(nullValue()));
	}

	@Test
	public void shouldExpireAllTokensForUser() {
		User user = new User("username");
		ofy().save().entity(user).now();

		String token1 = userTokenRepository.createToken(user);
		String token2 = userTokenRepository.createToken(user);
		String token3 = userTokenRepository.createToken(user);
		assertThat(userTokenRepository.getUserForToken(token1), is(user));
		assertThat(userTokenRepository.getUserForToken(token2), is(user));
		assertThat(userTokenRepository.getUserForToken(token3), is(user));

		userTokenRepository.expireTokens(user);
		assertThat(userTokenRepository.getUserForToken(token1), is(nullValue()));
		assertThat(userTokenRepository.getUserForToken(token2), is(nullValue()));
		assertThat(userTokenRepository.getUserForToken(token3), is(nullValue()));
	}
}
