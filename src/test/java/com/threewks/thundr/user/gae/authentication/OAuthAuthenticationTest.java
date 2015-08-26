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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;
import com.threewks.thundr.user.gae.UserGae;

public class OAuthAuthenticationTest {
	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(OAuthAuthentication.class, UserGae.class);

	private OAuthAuthentication oAuthAuthentication;

	@Test
	public void shouldSetIdOnConstruction() {
		oAuthAuthentication = new OAuthAuthentication("provider", "identity", "email");
		assertThat(oAuthAuthentication.id, is("identity:provider"));
	}

	@Test
	public void shouldSetIdOnSettingIdentityAndProvider() {
		oAuthAuthentication = new OAuthAuthentication();
		assertThat(oAuthAuthentication.id, is(nullValue()));
		assertThat(oAuthAuthentication.getProvider(), is(nullValue()));
		oAuthAuthentication.setProvider("provider");
		oAuthAuthentication.setIdentity("identity");
		assertThat(oAuthAuthentication.id, is("identity:provider"));
	}

	@Test
	public void shouldRetainRefToUser() {
		UserGae user = new UserGae("username");
		ofy().save().entity(user).now();

		OAuthAuthentication passwordAuthentication = new OAuthAuthentication();
		passwordAuthentication.setUser(user);
		assertThat(passwordAuthentication.getUser(ofy()), is(user));
	}

	@Test
	public void shouldGetRefToUserIfUserRefNotSet() {
		UserGae user = new UserGae("username");
		OAuthAuthentication existing = new OAuthAuthentication("test", "username", "username@email.com");
		existing.setUser(user);
		ofy().save().entity(user).now();
		ofy().save().entity(existing).now();

		OAuthAuthentication passwordAuthentication = new OAuthAuthentication("test", "username", "username@email.com");
		assertThat(passwordAuthentication.getUser(ofy()), is(user));
	}

	@Test
	public void shouldGetMatchingAuthentication() {
		UserGae user = new UserGae("username");
		OAuthAuthentication existing = new OAuthAuthentication("test", "username", "username@email.com");

		ofy().save().entity(user).now();
		ofy().save().entity(existing).now();

		OAuthAuthentication match = new OAuthAuthentication("test", "username", "username@email.com");
		OAuthAuthentication found = match.getMatchingAuthentication(ofy(), match);
		assertThat(found, is(existing));
	}

}
