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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.threewks.thundr.action.method.ActionInterceptor;
import com.threewks.thundr.http.URLEncoder;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.view.redirect.RedirectView;

public class UserRequiredActionInterceptor implements ActionInterceptor<UserRequired> {
	private UserService userService;
	private String loginUrl;

	public UserRequiredActionInterceptor(UserService userService, String userLoginPath) {
		this.userService = userService;
		this.loginUrl = userLoginPath;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RedirectView before(UserRequired annotation, HttpServletRequest req, HttpServletResponse resp) {
		User user = userService.getUserFromRequest(req);
		if (user == null) {
			Logger.info("No user authorised - redirecting to login");
			String requestUri = req.getRequestURI();
			String url = String.format("%s?r=%s", loginUrl, URLEncoder.encodeQueryComponent(requestUri));
			return new RedirectView(url);
		}
		Logger.info("User %s authorised", user.getUsername());
		req.setAttribute(annotation.value(), user);
		return null;
	}

	@Override
	public <T> T after(UserRequired annotation, Object result, HttpServletRequest req, HttpServletResponse resp) {
		return null;
	}

	@Override
	public <T> T exception(UserRequired annotation, Exception e, HttpServletRequest req, HttpServletResponse resp) {
		return null;
	}

}