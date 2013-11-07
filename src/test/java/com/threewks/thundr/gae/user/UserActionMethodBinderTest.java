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
package com.threewks.thundr.gae.user;

import static com.atomicleopard.expressive.Expressive.map;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.threewks.thundr.introspection.ParameterDescription;

public class UserActionMethodBinderTest {
	private UserService userService = mock(UserService.class);
	private UserActionMethodBinder userActionMethodBinder = new UserActionMethodBinder(userService);
	private User user = new User();
	private HttpServletRequest req = mock(HttpServletRequest.class);

	@Test
	public void shouldBindToUserParameterWhenNotBoundAlready() {
		when(userService.getUserFromRequest(req)).thenReturn(user);
		ParameterDescription parameterDescription = new ParameterDescription("user", User.class);
		ParameterDescription parameterDescription2 = new ParameterDescription("somethingElse", String.class);
		Map<ParameterDescription, Object> bindings = map(parameterDescription, null, parameterDescription2, null);
		userActionMethodBinder.bindAll(bindings, req, null, null);
		assertThat(bindings.get(parameterDescription), is((Object) user));
		assertThat(bindings.get(parameterDescription2), is((Object) null));
	}

	@Test
	public void shouldNotBindToUserParameterWhenAlreadyBound() {
		User existingUser = new User();
		when(userService.getUserFromRequest(req)).thenReturn(user);
		ParameterDescription parameterDescription = new ParameterDescription("user", User.class);
		Map<ParameterDescription, Object> bindings = map(parameterDescription, existingUser);
		userActionMethodBinder.bindAll(bindings, req, null, null);
		assertThat(bindings.get(parameterDescription), is(not((Object) user)));
		assertThat(bindings.get(parameterDescription), is((Object) existingUser));
	}

	@Test
	public void shouldBindToUserParameterRegardlessOfParameterName() {
		when(userService.getUserFromRequest(req)).thenReturn(user);
		ParameterDescription parameterDescription = new ParameterDescription("foop", User.class);
		Map<ParameterDescription, Object> bindings = map(parameterDescription, null);
		userActionMethodBinder.bindAll(bindings, req, null, null);
		assertThat(bindings.get(parameterDescription), is((Object) user));
	}

	@Test
	public void shouldBindNullToUserParameterWhenNoUserAvailable() {
		when(userService.getUserFromRequest(req)).thenReturn(null);
		ParameterDescription parameterDescription = new ParameterDescription("loggedIn", User.class);
		Map<ParameterDescription, Object> bindings = map(parameterDescription, null);
		userActionMethodBinder.bindAll(bindings, req, null, null);
		assertThat(bindings.get(parameterDescription), is(nullValue()));
	}
}
