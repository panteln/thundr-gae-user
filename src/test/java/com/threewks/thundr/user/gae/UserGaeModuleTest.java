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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.threewks.thundr.bind.BinderRegistry;
import com.threewks.thundr.gae.GaeModule;
import com.threewks.thundr.gae.objectify.ObjectifyModule;
import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.user.UserModule;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.bind.UserBinder;

public class UserGaeModuleTest {

	private UserGaeModule configuration;
	private UpdatableInjectionContext injectionContext;
	private BinderRegistry binderRegistry;

	@Before
	public void setUp() throws Exception {
		configuration = new UserGaeModule();
		injectionContext = new InjectionContextImpl();
		injectionContext.inject(BinderRegistry.class).as(BinderRegistry.class);
		binderRegistry = injectionContext.get(BinderRegistry.class);
	}

	@Test
	public void shouldDependOn() {
		DependencyRegistry dependencyRegistry = new DependencyRegistry();
		configuration.requires(dependencyRegistry);
		
		assertThat(dependencyRegistry.hasDependency(GaeModule.class), is(true));
		assertThat(dependencyRegistry.hasDependency(ObjectifyModule.class), is(true));
		assertThat(dependencyRegistry.hasDependency(UserModule.class), is(true));
		
	}
	@Test
	public void shouldInjectServices() {
		configuration.configure(injectionContext);
		assertThat(injectionContext.contains(UserServiceGae.class), is(true));
		assertThat(injectionContext.contains(UserRepository.class), is(true));
	}

	@Test
	public void shouldRegisterActionMethodBinder() {
		configuration.start(injectionContext);

		assertThat(binderRegistry.contains(UserBinder.class), is(true));
	}

	@Test
	public void shouldRegisterObjectify() {
		configuration.configure(injectionContext);
		ObjectifyFactory objectifyFactory = ObjectifyService.factory();
		assertThat(objectifyFactory, not(nullValue()));
	}
}
