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
package com.threewks.thundr.user.gae.authentication;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;
import com.threewks.thundr.user.gae.UserGae;

public class PasswordAuthenticationTest {

	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(PasswordAuthentication.class, UserGae.class);

	@Test
	public void shouldHaveDefaultCtor() throws InstantiationException, IllegalAccessException {
		assertThat(PasswordAuthentication.class.newInstance(), is(notNullValue()));
	}

	@Test
	public void shouldSetUsernameAsKeyAndHashPasswordWithDefaultStrategy() {
		PasswordAuthentication auth = new PasswordAuthentication("username", "password");
		assertThat(auth.getUsername(), is("username"));
		assertThat(auth.getHashedpassword(), is(not("password")));
		assertThat(auth.getHashedpassword().length(), is(88));
		assertThat(auth.getIterations(), is(1000));
		assertThat(auth.getDigest(), is("SHA-512"));
		assertThat(auth.getSalt(), is(notNullValue()));
	}

	@Test
	public void shouldSetUsernameAsKeyAndHashPasswordWithGivenIterationsAndStrategy() {
		PasswordAuthentication auth = new PasswordAuthentication("username", "password", 1, "MD5");
		assertThat(auth.getUsername(), is("username"));
		assertThat(auth.getHashedpassword(), is(not("password")));
		assertThat(auth.getHashedpassword().length(), is(24));
		assertThat(auth.getIterations(), is(1));
		assertThat(auth.getDigest(), is("MD5"));
		assertThat(auth.getSalt(), is(notNullValue()));
	}

	@Test
	public void shouldAllowSettingOfProperties() {
		PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
		// username
		assertThat(passwordAuthentication.getUsername(), is(nullValue()));
		passwordAuthentication.setUsername("username");
		assertThat(passwordAuthentication.getUsername(), is("username"));

		// password
		assertThat(passwordAuthentication.getHashedpassword(), is(nullValue()));
		passwordAuthentication.setHashedpassword("hashed-password");
		assertThat(passwordAuthentication.getHashedpassword(), is("hashed-password"));

		// salt
		assertThat(passwordAuthentication.getSalt(), is(nullValue()));
		passwordAuthentication.setSalt(new byte[] { 1, 2, 3 });
		assertThat(passwordAuthentication.getSalt(), is(new byte[] { 1, 2, 3 }));

		// digest
		assertThat(passwordAuthentication.getDigest(), is(nullValue()));
		passwordAuthentication.setDigest("SHA-1");
		assertThat(passwordAuthentication.getDigest(), is("SHA-1"));

		// iterations
		assertThat(passwordAuthentication.getIterations(), is(0));
		passwordAuthentication.setIterations(1024);
		assertThat(passwordAuthentication.getIterations(), is(1024));
	}

	@Test
	public void shouldRetainRefToUser() {
		UserGae user = new UserGae("username");
		ofy().save().entity(user).now();

		PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
		passwordAuthentication.setUser(user);
		assertThat(passwordAuthentication.getUser(ofy()), is(user));
	}

	@Test
	public void shouldGetRefToUserIfUserRefNotSet() {
		UserGae user = new UserGae("username");
		PasswordAuthentication existing = new PasswordAuthentication("username", "password");
		existing.setUser(user);
		ofy().save().entity(user).now();
		ofy().save().entity(existing).now();

		PasswordAuthentication passwordAuthentication = new PasswordAuthentication("username", "password");
		assertThat(passwordAuthentication.getUser(ofy()), is(user));
	}

	@Test
	public void shouldGetMatchingAuthentication() {
		UserGae user = new UserGae("username");
		PasswordAuthentication existing = new PasswordAuthentication("username", "password");

		ofy().save().entity(user).now();
		ofy().save().entity(existing).now();

		PasswordAuthentication match = new PasswordAuthentication("username", "something-else");
		PasswordAuthentication found = match.getMatchingAuthentication(ofy(), match);
		assertThat(found, is(existing));
	}

}
