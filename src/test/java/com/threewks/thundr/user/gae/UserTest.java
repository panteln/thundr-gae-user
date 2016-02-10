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

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import static com.atomicleopard.expressive.Expressive.*;

public class UserTest {
	private DateTime now = new DateTime(2013, 5, 5, 10, 0, 0, 0);

	@Before
	public void before() {
		DateTimeUtils.setCurrentMillisFixed(now.getMillis());
	}

	@Test
	public void shouldHaveDefaultCtor() throws InstantiationException, IllegalAccessException {
		assertThat(User.class.newInstance(), is(notNullValue()));
	}

	@Test
	public void shouldCreateWithUsername() {
		User user = new User("username");
		assertThat(user.getUsername(), is("username"));
		assertThat(user.getCreated(), is(now));
		assertThat(user.getLastLogin(), is(nullValue()));
		assertThat(user.getProperties().isEmpty(), is(true));
	}

	@Test
	public void shouldAllowSettingOfUsername() {
		User user = new User();
		assertThat(user.getUsername(), is(nullValue()));

		user.setUsername("username");

		assertThat(user.getUsername(), is("username"));

		assertThat(user.withUsername("new username"), is(user));
		assertThat(user.getUsername(), is("new username"));

	}

	@Test
	public void shouldAllowSettingOfEmail() {
		User user = new User();
		assertThat(user.getEmail(), is(nullValue()));
		assertThat(user.getEmailUser(), is(nullValue()));
		assertThat(user.getEmailDomain(), is(nullValue()));

		user.setEmail("user@domain.com");

		assertThat(user.getEmail(), is("user@domain.com"));
		assertThat(user.getEmailUser(), is("user"));
		assertThat(user.getEmailDomain(), is("domain.com"));

		assertThat(user.withEmail("other@different.co.uk"), is(user));

		assertThat(user.getEmail(), is("other@different.co.uk"));
		assertThat(user.getEmailUser(), is("other"));
		assertThat(user.getEmailDomain(), is("different.co.uk"));
	}

	@Test
	public void shouldAllowSettingOfRoles() {
		User user = new User();
		assertThat(user.getRoles().isEmpty(), is(true));
		assertThat(user.hasRole("Role"), is(false));
		assertThat(user.hasRoles("Role", "Role2"), is(false));
		assertThat(user.hasRoles(list("Role", "Role2")), is(false));

		user.addRole("Role");
		assertThat(user.hasRole("Role"), is(true));
		assertThat(user.hasRoles("Role", "Role2"), is(false));
		assertThat(user.hasRoles(list("Role", "Role2")), is(false));

		user.setRoles(list("Role", "Role2"));

		assertThat(user.hasRole("Role"), is(true));
		assertThat(user.hasRoles("Role", "Role2"), is(true));
		assertThat(user.hasRoles(list("Role", "Role2")), is(true));

		user.removeRole("Role");
		assertThat(user.hasRole("Role"), is(false));
		assertThat(user.hasRole("Role2"), is(true));
		assertThat(user.hasRoles("Role"), is(false));
		assertThat(user.hasRoles("Role2"), is(true));
		assertThat(user.hasRoles("Role", "Role2"), is(false));
		assertThat(user.hasRoles(list("Role", "Role2")), is(false));
	}

	@Test
	public void shouldAllowStoringOfStringProperties() {
		User user = new User();
		assertThat(user.getProperties().isEmpty(), is(true));

		user.setProperty("key", "value");
		assertThat(user.getProperties().isEmpty(), is(false));
		assertThat(user.getProperty("key"), is("value"));

		assertThat(user.withProperty("key", "new value"), is(user));
		assertThat(user.getProperty("key"), is("new value"));

		user.removeProperty("key");
		assertThat(user.getProperty("key"), is(nullValue()));
		assertThat(user.getProperties().isEmpty(), is(true));
	}

	@Test
	public void shouldUpdateLastLoginTime() {
		User user = new User();
		assertThat(user.getLastLogin(), is(nullValue()));

		user.setLastLogin(new DateTime());

		assertThat(user.getLastLogin(), is(notNullValue()));
	}
	
	@Test
	public void shouldSetRoles() {
		User user = new User();
		assertThat(user.getRoles().isEmpty(), is(true));
		user.setRoles(list("role1", "role2"));
		assertThat(user.getRoles(), is(set("role1", "role2")));
	}

}
