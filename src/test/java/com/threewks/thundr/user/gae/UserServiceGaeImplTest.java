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

import com.threewks.thundr.search.google.IndexOperation;
import com.threewks.thundr.search.google.SearchRequest;
import com.threewks.thundr.search.google.SearchService;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserTokenRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.atomicleopard.expressive.Expressive.list;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceGaeImplTest {

	public static final String USERNAME = "username";
	@Mock
	private SearchService searchService;
	@Mock
	private UserTokenRepository<User> tokenRepository;
	@Mock
	private UserRepository<User> userRepository;

	@Rule
	public LocalAppEngineServices localAppEngineServices = new LocalAppEngineServices();

	UserServiceGaeImpl userServiceImpl;

	@Before
	public void setUp() throws Exception {
		userServiceImpl = new UserServiceGaeImpl(searchService, tokenRepository, userRepository);
	}

	@Test
	public void shouldSaveUserAndIndexToSearchService() {
		User user = createUser(USERNAME);
		when(searchService.index(user, USERNAME, user.getFieldsToIndex())).thenReturn(mock(IndexOperation.class));
		userServiceImpl.put(user);
		verify(searchService, times(1)).index(user, USERNAME, user.getFieldsToIndex());
	}

	@Test
	public void shouldDeleteUserAndRemoveFromIndex() {
		User user = createUser(USERNAME);
		when(searchService.index(user, USERNAME, user.getFieldsToIndex())).thenReturn(mock(IndexOperation.class));
		userServiceImpl.put(user);
		userServiceImpl.delete(USERNAME);
		verify(searchService, times(1)).remove(User.class, list(USERNAME));
	}

	@Ignore
	public void shouldSearchByEmailAscendingWithLimit() {
		when(searchService.search(User.class)).thenReturn(mock(SearchRequest.class));
		userServiceImpl.search("test@mail.com", 100);
	}

	@Test
	public void registerObjectifyClasses_state_expectation() {
		// UserServiceImpl.registerObjectifyClasses(ofy);
	}

	private User createUser(String username) {
		return new User(username);
	}

}
