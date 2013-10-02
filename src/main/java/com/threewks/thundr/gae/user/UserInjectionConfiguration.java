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

import static com.atomicleopard.expressive.Expressive.list;

import com.threewks.thundr.action.method.ActionInterceptorRegistry;
import com.threewks.thundr.injection.InjectionConfiguration;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.route.Route;
import com.threewks.thundr.route.RouteType;
import com.threewks.thundr.route.Routes;
import com.threewks.thundr.search.google.GoogleSearchService;
import com.threewks.thundr.search.google.SearchService;

public class UserInjectionConfiguration implements InjectionConfiguration {

	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
		ensureSearchService(injectionContext);

		// TODO - NAO - after thundr is released next, then this will be a good option
		// ActionMethodBinderRegistry registry = null;
		ActionInterceptorRegistry actionInterceptorRegistry = injectionContext.get(ActionInterceptorRegistry.class);
		Routes routes = injectionContext.get(Routes.class);

		injectionContext.inject(UserServiceImpl.class).as(UserService.class);
		UserServiceImpl.registerObjectifyClasses();

		UserService userService = injectionContext.get(UserService.class);
		String userLoginPath = injectionContext.get(String.class, "userLoginPath");

		actionInterceptorRegistry.registerInterceptor(UserRequired.class, new UserRequiredActionInterceptor(userService, userLoginPath));
		actionInterceptorRegistry.registerInterceptor(BindUser.class, new BindUserActionInterceptor(userService));

		Route login = new Route("/_user/login", String.format("%s.%s", UserController.class.getName(), UserController.Methods.Login), RouteType.POST);
		Route logout = new Route("/_user/logout", String.format("%s.%s", UserController.class.getName(), UserController.Methods.Logout), RouteType.POST);
		routes.addRoutes(list(login, logout));
	}

	private void ensureSearchService(UpdatableInjectionContext injectionContext) {
		SearchService searchService = injectionContext.get(SearchService.class);
		if (searchService == null) {
			injectionContext.inject(GoogleSearchService.class).as(SearchService.class);
		}
	}

}
