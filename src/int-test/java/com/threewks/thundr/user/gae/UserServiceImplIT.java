/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://3wks.github.io/thundr/
 * Copyright (C) 2015 3wks, <thundr@3wks.com.au>
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
import com.threewks.thundr.session.SessionService;
import com.threewks.thundr.session.SessionServiceImpl;
import com.threewks.thundr.transformer.TransformerManager;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;

public class UserServiceImplIT {

	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(UserGae.class, SessionGae.class, SessionId.class, PasswordAuthentication.class);

	private String username = "username";
	private UserServiceGaeImpl<UserGae, SessionGae> service;
	private UserGae user;
	private PasswordAuthentication password;
	private SessionRepositoryGae sessionRepository;
	private SessionService<SessionGae> sessionService;

	@Before
	public void before() {
		user = new UserGae(username);
		password = new PasswordAuthentication(username, "password");

		sessionRepository = new SessionRepositoryGae();
		sessionService = new SessionServiceImpl<>(sessionRepository);
		UserRepositoryImpl<UserGae> userRepository = new UserRepositoryImpl<UserGae>(UserGae.class, new SearchConfig(TransformerManager.createWithDefaults(), new FieldMediatorSet(),
				new IndexTypeLookup()));
		service = new UserServiceGaeImpl<>(userRepository, sessionService);
	}

	@Test
	public void shouldCreateAndLoginUserWithPassword() {

		service.put(user, password);

		assertThat(service.get(username), is(notNullValue()));

		SessionGae session = sessionRepository.create();
		UserGae u = service.login(session, new PasswordAuthentication(username, "password"), "password");
		assertThat(u, is(user));
		assertThat(session.isAuthenticated(), is(true));
	}
}
