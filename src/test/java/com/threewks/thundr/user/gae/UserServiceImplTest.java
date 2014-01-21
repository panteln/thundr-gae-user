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

import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;
import com.threewks.thundr.search.google.IndexOperation;
import com.threewks.thundr.search.google.SearchRequest;
import com.threewks.thundr.search.google.SearchService;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserTokenRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceGaeImplTest {

	public static final String USERNAME = "username";
	@Mock private SearchService searchService;
	@Mock private UserTokenRepository<User> tokenRepository;
	@Mock private UserRepository<User> userRepository;

	@Rule public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule public SetupObjectify setupObjectify = new SetupObjectify(User.class);

	UserServiceImpl userServiceImpl;

	@Before
	public void setUp() throws Exception {
		userServiceImpl = new UserServiceImpl(searchService, tokenRepository, userRepository);
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

	@Test
	public void shouldSearchByEmailAscendingWithLimit() {
		SearchRequest query = mock(SearchRequest.class);
		when(searchService.search(User.class)).thenReturn(query);
		SearchRequest searchRequest = mock(SearchRequest.class);
		SearchOperation searchEmailOperation = mock(SearchOperation.class);
		when(query.field(User.Fields.Email)).thenReturn(searchEmailOperation);
		SortOperation sortOperation = mock(SortOperation.class);
		when(query.order(User.Fields.Email)).thenReturn(sortOperation);

		SearchResult searchResult = mock(SearchResult.class);
		when(query.search()).thenReturn(searchResult);
		EList resultIds = list("1", "2", "3");
		when(searchResult.getSearchResultIds()).thenReturn(resultIds);
		userServiceImpl.search("test@mail.com", 100);
		verify(searchEmailOperation, times(1)).is("test@mail.com");
		verify(sortOperation, times(1)).ascending();
		verify(query, times(1)).limit(100);
	}

	@Test
	public void registerObjectifyClasses_state_expectation() {
		ObjectifyFactory objectifyFactory = mock(ObjectifyFactory.class);
		UserServiceImpl.registerObjectifyClasses(objectifyFactory);
		verify(objectifyFactory, times(1)).register(User.class);
		verify(objectifyFactory, times(1)).register(UserToken.class);
		verify(objectifyFactory, times(1)).register(PasswordAuthentication.class);
		verify(objectifyFactory, times(1)).register(OAuthAuthentication.class);
	}

    @SuppressWarnings("unchecked")
    @Ignore
    public void shouldSearchByEmailAscendingWithLimit() {
        when(searchService.search(User.class)).thenReturn(mock(SearchRequest.class));
        userServiceImpl.search("test@mail.com", 100);
    }

	private User createUser(String username) {
		return new User(username);
	}

}
