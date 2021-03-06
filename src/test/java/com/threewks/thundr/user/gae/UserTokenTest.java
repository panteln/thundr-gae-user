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

import org.junit.Rule;
import org.junit.Test;

import com.googlecode.objectify.Key;
import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;
import com.threewks.thundr.test.TestSupport;

public class UserTokenTest {

	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(User.class, UserToken.class);

	@Test
	public void shouldCreateTokenForUser() {
		User user = new User("test");
		UserToken userToken = new UserToken(user);
		TestSupport.setField(userToken, "id", 1234l);

		assertThat(userToken, is(notNullValue()));
		assertThat(userToken.getToken(), is(notNullValue()));

		Key<UserToken> key = Key.create(userToken.getToken());
		assertThat(key.getId(), is(1234l));
		assertThat(key.getKind(), is("UserToken"));
		assertThat(key.getParent().getName(), is("test"));
		assertThat(key.getParent().getKind(), is("thundrUser"));
	}

	@Test
	public void shouldReturnUserFromToken() {
		User user = new User("test");
		ofy().save().entity(user).now();
		UserToken userToken = new UserToken(user);
		assertThat(userToken.getUser(), is(user));
	}

	@Test
	public void shouldGetKeyForToken() {
		User user = new User("test");
		ofy().save().entity(user).now();
		UserToken userToken = new UserToken(user);

		Key<User> userKey = Key.create(user);
		Long id = TestSupport.getField(userToken, "id");

		assertThat(userToken.getKey(), is(notNullValue()));
		assertThat(userToken.getKey(), is(Key.create(userKey, UserToken.class, id)));
		;
	}

	@Test
	public void shouldHaveDefaultCtor() throws InstantiationException, IllegalAccessException {
		assertThat(UserToken.class.newInstance(), is(notNullValue()));
	}
}
