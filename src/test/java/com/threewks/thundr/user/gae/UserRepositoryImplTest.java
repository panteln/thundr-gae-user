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

import static org.mockito.Mockito.*;

import org.junit.Test;

import com.googlecode.objectify.ObjectifyFactory;
import com.threewks.thundr.user.gae.authentication.OAuthAuthentication;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;

public class UserRepositoryImplTest {

	@Test
	public void registerObjectifyClasses_state_expectation() {
		ObjectifyFactory objectifyFactory = mock(ObjectifyFactory.class);
		UserRepositoryImpl.registerObjectifyClasses(objectifyFactory);
		verify(objectifyFactory, times(1)).register(User.class);
		verify(objectifyFactory, times(1)).register(UserToken.class);
		verify(objectifyFactory, times(1)).register(PasswordAuthentication.class);
		verify(objectifyFactory, times(1)).register(OAuthAuthentication.class);
	}
}
