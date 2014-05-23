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

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.threewks.thundr.action.method.bind.ActionMethodBinderRegistry;
import com.threewks.thundr.gae.GaeModule;
import com.threewks.thundr.gae.objectify.ObjectifyModule;
import com.threewks.thundr.injection.BaseModule;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.user.ThundrUserService;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserTokenRepository;
import com.threewks.thundr.user.action.UserActionMethodBinder;

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
		injectionContext.inject(UserRepositoryImpl.class).as(UserRepository.class);
		injectionContext.inject(UserTokenRepositoryImpl.class).as(UserTokenRepository.class);
		injectionContext.inject(UserServiceImpl.class).as(UserService.class);
		injectionContext.inject(UserServiceImpl.class).as(ThundrUserService.class);
		configureObjectify(injectionContext);
	}

	@Override
	public void start(UpdatableInjectionContext injectionContext) {
		UserService userService = injectionContext.get(UserService.class);
		ActionMethodBinderRegistry actionMethodBinderRegistry = injectionContext.get(ActionMethodBinderRegistry.class);
		UserActionMethodBinder<User> userActionMethodBinder = new UserActionMethodBinder<User>(User.class, userService);
		actionMethodBinderRegistry.registerActionMethodBinder(userActionMethodBinder);
	}

	private void configureObjectify(UpdatableInjectionContext injectionContext) {
		ObjectifyFactory objectifyFactory = ObjectifyService.factory();
		UserServiceImpl.registerObjectifyClasses(objectifyFactory);
	}
}
