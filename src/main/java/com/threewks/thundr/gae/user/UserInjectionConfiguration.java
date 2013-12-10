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

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.threewks.thundr.action.method.ActionInterceptorRegistry;
import com.threewks.thundr.action.method.MethodAction;
import com.threewks.thundr.action.method.bind.ActionMethodBinderRegistry;
import com.threewks.thundr.gae.GaeInjectionConfiguration;
import com.threewks.thundr.injection.BaseInjectionConfiguration;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.route.RouteType;
import com.threewks.thundr.route.Routes;

public class UserInjectionConfiguration extends BaseInjectionConfiguration {
	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		super.requires(dependencyRegistry);
		dependencyRegistry.addDependency(GaeInjectionConfiguration.class);
	}

	@Override
	public void initialise(UpdatableInjectionContext injectionContext) {
		super.initialise(injectionContext);
		injectionContext.inject(UserServiceImpl.class).as(UserService.class);
	}

	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
		super.configure(injectionContext);

		UserService userService = injectionContext.get(UserService.class);
		String userLoginPath = injectionContext.get(String.class, "userLoginPath");

		ActionMethodBinderRegistry actionMethodBinderRegistry = injectionContext.get(ActionMethodBinderRegistry.class);
		UserActionMethodBinder userActionMethodBinder = new UserActionMethodBinder(userService);
		actionMethodBinderRegistry.registerActionMethodBinder(userActionMethodBinder);

		ActionInterceptorRegistry actionInterceptorRegistry = injectionContext.get(ActionInterceptorRegistry.class);
		UserRequiredActionInterceptor interceptor = new UserRequiredActionInterceptor(userService, userLoginPath);
		actionInterceptorRegistry.registerInterceptor(UserRequired.class, interceptor);

		configureObjectify(injectionContext);
	}

	public void start(UpdatableInjectionContext injectionContext) {
		Routes routes = injectionContext.get(Routes.class);
		routes.addRoute(RouteType.POST, "/_user/login", null, new MethodAction(UserController.class, UserController.Methods.Login));
		routes.addRoute(RouteType.POST, "/_user/logout", null, new MethodAction(UserController.class, UserController.Methods.Logout));
	}

	private void configureObjectify(UpdatableInjectionContext injectionContext) {
		ObjectifyFactory objectifyFactory = ObjectifyService.factory();
		UserServiceImpl.registerObjectifyClasses(objectifyFactory);
	}

}
