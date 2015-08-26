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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.search.gae.mediator.FieldMediatorSet;
import com.threewks.thundr.search.gae.meta.IndexTypeLookup;
import com.threewks.thundr.session.Session;
import com.threewks.thundr.transformer.TransformerManager;
import com.threewks.thundr.user.authentication.AuthenticatedSession;
import com.threewks.thundr.user.authentication.AuthenticationContextRepository;
import com.threewks.thundr.user.gae.authentication.AuthenticationContextGae;
import com.threewks.thundr.user.gae.authentication.AuthenticationContextRepositoryImpl;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;

public class UserServiceImplIT {

	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(UserGae.class, SessionGae.class, AuthenticationContextGae.class, SessionId.class, PasswordAuthentication.class);

	private String username = "username";
	private UserServiceImpl service;
	private UserGae user;
	private PasswordAuthentication password;
	private SessionRepositoryGae sessionRepository;

	@Before
	public void before() {
		user = new UserGae(username);
		password = new PasswordAuthentication(username, "password");

		sessionRepository = new SessionRepositoryGae();
		UserRepositoryImpl<UserGae> userRepository = new UserRepositoryImpl<UserGae>(UserGae.class, new SearchConfig(TransformerManager.createWithDefaults(), new FieldMediatorSet(),
				new IndexTypeLookup()));
		AuthenticationContextRepository authenticationContextRepository = new AuthenticationContextRepositoryImpl();
		service = new UserServiceImpl(sessionRepository, userRepository, authenticationContextRepository);
	}

	@Test
	public void shouldCreateAndLoginUserWithPassword() {

		service.put(user, password);

		assertThat(service.get(username), is(notNullValue()));

		Session session = sessionRepository.create();
		AuthenticatedSession loggedIn = service.login(new PasswordAuthentication(username, "password"), "password", session);
		assertThat(loggedIn, is(notNullValue()));
		assertThat(loggedIn.getSession(), is(session));
		assertThat(loggedIn.<UserGae> getUser(), is(user));
		assertThat(loggedIn.getAuthenticationContext(), is(notNullValue()));
	}
}
