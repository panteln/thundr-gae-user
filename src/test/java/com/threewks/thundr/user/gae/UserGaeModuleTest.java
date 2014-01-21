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

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.threewks.thundr.action.method.bind.ActionMethodBinder;
import com.threewks.thundr.action.method.bind.ActionMethodBinderRegistry;
import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserTokenRepository;
import com.threewks.thundr.user.action.UserActionMethodBinder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UserGaeModuleTest {

	private UserGaeModule configuration;
	private UpdatableInjectionContext injectionContext;
	private ActionMethodBinderRegistry actionMethodBinderRegistry;

	@Before
	public void setUp() throws Exception {
		configuration = new UserGaeModule();
		injectionContext = new InjectionContextImpl();
		injectionContext.inject(ActionMethodBinderRegistry.class).as(ActionMethodBinderRegistry.class);
		actionMethodBinderRegistry = injectionContext.get(ActionMethodBinderRegistry.class);
	}

	@Test
	public void shouldInjectServices() {
		configuration.configure(injectionContext);
		assertThat(injectionContext.contains(UserTokenRepository.class), is(true));
		assertThat(injectionContext.contains(UserService.class), is(true));
		assertThat(injectionContext.contains(UserRepository.class), is(true));
	}

	@Test
	public void shouldRegisterActionMethodBinder() {
		configuration.start(injectionContext);
		UserActionMethodBinder<User> userActionMethodBinder = new UserActionMethodBinder<User>(User.class, injectionContext.get(UserService.class));
		HashMap<String, ActionMethodBinder> binders = Maps.newHashMap();
		Iterable<ActionMethodBinder> registeredActionMethodBinders = actionMethodBinderRegistry.getRegisteredActionMethodBinders();
		assertThat(registeredActionMethodBinders.iterator().next().getClass().getSimpleName(),
				is(UserActionMethodBinder.class.getSimpleName()));

	}

	@Test
	public void shouldRegisterObjectify() {
		configuration.configure(injectionContext);
		ObjectifyFactory objectifyFactory = ObjectifyService.factory();
		assertThat(objectifyFactory, not(nullValue()));
	}
}
