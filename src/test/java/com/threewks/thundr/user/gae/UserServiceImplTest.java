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

import static com.atomicleopard.expressive.Expressive.list;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.threewks.thundr.search.Is;
import com.threewks.thundr.search.OrderComponent;
import com.threewks.thundr.search.QueryComponent;
import com.threewks.thundr.search.Search;
import com.threewks.thundr.search.test.MockSearch;
import com.threewks.thundr.session.SessionService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

	public static final String USERNAME = "username";
	@Mock
	private SessionService<SessionGae> sessionService;
	@Mock
	private UserRepositoryImpl<UserGae> userRepository;

	private UserServiceGaeImpl<UserGae, SessionGae> userService;

	@Before
	public void setUp() throws Exception {
		userService = new UserServiceGaeImpl<>(userRepository, sessionService);
	}

	@Test
	public void shouldSaveUserAndIndexToSearchService() {
		UserGae user = createUser(USERNAME);
		userService.put(user);
		verify(userRepository, times(1)).put(user);
	}

	@Test
	public void shouldDeleteUserAndRemoveFromIndex() {
		UserGae user = createUser(USERNAME);

		when(userRepository.get(USERNAME)).thenReturn(user);

		boolean deleted = userService.delete(USERNAME);
		assertThat(deleted, is(true));
		verify(userRepository, times(1)).deleteByKey(USERNAME);
	}

	@Test
	public void shouldSearchByDelegatingToRepository() {
		UserGae user = createUser(USERNAME);
		MockSearch<UserGae, String> search = new MockSearch<UserGae, String>(list(user), null);
		when(userRepository.search()).thenReturn(search);

		List<UserGae> result = userService.search("test@mail.com", 100);
		assertThat(result, hasItem(user));
	}

	@Test
	public void shouldSearchByEmailAscendingWithLimit() {
		when(userRepository.search()).thenReturn(new MockSearch<UserGae, String>(null, list("1", "2", "3")));

		Search<UserGae, String> search = userService.buildSearch("test@mail.com", 100);
		assertThat(search.limit(), is(100));
		assertThat(search.order(), hasItem(OrderComponent.forFieldAscending("email")));
		assertThat(search.query(), hasItem(QueryComponent.forFieldQuery("email", Is.Is, "test@mail.com")));
	}

	private UserGae createUser(String username) {
		return new UserGae(username);
	}

}
