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

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.threewks.thundr.gae.SetupAppengine;
import com.threewks.thundr.gae.objectify.SetupObjectify;
import com.threewks.thundr.user.authentication.Authentication;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;

public class UserRepositoryIT {
	@Rule
	public SetupAppengine setupAppengine = new SetupAppengine();
	@Rule
	public SetupObjectify setupObjectify = new SetupObjectify(User.class, PasswordAuthentication.class);

	private UserRepositoryImpl<User> userRepository;
	
	@Before
	public void before() {
		 userRepository  = new UserRepositoryImpl<User>(User.class, null);
	}

	@Test
	public void shouldPutAuthentication() {
		User user = new User("username");
		PasswordAuthentication authentication = new PasswordAuthentication("username", "password");
		userRepository.putAuthentication(user, authentication);

		User savedUser = ofy().load().type(User.class).id("username").now();
		assertThat(savedUser, is(notNullValue()));
		PasswordAuthentication savedAuth = ofy().load().type(PasswordAuthentication.class).id("username").now();
		assertThat(savedAuth.getHashedpassword(), is(authentication.getHashedpassword()));
	}
	
	@Test
	public void shouldRemoveAuthentication() {
		User user = new User("username");
		PasswordAuthentication authentication = new PasswordAuthentication("username", "password");
		userRepository.putAuthentication(user, authentication);
		
		assertThat(ofy().load().type(PasswordAuthentication.class).id("username").now(), is(notNullValue()));
		userRepository.removeAuthentication(authentication);
		
		assertThat(ofy().load().type(PasswordAuthentication.class).id("username").now(), is(nullValue()));
	}
	
	@Test
	public void shouldGetUserForAuthentication() {
		User user = new User("username");
		PasswordAuthentication authentication = new PasswordAuthentication("username", "password");
		userRepository.putAuthentication(user, authentication);
		
		User found = userRepository.get(authentication);
		assertThat(found, is(notNullValue()));
		assertThat(found, is(user));
	}
	
	@Test
	public void shouldGetExistingAuthenticationForGivenAuthentication() {
		User user = new User("username");
		PasswordAuthentication authentication = new PasswordAuthentication("username", "password");
		userRepository.putAuthentication(user, authentication);
		
		Authentication found = userRepository.getAuthentication(new PasswordAuthentication("username", "junk"));
		assertThat(found, is(notNullValue()));
		assertThat(found, is((Authentication)authentication));
	}

}
