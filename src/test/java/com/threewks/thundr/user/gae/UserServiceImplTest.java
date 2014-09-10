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

import static com.atomicleopard.expressive.Expressive.list;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.threewks.thundr.gae.objectify.repository.AsyncResult;
import com.threewks.thundr.search.Is;
import com.threewks.thundr.search.OrderComponent;
import com.threewks.thundr.search.QueryComponent;
import com.threewks.thundr.search.Search;
import com.threewks.thundr.search.test.MockSearch;
import com.threewks.thundr.user.UserTokenRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

	public static final String USERNAME = "username";
	@Mock
	private UserTokenRepository<User> tokenRepository;
	@Mock
	private UserRepositoryImpl<User> userRepository;

	UserServiceImpl userServiceImpl;

	@Before
	public void setUp() throws Exception {
		userServiceImpl = new UserServiceImpl(tokenRepository, userRepository);
	}

	@Test
	public void shouldSaveUserAndIndexToSearchService() {
		User user = createUser(USERNAME);
		AsyncResult<User> async = mock(AsyncResult.class);
		when(userRepository.save(user)).thenReturn(async);
		userServiceImpl.put(user);
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void shouldDeleteUserAndRemoveFromIndex() {
		User user = createUser(USERNAME);

		when(userRepository.load(USERNAME)).thenReturn(user);
		AsyncResult<Void> async = mock(AsyncResult.class);
		when(userRepository.deleteByKey(anyString())).thenReturn(async);

		boolean deleted = userServiceImpl.delete(USERNAME);
		assertThat(deleted, is(true));
		verify(userRepository, times(1)).deleteByKey(USERNAME);
	}

	@Test
	public void shouldSearchByDelegatingToRepository() {
		User user = createUser(USERNAME);
		MockSearch<User, String> search = new MockSearch<User, String>(list(user), null);
		when(userRepository.search()).thenReturn(search);

		List<User> result = userServiceImpl.search("test@mail.com", 100);
		assertThat(result, hasItem(user));
	}

	@Test
	public void shouldSearchByEmailAscendingWithLimit() {
		when(userRepository.search()).thenReturn(new MockSearch<User, String>(null, list("1", "2", "3")));

		Search<User, String> search = userServiceImpl.buildSearch("test@mail.com", 100);
		assertThat(search.limit(), is(100));
		assertThat(search.order(), hasItem(OrderComponent.forFieldAscending("email")));
		assertThat(search.query(), hasItem(QueryComponent.forFieldQuery("email", Is.Is, "test@mail.com")));
	}

	private User createUser(String username) {
		return new User(username);
	}

}
