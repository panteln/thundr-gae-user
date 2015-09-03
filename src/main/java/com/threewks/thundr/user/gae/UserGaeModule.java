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

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.threewks.thundr.bind.BinderRegistry;
import com.threewks.thundr.gae.GaeModule;
import com.threewks.thundr.gae.objectify.ObjectifyModule;
import com.threewks.thundr.injection.BaseModule;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.search.gae.SearchConfig;
import com.threewks.thundr.session.Session;
import com.threewks.thundr.session.SessionRepository;
import com.threewks.thundr.session.SessionService;
import com.threewks.thundr.user.AccountRepository;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserService;
import com.threewks.thundr.user.bind.UserBinder;
import com.threewks.thundr.user.gae.authentication.OAuthAuthentication;
import com.threewks.thundr.user.gae.authentication.PasswordAuthentication;

public class UserGaeModule extends BaseModule {
	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		super.requires(dependencyRegistry);
		dependencyRegistry.addDependency(GaeModule.class);
		dependencyRegistry.addDependency(ObjectifyModule.class);
		dependencyRegistry.addDependency(com.threewks.thundr.user.UserModule.class);
	}

	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
		super.configure(injectionContext);
		configureObjectify(injectionContext);

		SearchConfig searchConfig = injectionContext.get(SearchConfig.class);
		UserRepositoryImpl<UserGae> userRepository = new UserRepositoryImpl<>(UserGae.class, searchConfig);
		injectionContext.inject(userRepository).as(UserRepository.class, UserRepositoryGae.class, UserRepositoryImpl.class);
		injectionContext.inject(UserServiceGaeImpl.class).as(UserService.class, UserServiceGae.class, UserServiceGaeImpl.class);
		injectionContext.inject(SessionRepositoryGae.class).as(SessionRepository.class);
		injectionContext.inject(AccountRepositoryImpl.class).as(AccountRepositoryImpl.class, AccountRepository.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(UpdatableInjectionContext injectionContext) {
		UserService<UserGae> userService = injectionContext.get(UserService.class);
		SessionService<Session> sessionService = injectionContext.get(SessionService.class);
		BinderRegistry binderRegistry = injectionContext.get(BinderRegistry.class);
		UserBinder<UserGae> userActionMethodBinder = new UserBinder<UserGae>(UserGae.class, userService, sessionService);
		binderRegistry.add(userActionMethodBinder);
	}

	private void configureObjectify(UpdatableInjectionContext injectionContext) {
		ObjectifyFactory objectifyFactory = ObjectifyService.factory();
		registerObjectifyClasses(objectifyFactory);
	}

	public static void registerObjectifyClasses(ObjectifyFactory objectifyFactory) {
		objectifyFactory.register(UserGae.class);
		objectifyFactory.register(SessionId.class);
		objectifyFactory.register(SessionGae.class);
		objectifyFactory.register(PasswordAuthentication.class);
		objectifyFactory.register(OAuthAuthentication.class);
		objectifyFactory.register(AccountGae.class);
	}
}
